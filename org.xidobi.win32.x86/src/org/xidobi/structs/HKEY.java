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
 * <p>
 * A HKEY is a handle to an open registry key.
 * 
 * @author Tobias Breﬂler
 */
public class HKEY extends Pointer {

	/**
	 * Creates a new instance on the heap. The instance must be disposed, when it isn't used
	 * anymore.
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public HKEY(WinApi win) {
		super(win, sizeOfHKEY(win));
	}

	/** Returns the size of an HKEY struct. */
	private static int sizeOfHKEY(WinApi win) {
		checkArgumentNotNull(win, "win");
		return win.sizeOf_HKEY();
	}

}
