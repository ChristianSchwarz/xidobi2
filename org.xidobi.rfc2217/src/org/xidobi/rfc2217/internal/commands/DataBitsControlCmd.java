/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 13:01:33
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.xidobi.DataBits;
import org.xidobi.spi.Preconditions;

import static org.xidobi.DataBits.DATABITS_5;
import static org.xidobi.DataBits.DATABITS_6;
import static org.xidobi.DataBits.DATABITS_7;
import static org.xidobi.DataBits.DATABITS_8;
import static org.xidobi.DataBits.DATABITS_9;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_RESP;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

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

	/**
	 * The Data Bits value of this control command
	 */
	@Nonnull
	private byte dataBits;

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
		
		this.dataBits = toByte(dataBits);

	}

	/**
	 * 
	 * 
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
		dataBits = input.readByte();
		
		if (dataBits < 0 || dataBits > 127)
			throw new IOException("Unexpected dataBits value: " + dataBits);
	}

	/**
	 * Transforms the given data bits byte value, as defined by RFC2217 to an {@link DataBits}
	 * value.
	 */
	@Nonnull
	private DataBits toEnum(final byte dataBits) {
		switch (dataBits) {
			case 5:
				return DATABITS_5;
			case 6:
				return DATABITS_6;
			case 7:
				return DATABITS_7;
			case 8:
				return DATABITS_8;
			case 9:
				return DATABITS_9;
		}
		return null;
	}

	/**
	 * Writes this coontrol command into the given {@code output}.
	 */
	@Override
	public void write(@Nonnull DataOutput output) throws IOException {
		output.writeByte(dataBits);
	}

	/** Transforms this given {@link DataBits}-value to its corresponding RFC2217 specific value */
	private byte toByte(@Nonnull DataBits dataBits) {
		checkArgumentNotNull(dataBits, "dataBits");
		switch (dataBits) {
			case DATABITS_5:
				return 5;
			case DATABITS_6:
				return 6;
			case DATABITS_7:
				return 7;
			case DATABITS_8:
				return 8;
			case DATABITS_9:
				return 9;
		}
		throw new IllegalStateException("Unexpected dataBits value:" + dataBits);
	}

	/**
	 * Returns {@link DataBits}-value of this control command.
	 * 
	 * @return <code>null</code>, when the decoded data bits value has no
	 *         corresponding {@link DataBits} value
	 */
	@CheckForNull
	public DataBits getDataBits() {
		return toEnum(dataBits);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dataBits;
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
		if (dataBits != other.dataBits)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataBitsControlCmd [dataBits=" + dataBits + "]";
	}
}
