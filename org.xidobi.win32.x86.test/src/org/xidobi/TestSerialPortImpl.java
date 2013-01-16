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

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.OS.INVALID_HANDLE_VALUE;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Tests the class {@link SerialPortImpl}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestSerialPortImpl {

	/** a valid HANDLE value used in tests */
	private static final int handle = 12345;

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private OS os;

	/** the class under test */
	private SerialPort port;

	@Before
	public void setUp() {
		initMocks(this);
		port = new SerialPortImpl(os, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link OS} is
	 * <code>null</code>.
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_nullOs() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >os< must not be null!");

		new SerialPortImpl(null, handle);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the handle is
	 * {@link OS#INVALID_HANDLE_VALUE} (-1).
	 * 
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_negativeHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >handle< is invalid! Invalid handle value");

		new SerialPortImpl(os, INVALID_HANDLE_VALUE);
	}

	/**
	 * Verifies that a call to {@link SerialPort#close()} frees the native resources.
	 */
	@Test
	public void close() throws Exception {
		port.close();
		verify(os, only()).CloseHandle(handle);
	}
}
