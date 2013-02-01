/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 01.02.2013 11:11:03
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.spi;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.xidobi.SerialPort;

/**
 * @author Christian Schwarz
 *
 */
public class BasicSerialConnection extends AbstractSerialConnection {

	private Reader reader;
	private Writer writer;

	/**
	 * @param portHandle
	 */
	protected BasicSerialConnection(SerialPort portHandle, Reader reader, Writer writer) {
		super(portHandle);
		this.reader = reader;
		this.writer = writer;
	}

	@Override
	protected void writeInternal(@Nonnull byte[] data) throws IOException {
		writer.write(data);
	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {
		return reader.read();
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	protected void closeInternal() throws IOException {
		writer.close();
		reader.close();
	}

}
