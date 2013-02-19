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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.ERROR_ACCESS_DENIED;
import static org.xidobi.WinApi.ERROR_FILE_NOT_FOUND;
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.OPEN_EXISTING;
import static org.xidobi.WinApi.PURGE_RXABORT;
import static org.xidobi.WinApi.PURGE_RXCLEAR;
import static org.xidobi.WinApi.PURGE_TXABORT;
import static org.xidobi.WinApi.PURGE_TXCLEAR;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.spi.NativeCodeException;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.OVERLAPPED;

/**
 * Tests the class {@link SerialConnectionImpl}.
 * 
 * @author Tobias Breﬂler
 */
public class TestSerialConnectionImpl {

	/** dummy size of OVERLAPPED */
	private static final int OVERLAPPED_SIZE = 1;
	/** dummy size of DWORD */
	private static final int DWORD_SIZE = 2;
	/** a dummy handle to the event object */
	private static final int eventHandle = 1;

	private static final int DUMMY_ERROR_CODE = 1324;

	/** pointer to an {@link OVERLAPPED}-struct */
	private int ptrOverlapped = 1;
	/** pointer to an {@link DWORD} */
	private int ptrBytesTransferred = 2;
	private int ptrEvtMask = 3;

	/** Class under test */
	private SerialConnectionImpl serialConnectionImpl;

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private SerialPort port;
	@Mock
	private WinApi os;

	private int handle = 1534;
	private int terminationHandle = 13423;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(os.sizeOf_OVERLAPPED()).thenReturn(OVERLAPPED_SIZE);
		when(os.malloc(OVERLAPPED_SIZE)).thenReturn(ptrOverlapped);

		when(os.sizeOf_DWORD()).thenReturn(DWORD_SIZE);
		when(os.malloc(DWORD_SIZE)).thenReturn(ptrBytesTransferred, ptrEvtMask);

		when(port.getPortName()).thenReturn("COM1");
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);

		serialConnectionImpl = new SerialConnectionImpl(port, os, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings({ "resource", "unused" })
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullPort() {
		new SerialConnectionImpl(null, os, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings({ "unused", "resource" })
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullWinApi() {
		new SerialConnectionImpl(port, null, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when an invalid port handle is
	 * passed.
	 */
	@SuppressWarnings({ "unused", "resource" })
	@Test(expected = IllegalArgumentException.class)
	public void new_withInvalidHandle() {
		new SerialConnectionImpl(port, os, INVALID_HANDLE_VALUE);
	}

	/**
	 * Verifies that all resources are closed and disposed, when the serial connection is closed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_successfull() throws Exception {
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true);
		when(os.CloseHandle(eventHandle)).thenReturn(true);
		when(os.CloseHandle(handle)).thenReturn(true);

		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(terminationHandle);
		when(os.CloseHandle(terminationHandle)).thenReturn(true);

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
		}
	}

	/**
	 * Verifies that all resources are closed and disposed, when the serial connection is closed and
	 * <code>CancelIo</code> fails.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_CancelIoFails() throws Exception {
		when(os.CancelIo(handle)).thenReturn(false); /* fails! */
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true);
		when(os.CloseHandle(eventHandle)).thenReturn(true);
		when(os.CloseHandle(handle)).thenReturn(true);

		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(terminationHandle);
		when(os.CloseHandle(terminationHandle)).thenReturn(true);

		exception.expect(NativeCodeException.class);

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
		}
	}

	/**
	 * Verifies that all resources are closed and disposed, when the serial connection is closed and
	 * <code>PurgeComm</code> fails.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_PurgeCommFails() throws Exception {
		// @formatter:off
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(false); /* fails! */
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true);
		when(os.CloseHandle(eventHandle)).thenReturn(true);
		when(os.CloseHandle(handle)).thenReturn(true);

		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(terminationHandle);
		when(os.CloseHandle(terminationHandle)).thenReturn(true);
		//@formatter:on

		exception.expect(NativeCodeException.class);

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
		}
	}

	/**
	 * Verifies that all resources are closed and disposed, when the serial connection is closed and
	 * <code>SetCommMask</code> fails.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_SetCommMaskFails() throws Exception {
		// @formatter:off
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(false); /* fails! */
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		when(os.CloseHandle(eventHandle)).thenReturn(true);
		when(os.CloseHandle(handle)).thenReturn(true);

		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(terminationHandle);
		when(os.CloseHandle(terminationHandle)).thenReturn(true);
		//@formatter:on

		exception.expect(NativeCodeException.class);

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
		}
	}

	/**
	 * Verifies that all resources are closed and disposed, when the serial connection is closed and
	 * <code>CloseHandle</code> for the event handle fails.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_CloseEventHandleFails() throws Exception {
		// @formatter:off
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true); 
		when(os.CloseHandle(eventHandle)).thenReturn(false); /* fails! */
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		when(os.CloseHandle(handle)).thenReturn(true);
		
		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(terminationHandle);
		when(os.CloseHandle(terminationHandle)).thenReturn(true);
		//@formatter:on

		exception.expect(NativeCodeException.class);

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
		}
	}

	/**
	 * Verifies that all resources are closed and disposed, when the serial connection is closed and
	 * <code>CloseHandle</code> for the port handle fails.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_ClosePortHandleFails() throws Exception {
		// @formatter:off
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true); 
		when(os.CloseHandle(eventHandle)).thenReturn(true); 
		when(os.CloseHandle(handle)).thenReturn(false); /* fails! */
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		
		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(terminationHandle);
		when(os.CloseHandle(terminationHandle)).thenReturn(true);
		//@formatter:on

		exception.expect(NativeCodeException.class);

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
		}
	}

	/**
	 * Verifies that {@link SerialConnectionImpl#close()} awaits the termination of serial
	 * connection, when the port is busy.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_waitForTermination_portBusy() throws Exception {
		// @formatter:off
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true); 
		when(os.CloseHandle(eventHandle)).thenReturn(true); 
		when(os.CloseHandle(handle)).thenReturn(true); 
		
		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(INVALID_HANDLE_VALUE, terminationHandle);
		when(os.GetLastError()).thenReturn(ERROR_ACCESS_DENIED);
		when(os.CloseHandle(terminationHandle)).thenReturn(true);
		//@formatter:on

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
			verify(os, times(2)).CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);
			verify(os).CloseHandle(terminationHandle);
		}
	}

	/**
	 * Verifies that {@link SerialConnectionImpl#close()} awaits the termination of serial
	 * connection, when the port was not found.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_waitForTermination_portNotFound() throws Exception {
		// @formatter:off
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true); 
		when(os.CloseHandle(eventHandle)).thenReturn(true); 
		when(os.CloseHandle(handle)).thenReturn(true); 
		
		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(INVALID_HANDLE_VALUE);
		when(os.GetLastError()).thenReturn(ERROR_FILE_NOT_FOUND);
		//@formatter:on

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
			verify(os).CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);
		}
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>CreateFileA</code> fails
	 * with an unexpected error during the wait for termination.
	 * 
	 * @throws Exception
	 */
	@Test
	public void close_waitForTerminationFailedUnexpected() throws Exception {
		// @formatter:off
		when(os.CancelIo(handle)).thenReturn(true);
		when(os.PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR)).thenReturn(true);
		when(os.SetCommMask(handle, EV_RXCHAR)).thenReturn(true); 
		when(os.CloseHandle(eventHandle)).thenReturn(true); 
		when(os.CloseHandle(handle)).thenReturn(true); 
		
		when(os.CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)).thenReturn(INVALID_HANDLE_VALUE);
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);
		//@formatter:on

		exception.expect(NativeCodeException.class);
		exception.expectMessage("Couldn't wait for close termination! CreateFileA failed unexpected!");

		try {
			serialConnectionImpl.close();
		}
		finally {
			verifyClosePort();
			verify(os).CreateFileA("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);
		}
	}

	// //////////////////////

	private void verifyClosePort() {
		verify(os).CancelIo(handle);
		verify(os).PurgeComm(handle, PURGE_RXABORT | PURGE_RXCLEAR | PURGE_TXABORT | PURGE_TXCLEAR);
		verify(os).SetCommMask(handle, EV_RXCHAR);
		verify(os).CloseHandle(handle);
	}

}
