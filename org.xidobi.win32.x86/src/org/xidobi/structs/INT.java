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
package org.xidobi.structs;

import static java.lang.String.valueOf;

/**
 * Wrapper for an int value in C, which can be used as parameter for native methods.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class INT {

	/** the shared int value */
	public int value;

	/**
	 * Creates a new pointer to an int value in C.
	 */
	public INT() {}

	/**
	 * Creates a new pointer to an int value in C.
	 * 
	 * @param value
	 *            the shared int value
	 */
	public INT(int value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return value == ((INT) obj).value;
	}

	@Override
	public String toString() {
		return valueOf(value);
	}
}
