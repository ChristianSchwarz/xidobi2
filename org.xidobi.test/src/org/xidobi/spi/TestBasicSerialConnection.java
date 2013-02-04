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

import static java.lang.Math.max;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
	@SuppressWarnings( { "resource", "unused" })
	public void new_nullPortPortHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >portHandle< must not be null!");

		new BasicSerialConnection(null,reader,writer);
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
	}

	/**
	 * Verifies that all calls to {@link SerialConnection#close()} will be delegate to
	 * {@link SerialConnection#close()} when all previous calls thrown an IOException and thus the
	 * port was not closed.
	 */
	@Test
	public void close_2xIOException() throws Exception {
		doThrow(IO_EXCEPTION).when(reader).close();
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

		verify(reader, times(2)).close();
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
	 * Verifies that {@link AbstractSerialConnection#writeInternal(byte[])} will not be call
	 * concurrent.
	 */
	@Test
	public void write_concurrentCalls() throws Exception {
		final int THREADS = 10;

		AtomicInteger maxNumberOfParallelThreads = new AtomicInteger();
		doAnswer(captureConcurrentThreads(maxNumberOfParallelThreads, THREADS)).when(writer).write(BYTES);
		ExecutorService ex = newFixedThreadPool(THREADS);
		for (int i = 0; i < THREADS; i++)
			ex.execute(writeBytes());

		ex.shutdown();
		assertThat(ex.awaitTermination(2, SECONDS), is(true));

		assertThat("Only one Thread at a time is allowed to call writeInternal(byte[])", maxNumberOfParallelThreads.get(), is(1));

	}

	/**
	 * Verifies that in case of an {@link IOException} the port will be closed
	 */
	@Test
	public void write_closePortOnIOException() throws Exception {
		doThrow(IO_EXCEPTION).when(writer).write(BYTES);
		try{
			port.write(BYTES);
			fail("expected an IOException");
		}catch(IOException ignore) {
		}
		verify(writer).close();
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
	 * Verifies that {@link AbstractSerialConnection#readInternal(byte[])} will not be call
	 * concurrent.
	 */
	@Test
	public void read_concurrentCalls() throws Exception {
		final int THREADS = 10;

		AtomicInteger maxNumberOfParallelThreads = new AtomicInteger();
		doAnswer(captureConcurrentThreads(maxNumberOfParallelThreads, THREADS)).when(reader).read();
		ExecutorService ex = newFixedThreadPool(THREADS);
		for (int i = 0; i < THREADS; i++)
			ex.execute(readBytes());

		ex.shutdown();
		assertThat(ex.awaitTermination(2, SECONDS), is(true));

		assertThat("Only one Thread at a time is allowed to call readInternal()", maxNumberOfParallelThreads.get(), is(1));
	}

	/**
	 * Verifies that in case of an {@link IOException} the port will be closed
	 */
	@Test
	public void read_closePortOnIOException() throws Exception {
		doThrow(IO_EXCEPTION).when(reader).read();
		try{
			port.read();
			fail("expected an IOException");
		}catch(IOException ignore) {
		}
		verify(reader).close();
		verify(writer).close();
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

	// Utilities for this Testclass ///////////////////////////////////////////////////////////
	

	/**
	 * Captures the max. number of Threads calling the method in parallel, during the given
	 * {@code captureDurationMs} Timespan.
	 * 
	 * @param maxConcurrentInvocations
	 *            used to set the the max. number of parallel invocations, the initial value must be
	 *            0
	 * @param captureDurationMs
	 *            milliseconds to capture
	 * @return <code>null</code>
	 */
	private Answer<Void> captureConcurrentThreads(final AtomicInteger maxConcurrentInvocations, final long captureDurationMs) {
		return new Answer<Void>() {
			private final Set<Thread> currentThreads = new HashSet<>();

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				currentThreads.add(currentThread());
				int concurrentThreads = currentThreads.size();
				int lastMax = maxConcurrentInvocations.get();
				maxConcurrentInvocations.set(max(lastMax, concurrentThreads));
				sleep(captureDurationMs);
				currentThreads.remove(currentThread());
				return null;
			}
		};
	}

	/** Returns a new {@link Runnable} that writes the {@link #BYTES} to the {@link #port}. */
	private Runnable writeBytes() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					port.write(BYTES);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

	/** Returns a new {@link Runnable} that read from the {@link #port}. */
	private Runnable readBytes() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					port.read();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
