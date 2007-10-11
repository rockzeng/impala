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

package net.java.impala.file;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Phil Zoio
 */
public class DefaultClassFilter implements FileFilter {

	public boolean accept(File file) {
		if (file.getName().startsWith("."))
			return false;
		if (file.getName().contains("$"))
			return false; // ignore inner classes
		if (file.isHidden())
			return false;
		if (file.isDirectory())
			return true;
		if (file.getName().endsWith(".class"))
			return true;
		return false;
	}

}
