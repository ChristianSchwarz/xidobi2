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
package org.xidobi.internal;

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
	 * Ensures that argument {@code arg} is not null. If it is <code>null</code> an
	 * {@link IllegalArgumentException} will be thrown the {@code argName} will be used in
	 * the error description.
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
	public static <T> T checkArgumentNotNull(T arg, String argName) {
		if (arg == null)
			throw new IllegalArgumentException("The argument " + wrap(argName) + "must not be null!");
		return arg;
	}

	/** Wraps the given {@code argName} with '&lt;' and '&gt;'. */
	private static String wrap(String argName) {
		if (argName == null)
			return "";
		return '>' + argName + "< ";
	}
}
