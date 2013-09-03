/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 11.01.2013 13:13:16
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi.structs;

import org.xidobi.WinApi;

import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

/**
 * A pointer to an HKEY in C.
 * <p>
 * A HKEY is a handle to an open registry key.
 * 
 * @author Tobias Breﬂler
 */
public class HKEY extends Pointer {

	/**
	 * Creates a new instance on the heap.
	 * <p>
	 * <b>Note:</b> The instance must be disposed, when it isn't used anymore!
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public HKEY(WinApi os) {
		super(os, sizeOfHKEY(os));
	}

	/** Returns the size of an HKEY struct. */
	private static int sizeOfHKEY(WinApi os) {
		checkArgumentNotNull(os, "os");
		return os.sizeOf_HKEY();
	}

}
