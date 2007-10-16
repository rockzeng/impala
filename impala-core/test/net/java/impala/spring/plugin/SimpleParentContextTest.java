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

package net.java.impala.spring.plugin;

import net.java.impala.spring.plugin.impl.Child;
import net.java.impala.spring.plugin.impl.Parent;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * @author Phil Zoio
 */
public class SimpleParentContextTest extends TestCase {

	public void testContexts() {
		
		ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext("childcontainer/parent-context.xml");
		Parent parentBean = (Parent) parent.getBean("parent");

		// this fails because there is no child context
		try {
			parentBean.tryGetChild();
			fail();
		}
		catch (NoSuchBeanDefinitionException e) {
		}

		ClassPathXmlApplicationContext child = new ClassPathXmlApplicationContext(
				new String[] { "childcontainer/child-context.xml" }, parent);
		Child childBean = (Child) child.getBean("child");

		// show that we've overridden the child
		Parent gotParent = childBean.tryGetParent();
		assertSame(parentBean, gotParent);
	}

}
