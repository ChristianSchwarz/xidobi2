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

import static org.xidobi.DataBits.DATABITS_5;
import static org.xidobi.DataBits.DATABITS_6;
import static org.xidobi.DataBits.DATABITS_7;
import static org.xidobi.DataBits.DATABITS_8;
import static org.xidobi.DataBits.DATABITS_9;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_RESP;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.xidobi.DataBits;

//@formatter:off
/**
 * <code>IAC SB COM-PORT-OPTION SET-DATASIZE &lt;value&gt; IAC SE</code> <br>
 * This command is sent by the client to the access server to set the data bit size. The command can
 * also be sent to query the current data bit size. The value is one octet (byte). The value is an
 * index into the following value table:
 * 
 * <table>
 * <tr><th>Value</th><th>Data Bit Size</th></tr>
 * <tr><td>0</td><td>Request Current Data Bits</td>
 * <tr><td>1-4</td><td>Available for Future Use</td></tr>
 * <tr><td>5</td><td>5</td></tr>
 * <tr><td>6</td><td>6</td></tr>
 * <tr><td>7</td><td>7</td></tr>
 * <tr><td>8</td><td>8</td></tr>
 * <tr><td>9</td><td>9</td></tr>
 * <tr><td>10-127</td><td>Available for Future Use</td></tr>
 * </table>
 * 
 * @author Peter-René Jeschke
 * @author Christian Schwarz
 */

//@formatter:on
public class DataBitsControlCmd extends AbstractControlCmd {

	/** The Data Bits value of this control command as defined in RFC2217 */
	private final byte dataBitsRfc2217;
	/** The Data Bits value of this control command, <code>null</code> if there is no equivalent to the {@link #dataBitsRfc2217}-value*/
	@Nonnull
	private final DataBits dataBitsXidobi;

	//@formatter:off
	private final static BiMap<DataBits,Byte> MAP = new BiMap<DataBits, Byte>(){{
		put(DATABITS_5, (byte)5);
		put(DATABITS_6, (byte)6);
		put(DATABITS_7, (byte)7);
		put(DATABITS_8, (byte)8);
		put(DATABITS_9, (byte)9);
	}};
	//@formatter:on
	
	/**
	 * Creates a new {@link DataBitsControlCmd}-Request using the given data bits.
	 * 
	 * @param dataBits
	 *            the Data Bits value of this control command
	 * @exception IllegalArgumentException
	 *                if <code>null</code> is passed
	 */
	public DataBitsControlCmd(@Nonnull DataBits dataBits) {
		super(SET_DATASIZE_REQ);
		checkArgumentNotNull(dataBits, "dataBits");
		final Byte d = MAP.getRfc2217Equivalent(dataBits);
		if (d==null)
			throw new IllegalStateException("Unexpected dataBits value:" + dataBits);
		
		this.dataBitsRfc2217 = d;
		this.dataBitsXidobi = dataBits;

	}

	/**
	 * Decodes the {@link DataBits} value from the first byte of the <i>input</i>. The values 0-127
	 * are supported, if any other value is read an {@link IOException} will be thrown.
	 * 
	 * @param input
	 *            used to decode the content of the command, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	public DataBitsControlCmd(@Nonnull DataInput input) throws IOException {
		super(SET_DATASIZE_RESP);
		dataBitsRfc2217 = input.readByte();
		if (dataBitsRfc2217 < 0 || dataBitsRfc2217 > 127)
			throw new IOException("Unexpected dataBits value: " + dataBitsRfc2217);

		dataBitsXidobi = MAP.getXidobiEquivalent(dataBitsRfc2217);
	}

	/**
	 * Writes this coontrol command into the given {@code output}.
	 */
	@Override
	public void write(@Nonnull DataOutput output) throws IOException {
		output.writeByte(dataBitsRfc2217);
	}

	/**
	 * Returns {@link DataBits}-value of this control command.
	 * 
	 * @return <code>null</code>, when the decoded data bits value has no corresponding
	 *         {@link DataBits} value
	 */
	@CheckForNull
	public DataBits getDataBits() {
		return dataBitsXidobi;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dataBitsRfc2217;
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
		DataBitsControlCmd other = (DataBitsControlCmd) obj;
		if (dataBitsRfc2217 != other.dataBitsRfc2217)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataBitsControlCmd [dataBits=" + dataBitsRfc2217 + "]";
	}
}
