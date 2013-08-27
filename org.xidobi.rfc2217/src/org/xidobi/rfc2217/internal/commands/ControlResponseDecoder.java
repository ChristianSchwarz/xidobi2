/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 21.08.2013 15:14:41
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.IOException;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_RESP;

/**
 * @author Christian Schwarz
 */
public class ControlResponseDecoder {

	/** Decodes the input and returns its as control command */
	public AbstractControlCmd decode(DataInput input) throws IOException {
		byte option = input.readByte();
		if (option != COM_PORT_OPTION)
			throw new IOException("Unexpected telnet option! Got: " + option);

		byte command = input.readByte();
		switch (command) {
			case SET_BAUDRATE_RESP:
				return new BaudrateControlCmd(input);
			case SET_DATASIZE_RESP:
				return new DataBitsControlCmd(input);

		}

		throw new IOException("Unsupported command option! Got: " + command);
	}
}
