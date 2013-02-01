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

import static org.xidobi.StopBits.StopBits_1_5;
import static org.xidobi.StopBits.StopBits_2;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;
import static org.xidobi.structs.DCB.DTR_CONTROL_DISABLE;
import static org.xidobi.structs.DCB.DTR_CONTROL_ENABLE;
import static org.xidobi.structs.DCB.EVENPARITY;
import static org.xidobi.structs.DCB.MARKPARITY;
import static org.xidobi.structs.DCB.NOPARITY;
import static org.xidobi.structs.DCB.ODDPARITY;
import static org.xidobi.structs.DCB.ONE5STOPBITS;
import static org.xidobi.structs.DCB.ONESTOPBIT;
import static org.xidobi.structs.DCB.RTS_CONTROL_DISABLE;
import static org.xidobi.structs.DCB.RTS_CONTROL_ENABLE;
import static org.xidobi.structs.DCB.RTS_CONTROL_HANDSHAKE;
import static org.xidobi.structs.DCB.SPACEPARITY;
import static org.xidobi.structs.DCB.TWOSTOPBITS;

import javax.annotation.Nonnull;

import org.xidobi.structs.DCB;

/**
 * Configures the native DCB "struct" with the values from the {@link SerialPortSettings}.
 * 
 * @author Tobias Breﬂler
 * 
 * @see DCB
 * @see SerialPortSettings
 */
public class DCBConfigurator {

	/** <code>int</code> value for <code>true</code> */
	private static final int TRUE = 1;
	/** <code>int</code> value for <code>false</code> */
	private static final int FALSE = 0;

	/**
	 * Configures the native DCB "struct" with the values from the given serial port settings.
	 * 
	 * @param dcb
	 *            the DCB "struct" that should be configured, must not be <code>null</code>
	 * @param settings
	 *            the serial port settings, must not be <code>null</code>
	 * @throws IllegalArgumentException
	 *             <ul>
	 *             <li>if <code>dcb == null</code></li>
	 *             <li>if <code>settings == null</code></li>
	 *             <li>if the serial port settings contains illegal value combinations: <i>The use
	 *             of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8 data
	 *             bits with 1.5 stop bits.</i></li>
	 *             </ul>
	 */
	public void configureDCB(@Nonnull DCB dcb, @Nonnull SerialPortSettings settings) throws IllegalArgumentException {
		checkArgumentNotNull(dcb, "dcb");
		checkArgumentNotNull(settings, "settings");

		checkPortSettings(settings);

		configureBaudRate(dcb, settings);
		configureDataBits(dcb, settings);
		configureStopBits(dcb, settings);
		configureParity(dcb, settings);

		configureRTS(dcb, settings);
		configureDTR(dcb, settings);

		configureFlowControl(dcb, settings);

		configureFixValues(dcb);
	}

	/**
	 * Checks the serial port settings for invalid combinations.
	 * <p>
	 * <i>The use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8 data
	 * bits with 1.5 stop bits.</i>
	 */
	private void checkPortSettings(SerialPortSettings settings) throws IllegalArgumentException {
		DataBits dataBits = settings.getDataBits();
		StopBits stopBits = settings.getStopBits();
		switch (dataBits) {
			case DataBits_5:
				if (stopBits == StopBits_2)
					throw new IllegalArgumentException("Invalid serial port settings! The use of 2 stop bits with 5 data bits is an invalid combination.");
				return;
			case DataBits_6:
			case DataBits_7:
			case DataBits_8:
				if (stopBits == StopBits_1_5)
					throw new IllegalArgumentException("Invalid serial port settings! The use of 1.5 stop bits with 6, 7 or 8 data bits is an invalid combination.");
				return;
			default:
				return;
		}
	}

	/** Configures the baud rate on the DCB "struct". */
	private void configureBaudRate(DCB dcb, SerialPortSettings settings) {
		dcb.BaudRate = settings.getBauds();
	}

	/** Configures the data bits on the DCB "struct". */
	private void configureDataBits(DCB dcb, SerialPortSettings settings) {
		switch (settings.getDataBits()) {
			case DataBits_5:
				dcb.ByteSize = 5;
				return;
			case DataBits_6:
				dcb.ByteSize = 6;
				return;
			case DataBits_7:
				dcb.ByteSize = 7;
				return;
			case DataBits_8:
				dcb.ByteSize = 8;
				return;
			case DataBits_9:
				dcb.ByteSize = 9;
				return;
		}
	}

	/** Configures the stop bits on the DCB "struct". */
	private void configureStopBits(DCB dcb, SerialPortSettings settings) {
		switch (settings.getStopBits()) {
			case StopBits_1:
				dcb.StopBits = ONESTOPBIT;
				return;
			case StopBits_1_5:
				dcb.StopBits = ONE5STOPBITS;
				return;
			case StopBits_2:
				dcb.StopBits = TWOSTOPBITS;
				return;
		}
	}

	/** Configures the parity on the DCB "struct". */
	private void configureParity(DCB dcb, SerialPortSettings settings) {
		switch (settings.getParity()) {
			case Parity_None:
				dcb.Parity = NOPARITY;
				return;
			case Parity_Even:
				dcb.Parity = EVENPARITY;
				return;
			case Parity_Odd:
				dcb.Parity = ODDPARITY;
				return;
			case Parity_Mark:
				dcb.Parity = MARKPARITY;
				return;
			case Parity_Space:
				dcb.Parity = SPACEPARITY;
				return;
		}
	}

	/** Configures the RTS on the DCB "struct". */
	private void configureRTS(DCB dcb, SerialPortSettings settings) {
		if (settings.isRTS())
			dcb.fRtsControl = RTS_CONTROL_ENABLE;
		else
			dcb.fRtsControl = RTS_CONTROL_DISABLE;
	}

	/** Configures the DTR on the DCB "struct". */
	private void configureDTR(DCB dcb, SerialPortSettings settings) {
		if (settings.isDTR())
			dcb.fDtrControl = DTR_CONTROL_ENABLE;
		else
			dcb.fDtrControl = DTR_CONTROL_DISABLE;
	}

	/** Configures the flow control on the DCB "struct". */
	private void configureFlowControl(DCB dcb, SerialPortSettings settings) {

		// Reset flow control settings:
		dcb.fRtsControl = RTS_CONTROL_ENABLE;
		dcb.fOutxCtsFlow = FALSE;
		dcb.fOutX = FALSE;
		dcb.fInX = FALSE;

		switch (settings.getFlowControl()) {
			case FlowControl_None:
				return;
			case FlowControl_RTSCTS_In:
				dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;
				return;
			case FlowControl_RTSCTS_Out:
				dcb.fOutxCtsFlow = TRUE;
				return;
			case FlowControl_RTSCTS_In_Out:
				dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;
				dcb.fOutxCtsFlow = TRUE;
				return;
			case FlowControl_XONXOFF_In:
				dcb.fInX = TRUE;
				return;
			case FlowControl_XONXOFF_Out:
				dcb.fOutX = TRUE;
				return;
			case FlowControl_XONXOFF_In_Out:
				dcb.fInX = TRUE;
				dcb.fOutX = TRUE;
				return;
		}
	}

	/** Resets the other values to default. */
	private void configureFixValues(DCB dcb) {
		dcb.fOutxDsrFlow = FALSE;
		dcb.fDsrSensitivity = FALSE;
		dcb.fTXContinueOnXoff = TRUE;
		dcb.fErrorChar = FALSE;
		dcb.fNull = FALSE;
		dcb.fAbortOnError = FALSE;
		dcb.XonLim = 2048;
		dcb.XoffLim = 512;
		dcb.XonChar = (char) 17; // DC1
		dcb.XoffChar = (char) 19; // DC3
	}
}
