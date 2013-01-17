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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.OS.INVALID_HANDLE_VALUE;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.structs.DCB;

/**
 * Tests the class {@link SerialPortConfigurer}.
 * 
 * @author Tobias Breﬂler
 */
public class TestSerialPortConfigurer {

	/** An unspecific error code */
	private static final int DUMMY_ERROR_CODE = 1;
	/** An unspecific handle */
	private static final int DUMMY_HANDLE = 1;

	/** Class under test */
	private SerialPortConfigurer configurer;

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
		configurer = new SerialPortConfigurer(os);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullOS() {
		new SerialPortConfigurer(null);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when an invalid handle (-1) is
	 * passed.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setupSerialPort_withNegativeHandle() throws Exception {
		configurer.setupSerialPort(INVALID_HANDLE_VALUE, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when
	 * <code>settings == null</code> is passed.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setupSerialPort_withNullSettings() throws Exception {
		configurer.setupSerialPort(DUMMY_HANDLE, null);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the native method
	 * {@link OS#GetCommState(int, DCB)} fails (returns false).
	 * 
	 * @throws Exception
	 */
	@Test
	public void setupSerialPort_whenGetCommStateIsNotSuccessful() throws Exception {
		when(os.GetCommState(eq(DUMMY_HANDLE), any(DCB.class))).thenReturn(false);
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(IOException.class);
		exception.expectMessage("Can't retrieve current control settings! (Error-Code: " + DUMMY_ERROR_CODE + ")");

		configurer.setupSerialPort(DUMMY_HANDLE, settings);

		verify(os, times(1)).GetCommState(eq(DUMMY_HANDLE), any(DCB.class));
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the native method
	 * {@link OS#SetCommState(int, DCB)} fails (returns false).
	 * 
	 * @throws Exception
	 */
	@Test
	public void setupSerialPort_whenSetCommStateIsNotSuccessful() throws Exception {
		when(os.GetCommState(eq(DUMMY_HANDLE), any(DCB.class))).thenReturn(true);
		when(os.SetCommState(eq(DUMMY_HANDLE), any(DCB.class))).thenReturn(false);
		when(os.GetLastError()).thenReturn(DUMMY_ERROR_CODE);

		exception.expect(IOException.class);
		exception.expectMessage("Can't set control settings! (Error-Code: " + DUMMY_ERROR_CODE + ")");

		configurer.setupSerialPort(DUMMY_HANDLE, settings);

		verify(os, times(1)).GetCommState(eq(DUMMY_HANDLE), any(DCB.class));
		verify(os, times(1)).SetCommState(eq(DUMMY_HANDLE), any(DCB.class));
	}

}
