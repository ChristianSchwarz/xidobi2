/*
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xidobi.rfc2217.internal;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

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
 * @author Christian Schwarz
 */
public class ComPortOptionHandler extends SimpleOptionHandler {

	/** Call back that is implemented to process received responses */
	@SuppressWarnings("javadoc")
	public interface CommandProcessor {

		/** Will be called when a control command reponse was received */
		void onResponseReceived(AbstractControlCmd response);
	}

	public interface DecoderErrorHandler {

		/** Will be called when a message could not be decoded. */
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
	 * @param commandProcessor
	 * @param errorHandler 
	 */
	public ComPortOptionHandler(CommandProcessor commandProcessor,
								DecoderErrorHandler errorHandler) {
		this(commandProcessor, errorHandler, new ControlResponseDecoder());
	}

	@VisibleForTesting
	ComPortOptionHandler(	CommandProcessor commandProcessor,
							DecoderErrorHandler errorHandler,
							ControlResponseDecoder decoder) {
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
		
		DataInput input = createDataInputFrom(suboptionData, suboptionLength);
		AbstractControlCmd resp;
		try {
			resp = decoder.decode(input);
		}
		catch (IOException e) {
			errorHandler.onDecoderError(e);
			return null;
		}
		commandProcessor.onResponseReceived(resp);

		return null;
	}

	/** Creates a {@link DataInput} from the given int[] and length. */
	private static DataInput createDataInputFrom(int[] suboptionData, int suboptionLength) {
		return new DataInputStream(new ByteArrayInputStream(toByteArray(suboptionData, suboptionLength)));
	}

}