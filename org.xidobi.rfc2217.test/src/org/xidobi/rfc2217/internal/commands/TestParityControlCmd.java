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
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.Parity.PARITY_EVEN;
import static org.xidobi.Parity.PARITY_MARK;
import static org.xidobi.Parity.PARITY_NONE;
import static org.xidobi.Parity.PARITY_ODD;
import static org.xidobi.Parity.PARITY_SPACE;
import static testtools.MessageBuilder.buffer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.Parity;

/**
 * Tests the class {@link ParityControlCmd}.
 * 
 * @author Peter-René Jeschke
 * @author Konrad Schulz
 */
@SuppressWarnings("javadoc")
public class TestParityControlCmd {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ParityControlCmd cmd;

	@Mock
	private DataOutput output;

	@Before
	public void setup() {
		initMocks(this);
	}

	/**
	 * When a <code>null</code> parity is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@Test
	public void new_withNull() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >parity< must not be null");
		new ParityControlCmd((Parity) null);
	}

	/**
	 * When the parity value from the input is negative or greater than 127, an {@link IOException}
	 * must be thrown.
	 * 
	 * @throws IOException
	 */
	@Test
	public void new_withIllegalParity() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected parity value: -1");
		new ParityControlCmd(buffer(-1).toDataInput());
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_NONE}.
	 */
	@Test
	public void read_parityNone() throws IOException {
		cmd = new ParityControlCmd(buffer(1).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_NONE));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_ODD}.
	 */
	@Test
	public void read_parityOdd() throws IOException {
		cmd = new ParityControlCmd(buffer(2).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_ODD));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_EVEN}.
	 */
	@Test
	public void read_parityEven() throws IOException {
		cmd = new ParityControlCmd(buffer(3).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_EVEN));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_MARK}.
	 */
	@Test
	public void read_parityMark() throws IOException {
		cmd = new ParityControlCmd(buffer(4).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_MARK));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_SPACE}.
	 */
	@Test
	public void read_paritySpace() throws IOException {
		cmd = new ParityControlCmd(buffer(5).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_SPACE));
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_NONE}.
	 */
	@Test
	public void write_parityNone() throws IOException {
		cmd = new ParityControlCmd(PARITY_NONE);
		cmd.write(output);

		verify(output).writeByte(1);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_ODD}.
	 */
	@Test
	public void write_parityOdd() throws IOException {
		cmd = new ParityControlCmd(PARITY_ODD);
		cmd.write(output);

		verify(output).writeByte(2);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_EVEN}.
	 */
	@Test
	public void write_parityEven() throws IOException {
		cmd = new ParityControlCmd(PARITY_EVEN);
		cmd.write(output);

		verify(output).writeByte(3);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_MARK}.
	 */
	@Test
	public void write_parityMark() throws IOException {
		cmd = new ParityControlCmd(PARITY_MARK);
		cmd.write(output);

		verify(output).writeByte(4);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_SPACE}.
	 */
	@Test
	public void write_paritySpace() throws IOException {
		cmd = new ParityControlCmd(PARITY_SPACE);
		cmd.write(output);

		verify(output).writeByte(5);
	}

	/**
	 *
	 */
	@Test
	public void read_invalidParity() throws IOException {
		cmd = new ParityControlCmd(buffer(6).toDataInput());
		cmd.write(output);
		verify(output).writeByte(6);
	}

	/**
	 * When the parity {@link #read(DataInput)} decoded value, has no corresponding {@link Parity}
	 * value, should be return a <code>null</code> value.
	 * 
	 * @throws Exception
	 */
	@Test
	public void parity_null() throws Exception {
		cmd = new ParityControlCmd(buffer(6).toDataInput());
		cmd.write(output);
		assertThat(cmd.getParity(), is(nullValue()));
	}

	/**
	 * If an invalid parity value is read from the input, an {@link IOException} is expected.
	 */
	@SuppressWarnings("unused")
	@Test
	public void parity_invalidValue() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected parity value: -1");

		new ParityControlCmd(buffer(-1).toDataInput());
	}

	/**
	 * Checks whether the commands equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		ParityControlCmd cmd = new ParityControlCmd(PARITY_MARK);
		ParityControlCmd cmd2 = new ParityControlCmd(PARITY_MARK);
		assertThat(cmd.equals(cmd2), is(true));
	}

	/**
	 * Checks whether the commands not equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		ParityControlCmd cmd = new ParityControlCmd(PARITY_MARK);
		ParityControlCmd cmd2 = new ParityControlCmd(PARITY_ODD);
		assertThat(cmd.equals(cmd2), is(false));
	}

	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		ParityControlCmd cmd = new ParityControlCmd(PARITY_MARK);
		assertThat(cmd.toString(), is("ParityControlCmd [parity=4]"));
	}
}
