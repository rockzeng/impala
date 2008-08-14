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

import org.springframework.util.Assert;

public class BasePropertyValue {
	
	private String name;
	
	private PropertySource propertiesSource;

	protected final String getRawValue() {
		Assert.notNull(propertiesSource, "propertiesSource must be specified");
		Assert.notNull(name, "name must be specified");
		String value = propertiesSource.getValue(name);
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPropertiesSource(PropertySource propertiesSource) {
		this.propertiesSource = propertiesSource;
	}

}
