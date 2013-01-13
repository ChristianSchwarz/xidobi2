package org.xidobi;

import java.util.concurrent.Executor;

/**
 * The {@link Receiver} will be notified by the {@link SerialPort} to process
 * received <code>byte's</code>.
 * 
 * @see SerialPort#setReceiver(Receiver)
 * @see SerialPort#setReceiver(Receiver, Executor)
 */
public interface Receiver {

	/**
	 * This method will invoked if some data was received by the
	 * {@link SerialPort}.
	 * 
	 * @param data
	 *            the received bytes, never <code>null</code>
	 */
	void onDataReceived(byte[] data);

}
