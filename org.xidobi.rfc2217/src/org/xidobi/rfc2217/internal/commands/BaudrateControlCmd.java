/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 12:42:08
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnegative;

/**
 * <code>IAC SB COM-PORT-OPTION SET-BAUD <value(4)> IAC SE</code><br />
 * This command is sent by the client to the access server to set the baud rate of the com port. The
 * value is four octets (4 bytes). The value is represented in network standard format. The value is
 * the baud rate being requested. A special case is the value 0. If the value is zero the client is
 * requesting the current baud rate of the com port on the access server.
 * 
 * @author Peter-René Jeschke
 */
public class BaudrateControlCmd extends AbstractControlCmd {

	/** The preferred baudrate. */
	private int baudrate;

	/**
	 * Creates a new instance using the given baud rate.
	 * 
	 * @param baudrate
	 *            the baudrate, must be greater than 0 
	 * 
	 */
	public BaudrateControlCmd(@Nonnegative int baudrate) {
		super(SET_BAUDRATE);
		if (baudrate < 1)
			throw new IllegalArgumentException("The baudrate must not be less than 1! Got: >" + baudrate + "<");

		this.baudrate = baudrate;
	}

	public BaudrateControlCmd(DataInput input) throws IOException {
		super(SET_BAUDRATE, input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		int baudrate = input.readInt();

		if (baudrate < 1)
			throw new IOException("The received baudrate is invalid! Expected a value greater or equal to 1, got: >" + baudrate + "<");

		this.baudrate = baudrate;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(baudrate);
	}

	/**
	 * Returns the requested baudrate.
	 * 
	 * @return the baudRate that was requested by the access-server
	 */
	public @Nonnegative
	int getBaudrate() {
		return baudrate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + baudrate;
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
		BaudrateControlCmd other = (BaudrateControlCmd) obj;
		if (baudrate != other.baudrate)
			return false;
		return true;
	}
	
	

}
