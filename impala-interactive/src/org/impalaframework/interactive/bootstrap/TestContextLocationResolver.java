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

package org.impalaframework.interactive.bootstrap;

import java.util.List;

import org.impalaframework.bootstrap.SimpleContextLocationResolver;
import org.impalaframework.config.PropertySource;

public class TestContextLocationResolver extends SimpleContextLocationResolver {

	@Override
	public void addContextLocations(List<String> contextLocations,
			PropertySource propertySource) {
		super.addContextLocations(contextLocations, propertySource);
		contextLocations.add("META-INF/impala-test-bootstrap.xml");
	}

}
