/*
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE_RESP;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnegative;

/**
 * <code>IAC SB COM-PORT-OPTION SET-BAUD <value(4)> IAC SE</code> <br>
 * This command is sent by the client to the access server to set the baud rate of the com port. The
 * value is four octets (4 bytes). The value is represented in network standard format. The value is
 * the baud rate being requested. A special case is the value 0. If the value is zero the client is
 * requesting the current baud rate of the com port on the access server.
 * 
 * @author Peter-René Jeschke
 */
public class BaudrateControlCmd extends AbstractControlCmd<Void,Void> {

	/** The preferred baudrate. */
	private int baudrate;

	/**
	 * Creates a new instance using the given baud rate.
	 * 
	 * @param baudrate
	 *            the baudrate, must be greater than 0
	 */
	public BaudrateControlCmd(@Nonnegative int baudrate) {
		super(SET_BAUDRATE_REQ);
		if (baudrate < 1)
			throw new IllegalArgumentException("The baudrate must not be less than 1! Got: >" + baudrate + "<");

		this.baudrate = baudrate;
	}

	/**
	 * @param input
	 * @throws IOException
	 */
	public BaudrateControlCmd(DataInput input) throws IOException {
		super(SET_BAUDRATE_RESP);

		baudrate = input.readInt();

		if (baudrate < 1)
			throw new IOException("The received baudrate is invalid! Expected a value greater or equal to 1, got: >" + baudrate + "<");
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

	@Override
	public String toString() {
		return "BaudrateControlCmd [baudrate=" + baudrate + "]";
	}

}
