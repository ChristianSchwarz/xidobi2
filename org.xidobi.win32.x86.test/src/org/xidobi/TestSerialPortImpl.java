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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;

import static org.xidobi.OS.INVALID_HANDLE_VALUE;

/**
 * Tests the class {@link SerialPortImpl}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestSerialPortImpl {

	/** a valid HANDLE value used in tests */
	private static final int handle = 12345;

	private static final byte[] DATA = {};

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
	 * Simulates are write operation without errors and verifies that all relevant methods of the {@link OS} are called.  
	 * @throws IOException 
	 */
	@Test
	public void write_succeed() throws IOException{
		final int  evtHandle= 321;
		final int sizeOf_OVERLAPPED=2345;
		
		when(os.CreateEventA(0, true, false, null)).thenReturn(evtHandle);
		when(os.sizeOf_OVERLAPPED()).thenReturn(sizeOf_OVERLAPPED);
		port.write(DATA);
		
		verify(os).CreateEventA(0, true, false, null);
		
		//creation of the OVERLAPPED
		verify(os).sizeOf_OVERLAPPED();
		verify(os).malloc(sizeOf_OVERLAPPED);
		
		//
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
