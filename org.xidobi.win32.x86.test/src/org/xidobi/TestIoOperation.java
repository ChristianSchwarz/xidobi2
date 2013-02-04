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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.spi.NativeCodeException;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.OVERLAPPED;

/**
 * Test for {@link IoOperation}
 * 
 * @author Christian Schwarz
 */
public class TestIoOperation {

	private static final int CREATE_EVENT_ERROR = 0;

	private static final int PORT_HANDLE = 123;

	private static final int OVERLAPPED_SIZE = 1;
	private static final int DWORD_SIZE = 2;
	/** pointer to an {@link OVERLAPPED}-struct */
	private int ptrOverlapped = 1;
	/** pointer to an {@link DWORD} */
	private int ptrBytesTransferred = 2;

	private final int eventHandle = 1;

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */

	private IoOperation operation;

	@Mock
	private SerialPort port;

	@Mock
	private WinApi os;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(os.sizeOf_OVERLAPPED()).thenReturn(OVERLAPPED_SIZE);
		when(os.malloc(OVERLAPPED_SIZE)).thenReturn(ptrOverlapped);

		when(os.sizeOf_DWORD()).thenReturn(DWORD_SIZE);
		when(os.malloc(DWORD_SIZE)).thenReturn(ptrBytesTransferred);

		when(port.getPortName()).thenReturn("COM1");
	}

	/**
	 * Verfies that an {@link IllegalArgumentException} is thrown if the {@link SerialPort} is
	 * <code>null</code>
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullPort() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >port< must not be null!");

		new _IoOperation(null, os, PORT_HANDLE);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown if the {@link WinApi} is
	 * <code>null</code>.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_nullOs() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >os< must not be null!");

		new _IoOperation(port, null, PORT_HANDLE);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown if an invalid handle is passed.
	 */
	@Test
	@SuppressWarnings({ "resource", "unused" })
	public void new_InvalidHandle() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >handle< is invalid!");

		new _IoOperation(port, os, INVALID_HANDLE_VALUE);
	}

	/**
	 * Verifies that an {@link NativeCodeException} is thrown if the creation of the event handle
	 * fails.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_createEvent_fails() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(CREATE_EVENT_ERROR);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("Create overlapped event failed!");

		try {
			new _IoOperation(port, os, PORT_HANDLE);
		}
		finally {
			verify(os).free(ptrOverlapped);
		}
	}

	/**
	 * Verifies that all resource are freed that were allocated in the construction
	 */
	@Test
	public void close() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		operation = new _IoOperation(port, os, PORT_HANDLE);

		operation.close();
		verify(os).CloseHandle(eventHandle);
		verify(os).free(ptrOverlapped);
		verify(os).free(ptrBytesTransferred);
	}

	/**
	 * Verifies that all resource are freed
	 */
	@Test()
	public void close_closeHandle_fails() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		operation = new _IoOperation(port, os, PORT_HANDLE);

		when(os.CloseHandle(eventHandle)).thenThrow(new RuntimeException());
		try {
			operation.close();
		}
		catch (RuntimeException ignore) {}
		verify(os).CloseHandle(eventHandle);
		verify(os).free(ptrOverlapped);
		verify(os).free(ptrBytesTransferred);

	}

	/**
	 * Verifies that all resource are freed
	 */
	@Test()
	public void close_free_Overlapped() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		operation = new _IoOperation(port, os, PORT_HANDLE);

		doThrow(new RuntimeException()).when(os).free(ptrOverlapped);
		try {
			operation.close();
		}
		catch (RuntimeException ignore) {}
		verify(os).CloseHandle(eventHandle);
		verify(os).free(ptrOverlapped);
		verify(os).free(ptrBytesTransferred);

	}

	/**
	 * Verifies that all resource are freed
	 */
	@Test()
	public void close_free_bytesTransferred() {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		operation = new _IoOperation(port, os, PORT_HANDLE);

		doThrow(new RuntimeException()).when(os).free(ptrBytesTransferred);
		try {
			operation.close();
		}
		catch (RuntimeException ignore) {}
		verify(os).CloseHandle(eventHandle);
		verify(os).free(ptrOverlapped);
		verify(os).free(ptrBytesTransferred);

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("javadoc")
	public static class _IoOperation extends IoOperation {

		public _IoOperation(SerialPort port,
							WinApi os,
							int handle) {
			super(port, os, handle);
		}
	}

}
