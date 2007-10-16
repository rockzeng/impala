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

package net.java.impala.spring.util;

import java.util.Arrays;
import java.util.Collection;

import net.java.impala.classloader.ContextResourceHelper;
import net.java.impala.spring.monitor.PluginMonitor;
import net.java.impala.spring.plugin.ApplicationContextSet;
import net.java.impala.spring.plugin.PluginSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@Deprecated
public class DefaultApplicationContextLoader implements ApplicationContextLoader {

	private static final Log log = LogFactory.getLog(DefaultApplicationContextLoader.class);

	private ContextResourceHelper contextResourceHelper;

	public DefaultApplicationContextLoader(ContextResourceHelper resourceHelper) {
		Assert.notNull(resourceHelper, ContextResourceHelper.class.getName() + " cannot be null");
		this.contextResourceHelper = resourceHelper;
	}

	public void loadParentContext(ApplicationContextSet appSet, PluginSpec parentSpec) {

		ConfigurableApplicationContext context = null;
		ClassLoader existingClassLoader = ClassUtils.getDefaultClassLoader();

		try {

			ClassLoader classLoader = this.contextResourceHelper.getParentClassLoader(existingClassLoader, parentSpec.getName());
			
			Thread.currentThread().setContextClassLoader(classLoader);

			String[] locations = parentSpec.getContextLocations();
			Assert.notNull(locations);
			Assert.notEmpty(locations);

			log.info("Loading application context from locations " + Arrays.toString(locations));

			Resource[] resource = new Resource[locations.length];

			for (int i = 0; i < locations.length; i++) {
				resource[i] = new ClassPathResource(locations[i], classLoader);
			}

			context = this.loadContextFromResources(null, resource, classLoader);

			appSet.setContext(context);

			Collection<PluginSpec> plugins = parentSpec.getPlugins();
			for (PluginSpec plugin : plugins) {
				addApplicationPlugin(appSet, plugin, context);
			}
		}
		finally {
			Thread.currentThread().setContextClassLoader(existingClassLoader);
		}
	}

	public void addApplicationPlugin(ApplicationContextSet appSet, PluginSpec plugin, ApplicationContext parent) {

		if (this.contextResourceHelper == null) {
			throw new IllegalStateException(ContextResourceHelper.class.getName() + " not set");
		}

		Resource springLocation = this.contextResourceHelper.getApplicationPluginSpringLocation(plugin.getName());

		// create the class loader
		ClassLoader classLoader = this.contextResourceHelper.getApplicationPluginClassLoader(parent.getClassLoader(),
				plugin.getName());

		ClassLoader existing = ClassUtils.getDefaultClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(classLoader);

			log.info("Loading application context from resource " + springLocation.getDescription());

			ConfigurableApplicationContext context = this.loadContextFromResources(parent,
					new Resource[] { springLocation }, classLoader);

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

	public ConfigurableApplicationContext loadContextFromResources(ApplicationContext parent, Resource[] resource,
			ClassLoader classLoader) {

		DefaultListableBeanFactory beanFactory = newBeanFactory();
		beanFactory.setBeanClassLoader(classLoader);

		// create the application context, and set the class loader
		GenericApplicationContext context = newApplicationContext(parent, beanFactory);
		context.setClassLoader(classLoader);

		BeanDefinitionReader xmlReader = newBeanDefinitionReader(context);
		if (xmlReader instanceof AbstractBeanDefinitionReader)
		{
			((AbstractBeanDefinitionReader)xmlReader).setBeanClassLoader(classLoader);
		}
		xmlReader.loadBeanDefinitions(resource);

		// refresh the application context - now we're ready to go
		refresh(context);
		return context;
	}

	protected GenericApplicationContext newApplicationContext(ApplicationContext parent,
			DefaultListableBeanFactory beanFactory) {
		GenericApplicationContext context = parent != null ? new GenericApplicationContext(beanFactory, parent)
				: new GenericApplicationContext(beanFactory);
		return context;
	}

	protected void beforeRefresh() {
	}

	protected void afterRefresh() {
	}

	protected XmlBeanDefinitionReader newBeanDefinitionReader(GenericApplicationContext context) {
		return new XmlBeanDefinitionReader(context);
	}

	private DefaultListableBeanFactory newBeanFactory() {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		return beanFactory;
	}

	protected ContextResourceHelper getContextResourceHelper() {
		return contextResourceHelper;
	}

	private void refresh(GenericApplicationContext context) {
		beforeRefresh();
		context.refresh();
		afterRefresh();
	}

	public void setPluginMonitor(PluginMonitor pluginMonitor) {
		
	}
}
