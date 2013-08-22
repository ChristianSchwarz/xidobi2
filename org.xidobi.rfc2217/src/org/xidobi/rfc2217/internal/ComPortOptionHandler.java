package org.xidobi.rfc2217.internal;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmd;
import org.xidobi.rfc2217.internal.commands.ControlResponseDecoder;

import com.google.common.annotations.VisibleForTesting;

/**
 * Handles the RFC 2217 telnet COM-PORT-OPTION.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc2217">RFC 2217</a>
 */
public class ComPortOptionHandler extends SimpleOptionHandler {

	public static interface CommandProcessor {

		void onResponseReceived(AbstractControlCmd response);
	}

	/** The processor will be notified when a command response was received */
	@Nonnull
	private final CommandProcessor commandProcessor;

	/** Used to decode response */
	@Nonnull
	private final ControlResponseDecoder decoder;

	/**
	 * @param commandProcessor
	 */
	public ComPortOptionHandler(CommandProcessor commandProcessor) {
		this(commandProcessor, new ControlResponseDecoder());
	}

	@VisibleForTesting
	ComPortOptionHandler(	CommandProcessor commandProcessor,
							ControlResponseDecoder decoder) {
		super(COM_PORT_OPTION, true, false, true, false);
		if (commandProcessor == null)
			throw new IllegalArgumentException("Parameter >commandProcessor< must not be null!");
		if (decoder == null)
			throw new IllegalArgumentException("Parameter >decoder< must not be null!");

		this.commandProcessor = commandProcessor;
		this.decoder = decoder;
	}

	@Override
	public int[] answerSubnegotiation(int[] suboptionData, int suboptionLength) {

		DataInput input = createDataInputFrom(suboptionData, suboptionLength);
		final AbstractControlCmd resp = decoder.decode(input);
		commandProcessor.onResponseReceived(resp);

		return null;
	}

	/** Creates a {@link DataInput} from the given int[] and length. */
	protected DataInput createDataInputFrom(int[] suboptionData, int suboptionLength) {
		return new DataInputStream(new ByteArrayInputStream(toByteArray(suboptionData, suboptionLength)));
	}

	@VisibleForTesting
	static byte[] toByteArray(int[] suboptionData, int suboptionLength) {
		byte[] result = new byte[suboptionLength];
		for (int i = 0; i < suboptionLength; i++)
			result[i] = (byte) (suboptionData[i] & 0xff);
		return result;
	}
}