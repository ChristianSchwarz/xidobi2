/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 11:23:00
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import java.io.IOException;

/**
 * Interface for serial port handles.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public interface SerialPortHandle {

	/**
	 * Opens a serial port with the given control settings and returns the connected serial port.
	 * 
	 * @param portName
	 *            the name of the port to be open, must not be <code>null</code>
	 * @param settings
	 *            the control settings for the port, must not be <code>null</code>
	 * @return a connected serial port, never <code>null</code>
	 * @throws IOException
	 *             if the port cannot be opened
	 */
	SerialPort open(String portName, SerialPortSettings settings) throws IOException;
}
