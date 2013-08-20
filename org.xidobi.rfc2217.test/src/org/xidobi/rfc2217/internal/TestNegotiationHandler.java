/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 17:13:52
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests the class {@link NegotiationHandler}
 * @author Christian Schwarz
 *
 */
public class TestNegotiationHandler {

	/** needed to verifiy exception*/
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test*/
	@InjectMocks
	private NegotiationHandler handler;

	@Mock
	private TelnetClient telnetClient;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		initMocks(this);
		handler = new NegotiationHandler(telnetClient);
	}
	
	/**
	 * If <code>null</code> is passed to the constructor an {@link IllegalArgumentException} must be thrown.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void new_nullTelnetClient() {
		new NegotiationHandler(null);
		
	}
}
