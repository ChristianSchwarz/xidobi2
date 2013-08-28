package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_REQ;

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
public class SignaturControlCmd extends AbstractControlCmd {

	private String signatur;

	SignaturControlCmd(String signatur) throws IOException {
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
	public SignaturControlCmd(DataInput input) throws IOException {
		super(SIGNATURE_REQ, input);
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
	protected void read(DataInput input) throws IOException {
		String signatur = input.readLine();
		this.signatur = signatur;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeChars(signatur);
	}

}
