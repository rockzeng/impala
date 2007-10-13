package net.java.impala.spring.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import javax.servlet.ServletContext;

import net.java.impala.spring.plugin.ParentSpec;
import junit.framework.TestCase;

public class MultiServletImpalaContextLoaderTest extends TestCase {

	public void testGetWebApplicationSpec() {
		ServletContext servletContext = createMock(ServletContext.class);
		expect(servletContext.getInitParameter(ImpalaContextLoader.WEBAPP_LOCATION_PARAM)).andReturn(
				"servlet-context1.xml, servlet-context2.xml");

		MultiServletImpalaContextLoader contextLoader = new MultiServletImpalaContextLoader();

		replay(servletContext);

		final ParentSpec webApplicationSpec = contextLoader.getWebApplicationSpec(servletContext);
		assertTrue(Arrays.equals(new String[] { "servlet-context1.xml", "servlet-context2.xml" }, webApplicationSpec
				.getContextLocations()));

		verify(servletContext);
	}

}
