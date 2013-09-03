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

import org.junit.Before;
import org.junit.Test;
import org.xidobi.SerialPortSettings.SerialPortSettingsBuilder;

import static org.xidobi.DataBits.DATABITS_5;
import static org.xidobi.DataBits.DATABITS_6;
import static org.xidobi.DataBits.DATABITS_8;
import static org.xidobi.FlowControl.FLOWCONTROL_NONE;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_IN;
import static org.xidobi.Parity.PARITY_NONE;
import static org.xidobi.Parity.PARITY_ODD;
import static org.xidobi.Parity.PARITY_SPACE;
import static org.xidobi.StopBits.STOPBITS_1;
import static org.xidobi.StopBits.STOPBITS_1_5;
import static org.xidobi.StopBits.STOPBITS_2;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.Assert.assertThat;

/**
 * Tests the class {@link SerialPortSettings}.
 * 
 * @author Tobias Breﬂler
 */
public class TestSerialPortSettings {

	private SerialPortSettingsBuilder builder;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		builder = SerialPortSettings.from9600bauds8N1();
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a negative baud rate is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void bauds_withNegativeBaudRate() {
		SerialPortSettings.from9600bauds8N1().bauds(-1);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a negative baud rate is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void bauds_withZeroBaudRate() {
		SerialPortSettings.from9600bauds8N1().bauds(0);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid baud rate (1200)
	 * is passed. The settings must then be:
	 * 
	 * <ul>
	 * <li><b>bauds = 1200 (passed)</b></li>
	 * <li>dataBits = 8 (default)</li>
	 * <li>stopBits = 1 (default)</li>
	 * <li>parity = none (default)</li>
	 * <li>flowControl = none (default)</li>
	 * <li>RTS = true (default)</li>
	 * <li>DTR = true (default)</li>
	 * </ul>
	 */
	@Test
	public void create_unspecified() {
		SerialPortSettings result = SerialPortSettings.from9600bauds8N1().create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getBauds(), is(9600));
		assertThat(result.getDataBits(), is(DATABITS_8));
		assertThat(result.getStopBits(), is(STOPBITS_1));
		assertThat(result.getParity(), is(PARITY_NONE));
		assertThat(result.getFlowControl(), is(FLOWCONTROL_NONE));
		assertThat(result.isRTS(), is(true));
		assertThat(result.isDTR(), is(true));
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for baud
	 * rate (1200) is passed.
	 */
	@Test
	public void create_withBauds() {
		SerialPortSettings result = builder.bauds(1200).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getBauds(), is(1200));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a <code>bauds == 0</code>
	 * is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withBauds0() {
		builder.bauds(0).create();
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a negative baud rate is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNegativeBauds() {
		builder.bauds(-1).create();
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for data
	 * bits (6) is passed.
	 */
	@Test
	public void create_withDataBits() {
		SerialPortSettings result = builder.set(DATABITS_6).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getDataBits(), is(DATABITS_6));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullDataBits() {
		builder.set((DataBits) null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for stop
	 * bits (2) is passed.
	 */
	@Test
	public void create_withStopBits() {
		SerialPortSettings result = builder.set(STOPBITS_2).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getStopBits(), is(STOPBITS_2));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullStopBits() {
		builder.set((StopBits) null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for parity
	 * (odd) is passed.
	 */
	@Test
	public void create_withParity() {
		SerialPortSettings result = builder.set(PARITY_ODD).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getParity(), is(PARITY_ODD));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullParity() {
		builder.set((Parity) null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for flow
	 * control (RTS/CTS In) is passed.
	 */
	@Test
	public void create_withFlowControl() {
		SerialPortSettings result = builder.set(FLOWCONTROL_RTSCTS_IN).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getFlowControl(), is(FLOWCONTROL_RTSCTS_IN));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullFlowControl() {
		builder.set((FlowControl) null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when RTS == true is passed.
	 */
	@Test
	public void create_withRTS_true() {
		SerialPortSettings result = builder.rts(true).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.isRTS(), is(true));
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when RTS == false is passed.
	 */
	@Test
	public void create_withRTS_false() {
		SerialPortSettings result = builder.rts(false).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.isRTS(), is(false));
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when DTR == true is passed.
	 */
	@Test
	public void create_withDTR_true() {
		SerialPortSettings result = builder.dtr(true).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.isDTR(), is(true));
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when DTR == false is passed.
	 */
	@Test
	public void create_withDTR_false() {
		SerialPortSettings result = builder.dtr(false).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.isDTR(), is(false));
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when all values were set.
	 */
	@Test
	public void create_withAllValuesSet() {
		//@formatter:off
		SerialPortSettings result = SerialPortSettings.from9600bauds8N1()
													  .set(DATABITS_5)
													  .set(STOPBITS_1_5)
													  .set(PARITY_SPACE)
													  .set(FLOWCONTROL_RTSCTS_IN)
													  .rts(false)
													  .dtr(false)
													  .create();
		//@formatter:on

		assertThat(result, is(notNullValue()));
		assertThat(result.getBauds(), is(9600));
		assertThat(result.getDataBits(), is(DATABITS_5));
		assertThat(result.getStopBits(), is(STOPBITS_1_5));
		assertThat(result.getParity(), is(PARITY_SPACE));
		assertThat(result.getFlowControl(), is(FLOWCONTROL_RTSCTS_IN));
		assertThat(result.isRTS(), is(false));
		assertThat(result.isDTR(), is(false));
	}

}
