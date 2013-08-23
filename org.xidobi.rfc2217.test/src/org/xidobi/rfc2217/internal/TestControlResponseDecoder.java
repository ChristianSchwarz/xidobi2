/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 23.08.2013 14:16:42
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.xidobi.rfc2217.internal.commands.ControlResponseDecoder;

import testtools.ByteBuffer;
import testtools.MessageBuilder;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE;
import static testtools.MessageBuilder.buffer;

/**
 * Tests class {@link ControlResponseDecoder}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestControlResponseDecoder {

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	@InjectMocks
	private ControlResponseDecoder decoder;

	@Before
	public void setUp() {
		initMocks(this);

	}

	/**
	 * 
*/
	@Test
	public void decodeBaudRateCommand() throws Exception {
		DataInput input = buffer()//
		.putBytes(COM_PORT_OPTION, SET_BAUDRATE)//
		.putInt(9600)//
		.toDataInput();

		decoder.decode(input);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Creates a new {@link DataInput} of the given array, using the lowest byte of every int. */
	private DataInput message(int... bytes) {
		byte[] buf = new ByteBuffer().putBytes(bytes).toByteArray();
		return new DataInputStream(new ByteArrayInputStream(buf));
	}
}
