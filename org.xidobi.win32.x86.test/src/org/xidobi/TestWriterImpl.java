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
package org.xidobi;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.ERROR_OPERATION_ABORTED;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.WinApi.WAIT_TIMEOUT;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.spi.NativeCodeException;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

/**
 * Tests the class {@link WriterImpl}
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class TestWriterImpl {

	/** Dummy size of a {@link OVERLAPPED} */
	private static final int OVERLAPPED_SIZE = 1;
	/** Dummy pointer to an {@link OVERLAPPED}-struct */
	private int PTR_OVERLAPPED = 1;
	/** Dummy size of a {@link DWORD} */
	private static final int DWORD_SIZE = 2;
	/** Dummy pointer to an {@link DWORD} */
	private int PTR_BYTES_TRANSFERRED = 2;

	/** some unspecific error code */
	private static final int DUMMY_ERROR_CODE = 12345;

	/** Dummy event handle */
	private static final int EVENT_HANDLE = 1;

	/** Dummy serial port handle */
	private static final int PORT_HANDLE = 2;

	/** some dummy data, that should be written */
	private static final byte[] DATA = new byte[5];

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private WinApi os;

	@Mock
	private SerialPort port;

	/** the class under test */
	private WriterImpl writer;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(os.sizeOf_OVERLAPPED()).thenReturn(OVERLAPPED_SIZE);
		when(os.malloc(OVERLAPPED_SIZE)).thenReturn(PTR_OVERLAPPED);

		when(os.sizeOf_DWORD()).thenReturn(DWORD_SIZE);
		when(os.malloc(DWORD_SIZE)).thenReturn(PTR_BYTES_TRANSFERRED);

		when(port.getPortName()).thenReturn("COM1");
		when(os.CloseHandle(anyInt())).thenReturn(true);
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.ResetEvent(EVENT_HANDLE)).thenReturn(true);
		writer = new WriterImpl(port, os, PORT_HANDLE);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link WinApi} is
	 * <code>null</code>.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullOs() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >os< must not be null!");

		new WriterImpl(port, null, PORT_HANDLE);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link SerialPort}
	 * is <code>null</code>.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullPortHandle() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >port< must not be null!");

		new WriterImpl(null, os, PORT_HANDLE);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the handle is
	 * {@link WinApi#INVALID_HANDLE_VALUE} (-1).
	 * 
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_negativeHandle() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >handle< is invalid! Invalid handle value");

		new WriterImpl(port, os, INVALID_HANDLE_VALUE);
	}

	/**
	 * Simulates are write operation that completes immediatly without the need to wait for
	 * completion of the pendig operation..
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_succeedImmediatly() throws IOException {
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(DATA.length);

		writer.write(DATA);

		verify(os, times(1)).WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED());
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WriteFile(...)</code>
	 * returns an unexpected number of transferred bytes.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WriteFileReturnsUnexpectedNumberOfBytes() throws IOException {
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(DATA.length - 1);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WriteFile returned an unexpected number of transferred bytes! Transferred: " + (DATA.length - 1) + ", expected: " + DATA.length);

		writer.write(DATA);

		verify(os, times(1)).WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED());
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WriteFile(...)</code> returns
	 * <code>false</code> and the last error code is not <code>ERROR_IO_PENDING</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WriteFileFailsWithERROR_INVALID_HANDLE() throws IOException {
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_INVALID_HANDLE);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the handle is invalid.");

		writer.write(DATA);
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WriteFile(...)</code> returns
	 * <code>false</code> and the last error code is <code>ERROR_OPERATION_ABORTED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WriteFileFailsWithERROR_OPERATION_ABORTED() throws IOException {
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_OPERATION_ABORTED);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation has been aborted.");

		writer.write(DATA);
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WriteFile(...)</code>
	 * returns <code>false</code> and the last error code is <code>ERROR_OPERATION_ABORTED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WriteFileFailsUnexpected() throws IOException {
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WriteFile failed unexpected!");

		writer.write(DATA);
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WriteFile(...)</code>
	 * returns <code>false</code> and the last error code is not <code>ERROR_IO_PENDING</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WriteFileFails() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WriteFile failed unexpected!");

		writer.write(DATA);
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when
	 * <code>WaitForSingleObject(...)</code> returns an undefined value.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WaitForSingleObjectReturnsUndefinedValue() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned unexpected value! Got: " + DUMMY_ERROR_CODE);

		writer.write(DATA);
	}

	/**
	 * Verifies that {@link SerialConnection#write(byte[])} returns normally, when all bytes are
	 * written.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_successfull() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(DATA.length);

		writer.write(DATA);
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when GetOverlappedResult(...)
	 * indicates that not all bytes are written.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_lessBytesWritten() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(DATA.length - 1);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("GetOverlappedResult returned an unexpected number of transferred bytes! Transferred: " + (DATA.length - 1) + ", expected: " + DATA.length);

		writer.write(DATA);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the
	 * <code>WaitForSingleObject(...)</code> indicates a time-out.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WaitForSingleObjectReturnsWAIT_TIMEOUT() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_TIMEOUT);

		exception.expect(IOException.class);
		exception.expectMessage("Write operation timed out after 2000 milliseconds!");

		writer.write(DATA);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code> and the last error
	 * code is <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WaitForSingleObjectFailsWithERROR_INVALID_HANDLE() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_INVALID_HANDLE);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_FAILED);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the handle is invalid.");

		writer.write(DATA);
	}

	/**
	 * Verifies that an {@link NativeCodeException} is thrown, when the
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WaitForSingleObjectFails() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_FAILED);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject failed unexpected!");

		writer.write(DATA);
	}

	/**
	 * Verifies that an {@link NativeCodeException} is thrown, when the
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_ABANDONED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_WaitForSingleObjectReturnsWAIT_ABANDONED() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_ABANDONED);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");

		writer.write(DATA);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the
	 * <code>GetOverlappedResult(...)</code> returns <code>false</code> and the last error is
	 * <code>ERROR_OPERATION_ABORTED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_GetOverlappedResultFailsWithERROR_OPERATION_ABORTED() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_OPERATION_ABORTED);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(false);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation has been aborted.");

		writer.write(DATA);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the
	 * <code>GetOverlappedResult(...)</code> returns <code>false</code> and the last error is
	 * <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_GetOverlappedResultFailsWithERROR_INVALID_HANDLE() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_INVALID_HANDLE);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(false);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the handle is invalid.");

		writer.write(DATA);
	}

	/**
	 * Verifies that an {@link NativeCodeException} is thrown, when the
	 * <code>GetOverlappedResult(...)</code> returns <code>false</code> and the last error is
	 * unexpected.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_GetOverlappedResultFailsUnexpected() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(EVENT_HANDLE);
		when(os.WriteFile(eq(PORT_HANDLE), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, DUMMY_ERROR_CODE);
		when(os.WaitForSingleObject(EVENT_HANDLE, 2000)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(false);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("GetOverlappedResult failed unexpected!");

		writer.write(DATA);
	}

	/**
	 * Verifies that a call to close() frees all handles.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close() throws Exception {
		writer.close();

		verify(os).CloseHandle(EVENT_HANDLE);
	}

	/**
	 * Verifies that a call to dispose() frees all resources.
	 * 
	 * @throws Exception
	 */
	@Test
	public void dispose() {
		writer.dispose();

		verify(os).free(PTR_BYTES_TRANSFERRED);
		verify(os).free(PTR_OVERLAPPED);
	}

	// Utilities for this Testclass ///////////////////////////////////////////////////////////

	/** matches any {@link OVERLAPPED} */
	private OVERLAPPED anyOVERLAPPED() {
		return any(OVERLAPPED.class);
	}

	/** matches any {@link INT} */
	private DWORD anyDWORD() {
		return any(DWORD.class);
	}
}
