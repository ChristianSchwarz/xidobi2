/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.01.2013 08:12:34
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
