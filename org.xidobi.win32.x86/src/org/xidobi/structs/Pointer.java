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

import static org.xidobi.spi.Preconditions.checkArgument;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import org.xidobi.WinApi;

/**
 * A pointer to an allocated memory on the heap.
 * 
 * @author Tobias Breﬂler
 */
public class Pointer {

	/** the native Win32-API, never <code>null</code> */
	private final WinApi win;

	/** The pointer to the allocated memory */
	private final int cPointer;

	/** the size of the allocated memory */
	private final int size;

	/** <code>true</code> if the instance is disposed */
	private boolean isDisposed = false;

	/**
	 * Allocates memory of the given size on the heap and stores a pointer to that memory.
	 * <p>
	 * <b>Note:</b> The instance must be disposed, when it isn't used anymore!
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 * @param size
	 *            the size of the memory, must be greater than 0
	 */
	public Pointer(	WinApi win,
					int size) {
		this.win = checkArgumentNotNull(win, "win");
		checkArgument(size > 0, "size", "Expected a value greater than 0");
		this.size = size;

		// allocate memory
		cPointer = win.malloc(size);
		// set all bytes to zero
		win.memset(cPointer, 0, size());
	}

	/**
	 * Returns the size of the allocated memory.
	 * 
	 * @return the size
	 */
	public int size() {
		checkIfDisposed();
		return size;
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

	/** Throws an {@link IllegalStateException} when this instance is disposed. */
	protected void checkIfDisposed() {
		if (isDisposed)
			throw new IllegalStateException("This instance was already disposed!");
	}

	/**
	 * Frees the resources of this instance (memory on the heap).
	 */
	public void dispose() {
		checkIfDisposed();
		win.free(cPointer);
		isDisposed = true;
	}

	/**
	 * Returns the native Win32-API.
	 * 
	 * @return Win32-API, never <code>null</code>
	 */
	protected WinApi getWinApi() {
		return win;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (!isDisposed)
			dispose();
	}

}
