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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;

/**
 * Abstract class for integration tests, which provides available serial ports that can be
 * configured via the properties file 'setupTests.properties'.
 * 
 * @author Tobias Breßler
 */
public abstract class AbstractIntegrationTest {

	/** Name of the properties file. */
	private static final String PROPERTIES_FILE = "setupTests.properties";
	/** Attribute for the available serial ports. */
	private static final String ATTR_AVAILABLE_PORT = "availableSerialPort";

	/** A serial port that is available on the system. */
	private String availableSerialPort;

	@Before
	@SuppressWarnings("javadoc")
	public final void before() {
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream(PROPERTIES_FILE));
			availableSerialPort = prop.getProperty(ATTR_AVAILABLE_PORT);
			assertThat(availableSerialPort, is(notNullValue()));
		}
		catch (IOException e) {
			fail("Couldn't load file >setupTests.properties<!");
		}

		setUp();
	}

	/**
	 * Here you can do the set up of your test.
	 */
	protected void setUp() {}

	/**
	 * Returns a serial port that is available on the system. The available port can be configured
	 * via the properties file 'setupTests.properties'.
	 * 
	 * @return serial port name
	 */
	protected final String getAvailableSerialPort() {
		return availableSerialPort;
	}
}
