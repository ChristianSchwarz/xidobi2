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

import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import org.xidobi.WinApi;

/**
 * Pointer to a DWORD in C.
 * 
 * @author Tobias Breﬂler
 */
public class DWORD extends Pointer {

	/**
	 * Creates a new pointer to a DWORD value in C.
	 * <p>
	 * <b>Note:</b> The instance must be disposed, when it isn't used anymore!
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public DWORD(WinApi os) {
		super(os, sizeOfDWORD(os));
	}

	/** Returns the size of a DWORD. */
	private static int sizeOfDWORD(WinApi os) {
		checkArgumentNotNull(os, "os");
		return os.sizeOf_DWORD();
	}

	/**
	 * Returns the value of the DWORD pointer.
	 * 
	 * @return the value
	 */
	public int getValue() {
		checkIfDisposed();
		return getWinApi().getValue_DWORD(this);
	}

	/**
	 * Sets the value of the DWORD pointer.
	 * 
	 * @param value
	 *            the value
	 */
	public void setValue(int value) {
		checkIfDisposed();
		getWinApi().setValue_DWORD(this, value);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
}
