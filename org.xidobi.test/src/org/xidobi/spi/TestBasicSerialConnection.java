/*
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xidobi.spi;

import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;

/**
 * Tests the class {@link BasicSerialConnection}
 * 
 * @author Christian Schwarz
 */
@SuppressWarnings("javadoc")
public class TestBasicSerialConnection {

	/** constant for better readability */
	private static final IOException IO_EXCEPTION = new IOException();
	private static final NativeCodeException NATIVE_CODE_EXCEPTION = new NativeCodeException("exception");

	private static final byte[] BYTES = {};

	/** the class under test */
	@InjectMocks
	private BasicSerialConnection port;

	@Mock
	private SerialPort portHandle;

	@Mock
	private Reader reader;

	@Mock
	private Writer writer;

	/** needed to verify exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		initMocks(this);

		when(portHandle.getPortName()).thenReturn("COM1");
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when <code>null</code> is passed
	 * to the constructor.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullPortPortHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >port< must not be null!");

		new BasicSerialConnection(null, reader, writer);
	}

	/**
	 * Verifies that a call to {@link SerialConnection#close()} will be deleagted to
	 * {@link AbstractSerialConnection#closeInternal()}.
	 * 
	 * @throws IOException
	 */

	@Test
	public void close() throws IOException {

		port.close();

		verify(reader).close();
		verify(writer).close();
		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that only the first call to {@link SerialConnection#close()} will be deleagted to
	 * {@link AbstractSerialConnection#closeInternal()}.
	 */
	@Test
	public void close_2x() throws Exception {
		port.close();

		port.close();

		verify(reader).close();
		verify(writer).close();
		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that the same {@link IOException} thrown by
	 * {@link AbstractSerialConnection#closeInternal()} is forwarded to the caller of
	 * {@link SerialConnection#close()} without modification.
	 */
	@Test
	public void close_IOException() throws Exception {
		exception.expect(is(IO_EXCEPTION));

		doThrow(IO_EXCEPTION).when(reader).close();
		port.close();

		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that {@link AbstractSerialConnection#isClosed()} returns <code>false</code>, when
	 * the port is not closed.
	 */
	@Test
	public void isClosed_whenOpen() {
		assertThat(port.isClosed(), is(false));
	}

	/**
	 * Verifies that {@link AbstractSerialConnection#isClosed()} returns <code>true</code>, when the
	 * port is closed.
	 */
	@Test
	public void isClosed_whenClosed() throws Exception {
		port.close();
		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that an {@link IOException} is thrown when the port is closed.
	 */
	@Test
	public void write_toClosedPort() throws Exception {
		when(portHandle.getPortName()).thenReturn("COM1");
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
	 * Verifies that {@link SerialConnection#write(byte[])} deleagtes to
	 * {@link AbstractSerialConnection#writeInternal(byte[])} if <code>data!=null</code> and the
	 * port is not closed.
	 */
	@Test
	public void write_delegate() throws Exception {
		port.write(BYTES);
		verify(writer).write(BYTES);
	}

	/**
	 * Verifies that in case of an {@link IOException} the port will be closed
	 */
	@Test
	public void write_closePortOnIOException() throws Exception {
		doThrow(IO_EXCEPTION).when(writer).write(BYTES);

		try {
			port.write(BYTES);
			fail("expected an IOException");
		}
		catch (IOException ignore) {}

		verify(writer).close();
		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that in case of an {@link NativeCodeException} the port will be closed
	 */
	@Test
	public void write_closePortOnNativeCodeException() throws Exception {
		doThrow(NATIVE_CODE_EXCEPTION).when(writer).write(BYTES);

		try {
			port.write(BYTES);
			fail("expected an NativeCodeException");
		}
		catch (NativeCodeException ignore) {}

		verify(writer).close();
		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that an {@link IOException} is thrown when the port is closed.
	 */
	@Test
	public void read_portIsClosed() throws Exception {
		when(portHandle.getPortName()).thenReturn("COM1");
		port.close();

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed!");

		port.read();
	}

	/**
	 * Verifies that {@link SerialConnection#read()} is delegated to
	 * {@link AbstractSerialConnection#readInternal()} if the port is not closed.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_delegate() throws IOException {
		port.read();

		verify(reader).read();
	}

	/**
	 * Verifies that in case of an {@link IOException} the port will be closed
	 */
	@Test
	public void read_closePortOnIOException() throws Exception {
		doThrow(IO_EXCEPTION).when(reader).read();

		try {
			port.read();
			fail("expected an IOException");
		}
		catch (IOException ignore) {}

		verify(reader).close();
		verify(writer).close();
		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that in case of an {@link NativeCodeException} the port will be closed
	 */
	@Test
	public void read_closePortOnNativeCloseException() throws Exception {
		doThrow(NATIVE_CODE_EXCEPTION).when(reader).read();

		try {
			port.read();
			fail("expected an NativeCodeException");
		}
		catch (NativeCodeException ignore) {}

		verify(reader).close();
		verify(writer).close();
		assertThat(port.isClosed(), is(true));
	}

	/**
	 * Verifies that {@link AbstractSerialConnection#portClosedException()} returns an
	 * {@link IOException} with a message 'Port ??? is closed!'.
	 */
	@Test
	public void portClosedException() {
		IOException result = port.portClosedException();

		assertThat(result, is(notNullValue()));
		assertThat(result.getMessage(), is("Port COM1 is closed!"));
	}

	/**
	 * Verifies that {@link AbstractSerialConnection#portClosedException(String)} returns an
	 * {@link IOException} with a message 'Port ??? is closed!' and an additional message.
	 */
	@Test
	public void portClosedException_withMessage() {
		IOException result = port.portClosedException("Additional message.");

		assertThat(result, is(notNullValue()));
		assertThat(result.getMessage(), is("Port COM1 is closed! Additional message."));
	}

}
