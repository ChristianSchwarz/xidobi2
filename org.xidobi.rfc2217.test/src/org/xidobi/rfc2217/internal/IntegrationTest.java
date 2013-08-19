/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 11:31:13
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetInputListener;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.omg.CORBA.TCKind;
import org.xidobi.rfc2217.internal.BinaryOptionHandler;
import org.xidobi.rfc2217.internal.ComPortOptionHandler;
import org.xidobi.rfc2217.internal.SerialConnectionImpl;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Christian Schwarz
 * 
 */
public class IntegrationTest {

	/**
	 * @author Christian Schwarz
	 *
	 */
	private  final class NegotiationListener implements TelnetNotificationHandler {
		public void receivedNegotiation(int negotiation_code, int option_code) {
			
			 String txt= null;
			
			switch (negotiation_code) {
				case TelnetNotificationHandler.RECEIVED_DO:
					txt = "DO "+option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_DONT:
					txt = "DONT "+option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_WILL:
					txt = "WILL "+option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_WONT:
					txt = "WONT "+option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_COMMAND:
					txt = "COMMAND "+option_code;
					break;
			}
			System.out.println(txt);
			
			optionSupport.put(option_code,negotiation_code);
			lock.tryLock();
			try{
				negotiationReceived.signal();
			}finally{
				lock.unlock();
			}
		}
	}
	private ReentrantLock lock = new ReentrantLock();
	private Condition negotiationReceived = lock.newCondition();
	

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */

	private SerialConnectionImpl port;

	@Mock
	private SerialConnectionImpl connection;
	
	private final  Map<Integer, Integer> optionSupport = new HashMap<Integer, Integer>();

	@Before
	public void setUp() {
		initMocks(this);

	}

	/**
	 * 
	 */
	@Test
	
	public void testName() throws Exception {

		TelnetClient telnetClient = new TelnetClient();
		telnetClient.setReaderThread(true);
		telnetClient.addOptionHandler(new BinaryOptionHandler());
		telnetClient.addOptionHandler(new ComPortOptionHandler());
		telnetClient.registerNotifHandler(new NegotiationListener());
		telnetClient.registerInputListener(new TelnetInputListener(){

			public void telnetInputAvailable() {
				System.out.println("telnetInputAvailable");
			}});
		
		
		System.out.println("connect");
		telnetClient.connect("192.168.98.31");
		
		lock.tryLock();
		try{
			negotiationReceived.await(15,TimeUnit.SECONDS);
		}finally{
			lock.unlock();
		}
		
		System.out.println(""+optionSupport);
	}
}
