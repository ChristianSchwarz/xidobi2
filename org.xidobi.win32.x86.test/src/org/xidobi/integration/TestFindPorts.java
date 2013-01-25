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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.xidobi.OS.OS;

import java.util.Set;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.xidobi.SerialPortFinderImpl;
import org.xidobi.SerialPortInfo;

/**
 * Integration test for class {@link SerialPortFinderImpl}.
 * <p>
 * <b>Please note:</b> Edit file <i>setupIntegrationTests.properties</i> to setup this integration
 * test for the system it is running on. This test can only be successful when it is executed under
 * Windows.
 * 
 * @author Tobias Breﬂler
 */
public class TestFindPorts extends AbstractIntegrationTest {

	/**
	 * Verifies that {@link SerialPortFinderImpl#find()} never returns a <code>null</code> value and
	 * never throws an exception.
	 */
	@Test(timeout = 1500)
	public void findSerialPortsLoop() {
		for (int i = 0; i < 10_000; i++) {
			SerialPortFinderImpl finder = new SerialPortFinderImpl(OS);
			Set<SerialPortInfo> result = finder.find();
			assertThat(result, is(notNullValue()));
			assertThat(result, hasItem(portInfoWith(getAvailableSerialPort())));
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Returns a Matcher that verifies the portName of a {@link SerialPortInfo}. */
	private TypeSafeMatcher<SerialPortInfo> portInfoWith(final String portName) {
		return new CustomTypeSafeMatcher<SerialPortInfo>("a serial port info with portName >" + portName + "<") {
			@Override
			protected boolean matchesSafely(SerialPortInfo actual) {
				if (!actual.getPortName().equals(portName))
					return false;
				return true;
			}
		};
	}

}
