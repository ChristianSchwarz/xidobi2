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
package org.xidobi.spi;

/**
 * Utility to check preconditions.
 * 
 * @author Christian Schwarz
 */
public class Preconditions {

	/**
	 * This class can not be instantiated
	 */
	private Preconditions() {}

	/**
	 * Ensures that argument {@code arg} is not . If it is {@code null} an
	 * {@link IllegalArgumentException} will be thrown the {@code argName} will be used in the error
	 * description.
	 * 
	 * <pre>
	 *  String arg1="xy", arg2=null; 
	 * 	checkNotNull(arg1,"arg1") -> returns "xy";
	 * 	checkNotNull(arg2,"arg1") -> throws an IllegalArgumentException("Argument >arg1< must not be null!");
	 * </pre>
	 * 
	 * @param arg
	 *            the value to check
	 * @param argName
	 *            the name of argument {@code arg}, can be <code>null</code>
	 * @return {@code arg}
	 */
	public static <T> T checkArgumentNotNull(T arg, String argName) {
		if (arg == null)
			throw new IllegalArgumentException("Argument " + wrap(argName) + "must not be null!");
		return arg;
	}

	/**
	 * Ensures that the {@code condition} is {@code true}. If it is {@code false} an
	 * {@link IllegalArgumentException} will be thrown with a message containing the {@code argName}
	 * .
	 * 
	 * <pre>
	 *  int[] array = {1,2,3};
	 *  checkArgument(array.length<5,"array"); -> pass 
	 *  checkArgument(array.length>5,"array"); -> throws an IllegalArgumentException("Argument >array< is invalid!");
	 * </pre>
	 * 
	 * @param condition
	 *            the condition to be checked
	 * @param argName
	 *            the name of the argument, can be <code>null</code>
	 * 
	 */
	public static void checkArgument(boolean condition, String argName) {
		checkArgument(condition, argName, null);
	}

	/**
	 * Ensures that the {@code condition} is {@code true}. If it is {@code false} an
	 * {@link IllegalArgumentException} will be thrown with a message containing the {@code argName}
	 * and the {@code expecation}.
	 * 
	 * <pre>
	 *  int[] array = {1,2,3};
	 *  checkArgument(array.length<5,"array","Expected less than 5 elements!"); 
	 *    -> pass 
	 *  checkArgument(array.length>5,null,null); 
	 *    -> throws an IllegalArgumentException;
	 *  checkArgument(array.length>5,null,"Expected more than 5 elements!"); 
	 *    -> throws an IllegalArgumentException -> "Expected more than 5 elements";
	 *  checkArgument(array.length>5,"array",null); 
	 *    -> throws an IllegalArgumentException("Argument >array< is invalid!);
	 *  checkArgument(array.length>5,"array","Expected more than 5 elements!"); 
	 *    -> throws an IllegalArgumentException("Argument >array< is invalid! Expected more than 5 elements!");
	 * </pre>
	 * 
	 * @param condition
	 *            the condition to be checked
	 * @param argName
	 *            the name of the argument, can be <code>null</code>
	 * @param description
	 *            the description to be used when the {@code condition} is not <code>true</code>,
	 *            can be <code>null</code>
	 */
	public static void checkArgument(boolean condition, String argName, String description) {
		if (condition)
			return;

		final String msg;
		if (argName == null && description == null)
			msg = null;
		else if (argName == null)
			msg = description;
		else if (description == null)
			msg = "Argument >" + argName + "< is invalid!";
		else
			msg = "Argument >" + argName + "< is invalid! " + description;

		throw new IllegalArgumentException(msg);

	}

	/**
	 * Wraps the given String with '&lt;' and '&gt;' if it is not <code>null</code>, otherwise an
	 * empty String is returned.
	 */
	private static String wrap(String text) {
		if (text == null)
			return "";
		return '>' + text + "< ";
	}
}
