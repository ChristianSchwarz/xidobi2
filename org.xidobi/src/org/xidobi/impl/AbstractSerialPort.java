/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 14.01.2013 16:41:27
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.impl;

import java.util.concurrent.Executor;

import org.xidobi.Receiver;
import org.xidobi.SerialPort;

/**
 * Basic implementation of {@link SerialPort}
 * 
 * @author Christian Schwarz
 * 
 */
public abstract class AbstractSerialPort implements SerialPort {

	private Receiver receiver;
	private Executor receiverNotificator;
	
	/**
	 * 
	 */
	protected AbstractSerialPort(Executor receiverNotificator) {
		this.receiverNotificator = receiverNotificator;
		
	}

	public final void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public final void setReceiver(Receiver receiver, Executor executor) {}

	/**
	 * Notifies the {@link Receiver} that the given byte's were received.
	 * 
	 * @param data
	 *            the received bytes, must not be <code>null</code>
	 * 
	 * @throws NullPointerException
	 *             if {@code data==null}
	 */
	protected final void fireReceived(final byte... data) {
		if (data == null)
			throw new NullPointerException("Parameter >data< must not be null!");
		
		receiverNotificator.execute(new Runnable() {
			
			public void run() {
				receiver.onDataReceived(data);
			}
		});
		
	}

}
