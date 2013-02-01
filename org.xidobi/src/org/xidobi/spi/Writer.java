/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 01.02.2013 11:08:32
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
public interface Writer extends Closeable{

	/**
	 * Writes the given byte[]. All bytes of the array were written.
	 * 
	 * @param data
	 *            must not be <code>null</code>
	 * @throws IOException
	 *             when the Port is closed
	 */
	void write(@Nonnull byte[] data) throws IOException;
}
