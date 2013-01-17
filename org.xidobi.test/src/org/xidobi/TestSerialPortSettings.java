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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.xidobi.DataBits.DataBits_5;
import static org.xidobi.DataBits.DataBits_6;
import static org.xidobi.DataBits.DataBits_8;
import static org.xidobi.FlowControl.FlowControl_None;
import static org.xidobi.FlowControl.FlowControl_RTSCTS_In;
import static org.xidobi.Parity.Parity_None;
import static org.xidobi.Parity.Parity_Odd;
import static org.xidobi.Parity.Parity_Space;
import static org.xidobi.StopBits.StopBits_1;
import static org.xidobi.StopBits.StopBits_1_5;
import static org.xidobi.StopBits.StopBits_2;

import org.junit.Before;
import org.junit.Test;
import org.xidobi.SerialPortSettings.SerialPortSettingsBuilder;

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
		builder = SerialPortSettings.bauds(3600);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a negative baud rate is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void bauds_withNegativeBaudRate() {
		SerialPortSettings.bauds(-1);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a negative baud rate is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void bauds_withZeroBaudRate() {
		SerialPortSettings.bauds(0);
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
	 * </ul>
	 */
	@Test
	public void create_withBaudsOnly() {
		SerialPortSettings result = SerialPortSettings.bauds(1200).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getBauds(), is(1200));
		assertThat(result.getDataBits(), is(DataBits_8));
		assertThat(result.getStopBits(), is(StopBits_1));
		assertThat(result.getParity(), is(Parity_None));
		assertThat(result.getFlowControl(), is(FlowControl_None));
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for data
	 * bits (6) is passed.
	 */
	@Test
	public void create_withDataBits() {
		SerialPortSettings result = builder.dataBits(DataBits_6).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getDataBits(), is(DataBits_6));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullDataBits() {
		builder.dataBits(null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for stop
	 * bits (2) is passed.
	 */
	@Test
	public void create_withStopBits() {
		SerialPortSettings result = builder.stopBits(StopBits_2).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getStopBits(), is(StopBits_2));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullStopBits() {
		builder.stopBits(null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for parity
	 * (odd) is passed.
	 */
	@Test
	public void create_withParity() {
		SerialPortSettings result = builder.parity(Parity_Odd).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getParity(), is(Parity_Odd));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullParity() {
		builder.parity(null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when a valid value for flow
	 * control (RTS/CTS In) is passed.
	 */
	@Test
	public void create_withFlowControl() {
		SerialPortSettings result = builder.flowControl(FlowControl_RTSCTS_In).create();

		assertThat(result, is(notNullValue()));
		assertThat(result.getFlowControl(), is(FlowControl_RTSCTS_In));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>null</code> is
	 * passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void create_withNullFlowControl() {
		builder.flowControl(null);
	}

	/**
	 * Verifies that a valid {@link SerialPortSettings} is returned, when all values were set.
	 */
	@Test
	public void create_withAllValuesSet() {
		//@formatter:off
		SerialPortSettings result = SerialPortSettings.bauds(9600)
													  .dataBits(DataBits_5)
													  .stopBits(StopBits_1_5)
													  .parity(Parity_Space)
													  .flowControl(FlowControl_RTSCTS_In)
													  .create();
		//@formatter:on

		assertThat(result, is(notNullValue()));
		assertThat(result.getBauds(), is(9600));
		assertThat(result.getDataBits(), is(DataBits_5));
		assertThat(result.getStopBits(), is(StopBits_1_5));
		assertThat(result.getParity(), is(Parity_Space));
		assertThat(result.getFlowControl(), is(FlowControl_RTSCTS_In));
	}

}
