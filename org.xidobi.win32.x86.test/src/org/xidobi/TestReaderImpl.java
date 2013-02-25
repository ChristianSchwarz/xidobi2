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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.ERROR_ACCESS_DENIED;
import static org.xidobi.WinApi.ERROR_BAD_COMMAND;
import static org.xidobi.WinApi.ERROR_GEN_FAILURE;
import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.ERROR_NOT_READY;
import static org.xidobi.WinApi.ERROR_OPERATION_ABORTED;
import static org.xidobi.WinApi.EV_RXCHAR;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xidobi.spi.NativeCodeException;
import org.xidobi.structs.COMSTAT;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

/**
 * Tests the class {@link ReaderImpl}.
 * 
 * @author Tobias Breﬂler
 */
public class TestReaderImpl {

	/** dummy size of OVERLAPPED */
	private static final int OVERLAPPED_SIZE = 1;
	/** pointer to an {@link OVERLAPPED}-struct */
	private int PTR_OVERLAPPED = 1;

	/** dummy size of DWORD */
	private static final int DWORD_SIZE = 2;

	/** pointer to an {@link DWORD}, that is used for the number of transferred bytes */
	private int PTR_BYTES_TRANSFERRED = 2;
	/** pointer to an {@link DWORD}, that is used for the event mask */
	private int PTR_EVT_MASK = 3;

	/** pointer to the native byte array */
	private static final int PTR_NATIVE_BYTE_ARRAY = 4;

	/** a dummy error code */
	private static final int DUMMY_ERROR_CODE = 12345;

	/** a dummy handle to the event object */
	private static final int DUMMY_EVENT_HANDLE = 1;
	/** a valid HANDLE value used in tests */
	private static final int DUMMY_PORT_HANDLE = 2;

	/** the dummy data that is read */
	private static final byte[] DATA = new byte[5];

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private WinApi os;

	@Mock
	private SerialPort port;

	/** the class under test */
	private ReaderImpl reader;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(os.malloc(DATA.length)).thenReturn(PTR_NATIVE_BYTE_ARRAY);

		when(os.sizeOf_OVERLAPPED()).thenReturn(OVERLAPPED_SIZE);
		when(os.malloc(OVERLAPPED_SIZE)).thenReturn(PTR_OVERLAPPED);

		when(os.sizeOf_DWORD()).thenReturn(DWORD_SIZE);
		when(os.malloc(DWORD_SIZE)).thenReturn(PTR_BYTES_TRANSFERRED, PTR_EVT_MASK);

		when(port.getPortName()).thenReturn("COM1");
		when(os.CloseHandle(anyInt())).thenReturn(true);
		when(os.CreateEventA(0, true, false, null)).thenReturn(DUMMY_EVENT_HANDLE);
		when(os.ResetEvent(DUMMY_EVENT_HANDLE)).thenReturn(true);

		reader = new ReaderImpl(port, os, DUMMY_PORT_HANDLE);
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> returns
	 * <code>false</code> and the last error is <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithERROR_INVALID_HANDLE() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_INVALID_HANDLE);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the handle is invalid.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> returns
	 * <code>false</code> and the last error is <code>ERROR_ACCESS_DENIED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithERROR_ACCESS_DENIED() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_ACCESS_DENIED);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because access denied.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> returns
	 * <code>false</code> and the last error is <code>ERROR_OPERATION_ABORTED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithERROR_OPERATION_ABORTED() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_OPERATION_ABORTED);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation has been aborted.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> returns
	 * <code>false</code> and the last error is <code>ERROR_GEN_FAILURE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithERROR_GEN_FAILURE() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_GEN_FAILURE);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because a device attached to the system is not functioning.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> returns
	 * <code>false</code> and the last error is <code>ERROR_BAD_COMMAND</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithERROR_BAD_COMMAND() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_BAD_COMMAND);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device doesn't recognize the command.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> returns
	 * <code>false</code> and the last error is <code>ERROR_NOT_READY</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithERROR_NOT_READY() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_NOT_READY);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device is not ready.");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WaitCommEvent(...)</code>
	 * returns <code>false</code> and the last error is not <code>ERROR_IO_PENDING</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithUnexpectedErrorCode() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitCommEvent failed unexpected!");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WaitCommEvent(...)</code>
	 * is successfull, but the event mask is not <code>EV_RXCHAR</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventForUnexpectedEvent() throws IOException {
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR + 1);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitCommEvt was signaled for unexpected event!");

		reader.read();
	}

	/**
	 * Verifies that the available data is read. The first time when <code>WaitCommEvent(...)</code>
	 * is called, the operation is pending and <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_TIMEOUT</code>. The second time it returns immediatly and the data can be read
	 * successfull.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingReturnsWAIT_TIMEOUT() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_TIMEOUT, WAIT_OBJECT_0);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length);
			doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
		// @formatter:on

		byte[] result = reader.read();

		assertThat(result, is(DATA));
	}

	/**
	 * Verifies that the available data is read, when <code>WaitCommEvent(...)</code> is called, the
	 * operation is pending and <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_OBJECT_0</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingReturnsWAIT_OBJECT_0() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_OBJECT_0);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
		// @formatter:on

		byte[] result = reader.read();

		assertThat(result, is(DATA));
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WaitCommEvent(...)</code>
	 * is called, the operation is pending and <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_ABANDONED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingReturnsWAIT_ABANDONED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_ABANDONED);
		// @formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> is
	 * called, the operation is pending, <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_FAILED</code> and the last error code is <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingFailedWithERROR_INVALID_HANDLE() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_INVALID_HANDLE);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		// @formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the handle is invalid.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> is
	 * called, the operation is pending, <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_FAILED</code> and the last error code is <code>ERROR_ACCESS_DENIED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingFailedWithERROR_ACCESS_DENIED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_ACCESS_DENIED);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		// @formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because access denied.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> is
	 * called, the operation is pending, <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_FAILED</code> and the last error code is <code>ERROR_GEN_FAILURE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingFailedWithERROR_GEN_FAILURE() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_GEN_FAILURE);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		// @formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because a device attached to the system is not functioning.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> is
	 * called, the operation is pending, <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_FAILED</code> and the last error code is <code>ERROR_BAD_COMMAND</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingFailedWithERROR_BAD_COMMAND() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_BAD_COMMAND);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		// @formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device doesn't recognize the command.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> is
	 * called, the operation is pending, <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_FAILED</code> and the last error code is <code>ERROR_NOT_READY</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingFailedWithERROR_NOT_READY() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_NOT_READY);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		// @formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device is not ready.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> is
	 * called, the operation is pending, <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_FAILED</code> and the last error code is <code>ERROR_OPERATION_ABORTED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingFailedWithERROR_OPERATION_ABORTED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_OPERATION_ABORTED);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		// @formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation has been aborted.");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WaitCommEvent(...)</code>
	 * is called, the operation is pending, <code>WaitForSingleObject(...)</code> returns
	 * <code>WAIT_FAILED</code> and the last error code is unexpected.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingFailedUnexpected() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, DUMMY_ERROR_CODE);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		// @formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject failed unexpected!");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WaitCommEvent(...)</code>
	 * is called, the operation is pending, <code>WaitForSingleObject(...)</code> returns unexpected
	 * value.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingWaitForSingleObjectFailedUnexpected() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, DUMMY_ERROR_CODE);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(DUMMY_ERROR_CODE);
		// @formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned unexpected value! Got: " + DUMMY_ERROR_CODE);

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ClearCommError(...)</code>
	 * returns <code>false</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ClearCommErrorFailed() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(10, false)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("ClearCommError failed unexpected!");

		reader.read();
	}

	/**
	 * Verifies that the available data is read, when <code>ReadFile(...)</code> returns successfull
	 * immediatly.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileSuccessfull() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
		//@formatter:on

		byte[] result = reader.read();

		assertThat(result, is(DATA));
	}

	/**
	 * Verifies that the available data is read, when <code>ClearCommError(...)</code> indicates
	 * that 0 bytes available at the first invocation. Only at the second call it indicates a number
	 * of bytes greater than 0.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_withNoDataAvailable() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		when(os.ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT())).
			then(withAvailableBytes(0, true)).
			then(withAvailableBytes(DATA.length, true));
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
		//@formatter:on

		byte[] result = reader.read();

		assertThat(result, is(DATA));
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ReadFile(...)</code>
	 * returns <code>false</code> and the last error code is not <code>ERROR_IO_PENDING</code> or
	 * <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileFailsUnexpected() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("ReadFile failed unexpected!");

		reader.read();
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when <code>ReadFile(...)</code> returns
	 * <code>false</code> and the last error code is <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileFailsERROR_INVALID_HANDLE() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_INVALID_HANDLE);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the handle is invalid.");

		reader.read();
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when <code>ReadFile(...)</code> returns
	 * <code>false</code> and the last error code is <code>ERROR_ACCESS_DENIED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileFailsERROR_ACCESS_DENIED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_ACCESS_DENIED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because access denied.");

		reader.read();
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when <code>ReadFile(...)</code> returns
	 * <code>false</code> and the last error code is <code>ERROR_GEN_FAILURE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileFailsERROR_GEN_FAILURE() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_GEN_FAILURE);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because a device attached to the system is not functioning.");

		reader.read();
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when <code>ReadFile(...)</code> returns
	 * <code>false</code> and the last error code is <code>ERROR_BAD_COMMAND</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileFailsERROR_BAD_COMMAND() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_BAD_COMMAND);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device doesn't recognize the command.");

		reader.read();
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when <code>ReadFile(...)</code> returns
	 * <code>false</code> and the last error code is <code>ERROR_NOT_READY</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileFailsERROR_NOT_READY() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_NOT_READY);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device is not ready.");

		reader.read();
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when <code>ReadFile(...)</code> returns
	 * <code>false</code> and the last error code is <code>ERROR_OPERATION_ABORTED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFileFailsERROR_OPERATION_ABORTED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_OPERATION_ABORTED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation has been aborted.");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ReadFile(...)</code> is
	 * pending and <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingWaitFails() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject failed unexpected!");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>ReadFile(...)</code> is pending and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code> and the last error is
	 * <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingWaitFailsWithERROR_INVALID_HANDLE() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_INVALID_HANDLE);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the handle is invalid.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>ReadFile(...)</code> is pending and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code> and the last error is
	 * <code>ERROR_ACCESS_DENIED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingWaitFailsWithERROR_ACCESS_DENIED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_ACCESS_DENIED);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because access denied.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>ReadFile(...)</code> is pending and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code> and the last error is
	 * <code>ERROR_GEN_FAILURE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingWaitFailsWithERROR_GEN_FAILURE() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_GEN_FAILURE);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because a device attached to the system is not functioning.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>ReadFile(...)</code> is pending and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code> and the last error is
	 * <code>ERROR_BAD_COMMAND</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingWaitFailsWithERROR_BAD_COMMAND() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_BAD_COMMAND);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device doesn't recognize the command.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>ReadFile(...)</code> is pending and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code> and the last error is
	 * <code>ERROR_NOT_READY</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingWaitFailsWithERROR_NOT_READY() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_NOT_READY);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation failed, because the device is not ready.");

		reader.read();
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>ReadFile(...)</code> is pending and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code> and the last error is
	 * <code>ERROR_OPERATION_ABORTED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingWaitFailsWithERROR_OPERATION_ABORTED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, ERROR_OPERATION_ABORTED);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! I/O operation has been aborted.");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ReadFile(...)</code> is
	 * pending and <code>WaitForSingleObject(...)</code> returns <code>WAIT_ABANDONED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingReturnsWAIT_ABANDONED() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_ABANDONED);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ReadFile(...)</code> is
	 * pending and <code>WaitForSingleObject(...)</code> returns an unexpected value.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingReturnsUnexpectedValue() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(123);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned unexpected value! Got: 123");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ReadFile(...)</code> is
	 * pending and <code>WaitForSingleObject(...)</code> returns <code>WAIT_TIMEOUT</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingReturnsWAIT_TIMEOUT() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_TIMEOUT);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("ReadFile timed out after 100 milliseconds!");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when
	 * <code>GetOverlappedResult(...)</code> fails.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_GetOverlappedResultFails() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING, 
		                                         DUMMY_ERROR_CODE);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(DUMMY_PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(false);
		// @formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("GetOverlappedResult failed unexpected!\r\nError-Code " + DUMMY_ERROR_CODE);

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when
	 * <code>GetOverlappedResult(...)</code> returns <code>true</code>, but signals an unexpected
	 * number of bytes.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingReturnsUnexpectedNumberOfBytes() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length - 2);
		doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(DUMMY_PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(true);
		// @formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("GetOverlappedResult returned an unexpected number of read bytes! Read: " + (DATA.length - 2) + ", expected: " + DATA.length);

		reader.read();
	}

	/**
	 * Verifies that the available data is read, when <code>ReadFile(...)</code> is pending and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_OBJECT_0</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingSuccess() throws IOException {
		//@formatter:off
		when(os.WaitCommEvent(eq(DUMMY_PORT_HANDLE), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(os.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length);
			doAnswer(withAvailableBytes(DATA.length, true)).when(os).ClearCommError(eq(DUMMY_PORT_HANDLE), anyINT(), anyCOMSTAT());
		when(os.ReadFile(eq(DUMMY_PORT_HANDLE), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(os.GetLastError()).thenReturn(ERROR_IO_PENDING);
		when(os.WaitForSingleObject(DUMMY_EVENT_HANDLE, 100)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(DUMMY_PORT_HANDLE), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(true);
		when(os.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
		// @formatter:on

		byte[] result = reader.read();

		assertThat(result, is(DATA));
	}

	/**
	 * Verifies that all handles are disposed, when the reader is closed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close() throws Exception {

		reader.close();

		verify(os).CloseHandle(DUMMY_EVENT_HANDLE);
	}

	/**
	 * Verifies that all resources are disposed, when the reader is disposed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void dispose() throws Exception {

		reader.dispose();

		verify(os).free(PTR_BYTES_TRANSFERRED);
		verify(os).free(PTR_OVERLAPPED);
		verify(os).free(PTR_EVT_MASK);
	}

	// Utilities for this Testclass ///////////////////////////////////////////////////////////

	/** matches any {@link OVERLAPPED} */
	private OVERLAPPED anyOVERLAPPED() {
		return any(OVERLAPPED.class);
	}

	/** matches any {@link DWORD} */
	private DWORD anyDWORD() {
		return any(DWORD.class);
	}

	/** matches any {@link DWORD} */
	private INT anyINT() {
		return any(INT.class);
	}

	/** matches any {@link COMSTAT} */
	private COMSTAT anyCOMSTAT() {
		return any(COMSTAT.class);
	}

	/**
	 * Returns an {@link Answer} that sets the <code>availableBytes</code> on the
	 * <code>COMSTAT</code> parameter and returns the given <code>returnValue</code>.
	 */
	private Answer<Boolean> withAvailableBytes(final int availableByte, final boolean returnValue) {
		return new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				COMSTAT comstat = (COMSTAT) invocation.getArguments()[2];
				comstat.cbInQue = availableByte;
				return returnValue;
			}
		};
	}

}
