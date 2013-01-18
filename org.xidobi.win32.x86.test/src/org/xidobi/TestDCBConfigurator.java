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
import static org.xidobi.DataBits.DataBits_5;
import static org.xidobi.DataBits.DataBits_6;
import static org.xidobi.DataBits.DataBits_7;
import static org.xidobi.DataBits.DataBits_8;
import static org.xidobi.DataBits.DataBits_9;
import static org.xidobi.FlowControl.FlowControl_None;
import static org.xidobi.FlowControl.FlowControl_RTSCTS_In;
import static org.xidobi.FlowControl.FlowControl_RTSCTS_In_Out;
import static org.xidobi.FlowControl.FlowControl_RTSCTS_Out;
import static org.xidobi.FlowControl.FlowControl_XONXOFF_In;
import static org.xidobi.FlowControl.FlowControl_XONXOFF_In_Out;
import static org.xidobi.FlowControl.FlowControl_XONXOFF_Out;
import static org.xidobi.Parity.Parity_Even;
import static org.xidobi.Parity.Parity_Mark;
import static org.xidobi.Parity.Parity_None;
import static org.xidobi.Parity.Parity_Odd;
import static org.xidobi.Parity.Parity_Space;
import static org.xidobi.StopBits.StopBits_1;
import static org.xidobi.StopBits.StopBits_1_5;
import static org.xidobi.StopBits.StopBits_2;
import static org.xidobi.structs.DCB.EVENPARITY;
import static org.xidobi.structs.DCB.MARKPARITY;
import static org.xidobi.structs.DCB.NOPARITY;
import static org.xidobi.structs.DCB.ODDPARITY;
import static org.xidobi.structs.DCB.RTS_CONTROL_ENABLE;
import static org.xidobi.structs.DCB.RTS_CONTROL_HANDSHAKE;
import static org.xidobi.structs.DCB.SPACEPARITY;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.structs.DCB;

/**
 * Tests the class {@link DCBConfigurator}.
 * 
 * @author Tobias Breﬂler
 */
public class TestDCBConfigurator {

	private final static int IGNORE_BAUDS = 9600;
	private final static DataBits IGNORE_DATABITS = DataBits_9;
	private final static StopBits IGNORE_STOPBITS = StopBits_1_5;
	private final static Parity IGNORE_PARITY = Parity_None;
	private final static FlowControl IGNORE_FLOWCONTROL = FlowControl_None;
	private final static boolean IGNORE = true;

	/** Class under test */
	private DCBConfigurator configurator;

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
		configurator = new DCBConfigurator();
		dcb = new DCB();
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when
	 * <code>settings == null</code> is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void configureDCB_withNullDCB() {
		configurator.configureDCB(null, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when
	 * <code>settings == null</code> is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void configureDCB_withNullSettings() {
		configurator.configureDCB(dcb, null);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a serial port settings with
	 * 5 data bits and 2 stop bits ist passed. This is an invalid combination:
	 * <p>
	 * <i>The use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8 data
	 * bits with 1.5 stop bits.</i>
	 */
	@Test
	public void configureDCB_with5dataBitsAnd2stopBits() {
		mockSerialPortSettings(9600, DataBits_5, StopBits_2, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid serial port settings! The use of 2 stop bits with 5 data bits is an invalid combination.");

		configurator.configureDCB(dcb, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a serial port settings with
	 * 6 data bits and 1.5 stop bits ist passed. This is an invalid combination:
	 * <p>
	 * <i>The use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8 data
	 * bits with 1.5 stop bits.</i>
	 */
	@Test
	public void configureDCB_with6dataBitsAnd15stopBits() {
		mockSerialPortSettings(9600, DataBits_6, StopBits_1_5, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid serial port settings! The use of 1.5 stop bits with 6, 7 or 8 data bits is an invalid combination.");

		configurator.configureDCB(dcb, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a serial port settings with
	 * 7 data bits and 1.5 stop bits ist passed. This is an invalid combination:
	 * <p>
	 * <i>The use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8 data
	 * bits with 1.5 stop bits.</i>
	 */
	@Test
	public void configureDCB_with7dataBitsAnd15stopBits() {
		mockSerialPortSettings(9600, DataBits_7, StopBits_1_5, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid serial port settings! The use of 1.5 stop bits with 6, 7 or 8 data bits is an invalid combination.");

		configurator.configureDCB(dcb, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a serial port settings with
	 * 8 data bits and 1.5 stop bits ist passed. This is an invalid combination:
	 * <p>
	 * <i>The use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8 data
	 * bits with 1.5 stop bits.</i>
	 */
	@Test
	public void configureDCB_with8dataBitsAnd15stopBits() {
		mockSerialPortSettings(9600, DataBits_8, StopBits_1_5, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid serial port settings! The use of 1.5 stop bits with 6, 7 or 8 data bits is an invalid combination.");

		configurator.configureDCB(dcb, settings);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a serial port settings with
	 * 5 data bits and 2 stop bits ist passed. This is an invalid combination:
	 * <p>
	 * <i>The use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8 data
	 * bits with 1.5 stop bits.</i>
	 */
	@Test
	public void configureDCB_withInvalid() {
		mockSerialPortSettings(9600, DataBits_5, StopBits_2, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid serial port settings! The use of 2 stop bits with 5 data bits is an invalid combination.");

		configurator.configureDCB(dcb, settings);
	}

	/**
	 * Verifies that the baud rate from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withBaudRate() {
		mockSerialPortSettings(9600, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.BaudRate, is(9600));
	}

	/**
	 * Verifies that the data bits (5) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withDataBits5() {
		mockSerialPortSettings(IGNORE_BAUDS, DataBits_5, IGNORE_STOPBITS, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.ByteSize, is((byte) 0x05));
	}

	/**
	 * Verifies that the data bits (6) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withDataBits6() {
		mockSerialPortSettings(IGNORE_BAUDS, DataBits_6, StopBits_1, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.ByteSize, is((byte) 0x06));
	}

	/**
	 * Verifies that the data bits (7) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withDataBits7() {
		mockSerialPortSettings(IGNORE_BAUDS, DataBits_7, StopBits_1, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.ByteSize, is((byte) 0x07));
	}

	/**
	 * Verifies that the data bits (8) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withDataBits8() {
		mockSerialPortSettings(IGNORE_BAUDS, DataBits_8, StopBits_1, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.ByteSize, is((byte) 0x08));
	}

	/**
	 * Verifies that the data bits (9) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withDataBits9() {
		mockSerialPortSettings(IGNORE_BAUDS, DataBits_9, IGNORE_STOPBITS, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.ByteSize, is((byte) 0x09));
	}

	/**
	 * Verifies that the stop bits (1) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withStopBits1() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, StopBits_1, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.StopBits, is((byte) 0x00));
	}

	/**
	 * Verifies that the stop bits (1.5) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withStopBits1_5() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, StopBits_1_5, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.StopBits, is((byte) 0x01));
	}

	/**
	 * Verifies that the stop bits (2) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withStopBits2() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, StopBits_2, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.StopBits, is((byte) 0x02));
	}

	/**
	 * Verifies that the parity (none) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityNone() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, Parity_None, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) NOPARITY));
	}

	/**
	 * Verifies that the parity (odd) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityOdd() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, Parity_Odd, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) ODDPARITY));
	}

	/**
	 * Verifies that the parity (even) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityEven() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, Parity_Even, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) EVENPARITY));
	}

	/**
	 * Verifies that the parity (mark) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParityMark() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, Parity_Mark, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) MARKPARITY));
	}

	/**
	 * Verifies that the parity (space) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withParitySpace() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, Parity_Space, IGNORE_FLOWCONTROL, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.Parity, is((byte) SPACEPARITY));
	}

	/**
	 * Verifies that the flow control (none) from the serial port settings are set on the DCB
	 * struct.
	 */
	@Test
	public void configureDCB_withFlowControlNone() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, FlowControl_None, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(RTS_CONTROL_ENABLE));
		assertThat(dcb.fOutxCtsFlow, is(0));
		assertThat(dcb.fOutX, is(0));
		assertThat(dcb.fInX, is(0));
	}

	/**
	 * Verifies that the flow control (RTS/CTS In) from the serial port settings are set on the DCB
	 * struct.
	 */
	@Test
	public void configureDCB_withFlowControlRTSCTSIn() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, FlowControl_RTSCTS_In, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(RTS_CONTROL_HANDSHAKE));
		assertThat(dcb.fOutxCtsFlow, is(0));
		assertThat(dcb.fOutX, is(0));
		assertThat(dcb.fInX, is(0));
	}

	/**
	 * Verifies that the flow control (RTS/CTS Out) from the serial port settings are set on the DCB
	 * struct.
	 */
	@Test
	public void configureDCB_withFlowControlRTSCTSOut() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, FlowControl_RTSCTS_Out, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(RTS_CONTROL_ENABLE));
		assertThat(dcb.fOutxCtsFlow, is(1));
		assertThat(dcb.fOutX, is(0));
		assertThat(dcb.fInX, is(0));
	}

	/**
	 * Verifies that the flow control (RTS/CTS In & Out) from the serial port settings are set on
	 * the DCB struct.
	 */
	@Test
	public void configureDCB_withFlowControlRTSCTSInOut() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, FlowControl_RTSCTS_In_Out, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(RTS_CONTROL_HANDSHAKE));
		assertThat(dcb.fOutxCtsFlow, is(1));
		assertThat(dcb.fOutX, is(0));
		assertThat(dcb.fInX, is(0));
	}

	/**
	 * Verifies that the flow control (XON/XOFF In) from the serial port settings are set on the DCB
	 * struct.
	 */
	@Test
	public void configureDCB_withFlowControlXONXOFFIn() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, FlowControl_XONXOFF_In, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(RTS_CONTROL_ENABLE));
		assertThat(dcb.fOutxCtsFlow, is(0));
		assertThat(dcb.fOutX, is(0));
		assertThat(dcb.fInX, is(1));
	}

	/**
	 * Verifies that the flow control (XON/XOFF Out) from the serial port settings are set on the
	 * DCB struct.
	 */
	@Test
	public void configureDCB_withFlowControlXONXOFFOut() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, FlowControl_XONXOFF_Out, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(RTS_CONTROL_ENABLE));
		assertThat(dcb.fOutxCtsFlow, is(0));
		assertThat(dcb.fOutX, is(1));
		assertThat(dcb.fInX, is(0));
	}

	/**
	 * Verifies that the flow control (XON/XOFF In & Out) from the serial port settings are set on
	 * the DCB struct.
	 */
	@Test
	public void configureDCB_withFlowControlXONXOFFInOut() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, FlowControl_XONXOFF_In_Out, IGNORE, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(RTS_CONTROL_ENABLE));
		assertThat(dcb.fOutxCtsFlow, is(0));
		assertThat(dcb.fOutX, is(1));
		assertThat(dcb.fInX, is(1));
	}

	/**
	 * Verifies that the RTS (true) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withRTS_true() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, IGNORE_FLOWCONTROL, true, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(1));
	}

	/**
	 * Verifies that the RTS (false) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withRTS_false() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, IGNORE_FLOWCONTROL, false, IGNORE);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fRtsControl, is(1)); // this must be 1 because the flow control value
											// overrides it
	}

	/**
	 * Verifies that the DTR (true) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withDTR_true() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, true);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fDtrControl, is(1));
	}

	/**
	 * Verifies that the DTR (false) from the serial port settings are set on the DCB struct.
	 */
	@Test
	public void configureDCB_withDTR_false() {
		mockSerialPortSettings(IGNORE_BAUDS, IGNORE_DATABITS, IGNORE_STOPBITS, IGNORE_PARITY, IGNORE_FLOWCONTROL, IGNORE, false);

		configurator.configureDCB(dcb, settings);

		assertThat(dcb.fDtrControl, is(0));
	}

	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Mocks the values of a {@link SerialPortSettings}.
	 */
	private void mockSerialPortSettings(int bauds, DataBits dataBits, StopBits stopBits, Parity parity, FlowControl flowControl, boolean rts, boolean dtr) {
		when(settings.getBauds()).thenReturn(bauds);
		when(settings.getParity()).thenReturn(parity);
		when(settings.getDataBits()).thenReturn(dataBits);
		when(settings.getStopBits()).thenReturn(stopBits);
		when(settings.getFlowControl()).thenReturn(flowControl);
		when(settings.isRTS()).thenReturn(rts);
		when(settings.isDTR()).thenReturn(dtr);
	}
}
