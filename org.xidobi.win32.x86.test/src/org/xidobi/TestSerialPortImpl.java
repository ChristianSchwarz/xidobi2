/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 13:23:49
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.OS.INVALID_HANDLE_VALUE;

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

	/** check exceptions */
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
	@SuppressWarnings("resource")
	public void new_nullOs() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The argument >os< must not be null!");

		new SerialPortImpl(null, 12345);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown when the handle is {@link OS#INVALID_HANDLE_VALUE} (-1).
	 * 
	 */
	@Test
	@SuppressWarnings("resource")
	public void new_negativeHandle() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The argument >os< must not be null!");

		new SerialPortImpl(os, INVALID_HANDLE_VALUE);
	}

}
