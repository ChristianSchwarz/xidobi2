/*
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xidobi.internal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests the class {@link Preconditions}
 * 
 * @author Christian Schwarz
 */
public class TestPreconditions {

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Verifies that the argument is returned if it is not <code>null</code>
	 */
	@Test
	public void checkArgumentNotNull_nonNullValue() {
		String result = Preconditions.checkArgumentNotNull("i'am not null", "argName");
		assertThat(result, is("i'am not null"));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, if the argument is
	 * <code>null</code>.
	 */
	@Test
	public void checkArgumentNotNull_nullValue() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >argName< must not be null!");

		Preconditions.checkArgumentNotNull(null, "argName");
	}

	/**
	 * Verifies that the argument is returned if it is not <code>null</code>
	 */
	@Test
	public void checkArgumentNotNull_nonNullValue_noArgName() {
		String result = Preconditions.checkArgumentNotNull("i'am not null", null);
		assertThat(result, is("i'am not null"));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, if the argument is
	 * <code>null</code>.
	 */
	@Test
	public void checkArgumentNotNull_nullValue_noArgName() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument must not be null!");

		Preconditions.checkArgumentNotNull(null, null);
	}

	/**
	 * Verifies that no exception is thrown when the condition is <code>true</code>.
	 */
	@Test
	public void checkArgument_conditionMatch() {
		Preconditions.checkArgument(true, null, null);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the condition does'n match.
	 */
	@Test
	public void checkArgument_conditionMismatch_nullArg_nullExpectation() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(nullValue(String.class));

		Preconditions.checkArgument(false, null, null);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the condition does'n match.
	 * Containing the argument name as message.
	 */
	@Test
	public void checkArgument_conditionMismatch_withArg_nullExpectation() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >argName< is invalid");

		Preconditions.checkArgument(false, "argName", null);
	}
	
	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the condition does'n match.
	 * Containing the argument name as message.
	 */
	@Test
	public void checkArgument_conditionMismatch_nullArg_withExpectation() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The expected exception message.");
		
		Preconditions.checkArgument(false, null, "The expected exception message.");
	}
	
	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the condition does'n match.
	 * Containing the argument name as message.
	 */
	@Test
	public void checkArgument_conditionMismatch_withArg_withExpectation() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >argName< is invalid! The expected exception message.");
		
		Preconditions.checkArgument(false, "argName", "The expected exception message.");
	}

}
