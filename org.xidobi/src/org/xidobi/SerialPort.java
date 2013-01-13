package org.xidobi;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * 
 * @author Christian Schwarz
 * 
 */
public interface SerialPort {

	/**
	 * Writes the given byte[].All bytes of the array were written.
	 * 
	 * @param data
	 *            must not be <code>null</code>
	 * @throws IOException
	 *             when the Port is closed
	 */
	void write(byte[] data) throws IOException;

	/**
	 * Set the {@link Receiver} to be notified when data was received, or
	 * <code>null</code> to remove the current {@link Receiver}.
	 * 
	 * @param receiver
	 *            the {@link Receiver} to be notified or <code>null</code> to
	 *            remove the current
	 */
	void setReceiver(Receiver receiver);

	/**
	 * Set the {@link Receiver} to be notified when data was received, or
	 * <code>null</code> to remove the current {@link Receiver}. The given
	 * {@link Executor} will be used to notify the receiver.
	 * 
	 * @param receiver
	 *            the {@link Receiver} to be notified or <code>null</code> to
	 *            remove the current {@link Receiver}
	 * 
	 * @param executor
	 *            the {@link Executor} to be used to notifiy the <b>receiver</b>
	 *            
	 *            @exception IllegalArgumentException if <code>executor==null</code>
	 */
	void setReceiver(Receiver receiver, Executor executor);
}
