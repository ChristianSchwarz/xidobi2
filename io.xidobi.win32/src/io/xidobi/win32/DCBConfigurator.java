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
package io.xidobi.win32;

import static org.xidobi.StopBits.STOPBITS_1_5;
import static org.xidobi.StopBits.STOPBITS_2;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import javax.annotation.Nonnull;

import org.xidobi.DataBits;
import org.xidobi.SerialPortSettings;
import org.xidobi.StopBits;

import com.sun.jna.platform.win32.WinBase.DCB;
import static com.sun.jna.platform.win32.WinBase.DCB.*;
/**
 * Configures the native {@link DCB} with the values from the {@link SerialPortSettings}.
 * Additionally it verifies the settings to be valid.
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
	 * Configures the native {@link DCB} with the values from the given serial port settings.
	 * 
	 * @param dcb
	 *            the {@link DCB} that should be configured, must not be <code>null</code>
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
			case DATABITS_5:
				if (stopBits == STOPBITS_2)
					throw new IllegalArgumentException("Invalid serial port settings! The use of 2 stop bits with 5 data bits is an invalid combination.");
				return;
			case DATABITS_6:
			case DATABITS_7:
			case DATABITS_8:
				if (stopBits == STOPBITS_1_5)
					throw new IllegalArgumentException("Invalid serial port settings! The use of 1.5 stop bits with 6, 7 or 8 data bits is an invalid combination.");
				return;
			default:
				return;
		}
	}

	/** Configures the baud rate on the {@link DCB}. */
	private void configureBaudRate(DCB dcb, SerialPortSettings settings) {
		dcb.BaudRate = settings.getBauds();
	}

	/** Configures the data bits on the {@link DCB}. */
	private void configureDataBits(DCB dcb, SerialPortSettings settings) {
		switch (settings.getDataBits()) {
			case DATABITS_5:
				dcb.ByteSize = 5;
				return;
			case DATABITS_6:
				dcb.ByteSize = 6;
				return;
			case DATABITS_7:
				dcb.ByteSize = 7;
				return;
			case DATABITS_8:
				dcb.ByteSize = 8;
				return;
			case DATABITS_9:
				dcb.ByteSize = 9;
				return;
		}
	}

	/** Configures the stop bits on the {@link DCB}. */
	private void configureStopBits(DCB dcb, SerialPortSettings settings) {
		switch (settings.getStopBits()) {
			case STOPBITS_1:
				dcb.StopBits = ONESTOPBIT;
				return;
			case STOPBITS_1_5:
				dcb.StopBits = ONE5STOPBITS;
				return;
			case STOPBITS_2:
				dcb.StopBits = TWOSTOPBITS;
				return;
		}
	}

	/** Configures the parity on the {@link DCB}. */
	private void configureParity(DCB dcb, SerialPortSettings settings) {
		switch (settings.getParity()) {
			case PARITY_NONE:
				dcb.Parity = NOPARITY;
				return;
			case PARITY_EVEN:
				dcb.Parity = EVENPARITY;
				return;
			case PARITY_ODD:
				dcb.Parity = ODDPARITY;
				return;
			case PARITY_MARK:
				dcb.Parity = MARKPARITY;
				return;
			case PARITY_SPACE:
				dcb.Parity = SPACEPARITY;
				return;
		}
	}

	/** Configures the RTS on the {@link DCB}. */
	private void configureRTS(DCB dcb, SerialPortSettings settings) {
		if (settings.isRTS())
			dcb.fRtsControl = RTS_CONTROL_ENABLE;
		else
			dcb.fRtsControl = RTS_CONTROL_DISABLE;
	}

	/** Configures the DTR on the {@link DCB}. */
	private void configureDTR(DCB dcb, SerialPortSettings settings) {
		if (settings.isDTR())
			dcb.fDtrControl = DTR_CONTROL_ENABLE;
		else
			dcb.fDtrControl = DTR_CONTROL_DISABLE;
	}

	/** Configures the flow control on the {@link DCB}. */
	private void configureFlowControl(DCB dcb, SerialPortSettings settings) {

		// reset the flow control settings:
		dcb.fRtsControl = RTS_CONTROL_ENABLE;
		dcb.fOutxCtsFlow = FALSE;
		dcb.fOutX = FALSE;
		dcb.fInX = FALSE;

		// set the flow control:
		switch (settings.getFlowControl()) {
			case FLOWCONTROL_NONE:
				return;
			case FLOWCONTROL_RTSCTS_IN:
				dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;
				return;
			case FLOWCONTROL_RTSCTS_OUT:
				dcb.fOutxCtsFlow = TRUE;
				return;
			case FLOWCONTROL_RTSCTS_IN_OUT:
				dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;
				dcb.fOutxCtsFlow = TRUE;
				return;
			case FLOWCONTROL_XONXOFF_IN:
				dcb.fInX = TRUE;
				return;
			case FLOWCONTROL_XONXOFF_OUT:
				dcb.fOutX = TRUE;
				return;
			case FLOWCONTROL_XONXOFF_IN_OUT:
				dcb.fInX = TRUE;
				dcb.fOutX = TRUE;
				return;
		}
	}

	/** Resets the other values to default. */
	private void configureFixValues(DCB dcb) {

		// NOTE: We configure the following values in the same way as they do in the jSSC project.
		// Please do not change these values, until you know any better.

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
