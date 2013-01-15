/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 11.01.2013 13:13:16
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi.structs;

import static org.xidobi.OS.OS;

/**
 * A pointer to an HKEY in C.
 * 
 * @author Tobias Breﬂler
 */
public class HKEY {

	/** The size of the HKEY struct */
	private static int SIZE_OF;

	/** The pointer to the C instance */
	private final int cPointer;

	private boolean isDisposed = false;

	static {
		SIZE_OF = OS.sizeOf_HKEY();
	}

	/**
	 * Creates a new instance on the heap. The instance must be disposed, when it isn't used
	 * anymore.
	 */
	public HKEY() {
		cPointer = OS.malloc(SIZE_OF);
	}

	/**
	 * Disposed this instance and frees the memory on the heap.
	 */
	public void dispose() {
		OS.free(cPointer);
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
