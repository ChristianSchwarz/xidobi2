package org.xidobi;

import java.io.Closeable;
import java.io.IOException;

/**
 * Repesents a connected Serial-Port. Clients must call {@link #close()} to free the SerialPort
 * after usage!
 * 
 * @author Christian Schwarz
 * 
 */
public interface SerialPort extends Closeable {

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
	 * Reads from this Serialport and returns the read byte's or throws an {@link IOException} when
	 * the port was closed or an other IO-Error occurs. This method blocks until at least one byte
	 * can be returned or an {@link IOException} is thrown.
	 * 
	 * @return the received byte[]
	 * @throws IOException
	 *             if this port was closed or an unexpected IO-Error occurs.
	 */
	byte[] read() throws IOException;

}