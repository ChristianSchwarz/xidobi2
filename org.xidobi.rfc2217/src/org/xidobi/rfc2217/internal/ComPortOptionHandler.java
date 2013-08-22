package org.xidobi.rfc2217.internal;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmdResp;
import org.xidobi.rfc2217.internal.commands.ControlResponseDecoder;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;

/**
 * Handles the RFC 2217 telnet COM-PORT-OPTION.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc2217">RFC 2217</a>
 */
public class ComPortOptionHandler extends SimpleOptionHandler {

	/** The processor will be notified when a command response was received */
	@Nonnull
	private final CommandProcessor commandProcessor;

	public static interface CommandProcessor {
		void onResponseReceived(AbstractControlCmdResp response);
	}

	public ComPortOptionHandler(CommandProcessor commandProcessor) {
		this(commandProcessor, new ControlResponseDecoder());
	}

	/**
	 * 
	 */
	public ComPortOptionHandler(CommandProcessor commandProcessor,
								ControlResponseDecoder decoder) {
		super(COM_PORT_OPTION, true, false, true, false);
		if (commandProcessor == null)
			throw new IllegalArgumentException("Parameter >commandProcessor< must not be null!");

		this.commandProcessor = commandProcessor;
	}

	@Override
	public int[] answerSubnegotiation(int[] suboptionData, int suboptionLength) {
		System.out.println(Arrays.toString(suboptionData));
		return null;
	}
}