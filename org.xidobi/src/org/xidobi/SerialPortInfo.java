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

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

/**
 * Delivers the information for a single serial port.
 * 
 * @author Tobias Breﬂler
 */
public class SerialPortInfo {

	/** the name of the serial port, never <code>null</code> */
	private final String portName;
	/**
	 * the description for the port, can be <code>null</code> if no description is available
	 */
	private final String description;

	/**
	 * Creates a new serial port info object.
	 * 
	 * @param portName
	 *            the name of the serial port, must not be <code>null</code>
	 * @param description
	 *            the additional description for the port, can be <code>null</code>
	 */
	public SerialPortInfo(	String portName,
							String description) {
		this.portName = checkArgumentNotNull(portName, "portName");
		this.description = description;
	}

	/**
	 * Returns the name of the serial port.
	 * 
	 * @return the name of the serial port, never <code>null</code>
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * Returns the additional description for the serial port.
	 * 
	 * @return the description for the serial port, can be <code>null</code> if no description is
	 *         available
	 */
	public String getDescription() {
		return description;
	}

}
