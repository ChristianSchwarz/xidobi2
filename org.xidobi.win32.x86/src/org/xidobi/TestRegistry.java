/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 09.01.2013 15:19:30
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi;

import static org.xidobi.OS.ERROR_SUCCESS;

import org.xidobi.structs.HKEY;
import org.xidobi.structs.INT;

/**
 * 
 * 
 * @author Tobias Breﬂler
 * 
 */
public class TestRegistry {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println("-- JNI -------");

		HKEY phkResult = new HKEY();
		int status = OS.RegOpenKeyExA(OS.HKEY_LOCAL_MACHINE, "HARDWARE\\DEVICEMAP\\SERIALCOMM\\", 0, OS.KEY_READ, phkResult);

		System.err.println(status);

		if (status == ERROR_SUCCESS) {
			System.out.println("Success");

			char[] lpValueName = new char[255];
			INT lpcchValueName = new INT(255);
			byte[] lpData = new byte[255];
			INT lpcbData = new INT(255);
			int regEnumValue = OS.RegEnumValueA(phkResult, 0, lpValueName, lpcchValueName, 0, new INT(), lpData, lpcbData);
			System.err.println(regEnumValue);
			System.out.println(new String(lpValueName, 0, lpcchValueName.value));
			System.out.println(new String(lpData, 0, lpcbData.value));

			int status2 = OS.RegCloseKey(phkResult);
			System.err.println(status2);

			phkResult.dispose();
		}

	}
}
