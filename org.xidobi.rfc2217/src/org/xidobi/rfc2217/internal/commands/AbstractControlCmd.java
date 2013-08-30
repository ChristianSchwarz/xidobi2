/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 11:41:52
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.rfc2217.internal.RFC2217;

/**
 * Baseclass for configuration commands.
 * 
 * @author Christian Schwarz
 * @author Peter-René Jeschke
 */
public abstract class AbstractControlCmd {

	private byte commandCode;

	/**
	 * This constructor is used by subclasses to create a new message.
	 * 
	 * @param commandCode
	 *            the code of this command
	 * @exception IllegalArgumentException if  {@code 0 > commandCode > 12}
	 */
	AbstractControlCmd(int commandCode) {
		if (commandCode < 0 || commandCode > 12)
			throw new IllegalArgumentException("The command codem must be in the range [0..12]! Got: " + commandCode);
		this.commandCode = (byte) commandCode;
	}

	/**
	 * This constructor is used by subclasses to decode the message.
	 * <p>
	 * Implementation Note: The constructor call will delegate {@link #read(DataInput)} to
	 * {@link #read(DataInput)} in order to decode the content.
	 * 
	 * @param commandCode
	 *            the code of this command
	 * @param input
	 *            used to decode the content
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	AbstractControlCmd(	int commandCode,
						@Nonnull DataInput input) throws IOException {
		this(commandCode);
		read(input);
	}

	/**
	 * Subclasses implement this method to decode the content of this command. The given input
	 * starts at the beginning of the response specific content. That means after the command
	 * specific identifier.
	 * 
	 * @param input
	 *            used to decode the content
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	protected abstract void read(DataInput input) throws IOException;

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
