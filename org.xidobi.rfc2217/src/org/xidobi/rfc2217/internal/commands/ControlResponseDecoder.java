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
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_RESP;

/**
 * Decodes com port option responses of the access server.
 * 
 * @author Christian Schwarz
 */
public class ControlResponseDecoder {

	/**
	 * Decodes the input and returns it as control command response.
	 * 
	 * @param input
	 *            the data to decode
	 * @return the decoded resonse
	 * @throws IOException
	 *             if the input is malformed or unknown
	 */
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
			case SET_STOPSIZE_RESP:
				return new StopBitsControlCmd(input);
			case SET_PARITY_RESP:
				return new ParityControlCmd(input);
			case SET_CONTROL_RESP:
				return new FlowControlCmd(input);
			case SIGNATURE_RESP:
				return new SignatureControlCmd(input);
		}

		throw new IOException("Unknown command option! Got: " + command);
	}
}
