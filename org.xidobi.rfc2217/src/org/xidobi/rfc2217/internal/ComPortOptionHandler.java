package org.xidobi.rfc2217.internal;

import java.util.Arrays;

import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmdResp;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;

/**
 * Handles the RFC 2217 telnet COM-PORT-OPTION.
 *
 * @see <a href="http://tools.ietf.org/html/rfc2217">RFC 2217</a>
 */
public class ComPortOptionHandler extends SimpleOptionHandler {
	
	public static interface CommandProcessor {
		void onResponseReceived(AbstractControlCmdResp response);
	}
	
    public ComPortOptionHandler() {
        super(COM_PORT_OPTION, true, false, true, false);
    }
    
    @Override
    public int[] answerSubnegotiation(int[] suboptionData, int suboptionLength) {
    	System.out.println(Arrays.toString(suboptionData));
    	return super.answerSubnegotiation(suboptionData, suboptionLength);
    }
}