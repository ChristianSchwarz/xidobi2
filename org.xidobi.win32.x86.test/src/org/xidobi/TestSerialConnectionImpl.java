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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

/**
 * Tests the class {@link SerialConnectionImpl}
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
@SuppressWarnings("javadoc")
public class TestSerialConnectionImpl {

	private static final int OVERLAPPED_SIZE = 1;
	private static final int DWORD_SIZE = 2;

	private static final int DUMMY_ERROR_CODE = 12345;

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
	private SerialPort portHandle;

	/** the class under test */
	private SerialConnectionImpl port;

	/** pointer to an {@link OVERLAPPED}-struct */
	private int ptrOverlapped = 1;
	/** pointer to an {@link DWORD} */
	private int ptrDword = 2;

	/** pointer to an {@link NativeByteArray} */
	private int ptrNativeByteArray = 3;

	@Before
	public void setUp() {
		initMocks(this);

		port = new SerialConnectionImpl(portHandle, win, handle);

		when(win.sizeOf_OVERLAPPED()).thenReturn(OVERLAPPED_SIZE);
		when(win.sizeOf_DWORD()).thenReturn(DWORD_SIZE);

		when(win.malloc(OVERLAPPED_SIZE)).thenReturn(ptrOverlapped);
		when(win.malloc(DWORD_SIZE)).thenReturn(ptrDword);

		when(win.malloc(255)).thenReturn(ptrNativeByteArray);
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

		new SerialConnectionImpl(portHandle, null, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link SerialPort}
	 * is <code>null</code>.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullPortHandle() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >portHandle< must not be null!");

		new SerialConnectionImpl(null, win, handle);
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

		new SerialConnectionImpl(portHandle, win, INVALID_HANDLE_VALUE);
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>CreateEventA(...)</code>
	 * fails. In this case the method returns 0.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_CreateEventAReturns0() throws IOException {
		when(win.CreateEventA(0, true, false, null)).thenReturn(0);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("CreateEventA illegally returned 0!");

		try {
			port.write(DATA);
		}
		finally {
			verify(win, never()).CloseHandle(0);
			verify(win, times(1)).free(ptrOverlapped);
			verify(win, times(1)).free(ptrDword);
		}
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
		when(win.WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED())).thenReturn(true);

		port.write(DATA);

		verify(win, times(1)).WriteFile(eq(handle), eq(DATA), eq(DATA.length), anyDWORD(), anyOVERLAPPED());
		verify(win, times(1)).CloseHandle(eventHandle);
		verify(win, times(1)).free(ptrOverlapped);
		verify(win, times(1)).free(ptrDword);
	}

	/**
	 * Verifies that a call to {@link SerialConnection#close()} frees the native resources.
	 */
	@Test
	public void close() throws Exception {
		when(win.CloseHandle(handle)).thenReturn(true);

		port.close();

		verify(win).CloseHandle(handle);
		verifyNoMoreInteractions(win);
	}

	/**
	 * Verifies that a {@link NativeCodeException} is thrown, when <code>CloseHandle()</code> fails.
	 */
	@Test
	public void close_fails() throws Exception {
		when(win.CloseHandle(handle)).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("CloseHandle failed unexpected!\r\nError-Code " + DUMMY_ERROR_CODE);

		port.close();

		verify(win).CloseHandle(handle);
		verify(win).getPreservedError();
		verifyNoMoreInteractions(win);
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

	// /** This answer returns <code>returnValue</code> and set the written bytes. */
	// private Answer<Boolean> setWrittenBytesAndReturn(final int bytesWritten, final boolean
	// returnValue) {
	// return new Answer<Boolean>() {
	//
	// @Override
	// public Boolean answer(InvocationOnMock invocation) throws Throwable {
	// if (!"GetOverlappedResult".equals(invocation.getMethod().getName()))
	// throw new
	// IllegalStateException("This Answer can only be applied to method: GetOverlappedResult(..)");
	// INT writtenBytes = (INT) invocation.getArguments()[2];
	// writtenBytes.value = bytesWritten;
	// return returnValue;
	// }
	// };
	// }
	//
	// /** This answer returns <code>returnValue</code> and set the written bytes. */
	// private Answer<Boolean> setReadBytesAndReturn(final int bytesRead, final boolean returnValue)
	// {
	// return new Answer<Boolean>() {
	// @Override
	// public Boolean answer(InvocationOnMock invocation) throws Throwable {
	// if (!"GetOverlappedResult".equals(invocation.getMethod().getName()))
	// throw new
	// IllegalStateException("This Answer can only be applied to method: GetOverlappedResult(..)");
	// INT writtenBytes = (INT) invocation.getArguments()[2];
	// writtenBytes.value = bytesRead;
	// return returnValue;
	// }
	// };
	// }
	//
	// /** This answer returns <code>returnValue</code> and set the read bytes. */
	// private Answer<Boolean> readBytesAndReturn(final byte[] data, final boolean returnValue) {
	// return new Answer<Boolean>() {
	// @Override
	// public Boolean answer(InvocationOnMock invocation) throws Throwable {
	// if (!"ReadFile".equals(invocation.getMethod().getName()))
	// throw new IllegalStateException("This Answer can only be applied to method: ReadFile(..)");
	// INT countReadBytes = (INT) invocation.getArguments()[3];
	// countReadBytes.value = data.length;
	// return returnValue;
	// }
	// };
	// }

	/** Verifies that all allocated resources are freed. */
	private void verifyResourcesDisposed() {
		verify(win).CloseHandle(eventHandle);
		verify(win).free(ptrOverlapped);
	}
}
