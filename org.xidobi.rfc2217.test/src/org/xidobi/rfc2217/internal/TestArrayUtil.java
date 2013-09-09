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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;

/**
 * Tests the class {@link ArrayUtil}.
 * 
 * @author Christian Schwarz
 */
public class TestArrayUtil {

	/**
	 * Check the transformation of an int[] of a given length to an byte[]. Values greater than
	 * {@code 0xFF} must truncated to 8Bit.
	 */
	@Test
	public void toByteArray() {
		byte[] bytes = ArrayUtil.toByteArray(new int[] { 0xff, 0x102, 3, 0, 0, 0 }, 3);
		assertThat(bytes, is(new byte[] { (byte) 0xff, (byte) 0x02, 3 }));
	}

	/**
	 * Check the transformation of an byte[] to an int[].
	 */
	@Test
	public void toIntArray() {
		int[] bytes = ArrayUtil.toIntArray(new byte[] { -1, 2, 3, });
		assertThat(bytes, is(new int[] { -1, 2, 3 }));
	}
}
