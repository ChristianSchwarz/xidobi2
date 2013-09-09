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
import static testtools.MessageBuilder.flowControlResponse;
import static testtools.MessageBuilder.parityResponse;
import static testtools.MessageBuilder.signatureResponse;
import static testtools.MessageBuilder.stopBitsResponse;

import java.io.IOException;

import org.apache.commons.net.telnet.TelnetOption;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;

import testtools.MessageBuilder;

/**
 * Tests class {@link ControlResponseDecoder}
 * 
 * @author Christian Schwarz
 * @author Peter-René Jeschke
 */
public class TestControlResponseDecoder {

	/** needed to verify exceptions */
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

	/** Tests whether a well formatted data bits response can be decoded. */
	@Test
	public void decode_invalidOption() throws Exception {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected telnet option! Got: " + BINARY);

		decoder.decode(buffer(TelnetOption.BINARY).toDataInput());
	}

	/** Tests whether a well formatted data bits response can be decoded. */
	@Test
	public void decode_databits() throws Exception {
		resp = decoder.decode(dataBitsResponse(6).toDataInput());
		assertThat(resp, is(instanceOf(DataBitsControlCmd.class)));
	}

	/** Tests whether a well formatted baud rate response can be decoded. */
	@Test
	public void decode_baudRate() throws Exception {
		resp = decoder.decode(baudRateResponse(9600).toDataInput());
		assertThat(resp, is(instanceOf(BaudrateControlCmd.class)));
	}

	/** Tests whether a well formmtted parity response can be decoded. */
	@Test
	public void decode_parity() throws Exception {
		resp = decoder.decode(parityResponse(2).toDataInput());
		assertThat(resp, is(instanceOf(ParityControlCmd.class)));
	}

	/** Tests whether a well formatted flow-control response can be decoded. */
	@Test
	public void decode_flowControl() throws Exception {
		resp = decoder.decode(parityResponse(2).toDataInput());
		assertThat(resp, is(instanceOf(ParityControlCmd.class)));
	}

	/**
	 * Tests whether a well formatted stop-bits response can be decoded.
	 */
	@Test
	public void decode_stopBits() throws IOException {
		resp = decoder.decode(stopBitsResponse(2).toDataInput());
		assertThat(resp, is(instanceOf(StopBitsControlCmd.class)));
	}

	/**
	 * Tests whether a well formatted control-response can be decoded.
	 */
	@Test
	public void decode_control() throws IOException {
		resp = decoder.decode(flowControlResponse(2).toDataInput());
		assertThat(resp, is(instanceOf(FlowControlCmd.class)));
	}

	/**
	 * Tests whether a well formatted signature-response can be decoded.
	 */
	@Test
	public void decode_signature() throws IOException {
		resp = decoder.decode(signatureResponse("Signature").toDataInput());
		assertThat(resp, is(instanceOf(SignatureControlCmd.class)));
	}

	/**
	 * Tests whether an unknown response causes an IOException.
	 */
	@Test
	public void decode_invalidResponse() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unknown command option! Got: 90");
		decoder.decode(MessageBuilder.buildComPortCommand(90).toDataInput());
	}
}
