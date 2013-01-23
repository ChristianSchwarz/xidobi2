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

import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.xidobi.WinApi;

/**
 * Java representation of a byte array in C.
 * 
 * @author Tobias Breﬂler
 */
public class NativeByteArray {

	/** the native Win32-API, never <code>null</code> */
	private WinApi win;

	/** The pointer to the C struct */
	private final int cPointer;

	/** <code>true</code> if the instance is disposed */
	private boolean isDisposed = false;

	/** the size of the native byte array */
	private final int length;

	/**
	 * Creates a new byte array instance on the heap. The instance must be disposed, when it isn't
	 * used anymore.
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 * @param length
	 *            the size of the native byte array, must be greater than 0
	 */
	public NativeByteArray(	@Nonnull WinApi win,
							@Nonnegative int length) {
		this.win = checkArgumentNotNull(win, "win");
		checkArgument(length > 0, "length", "Expected a value greater than 0");
		this.length = length;

		cPointer = win.malloc(length);
	}

	/**
	 * Returns the size of the native byte array.
	 * 
	 * @return size of byte array
	 */
	public int length() {
		return length;
	}

	public byte[] getByteArray() {
		return win.getByteArray(this, length);
	}

	public byte[] getByteArray(int size) {
		checkArgument(size > 0, "size", "Expected a value greater than 0");
		checkArgument(size <= length, "size", "Expected a value lesser than or equal to length");

		return win.getByteArray(this, size);
	}

	/**
	 * Frees the resources of this instance ( memory on the heap).
	 */
	public void dispose() {
		win.free(cPointer);
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
