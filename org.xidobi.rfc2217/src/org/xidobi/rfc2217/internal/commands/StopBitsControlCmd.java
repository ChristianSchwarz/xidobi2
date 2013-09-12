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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.StopBits;

import static org.xidobi.StopBits.STOPBITS_1;
import static org.xidobi.StopBits.STOPBITS_1_5;
import static org.xidobi.StopBits.STOPBITS_2;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE_RESP;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

//@formatter:off
/**
 * 
 * This command is sent by the client to the access server to set the number of stop bits. The
 * command can also be sent to query the current stop bit size. The value is one octet (byte). The
 * value is an index into the following value table:
 * 
 * 
 * 	<table border="1">
    	<tr><th> Value</th><th> Stop Bit Size </th></tr>
        <tr><td> 0 </td><td> Request Current Data Size </td></tr>
        <tr><td> 1 </td><td> 1	</td></tr>
        <tr><td> 2 </td><td> 2	</td></tr>
        <tr><td> 3 </td><td> 1.5</td></tr>
        <tr><td> 4-127 </td><td> Available for Future Use </td></tr>
    </table>
   
 * 
 * @author Christin Nitsche
 * @author Konrad Schulz
 */
//@formatter:on
public class StopBitsControlCmd extends AbstractControlCmd {

	/** The stop bits */
	private final byte stopBitsRfc2217;
	private final StopBits stopBitsXidobi;
	
	//@formatter:off
	private final static BiMap<StopBits, Byte> MAP = new BiMap<StopBits, Byte>() {{
		put(STOPBITS_1,		(byte) 1);
		put(STOPBITS_1_5,	(byte) 2);
		put(STOPBITS_2,		(byte) 3);
	}};
	//@formatter:on
	

	/**
	 * Creates a new {@link StopBitsControlCmd}-Request using the given stop bits.
	 * 
	 * @param stopBits
	 *            the stopbits
	 */
	public StopBitsControlCmd(@Nonnull StopBits stopBits) {
		super(SET_STOPSIZE_REQ);
		checkArgumentNotNull(stopBits, "stopBits"); 
		stopBitsXidobi = stopBits;
		stopBitsRfc2217 = MAP.getRfc2217Equivalent(stopBits);
	}

	/**
	 * Creates a new {@link StopBitsControlCmd}-Response, that is decoded from the given
	 * <i>input</i>.
	 * 
	 * @param input
	 *            the input where the command must be read from, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	public StopBitsControlCmd(DataInput input) throws IOException {
		super(SET_STOPSIZE_RESP);
		stopBitsRfc2217 = input.readByte();
		if (stopBitsRfc2217 < 0 || stopBitsRfc2217 > 127)
			throw new IOException("Unexpected stopBits value: " + stopBitsRfc2217);
		stopBitsXidobi = MAP.getXidobiEquivalent(stopBitsRfc2217);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(stopBitsRfc2217);
	}

	/**
	 * Returns the stop bits.
	 * 
	 * @return the stop bits
	 */
	public StopBits getStopBits() {
		return stopBitsXidobi;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + stopBitsRfc2217;
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
		if (stopBitsRfc2217 != other.stopBitsRfc2217)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StopBitsControlCmd [stopBits=" + stopBitsRfc2217 + "]";
	}

}
