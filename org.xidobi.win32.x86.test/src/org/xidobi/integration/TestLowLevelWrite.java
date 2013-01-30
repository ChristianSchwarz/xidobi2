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

import static org.xidobi.SerialPortSettings.from9600_8N1;
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.FILE_FLAG_NO_BUFFERING;
import static org.xidobi.WinApi.FILE_FLAG_OVERLAPPED;
import static org.xidobi.WinApi.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.xidobi.WinApi.FORMAT_MESSAGE_IGNORE_INSERTS;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.LANG_NEUTRAL;
import static org.xidobi.WinApi.OPEN_EXISTING;
import static org.xidobi.WinApi.PURGE_RXCLEAR;
import static org.xidobi.WinApi.PURGE_TXABORT;
import static org.xidobi.WinApi.PURGE_TXCLEAR;
import static org.xidobi.WinApi.SUBLANG_NEUTRAL;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xidobi.DCBConfigurator;
import org.xidobi.OS;
import org.xidobi.WinApi;
import org.xidobi.structs.DCB;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.OVERLAPPED;

/**
 * Integration tests for the low level API.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class TestLowLevelWrite {

	private static WinApi os = OS.OS;
	private DCBConfigurator configurator = new DCBConfigurator();

	private int portHandle;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		portHandle = os.CreateFile("\\\\.\\COM75", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED | FILE_FLAG_NO_BUFFERING, 0);
		if (portHandle == INVALID_HANDLE_VALUE)
			throw new IOException("Invalid handle! " + getNativeErrorMessage(os.getPreservedError()));

		os.PurgeComm(portHandle, PURGE_RXCLEAR);

		boolean setCommMaskResult = os.SetCommMask(portHandle, EV_RXCHAR);
		if (!setCommMaskResult)
			throw new IOException("SetCommMask failed: " + getNativeErrorMessage(os.getPreservedError()));

		final DCB dcb = new DCB();

		if (!os.GetCommState(portHandle, dcb))
			throw new IOException("Unable to retrieve the current control settings for port (COM1)!");

		configurator.configureDCB(dcb, from9600_8N1().create());
		if (!os.SetCommState(portHandle, dcb))
			throw new IOException("Unable to set the control settings!");

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void write() throws Exception {
		while (true) {
			println("-- Start ------");
			int lastError;
			OVERLAPPED ov = new OVERLAPPED(os);
			DWORD lpNumberOfBytesRead = new DWORD(os);
			ov.hEvent = os.CreateEventA(0, true, false, null);

			try {
				if (ov.hEvent == 0) {
					lastError = os.getPreservedError();
					throw new IOException("CreateEventA failed! " + getNativeErrorMessage(lastError));
				}

				byte[] lpBuffer = "Dies ist ein bisschen Text!".getBytes();
				boolean readFile = os.WriteFile(portHandle, lpBuffer, lpBuffer.length, lpNumberOfBytesRead, ov);
				if (!readFile) {
					lastError = os.getPreservedError();
					if (lastError == WinApi.ERROR_IO_PENDING) {
						for (int i = 0; i < 5; i++) {
							println("WaitForSingleObject");
							int waitForSingleObject = os.WaitForSingleObject(ov.hEvent, 1000);
							if (waitForSingleObject == WinApi.WAIT_TIMEOUT) {
								println("GetOverlappedResult");
								os.GetOverlappedResult(portHandle, ov, lpNumberOfBytesRead, false);
								int bytesWritten = lpNumberOfBytesRead.getValue();
								println("Byte written: " + bytesWritten);
								continue;
							}
							println("=" + waitForSingleObject);
							break;
						}
						println("PurgeComm");
						os.PurgeComm(portHandle, PURGE_TXABORT | PURGE_TXCLEAR);
					}
				}
			}
			finally {
				try {
					os.CloseHandle(ov.hEvent);
				}
				finally {
					try {
						ov.dispose();
					}
					finally {
						lpNumberOfBytesRead.dispose();
					}
				}
			}
			println("-- Finished ---");
			Thread.sleep(100);
		}
	}

	/**
	 * Returns an error message for the given error code. If no message can be found, then
	 * "No error message available" is returned.
	 */
	private static String getNativeErrorMessage(int errorCode) {

		byte[] lpMsgBuf = new byte[255];
		//@formatter:off
		int result = os.FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS, 
		                                null, errorCode, os.MAKELANGID(LANG_NEUTRAL, SUBLANG_NEUTRAL), lpMsgBuf, 255, null);
		//@formatter:on
		if (result == 0)
			return "No error description available.";

		// cut bytes to the length (result) without trailing linebreaks
		// and convert to a String:
		return new String(lpMsgBuf, 0, result - 2);
	}

	private static void println(String text) {
		System.err.println(text);
	}
}