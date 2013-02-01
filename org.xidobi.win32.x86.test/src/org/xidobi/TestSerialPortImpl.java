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
import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.DCB;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.FILE_FLAG_OVERLAPPED;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.OPEN_EXISTING;
import static org.xidobi.WinApi.PURGE_RXCLEAR;
import static org.xidobi.WinApi.PURGE_TXCLEAR;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;

/**
 * Tests the class {@link SerialPortImpl}
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class TestSerialPortImpl {

	/** some value for an unspecific win32 error code */
	private static final int DUMMY_ERROR_CODE = 1;
	/** some value for an unspecific handle */
	private static final int PORT_HANDLE = 1;

	/** constant for an invalid handle */
	private static final int INVALID_HANDLE = -1;

	/** Class under test */
	private SerialPortImpl handle;

	@Mock
	private WinApi win;
	@Mock
	private SerialPortSettings settings;
	@Mock
	private DCBConfigurator configurator;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		handle = new SerialPortImpl(win, "COM1", "description", configurator);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_nullOS() {
		new SerialPortImpl(null, "COM1", "description", configurator);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_nullPortName() {
		new SerialPortImpl(win, null, "description", configurator);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_nullConfigurator() {
		new SerialPortImpl(win, "COM1", null, null);
	}

	/**
	 * Verifies that {@link SerialPortImpl#getPortName()} returns the port name that was
	 * passed to the constructor.
	 */
	@Test
	public void getPortName() {
		assertThat(handle.getPortName(), is("COM1"));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is passed
	 * as argument <code>settings</code>.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void open_withNullSettings() throws Exception {
		handle.open(null);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the call to
	 * {@link WinApi#CreateFile(String, int, int, int, int, int, int)} returns an invalid handle
	 * (-1). In this case the {@link IOException} must contain the error code that is returned by
	 * {@link WinApi#GetLastError()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_fail_CreateFileReturnsInvalidHandle() throws Exception {
		when(win.CreateFile(anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(INVALID_HANDLE);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(IOException.class);
		exception.expectMessage("Unable to open port (COM1)!\r\nError-Code " + DUMMY_ERROR_CODE);

		handle.open(settings);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the call to
	 * {@link WinApi#GetCommState(int, DCB)} is unsuccessful and returns <code>false</code>. In this
	 * case the {@link IOException} must contain the error code that is returned by
	 * {@link WinApi#GetLastError()} .
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_fail_GetCommStateReturnsFalse() throws Exception {
		when(win.CreateFile(anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(PORT_HANDLE);
		when(win.GetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);
		when(win.PurgeComm(PORT_HANDLE,PURGE_RXCLEAR | PURGE_TXCLEAR)).thenReturn(true);

		exception.expect(IOException.class);
		exception.expectMessage("Unable to retrieve the current control settings for port (COM1)!\r\nError-Code " + DUMMY_ERROR_CODE);

		try {
			handle.open(settings);
		}
		finally {
			verify(win).CloseHandle(PORT_HANDLE);
		}
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the call to
	 * {@link WinApi#GetCommState(int, DCB)} is unsuccessful and returns <code>false</code>. In this
	 * case the {@link IOException} must contain the error code that is returned by
	 * {@link WinApi#GetLastError()} .
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_fail_SetCommStateReturnsFalse() throws Exception {
		when(win.CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(PORT_HANDLE);
		when(win.GetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(true);
		when(win.SetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(IOException.class);
		exception.expectMessage("Unable to set the control settings (COM1)!\r\nError-Code " + DUMMY_ERROR_CODE);

		try {
			handle.open(settings);
		}
		finally {
			verify(win).CloseHandle(PORT_HANDLE);
		}
	}
	
	/**
	 * Verifies that an {@link NativeCodeException} is thrown, when the call to
	 * {@link WinApi#PurgeComm(int, int)} returns <code>false</code>. In this
	 * case the {@link NativeCodeException} must contain the error code that is returned by
	 * {@link WinApi#GetLastError()} .
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_fail_PurgeCommReturnsFalse() throws Exception {
		when(win.CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(PORT_HANDLE);
		when(win.GetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(true);
		when(win.SetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(true);
		when(win.PurgeComm(PORT_HANDLE,PURGE_RXCLEAR | PURGE_TXCLEAR)).thenReturn(false);
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("PurgeComm failed!\r\nError-Code " + DUMMY_ERROR_CODE);

		try {
			handle.open(settings);
		}
		finally {
			verify(win).CloseHandle(PORT_HANDLE);
		}
	}
	/**
	 * Verifies that an {@link NativeCodeException} is thrown, when the call to
	 * {@link WinApi#SetCommMask(int, int)} returns <code>false</code>. In this
	 * case the {@link NativeCodeException} must contain the error code that is returned by
	 * {@link WinApi#GetLastError()} .
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_fail_SetCommMaskReturnsFalse() throws Exception {
		when(win.CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(PORT_HANDLE);
		when(win.GetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(true);
		when(win.SetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(true);
		when(win.PurgeComm(PORT_HANDLE,PURGE_RXCLEAR | PURGE_TXCLEAR)).thenReturn(true);
		when(win.SetCommMask(PORT_HANDLE, EV_RXCHAR)).thenReturn(false);
		
		when(win.getPreservedError()).thenReturn(DUMMY_ERROR_CODE);
		
		exception.expect(NativeCodeException.class);
		exception.expectMessage("SetCommMask failed!\r\nError-Code " + DUMMY_ERROR_CODE);
		
		try {
			handle.open(settings);
		}
		finally {
			verify(win).CloseHandle(PORT_HANDLE);
		}
	}

	/**
	 * Verifies that a non <code>null</code> {@link SerialConnection} is returned, when the native methods
	 * are successful.
	 * 
	 * @throws Exception
	 */
	@Test
	public void open_succeed() throws Exception {
		when(win.CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0)).thenReturn(PORT_HANDLE);
		when(win.GetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(true);
		when(win.SetCommState(eq(PORT_HANDLE), anyDCB())).thenReturn(true);
		when(win.PurgeComm(PORT_HANDLE,PURGE_RXCLEAR | PURGE_TXCLEAR)).thenReturn(true);
		when(win.SetCommMask(PORT_HANDLE, EV_RXCHAR)).thenReturn(true);
		
		SerialConnection result = handle.open(settings);

		verify(win).CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);
		verify(win).GetCommState(eq(PORT_HANDLE), anyDCB());
		verify(configurator).configureDCB(anyDCB(), eq(settings));
		verify(win).SetCommState(eq(PORT_HANDLE), anyDCB());
		verify(win).PurgeComm(PORT_HANDLE, PURGE_RXCLEAR | PURGE_TXCLEAR);
		
		verify(win, never()).CloseHandle(PORT_HANDLE);
		assertThat(result, is(notNullValue()));
	}

	

	/**
	 * Verifies that {@link SerialPort#getDescription()} returns the <code>description</code>
	 * that was given ton the constructor.
	 */
	@Test
	public void getDescription() {
		SerialPort info = new SerialPortImpl(win, "portName", "description");
		assertThat(info.getDescription(), is("description"));
	}

	/**
	 * Verifies that {@link SerialPort#getDescription()} returns <code>null</code>, when no
	 * description was given to the constructor.
	 */
	@Test
	public void getDescription_isNull() {
		SerialPort info = new SerialPortImpl(win, "portName", null);
		assertThat(info.getDescription(), is(nullValue()));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Matcher for {@link DCB}
	 */
	private DCB anyDCB() {
		return any(DCB.class);
	}
}
