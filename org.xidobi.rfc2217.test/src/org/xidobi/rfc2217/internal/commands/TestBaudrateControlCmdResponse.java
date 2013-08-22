/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 10:42:21
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Tests the class {@link BaudrateControlCmdResponse}.
 * 
 * @author Peter-René Jeschke
 */
public class TestBaudrateControlCmdResponse {

	@Rule
	public ExpectedException exception = none();

	private BaudrateControlCmdResponse response;

	@Mock
	private DataInput input;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		when(input.readUnsignedByte()).thenReturn(44, 1);
		when(input.readInt()).thenReturn(1600);
	}

	/**
	 * When the commandCode is the code of the client, the message must be accepted and read.
	 */
	@Test
	public void read_isCorrect() throws IOException {
		response = new BaudrateControlCmdResponse(input);

		assertThat(response.getBaudrate(), is(1600));
	}

	/**
	 * When the commandCode is the code of the server, the message must be accepted and read.
	 */
	@Test
	public void read_fromServer() throws IOException {
		when(input.readUnsignedByte()).thenReturn(44, 101);

		response = new BaudrateControlCmdResponse(input);
		assertThat(response.getBaudrate(), is(1600));
	}

	/**
	 * When the commandCode is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	public void read_invalidCommandCode() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The message was invalid! Expected a baudrate, got a message with the command code >3<");

		when(input.readUnsignedByte()).thenReturn(44, 3);

		response = new BaudrateControlCmdResponse(input);
	}

	/**
	 * When the baudrate is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	public void read_invalidBaudrate() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The received baudrate is invalid! Expected a value greater or equal to 1, got: >-3<");

		when(input.readInt()).thenReturn(-3);

		response = new BaudrateControlCmdResponse(input);
	}
}
