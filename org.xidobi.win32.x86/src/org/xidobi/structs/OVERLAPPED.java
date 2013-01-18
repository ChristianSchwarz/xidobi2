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
 * Java representation of the C-struct OVERLAPPED.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class OVERLAPPED {

	/** the native Win32-API, never <code>null</code> */
	private WinApi os;

	/** The pointer to the C struct */
	private final int cPointer;

	/** <code>true</code> if the instance is disposed */
	private boolean isDisposed = false;

	// ULONG_PTR
	// public long Internal;
	// ULONG_PTR
	// public long InternalHigh;

	// __GNUC_EXTENSION union {
	// __GNUC_EXTENSION struct {
	// DWORD
	// public int Offset;
	// DWORD
	// public int OffsetHigh;
	// };
	// PVOID
	// public int Pointer;
	// };

	/** {@code HANDLE} - Event handle */
	public int hEvent;

	/**
	 * Creates a new instance on the heap. The instance must be disposed, when it isn't used
	 * anymore.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public OVERLAPPED(WinApi os) {
		this.os = checkArgumentNotNull(os, "os");

		int sizeofOVERLAPPED = os.sizeOf_OVERLAPPED();
		cPointer = os.malloc(sizeofOVERLAPPED);
	}

	/**
	 * Frees the resources of this instance ( memory on the heap).
	 */
	public void dispose() {
		os.free(cPointer);
		isDisposed = true;
	}

	/**
	 * Returns <code>true</code>, if this instance was disposed.
	 * 
	 * @return <ul>
	 *         <li> <code>true</code>, if the instance was disposed
	 *         <li> <code>false</code>, if the instance is not disposed
	 *         </ul>
	 */
	public boolean isDisposed() {
		return isDisposed;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (!isDisposed)
			dispose();
	}

}
