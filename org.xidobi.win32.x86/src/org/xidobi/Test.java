/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 03.01.2013 13:40:09
 * Erstellt von: Tobias Breﬂler
 */
package org.xidobi;

import static org.xidobi.OS.FILE_FLAG_OVERLAPPED;
import static org.xidobi.OS.GENERIC_READ;
import static org.xidobi.OS.GENERIC_WRITE;
import static org.xidobi.OS.OPEN_EXISTING;

/**
 *
 *
 * @author Tobias Breﬂler
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int handle = OS.CreateFile("\\\\.\\COM1", 
		              GENERIC_READ | GENERIC_WRITE, 
		              0,
		              0, 
		              OPEN_EXISTING, 
		              FILE_FLAG_OVERLAPPED, 
		              0);
		System.out.println("Handle: " + handle);
		
		DCB dcb = new DCB();
		OS.GetCommState(handle, dcb);
		System.out.println("BaudRate: " + dcb.BaudRate);
		
		boolean status = OS.CloseHandle(handle);
		System.out.println("Status: " + status);
	}

}
