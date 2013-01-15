/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 12:51:21
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

/**
 * @author Christian Schwarz
 *
 */
public class Preconditions {
	/**
	 * This class can not be instantiated
	 */
	private Preconditions() {}
	
	
	public static <T> T checkNotNull(T arg, String argName){
		return arg;
	}
}
