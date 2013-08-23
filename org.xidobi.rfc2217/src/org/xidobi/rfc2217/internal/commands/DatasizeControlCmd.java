/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 13:01:33
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * <code>IAC SB COM-PORT-OPTION SET-DATASIZE &lt;value&gt; IAC SE</code> <br />
 * This command is sent by the client to the access server to set the data bit size. The command can
 * also be sent to query the current data bit size. The value is one octet (byte). The value is an
 * index into the following value table: Value Data Bit Size 0 Request Current Data Bit Size 1
 * Available for Future Use 2 Available for Future Use 3 Available for Future Use 4 Available for
 * Future Use 5 5 6 6 7 7 8 8 9-127 Available for Future Use
 * 
 * @author Peter-René Jeschke
 */
public class DatasizeControlCmd extends AbstractControlCmd {

	/**
	 * The preferred datasize
	 */
	private int dataSize;

	/**
	 * Returns the preferred datasize.
	 * 
	 * @return the dataSize, greater or equal to one
	 */
	public int getDataSize() {
		return dataSize;
	}

	/**
	 * Creates a new {@link DatasizeControlCmd}.
	 * 
	 * @param dataSize
	 *            the preferred datasize, must not be less than one
	 */
	public DatasizeControlCmd(@Nonnegative int dataSize) {
		super(SET_DATASIZE);

		if (dataSize < 1)
			throw new IllegalArgumentException("The dataSize must not be less than 1! Got: >" + dataSize + "<");
		this.dataSize = dataSize;

	}

	/**
	 * Creates a new {@link DatasizeControlCmd}.
	 * 
	 * @param input
	 *            used to decode the content of the command, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	public DatasizeControlCmd(@Nonnull DataInput input) throws IOException {
		super(SET_DATASIZE, input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		int dataSize = input.readByte();
		if (dataSize < 1)
			throw new IOException("The received datasize is invalid! Expected a value greater or equal to 1, got: >" + dataSize + "<");

		this.dataSize = dataSize;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.write(COM_PORT_OPTION);
		output.write(SET_DATASIZE);
		output.writeByte(dataSize);
	}

}
