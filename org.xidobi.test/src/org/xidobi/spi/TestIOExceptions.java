/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 02.09.2013 10:13:27
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.spi;

import java.io.IOException;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.Assert.assertThat;

/**
 * @author Christian Schwarz
 *
 */
public class TestIOExceptions {

	
	/**
	 * Verifies that {@link IoExceptions#portClosedException()} returns an
	 * {@link IOException} with a message 'Port was closed!'.
	 */
	@Test
	public void portClosedException() {
		IOException result = IoExceptions.portClosedException();

		assertThat(result, is(notNullValue()));
		assertThat(result.getMessage(), is("Port was closed!"));
	}
	
	/**
	 * Verifies that {@link IoExceptions#portClosedException()} returns an
	 * {@link IOException} with a message 'Port XXX was closed!'.
	 */
	@Test
	public void portClosedException_withPortName() {
		IOException result = IoExceptions.portClosedException("COM1");
		
		assertThat(result, is(notNullValue()));
		assertThat(result.getMessage(), is("Port COM1 was closed!"));
	}

	/**
	 * Verifies that {@link IoExceptions#portClosedException(String)} returns an
	 * {@link IOException} with a message 'Port XXX is closed!' and an additional message.
	 */
	@Test
	public void portClosedException_withPortNameAndMessage() {
		IOException result = IoExceptions.portClosedException("COM1","Additional message.");

		assertThat(result, is(notNullValue()));
		assertThat(result.getMessage(), is("Port COM1 was closed! Additional message."));
	}
}
