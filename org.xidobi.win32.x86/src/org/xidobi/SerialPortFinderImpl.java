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
import static org.xidobi.WinApi.ERROR_SUCCESS;
import static org.xidobi.WinApi.HKEY_LOCAL_MACHINE;
import static org.xidobi.WinApi.KEY_READ;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

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
	private WinApi win;

	/**
	 * Creates a new instance, that finds all serial ports that are available in the Windows
	 * Registry.
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public SerialPortFinderImpl(@Nonnull WinApi win) {
		this.win = checkArgumentNotNull(win, "win");
	}

	/** {@inheritDoc} */
	public Set<SerialPortInfo> find() {
		HKEY keyHandle = new HKEY(win);
		try {
			openRegistry(keyHandle);
			try {
				return getPortsFromRegistry(keyHandle);
			}
			finally {
				win.RegCloseKey(keyHandle);
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
		int status = win.RegOpenKeyExA(HKEY_LOCAL_MACHINE, HARDWARE_DEVICEMAP_SERIALCOMM, 0, KEY_READ, phkResult);
		if (status != ERROR_SUCCESS)
			throw newNativeCodeException(win, "Couldn't open Windows Registry for subkey >" + HARDWARE_DEVICEMAP_SERIALCOMM + "<!", status);
	}

	/**
	 * Returns a {@link Set} with informations of the serial ports that are available in the Windows
	 * Registry.
	 */
	private Set<SerialPortInfo> getPortsFromRegistry(HKEY phkResult) {
		Set<SerialPortInfo> ports = new HashSet<SerialPortInfo>();

		byte[] registryKey = new byte[255]; // port description
		INT sizeOfKey = new INT(); // size of the port description

		byte[] registryValue = new byte[255]; // port name
		INT sizeOfValue = new INT(); // size of the port name

		for (int index = 0; index < MAX_VALUE; index++) {
			sizeOfKey.value = 255;
			sizeOfValue.value = 255;

			int status = win.RegEnumValueA(phkResult, index, registryKey, sizeOfKey, 0, new INT(), registryValue, sizeOfValue);
			if (status != ERROR_SUCCESS)
				break;

			String portName = new String(registryValue, 0, sizeOfValue.value - 1);
			String description = new String(registryKey, 0, sizeOfKey.value);
			SerialPortInfo serialPort = new SerialPortInfo(portName, description);
			ports.add(serialPort);
		}

		return ports;
	}

}
