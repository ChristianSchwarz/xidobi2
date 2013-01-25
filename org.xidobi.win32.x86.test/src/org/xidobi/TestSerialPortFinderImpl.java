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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.ERROR_NO_MORE_ITEMS;
import static org.xidobi.WinApi.ERROR_SUCCESS;
import static org.xidobi.WinApi.HKEY_LOCAL_MACHINE;
import static org.xidobi.WinApi.KEY_READ;

import java.util.Set;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xidobi.internal.NativeCodeException;
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
	private WinApi win;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);
		finder = new SerialPortFinderImpl(win);

		when(win.sizeOf_HKEY()).thenReturn(1);
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
	 * Verifies that an {@link NativeCodeException} is thrown, when
	 * {@link WinApi#RegOpenKeyExA(int, String, int, int, HKEY)} is not successful. The allocated
	 * HKEY must be disposed at the end.
	 */
	@Test
	public void find_whenRegOpenKeyExANotSuccessful() {
		when(win.sizeOf_HKEY()).thenReturn(SIZE_OF_HKEY);
		when(win.malloc(SIZE_OF_HKEY)).thenReturn(HKEY_POINTER);
		when(win.RegOpenKeyExA(eq(HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(KEY_READ), any(HKEY.class))).thenReturn(AN_ERROR_CODE);

		exception.expect(NativeCodeException.class);
		exception.expectMessage("Couldn't open Windows Registry for subkey >" + HARDWARE_DEVICEMAP_SERIALCOMM + "<!\r\nError-Code " + AN_ERROR_CODE);

		try {
			finder.find();
		}
		finally {
			verify(win, times(1)).malloc(SIZE_OF_HKEY);
			verify(win, times(1)).free(HKEY_POINTER);
		}
	}

	/**
	 * Verifies that an empty {@link Set} is returned, when no values for serial ports are present
	 * in the Windows Registry.
	 */
	@Test
	public void find_withNoSerialPortValuesInRegistry() {
		//@formatter:off
		when(win.RegOpenKeyExA(eq(HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(KEY_READ), any(HKEY.class)))
			.thenReturn(ERROR_SUCCESS);
		doAnswer(withValue("", "", ERROR_NO_MORE_ITEMS))
			.when(win).RegEnumValueA(any(HKEY.class), eq(0), any(byte[].class), argThat(isINT(255)), eq(0), any(INT.class), any(byte[].class), argThat(isINT(255)));
		//@formatter:on

		Set<SerialPortHandle> result = finder.find();

		assertThat(result, is(notNullValue()));
		assertThat(result, is(hasSize(0)));
	}

	/**
	 * Verifies that a {@link Set} with one serial port is returned, when one value for a serial
	 * port is present in the Windows Registry.
	 */
	@Test
	public void find_withOneSerialPortValueInRegistry() {
		//@formatter:off
		when(win.RegOpenKeyExA(eq(HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(KEY_READ), any(HKEY.class)))
			.thenReturn(ERROR_SUCCESS);
		doAnswer(withValue("/Device/Serial1", "COM1 ", ERROR_SUCCESS))
			.when(win).RegEnumValueA(any(HKEY.class), eq(0), any(byte[].class), argThat(isINT(255)), eq(0), any(INT.class), any(byte[].class), argThat(isINT(255)));
		doAnswer(withValue("", "", ERROR_NO_MORE_ITEMS))
			.when(win).RegEnumValueA(any(HKEY.class), eq(1), any(byte[].class), argThat(isINT(255)), eq(0), any(INT.class), any(byte[].class), argThat(isINT(255)));
		//@formatter:on

		Set<SerialPortHandle> result = finder.find();

		assertThat(result, is(notNullValue()));
		assertThat(result, is(hasSize(1)));
		assertThat(result, contains(serialPortWith("COM1", "/Device/Serial1")));
	}

	/**
	 * Verifies that a {@link Set} with two serial ports is returned, when two values for serial
	 * ports are present in the Windows Registry.
	 */
	@Test
	public void find_withTwoSerialPortValueInRegistry() {
		//@formatter:off
		when(win.RegOpenKeyExA(eq(HKEY_LOCAL_MACHINE), eq(HARDWARE_DEVICEMAP_SERIALCOMM), eq(0), eq(KEY_READ), any(HKEY.class)))
			.thenReturn(ERROR_SUCCESS);
		doAnswer(withValue("/Device/Serial1", "COM1 ", ERROR_SUCCESS))
			.when(win).RegEnumValueA(any(HKEY.class), eq(0), any(byte[].class), argThat(isINT(255)), eq(0), any(INT.class), any(byte[].class), argThat(isINT(255)));
		doAnswer(withValue("/Device/Serial2", "COM2 ", ERROR_SUCCESS))
			.when(win).RegEnumValueA(any(HKEY.class), eq(1), any(byte[].class), argThat(isINT(255)), eq(0), any(INT.class), any(byte[].class), argThat(isINT(255)));
		doAnswer(withValue("", "", ERROR_NO_MORE_ITEMS))
			.when(win).RegEnumValueA(any(HKEY.class), eq(2), any(byte[].class), argThat(isINT(255)), eq(0), any(INT.class), any(byte[].class), argThat(isINT(255)));
		//@formatter:on

		Set<SerialPortHandle> result = finder.find();

		assertThat(result, is(notNullValue()));
		assertThat(result, is(hasSize(2)));
		//@formatter:off
		assertThat(result, containsInAnyOrder(serialPortWith("COM1", "/Device/Serial1"), 
		                                      serialPortWith("COM2", "/Device/Serial2")));
		//@formatter:on
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Returns an answer that mocks the behaviour of the native method RegEnumValueA(). */
	private Answer<Integer> withValue(final String lpValueName, final String lpData, final int status) {
		return new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				byte[] valueName = (byte[]) invocation.getArguments()[2];
				INT valueSize = (INT) invocation.getArguments()[3];
				byte[] data = (byte[]) invocation.getArguments()[6];
				INT dataSize = (INT) invocation.getArguments()[7];

				copyToBytes(lpValueName, valueName, valueSize);
				copyToBytes(lpData, data, dataSize);

				return status;
			}
		};
	}

	/**
	 * Copies the bytes from the given {@link String} to the byte[] and sets the size on the
	 * pointer.
	 */
	private void copyToBytes(final String source, byte[] destination, INT sizePointer) {
		for (int i = 0; i < source.length(); i++)
			destination[i] = source.getBytes()[i];
		sizePointer.value = source.length();
	}

	/** Returns a Matcher that verifies the portName and description of a {@link SerialPortHandle}. */
	private TypeSafeMatcher<SerialPortHandle> serialPortWith(final String portName, final String description) {
		return new CustomTypeSafeMatcher<SerialPortHandle>("a serial port info with portName >" + portName + "< and description >" + description + "<") {
			@Override
			protected boolean matchesSafely(SerialPortHandle actual) {
				if (!actual.getPortName().equals(portName))
					return false;
				if (!actual.getDescription().equals(description))
					return false;
				return true;
			}
		};
	}

	/** Returns a Matcher that verifies the value of an {@link INT}. */
	private TypeSafeMatcher<INT> isINT(final int value) {
		return new CustomTypeSafeMatcher<INT>("an INT with value >" + value + "<") {
			@Override
			protected boolean matchesSafely(INT actual) {
				if (actual.value != value)
					return false;
				return true;
			}
		};
	}

}
