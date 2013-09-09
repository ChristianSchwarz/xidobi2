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
package org.xidobi.rfc2217.internal;

/**
 * @author Christian Schwarz
 */
@SuppressWarnings("javadoc")
public class ArrayUtil {

	/** This class is not intended to be instanciated */
	private ArrayUtil() {
	}

	/** Transforms the given byte[] to an int[] by coping all bytes into it. */
	public static int[] toIntArray(byte[] bytes) {
		int[] result = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			result[i] = bytes[i];
		return result;
	}

	/**
	 * Transforms the given int[] to an byte[]. Only the byte of lowest order will be copied, higher
	 * ordered bytes of an int will be ignored, e.g. {@code int[] 0x102} will be converted to
	 * {@code byte[] 0x02}
	 */
	public static byte[] toByteArray(int[] bytes, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++)
			result[i] = (byte) (bytes[i] & 0xff);
		return result;
	}
}