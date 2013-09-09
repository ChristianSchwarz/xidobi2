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

import static org.xidobi.rfc2217.internal.ArrayUtil.toIntArray;
import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Provides static methods to create Com-Port-Option Requests in binary form. The specific command
 * is passed to the constructor.
 * 
 * @author Christian Schwarz
 */
public class ControlRequestEncoder {

	/** This class is not intendet to be instanciated */
	private ControlRequestEncoder() {
	}

	/**
	 * Encodes the given message to an array of int's, every int of this represents a byte-value.
	 * 
	 * @param message
	 *            the message to encode
	 * @return the encoded message
	 * @throws IOException
	 *             if an failure occured during encoding the message
	 */
	@Nonnull
	public static int[] encode(@Nonnull ControlCmd message) throws IOException {
		checkArgumentNotNull(message, "message");

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		DataOutput output = new DataOutputStream(bo);
		output.writeByte(COM_PORT_OPTION);
		output.writeByte(message.getCommandCode());
		message.write(output);

		return toIntArray(bo.toByteArray());
	}
}
