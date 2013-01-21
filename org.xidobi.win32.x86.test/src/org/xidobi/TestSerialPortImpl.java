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

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.WAIT_OBJECT_0;

/**
 * Tests the class {@link SerialPortImpl}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestSerialPortImpl {

	/**
	 * 
	 */
	private static final int DUMMY_ERROR_CODE = 12345;

	/**
	 * 
	 */
	private static final int eventHandle = 1;

	/** a valid HANDLE value used in tests */
	private static final int handle = 2;

	private static final byte[] DATA = new byte[5];

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private WinApi win;

	@Mock
	private SerialPortHandle portHandle;

	/** the class under test */
	private SerialPortImpl port;

	@Before
	public void setUp() {
		initMocks(this);
		port = new SerialPortImpl(portHandle, win, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link WinApi} is
	 * <code>null</code>.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullOs() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >win< must not be null!");

		new SerialPortImpl(portHandle, null, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed
	 * {@link SerialPortHandle} is <code>null</code>.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullPortHandle() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >portHandle< must not be null!");

		new SerialPortImpl(null, win, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the handle is
	 * {@link WinApi#INVALID_HANDLE_VALUE} (-1).
	 * 
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_negativeHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >handle< is invalid! Invalid handle value");

		new SerialPortImpl(portHandle, win, INVALID_HANDLE_VALUE);
	}

	/**
	 * Simulates are write operation that completes immediatly without the need to wait for
	 * completion of the pendig operation..
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_succeedImmediatly() throws IOException {
		when(win.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		when(win.WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyINT(), anyOVERLAPPED())).thenReturn(true);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_OBJECT_0);
		when(win.GetOverlappedResult(eq(handle), anyOVERLAPPED(), anyINT(), eq(true))).thenReturn(true);

		port.write(DATA);

		verify(win).CreateEventA(0, true, false, null);
		verify(win).WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyINT(), anyOVERLAPPED());
		verify(win, never()).WaitForSingleObject(anyInt(), anyInt());
		verify(win, never()).GetOverlappedResult(anyInt(), anyOVERLAPPED(), anyINT(), Mockito.anyBoolean());
	}

	/**
	 * Simulates a pending write operation without errors and verifies that all relevant methods of
	 * the {@link WinApi} are called.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_succeedPending() throws IOException {
		when(win.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		when(win.WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyINT(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_OBJECT_0);
		when(win.GetOverlappedResult(eq(handle), anyOVERLAPPED(), anyINT(), eq(true))).thenReturn(true);

		port.write(DATA);

		verify(win).CreateEventA(0, true, false, null);
		verify(win).WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyINT(), anyOVERLAPPED());
		verify(win).WaitForSingleObject(eventHandle, 2000);
		verify(win).GetOverlappedResult(eq(handle), anyOVERLAPPED(), anyINT(), eq(true));
	}

	/**
	 * Verifies that an {@link NativeCodeException} is thrown when {@code CreateEventA} returned an
	 * unexpected handle value.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_createEventFail() throws IOException {
		when(win.CreateEventA(0, true, false, null)).thenReturn(0);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("CreateEventA returned unexpected with 0! (Error-Code: " + DUMMY_ERROR_CODE + ")");

		port.write(DATA);
	}

	/**
	 * Verifies that an {@link NativeCodeException} is thrown if the native
	 * {@link WinApi#WriteFile(int, byte[], int, INT, OVERLAPPED)} fails and is not pending.
	 */
	@Test
	public void write_writeFileFailsUnexpected() throws Exception {
		when(win.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		when(win.WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyINT(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WriteFile failed unexpected! (Error-Code: " + DUMMY_ERROR_CODE);
		port.write(DATA);
	}

	/**
	 * Verifies that an {@link NativeCodeException} is throw when in an pending operation a call to
	 * {@link WinApi#GetOverlappedResult(int, OVERLAPPED, INT, boolean)} fails.
	 */
	@Test
	public void write_getOverlappedResultFail() throws Exception {
		when(win.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		when(win.WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyINT(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_OBJECT_0);

		// This is the important part of the test, in the case the NativeCodeException must be
		// thrown
		when(win.GetOverlappedResult(eq(eventHandle), anyOVERLAPPED(), anyINT(), anyBoolean())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("WaitForSingleObject failed unexpected! (Error-Code: " + DUMMY_ERROR_CODE);

		port.write(DATA);
	}

	/**
	 * Verifies that an {@link NativeCodeException} is thrown when the number of transmitted bytes
	 * is not equal to the size of the {@code byte[]} to send.
	 */
	@Test
	public void write_unexpectedTransmittedBytes() throws Exception {
		when(win.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		when(win.WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyINT(), anyOVERLAPPED())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(ERROR_IO_PENDING);
		when(win.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_OBJECT_0);

		// This is the important part of the test, in the case the NativeCodeException must be
		// thrown
		when(win.GetOverlappedResult(eq(eventHandle), anyOVERLAPPED(), anyINT(), anyBoolean())).then(returnFalseAndSetWrittenBytes(DATA.length - 1));

	}

	/**
	 * Verifies that a call to {@link SerialPort#close()} frees the native resources.
	 */
	@Test
	public void close() throws Exception {
		port.close();
		verify(win).CloseHandle(handle);
		verifyNoMoreInteractions(win);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** matches any {@link OVERLAPPED} */
	private OVERLAPPED anyOVERLAPPED() {
		return any(OVERLAPPED.class);
	}

	/** matches any {@link INT} */
	private INT anyINT() {
		return any(INT.class);
	}

	/**
	 * This answer returns <code>false</code> and set the written bytes.
	 */
	private Answer<Boolean> returnFalseAndSetWrittenBytes(final int bytesWritten) {
		return new Answer<Boolean>() {

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				INT writtenBytes = (INT) invocation.getArguments()[3];
				writtenBytes.value = bytesWritten;
				return false;
			}
		};
	}

}
