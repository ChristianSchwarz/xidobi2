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

import static java.nio.ByteBuffer.wrap;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.OS.ERROR_NO_MORE_ITEMS;
import static org.xidobi.OS.ERROR_SUCCESS;
import static org.xidobi.OS.HKEY_LOCAL_MACHINE;
import static org.xidobi.OS.KEY_READ;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xidobi.structs.HKEY;
import org.xidobi.structs.INT;

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
		when(os.RegOpenKeyExA(eq(HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(KEY_READ), any(HKEY.class))).thenReturn(AN_ERROR_CODE);

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

	@Test
	public void find_withNoSerialPortValuesInRegistry() {
		//@formatter:off
		when(os.RegOpenKeyExA(eq(HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(KEY_READ), any(HKEY.class)))
			.thenReturn(ERROR_SUCCESS);
		doAnswer(withValue("", "", ERROR_NO_MORE_ITEMS))
			.when(os).RegEnumValueA(any(HKEY.class), eq(0), any(byte[].class), any(INT.class), eq(0), any(INT.class), any(byte[].class), any(INT.class));
		// @formatter:on

		Set<SerialPortInfo> result = finder.find();

		assertThat(result, is(notNullValue()));
		assertThat(result, is(hasSize(0)));
	}

	@Test
	public void find_withOneSerialPortValueInRegistry() {
		//@formatter:off
		when(os.RegOpenKeyExA(eq(HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(KEY_READ), any(HKEY.class)))
			.thenReturn(ERROR_SUCCESS);
		doAnswer(withValue("/Device/Serial1", "COM1", ERROR_SUCCESS))
			.when(os).RegEnumValueA(any(HKEY.class), eq(0), any(byte[].class), any(INT.class), eq(0), any(INT.class), any(byte[].class), any(INT.class));
		doAnswer(withValue("", "", ERROR_NO_MORE_ITEMS))
			.when(os).RegEnumValueA(any(HKEY.class), eq(1), any(byte[].class), any(INT.class), eq(0), any(INT.class), any(byte[].class), any(INT.class));
		// @formatter:on

		Set<SerialPortInfo> result = finder.find();

		assertThat(result, is(notNullValue()));
		assertThat(result, is(hasSize(1)));
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	/** Mocks the behaviour of the native method RegEnumValueA(). */
	private Answer<Integer> withValue(final String lpValueName, final String lpData, final int status) {
		System.out.println("2");
		return new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				byte[] valueName = (byte[]) invocation.getArguments()[2];
				INT valueSize = (INT) invocation.getArguments()[3];
				byte[] data = (byte[]) invocation.getArguments()[6];
				INT dataSize = (INT) invocation.getArguments()[7];

				wrap(valueName).asCharBuffer().put(lpValueName);
				valueSize.value = lpValueName.length();
				wrap(data).asCharBuffer().put(lpData);
				dataSize.value = lpData.length();

				return status;
			}
		};
	}

}
