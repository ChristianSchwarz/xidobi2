/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 21.08.2013 15:18:01
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Christian Schwarz
 */
public abstract class AbstractControlCmdReq {

	protected abstract void write(DataOutput output) throws IOException;

}
