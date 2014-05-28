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

import static org.xidobi.DataBits.DATABITS_5;
import static org.xidobi.DataBits.DATABITS_6;
import static org.xidobi.DataBits.DATABITS_7;
import static org.xidobi.DataBits.DATABITS_8;
import static org.xidobi.FlowControl.FLOWCONTROL_NONE;
import static org.xidobi.Parity.PARITY_NONE;
import static org.xidobi.StopBits.STOPBITS_1;
import static org.xidobi.StopBits.STOPBITS_2;
import static org.xidobi.spi.Preconditions.checkArgument;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Specifies the control settings for a serial port.
 * <p>
 * <b>Usage:</b>
 * <p>
 * <code>
 * SerialPortSettings settings;</br>
 * settings = SerialPortSettings.from9600_8N1().dataBits(DataBits_6).create();
 * </code>
 * <p>
 * This creates control settings with the following values:
 * <ul>
 * <li>bauds = 9600 (as configured)</li>
 * <li>data bits = 6 (as configured)</li>
 * <li>stop bits = 1 (default)</li>
 * <li>parity = none (default)</li>
 * <li>flow control = none (default)</li>
 * <li>RTS = true (default)</li>
 * <li>DTR = true (default)</li>
 * </ul>
 * 
 * @author Tobias Bre�ler
 * 
 * @see DataBits
 * @see StopBits
 * @see Parity
 * @see FlowControl
 */
@Immutable
public class SerialPortSettings {

	/**
	 * A builder for serial port settings.
	 * 
	 * @author Tobias Bre�ler
	 */
	public static final class SerialPortSettingsBuilder {

		/** the baud rate */
		private int bauds = 9600;
		/** the data bits, default is {@link DataBits#DATABITS_8} */
		private DataBits dataBits = DATABITS_8;
		/** the stop bits, default is {@link StopBits#STOPBITS_1} */
		private StopBits stopBits = STOPBITS_1;
		/** the parity, default is {@link Parity#PARITY_NONE} */
		private Parity parity = PARITY_NONE;
		/** the flow control, default is {@link FlowControl#FLOWCONTROL_NONE} */
		private FlowControl flowControl = FLOWCONTROL_NONE;
		/** the RTS (Request To Send) */
		private boolean rts = true;
		/** the DRT (Data Terminal Ready) */
		private boolean dtr = true;

		/** Creates a builder for serial port settings. */
		private SerialPortSettingsBuilder() {
		}

		/**
		 * Sets the baud rate.
		 * 
		 * @param bauds
		 *            the baud rate, must be greater than 0
		 * @return {@code this}
		 */
		@Nonnull
		public SerialPortSettingsBuilder bauds(@Nonnegative int bauds) {
			checkArgument(bauds > 0, "bauds", "Baud rate must be greater than 0!");
			this.bauds = bauds;
			return this;
		}

		/**
		 * Sets the data bits to the given value.
		 * <p>
		 * <i><b>Hint:</b> Under Windows the use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7,
		 * or 8 data bits with 1.5 stop bits.</i>
		 * 
		 * @param dataBits
		 *            the data bits, must not be <code>null</code>
		 * @return {@code this}
		 */
		@Nonnull
		public SerialPortSettingsBuilder set(@Nonnull DataBits dataBits) {
			this.dataBits = checkArgumentNotNull(dataBits, "dataBits");
			return this;
		}

		/**
		 * Sets the stop bits to the given value.
		 * <p>
		 * <i><b>Hint:</b> Under Windows the use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7,
		 * or 8 data bits with 1.5 stop bits.</i>
		 * 
		 * @param stopBits
		 *            the stop bits, must not be <code>null</code>
		 * @return {@code this}
		 */
		@Nonnull
		public SerialPortSettingsBuilder set(@Nonnull StopBits stopBits) {
			this.stopBits = checkArgumentNotNull(stopBits, "stopBits");
			return this;
		}

		/**
		 * Sets the parity to the given value.
		 * 
		 * @param parity
		 *            the parity, must not be <code>null</code>
		 * @return {@code this}
		 */
		@Nonnull
		public SerialPortSettingsBuilder set(@Nonnull Parity parity) {
			this.parity = checkArgumentNotNull(parity, "parity");
			return this;
		}

		/**
		 * Sets the flow control to the given value.
		 * 
		 * @param flowControl
		 *            the flow control, must not be <code>null</code>
		 * @return {@code this}
		 */
		@Nonnull
		public SerialPortSettingsBuilder set(@Nonnull FlowControl flowControl) {
			this.flowControl = checkArgumentNotNull(flowControl, "flowControl");
			return this;
		}

		/**
		 * Sets the RTS (Request To Send) to the given value.
		 * 
		 * @param rts
		 *            the RTS:
		 *            <ul>
		 *            <li> <code>true</code> turns RTS on
		 *            <li> <code>false</code> turns RTS off
		 *            </ul>
		 * @return {@code this}
		 */
		@Nonnull
		public SerialPortSettingsBuilder rts(boolean rts) {
			this.rts = rts;
			return this;
		}

		/**
		 * Sets the DTR (Data Terminal Ready) to the given value.
		 * 
		 * @param dtr
		 *            the DTR:
		 *            <ul>
		 *            <li> <code>true</code> turns DTR on
		 *            <li> <code>false</code> turns DTR off
		 *            </ul>
		 * @return {@code this}
		 */
		@Nonnull
		public SerialPortSettingsBuilder dtr(boolean dtr) {
			this.dtr = dtr;
			return this;
		}

		/**
		 * Creates and returns the serial port settings, specified by the current builder.
		 * 
		 * @return the serial port settings, never <code>null</code>
		 */
		@Nonnull
		public SerialPortSettings create() {
			return new SerialPortSettings(bauds, dataBits, stopBits, parity, flowControl, rts, dtr);
		}

	}

	/** the baud rate */
	private final int bauds;
	/** the data bits */
	private final DataBits dataBits;
	/** the stop bits */
	private final StopBits stopBits;
	/** the parity */
	private final Parity parity;
	/** the flow control */
	private final FlowControl flowControl;
	/** the RTS (Request To Send) */
	private final boolean rts;
	/** the DRT (Data Terminal Ready) */
	private final boolean dtr;

	/**
	 * Creates a serial port setting with the given values.
	 * 
	 * @exception IllegalArgumentException
	 *                Under Windows the use of 5 data bits with 2 stop bits is an invalid combination, as is 6, 7, or 8
	 *                data bits with 1.5 stop bits.
	 */
	private SerialPortSettings(@Nonnegative int bauds, @Nonnull DataBits dataBits, @Nonnull StopBits stopBits, @Nonnull Parity parity, @Nonnull FlowControl flowControl, boolean rts, boolean dtr) {
		checkArgument(!(dataBits==DATABITS_5 && stopBits==STOPBITS_2),"The use of 5 data bits with 2 stop bits is an invalid combination!");
		checkArgument(!((dataBits==DATABITS_6 || dataBits==DATABITS_7 || dataBits==DATABITS_8) && stopBits==StopBits.STOPBITS_1_5),"The use of 6, 7, or 8 data bits with 1.5 stop bits is an invalid combination!");
		
		this.bauds = bauds;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.flowControl = flowControl;
		this.rts = rts;
		this.dtr = dtr;
	}

	/**
	 * Creates a builder for the serial port settings. The initial values of the port settings are 8/N/1 with 9600
	 * bauds:
	 * <ul>
	 * <li>bauds = 9600</li>
	 * <li>data bits = 8</li>
	 * <li>parity = none</li>
	 * <li>stop bits = 1</li>
	 * <li>flow control = none</li>
	 * <li>RTS = true (default)</li>
	 * <li>DTR = true (default)</li>
	 * </ul>
	 * 
	 * @return a new builder for the serial port settings, never <code>null</code>
	 */
	@Nonnull
	public static SerialPortSettingsBuilder from9600bauds8N1() {
		return new SerialPortSettingsBuilder();
	}

	/**
	 * Returns the bauds.
	 * 
	 * @return the bauds
	 */
	@Nonnegative
	public int getBauds() {
		return bauds;
	}

	/**
	 * Returns the data bits.
	 * 
	 * @return the data bits, never <code>null</code>
	 */
	@Nonnull
	public DataBits getDataBits() {
		return dataBits;
	}

	/**
	 * Returns the stop bits.
	 * 
	 * @return the stop bits, never <code>null</code>
	 */
	@Nonnull
	public StopBits getStopBits() {
		return stopBits;
	}

	/**
	 * Returns the parity.
	 * 
	 * @return the parity, never <code>null</code>
	 */
	@Nonnull
	public Parity getParity() {
		return parity;
	}

	/**
	 * Returns the flow control.
	 * 
	 * @return the flow control, never <code>null</code>
	 */
	@Nonnull
	public FlowControl getFlowControl() {
		return flowControl;
	}

	/**
	 * Returns the RTS (Request To Send).
	 * 
	 * @return the RTS
	 */
	public boolean isRTS() {
		return rts;
	}

	/**
	 * Returns the DTR (Data Terminal Ready).
	 * 
	 * @return the DTR
	 */
	public boolean isDTR() {
		return dtr;
	}

	@Override
	public String toString() {
		return "SerialPortSettings [bauds=" + bauds + ", dataBits=" + dataBits + ", stopBits=" + stopBits + ", parity=" + parity + ", flowControl=" + flowControl + ", rts=" + rts + ", dtr=" + dtr + "]";
	}
}
