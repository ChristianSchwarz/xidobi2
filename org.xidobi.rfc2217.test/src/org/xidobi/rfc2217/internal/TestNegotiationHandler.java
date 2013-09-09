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

import java.io.IOException;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TelnetOption;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations.Mock;

import static java.lang.System.currentTimeMillis;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_DO;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_DONT;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_WILL;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_WONT;
import static org.apache.commons.net.telnet.TelnetOption.BINARY;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

/**
 * Tests the class {@link NegotiationHandler}
 * 
 * @author Christian Schwarz
 */
public class TestNegotiationHandler {

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	@InjectMocks
	private NegotiationHandler handler;

	@Mock
	private TelnetClient telnetClient;

	@Captor
	private ArgumentCaptor<TelnetNotificationHandler> notificationHandler;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		initMocks(this);
		verify(telnetClient).registerNotifHandler(notificationHandler.capture());
	}

	/**
	 * If <code>null</code> is passed to the constructor an {@link IllegalArgumentException} must be
	 * thrown.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void new_nullTelnetClient() {
		new NegotiationHandler(null);

	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must return immediatly if
	 * the access server signaled that it is willing to accept the option, before the method was
	 * called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_acceptedBeforeCall() throws IOException {
		notifyNegotiationReceived(RECEIVED_DO, BINARY);

		handler.awaitAcceptOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must throw an IOException
	 * if the access server signaled that it refused to accept the option, before the method was
	 * called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_refusedBeforeCall() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server refused to accept option: " + BINARY + "!");

		notifyNegotiationReceived(RECEIVED_DONT, BINARY);

		handler.awaitAcceptOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must return if the access
	 * server signaled that it is willing to accept the option, before the method was called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_refusedWhileWaiting() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server refused to accept option: " + BINARY + "!");

		notifyAsyncNegotiationReceived(RECEIVED_DONT, BINARY, 20);

		handler.awaitAcceptOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must return if the access
	 * server is willing to accept the option while waiting.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_acceptedWhileWaiting() throws IOException {
		notifyAsyncNegotiationReceived(RECEIVED_DO, BINARY, 20);

		handler.awaitAcceptOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must throw an IOException
	 * if the access server didn't answer within the given time if it is willing to accept the
	 * option.
	 * <p>
	 * No notification of an refused or accepted option is send in this test case.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 200)
	public void awaitWillAcceptOption_timeout() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option: " + BINARY + "!");

		long start = currentTimeMillis();
		handler.awaitAcceptOptionNegotiation(BINARY, 50);
		assertThat(currentTimeMillis() - start, is(lessThan(100L)));
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must throw an IOException
	 * if the negotiation of the given option timed out.
	 * <p>
	 * An irrelevant notification of an the interested option is send in this test case, while
	 * waiting.This must have no effect to the waiting method.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_timeout2() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option: " + BINARY + "!");

		notifyAsyncNegotiationReceived(RECEIVED_DO, TelnetOption.ECHO, 20);

		long start = currentTimeMillis();
		handler.awaitAcceptOptionNegotiation(BINARY, 50);
		assertThat(currentTimeMillis() - start, is(lessThan(100L)));
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must return immediatly if
	 * the access server signaled that it is willing to send the option, before the method was
	 * called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillSendOption_acceptedBeforeCall() throws IOException {
		notifyNegotiationReceived(RECEIVED_WILL, BINARY);

		handler.awaitSendOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must throw an IOException
	 * if the access server signaled that it refused to send the option, before the method was
	 * called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillSendOption_refusedBeforeCall() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server refused to send option: " + BINARY + "!");

		notifyNegotiationReceived(RECEIVED_WONT, BINARY);

		handler.awaitSendOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must return if the access
	 * server signaled that it is willing to send the option, before the method was called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillSendOption_refusedWhileWaiting() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server refused to send option: " + BINARY + "!");

		notifyAsyncNegotiationReceived(RECEIVED_WONT, BINARY, 20);

		handler.awaitSendOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must return if the access
	 * server is willing to send the option while waiting.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillSendOption_acceptedWhileWaiting() throws IOException {
		notifyAsyncNegotiationReceived(RECEIVED_WILL, BINARY, 20);

		handler.awaitSendOptionNegotiation(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must throw an IOException
	 * if the access server didn't answer within the given time if it is willing to accept or refuse
	 * the option.
	 * <p>
	 * No notification of an refused or accepted option is received in this test case.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 200)
	public void awaitWillSendOption_timeout() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option: " + BINARY + "!");

		long start = currentTimeMillis();
		handler.awaitSendOptionNegotiation(BINARY, 50);
		assertThat(currentTimeMillis() - start, is(lessThan(100L)));
	}

	/**
	 * {@link NegotiationHandler#awaitAcceptOptionNegotiation(int, long)} must throw an IOException
	 * if the negotiation of the given option timed out.
	 * <p>
	 * An irrelevant notification of an the interested option is send in this test case, while
	 * waiting.This must have no effect to the waiting method.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillSendOption_timeout2() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option: " + BINARY + "!");

		notifyAsyncNegotiationReceived(RECEIVED_WILL, TelnetOption.ECHO, 20);

		long start = currentTimeMillis();
		handler.awaitSendOptionNegotiation(BINARY, 50);
		assertThat(currentTimeMillis() - start, is(lessThan(100L)));
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void notifyNegotiationReceived(int negotiationCode, int optionCode) {
		notificationHandler.getValue().receivedNegotiation(negotiationCode, optionCode);
	}

	protected void notifyAsyncNegotiationReceived(final int negotiationCode, final int optionCode, final long delayMs) {
		new Thread() {

			@Override
			public void run() {
				try {
					sleep(delayMs);
					notifyNegotiationReceived(negotiationCode, optionCode);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}