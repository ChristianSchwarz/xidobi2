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
package org.xidobi.integration;

import static org.xidobi.OS.ERROR_SUCCESS;
import static org.xidobi.OS.HKEY_LOCAL_MACHINE;
import static org.xidobi.OS.KEY_READ;

import org.xidobi.OS;
import org.xidobi.structs.HKEY;
import org.xidobi.structs.INT;

/**
 * 
 * 
 * @author Tobias Breﬂler
 */
@Deprecated
public class TestRegistry {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OS os = OS.OS;

		HKEY phkResult = new HKEY(os);
		int status = os.RegOpenKeyExA(HKEY_LOCAL_MACHINE, "HARDWARE\\DEVICEMAP\\SERIALCOMM\\", 0, KEY_READ, phkResult);

		if (status == ERROR_SUCCESS) {
			int regEnumValue;
			int i = 0;
			for (;;) {
				byte[] lpValueName = new byte[255];
				INT lpcchValueName = new INT(255);
				byte[] lpData = new byte[255];
				INT lpcbData = new INT(255);
				regEnumValue = os.RegEnumValueA(phkResult, i, lpValueName, lpcchValueName, 0, new INT(), lpData, lpcbData);
				if (regEnumValue != ERROR_SUCCESS)
					break;
				System.out.println(new String(lpValueName, 0, lpcchValueName.value) + " = " + new String(lpData, 0, lpcbData.value));
				i++;
			}

			os.RegCloseKey(phkResult);

			phkResult.dispose();
		}

	}
}
