/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.01.2013 17:04:21
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortHandle;

import static junit.framework.Assert.fail;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

	private static final byte[] BYTES = {};

	/** the class under test */
	private AbstractSerialPort port;

	@Mock
	private SerialPortHandle portHandle;

	/** needed to verify exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		initMocks(this);
		port = mock(AbstractSerialPort.class);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when <code>null</code> is passed
	 * to the constructor.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullPortPortHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >portHandle< must not be null!");

		new _AbstractSerialPort(null);
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

	/**
	 * Verifies that an {@link IOException} is thrown when the port is closed.
	 */
	@Test
	public void write_portIsClosed() throws Exception {
		when(portHandle.getPortName()).thenReturn("COM1");
		port = new _AbstractSerialPort(portHandle);
		port.close();

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed!");

		port.write(BYTES);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when <code>null</code> is passed.
	 */
	@Test
	public void write_nullData() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >data< must not be null!");

		port.write(null);
	}

	/**
	 * Verifies that {@link SerialPort#write(byte[])} deleagtes to
	 * {@link AbstractSerialPort#writeInternal(byte[])} if <code>data!=null</code> and the port is
	 * not closed.
	 */
	@Test
	public void write_delegate() throws Exception {
		port.write(BYTES);
		verify(port).writeInternal(BYTES);
	}

	/**
	 * Verifies that an {@link IOException} is thrown when the port is closed.
	 */
	@Test
	public void read_portIsClosed() throws Exception {
		when(portHandle.getPortName()).thenReturn("COM1");
		port = new _AbstractSerialPort(portHandle);
		port.close();

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed!");

		port.read();
	}

	/**
	 * Verifies that {@link SerialPort#read()} is delegated to
	 * {@link AbstractSerialPort#readInternal()} if the port is not closed.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_delegate() throws IOException {
		port.read();

		verify(port).readInternal();
	}

	// Utilities for this Testclass ///////////////////////////////////////////////////////////
	public static final class _AbstractSerialPort extends AbstractSerialPort {
		public _AbstractSerialPort(SerialPortHandle portHandle) {
			super(portHandle);
		}

		@Override
		protected void closeInternal() throws IOException {}

		@Override
		@Nonnull
		protected byte[] readInternal() throws IOException {
			return null;
		}

		@Override
		protected void writeInternal(@Nonnull byte[] data) throws IOException {}
	}
}
