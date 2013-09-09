/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 06.09.2013 15:31:34
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataOutput;
import java.io.IOException;

import org.xidobi.rfc2217.internal.RFC2217;

/**
 * @author Christian Schwarz
 */
public interface ControlCmd {

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
	public abstract byte getCommandCode();

}