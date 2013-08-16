package org.xidobi.rfc2217.internal;

import org.apache.commons.net.telnet.SimpleOptionHandler;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;

/**
 * Handles the RFC 2217 telnet COM-PORT-OPTION.
 *
 * @see <a href="http://tools.ietf.org/html/rfc2217">RFC 2217</a>
 */
public class ComPortOptionHandler extends SimpleOptionHandler {

    private final SerialConnectionImpl connection;

    protected ComPortOptionHandler(SerialConnectionImpl connection) {
        super(COM_PORT_OPTION, true, false, true, false);
        throw new UnsupportedOperationException("not implmented yet");
    }

    @Override
    public int[] answerSubnegotiation(int[] data, int length) {
    	//decode the content to a RFC2217 Command

        //process the Command
    	
        return null;
    }

    @Override
    public int[] startSubnegotiationLocal() {
 
        // notify the SerialConnectionImpl that the server has agreed to accept COM-PORT-OPTION subnegotiation commands
        return null;
    }
}