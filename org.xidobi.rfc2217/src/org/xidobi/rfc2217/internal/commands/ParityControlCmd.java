/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 23.08.2013 08:26:50
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.DataBits;
import org.xidobi.Parity;

import static org.xidobi.Parity.PARITY_EVEN;
import static org.xidobi.Parity.PARITY_MARK;
import static org.xidobi.Parity.PARITY_NONE;
import static org.xidobi.Parity.PARITY_ODD;
import static org.xidobi.Parity.PARITY_SPACE;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_RESP;

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

	/**
	 * Creates a new {@link ParityControlCmd}-Request using the given parity.
	 * 
	 * @param parity
	 *            the parity for this message, must not be <code>null</code>
	 */
	public ParityControlCmd(@Nonnull Parity parity) {
		super(SET_PARITY_REQ);
		if (parity == null)
			throw new IllegalArgumentException("The parameter >parity< must not be null");
		this.parity = toByte(parity);
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
	 * Transforms the given data bits byte value, as defined by RFC2217 to an {@link DataBits}
	 * value.
	 */
	private Parity toEnum(final byte parity) {
		switch (parity) {
			case 1:
				return PARITY_NONE;
			case 2:
				return PARITY_ODD;
			case 3:
				return PARITY_EVEN;
			case 4:
				return PARITY_MARK;
			case 5:
				return PARITY_SPACE;
		}
		return null;
	}

	/**
	 * Returns the byte value belonging to the assigned {@link Parity}.
	 * 
	 * @param parity
	 *            the {@link Parity} that needs to be translated for the output byte value
	 * @return the byte value belonging to the assigned {@link Parity}
	 * 
	 * @throws IOException
	 *             when there was no byte value found to the assigned {@link Parity}
	 */
	private byte toByte(Parity parity) {
		switch (parity) {
			case PARITY_NONE:
				return 1;
			case PARITY_ODD:
				return 2;
			case PARITY_EVEN:
				return 3;
			case PARITY_MARK:
				return 4;
			case PARITY_SPACE:
				return 5;
		}
		throw new IllegalStateException("Unexpected parity value:" + parity);
	}

	/**
	 * Returns the parity.
	 * 
	 * @return the parity
	 */
	public Parity getParity() {
		return toEnum(parity);
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
