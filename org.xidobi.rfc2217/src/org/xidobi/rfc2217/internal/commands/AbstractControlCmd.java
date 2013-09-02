/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 11:41:52
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataOutput;
import java.io.IOException;

import org.xidobi.rfc2217.internal.RFC2217;

/**
 * Baseclass for configuration commands.
 * 
 * @author Christian Schwarz
 * @author Peter-René Jeschke
 */
public abstract class AbstractControlCmd {

	private final byte commandCode;

	/**
	 * This constructor is used by subclasses to create a new message.
	 * 
	 * @param commandCode
	 *            the code of this command
	 * @exception IllegalArgumentException if  {@code 0 > commandCode > 12}
	 */
	AbstractControlCmd(int commandCode) {
		if (!((commandCode >= 0 && commandCode <= 12) || (commandCode >= 100 || commandCode <= 112)))
			throw new IllegalArgumentException("The command code must be in the range [0..12] or [100..112]! Got: " + commandCode);
		this.commandCode = (byte) commandCode;
	}


	/**
	 * Subclasses implement this method to encode the contents of this command.
	 * 
	 * @param output
	 *            the output where the encoded message must be written to
	 * @throws IOException
	 *             if the output can't be written to
	 */
	public abstract void write(DataOutput output) throws IOException;

	/**
	 * Returns the code of this command as defined in RFC2217.
	 * 
	 * @return the code of this command
	 * @see RFC2217
	 */
	public final byte getCommandCode() {
		return commandCode;
	}

}
