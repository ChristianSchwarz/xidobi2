/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 28.01.2013 10:14:42
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.integration;

import java.io.IOException;

import org.junit.Test;
import org.xidobi.DCBConfigurator;
import org.xidobi.OS;
import org.xidobi.SerialPortSettings;
import org.xidobi.WinApi;
import org.xidobi.structs.DCB;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

import static java.nio.charset.Charset.forName;
import static org.xidobi.SerialPortSettings.from9600_8N1;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.ERROR_SUCCESS;
import static org.xidobi.WinApi.FILE_FLAG_NO_BUFFERING;
import static org.xidobi.WinApi.FILE_FLAG_OVERLAPPED;
import static org.xidobi.WinApi.FILE_FLAG_WRITE_THROUGH;
import static org.xidobi.WinApi.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.xidobi.WinApi.FORMAT_MESSAGE_IGNORE_INSERTS;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.INFINITE;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.LANG_NEUTRAL;
import static org.xidobi.WinApi.OPEN_EXISTING;
import static org.xidobi.WinApi.SUBLANG_NEUTRAL;
import static org.xidobi.WinApi.WAIT_OBJECT_0;

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

		int portHandle = os.CreateFile("\\\\.\\COM1", GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);
		if (portHandle == INVALID_HANDLE_VALUE)
			throw new IOException("Invalid handle! " + getNativeErrorMessage(os.getPreservedError()));

		final DCB dcb = new DCB();

		if (!os.GetCommState(portHandle, dcb))
			throw new IOException("Unable to retrieve the current control settings for port (COM1)!");

		configurator.configureDCB(dcb, from9600_8N1().create());

		if (!os.SetCommState(portHandle, dcb))
			throw new IOException("Unable to set the control settings!");

		final int eventHandle = os.CreateEventA(0, true, false, null);
		if (eventHandle == 0)
			throw new IOException("CreateEventA returned unexpected with 0! " + getNativeErrorMessage(os.getPreservedError()));

		OVERLAPPED overlapped = new OVERLAPPED(os);
		overlapped.hEvent = eventHandle;

		NativeByteArray readBuffer = new NativeByteArray(os, 256);
		int i=0;
		while (true) {
			i++;
			write(portHandle, (""+i).getBytes("UTF-8"), overlapped);

			//System.out.println("<<" + new String(bytesRead, forName("UTF-8")));
		}
	}

	/**
	 * 
	 * @param portHandle
	 * @param bytesToWrite
	 * @param overlapped
	 * @throws IOException
	 */
	private void write(int portHandle, byte[] bytesToWrite, OVERLAPPED overlapped) throws IOException {

		INT numberOfBytesRead = new INT(0);
		boolean succeed = os.WriteFile(portHandle, bytesToWrite, bytesToWrite.length, numberOfBytesRead, overlapped);

		int lastError = os.getPreservedError();
		if (lastError != ERROR_IO_PENDING && lastError != ERROR_SUCCESS)
			throw new IOException("ReadFile failed unexpected! " + getNativeErrorMessage(lastError));

		int waitResult = os.WaitForSingleObject(overlapped.hEvent, INFINITE);

		if (waitResult != WAIT_OBJECT_0)
			throw new IOException("WaitForSingleObject failed! " + waitResult + " " + getNativeErrorMessage(os.getPreservedError()));

		succeed = os.GetOverlappedResult(portHandle, overlapped, numberOfBytesRead, true);
		if (!succeed)
			throw new IOException("GetOverlappedResult failed! " + getNativeErrorMessage(os.getPreservedError()));

	}

	/**
	 * @param os
	 * @param portHandle
	 * @param eventHandle
	 * @param overlapped
	 * @param readBuffer
	 * @return
	 * @throws IOException
	 */
	private byte[] read(int portHandle, OVERLAPPED overlapped, NativeByteArray readBuffer) throws IOException {

		INT numberOfBytesRead = new INT(0);
		boolean succeed = os.ReadFile(portHandle, readBuffer, readBuffer.size(), numberOfBytesRead, overlapped);

		int lastError = os.getPreservedError();
		if (lastError != ERROR_IO_PENDING && lastError != ERROR_SUCCESS)
			throw new IOException("ReadFile failed unexpected! " + getNativeErrorMessage(lastError));

		int waitResult = os.WaitForSingleObject(overlapped.hEvent, INFINITE);

		if (waitResult != WAIT_OBJECT_0)
			throw new IOException("WaitForSingleObject failed! " + waitResult + " " + getNativeErrorMessage(os.getPreservedError()));

		succeed = os.GetOverlappedResult(portHandle, overlapped, numberOfBytesRead, true);
		if (!succeed)
			throw new IOException("GetOverlappedResult failed! " + getNativeErrorMessage(os.getPreservedError()));

		byte[] bytesRead = readBuffer.getByteArray(numberOfBytesRead.value);
		return bytesRead;
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
