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
package org.xidobi;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Tests the class {@link SerialConnectionImpl}.
 * 
 * @author Tobias Breﬂler
 */
public class TestSerialConnectionImpl {

	@Mock
	private SerialPort port;
	@Mock
	private WinApi os;

	private int handle = 1;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings({ "resource", "unused" })
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullPort() {
		new SerialConnectionImpl(null, os, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@SuppressWarnings({ "unused", "resource" })
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullWinApi() {
		new SerialConnectionImpl(port, null, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when an invalid port handle is
	 * passed.
	 */
	@SuppressWarnings({ "unused", "resource" })
	@Test(expected = IllegalArgumentException.class)
	public void new_withInvalidHandle() {
		new SerialConnectionImpl(port, os, INVALID_HANDLE_VALUE);
	}

}
