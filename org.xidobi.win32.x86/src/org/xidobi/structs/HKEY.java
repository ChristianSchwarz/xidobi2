/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 11.01.2013 13:13:16
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi.structs;

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import org.xidobi.OS;

/**
 * A pointer to an HKEY in C.
 * 
 * @author Tobias Breﬂler
 */
public class HKEY {

	/** the native Win32-API, never <code>null</code> */
	private OS os;

	/** The pointer to the C instance */
	private final int cPointer;

	private boolean isDisposed = false;

	/**
	 * Creates a new instance on the heap. The instance must be disposed, when it isn't used
	 * anymore.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public HKEY(OS os) {
		this.os = checkArgumentNotNull(os, "os");

		int sizeOfHKEY = os.sizeOf_HKEY();
		cPointer = os.malloc(sizeOfHKEY);
	}

	/**
	 * Disposed this instance and frees the memory on the heap.
	 */
	public void dispose() {
		os.free(cPointer);
		isDisposed = true;
	}

	/**
	 * Returns <code>true</code>, if this instance is disposed.
	 * 
	 * @return <ul>
	 *         <li> <code>true</code>, if the instance is disposed
	 *         <li> <code>false</code>, if the instance is not disposed
	 *         </ul>
	 */
	public boolean isDisposed() {
		return isDisposed;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!isDisposed)
			dispose();
		super.finalize();
	}
}
