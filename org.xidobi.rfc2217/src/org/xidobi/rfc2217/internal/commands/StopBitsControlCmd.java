package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.StopBits.STOPBITS_1;
import static org.xidobi.StopBits.STOPBITS_1_5;
import static org.xidobi.StopBits.STOPBITS_2;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE_REQ;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.xidobi.StopBits;

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
 * 
 * @author Christin Nitsche
 * @author Konrad Schulz
 */

public class StopBitsControlCmd extends AbstractControlCmd {

	private StopBits stopBits;

	/**
	 * Creates a new {@link StopBitsControlCmd}.
	 * 
	 * @param commandCode
	 *            the preferred stopsize, greater or equal to one.
	 */
	StopBitsControlCmd(StopBits stopBits) {
		super(SET_STOPSIZE_REQ);
		if (stopBits == null)
			throw new IllegalArgumentException("The parameter >stopBits< must not be null");
		this.stopBits = stopBits;
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
		super(SET_STOPSIZE_REQ, input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		byte byteValue = input.readByte();
		StopBits stopBits = toEnum(byteValue);
		this.stopBits = stopBits;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(toByte(stopBits));
	}

	/**
	 * Returns the preferred stop bits.
	 * 
	 * @return the stop bits
	 */
	public StopBits getStopBits() {
		return stopBits;
	}

	private StopBits toEnum(final byte stopBits) throws IOException {
		switch (stopBits) {
			case 1:
				return STOPBITS_1;
			case 2:
				return STOPBITS_1_5;
			case 3:
				return STOPBITS_2;
		}
		throw new IOException("Unexpected stopBits value: " + stopBits);
	}

	private int toByte(StopBits stopBits) {
		switch (stopBits) {
			case STOPBITS_1:
				return 1;
			case STOPBITS_1_5:
				return 2;
			case STOPBITS_2:
				return 3;
		}
		throw new IllegalStateException("Unexpected stopBits value:" + stopBits);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stopBits == null) ? 0 : stopBits.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StopBitsControlCmd other = (StopBitsControlCmd) obj;
		if (stopBits != other.stopBits)
			return false;
		return true;
	}

}
