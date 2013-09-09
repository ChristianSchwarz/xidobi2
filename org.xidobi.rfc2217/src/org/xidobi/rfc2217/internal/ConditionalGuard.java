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

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Christian Schwarz
 */
public class ConditionalGuard {

	/** This lock guards the {@link #change}-Condition */
	private final Lock lock = new ReentrantLock();

	/** This condition is signaled everytime a option negotiation was received */
	private final java.util.concurrent.locks.Condition change = lock.newCondition();

	/** This interface is used to implement the specific behavior of will accept and will send */
	public interface Condition {

		boolean isSatisfied();

	}

	/**
	 * Returns <code>true</code> if the loop finished because the option was accepted return
	 * <code>false</code> if an timeout was detected.
	 */
	public boolean awaitUninterruptibly(Condition condition, long timeoutMs) {
		long startTime = currentTimeMillis();

		long remainingTime = timeoutMs;
		do {
			if (condition.isSatisfied())
				return true;

			awaitNotification(remainingTime);

			final long elapsedMs = currentTimeMillis() - startTime;
			remainingTime = max(timeoutMs - elapsedMs, 0);

		}
		while (remainingTime > 0);

		return false;

	}

	/**
	 * Waits for any option notification, send by the access server.
	 */
	private void awaitNotification(long remainingTime) {
		lock.lock();
		try {
			change.await(remainingTime, MILLISECONDS);
		}
		catch (InterruptedException ignore) {}
		finally {
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	public void signalAll() {
		lock.lock();
		try {
			change.signalAll();
		}
		finally {
			lock.unlock();
		}
	}
}