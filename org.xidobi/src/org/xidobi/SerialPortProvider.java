/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 25.01.2013 14:54:29
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import static java.lang.Thread.currentThread;

/**
 * @author Christian Schwarz
 * 
 */
public class SerialPortProvider {

	private static SerialPortFinder finder;

	static {
		ClassLoader cl = currentThread().getContextClassLoader();
		Class<?> finderClass;
		try {
			finderClass = cl.loadClass("org.xidobi.SerialPortFinderImpl");

			if (!SerialPortFinder.class.isAssignableFrom(finderClass))
				throw new ClassCastException(finderClass + " does not implement " + SerialPortFinder.class);

			finder = (SerialPortFinder) finderClass.newInstance();
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private SerialPortProvider() {}

	public static SerialPortFinder getSerialPortFinder() {
		return finder;
	}
}
