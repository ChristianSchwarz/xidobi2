/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 21.08.2013 14:28:25
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.IOException;

/**
 * Defines the Basic Interface for command that can be send to the access server or can be received
 * by the client.
 * 
 * @author Christian Schwarz
 * 
 */
public abstract class AbstractControlCmdResp {

	 AbstractControlCmdResp(DataInput input) throws IOException {
		read(input);
	}

	/**
	 * @param input
	 */
	protected abstract void read(DataInput input)throws IOException;

	
}