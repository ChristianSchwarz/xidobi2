/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 23.08.2013 08:26:50
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * <code>IAC SB COM-PORT-OPTION SET-PARITY &lt;value&gt; IAC SE</code><br />
 * This command is sent by the client to the access server to set the parity. The command can also
 * be sent to query the current parity. The value is one octet (byte).<br />
 * The value is an index into the following value table: <br />
 * Value Parity [1] 0 Request Current Data Size 1 NONE 2 ODD 3 EVEN 4 MARK 5 SPACE 6-127 Available
 * for Future Use
 * 
 * @author Peter-René Jeschke
 */
public class ParityControlCmd extends AbstractControlCmd {

	/** The preferred parity. */
	private int parity;

	/**
	 * Creates a new {@link ParityControlCmd}.
	 * 
	 * @param parity
	 *            the preferred parity for this message, must not be negative
	 */
	public ParityControlCmd(@Nonnegative int parity) {
		super(SET_PARITY);
		if (parity < 0)
			throw new IllegalArgumentException("The parity must not be negative! Got: >" + parity + "<");
		this.parity = parity;
	}

	/**
	 * Reads a new {@link ParityControlCmd}.
	 * 
	 * @param input
	 *            the input where the command schould be read from, must not be
	 *            {@link NullPointerException}
	 * @throws IOException
	 */
	public ParityControlCmd(@Nonnull DataInput input) throws IOException {
		super(SET_PARITY, input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		int parity = input.readByte();
		if (parity < 0)
			throw new IOException("The received parity is invalid! Expected a value greater or equal to 0, got: >" + parity + "<");

		this.parity = parity;
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
	public int getParity() {
		return parity;
	}

}
