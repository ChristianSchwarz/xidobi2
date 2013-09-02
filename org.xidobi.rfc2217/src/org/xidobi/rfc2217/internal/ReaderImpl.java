/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 14:03:23
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.xidobi.spi.Reader;
import org.xidobi.utils.Throwables;

import static java.lang.System.arraycopy;

/**
 * @author Christian Schwarz
 * 
 */
final class ReaderImpl implements Reader {
	private InputStream inputStream;

	/**
	 * @param inputStream
	 */
	public ReaderImpl(InputStream inputStream) {
		this.inputStream = inputStream;}

	public byte[] read() throws IOException {
		
			
		byte[] buffer = new byte[4096];
		
		final int readBytes = inputStream.read(buffer);
		if (readBytes==buffer.length)
			return buffer;
		
		byte[] result = new byte[readBytes];
		arraycopy(buffer, 0, result, 0, readBytes);
		
		return result;
	}

	public void performActionBeforeConnectionClosed() throws IOException {}

	public void performActionAfterConnectionClosed() {}
}