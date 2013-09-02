/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.08.2013 11:05:11
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.xidobi.rfc2217.Rfc2217SerialPort;

/**
 * Tests the class {@link SerialConnectionImpl}
 * 
 * @author Christian Schwarz
 */
public class TestSerialConnectionImpl {

	private SerialConnectionImpl connection;

	@Mock
	private Rfc2217SerialPort parent;

	@Mock
	private TelnetClient telnetClient;

	@Before
	public void setup() {
		initMocks(this);

		when(telnetClient.getInputStream()).thenReturn(mock(InputStream.class));
		when(telnetClient.getOutputStream()).thenReturn(mock(OutputStream.class));

		connection = new SerialConnectionImpl(parent, telnetClient);
	}

	/**
	 * When the connection is closed, the telnetClient must be closed, too.
	 */
	@Test
	public void close_closesTelnetClient() throws IOException {
		connection.close();
		verify(telnetClient).disconnect();
	}
}
