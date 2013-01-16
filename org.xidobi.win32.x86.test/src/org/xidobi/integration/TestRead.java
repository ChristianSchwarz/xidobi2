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
package org.xidobi.integration;

import static org.xidobi.OS.FILE_FLAG_OVERLAPPED;
import static org.xidobi.OS.GENERIC_READ;
import static org.xidobi.OS.GENERIC_WRITE;
import static org.xidobi.OS.OPEN_EXISTING;
import static org.xidobi.OS.WAIT_OBJECT_0;

import org.xidobi.OS;
import org.xidobi.structs.DCB;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

/**
 * 
 * 
 * @author Tobias Breßler
 * 
 */
@Deprecated
public class TestRead {

	/**
	 * 
	 */

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		
		boolean succeed;
		OS os = OS.OS;
		int handle = os.CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

		if (handle == -1) {
			System.err.println("No handle!");
			return;
		}
		System.out.println("Handle: " + handle);

		DCB dcb = new DCB();
		os.GetCommState(handle, dcb);
		dcb.BaudRate = 9600;
		os.SetCommState(handle, dcb);

		for (;;) {
			byte[] LP_BUFFER = new byte[9];

			println("-----------------");
			int eventHandle = os.CreateEventA(0, true, false, null);
			System.err.println("Error?" + os.GetLastError());
			println("Event-Handle: " + eventHandle);

			OVERLAPPED overlapped = new OVERLAPPED(os);
			overlapped.hEvent = eventHandle;

			INT lpNumberOfBytesRead = new INT();
			succeed = os.ReadFile(handle, LP_BUFFER, 9, lpNumberOfBytesRead, overlapped);
			System.err.println("Error?" + os.GetLastError());
			println("ReadFile->" + succeed + " bytes read: " + lpNumberOfBytesRead);

			int waitForSingleObject = os.WaitForSingleObject(eventHandle, 2000);
			System.err.println("Error?" + os.GetLastError());
			System.out.println("WaitForSingleObject->" + waitForSingleObject);

			if (waitForSingleObject == WAIT_OBJECT_0) {
				INT numberOfBytesRead = new INT();
				succeed = os.GetOverlappedResult(handle, overlapped, numberOfBytesRead, true);
				System.err.println("Error?" + os.GetLastError());
				println("GetOverlappedResult->" + succeed + " read:" + numberOfBytesRead);
				
				System.out.println("Read: " + new String(LP_BUFFER));
				
			} else {
				System.out.println("Wait error: " + waitForSingleObject);
			}
			
			overlapped.dispose();

			println("close eventHandle=" + eventHandle + " ->" + os.CloseHandle(eventHandle));
			
			Thread.sleep(100);

		}

//		println("close handle=" + handle + " ->" + CloseHandle(handle));
	}

	/**
	 * 
	 */
	private static void println(Object text) {
		System.out.println(text);
	}

}
