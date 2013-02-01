/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 01.02.2013 11:06:46
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.spi;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * @author Christian Schwarz
 *
 */
public interface Reader extends Closeable{

	/**
	 * Reads from this Serialport and returns the read byte's or throws an {@link IOException} when
	 * the port was closed or an other I/O error occurs. This method blocks until at least one byte
	 * can be returned or an {@link IOException} is thrown.
	 * 
	 * @return the received byte[], never <code>null</code>
	 * @throws IOException
	 *             if this port was closed or an unexpected I/O error occurs.
	 */
	@Nonnull
	byte[] read() throws IOException;
}