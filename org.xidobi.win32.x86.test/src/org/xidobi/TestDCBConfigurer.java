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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.Parity.Parity_Even;
import static org.xidobi.Parity.Parity_Mark;
import static org.xidobi.Parity.Parity_None;
import static org.xidobi.Parity.Parity_Odd;
import static org.xidobi.Parity.Parity_Space;
import static org.xidobi.structs.DCB.EVENPARITY;
import static org.xidobi.structs.DCB.MARKPARITY;
import static org.xidobi.structs.DCB.NOPARITY;
import static org.xidobi.structs.DCB.ODDPARITY;
import static org.xidobi.structs.DCB.SPACEPARITY;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.structs.DCB;

/**
 * Tests the class {@link DCBConfigurer}.
 * 
 * @author Tobias Breﬂler
 */
public class TestDCBConfigurer {

	/** Class under test */
	private DCBConfigurer configurer;

	@Mock
	private SerialPortSettings settings;

	private DCB dcb;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);
		configurer = new DCBConfigurer();
		dcb = new DCB();
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when
	 * <code>settings == null</code> is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void configureDCB_withNullDCB() {
		configurer.configureDCB(null, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when
	 * <code>settings == null</code> is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void configureDCB_withNullSettings() {
		configurer.configureDCB(dcb, null);
	}

	/**
	 * Verifies that the baud rate from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withBaudRate() {
		mockSerialPortSettings(9600, Parity_None);

		configurer.configureDCB(dcb, settings);

		assertThat(dcb.BaudRate, is(9600));
	}

	/**
	 * Verifies that the parity (none) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityNone() {
		mockSerialPortSettings(9600, Parity_None);

		configurer.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) NOPARITY));
	}

	/**
	 * Verifies that the parity (odd) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityOdd() {
		mockSerialPortSettings(9600, Parity_Odd);

		configurer.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) ODDPARITY));
	}

	/**
	 * Verifies that the parity (even) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityEven() {
		mockSerialPortSettings(9600, Parity_Even);

		configurer.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) EVENPARITY));
	}

	/**
	 * Verifies that the parity (mark) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityMark() {
		mockSerialPortSettings(9600, Parity_Mark);

		configurer.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) MARKPARITY));
	}

	/**
	 * Verifies that the parity (space) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParitySpace() {
		mockSerialPortSettings(9600, Parity_Space);

		configurer.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) SPACEPARITY));
	}

	// //////////////////////////////////////////////////////////////////////////////////////

	/** Mocks the values of a {@link SerialPortSettings}. */
	private void mockSerialPortSettings(int bauds, Parity parity) {
		when(settings.getBauds()).thenReturn(bauds);
		when(settings.getParity()).thenReturn(parity);
	}
}
