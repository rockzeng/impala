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

package org.impalaframework.module.lock;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.impalaframework.module.spi.FrameworkLockHolder;

/**
 * Implements {@link FrameworkLockHolder} using the {@link ReentrantLock} class from the Java Concurrency API.
 * @author Phil Zoio
 */
public class DefaultFrameworkLockHolder implements FrameworkLockHolder {

	private static Log logger = LogFactory.getLog(DefaultFrameworkLockHolder.class);

	private ReentrantLock lock = new ReentrantLock();

	public DefaultFrameworkLockHolder() {
		super();
	}
	
	public void lock() {
		this.lock.lock();
	}
	
	public void unlock() {
		this.lock.unlock();
	}
	
	public boolean isAvailable() {
		
		//FIXME check the semantics of this - want to robustify operations on service registry and
		//also on proxies
		if (this.lock.isLocked()) {
			if (!this.lock.isHeldByCurrentThread()) {
			
				if (logger.isDebugEnabled()) {
					logger.debug("Module is unavailable with hold count of " + lock.getHoldCount() + " but not held by current thread");
				}
				return false;
			}
			return true;
		}
		return true;
	}

	public boolean hasLock() {
		return this.lock.isHeldByCurrentThread();
	}

}
