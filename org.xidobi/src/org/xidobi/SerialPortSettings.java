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

import static org.xidobi.DataBits.DataBits_8;
import static org.xidobi.FlowControl.FlowControl_None;
import static org.xidobi.Parity.Parity_None;
import static org.xidobi.StopBits.StopBits_1;
import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

/**
 * Specifies the control settings for a serial port.
 * <p>
 * <b>Usage:</b>
 * <p>
 * <code>
 * SerialPortSettings settings;</br>
 * settings = SerialPortSettings.bauds(9600).dataBits(DataBits.DataBits_6).create();
 * </code>
 * <p>
 * This creates control settings with the following values:
 * <ul>
 * <li>bauds = 96000 (as configured)</li>
 * <li>dataBits = 6 (as configured)</li>
 * <li>stopBits = 1 (default)</li>
 * <li>parity = none (default)</li>
 * <li>flowControl = none (default)</li>
 * <li>RTS = true (default)</li>
 * <li>DTR = true (default)</li>
 * </ul>
 * 
 * @author Tobias Breßler
 * 
 * @see DataBits
 * @see StopBits
 * @see Parity
 * @see FlowControl
 */
public class SerialPortSettings {

	/**
	 * A builder for serial port settings.
	 * 
	 * @author Tobias Breßler
	 */
	public static final class SerialPortSettingsBuilder {

		/** the baud rate */
		private final int bauds;
		/** the data bits, default is {@link DataBits#DataBits_8} */
		private DataBits dataBits = DataBits_8;
		/** the stop bits, default is {@link StopBits#StopBits_1} */
		private StopBits stopBits = StopBits_1;
		/** the parity, default is {@link Parity#Parity_None} */
		private Parity parity = Parity_None;
		/** the flow control, default is {@link FlowControl#FlowControl_None} */
		private FlowControl flowControl = FlowControl_None;
		/** the RTS (Request To Send) */
		private boolean rts = true;
		/** the DRT (Data Terminal Ready) */
		private boolean dtr = true;

		/**
		 * Creates a builder for serial port settings.
		 * 
		 * @param bauds
		 *            the baud rate, must be greater than 0
		 */
		private SerialPortSettingsBuilder(int bauds) {
			checkArgument(bauds > 0, "bauds", "Baud rate must be greater than 0!");
			this.bauds = bauds;
		}

		/**
		 * Sets the data bits to the given value.
		 * <p>
		 * <i><b>Hint:</b> Under Windows the use of 5 data bits with 2 stop bits is an invalid
		 * combination, as is 6, 7, or 8 data bits with 1.5 stop bits.</i>
		 * 
		 * @param dataBits
		 *            the data bits, must not be <code>null</code>
		 * @return the current builder for the serial port settings, never <code>null</code>
		 */
		public SerialPortSettingsBuilder dataBits(DataBits dataBits) {
			this.dataBits = checkArgumentNotNull(dataBits, "dataBits");
			return this;
		}

		/**
		 * Sets the stop bits to the given value.
		 * <p>
		 * <i><b>Hint:</b> Under Windows the use of 5 data bits with 2 stop bits is an invalid
		 * combination, as is 6, 7, or 8 data bits with 1.5 stop bits.</i>
		 * 
		 * @param stopBits
		 *            the stop bits, must not be <code>null</code>
		 * @return the current builder for the serial port settings, never <code>null</code>
		 */
		public SerialPortSettingsBuilder stopBits(StopBits stopBits) {
			this.stopBits = checkArgumentNotNull(stopBits, "stopBits");
			return this;
		}

		/**
		 * Sets the parity to the given value.
		 * 
		 * @param parity
		 *            the parity, must not be <code>null</code>
		 * @return the current builder for the serial port settings, never <code>null</code>
		 */
		public SerialPortSettingsBuilder parity(Parity parity) {
			this.parity = checkArgumentNotNull(parity, "parity");
			return this;
		}

		/**
		 * Sets the flow control to the given value.
		 * 
		 * @param flowControl
		 *            the flow control, must not be <code>null</code>
		 * @return the current builder for the serial port settings, never <code>null</code>
		 */
		public SerialPortSettingsBuilder flowControl(FlowControl flowControl) {
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
		 * @return the current builder for the serial port settings, never <code>null</code>
		 */
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
		 * @return the current builder for the serial port settings, never <code>null</code>
		 */
		public SerialPortSettingsBuilder dtr(boolean dtr) {
			this.dtr = dtr;
			return this;
		}

		/**
		 * Creates and returns the serial port settings.
		 * 
		 * @return the serial port settings, never <code>null</code>
		 */
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
	 */
	private SerialPortSettings(	int bauds,
								DataBits dataBits,
								StopBits stopBits,
								Parity parity,
								FlowControl flowControl,
								boolean rts,
								boolean dtr) {
		this.bauds = bauds;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.flowControl = flowControl;
		this.rts = rts;
		this.dtr = dtr;
	}

	/**
	 * Creates a builder for the serial port settings with the given baud rate. The initial values
	 * of the port settings are (8/N/1):
	 * <ul>
	 * <li>dataBits = 8</li>
	 * <li>parity = none</li>
	 * <li>stopBits = 1</li>
	 * <li>flowControl = none</li>
	 * <li>RTS = true (default)</li>
	 * <li>DTR = true (default)</li>
	 * </ul>
	 * 
	 * @param bauds
	 *            the baud rate, must be greater than 0
	 * @return a new builder for the serial port settings, never <code>null</code>
	 */
	public static SerialPortSettingsBuilder bauds(int bauds) {
		return new SerialPortSettingsBuilder(bauds);
	}

	/**
	 * Returns the bauds.
	 * 
	 * @return the bauds
	 */
	public int getBauds() {
		return bauds;
	}

	/**
	 * Returns the data bits.
	 * 
	 * @return the data bits, never <code>null</code>
	 */
	public DataBits getDataBits() {
		return dataBits;
	}

	/**
	 * Returns the stop bits.
	 * 
	 * @return the stop bits, never <code>null</code>
	 */
	public StopBits getStopBits() {
		return stopBits;
	}

	/**
	 * Returns the parity.
	 * 
	 * @return the parity, never <code>null</code>
	 */
	public Parity getParity() {
		return parity;
	}

	/**
	 * Returns the flow control.
	 * 
	 * @return the flow control, never <code>null</code>
	 */
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
}
