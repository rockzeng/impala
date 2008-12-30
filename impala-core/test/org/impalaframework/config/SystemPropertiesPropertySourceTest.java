/*
 * Copyright 2007-2008 the original author or authors.
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

package org.impalaframework.config;

import junit.framework.TestCase;

public class SystemPropertiesPropertySourceTest extends TestCase {

	private SystemPropertiesPropertySource source;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		source = new SystemPropertiesPropertySource();
		System.clearProperty(SystemPropertiesPropertySourceTest.class.getSimpleName());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.clearProperty(SystemPropertiesPropertySourceTest.class.getSimpleName());
	}
	
	public void testGetValueNull() {
		assertNull(source.getValue(SystemPropertiesPropertySourceTest.class.getSimpleName()));
	}
	
	public void testGetValue() {
		System.setProperty(SystemPropertiesPropertySourceTest.class.getSimpleName(), "testvalue");
		assertEquals("testvalue", source.getValue(SystemPropertiesPropertySourceTest.class.getSimpleName()));
	}
	
}
