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
import static org.xidobi.WinApi.SUBLANG_NEUTRAL;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.WinApi;
import org.xidobi.spi.NativeCodeException;

/**
 * Some utilities which helps to create exceptions.
 * 
 * @author Tobias Breﬂler
 */
public final class Throwables {

	/** This class can not be instantiated */
	private Throwables() {}

	/**
	 * Creates and returns a new {@link NativeCodeException} with the given message, the given
	 * error-code and a message to the error-code, if available.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param message
	 *            the message, must not be <code>null</code>
	 * @param errorCode
	 *            the native error code
	 * @return a new {@link NativeCodeException}, never <code>null</code>
	 */
	@Nonnull
	public static final NativeCodeException newNativeCodeException(@Nonnull WinApi os, @Nonnull String message, int errorCode) {
		return new NativeCodeException(getErrorMessage(os, message, errorCode));
	}

	/**
	 * Creates and returns a new {@link IOException} with the given message, the given error-code
	 * and a message to the error-code, if available.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param message
	 *            the message, must not be <code>null</code>
	 * @param errorCode
	 *            the native error code
	 * @return a new {@link IOException}, never <code>null</code>
	 */
	@Nonnull
	public static final IOException newIOException(@Nonnull WinApi os, @Nonnull String message, int errorCode) {
		return new IOException(getErrorMessage(os, message, errorCode));
	}

	/**
	 * Returns an error message with the given message, the given error-code and a message to the
	 * error-code, if available.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param errorCode
	 *            the native error code
	 * @return error message, never <code>null</code>
	 */
	@Nonnull
	public static final String getErrorMessage(@Nonnull WinApi os, int errorCode) {
		String nativeErrorMessage = getNativeErrorMessage(os, errorCode);
		return "Error-Code " + errorCode + ": " + nativeErrorMessage;
	}

	/**
	 * Returns an error message with the given message, the given error-code and a message to the
	 * error-code, if available.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param message
	 *            the message, must not be <code>null</code>
	 * @param errorCode
	 *            the native error code
	 * @return error message, never <code>null</code>
	 */
	@Nonnull
	public static final String getErrorMessage(@Nonnull WinApi os, @Nonnull String message, int errorCode) {
		checkArgumentNotNull(message, "message");
		return message + "\r\n" + getErrorMessage(os, errorCode);
	}

	/**
	 * Returns an error message for the given error code. If no message can be found, then
	 * "No error message available" is returned.
	 */
	private static String getNativeErrorMessage(WinApi os, int errorCode) {
		checkArgumentNotNull(os, "win");

		byte[] lpMsgBuf = new byte[255];
		//@formatter:off
		int result = os.FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS, 
		                                null, errorCode, os.MAKELANGID(LANG_NEUTRAL, SUBLANG_NEUTRAL), lpMsgBuf, 255, null);
		//@formatter:on
		if (result == 0)
			return "No error description available.";

		// cut bytes to the length (result) without trailing linebreaks
		// and convert to a String:
		return new String(lpMsgBuf, 0, result - 2);
	}
}
