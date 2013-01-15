/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 13:23:49
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Tests the class {@link SerialPortImpl}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestSerialPortImpl {

	/** check exceptions*/
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Mock
	private OS os;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is throw when the passed {@link OS} is
	 * <code>null</code>.
	 */

	@Test
	public void new_nullOs(){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The argument >os< must not be null!");


		new SerialPortImpl(null, 12345);
	}
}
