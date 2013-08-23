/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 15:13:31
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

/**
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class ArrayUtil {

	/** This class is not intended to be instanciated */
	private ArrayUtil() {}

	/** Transforms the given byte[] to an int[] by coping all bytes into it. */
	public static int[] toIntArray(byte[] bytes) {
		int[] result = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			result[i] = bytes[i];
		return result;
	}

	/**
	 * 
	 * Transforms the given int[] to an byte[]. Only the byte of lowest order will be copied, higher
	 * ordered bytes of an int will be ignored, e.g. {@code int[]{0x102} will be converted to
	 * {@code byte[]{0x02}
	 */
	public static byte[] toByteArray(int[] bytes, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++)
			result[i] = (byte) (bytes[i] & 0xff);
		return result;
	}
}