/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.08.2013 10:18:45
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.net.InetSocketAddress.createUnresolved;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests the class {@link Rfc2217SerialPort}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestRfc2217SerialPort {

	/** The Dummy Address of an Acess Server */
	private static final InetSocketAddress ACCESS_SERVER_ADDRESS = createUnresolved("host", 12345);

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	private Rfc2217SerialPort port;

	/**
	 * Init's the {@link Rfc2217SerialPort} with an unresolved Address.
	 */
	@Before
	public void setUp() {
		initMocks(this);
		port = new Rfc2217SerialPort(ACCESS_SERVER_ADDRESS);
	}

	/**
	 * If argument {@code accessServer} is <code>null</code> an {@link IllegalArgumentException}
	 * must be thrown.
	 */
	@Test
	public void new_nullAddress() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Parameter >accessServer< must not be null!");

		new Rfc2217SerialPort(null);
	}

	/**
	 * If argument {@code settings} is <code>null</code> an {@link IllegalArgumentException} must be
	 * thrown.
	 * @throws IOException 
	 */
	@Test
	public void open_nullSetting() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Parameter >settings< must not be null!");
		
		port.open(null);
	}
}
