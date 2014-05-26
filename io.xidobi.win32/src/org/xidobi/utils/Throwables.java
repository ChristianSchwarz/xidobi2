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


import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.spi.NativeCodeException;

import com.sun.jna.platform.win32.Kernel32Util;

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
	public static final NativeCodeException newNativeCodeException(@Nonnull String message, int errorCode) {
		return new NativeCodeException(getErrorMessage(message, errorCode));
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
	public static final IOException newIOException(@Nonnull String message, int errorCode) {
		return new IOException(getErrorMessage(message, errorCode));
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
	public static final String getErrorMessage(int errorCode) {
		String nativeErrorMessage = getNativeErrorMessage(errorCode);
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
	public static final String getErrorMessage(@Nonnull String message, int errorCode) {
		checkArgumentNotNull(message, "message");
		return message + "\r\n" + getErrorMessage(errorCode);
	}

	/**
	 * Returns an error message for the given error code. If no message can be found, then
	 * "No error message available" is returned.
	 */
	private static String getNativeErrorMessage( int errorCode) {
		return Kernel32Util.formatMessageFromLastErrorCode(errorCode);
	}
}
