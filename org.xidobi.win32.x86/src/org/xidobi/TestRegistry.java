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

			int status2 = OS.RegCloseKey(phkResult);
			System.err.println(status2);
		}

	}
}
