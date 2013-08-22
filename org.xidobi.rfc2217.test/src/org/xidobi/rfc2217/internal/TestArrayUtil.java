/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 15:54:16
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;

/**
 * @author Christian Schwarz
 *
 */
public class TestArrayUtil {


	
	/**
	 * Check the transformation of an int[] of a given length to an byte[]. Values greater than {@code 0xFF} must truncated to 8Bit.
	 */
	@Test
	public void toByteArray(){
		byte[] bytes = ArrayUtil.toByteArray(new int[] { 0xff, 0x102, 3, 0, 0, 0 }, 3);
		assertThat(bytes, is(new byte[] { (byte) 0xff, (byte) 0x02, 3 }));
	}
	
	/**
	 * Check the transformation of an byte[]  to an int[]. 
	 */
	@Test
	public void toIntArray(){
		int[] bytes = ArrayUtil.toIntArray(new byte[] {-1,2,3,});
		assertThat(bytes, is(new int[] { -1, 2, 3 }));
	}
}
