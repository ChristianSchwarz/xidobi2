/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 12:54:20
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests the class {@link Preconditions}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestPreconditions {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Verifies that the argument is returned if it is not <code>null</code>
	 */
	@Test
	public void checkNotNull_nonNullValue() {
		String result = Preconditions.checkNotNull("i'am not null", "argName");
		assertThat(result, is("i'am not null"));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, if the argument is
	 * <code>null</code>.
	 */
	@Test
	public void checkNotNull_nullValue()  {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The argument >argName< must not be null!");
		
		Preconditions.checkNotNull(null, "argName");
	}
}
