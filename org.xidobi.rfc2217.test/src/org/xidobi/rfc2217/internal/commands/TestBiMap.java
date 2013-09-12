/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 12.09.2013 10:55:09
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;

/**
 * @author Christian Schwarz
 *
 */
public class TestBiMap {
	
	private BiMap<String, Integer> map;

	@Before
	public void setUp(){
		map = new BiMap<String, Integer>();
	}
	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to RFC2217
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getRfc2217Equivalent_notMapped()  {

		assertThat(map.getRfc2217Equivalent("this a not mapped value"),is(nullValue()));
	}
	
	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to RFC2217
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getRfc2217Equivalent_null()  {
		map.addEquivalents(null, -1);
		
		assertThat(map.getRfc2217Equivalent(null),is(-1));
	}
	
	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to RFC2217
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getXidobiEquivalent_null()  {
		map.addEquivalents("-", null);
		
		assertThat(map.getXidobiEquivalent(null),is("-"));
	}
	
	/**
	 * An {@link IllegalArgumentException} must be thrown if a value has no mapping to a xidobi
	 * value. This ist mostly used to indicate a possible bug of the class that extends
	 * {@link AbstractControlCmd}.
	 */
	@Test
	public void getXidobiEquivalent_notMapped()  {
		assertThat(map.getXidobiEquivalent(null),is(nullValue()));
	}
}
