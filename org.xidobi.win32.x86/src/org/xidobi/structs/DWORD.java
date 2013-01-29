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

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import org.xidobi.WinApi;

/**
 * Pointer to an int value in C.
 * 
 * @author Tobias Breﬂler
 */
public class DWORD extends Pointer {

	/**
	 * Creates a new pointer to an int value in C.
	 */
	public DWORD(WinApi win) {
		super(win, sizeOfDWORD(win));
	}

	/** Returns the size of an DWORD. */
	private static int sizeOfDWORD(WinApi win) {
		checkArgumentNotNull(win, "win");
		return win.sizeOf_DWORD();
	}

}
