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

import static org.xidobi.rfc2217.internal.commands.ControlRequestEncoder.encode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.TelnetClient;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.CommandProcessor;
import org.xidobi.rfc2217.internal.ConditionalGuard.Condition;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmd;
import org.xidobi.rfc2217.internal.commands.ControlCmd;

/**
 * Used to send and receive com port control command in a blocking manner.
 * <p>
 * 
 * @author Christian Schwarz
 */
public class BlockingCommandSender implements CommandProcessor {

	/**
	 * contains the last received reponse of a type
	 * <ul>
	 * <li><b>Key:</b> the type of the response
	 * <li><b>Value:</b>the last received response
	 * </ul>
	 */
	private final Map<Class<? extends AbstractControlCmd>, AbstractControlCmd> responses = new HashMap<Class<? extends AbstractControlCmd>, AbstractControlCmd>();
	/** */
	private final ConditionalGuard guard = new ConditionalGuard();

	private TelnetClient telnetClient;

	/**
	 * Creates a new instance, using the given {@link TelnetClient}.
	 * 
	 * @param telnetClient
	 *            used to send control commands
	 */
	public BlockingCommandSender(TelnetClient telnetClient) {
		this.telnetClient = telnetClient;
	}

	/**
	 * Sends the given Control Command and returns the response. An {@link IOException} will be
	 * thrown if an I/O Error occured while writing or an receive timeout was detected.
	 * 
	 * @param req
	 *            the control command to be send
	 * @return the response
	 * @throws IOException
	 *             if an I/O Error occured while writing or an receive timeout was detected
	 */
	@SuppressWarnings("unchecked")
	@Nonnull
	public <T extends ControlCmd> T send(T req) throws IOException {
		removeResponse(req.getClass());
		sendCmd(req);
		final T resp = (T) awaitResponse(req.getClass());

		if (resp == null)
			throw new IOException("Response-Timeout: No response received for command:" + req);
		return resp;
	}

	/**
	 * Waits 1 second for the control command of the given type and returns it. If nothing was
	 * received within this time <code>null</code> will be returned.
	 */
	private <T extends ControlCmd> T awaitResponse(final Class<T> commandType) {
		final AtomicReference<T> resp = new AtomicReference<T>();
		Condition condition = new Condition() {

			public boolean isSatisfied() {
				final T cmdResp = (T) removeResponse(commandType);
				resp.set(cmdResp);
				return cmdResp != null;
			}
		};

		guard.awaitUninterruptibly(condition, 3000);

		return resp.get();
	}

	/**
	 * Sends the given given control command.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs while writing the message
	 */
	protected void sendCmd(ControlCmd message) throws IOException {
		int[] bytes = encode(message);
		telnetClient.sendSubnegotiation(bytes);
	}

	/**
	 * Remove the previous received Control Command of the given Type, and return it.
	 * <code>null</code> will be returned if no Control Command was removed.
	 */
	@SuppressWarnings("unchecked")
	private <T extends ControlCmd> T removeResponse(final Class<T> commandType) {
		return (T) responses.remove(commandType);

	}

	/**
	 * @noreference This method is not intended to be referenced by clients. It exists to serve as
	 *              callback for received messages of the {@link ComPortOptionHandler}.
	 * @see ComPortOptionHandler
	 * @see CommandProcessor
	 */
	public void onResponseReceived(AbstractControlCmd response) {
		responses.put(response.getClass(), response);
		guard.signalAll();
	}
}