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
public class ArrayUtil {
	
	
	/** This class is not intended to be instanciated */
	private ArrayUtil() {}
	
	
	public static int[] toIntArray(byte[] bytes){
		int[] result = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			result[i] =bytes[i];
		return result;
	}
	
	/**
	 * 
	 * @param bytes
	 * @param length
	 * @return
	 */
	public static byte[] toByteArray(int[] bytes, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++)
			result[i] = (byte) (bytes[i] & 0xff);
		return result;
	}
}