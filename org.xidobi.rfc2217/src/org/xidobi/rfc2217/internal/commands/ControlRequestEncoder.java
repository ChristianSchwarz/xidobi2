/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 21.08.2013 15:17:35
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Nonnull;

import static org.xidobi.rfc2217.internal.ArrayUtil.toIntArray;
import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

/**
 * Provides static methods to create Com-Port-Option Requests in binary form. The specific command
 * is passed to the constructor.
 * 
 * @author Christian Schwarz
 * 
 */
public class ControlRequestEncoder {

	/** This class is not intendet to be instanciated */
	private ControlRequestEncoder() {}

	/**
	 * Encodes the given message to an array of int's, every int of this represents a byte-value.
	 * 
	 * 
	 * @param message the message to encode
	 * @return the encoded message
	 * 
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

		System.err.println("->" + Arrays.toString(bo.toByteArray()));

		return toIntArray(bo.toByteArray());
	}
}
