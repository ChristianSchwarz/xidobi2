package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_RESP;

/**
 * This command may be sent by either the client or the access server to exchange signature
 * information. If the command is sent without <text> it is a request from the sender to receive the
 * signature text of the receiver. The text may be a combination of any characters. There is no
 * structure to the <text> field. It may contain manufacturer information, version number
 * information, or any other information desired. If an IAC character appears in the text it must be
 * translated to IAC-IAC to avoid conflict with the IAC which terminates the command.
 * 
 * @author Christin Nitsche
 * 
 */
public class SignaturControlCmd extends AbstractControlCmd {

	private String signatur;

	public SignaturControlCmd(String signatur) throws IOException {
		super(SIGNATURE_REQ);
		if (signatur == null)
			throw new IllegalArgumentException("The parameter >signatur< must not be null");
		this.signatur = signatur;
	}

	/**
	 * Creates a new {@link SignaturControlCmd}
	 * 
	 * @param input
	 *            used to decode the content of the command, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	SignaturControlCmd(DataInput input) throws IOException {
		super(SIGNATURE_RESP);
		
		signatur = input.readLine();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeChars(signatur);
	}
	
	/**
	 * Returns the preferred signatur.
	 * 
	 * @return
	 */
	public String getSignatur() {
		return signatur;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((signatur == null) ? 0 : signatur.hashCode());
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
		SignaturControlCmd other = (SignaturControlCmd) obj;
		if (signatur == null) {
			if (other.signatur != null)
				return false;
		} else if (!signatur.equals(other.signatur))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SignaturControlCmd [signatur=" + signatur + "]";
	}

}
