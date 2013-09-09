/*
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_RESP;

import java.io.DataInput;
import java.io.IOException;

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
