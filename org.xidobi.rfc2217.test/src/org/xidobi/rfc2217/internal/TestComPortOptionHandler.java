/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 08:54:12
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.CommandProcessor;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmdResp;

import static org.mockito.Mockito.verify;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import testtools.MessageBuilder;
import static org.mockito.MockitoAnnotations.initMocks;
import static testtools.MessageBuilder.buildSetBaudRateRequest;

/**
 * Tests the class {@link ComPortOptionHandler}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestComPortOptionHandler {

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	
	@Mock
	private CommandProcessor processor;
	
	/** class under test */
	@InjectMocks
	private ComPortOptionHandler handler;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * When <code>null</code> is passed an {@link IllegalArgumentException} must be throw.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void new_nullProcessor() throws Exception {
		new ComPortOptionHandler(null);
	}

	/**
	 * When a command response of the acces server was received the registered
	 * {@link CommandProcessor} must be notified.
	 */
	@Test
	public void answerSubnegotiation() throws Exception {
		final int[] command = buildSetBaudRateRequest(9600);
		handler.answerSubnegotiation(command, command.length);
		
		verify(processor).onResponseReceived(Mockito.argThat(any(AbstractControlCmdResp.class)));
	}
}
