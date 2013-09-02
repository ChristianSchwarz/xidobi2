/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 02.09.2013 11:37:47
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;

/**
 * Tests the class {@link WriterImpl}.
 * 
 * @author Peter-René Jeschke
 */
public class TestWriterImpl {

	@Rule
	public ExpectedException exception = none();

	private WriterImpl writer;

	@Mock
	private OutputStream out;

	@Before
	public void setup() {
		initMocks(this);
		writer = new WriterImpl(out);
	}

	/**
	 * Expects to throw an {@link IllegalArgumentException} if the argument for the constructor is
	 * null.
	 **/
	@SuppressWarnings("unused")
	@Test
	public void new_withNull() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >outputStream< must not be null!");
		new WriterImpl(null);
	}

	/**
	 * When the writer is requested to write bytes, the bytes must be written to the outputstream
	 * and the stream must be flushed.
	 */
	@Test
	public void write_writesData() throws IOException {
		byte[] data = new byte[] { 1, 2, 3, 4, 5 };
		writer.write(data);

		verify(out).write(data);
		verify(out).flush();
	}

	/**
	 * When <code>null</code> is supplied as data to write(), an {@link IllegalArgumentException}
	 * must be thrown.
	 */
	@Test
	public void write_withNull() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >data< must not be null!");
		writer.write(null);
	}

}
