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
import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
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
	/** dummy size of DWORD */
	private static final int DWORD_SIZE = 2;
	/** a dummy error code */
	private static final int DUMMY_ERROR_CODE = 12345;
	/** a dummy handle to the event object */
	private static final int eventHandle = 1;
	/** a valid HANDLE value used in tests */
	private static final int portHandle = 2;
	/** the dummy data that is read */
	private static final byte[] DATA = new byte[5];

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private WinApi win;

	@Mock
	private SerialPort port;

	/** the class under test */
	private ReaderImpl reader;

	/** pointer to an {@link OVERLAPPED}-struct */
	private int ptrOverlapped = 1;
	/** pointer to an {@link DWORD} */
	private int ptrBytesTransferred = 2;
	private int ptrEvtMask = 3;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(win.sizeOf_OVERLAPPED()).thenReturn(OVERLAPPED_SIZE);
		when(win.malloc(OVERLAPPED_SIZE)).thenReturn(ptrOverlapped);

		when(win.sizeOf_DWORD()).thenReturn(DWORD_SIZE);
		when(win.malloc(DWORD_SIZE)).thenReturn(ptrBytesTransferred, ptrEvtMask);

		when(port.getPortName()).thenReturn("COM1");
		when(win.CloseHandle(anyInt())).thenReturn(true);
		when(win.CreateEventA(0, true, false, null)).thenReturn(eventHandle);

		reader = new ReaderImpl(port, win, portHandle);
	}

	/**
	 * Verifies that a {@link IOException} is thrown, when <code>WaitCommEvent(...)</code> returns
	 * <code>false</code> and the last error is <code>ERROR_INVALID_HANDLE</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventFailsWithERROR_INVALID_HANDLE() throws IOException {
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_INVALID_HANDLE);

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! Read operation failed, because the handle is invalid!");

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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR + 1);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitCommEvt was signaled for unexpected event!");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WaitCommEvent(...)</code>
	 * returns <code>false</code>, the last error is <code>ERROR_IO_PENDING</code> and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingReturnsWAIT_FAILED() throws IOException {
		//@formatter:off
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING, 
		                                         DUMMY_ERROR_CODE);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned an unexpected value: WAIT_FAILED!");

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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(false, true);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_TIMEOUT);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length);
			doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_OBJECT_0);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
		// @formatter:on

		byte[] result = reader.read();

		assertThat(result, is(DATA));
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>WaitCommEvent(...)</code>
	 * returns <code>false</code>, the last error is <code>ERROR_IO_PENDING</code> and
	 * <code>WaitForSingleObject(...)</code> returns <code>WAIT_ABANDONED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_WaitCommEventPendingReturnsWAIT_ABANDONED() throws IOException {
		//@formatter:off
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING, 
		                                         DUMMY_ERROR_CODE);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_ABANDONED);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");

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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(10, false)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("ClearCommError failed unexpected!");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ClearCommError(...)</code>
	 * returns <code>true</code>, but signals that 0 bytes are available.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ClearCommErrorSignals0BytesAvailable() throws IOException {
		//@formatter:off
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(0, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("Arrival of data was signaled, but number of available bytes is 0!");

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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);
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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_INVALID_HANDLE);
		//@formatter:on

		exception.expect(IOException.class);
		exception.expectMessage("Port COM1 is closed! Read operation failed, because the handle is invalid!");

		reader.read();
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>ReadFile(...)</code> is
	 * pending and <code>WaitForSingleObject(...)</code> returns <code>WAIT_FAILED</code>.
	 * 
	 * @throws IOException
	 */
	@Test
	public void read_ReadFilePendingReturnsWAIT_FAILED() throws IOException {
		//@formatter:off
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 100)).thenReturn(WAIT_FAILED);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned an unexpected value: WAIT_FAILED!");

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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 100)).thenReturn(WAIT_ABANDONED);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");

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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 100)).thenReturn(WAIT_TIMEOUT);
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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING, 
		                                         DUMMY_ERROR_CODE);
		when(win.WaitForSingleObject(eventHandle, 100)).thenReturn(WAIT_OBJECT_0);
		when(win.GetOverlappedResult(eq(portHandle), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(false);
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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length - 2);
		doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 100)).thenReturn(WAIT_OBJECT_0);
		when(win.GetOverlappedResult(eq(portHandle), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(true);
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
		when(win.WaitCommEvent(eq(portHandle), anyDWORD(), anyOVERLAPPED())).thenReturn(true);
		when(win.getValue_DWORD(anyDWORD())).thenReturn(EV_RXCHAR, 
		                                                DATA.length);
			doAnswer(withAvailableBytes(DATA.length, true)).when(win).ClearCommError(eq(portHandle), anyINT(), anyCOMSTAT());
		when(win.ReadFile(eq(portHandle), any(NativeByteArray.class), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 100)).thenReturn(WAIT_OBJECT_0);
		when(win.GetOverlappedResult(eq(portHandle), anyOVERLAPPED(), anyDWORD(), eq(true))).thenReturn(true);
		when(win.getByteArray(any(NativeByteArray.class), eq(DATA.length))).thenReturn(DATA);
		// @formatter:on

		byte[] result = reader.read();

		assertThat(result, is(DATA));
	}

	/**
	 * Verifies that all resources are disposed, when the reader is closed.
	 */
	@Test
	public void close() {

		reader.close();

		verify(win).free(ptrBytesTransferred);
		verify(win).free(ptrEvtMask);
		verify(win).free(ptrOverlapped);
		verify(win).CloseHandle(eventHandle);

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
