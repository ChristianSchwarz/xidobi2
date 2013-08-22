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

import org.apache.commons.net.telnet.TelnetClient;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.CommandProcessor;
import org.xidobi.rfc2217.internal.UpdatingGuard.Predicate;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmd;

import static org.xidobi.rfc2217.internal.ArrayUtil.toIntArray;

/**
 * @author Christian Schwarz
 *
 */
public class BlockingCommandSender implements CommandProcessor{

	
	private final Map<Class<?>, AbstractControlCmd> responses = new HashMap<Class<?>, AbstractControlCmd>() ;
	UpdatingGuard guard = new UpdatingGuard();
	
	private TelnetClient telnetClient;
	/**
	 * 
	 */
	public BlockingCommandSender(TelnetClient telnetClient) {
		this.telnetClient = telnetClient;
		
	}
	
	public <T extends AbstractControlCmd> T send(T req) throws IOException{
		removeResponse(req.getClass());
		sendCmd(req);
		return  (T)awaitResponse(req.getClass());
	}
	
	
	

	/**
	 * @param setBaudrate
	 * @return 
	 * @throws IOException 
	 */
	private <T extends AbstractControlCmd> T  awaitResponse(final Class<T> commandType) throws IOException {
		final AtomicReference<T> resp = new AtomicReference<T>();
		Predicate condition = new Predicate() {
			
			public boolean isSatisfied() {
				final T cmdResp = (T)removeResponse(commandType);
				resp.set(cmdResp);
				return cmdResp!=null;
			}
		};
		
		guard.awaitUninterruptibly(condition, 1000);
	
		return resp.get();
	}

	/**
	 * @param is 
	 * @throws IOException
	 */
	protected void sendCmd(AbstractControlCmd message) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		DataOutput l = new DataOutputStream(bo);
		message.write(l);
		int[] bytes = toIntArray(bo.toByteArray());
		telnetClient.sendSubnegotiation(bytes);
	}

	/**
	 * @param command
	 */
	protected <T extends AbstractControlCmd> T removeResponse(final Class<T> commandType) {
		return (T) responses.remove(commandType);
		
	}

	public void onResponseReceived(AbstractControlCmd response) {
		guard.signalAll();
	}
	
	
}
