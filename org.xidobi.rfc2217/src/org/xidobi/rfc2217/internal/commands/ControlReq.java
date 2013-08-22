/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 21.08.2013 15:17:35
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import javax.annotation.Nonnull;

/**
 * Base class for all Com-Port-Option Request Messages. The specific command is passed to the
 * constructor.
 * 
 * @author Christian Schwarz
 * 
 */
public class ControlReq {

	/**
	 * Creates a new {@link ControlReq} with the given Command Request.
	 * 
	 * @param command
	 *            the Command of this request
	 * @exception IllegalArgumentException
	 *                if <code>null</code> is passed
	 */
	public ControlReq(@Nonnull AbstractControlCmdReq command) {}
}
