/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 12:51:21
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

/**
 * Utility to check preconditions.
 * 
 * @author Christian Schwarz
 * 
 */
public class Preconditions {
	/**
	 * This class can not be instantiated
	 */
	private Preconditions() {}

	/**
	 * Return the argument {@code arg} if it is not null, otherwise an
	 * {@link IllegalArgumentException} will be thrown. In that case {@code argName} will be used in
	 * the description as the argument.
	 * 
	 * <pre>
	 * 	checkNotNull("xy","arg1") -> returns "xy";
	 * 	checkNotNull(null,"arg1") -> throws new IllegalArgumentException("The argument >arg1< must not be null!");
	 * </pre>
	 * 
	 * @param arg
	 *            the value to check
	 * @param argName
	 *            the name of argument {@code arg}
	 * @return {@code arg}
	 */
	public static <T> T checkNotNull(T arg, String argName) {
		if (arg == null)
			throw new IllegalArgumentException("The argument " + wrap(argName) + "must not be null!");
		return arg;
	}
	
	private static String wrap(String argName){
		if (argName==null)
			return "";
		return '>'+argName+"< ";
	}
}
