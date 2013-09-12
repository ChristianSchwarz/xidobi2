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
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests the class {@link AbstractControlCmd}.
 * 
 * @author Peter-René Jeschke
 */
public class TestAbstractControlCommand {

	@Rule
	public ExpectedException exception = none();
	private AbstractControlCmd<String, Integer> cmd;

	@Before
	public void setUp() {
		cmd = new AbstractControlCmdImpl(10);
	}

	/**
	 * When the commandCode is smaller than 0, an {@link IllegalArgumentException} is expected.
	 */
	@Test
	public void new_negativeCommandCode() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The command code must be in the range [0..12] or [100..112]! Got: -1");
		new AbstractControlCmdImpl(-1);
	}

	/**
	 * When the commandCode is between [0..12], no exception should be thrown.
	 */
	@Test
	public void new_commandCodeBetween0and12() {
		for (int i = 0; i <= 12; i++) {
			new AbstractControlCmdImpl(i);
		}
	}

	/**
	 * When the commandCode is between 12 and 100, an {@link IllegalArgumentException} should be
	 * thrown.
	 */
	@Test
	public void new_invalidCommandCode() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The command code must be in the range [0..12] or [100..112]! Got: 90");
		new AbstractControlCmdImpl(90);
	}

	/**
	 * When the commandCode is between 100 and 112, no exception should be thrown.
	 */
	@Test
	public void new_commandCodeBetween100And112() {
		for (int i = 100; i <= 112; i++)
			new AbstractControlCmdImpl(i);

	}

	/**
	 * When the commandCode is too high, an {@link IllegalArgumentException} should be thrown.
	 */
	@Test
	public void new_commandCodeTooHigh() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The command code must be in the range [0..12] or [100..112]! Got: 150");
		new AbstractControlCmdImpl(150);
	}

	/**
	 * Verifies that {@link AbstractControlCmd#getCommandCode()} returns the supplied commandCode.
	 */
	@Test
	public void getCommandCode() {
		assertThat(cmd.getCommandCode(), is((byte) 10));
	}

	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to RFC2217
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getRfc2217Equivalent_notMapped()  {

		assertThat(cmd.getRfc2217Equivalent("this a not mapped value"),is(nullValue()));
	}
	
	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to RFC2217
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getRfc2217Equivalent_null()  {
		cmd.addEquivalents(null, -1);
		
		assertThat(cmd.getRfc2217Equivalent(null),is(-1));
	}
	
	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to RFC2217
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getXidobiEquivalent_null()  {
		cmd.addEquivalents("-", null);
		
		assertThat(cmd.getXidobiEquivalent(null),is("-"));
	}
	
	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to a xidobi
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getXidobiEquivalent_notMapped()  {
		assertThat(cmd.getXidobiEquivalent(null),is(nullValue()));
	}

	// ///////////////////////////////////////////////////////////////////////////////
	private class AbstractControlCmdImpl extends AbstractControlCmd<String, Integer> {

		AbstractControlCmdImpl(int commandCode) {
			super(commandCode);
		}

		@Override
		public void write(DataOutput output) throws IOException {}

	}
}
