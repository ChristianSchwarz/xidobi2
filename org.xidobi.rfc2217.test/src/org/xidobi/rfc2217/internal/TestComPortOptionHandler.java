/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 08:54:12
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.DataInput;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.CommandProcessor;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmd;
import org.xidobi.rfc2217.internal.commands.ControlResponseDecoder;

import static org.mockito.Matchers.argThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;

/**
 * Tests the class {@link ComPortOptionHandler}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestComPortOptionHandler {

	private static final AbstractControlCmd DUMMY_RESPONSE = mock(AbstractControlCmd.class);

	/** needed to verifiy exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private CommandProcessor processor;

	@Mock
	private ControlResponseDecoder decoder;

	/** class under test */
	@InjectMocks
	private ComPortOptionHandler handler;

	@Captor
	private ArgumentCaptor<DataInput> input;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * When <code>null</code> is passed an {@link IllegalArgumentException} must be throw.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void new_nullProcessor() {
		new ComPortOptionHandler(null);
	}

	/**
	 * When a command response of the acces server was received the registered
	 * {@link CommandProcessor} must be notified.
	 */
	@Test
	public void answerSubnegotiation() {
		when(decoder.decode(argThat(is(any(DataInput.class))))).thenReturn(DUMMY_RESPONSE);

		final int[] binaryDummyResponse = {};
		handler.answerSubnegotiation(binaryDummyResponse, binaryDummyResponse.length);

		verify(processor).onResponseReceived(DUMMY_RESPONSE);
	}

	
}
