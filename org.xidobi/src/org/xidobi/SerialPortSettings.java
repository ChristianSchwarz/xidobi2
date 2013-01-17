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
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

/**
 * The control settings for a serial port.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
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
	 * @author Tobias Breﬂler
	 */
	public static final class SerialPortSettingsBuilder {

		private final int bauds;
		private DataBits dataBits = DataBits_8;
		private StopBits stopBits = StopBits_1;
		private Parity parity = Parity_None;
		private FlowControl flowControl = FlowControl_None;

		/**
		 * Creates a builder for serial port settings.
		 * 
		 * @param bauds
		 *            the baud rate
		 */
		private SerialPortSettingsBuilder(int bauds) {
			this.bauds = bauds;
		}

		/**
		 * Sets the data bits to the given value.
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
		 * 
		 * @return
		 */
		public SerialPortSettings create() {
			return new SerialPortSettings(bauds, dataBits, stopBits, parity, flowControl);
		}

	}

	private final int bauds;

	private final DataBits dataBits;
	private final StopBits stopBits;
	private final Parity parity;
	private final FlowControl flowControl;

	private SerialPortSettings(	int bauds,
								DataBits dataBits,
								StopBits stopBits,
								Parity parity,
								FlowControl flowControl) {
		this.bauds = bauds;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.flowControl = flowControl;
	}

	/**
	 * Creates a new builder for the serial port settings.
	 * 
	 * TODO describe the initial values
	 * 
	 * @param bauds
	 *            the baud rate
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
	 * @return the data bits
	 */
	public DataBits getDataBits() {
		return dataBits;
	}

	/**
	 * @return the stop bits
	 */
	public StopBits getStopBits() {
		return stopBits;
	}

	/**
	 * @return the parity
	 */
	public Parity getParity() {
		return parity;
	}

	/**
	 * @return the flow control
	 */
	public FlowControl getFlowControl() {
		return flowControl;
	}
}
