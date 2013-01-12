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
	 * Writes the given byte[].
	 * @param data must not be <code>null</code>
	 * @throws IOException when the Port is closed
	 */
	void write(byte[] data) throws IOException;
	
	void setReceiver(Receiver receiver);
	void setReceiver(Receiver receiver, Executor executor);
}
