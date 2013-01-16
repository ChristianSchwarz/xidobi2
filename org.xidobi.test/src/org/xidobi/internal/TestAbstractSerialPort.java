/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.01.2013 17:04:21
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xidobi.SerialPort;

import static junit.framework.Assert.fail;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.hamcrest.Matchers.is;

/**
 * Tests the class {@link AbstractSerialPort}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestAbstractSerialPort {

	/** constant for better readability */
	private static final IOException IO_EXCEPTION = new IOException();

	/** the class under test */
	private AbstractSerialPort port;

	/** needed to verify exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		initMocks(this);
		port = mock(AbstractSerialPort.class);
	}

	/**
	 * Verifies that a call to {@link SerialPort#close()} will be deleagted to
	 * {@link AbstractSerialPort#closeInternal()}.
	 * 
	 * @throws IOException
	 */

	@Test
	public void close() throws IOException {
		port.close();
		verify(port).closeInternal();
	}

	/**
	 * Verifies that only the first call to {@link SerialPort#close()} will be deleagted to
	 * {@link AbstractSerialPort#closeInternal()}.
	 */
	@Test
	public void close_2x() throws Exception {
		port.close();
		port.close();
		verify(port).closeInternal();
	}

	/**
	 * Verifies that the same {@link IOException} thrown by
	 * {@link AbstractSerialPort#closeInternal()} is forwarded to the caller of
	 * {@link SerialPort#close()} without modification.
	 */
	@Test
	public void close_IOException() throws Exception {
		exception.expect(is(IO_EXCEPTION));

		doThrow(IO_EXCEPTION).when(port).closeInternal();
		port.close();
	}

	/**
	 * Verifies that all calls to {@link SerialPort#close()} will be delegate to
	 * {@link SerialPort#close()} when all previous calls thrown an IOException and thus the port
	 * was not closed.
	 */
	@Test
	public void close_2xIOException() throws Exception {
		doThrow(IO_EXCEPTION).when(port).closeInternal();
		try {
			port.close();
			fail("Expected an IOException");
		}
		catch (IOException ignored) {}
		try {
			port.close();
			fail("Expected an IOException");
		}
		catch (IOException ignored) {}
		
		verify(port, times(2)).closeInternal();

	}
}
