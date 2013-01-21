/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 11.01.2013 13:13:16
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi.structs;

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import org.xidobi.WinApi;

/**
 * A pointer to an HKEY in C.
 * 
 * @author Tobias Breﬂler
 */
public class HKEY {

	/** the native Win32-API, never <code>null</code> */
	private WinApi win;

	/** The pointer to the C instance */
	private final int cPointer;

	private boolean isDisposed = false;

	/**
	 * Creates a new instance on the heap. The instance must be disposed, when it isn't used
	 * anymore.
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public HKEY(WinApi win) {
		this.win = checkArgumentNotNull(win, "win");

		int sizeOfHKEY = win.sizeOf_HKEY();
		cPointer = win.malloc(sizeOfHKEY);
	}

	/**
	 * Disposed this instance and frees the memory on the heap.
	 */
	public void dispose() {
		win.free(cPointer);
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
