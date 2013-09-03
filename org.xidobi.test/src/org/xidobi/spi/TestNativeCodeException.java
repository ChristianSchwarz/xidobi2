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
package org.xidobi.spi;

import org.junit.Test;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

/**
 * Tests the class {@link NativeCodeException}.
 * 
 * @author Tobias Breﬂler
 */
public class TestNativeCodeException {

	/**
	 * Verifies that an {@link NativeCodeException} can be created without message and
	 * {@link NativeCodeException#getMessage()} returns a message with a bug report link.
	 */
	@Test
	public void getMessage_withNull() {
		NativeCodeException exception = new NativeCodeException(null);
		assertThat(exception.getMessage(), is("Oops this should never happen, you found a bug! Please report it at: https://code.google.com/p/xidobi/issues"));
	}

	/**
	 * Verifies that an {@link NativeCodeException} can be created without message and
	 * {@link NativeCodeException#getMessage()} returns a message with a bug report link.
	 */
	@Test
	public void getMessage() {
		NativeCodeException exception = new NativeCodeException("An error message!");
		assertThat(exception.getMessage(), is("An error message!\r\nOops this should never happen, you found a bug! Please report it at: https://code.google.com/p/xidobi/issues"));
	}

}
