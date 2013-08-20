package org.xidobi.rfc2217.internal;

import org.apache.commons.net.telnet.SimpleOptionHandler;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;

/**
 * Handles the RFC 2217 telnet COM-PORT-OPTION.
 *
 * @see <a href="http://tools.ietf.org/html/rfc2217">RFC 2217</a>
 */
public class ComPortOptionHandler extends SimpleOptionHandler {
    public ComPortOptionHandler() {
        super(COM_PORT_OPTION, true, false, true, false);
    }
}