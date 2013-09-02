/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 14:03:23
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import static java.lang.System.arraycopy;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.xidobi.spi.Reader;

/**
 * Implementation for read-operations.
 * 
 * @author Christian Schwarz
 */
@SuppressWarnings("restriction")
final class ReaderImpl implements Reader {

	/** The inputstream that belongs to the telnet-connection. */
	private InputStream inputStream;

	/**
	 * Creates a new Reader.
	 * 
	 * @param inputStream
	 *            the inputstream that belongs to the telnet-connection, must not be
	 *            <code>null</code>
	 */
	public ReaderImpl(InputStream inputStream) {
		this.inputStream = checkArgumentNotNull(inputStream, "inputStream");
	}

	public byte[] read() throws IOException {

		byte[] buffer = new byte[4096];

		final int readBytes = inputStream.read(buffer);
		if (readBytes == buffer.length)
			return buffer;

		byte[] result = new byte[readBytes];
		arraycopy(buffer, 0, result, 0, readBytes);

		return result;
	}

	public void close() throws IOException {
	}

	public void dispose() {
	}
}