package org.impalaframework.spring.resource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import junit.framework.TestCase;

public class ClassPathResourceLoaderTest extends TestCase {

	private ClassPathResourceLoader resourceLoader;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.resourceLoader = new ClassPathResourceLoader();
	}
	
	public final void testGetResource() {
		Resource resource = resourceLoader.getResource("beanset.properties", ClassUtils.getDefaultClassLoader());
		assertTrue(resource instanceof ClassPathResource);
	}
	
	public final void testGetResourceWithPrefix() {
		resourceLoader.setPrefix("beanset/");
		Resource resource = resourceLoader.getResource("imported-context.xml", ClassUtils.getDefaultClassLoader());
		assertTrue(resource instanceof ClassPathResource);
	}

}
