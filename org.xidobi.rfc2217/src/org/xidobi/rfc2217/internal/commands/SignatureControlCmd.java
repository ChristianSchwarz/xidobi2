package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_RESP;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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
public class SignatureControlCmd extends AbstractControlCmd {

	private String signature;

	/** Telnet commands */
	private final static int IAC = 255;

	public SignatureControlCmd(String signature) throws IOException {
		super(SIGNATURE_REQ);
		if (signature == null)
			throw new IllegalArgumentException("The parameter >signature< must not be null");
		this.signature = signature;

	}

	/**
	 * Creates a new {@link SignatureControlCmd}
	 * 
	 * @param input
	 *            used to decode the content of the command, must not be <code>null</code>
	 * @throws IOException
	 *             if the message is malformed or the underlying media can't be read
	 */
	SignatureControlCmd(DataInput input) throws IOException {
		super(SIGNATURE_RESP);
		signature = input.readLine();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		translateIAC();
		output.writeChars(signature);
	}

	/**
	 * Returns the preferred signatur.
	 * 
	 * @return
	 */
	public String getSignature() {
		translateIAC();
		return signature;
	}

	/**
	 * If an IAC character appears in the text it must be translated to IAC-IAC to avoid conflict
	 * with the IAC which terminates the command.
	 */
	private void translateIAC() {
		String s = new String();
		String iacString = Character.toString((char) IAC);
		if (signature.contains(iacString)) {
			String[] split = signature.split(iacString);
			if (split.length > 0) {
				for (int i = 0; i < split.length; i++) {
					s += split[i] + iacString + iacString;
				}
				signature = s;
			} else
				signature = signature + signature;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((signature == null) ? 0 : signature.hashCode());
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
		SignatureControlCmd other = (SignatureControlCmd) obj;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SignatureControlCmd [signature=" + signature + "]";
	}

}
