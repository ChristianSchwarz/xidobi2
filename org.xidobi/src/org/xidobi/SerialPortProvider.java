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

import static java.lang.Thread.currentThread;

/**
 * Provides the serial port finder implementation.
 * 
 * @author Christian Schwarz
 */
public class SerialPortProvider {

	/** instance of the {@link SerialPortFinder} */
	private static SerialPortFinder finder;

	static {
		ClassLoader cl = currentThread().getContextClassLoader();
		try {
			Class<?> finderClass = cl.loadClass("org.xidobi.SerialPortFinderImpl");

			if (!SerialPortFinder.class.isAssignableFrom(finderClass))
				throw new ClassCastException(finderClass + " does not implement " + SerialPortFinder.class);

			finder = (SerialPortFinder) finderClass.newInstance();
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/** This class can not be instantiated */
	private SerialPortProvider() {}

	/**
	 * Returns an instance of the {@link SerialPortFinder}.
	 * 
	 * @return instance of the {@link SerialPortFinder}
	 */
	public static SerialPortFinder getSerialPortFinder() {
		return finder;
	}
}
