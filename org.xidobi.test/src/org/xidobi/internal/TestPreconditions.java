/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 12:54:20
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests the class {@link Preconditions}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestPreconditions {

	/**
	 * Verifies that the argument is returned if it is not <code>null</code>
	 */
	@Test
	public void checkNotNull_nonNullValue() {
		String result = Preconditions.checkNotNull("i'am not null", "argName");
		assertThat(result, is("i'am not null"));
	}
}
