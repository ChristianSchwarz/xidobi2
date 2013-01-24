/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.01.2013 17:04:21
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

import static java.lang.Math.max;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
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

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortHandle;

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

	@Mock
	private AbstractPart abstr;

	/** needed to verify exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		initMocks(this);
		port = new _AbstractSerialPort(portHandle);
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
		verify(abstr).closeInternal();
	}

	/**
	 * Verifies that only the first call to {@link SerialPort#close()} will be deleagted to
	 * {@link AbstractSerialPort#closeInternal()}.
	 */
	@Test
	public void close_2x() throws Exception {
		port.close();
		port.close();
		verify(abstr).closeInternal();
	}

	/**
	 * Verifies that the same {@link IOException} thrown by
	 * {@link AbstractSerialPort#closeInternal()} is forwarded to the caller of
	 * {@link SerialPort#close()} without modification.
	 */
	@Test
	public void close_IOException() throws Exception {
		exception.expect(is(IO_EXCEPTION));

		doThrow(IO_EXCEPTION).when(abstr).closeInternal();
		port.close();
	}

	/**
	 * Verifies that all calls to {@link SerialPort#close()} will be delegate to
	 * {@link SerialPort#close()} when all previous calls thrown an IOException and thus the port
	 * was not closed.
	 */
	@Test
	public void close_2xIOException() throws Exception {
		doThrow(IO_EXCEPTION).when(abstr).closeInternal();
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

		verify(abstr, times(2)).closeInternal();

	}

	/**
	 * Verifies that an {@link IOException} is thrown when the port is closed.
	 */
	@Test
	public void write_portIsClosed() throws Exception {
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
	 * Verifies that {@link SerialPort#write(byte[])} deleagtes to
	 * {@link AbstractSerialPort#writeInternal(byte[])} if <code>data!=null</code> and the port is
	 * not closed.
	 */
	@Test
	public void write_delegate() throws Exception {
		port.write(BYTES);
		verify(abstr).writeInternal(BYTES);
	}

	/**
	 * Verifies that {@link AbstractSerialPort#writeInternal(byte[])} will not be call concurrent.
	 */
	@Test
	public void write_concurrentCalls() throws Exception {
		final int THREADS = 10;

		AtomicInteger maxNumberOfParallelThreads = new AtomicInteger();
		doAnswer(captureConcurrentThreads(maxNumberOfParallelThreads, THREADS)).when(abstr).writeInternal(BYTES);
		ExecutorService ex = newFixedThreadPool(THREADS);
		for (int i = 0; i < THREADS; i++)
			ex.execute(writeBytes());

		ex.shutdown();
		assertThat(ex.awaitTermination(2, SECONDS), is(true));

		assertThat("Only one Thread at a time is allowed to call writeInternal(byte[])", maxNumberOfParallelThreads.get(), is(1));

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

		verify(abstr).readInternal();
	}
	
	/**
	 * Verifies that {@link AbstractSerialPort#readInternal(byte[])} will not be call concurrent.
	 */
	@Test
	public void read_concurrentCalls() throws Exception {
		final int THREADS = 10;

		AtomicInteger maxNumberOfParallelThreads = new AtomicInteger();
		doAnswer(captureConcurrentThreads(maxNumberOfParallelThreads, THREADS)).when(abstr).readInternal();
		ExecutorService ex = newFixedThreadPool(THREADS);
		for (int i = 0; i < THREADS; i++)
			ex.execute(readBytes());

		ex.shutdown();
		assertThat(ex.awaitTermination(2, SECONDS), is(true));

		assertThat("Only one Thread at a time is allowed to call readInternal()", maxNumberOfParallelThreads.get(), is(1));

	}

	// Utilities for this Testclass ///////////////////////////////////////////////////////////
	public final class _AbstractSerialPort extends AbstractSerialPort {
		public _AbstractSerialPort(SerialPortHandle portHandle) {
			super(portHandle);
		}

		@Override
		protected void closeInternal() throws IOException {
			abstr.closeInternal();
		}

		@Override
		@Nonnull
		protected byte[] readInternal() throws IOException {
			return abstr.readInternal();
		}

		@Override
		protected void writeInternal(@Nonnull byte[] data) throws IOException {
			abstr.writeInternal(data);
		}
	}

	public interface AbstractPart {

		void closeInternal() throws IOException;

		byte[] readInternal() throws IOException;

		void writeInternal(@Nonnull byte[] data) throws IOException;
	}

	/**
	 * Captures the max. number of Threads calling the method in parallel, during the given
	 * {@code captureDurationMs} Timespan.
	 * 
	 * @param maxConcurrentInvocations used to set the the max. number of parallel invocations, the initial value must be 0
	 * @param captureDurationMs milliseconds to capture 
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
