package org.impalaframework.web.loader;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.impalaframework.exception.ConfigurationException;
import org.impalaframework.web.WebConstants;

public class ConfigurableWebXmlBasedContextLoaderTest extends TestCase {

	private ConfigurableWebXmlBasedContextLoader contextLoader;

	private ServletContext servletContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contextLoader = new ConfigurableWebXmlBasedContextLoader();
		servletContext = createMock(ServletContext.class);
	}

	public final void testModuleSetGetProperties() {
		System.setProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM,
				"org/impalaframework/web/module/locations.properties");
		try {
			replay(servletContext);
			String moduleDefinition = contextLoader.getModuleDefinitionString(servletContext);
			assertEquals("plugin1,plugin2", moduleDefinition);
			verify(servletContext);

		}
		finally {
			System.clearProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM);
		}
	}

	public final void testModulesSetGetPropertiesNotFound() {
		System.setProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM, "a location which does not exist");
		try {
			expect(servletContext.getInitParameter("moduleNames")).andReturn("a value");
			replay(servletContext);
			String definition = contextLoader.getModuleDefinitionString(servletContext);

			assertEquals("a value", definition);
			verify(servletContext);

		}
		finally {
			System.clearProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM);
		}
	}

	public final void testModulesWithPropertyNotFound() {
		System.setProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM,
				"org/impalaframework/web/module/unspecified_locations.properties");
		try {
			replay(servletContext);

			try {
				contextLoader.getModuleDefinitionString(servletContext);
				fail();
			}
			catch (ConfigurationException e) {
				assertEquals("Bootstrap location resource 'class path resource [org/impalaframework/web/module/unspecified_locations.properties]' does not contain property 'moduleNames'" +
						"", e.getMessage());
			}
			verify(servletContext);
		}
		finally {
			System.clearProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM);
		}
	}
	
	public final void testParentLocationsSetGetProperties() {
		System.setProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM,
				"org/impalaframework/web/module/locations.properties");
		try {
			replay(servletContext);
			String[] parentLocations = contextLoader.getParentLocations(servletContext);
			System.out.println(Arrays.toString(parentLocations));
			assertTrue(Arrays.equals(new String[]{"parent1", "parent2"}, parentLocations));
			verify(servletContext);

		}
		finally {
			System.clearProperty(WebConstants.BOOTSTRAP_LOCATIONS_RESOURCE_PARAM);
		}
	}

	public final void testParentLocationsGetPropertiesNotFound() {
		System.setProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM, "a location which does not exist");
		try {
			expect(servletContext.getInitParameter("contextConfigLocation")).andReturn("location1 location2");
			replay(servletContext);
			String[] parentLocations = contextLoader.getParentLocations(servletContext);

			System.out.println(Arrays.toString(parentLocations));
			assertTrue(Arrays.equals(new String[]{"location1", "location2"}, parentLocations));
			verify(servletContext);

		}
		finally {
			System.clearProperty(WebConstants.BOOTSTRAP_LOCATIONS_RESOURCE_PARAM);
		}
	}

	public final void testParentLocationsPropertyNotFound() {
		System.setProperty(WebConstants.BOOTSTRAP_MODULES_RESOURCE_PARAM,
				"org/impalaframework/web/module/unspecified_locations.properties");
		try {
			replay(servletContext);

			try {
				contextLoader.getParentLocations(servletContext);
				fail();
			}
			catch (ConfigurationException e) {
				assertEquals("Bootstrap location resource 'class path resource [org/impalaframework/web/module/unspecified_locations.properties]' does not contain property 'parentLocations'" +
						"", e.getMessage());
			}
			verify(servletContext);
		}
		finally {
			System.clearProperty(WebConstants.BOOTSTRAP_LOCATIONS_RESOURCE_PARAM);
		}
	}	
	
	
}
