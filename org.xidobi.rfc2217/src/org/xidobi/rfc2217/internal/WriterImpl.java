/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 14:03:30
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;

import org.xidobi.spi.Writer;

/**
 * @author Christian Schwarz
 *
 */
final class WriterImpl implements Writer {
	public void performActionBeforeConnectionClosed() throws IOException {}

	public void performActionAfterConnectionClosed() {}

	public void write(byte[] data) throws IOException {}
}