/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 08:29:41
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

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
public class BaudrateControlCmdRequest extends AbstractControlCmdReq {

	/**
	 * The command code of this request when it's sent by the server.
	 */
	private static final int COMMAND_CODE_SERVER = 101;
	/**
	 * The command code of this request when it's sent by the client.
	 */
	private static final int COMMAND_CODE_CLIENT = 1;
	/**
	 * The byte that signals that a message sets a com-port option.
	 */
	private static final int COM_PORT_OPTION = 44;
	/** <code>true</code>, if the request is sent by a client, otherwise <code>false</code>. */
	private boolean fromClient;
	/** The preferred baudrate. */
	private int baudrate;

	/**
	 * Creates a new {@link BaudrateControlCmdRequest}.
	 * 
	 * @param baudrate
	 *            the preferred baudrate, must not be smaller than 1
	 * @param fromClient
	 *            <code>true</code>, if the message is sent by the client, <code>false</code> if the
	 *            message is sent by the server
	 */
	public BaudrateControlCmdRequest(	@Nonnegative int baudrate,
										boolean fromClient) {
		if (baudrate < 1)
			throw new IllegalArgumentException("The baudrate must not be less than 1! Got: >" + baudrate + "<");

		this.baudrate = baudrate;
		this.fromClient = fromClient;
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.write(COM_PORT_OPTION);
		output.write(fromClient ? COMMAND_CODE_CLIENT : COMMAND_CODE_SERVER);
		output.writeInt(baudrate);
	}

}
