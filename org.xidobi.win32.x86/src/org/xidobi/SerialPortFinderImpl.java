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

import static java.lang.Integer.MAX_VALUE;
import static org.xidobi.OS.ERROR_SUCCESS;
import static org.xidobi.OS.HKEY_LOCAL_MACHINE;
import static org.xidobi.OS.KEY_READ;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.util.HashSet;
import java.util.Set;

import org.xidobi.structs.HKEY;
import org.xidobi.structs.INT;

/**
 * Implementation of the interface {@link SerialPortFinder}, that finds all serial ports that are
 * available in the Windows Registry.
 * 
 * @author Tobias Breﬂler
 * 
 * @see SerialPortFinder
 */
public class SerialPortFinderImpl implements SerialPortFinder {

	/** Subkey to the serial ports in the Windows Registry */
	private static final String HARDWARE_DEVICEMAP_SERIALCOMM = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";

	/** the native Win32-API, never <code>null</code> */
	private OS os;

	/**
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public SerialPortFinderImpl(OS os) {
		this.os = checkArgumentNotNull(os, "os");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPortFinder#find()
	 */
	public Set<SerialPortInfo> find() {
		HKEY keyHandle = new HKEY(os);
		try {
			openRegistry(keyHandle);
			try {
				return getPortsFromRegistry(keyHandle);
			}
			finally {
				os.RegCloseKey(keyHandle);
			}
		}
		finally {
			keyHandle.dispose();
		}
	}

	/**
	 * Opens the Windows Registry for subkey {@value #HARDWARE_DEVICEMAP_SERIALCOMM}.
	 */
	private void openRegistry(HKEY phkResult) {
		int status = os.RegOpenKeyExA(HKEY_LOCAL_MACHINE, HARDWARE_DEVICEMAP_SERIALCOMM, 0, KEY_READ, phkResult);
		if (status != ERROR_SUCCESS)
			throw new IllegalStateException("Couldn't open windows registry for subkey >" + HARDWARE_DEVICEMAP_SERIALCOMM + "<! (Error-Code: " + status + ")");
	}

	/**
	 * Returns a {@link Set} with informations of the serial ports that are available in the Windows
	 * Registry.
	 */
	private Set<SerialPortInfo> getPortsFromRegistry(HKEY phkResult) {
		Set<SerialPortInfo> ports = new HashSet<SerialPortInfo>();

		byte[] lpValueName = new byte[255];
		INT lpcchValueName = new INT();

		byte[] lpData = new byte[255];
		INT lpcbData = new INT();

		for (int dwIndex = 0; dwIndex < MAX_VALUE; dwIndex++) {
			lpcchValueName.value = 255;
			lpcbData.value = 255;

			int status = os.RegEnumValueA(phkResult, dwIndex, lpValueName, lpcchValueName, 0, new INT(), lpData, lpcbData);
			if (status != ERROR_SUCCESS)
				break;

			String portName = new String(lpData, 0, lpcbData.value - 1);
			String description = new String(lpValueName, 0, lpcchValueName.value);
			SerialPortInfo serialPort = new SerialPortInfo(portName, description);
			ports.add(serialPort);
		}

		return ports;
	}

}
