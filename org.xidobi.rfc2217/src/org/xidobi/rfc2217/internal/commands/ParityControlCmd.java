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

import org.xidobi.Parity;
import org.xidobi.spi.Preconditions;

import static org.xidobi.Parity.PARITY_EVEN;
import static org.xidobi.Parity.PARITY_MARK;
import static org.xidobi.Parity.PARITY_NONE;
import static org.xidobi.Parity.PARITY_ODD;
import static org.xidobi.Parity.PARITY_SPACE;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_RESP;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

//@formatter:off
/**
 * <code>IAC SB COM-PORT-OPTION SET-PARITY &lt;value&gt; IAC SE</code><br />
 * This command is sent by the client to the access server to set the parity. The command can also
 * be sent to query the current parity. The value is one octet (byte).<br />
 * The value is an index into the following value table: <br />
 * 
 * <table border="1">
    	<tr><th> Value</th><th> Parity</th></tr>
        <tr><td> 0 </td><td> Request Current Data Size </td></tr>
        <tr><td> 1 </td><td> NONE	</td></tr>
        <tr><td> 2 </td><td> ODD	</td></tr>
        <tr><td> 3 </td><td> EVEN	</td></tr>
        <tr><td> 4 </td><td> MARK</td></tr>
    </table>
 * 
 * 
 * 
 * @author Peter-René Jeschke
 * @author Konrad Schulz
 */
//@formatter:on
public class ParityControlCmd extends AbstractControlCmd {

	/** The parity. */
	@Nonnull
	private byte parity;

	//@formatter:off
	private final static BiMap<Parity, Byte> MAP = new BiMap<Parity, Byte>() {{
		put(PARITY_NONE,	(byte) 1);
		put(PARITY_ODD,		(byte) 2);
		put(PARITY_EVEN,	(byte) 3);
		put(PARITY_MARK,	(byte) 4);
		put(PARITY_SPACE,	(byte) 5);
	}};
	//@formatter:on

	/**
	 * Creates a new {@link ParityControlCmd}-Request using the given parity.
	 * 
	 * @param parity
	 *            the parity for this message, must not be <code>null</code>
	 */
	public ParityControlCmd(@Nonnull Parity parity) {
		super(SET_PARITY_REQ);
		checkArgumentNotNull(parity,"parity");
		
		Byte p = MAP.getRfc2217Equivalent(parity);
		if (p == null)
			throw new IllegalStateException("Unexpected parity value:" + parity);
		
		this.parity = p;
	}

	/**
	 * Decodes the {@link Parity} value from the first byte of the <i>input</i>. The values 0-127
	 * are supported, if any other value is read an {@link IOException} will be thrown.
	 * 
	 * @param input
	 *            the input where the command must be read from, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	public ParityControlCmd(@Nonnull DataInput input) throws IOException {
		super(SET_PARITY_RESP);

		parity = input.readByte();
		if (parity < 0 || parity > 127)
			throw new IOException("Unexpected parity value: " + parity);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(parity);
	}
	

	/**
	 * Returns the parity.
	 * 
	 * @return the parity
	 */
	public Parity getParity() {
		return MAP.getXidobiEquivalent(parity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + parity;
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
		ParityControlCmd other = (ParityControlCmd) obj;
		if (parity != other.parity)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParityControlCmd [parity=" + parity + "]";
	}

}
