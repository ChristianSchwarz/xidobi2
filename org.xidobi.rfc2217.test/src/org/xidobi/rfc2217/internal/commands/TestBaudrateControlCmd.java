/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 12:45:13
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;

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
	 * Checks whether the commands equal.
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		BaudrateControlCmd cmd2 =  new BaudrateControlCmd(1600);
		assertThat(cmd.equals(cmd2),is(true));
	}
	/**
	 * Checks whether the commands not equal.
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		BaudrateControlCmd cmd2 = new BaudrateControlCmd(1601);
		assertThat(cmd.equals(cmd2), is(false));
	}
	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		assertThat(cmd.toString(), is("BaudrateControlCmd [baudrate=1600]"));
	}

}
