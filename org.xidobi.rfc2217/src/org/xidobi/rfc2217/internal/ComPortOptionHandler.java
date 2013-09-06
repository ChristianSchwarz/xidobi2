package org.xidobi.rfc2217.internal;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmd;
import org.xidobi.rfc2217.internal.commands.ControlResponseDecoder;

import com.google.common.annotations.VisibleForTesting;

import static org.xidobi.rfc2217.internal.ArrayUtil.toByteArray;
import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;

/**
 * Handles the RFC 2217 telnet COM-PORT-OPTION.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc2217">RFC 2217</a>
 */
public class ComPortOptionHandler extends SimpleOptionHandler {

	/** Call back that is implemented to process received responses*/
	@SuppressWarnings("javadoc")
	public static interface CommandProcessor {
		/** Will be called when a control command reponse was received */
		void onResponseReceived(AbstractControlCmd response);
	}
	public static interface DecoderErrorHandler{
		/** Will be called when a message could not be decoded.	 */
		void onDecoderError(IOException e);
	}

	/** The processor will be notified when a command response was received */
	@Nonnull
	private final CommandProcessor commandProcessor;
	
	/** Used to decode response */
	@Nonnull
	private final ControlResponseDecoder decoder;

	private DecoderErrorHandler errorHandler;

	/**
	 * 
	 * @param commandProcessor
	 */
	public ComPortOptionHandler(CommandProcessor commandProcessor,
		                     	DecoderErrorHandler errorHandler) {
		this(commandProcessor,errorHandler, new ControlResponseDecoder());
	}

	@VisibleForTesting
	ComPortOptionHandler(	CommandProcessor commandProcessor,
	                     	DecoderErrorHandler errorHandler,
							ControlResponseDecoder decoder
							) {
		super(COM_PORT_OPTION, true, false, true, false);
		if (commandProcessor == null)
			throw new IllegalArgumentException("Parameter >commandProcessor< must not be null!");
		if (decoder == null)
			throw new IllegalArgumentException("Parameter >decoder< must not be null!");
		if (errorHandler == null)
			throw new IllegalArgumentException("Parameter >errorHandler< must not be null!");

		this.errorHandler = errorHandler;
		this.commandProcessor = commandProcessor;
		this.decoder = decoder;
	}

	@Override
	public int[] answerSubnegotiation(int[] suboptionData, int suboptionLength) {
		System.err.println("<-"+Arrays.toString(toByteArray(suboptionData, suboptionLength)));
		DataInput input = createDataInputFrom(suboptionData, suboptionLength);
		AbstractControlCmd resp;
		try {
			resp = decoder.decode(input);
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			errorHandler.onDecoderError(e);
			return null;
		}
		commandProcessor.onResponseReceived(resp);
		System.err.println(resp);

		return null;
	}

	/** Creates a {@link DataInput} from the given int[] and length. */
	private static DataInput createDataInputFrom(int[] suboptionData, int suboptionLength) {
		return new DataInputStream(new ByteArrayInputStream(toByteArray(suboptionData, suboptionLength)));
	}

	
}