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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;

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
 * Tests the class {@link ReaderImpl}.
 * 
 * @author Tobias Breﬂler
 */
public class TestReaderImpl {

	private static final int OVERLAPPED_SIZE = 1;
	private static final int DWORD_SIZE = 2;

	private static final int DUMMY_ERROR_CODE = 12345;

	private static final int eventHandle = 1;

	/** a valid HANDLE value used in tests */
	private static final int portHandle = 2;

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

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(win.sizeOf_OVERLAPPED()).thenReturn(OVERLAPPED_SIZE);
		when(win.malloc(OVERLAPPED_SIZE)).thenReturn(ptrOverlapped);

		when(win.sizeOf_DWORD()).thenReturn(DWORD_SIZE);
		when(win.malloc(DWORD_SIZE)).thenReturn(ptrBytesTransferred);

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

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** matches any {@link OVERLAPPED} */
	private OVERLAPPED anyOVERLAPPED() {
		return any(OVERLAPPED.class);
	}

	/** matches any {@link INT} */
	private DWORD anyDWORD() {
		return any(DWORD.class);
	}

}
