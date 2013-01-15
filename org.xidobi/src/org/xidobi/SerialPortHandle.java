/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 11:23:00
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import java.io.IOException;

/**
 * @author Christian Schwarz
 *
 */
public interface SerialPortHandle {

	SerialPort open(String portName, SerialPortSettings settings) throws IOException;
}
