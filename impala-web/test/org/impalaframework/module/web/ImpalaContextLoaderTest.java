package org.impalaframework.module.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.impalaframework.module.spec.RootModuleDefinition;
import org.impalaframework.module.spec.ModuleDefinitionSource;
import org.impalaframework.module.web.WebConstants;
import org.impalaframework.module.web.WebXmlBasedContextLoader;

public class ImpalaContextLoaderTest extends TestCase {

	private WebXmlBasedContextLoader contextLoader;
	private ServletContext servletContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contextLoader = new WebXmlBasedContextLoader();
		servletContext = createMock(ServletContext.class);
	}

	public void testBootstrapLocations() throws Exception {
		String[] locations = contextLoader.getBootstrapContextLocations(EasyMock.createMock(ServletContext.class));
		assertTrue(locations.length > 0);
	}

	public void testGetPluginSpec() {
		expect(servletContext.getInitParameter(WebXmlBasedContextLoader.CONFIG_LOCATION_PARAM)).andReturn(
				"context1.xml, context2.xml");
		expect(servletContext.getInitParameter(WebConstants.PLUGIN_NAMES_PARAM)).andReturn("p1, p2, p3");

		WebXmlBasedContextLoader contextLoader = new WebXmlBasedContextLoader();

		replay(servletContext);

		ModuleDefinitionSource builder = contextLoader.getPluginSpecBuilder(servletContext);
		RootModuleDefinition rootModuleDefinition = builder.getModuleDefintion();

		List<String> list = new ArrayList<String>();
		list.add("context1.xml");
		list.add("context2.xml");

		assertEquals(list, rootModuleDefinition.getContextLocations());

		assertTrue(Arrays.equals(new String[] { "p1", "p2", "p3" }, rootModuleDefinition.getPluginNames().toArray(new String[3])));

		verify(servletContext);
	}

	public void testGetChildPluginSpecString() {

		expect(servletContext.getInitParameter(WebConstants.PLUGIN_NAMES_PARAM)).andReturn("plugin1, plugin2");

		replay(servletContext);
		assertEquals("plugin1, plugin2", contextLoader.getPluginDefinitionString(servletContext));
		verify(servletContext);
	}

}
