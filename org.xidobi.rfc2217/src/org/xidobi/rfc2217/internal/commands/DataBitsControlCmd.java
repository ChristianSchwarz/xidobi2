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

import javax.annotation.Nonnull;

import org.xidobi.DataBits;

import static org.xidobi.DataBits.DATABITS_5;
import static org.xidobi.DataBits.DATABITS_6;
import static org.xidobi.DataBits.DATABITS_7;
import static org.xidobi.DataBits.DATABITS_8;
import static org.xidobi.DataBits.DATABITS_9;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE;

//@formatter:off
/**
 * <code>IAC SB COM-PORT-OPTION SET-DATASIZE &lt;value&gt; IAC SE</code> <br>
 * This command is sent by the client to the access server to set the data bit size. The command can
 * also be sent to query the current data bit size. The value is one octet (byte). The value is an
 * index into the following value table:

 * 
 * <table border="1">
  <tr><th>Value</th><th>Data Bit Size</th></tr>
  <tr><td>0</td><td>Request Current Data Bits</td>
  <tr><td>1</td><td></td></tr>
  <tr><td>2</td><td></td></tr>
  <tr><td>3</td><td></td></tr>
  <tr><td>4</td><td></td></tr>
  
</table>

 * 
 * @author Peter-René Jeschke
 */

//@formatter:on
public class DataBitsControlCmd extends AbstractControlCmd {

	/**
	 * The preferred datasize
	 */
	private DataBits dataBits;

	

	/**
	 * Creates a new {@link DataBitsControlCmd}.
	 * 
	 * @param dataBits
	 *            the preferred datasize, must not be less than one
	 */
	public DataBitsControlCmd(@Nonnull DataBits dataBits) {
		super(SET_DATASIZE);
		if (dataBits==null)
			throw new IllegalArgumentException("Parameter >dataBits< must not be null!");

		this.dataBits = dataBits;

	}

	/**
	 * Creates a new {@link DataBitsControlCmd}.
	 * 
	 * @param input
	 *            used to decode the content of the command, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	public DataBitsControlCmd(@Nonnull DataInput input) throws IOException {
		super(SET_DATASIZE, input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		final byte byteValue = input.readByte();
		dataBits=toEnum(byteValue);
		
	}

	private DataBits toEnum(final byte dataBits) throws IOException {
		switch (dataBits){
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
		throw new IOException("Unexpected data bits value: "+dataBits);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(toByte(dataBits));
	}
	
	
	private int toByte(DataBits dataBits) {
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
		throw new IllegalStateException("Unexpected DataBits value:"+ dataBits);
	}

	/**
	 * Returns the preferred datasize.
	 * 
	 * @return the dataSize, greater or equal to one
	 */
	public DataBits getDataBits() {
		return dataBits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataBits == null) ? 0 : dataBits.hashCode());
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

}
