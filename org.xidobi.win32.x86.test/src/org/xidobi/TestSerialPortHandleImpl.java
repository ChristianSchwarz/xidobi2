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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.OS.FILE_FLAG_OVERLAPPED;
import static org.xidobi.OS.GENERIC_READ;
import static org.xidobi.OS.GENERIC_WRITE;
import static org.xidobi.OS.OPEN_EXISTING;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.structs.DCB;

/**
 * Tests the class {@link SerialPortHandleImpl}
 * 
 * @author Christian Schwarz
 * @author Tobias Bre�ler
 */
public class TestSerialPortHandleImpl {

	/** some value for an unspecific win32 error code */
	private static final int AN_ERROR_CODE = 1;
	/** some value for an unspecific handle */
	private static final int A_PORT_HANDLE = 1;

	/** constant for an invalid handle */
	private static final int INVALID_HANDLE = -1;

	/** Class under test */
	private SerialPortHandleImpl handle;

	@Mock
	private OS os;
	@Mock
	private SerialPortSettings settings;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		handle = new SerialPortHandleImpl(os);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_nullOS() {
		new SerialPortHandleImpl(null);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is passed
	 * as argument <code>portName</code>.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void open_withNullPortName() throws Exception {
		handle.open(null, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is passed
	 * as argument <code>settings</code>.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void open_withNullSettings() throws Exception {
		handle.open("portName", null);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the call to
	 * {@link OS#CreateFile(String, int, int, int, int, int, int)} returns an invalid handle (-1).
	 * In this case the {@link IOException} must contain the error code that is returned by
	 * {@link OS#GetLastError()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_CreateFileReturnsInvalidHandle() throws Exception {
		when(os.CreateFile("\\\\.\\portName", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(INVALID_HANDLE);
		when(os.GetLastError()).thenReturn(AN_ERROR_CODE);

		exception.expect(IOException.class);
		exception.expectMessage("Unable to open port >portName<! (Error-Code: 1)");

		handle.open("portName", settings);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the call to
	 * {@link OS#GetCommState(int, DCB)} is unsuccessful and returns <code>false</code>. In this
	 * case the {@link IOException} must contain the error code that is returned by
	 * {@link OS#GetLastError()} .
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_GetCommStateReturnsFalse() throws Exception {
		when(os.CreateFile("\\\\.\\portName", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(A_PORT_HANDLE);
		when(os.GetCommState(eq(A_PORT_HANDLE), any(DCB.class))).thenReturn(false);
		when(os.GetLastError()).thenReturn(AN_ERROR_CODE);

		exception.expect(IOException.class);
		exception.expectMessage("Unable to retrieve the current control settings for port >portName<! (Error-Code: 1)");

		try {
			handle.open("portName", settings);
		}
		finally {
			verify(os).CloseHandle(A_PORT_HANDLE);
		}
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the call to
	 * {@link OS#GetCommState(int, DCB)} is unsuccessful and returns <code>false</code>. In this
	 * case the {@link IOException} must contain the error code that is returned by
	 * {@link OS#GetLastError()} .
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_SetCommStateReturnsFalse() throws Exception {
		when(os.CreateFile("\\\\.\\portName", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(A_PORT_HANDLE);
		when(os.GetCommState(eq(A_PORT_HANDLE), any(DCB.class))).thenReturn(true);
		when(os.SetCommState(eq(A_PORT_HANDLE), any(DCB.class))).thenReturn(false);
		when(os.GetLastError()).thenReturn(AN_ERROR_CODE);

		exception.expect(IOException.class);
		exception.expectMessage("Unable to set the control settings for port >portName<! (Error-Code: 1)");

		try {
			handle.open("portName", settings);
		}
		finally {
			verify(os).CloseHandle(A_PORT_HANDLE);
		}
	}

	/**
	 * Verifies that a non <code>null</code> {@link SerialPort} is returned, when the native methods
	 * are successful.
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_returnsSerialPort() throws Exception {
		when(os.CreateFile("\\\\.\\portName", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(A_PORT_HANDLE);
		when(os.GetCommState(eq(A_PORT_HANDLE), any(DCB.class))).thenReturn(true);
		when(os.SetCommState(eq(A_PORT_HANDLE), any(DCB.class))).thenReturn(true);

		SerialPort result = handle.open("portName", settings);

		assertThat(result, is(notNullValue()));
		verify(os).CreateFile("\\\\.\\portName", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);
		verify(os).GetCommState(eq(A_PORT_HANDLE), any(DCB.class));
		verify(os).SetCommState(eq(A_PORT_HANDLE), any(DCB.class));
		verify(os, never()).CloseHandle(A_PORT_HANDLE);
	}
}
