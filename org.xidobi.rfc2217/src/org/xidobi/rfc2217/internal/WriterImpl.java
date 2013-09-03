/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 14:03:30
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;
import java.io.OutputStream;

import org.xidobi.spi.Writer;

import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

/**
 * Writer-implementation for a telnet-connection.
 * 
 * @author Christian Schwarz
 * @author Peter-René Jeschke
 */
@SuppressWarnings("restriction")
final class WriterImpl implements Writer {

	/** The outputstream that belongs to the connection. */
	private OutputStream outputStream;

	/**
	 * Creates a new Writer.
	 * 
	 * @param outputStream
	 *            the outputstream that belongs to the connection, must not be <code>null</code>
	 */
	public WriterImpl(OutputStream outputStream) {
		this.outputStream = checkArgumentNotNull(outputStream, "outputStream");

	}

	public void write(byte[] data) throws IOException {
		checkArgumentNotNull(data, "data");
		outputStream.write(data);
		outputStream.flush();
	}

	public void performActionBeforeConnectionClosed() throws IOException {
	}

	public void performActionAfterConnectionClosed() {
	}

}
