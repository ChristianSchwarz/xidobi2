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

import static org.xidobi.WinApi.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.xidobi.WinApi.FORMAT_MESSAGE_IGNORE_INSERTS;
import static org.xidobi.WinApi.LANG_NEUTRAL;
import static org.xidobi.WinApi.SUBLANG_DEFAULT;
import static org.xidobi.WinApi.SUBLANG_NEUTRAL;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.WinApi;
import org.xidobi.internal.NativeCodeException;

/**
 * Some utilities for the handling of exceptions.
 * 
 * @author Tobias Breﬂler
 */
public final class Throwables {

	/**
	 * This class can not be instantiated
	 */
	private Throwables() {}

	/**
	 * Creates and returns a new {@link NativeCodeException} with the given message, the given
	 * error-code and a message to the error-code, if available.
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 * @param message
	 *            the message, must not be <code>null</code>
	 * @param errorCode
	 *            the native error code
	 * @return a new {@link NativeCodeException}, never <code>null</code>
	 */
	@Nonnull
	public static final NativeCodeException newNativeCodeException(@Nonnull WinApi win, @Nonnull String message, int errorCode) {
		checkArgumentNotNull(message, "message");

		String nativeErrorMessage = getNativeErrorMessage(win, errorCode);
		return new NativeCodeException(message + "\r\nError-Code " + errorCode + ": " + nativeErrorMessage);
	}

	/**
	 * Creates and returns a new {@link IOException} with the given message, the given error-code
	 * and a message to the error-code, if available.
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 * @param message
	 *            the message, must not be <code>null</code>
	 * @param errorCode
	 *            the native error code
	 * @return a new {@link IOException}, never <code>null</code>
	 */
	@Nonnull
	public static final IOException newIOException(@Nonnull WinApi win, @Nonnull String message, int errorCode) {
		checkArgumentNotNull(message, "message");

		String nativeErrorMessage = getNativeErrorMessage(win, errorCode);
		return new IOException(message + "\r\nError-Code " + errorCode + ": " + nativeErrorMessage);
	}

	/**
	 * Returns an error message for the given error code. If no message can be found, then
	 * "No error message available" is returned.
	 */
	private static String getNativeErrorMessage(WinApi win, int errorCode) {
		checkArgumentNotNull(win, "win");

		byte[] lpMsgBuf = new byte[255];
		//@formatter:off
		int result = win.FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS, 
		                                null, errorCode, win.MAKELANGID(LANG_NEUTRAL, SUBLANG_NEUTRAL), lpMsgBuf, 255, null);
		//@formatter:on
		if (result == 0)
			return "No error description available.";
		return new String(lpMsgBuf, 0, result);
	}
}
