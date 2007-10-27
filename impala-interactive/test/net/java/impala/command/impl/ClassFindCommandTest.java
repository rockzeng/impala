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
package net.java.impala.command.impl;

import java.util.List;

import net.java.impala.command.CommandInfo;
import net.java.impala.command.CommandLineInputCapturer;
import net.java.impala.command.CommandSpec;
import net.java.impala.command.CommandState;

public class ClassFindCommandTest extends ManualClassFindCommandTest {

	public void testCommandSpec() {
		ClassFindCommand command = new ClassFindCommand();
		CommandSpec commandSpec = command.getCommandSpec();
		assertEquals(1, commandSpec.getCommandInfos().size());
		CommandInfo ci = commandSpec.getCommandInfos().get(0);

		String nullOrEmptyText = "Please enter type (class or interface) to find";
		try {
			assertEquals(nullOrEmptyText, ci.validate(null));
			fail();
		}
		catch (IllegalArgumentException e) {
		}
		assertEquals("Search text should be at least 3 characters long", ci.validate("a"));

	}

	public void testFindClass() throws Exception {

		ClassFindCommand command = getCommand();

		doTest(command, "ClassFindFileRecurseHandler", 1);

		// show that it can handle packages correctly (if last part is correctly
		// specified)
		doTest(command, "ClassFindCommand", 4);
		doTest(command, "impl.ClassFindCommand", 4);

		// will not find inner class
		doTest(command, "PrintDetails", 1);
	}

	private void doTest(ClassFindCommand command, final String classNameToSearch, int expected)
			throws ClassNotFoundException {

		System.out.println("----");
		// now need to capture
		CommandState commandState = new CommandState();

		CommandLineInputCapturer inputCapturer = getInputCapturer(classNameToSearch);
		commandState.setInputCapturer(inputCapturer);

		commandState.capture(command);
		command.execute(commandState);

		List<String> foundClasses = command.getFoundClasses();

		for (String className : foundClasses) {
			// check that we can instantiate classes
			System.out.println(className);
			Class.forName(className);
		}
		assertEquals(expected, foundClasses.size());
	}

	@Override
	protected CommandLineInputCapturer getInputCapturer(final String classNameToSearch) {
		CommandLineInputCapturer inputCapturer = new CommandLineInputCapturer() {

			public String capture(CommandInfo info) {
				if (info.getPropertyName().equals("class")) {
					return classNameToSearch;
				}
				return null;
			}
		};
		return inputCapturer;
	}
}
