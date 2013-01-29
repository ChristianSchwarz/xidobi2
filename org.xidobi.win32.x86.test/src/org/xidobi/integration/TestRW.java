/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 28.01.2013 10:14:42
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.integration;

import static org.xidobi.SerialPortSettings.from9600_8N1;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.FILE_FLAG_NO_BUFFERING;
import static org.xidobi.WinApi.FILE_FLAG_OVERLAPPED;
import static org.xidobi.WinApi.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.xidobi.WinApi.FORMAT_MESSAGE_IGNORE_INSERTS;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.INFINITE;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.LANG_NEUTRAL;
import static org.xidobi.WinApi.OPEN_EXISTING;
import static org.xidobi.WinApi.SUBLANG_NEUTRAL;

import java.io.IOException;

import org.junit.Test;
import org.xidobi.DCBConfigurator;
import org.xidobi.OS;
import org.xidobi.WinApi;
import org.xidobi.structs.COMSTAT;
import org.xidobi.structs.DCB;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

/**
 * @author Christian Schwarz
 * 
 */
public class TestRW {
	private static WinApi os = OS.OS;
	private DCBConfigurator configurator = new DCBConfigurator();

	/**
	 * 
	 */
	@Test
	public void read() throws Exception {

		int portHandle = os.CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED | FILE_FLAG_NO_BUFFERING, 0);
		if (portHandle == INVALID_HANDLE_VALUE)
			throw new IOException("Invalid handle! " + getNativeErrorMessage(os.getPreservedError()));

		os.PurgeComm(portHandle, WinApi.PURGE_RXCLEAR);

		boolean setCommMaskResult = os.SetCommMask(portHandle, EV_RXCHAR);
		if (!setCommMaskResult)
			throw new IOException("SetCommMask failed: " + getNativeErrorMessage(os.getPreservedError()));

		final DCB dcb = new DCB();

		if (!os.GetCommState(portHandle, dcb))
			throw new IOException("Unable to retrieve the current control settings for port (COM1)!");

		configurator.configureDCB(dcb, from9600_8N1().create());
		if (!os.SetCommState(portHandle, dcb))
			throw new IOException("Unable to set the control settings!");

		while (true) {
			read(portHandle);
			System.err.println("-- Read finished! --");
		}
	}

	/**
	 * @param portHandle
	 * @throws IOException
	 */
	private void read(int portHandle) throws IOException {
		int lastError;
		OVERLAPPED ov = new OVERLAPPED(os);
		DWORD dword = new DWORD(os);
		ov.hEvent = os.CreateEventA(0, true, false, null);

		try {
			if (ov.hEvent == 0) {
				lastError = os.getPreservedError();
				throw new IOException("CreateEventA failed! " + getNativeErrorMessage(lastError));
			}

			System.err.println("WaitCommEvent");
			boolean waitCommEvent = os.WaitCommEvent(portHandle, dword, ov);
			lastError = os.getPreservedError();

			if (!waitCommEvent && lastError != ERROR_IO_PENDING)
				throw new IOException("WaitCommEvent failed! " + getNativeErrorMessage(lastError));

			System.err.println("WaitForSingleObject");
			int waitForSingleObject = os.WaitForSingleObject(ov.hEvent, INFINITE);
			DWORD evtResult = new DWORD(os);
			System.err.println("GetOverlappedResult");
			os.GetOverlappedResult(portHandle, ov, evtResult, true);
			System.err.println("GetOverlappedResult = " + evtResult.getValue());
			System.err.println("GetOverlappedResult = " + dword.getValue());

			COMSTAT lpStat = new COMSTAT();
			os.ClearCommError(portHandle, new INT(0), lpStat);
			int availableBytes = lpStat.cbInQue;
			System.err.println("COMSTAT.cbInQue = " + availableBytes);
			if (availableBytes == 0)
				return;

			switch (waitForSingleObject) {
				case WinApi.WAIT_OBJECT_0:
					DWORD lpNumberOfBytesRead = new DWORD(os);
					NativeByteArray lpBuffer = new NativeByteArray(os, availableBytes);
					System.err.println("ReadFile");
					boolean readFile = os.ReadFile(portHandle, lpBuffer, lpBuffer.size(), lpNumberOfBytesRead, ov);
					if (!readFile) {
						System.err.println("WaitForSingleObject");
						os.WaitForSingleObject(ov.hEvent, 100);
						os.GetOverlappedResult(portHandle, ov, lpNumberOfBytesRead, true);
					}
					System.err.println("lpNumberOfBytesRead = " + lpNumberOfBytesRead.getValue());
					if (lpNumberOfBytesRead.getValue() > 0) {
						byte[] data = lpBuffer.getByteArray(lpNumberOfBytesRead.getValue());
						System.out.println(new String(data));
						System.out.println("___________________________");
						System.out.flush();
					}
					lpBuffer.dispose();
					lpNumberOfBytesRead.dispose();
					break;
				default:
					throw new IOException("WaitForSingleObject failed!");
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
					dword.dispose();
				}
			}
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
}
