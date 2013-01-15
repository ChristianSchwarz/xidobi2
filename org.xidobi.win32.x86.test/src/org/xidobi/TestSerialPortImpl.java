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
import static org.xidobi.OS.INVALID_HANDLE_VALUE;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Tests the class {@link SerialPortImpl}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestSerialPortImpl {

	/** check exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Mock
	private OS os;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link OS} is
	 * <code>null</code>.
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_nullOs() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The argument >os< must not be null!");

		new SerialPortImpl(null, 12345);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the handle is {@link OS#INVALID_HANDLE_VALUE} (-1).
	 * 
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_negativeHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The argument >os< must not be null!");

		new SerialPortImpl(os, INVALID_HANDLE_VALUE);
	}

}
