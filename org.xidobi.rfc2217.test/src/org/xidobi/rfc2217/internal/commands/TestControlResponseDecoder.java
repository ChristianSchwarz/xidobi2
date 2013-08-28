/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 26.08.2013 15:39:33
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import static org.apache.commons.net.telnet.TelnetOption.BINARY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;
import static testtools.MessageBuilder.baudRateResponse;
import static testtools.MessageBuilder.buffer;
import static testtools.MessageBuilder.dataBitsResponse;
import static testtools.MessageBuilder.parityResponse;

import java.io.IOException;

import org.apache.commons.net.telnet.TelnetOption;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;

/**
 * Tests class {@link ControlResponseDecoder}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestControlResponseDecoder {

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	@InjectMocks
	private ControlResponseDecoder decoder;

	private AbstractControlCmd resp;

	@Before
	public void setUp() {
		initMocks(this);
		decoder = new ControlResponseDecoder();
	}

	/** Tests it a well formmatted data bits response can be decoded. */
	@Test
	public void decode_invalidOption() throws Exception {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected telnet option! Got: " + BINARY);

		decoder.decode(buffer(TelnetOption.BINARY).toDataInput());
	}

	/** Tests it a well formmatted data bits response can be decoded. */
	@Test
	public void decode_Databits() throws Exception {
		resp = decoder.decode(dataBitsResponse(6).toDataInput());
		assertThat(resp, is(instanceOf(DataBitsControlCmd.class)));
	}

	/** Tests it a well formmatted baud rate response can be decoded. */
	@Test
	public void decode_baudRate() throws Exception {
		resp = decoder.decode(baudRateResponse(9600).toDataInput());
		assertThat(resp, is(instanceOf(BaudrateControlCmd.class)));
	}

	/** Tests it a well formmatted baud rate response can be decoded. */
	@Test
	public void decode_parity() throws Exception {
		resp = decoder.decode(parityResponse(9600).toDataInput());
		assertThat(resp, is(instanceOf(BaudrateControlCmd.class)));
	}
}
