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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.google.common.testing.EqualsTester;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static testtools.MessageBuilder.buffer;

/**
 * Tests the class {@link BaudrateControlCmd}.
 * 
 * @author Peter-René Jeschke
 */
@SuppressWarnings("javadoc")
public class TestBaudrateControlCmd {

	@Rule
	public ExpectedException exception = none();

	private BaudrateControlCmd cmd;

	@Mock
	private DataInput input;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		when(input.readUnsignedByte()).thenReturn(44, 1);
		when(input.readInt()).thenReturn(1600);

		cmd = new BaudrateControlCmd(1600);
	}

	/**
	 * When the commandCode is the code of the client, the message must be accepted and read.
	 */
	@Test
	public void read_isCorrect() throws IOException {
		cmd = new BaudrateControlCmd(input);

		assertThat(cmd.getBaudrate(), is(1600));
	}

	/**
	 * When the baudrate is invalid, an {@link IOException} should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void read_invalidBaudrate() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The received baudrate is invalid! Expected a value greater or equal to 1, got: >-3<");

		when(input.readInt()).thenReturn(-3);

		new BaudrateControlCmd(input);
	}

	/**
	 * When a baudrate that is smaller than 1 is supplied to the constructor, an
	 * {@link IllegalArgumentException} should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_withNegativeBaudrate() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The baudrate must not be less than 1! Got: >-3<");
		new BaudrateControlCmd(-3);
	}

	/**
	 * Checks whether the encoded message is correct.
	 */
	@Test
	public void write_correctData() throws IOException {
		DataOutput output = mock(DataOutput.class);
		cmd.write(output);

		InOrder orderedVerification = inOrder(output);

		orderedVerification.verify(output).writeInt(1600); // The baudrate
	}
	/**
	 * Checks the equals/hashCode contract.
	 *  
	 * @throws Exception
	 */
	//@formatter:off
	@Test
	public void equalsHashCode() throws Exception {
		new EqualsTester()
		.addEqualityGroup(new BaudrateControlCmd(9600),
		                  new BaudrateControlCmd(9600))
		.addEqualityGroup(new BaudrateControlCmd(4800))
		.addEqualityGroup(new BaudrateControlCmd(1200),
		                  new BaudrateControlCmd(buffer().putInt(1200).toDataInput()))
		.testEquals();
	}
	//@formatter:on
	
	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		assertThat(cmd.toString(), is("BaudrateControlCmd [baudrate=1600]"));
	}

}
