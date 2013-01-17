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
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.xidobi.OS.INVALID_HANDLE_VALUE;
import static org.xidobi.OS.WAIT_OBJECT_0;

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
	private static final int eventHandle = 1;

	/** a valid HANDLE value used in tests */
	private static final int handle = 2;

	private static final byte[] DATA = new byte[5];

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private OS os;

	@Mock
	private SerialPortHandle portHandle;

	/** the class under test */
	private SerialPortImpl port;

	@Before
	public void setUp() {
		initMocks(this);
		port = new SerialPortImpl(portHandle, os, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link OS} is
	 * <code>null</code>.
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_nullOs() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >os< must not be null!");

		new SerialPortImpl(portHandle, null, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed
	 * {@link SerialPortHandle} is <code>null</code>.
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_nullPortHandle() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >portHandle< must not be null!");

		new SerialPortImpl(null, os, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the handle is
	 * {@link OS#INVALID_HANDLE_VALUE} (-1).
	 * 
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_negativeHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >handle< is invalid! Invalid handle value");

		new SerialPortImpl(portHandle, os, INVALID_HANDLE_VALUE);
	}

	/**
	 * Simulates are write operation without errors and verifies that all relevant methods of the
	 * {@link OS} are called.
	 * 
	 * @throws IOException
	 */
	@Test
	public void write_succeed() throws IOException {
		when(os.CreateEventA(0, true, false, null)).thenReturn(eventHandle);
		when(os.WriteFile(eq(handle), eq(DATA), eq(DATA.length), any(INT.class), any(OVERLAPPED.class))).thenReturn(true);
		when(os.WaitForSingleObject(eventHandle, 2000)).thenReturn(WAIT_OBJECT_0);
		when(os.GetOverlappedResult(eq(handle), any(OVERLAPPED.class),any(INT.class),eq(true))).thenReturn(true);
		
		port.write(DATA);

		verify(os).CreateEventA(0, true, false, null);
		verify(os).WriteFile(eq(handle), eq(DATA), eq(DATA.length), any(INT.class), any(OVERLAPPED.class));
		verify(os).WaitForSingleObject(eventHandle, 2000);
		verify(os).GetOverlappedResult(eq(handle), any(OVERLAPPED.class),any(INT.class),eq(true));
		
	}

	/**
	 * Verifies that a call to {@link SerialPort#close()} frees the native resources.
	 */
	@Test
	public void close() throws Exception {
		port.close();
		verify(os).CloseHandle(handle);
		verifyNoMoreInteractions(os);
	}
}
