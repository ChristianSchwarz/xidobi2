/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 23.08.2013 14:16:42
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.DataInput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.xidobi.rfc2217.internal.commands.BaudrateControlCmd;
import org.xidobi.rfc2217.internal.commands.ControlResponseDecoder;

import testtools.MessageBuilder;

import static org.hamcrest.Matchers.is;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.hamcrest.MatcherAssert.assertThat;
import static testtools.MessageBuilder.buffer;
import static testtools.MessageBuilder.buildSetBaudRateResponse;

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
	 * Decode must fail if the option code is not the expected RFC2217 Com-Port-Option (44)
	 */
	@Test
	public void decodeUnexpectedOptionCode() throws Exception {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected option code: 0");


		DataInput input = buffer(0).toDataInput();//
		decoder.decode(input);

	}
	/**
	 * 
	 */
	@Test
	public void decodeBaudRateCommand() throws Exception {
		DataInput input = buildSetBaudRateResponse(9600).toDataInput();//
		BaudrateControlCmd cmd = (BaudrateControlCmd) decoder.decode(input);
		assertThat(cmd.getBaudrate(), is(9600));
	}

}
