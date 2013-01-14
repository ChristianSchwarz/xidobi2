/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 14.01.2013 16:49:29
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.impl;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xidobi.Receiver;

/**
 * Tests the class {@link AbstractSerialPort}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestAbstractSerialPort {

	public AbstractSerialPort serialPort;

	@Mock
	private Receiver receiver;
	
	@Mock
	private Executor receiverNotificator;

	@Before
	public void setUp() {
		initMocks(this);
		serialPort = mock(AbstractSerialPort.class, CALLS_REAL_METHODS);
	}

	/** Verifies that an {@link NullPointerException} is throw when <code>null</code> is passed. */
	@Test(expected = NullPointerException.class)
	public void fireReceived_null() {
		serialPort.fireReceived(null);
	}

	/**
	 * Verifies that nothing happens if {@link AbstractSerialPort#fireReceived(byte[])} is called
	 * and no {@link Receiver} is set.
	 * 
	 * @see AbstractSerialPort#setReceiver(Receiver)
	 * @see AbstractSerialPort#setReceiver(Receiver, Executor)
	 */
	@Test
	public void fireReceived_withoutReceiver() throws Exception {
		serialPort.fireReceived(new byte[1]);

		verifyZeroInteractions(receiver);
	}

	/**
	 * Verifies that {@link Receiver#onDataReceived(byte[])} is called when a {@link Receiver} is
	 * set.
	 */
	@Test
	public void fireReceived_withReceiver() throws Exception {
		final byte[] data = new byte[1];
		serialPort.setReceiver(receiver);
		serialPort.fireReceived(data);
		
		verify(receiver,only()).onDataReceived(data);
	}
}
