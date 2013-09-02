/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.08.2013 10:59:58
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.TelnetClient;
import org.xidobi.rfc2217.Rfc2217SerialPort;
import org.xidobi.spi.BasicSerialConnection;

/**
 * @author Christian Schwarz
 */
public class SerialConnectionImpl extends BasicSerialConnection {

	private TelnetClient telnetClient;

	/**
	 * @param parent
	 *            the serial port, must not be <code>null</code>
	 * @param reader
	 * @param writer
	 */
	public SerialConnectionImpl(@Nonnull Rfc2217SerialPort parent,
								@Nonnull TelnetClient telnetClient) {
		super(parent, new ReaderImpl(telnetClient.getInputStream()), new WriterImpl(telnetClient.getOutputStream()));
		this.telnetClient = telnetClient;
	}

	@Override
	protected void closeInternal() throws IOException {
		telnetClient.disconnect();
	}

}