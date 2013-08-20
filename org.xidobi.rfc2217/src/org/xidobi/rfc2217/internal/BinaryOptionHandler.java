package org.xidobi.rfc2217.internal;
import org.apache.commons.net.telnet.SimpleOptionHandler;

import static org.apache.commons.net.telnet.TelnetOption.BINARY;

/**
 * Handler for the telnet {@code TRANSMIT-BINARY} option defined by RFC 856.
 */
public class BinaryOptionHandler extends SimpleOptionHandler {

	/**
	 * Creates a new instance, all initial and accept values are set to true.
	 */
    public BinaryOptionHandler() {
        super(BINARY, true, true, true, true);
    }
}