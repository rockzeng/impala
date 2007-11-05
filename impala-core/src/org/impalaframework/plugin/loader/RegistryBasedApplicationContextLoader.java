/*
 * Copyright 2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.impalaframework.plugin.loader;

import java.util.Collection;


import org.impalaframework.plugin.monitor.PluginMonitor;
import org.impalaframework.plugin.spec.ApplicationContextSet;
import org.impalaframework.plugin.spec.PluginSpec;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author Phil Zoio
 */
public class RegistryBasedApplicationContextLoader implements ApplicationContextLoader {

	private PluginLoaderRegistry registry;

	private PluginMonitor pluginMonitor;

	public RegistryBasedApplicationContextLoader(PluginLoaderRegistry registry) {
		Assert.notNull(registry, PluginLoaderRegistry.class.getName() + " cannot be null");
		this.registry = registry;
	}

	public void loadParentContext(ApplicationContextSet appSet, PluginSpec parentSpec) {
		addApplicationPlugin(appSet, parentSpec, null);
	}

	public void addApplicationPlugin(ApplicationContextSet appSet, PluginSpec plugin, ApplicationContext parent) {

		//FIXME add capability for detatching and reattaching plugins to root
		
		ClassLoader existing = ClassUtils.getDefaultClassLoader();

		try {

			final PluginLoader pluginLoader = registry.getPluginLoader(plugin.getType());
			
			//FIXME add test for this
			if (pluginLoader == null) {
				throw new IllegalStateException("No " + PluginLoader.class.getSimpleName()
						+ " instance registered for plugin type " + plugin.getType());
			}
			
			ClassLoader classLoader = pluginLoader.newClassLoader(appSet, plugin, parent);

			Thread.currentThread().setContextClassLoader(classLoader);

			//FIXME create interface which will delegate entire Spring application context
			//loading process via separate interface. If none present, then use the
			//built in one
			
			final Resource[] resources = pluginLoader.getSpringConfigResources(appSet, plugin, classLoader);

			ConfigurableApplicationContext context = pluginLoader.newApplicationContext(parent, classLoader);
			
			BeanDefinitionReader reader = pluginLoader.newBeanDefinitionReader(context, plugin);

			if (reader instanceof AbstractBeanDefinitionReader) {
				((AbstractBeanDefinitionReader) reader).setBeanClassLoader(classLoader);
			}
			reader.loadBeanDefinitions(resources);
			
			// refresh the application context - now we're ready to go
			context.refresh();
			
			pluginLoader.afterRefresh(context, reader, plugin);

			Resource[] toMonitor = pluginLoader.getClassLocations(appSet, plugin);
			if (pluginMonitor != null) {
				pluginMonitor.setResourcesToMonitor(plugin.getName(), toMonitor);
			}

			appSet.getPluginContext().put(plugin.getName(), context);

			// now recursively add context
			final Collection<PluginSpec> plugins = plugin.getPlugins();
			for (PluginSpec childPlugin : plugins) {
				addApplicationPlugin(appSet, childPlugin, context);
			}

		}
		finally {
			Thread.currentThread().setContextClassLoader(existing);
		}

	}

	public void setPluginMonitor(PluginMonitor pluginMonitor) {
		PluginMonitor existing = this.pluginMonitor;

		if (existing != pluginMonitor) {
			if (existing != null) {
				existing.stop();
			}
			this.pluginMonitor = pluginMonitor;
			if (pluginMonitor != null) {
				this.pluginMonitor.start();
			}
		}
	}

	public PluginMonitor getPluginMonitor() {
		return this.pluginMonitor;
	}

	public PluginLoaderRegistry getRegistry() {
		return registry;
	}

}
