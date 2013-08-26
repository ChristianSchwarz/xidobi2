package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;



/**
 * 
 * This command is sent by the client to the access server to set the number of stop bits. The
 * command can also be sent to query the current stop bit size. The value is one octet (byte). The
 * value is an index into the following value table:
 * 
 * <pre>
 * Value		Stop Bit Size 
 *     0		Request Current Data Size 
 *     1		1
 *     2		2
 *     3		1.5
 * 4-127		Available for Future Use
 * </pre>
 * @author Christin Nitsche
 * 
 */

public class StopBitsControlCmd extends AbstractControlCmd {

	private int stopBits;

	/**
	 * Creates a new {@link StopBitsControlCmd}.
	 * 
	 * @param commandCode
	 *            the preferred stopsize, greater or equal to one.
	 */
	StopBitsControlCmd(int stopsize) {
		super(SET_STOPSIZE);
		if (stopsize < 1)
			throw new IllegalArgumentException("The stopsize must not be less than 1! Got: >" + stopsize + "<");
		this.stopBits = stopsize;
	}

	/**
	 * Creates a new {@link StopBitsControlCmd}.
	 * 
	 * @param input
	 *            used to decode the content of the command, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	public StopBitsControlCmd(DataInput input) throws IOException {
		super(SET_STOPSIZE, input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		int stopBits = input.readByte();
		if (stopBits < 1)
			throw new IOException("The received stopsize is invalid! Expected a value greater or equal to 1, got: >" + stopBits + "<");
		this.stopBits = stopBits;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.write(COM_PORT_OPTION);
		output.write(SET_STOPSIZE);
		output.writeByte(stopBits);
	}

	/**
	 * Returns the preferred stopsize.
	 * 
	 * @return the stopsize, greater or equal to one
	 */
	public int getStopBits() {
		return stopBits;
	}

}
