/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 12:43:58
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import org.junit.Test;

/**
 * 
 * Tests the class {@link SerialPortHandleImpl}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestSerialPortHandleImpl {

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when <code>null</code> is passed.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void new_nullOS()  {
		new SerialPortHandleImpl(null);
	}
}
