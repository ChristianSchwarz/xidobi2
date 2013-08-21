/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 21.08.2013 14:28:25
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Base class for command responses send from the access server. Subclasses override
 * {@link #read(DataInput)} to by the client.
 * 
 * @author Christian Schwarz
 * 
 */
public abstract class AbstractControlCmdResp {

	/**
	 * This constructor is used by subclasses to decode the response.
	 * <p>
	 * Implementation Note: The the constuctor call will be delegate to {@link #read(DataInput)} to
	 * {@link #read(DataInput)} in order to decode the content.
	 * 
	 * @param input
	 *            used to decode the content
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	AbstractControlCmdResp(@Nonnull DataInput input) throws IOException {
		read(input);
	}

	/**
	 * Subclasses implement this methode to decode the content of this instance. The given input
	 * starts at the beginning of the response specific content. That means after the command
	 * specific idenitfier.
	 * 
	 *@param input
	 *            used to decode the content
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	protected abstract void read(@Nonnull DataInput input) throws IOException;

}