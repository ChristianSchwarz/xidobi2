/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 13:58:54
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.TelnetClient;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.CommandProcessor;
import org.xidobi.rfc2217.internal.ConditionalGuard.Condition;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmd;

import static org.xidobi.rfc2217.internal.ArrayUtil.toIntArray;
import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;

/**
 * Used to send and receive com port control command in a blocking manner. 
 * @author Christian Schwarz
 * 
 */
public class BlockingCommandSender implements CommandProcessor {

	private final Map<Class<?>, AbstractControlCmd> responses = new HashMap<Class<?>, AbstractControlCmd>();
	/** */
	private final ConditionalGuard guard = new ConditionalGuard();

	private TelnetClient telnetClient;
	
	/**
	 * Creates a new instance, using the given {@link TelnetClient}.
	 * @param telnetClient used to send control commands
	 */
	public BlockingCommandSender(TelnetClient telnetClient) {
		this.telnetClient = telnetClient;
	}

	/**
	 * Sends the given Control Command and returns the response. An {@link IOException} will be
	 * thrown if an I/O Error occured while writing or an receive timeout was detected.
	 * 
	 * @param req the control command to be send
	 * @return the response
	 * @throws IOException if an I/O Error occured while writing or an receive timeout was detected
	 */
	@SuppressWarnings("unchecked")
	@Nonnull
	public <T extends AbstractControlCmd> T send(T req) throws IOException {
		removeResponse(req.getClass());
		sendCmd(req);
		final T resp = (T) awaitResponse(req.getClass());
		
		if (resp==null)
			throw new IOException("Response-Timeout: No response received for command:"+ req);
		return resp;
	}

	/**
	 * Waits 1 second for the control command of the given type and returns it. If nothing was
	 * received within this time <code>null</code> will be returned.
	 */
	private <T extends AbstractControlCmd> T awaitResponse(final Class<T> commandType) {
		final AtomicReference<T> resp = new AtomicReference<T>();
		Condition condition = new Condition() {

			public boolean isSatisfied() {
				final T cmdResp = (T) removeResponse(commandType);
				resp.set(cmdResp);
				return cmdResp != null;
			}
		};

		guard.awaitUninterruptibly(condition, 1000);

		return resp.get();
	}

	/**
	 * Sends the given given control command.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs while writing the message
	 */
	protected void sendCmd(AbstractControlCmd message) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		DataOutput output = new DataOutputStream(bo);
		output.writeByte(COM_PORT_OPTION);
		output.writeByte(message.getCommandCode());
		message.write(output);
		int[] bytes = toIntArray(bo.toByteArray());
		telnetClient.sendSubnegotiation(bytes);
	}

	/**
	 * Remove the previous received Control Command of the given Type, and return it.
	 * <code>null</code> will be returned if no Control Command was removed.
	 */
	@SuppressWarnings("unchecked")
	private <T extends AbstractControlCmd> T removeResponse(final Class<T> commandType) {
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
