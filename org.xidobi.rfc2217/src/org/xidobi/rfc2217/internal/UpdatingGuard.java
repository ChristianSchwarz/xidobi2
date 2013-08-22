/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 12:57:26
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Christian Schwarz
 * 
 */
public class UpdatingGuard  {

	/** This lock guards the {@link #change}-Condition */
	private final Lock lock = new ReentrantLock();

	/** This condition is signaled everytime a option negotiation was received */
	private final Condition change = lock.newCondition();

	/** This interface is used to implement the specific behavior of will accept and will send */
	public static interface Predicate {
		boolean isSatisfied();

	}

	/**
	 * Returns <code>true</code> if the loop finished because the option was accepted return
	 * <code>false</code> if an timeout was detected, or throws an {@link IOException} if the option
	 * was refused.
	 */
	protected boolean awaitUninterruptibly(Predicate condition, long timeoutMs) throws IOException {
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
	public void checkCondition() {
		lock.lock();
		try {
			change.signalAll();
		}
		finally {
			lock.unlock();
		}
	}
}