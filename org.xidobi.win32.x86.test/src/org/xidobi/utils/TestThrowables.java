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
package org.xidobi.utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.FORMAT_MESSAGE_ALLOCATE_BUFFER;
import static org.xidobi.WinApi.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.xidobi.WinApi.FORMAT_MESSAGE_IGNORE_INSERTS;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xidobi.WinApi;
import org.xidobi.internal.NativeCodeException;

/**
 * Tests class {@link Throwables}.
 * 
 * @author Tobias Breﬂler
 */
public class TestThrowables {

	private static int FORMAT = FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS;

	@Mock
	private WinApi win;

	private String message = "An error message!";
	private int errorCode = 1;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void newNativeCodeException_withNullWinApi() {
		Throwables.newNativeCodeException(null, message, errorCode);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void newNativeCodeException_withNullMessage() {
		Throwables.newNativeCodeException(win, null, errorCode);
	}

	/**
	 * Verifies that {@link Throwables#newNativeCodeException(WinApi, String, int)} returns a
	 * {@link NativeCodeException} with a default text, when no error message is available for the
	 * given error code.
	 */
	@Test
	public void newNativeCodeException_noNativeErrorMessage() {
		//@formatter:off
		when(win.FormatMessageA(eq(FORMAT), eq((Void) null), eq(errorCode), anyInt(), any(byte[].class), eq(255), eq((Void) null)))
			.thenReturn(0);
		//@formatter:on

		NativeCodeException result = Throwables.newNativeCodeException(win, message, errorCode);

		assertThat(result, is(nativeCodeException("An error message!\r\nError-Code 1: No error message available")));
	}

	/**
	 * Verifies that {@link Throwables#newNativeCodeException(WinApi, String, int)} returns a
	 * {@link NativeCodeException} with an error message for the given error code.
	 */
	@Test
	public void newNativeCodeException_withNativeErrorMessage() {
		String nativeErrorMessage = "This is a native error";

		//@formatter:off
		doAnswer(withMessage(nativeErrorMessage)).
			when(win).FormatMessageA(eq(FORMAT), eq((Void) null), eq(errorCode), anyInt(), any(byte[].class), eq(255), eq((Void) null));
		//@formatter:on

		NativeCodeException result = Throwables.newNativeCodeException(win, message, errorCode);

		assertThat(result, is(nativeCodeException("An error message!\r\nError-Code 1: This is a native error")));
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////

	/** Matcher for {@link NativeCodeException} starting with the message. */
	private TypeSafeMatcher<NativeCodeException> nativeCodeException(final String message) {
		return new CustomTypeSafeMatcher<NativeCodeException>("NativeCodeException with message >" + message + "<") {
			@Override
			protected boolean matchesSafely(NativeCodeException actual) {
				return (actual.getMessage().startsWith(message));
			}
		};
	}

	/** {@link Answer} for FormatMessageA() that returns a native error message. */
	private Answer<Integer> withMessage(final String nativeErrorMessage) {
		return new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				byte[] lpBuffer = (byte[]) invocation.getArguments()[4];
				copyToBytes(nativeErrorMessage, lpBuffer);
				return nativeErrorMessage.length();
			}
		};
	}

	/**
	 * Copies the bytes from the given {@link String} to the byte array.
	 */
	private void copyToBytes(final String source, byte[] destination) {
		for (int i = 0; i < source.length(); i++)
			destination[i] = source.getBytes()[i];
	}

}
