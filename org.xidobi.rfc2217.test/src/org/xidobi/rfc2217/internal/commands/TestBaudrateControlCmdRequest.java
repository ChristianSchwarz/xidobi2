/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 08:52:10
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

/**
 * Tests the class {@link BaudrateControlCmdRequest}.
 * 
 * @author Peter-René Jeschke
 */
public class TestBaudrateControlCmdRequest {

	/** Used to test exceptions. */
	@Rule
	public ExpectedException exception = none();

	private BaudrateControlCmdRequest request;

	@Before
	public void setup() {
		initMocks(this);
		request = new BaudrateControlCmdRequest(1600, true);
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
		new BaudrateControlCmdRequest(-3, true);
	}

	/**
	 * Checks whether the encoded message is correct.
	 */
	@Test
	public void write_correctData() throws IOException {
		DataOutput output = mock(DataOutput.class);
		request.write(output);

		InOrder orderedVerification = inOrder(output);

		orderedVerification.verify(output).write(44); // COM-PORT-OPTION
		orderedVerification.verify(output).write(1); // By Client
		orderedVerification.verify(output).writeInt(1600); // The baudrate
	}

	/**
	 * Checks whether the encoded message is correct, when the request should be sent by server.
	 */
	@Test
	public void write_correctDataSentByServer() throws IOException {
		request = new BaudrateControlCmdRequest(1600, false);
		DataOutput output = mock(DataOutput.class);
		request.write(output);

		InOrder orderedVerification = inOrder(output);

		orderedVerification.verify(output).write(44); // COM-PORT-OPTION
		orderedVerification.verify(output).write(101); // By Client
		orderedVerification.verify(output).writeInt(1600); // The baudrate
	}
}
