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

package org.impalaframework.module.holder;

import org.impalaframework.module.modification.ModuleStateChange;

/**
 * Defines a contract for {@link ModuleStateChangeListener}s to subscribe to
 * changes in a module's state (for example, a module becoming stale and
 * requiring a reload). Implements the Observer Pattern.
 * 
 * @see ModuleStateChange
 * @see ModuleStateChangeListener
 * @author Phil Zoio
 */
public interface ModuleStateChangeNotifier {

	void notify(ModuleStateHolder moduleStateHolder, ModuleStateChange change);
	public void addListener(ModuleStateChangeListener listener);
	public boolean removeListener(ModuleStateChangeListener listener);
	
}