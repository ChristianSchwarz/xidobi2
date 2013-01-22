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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests the class {@link SerialPortInfo}.
 * 
 * @author Tobias Breﬂler
 */
public class TestSerialPortInfo {

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when
	 * <code>portName == null</code>.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullPortName() {
		new SerialPortInfo(null, "description");
	}

	/**
	 * Verifies that <b>no</b> {@link IllegalArgumentException} is thrown, when
	 * <code>description == null</code>.
	 */
	@Test
	@SuppressWarnings("unused")
	public void new_withNullDescription() {
		new SerialPortInfo("portName", null);
	}

	/**
	 * Verifies that {@link SerialPortInfo#getPortName()} returns the <code>portName</code> that was
	 * given to the constructor.
	 */
	@Test
	public void getPortName() {
		SerialPortInfo info = new SerialPortInfo("portName", "description");
		assertThat(info.getPortName(), is("portName"));
	}

	/**
	 * Verifies that {@link SerialPortInfo#getDescription()} returns the <code>description</code>
	 * that was given ton the constructor.
	 */
	@Test
	public void getDescription() {
		SerialPortInfo info = new SerialPortInfo("portName", "description");
		assertThat(info.getDescription(), is("description"));
	}

	/**
	 * Verifies that {@link SerialPortInfo#getDescription()} returns <code>null</code>, when no
	 * description was given to the constructor.
	 */
	@Test
	public void getDescription_isNull() {
		SerialPortInfo info = new SerialPortInfo("portName", null);
		assertThat(info.getDescription(), is(nullValue()));
	}

}
