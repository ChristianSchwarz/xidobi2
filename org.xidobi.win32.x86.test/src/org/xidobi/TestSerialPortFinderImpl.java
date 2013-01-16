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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.structs.HKEY;

/**
 * Tests the class {@link SerialPortFinderImpl}.
 * 
 * @author Tobias Breﬂler
 */
public class TestSerialPortFinderImpl {

	/** Some unspecified error code */
	private static final int AN_ERROR_CODE = 123;
	/** Size of HKEY */
	private static final int SIZE_OF_HKEY = 1;
	/** Pointer to HKEY */
	private static final int HKEY_POINTER = 2;
	/** Subkey for serial ports in the Windows Registry */
	private static final String HARDWARE_DEVICEMAP_SERIALCOMM = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";

	/** Class under test */
	private SerialPortFinderImpl finder;

	@Mock
	private OS os;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);
		finder = new SerialPortFinderImpl(os);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>os == null</code> is
	 * passed to the constructor.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullOS() {
		new SerialPortFinderImpl(null);
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when
	 * {@link OS#RegOpenKeyExA(int, String, int, int, HKEY)} is not successful. The allocated HKEY
	 * must be disposed at the end.
	 */
	@Test
	public void find_whenRegOpenKeyExANotSuccessful() {
		when(os.sizeOf_HKEY()).thenReturn(SIZE_OF_HKEY);
		when(os.malloc(SIZE_OF_HKEY)).thenReturn(HKEY_POINTER);
		when(os.RegOpenKeyExA(eq(OS.HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(OS.KEY_READ), any(HKEY.class))).thenReturn(AN_ERROR_CODE);

		exception.expect(IllegalStateException.class);
		exception.expectMessage("Couldn't open windows registry for subkey >" + HARDWARE_DEVICEMAP_SERIALCOMM + "<! (Error-Code: " + AN_ERROR_CODE + ")");

		try {
			finder.find();
		}
		finally {
			verify(os, times(1)).malloc(SIZE_OF_HKEY);
			verify(os, times(1)).free(HKEY_POINTER);
		}
	}
}
