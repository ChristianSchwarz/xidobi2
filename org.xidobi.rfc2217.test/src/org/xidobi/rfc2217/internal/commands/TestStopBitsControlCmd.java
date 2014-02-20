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
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.StopBits.STOPBITS_1;
import static org.xidobi.StopBits.STOPBITS_1_5;
import static org.xidobi.StopBits.STOPBITS_2;
import static testtools.MessageBuilder.buffer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.StopBits;

/**
 * Tests the class {@link StopBitsControlCmd}.
 * 
 * @author Christin Nitsche
 * @author Konrad Schulz
 * @author Peter-René Jeschke
 */
public class TestStopBitsControlCmd {

	@Rule
	public ExpectedException exception = none();

	private StopBitsControlCmd cmd;

	@Mock
	private DataOutput output;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * When a <code>null</code> stopBit is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@Test
	public void new_withNullStopBits() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >stopBits< must not be null");
		new StopBitsControlCmd((StopBits) null);
	}

	/**
	 * When the stopBit value from the input is negative or greater than 127, an {@link IOException}
	 * must be thrown.
	 * 
	 * @throws IOException
	 */
	@Test
	public void new_withIllegalStopBits() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected stopBits value: -1");
		new StopBitsControlCmd(buffer(-1).toDataInput());
	}

	/**
	 * Checks whether the stopBit is read correctly, {@link StopBits#STOPBITS_1}.
	 */
	@Test
	public void read_stopBits_1() throws IOException {
		cmd = new StopBitsControlCmd(buffer(1).toDataInput());

		assertThat(cmd.getStopBits(), is(STOPBITS_1));
	}

	/**
	 * Checks whether the stopBit is read correctly, {@link StopBits#STOPBITS_1_5}.
	 */
	@Test
	public void read_stopBits_1_5() throws IOException {
		cmd = new StopBitsControlCmd(buffer(3).toDataInput());

		assertThat(cmd.getStopBits(), is(STOPBITS_1_5));
	}

	/**
	 * Checks whether the stopBit is read correctly, {@link StopBits#STOPBITS_2}.
	 */
	@Test
	public void read_stopBits_2() throws IOException {
		cmd = new StopBitsControlCmd(buffer(2).toDataInput());

		assertThat(cmd.getStopBits(), is(STOPBITS_2));
	}

	/**
	 * Checks whether the encoded message is correct, {@link StopBits#STOPBITS_1}.
	 */
	@Test
	public void write_stopBits_1() throws IOException {
		cmd = new StopBitsControlCmd(STOPBITS_1);
		cmd.write(output);

		verify(output).writeByte(1);
	}

	/**
	 * Checks whether the encoded message is correct, {@link StopBits#STOPBITS_1_5}.
	 */
	@Test
	public void write_stopBits_1_5() throws IOException {
		cmd = new StopBitsControlCmd(STOPBITS_1_5);
		cmd.write(output);

		verify(output).writeByte(3);
	}

	/**
	 * Checks whether the encoded message is correct, {@link StopBits#STOPBITS_2}.
	 */
	@Test
	public void write_stopBits_2() throws IOException {
		cmd = new StopBitsControlCmd(STOPBITS_2);
		cmd.write(output);

		verify(output).writeByte(2);
	}

	/**
	 * 
	 */
	@Test
	public void write_invalidStopBits() throws IOException {
		cmd = new StopBitsControlCmd(buffer(10).toDataInput());
		cmd.write(output);
		verify(output).writeByte(10);
	}

	/**
	 * When the stopbits {@link #read(DataInput)} decoded value, has no corresponding
	 * {@link StopBits} value, should be return a <code>null</code> value.
	 * 
	 * @throws Exception
	 */
	@Test
	public void stopBits_null() throws Exception {
		cmd = new StopBitsControlCmd(buffer(10).toDataInput());
		cmd.write(output);
		assertThat(cmd.getStopBits(), is(nullValue()));
	}

	/**
	 * Checks whether the commands equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		StopBitsControlCmd cmd = new StopBitsControlCmd(STOPBITS_1_5);
		StopBitsControlCmd cmd2 = new StopBitsControlCmd(STOPBITS_1_5);
		assertThat(cmd.equals(cmd2), is(true));
	}

	/**
	 * Checks whether the commands not equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		StopBitsControlCmd cmd = new StopBitsControlCmd(STOPBITS_1_5);
		StopBitsControlCmd cmd2 = new StopBitsControlCmd(STOPBITS_1);
		assertThat(cmd.equals(cmd2), is(false));
	}

	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		StopBitsControlCmd cmd = new StopBitsControlCmd(STOPBITS_1_5);
		assertThat(cmd.toString(), is("StopBitsControlCmd [stopBits=1.5]"));
	}

	/**
	 * When an invalid value for the stop-bits is in the DataInput, an {@link IOException} is
	 * expected.
	 */
	@Test
	public void invalidStopBits() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected stopBits value: -1");
		new StopBitsControlCmd(buffer(-1).toDataInput());
	}
}
