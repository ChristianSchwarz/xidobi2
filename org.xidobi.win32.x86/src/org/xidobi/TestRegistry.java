/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 09.01.2013 15:19:30
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi;

import static org.xidobi.OS.ERROR_SUCCESS;
import static org.xidobi.registry.Registry.HKEY_LOCAL_MACHINE;
import static org.xidobi.registry.Registry.KEY_READ;
import static org.xidobi.registry.Registry.RegCloseKey;
import static org.xidobi.registry.Registry.RegEnumValue;
import static org.xidobi.registry.Registry.RegOpenKey;
import static org.xidobi.registry.Registry.RegQueryValueEx;

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
		int[] result = RegOpenKey(HKEY_LOCAL_MACHINE, "HARDWARE\\DEVICEMAP\\SERIALCOMM".getBytes(), KEY_READ);
		int hkey = result[0];
		if (result[1] == ERROR_SUCCESS) {
			System.out.println("Success");

			byte[] value;
			int i = 0;
			while ((value = RegEnumValue(hkey, i, 255)) != null) {
				byte[] data = RegQueryValueEx(hkey, value);
				System.out.println(new String(value) + "= " + new String(data));
				i++;
			}

			RegCloseKey(hkey);
		}
	}
}
