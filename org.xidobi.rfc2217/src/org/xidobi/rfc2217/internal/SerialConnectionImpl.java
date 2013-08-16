/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.08.2013 10:59:58
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.xidobi.rfc2217.Rfc2217SerialPort;
import org.xidobi.spi.BasicSerialConnection;

/**
 * @author Christian Schwarz
 *
 */
public class SerialConnectionImpl extends BasicSerialConnection {

	private static final String TERMINAL_TYPE = "VT100";
	
	/**
	 * @param parent the serial port, must not be <code>null</code>
	 * @param reader
	 * @param writer
	 */
	protected SerialConnectionImpl(	@Nonnull Rfc2217SerialPort parent,
									@Nonnull TelnetClient tc) {
		super(parent, null, null);
		
		tc.setReaderThread(true);                                   // allows immediate option negotiation
        try {
            tc.addOptionHandler(new TerminalTypeOptionHandler(TERMINAL_TYPE, false, false, true, false));
            tc.addOptionHandler(new EchoOptionHandler(false, false, false, false));
            tc.addOptionHandler(new SuppressGAOptionHandler(true, true, true, true));
            tc.addOptionHandler(new BinaryOptionHandler());
            tc.addOptionHandler(new ComPortOptionHandler(this));
        } catch (IOException e) {
            throw new RuntimeException("unexpected exception", e);
        } catch (InvalidTelnetOptionException e) {
            throw new RuntimeException("unexpected exception", e);
        }
	}

	
}
