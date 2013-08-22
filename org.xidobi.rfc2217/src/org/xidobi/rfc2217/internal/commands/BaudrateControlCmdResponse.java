/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 10:35:35
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
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
public class BaudrateControlCmdResponse extends AbstractControlCmdResp {

	/** The requested baudrate. */
	private int baudrate;

	/**
	 * Reads out a BaudRateControlCmd.
	 * 
	 * @param input
	 *            the input that contains the message
	 * @throws IOException
	 *             when the message is invalid or not readable
	 */
	public BaudrateControlCmdResponse(DataInput input) throws IOException {
		super(input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		int requestByte = input.readUnsignedByte();
		int commandCode = input.readUnsignedByte();
		int baudrate = input.readInt();

		if (commandCode != 1 && commandCode != 101)
			throw new IOException("The message was invalid! Expected a baudrate, got a message with the command code >" + commandCode + "<!");

		if (baudrate < 1)
			throw new IOException("The received baudrate is invalid! Expected a value greater or equal to 1, got: >" + baudrate + "<");

		this.baudrate = baudrate;
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

}
