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
package org.xidobi.internal;

/**
 * This exception is used to indicate an unexpected result, behavior or error that was detected
 * after a native method was called.
 * 
 * @author Christian Schwarz
 */
public class NativeCodeException extends RuntimeException {

	/** Serial-Version-UID */
	private static final long serialVersionUID = 8424047512526437203L;

	/**
	 * Creates a new {@link NativeCodeException} to indicate an unexpected result, behavior or error
	 * that was detected after a native method was called.
	 * 
	 * @param message
	 *            an error description, can be <code>null</code>
	 */
	public NativeCodeException(String message) {
		super(message + "\r\nOops this should never happen, you found a bug! Please report it at: https://code.google.com/p/xidobi/issues");
	}

}