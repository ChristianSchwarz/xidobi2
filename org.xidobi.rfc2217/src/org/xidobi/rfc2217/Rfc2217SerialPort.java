/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 14.08.2013 09:02:05
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillCloseWhenClosed;

import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortSettings;

/**
 * Implements the client side of the RFC2217 serial over telnet protocol as {@link SerialPort}.
 * 
 * @author Christian Schwarz
 * 
 */
public class Rfc2217SerialPort implements SerialPort {

	/**
	 * Opens this serial port by establihing a Telnet session with the access server, defined in the
	 * constructor.
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 *             if the port cannot be opened, this may occure if ...
	 *             <ul>
	 *             <li>the access server is unreachable 
	 *             <li>the access server refuse the Telnet connection
	 *             <li>a timeout occures during connect 
	 *             <li>the access server refuse the {@link SerialPortSettings}
	 *             </ul>
	 */
	@Nonnull
	@Override
	@WillCloseWhenClosed
	public SerialConnection open(@Nonnull SerialPortSettings settings) throws IOException {
		return null;
	}

	/**
	 * Returns the name of this port in the form {@code "RFC2217@"+hostname+":"+port}, e.g
	 * "RFC2217@192.168.0.15:5588". {@inheritDoc}
	 */
	@Nonnull
	@Override
	public String getPortName() {
		return null;
	}

	/**
	 * Returns the signature information as defined in RFC2217 of the acces server once this port is
	 * open. If this port is not open <code>null</code> will be returned.
	 * <p>
	 * The signature information may be combination of any characters, there is no defined structure
	 * of the text. It may contain manufactor information, version number information, or any other
	 * information. Thus clients should not rely on this!
	 * 
	 * @return <ul>
	 *         <li><code>null</code> if this port is not open <li>signature information of the
	 *         access server as defined in RFC2217, if this port is open
	 *         </ul>
	 */
	@Nullable
	@Override
	public String getDescription() {
		return null;
	}

}
