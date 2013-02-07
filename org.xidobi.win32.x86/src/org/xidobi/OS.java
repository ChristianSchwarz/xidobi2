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
package org.xidobi;

import static java.lang.System.loadLibrary;
import static java.lang.Thread.currentThread;

import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xidobi.structs.COMSTAT;
import org.xidobi.structs.DCB;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.HKEY;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

/**
 * This class contains one-to-one mappings of native methods used by the OS to control serial ports.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class OS implements WinApi {

	/** The name of the native shared library. */
	private static final String NATIVE_LIB = "xidobi";

	/**
	 * Stores the last native error codes. Contains:
	 * <ul>
	 * <li><b>key:</b> the thread
	 * <li><b>value:</b> the last native error code
	 * </ul>
	 */
	private final Map<Thread, Integer> lastNativeErrorCodes = new WeakHashMap<Thread, Integer>();

	/** The singleton instance of this class */
	public final static WinApi OS = new OS();

	/**
	 * This class is not intended to be instantiated.
	 * 
	 * @see #OS
	 */
	private OS() {
		try {
			loadLibrary(NATIVE_LIB);
			return;
		}
		catch (UnsatisfiedLinkError ignore) {
			throw new UnsatisfiedLinkError("Unable to find " + NATIVE_LIB + ".dll!\r\nYou must run in an OSGi enviroment!");
		}
	}

	/** Stores the last error code. */
	private void preserveLastError(INT lastError) {
		lastNativeErrorCodes.put(currentThread(), lastError.value);
	}

	/** {@inheritDoc} */
	@CheckReturnValue
	public int CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode, int lpSecurityAttributes, int dwCreationDisposition, int dwFlagsAndAttributes, int hTemplateFile) {
		INT lastError = new INT(0);
		int result = CreateFile(lpFileName, dwDesiredAccess, dwShareMode, lpSecurityAttributes, dwCreationDisposition, dwFlagsAndAttributes, hTemplateFile, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native int CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode, int lpSecurityAttributes, int dwCreationDisposition, int dwFlagsAndAttributes, int hTemplateFile, INT lastError);

	/** {@inheritDoc} */
	@CheckReturnValue
	public boolean CloseHandle(int handle) {
		INT lastError = new INT(0);
		boolean result = CloseHandle(handle, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean CloseHandle(int handle, INT lastError);

	/** {@inheritDoc} */
	@CheckReturnValue
	public boolean GetCommState(int handle, DCB dcb) {
		INT lastError = new INT(0);
		boolean result = GetCommState(handle, dcb, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean GetCommState(int handle, DCB dcb, INT lastError);

	/** {@inheritDoc} */
	@CheckReturnValue
	public boolean SetCommState(int handle, DCB dcb) {
		INT lastError = new INT(0);
		boolean result = SetCommState(handle, dcb, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean SetCommState(int handle, DCB dcb, INT lastError);

	/** {@inheritDoc} */
	@CheckReturnValue
	public int CreateEventA(int lpEventAttributes, boolean bManualReset, boolean bInitialState, @Nullable String lpName) {
		INT lastError = new INT(0);
		int result = CreateEventA(lpEventAttributes, bManualReset, bInitialState, lpName, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native int CreateEventA(int lpEventAttributes, boolean bManualReset, boolean bInitialState, @Nullable String lpName, INT lastError);

	/** {@inheritDoc} */
	@CheckReturnValue
	public boolean WriteFile(int handle, @Nonnull byte[] lpBuffer, int nNumberOfBytesToWrite, @Nullable DWORD lpNumberOfBytesWritten, @Nullable OVERLAPPED lpOverlapped) {
		INT lastError = new INT(0);
		boolean result = WriteFile(handle, lpBuffer, nNumberOfBytesToWrite, lpNumberOfBytesWritten, lpOverlapped, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean WriteFile(int handle, @Nonnull byte[] lpBuffer, int nNumberOfBytesToWrite, @Nullable DWORD lpNumberOfBytesWritten, @Nullable OVERLAPPED lpOverlapped, INT lastError);

	/** {@inheritDoc} */
	@CheckReturnValue
	public boolean ReadFile(int handle, @Nonnull NativeByteArray lpBuffer, int nNumberOfBytesToRead, @Nullable DWORD lpNumberOfBytesRead, OVERLAPPED lpOverlapped) {
		INT lastError = new INT(0);
		boolean result = ReadFile(handle, lpBuffer, nNumberOfBytesToRead, lpNumberOfBytesRead, lpOverlapped, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean ReadFile(int handle, @Nonnull NativeByteArray lpBuffer, int nNumberOfBytesToRead, @Nullable DWORD lpNumberOfBytesRead, OVERLAPPED lpOverlapped, INT lastError);

	/** {@inheritDoc} */
	public int GetLastError() {
		return lastNativeErrorCodes.get(currentThread());
	}

	/** {@inheritDoc} */
	public native int MAKELANGID(short usPrimaryLanguage, short usSubLanguage);

	/** {@inheritDoc} */
	public int FormatMessageA(int dwFlags, Void lpSource, int dwMessageId, int dwLanguageId, @Nonnull byte[] lpBuffer, int nSize, Void arguments) {
		INT lastError = new INT(0);
		int result = FormatMessageA(dwFlags, lpSource, dwMessageId, dwLanguageId, lpBuffer, nSize, arguments, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native int FormatMessageA(int dwFlags, Void lpSource, int dwMessageId, int dwLanguageId, @Nonnull byte[] lpBuffer, int nSize, Void arguments, INT lastError);

	/** {@inheritDoc} */
	public boolean GetOverlappedResult(int handle, OVERLAPPED lpOverlapped, DWORD lpNumberOfBytesTransferred, boolean bWait) {
		INT lastError = new INT(0);
		boolean result = GetOverlappedResult(handle, lpOverlapped, lpNumberOfBytesTransferred, bWait, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean GetOverlappedResult(int handle, OVERLAPPED lpOverlapped, DWORD lpNumberOfBytesTransferred, boolean bWait, INT lastError);

	/** {@inheritDoc} */
	public int WaitForSingleObject(int hHandle, int dwMilliseconds) {
		INT lastError = new INT(0);
		int result = WaitForSingleObject(hHandle, dwMilliseconds, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native int WaitForSingleObject(int hHandle, int dwMilliseconds, INT lastError);

	/** {@inheritDoc} */
	public native int RegOpenKeyExA(int hKey, String lpSubKey, int ulOptions, int samDesired, HKEY phkResult);

	/** {@inheritDoc} */
	public native int RegCloseKey(HKEY hKey);

	/** {@inheritDoc} */
	public native int RegEnumValueA(HKEY hKey, int dwIndex, byte[] lpValueName, INT lpcchValueName, int lpReserved, INT lpType, byte[] lpData, INT lpcbData);

	/** {@inheritDoc} */
	public boolean SetCommMask(int hFile, int dwEvtMask) {
		INT lastError = new INT(0);
		boolean result = SetCommMask(hFile, dwEvtMask, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean SetCommMask(int hFile, int dwEvtMask, INT lastError);

	/** {@inheritDoc} */
	public boolean WaitCommEvent(int hFile, DWORD lpEvtMask, OVERLAPPED lpOverlapped) {
		INT lastError = new INT(0);
		boolean result = WaitCommEvent(hFile, lpEvtMask, lpOverlapped, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean WaitCommEvent(int hFile, DWORD lpEvtMask, OVERLAPPED lpOverlapped, INT lastError);

	/** {@inheritDoc} */
	public boolean PurgeComm(int hFile, int dwFlags) {
		INT lastError = new INT(0);
		boolean result = PurgeComm(hFile, dwFlags, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean PurgeComm(int hFile, int dwFlags, INT lastError);

	/** {@inheritDoc} */
	public boolean ClearCommError(int hFile, INT lpErrors, COMSTAT lpStat) {
		INT lastError = new INT(0);
		boolean result = ClearCommError(hFile, lpErrors, lpStat, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean ClearCommError(int hFile, INT lpErrors, COMSTAT lpStat, INT lastError);

	/** {@inheritDoc} */
	public boolean ResetEvent(int hEvent) {
		INT lastError = new INT(0);
		boolean result = ResetEvent(hEvent, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean ResetEvent(int hEvent, INT lastError);

	/** {@inheritDoc} */
	public boolean SetCommBreak(int hFile) {
		INT lastError = new INT(0);
		boolean result = SetCommBreak(hFile, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean SetCommBreak(int hFile, INT lastError);

	/** {@inheritDoc} */
	public boolean ClearCommBreak(int hFile) {
		INT lastError = new INT(0);
		boolean result = ClearCommBreak(hFile, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean ClearCommBreak(int hFile, INT lastError);

	/** {@inheritDoc} */
	public boolean CancelIo(int hFile) {
		INT lastError = new INT(0);
		boolean result = CancelIo(hFile, lastError);
		preserveLastError(lastError);
		return result;
	}

	private native boolean CancelIo(int hFile, INT lastError);

	/** {@inheritDoc} */
	public native int malloc(@Nonnegative int size);

	/** {@inheritDoc} */
	public native void memset(int ptr, int value, int num);

	/** {@inheritDoc} */
	public native void free(int pointer);

	/** {@inheritDoc} */
	public native int sizeOf_OVERLAPPED();

	/** {@inheritDoc} */
	public native int sizeOf_HKEY();

	/** {@inheritDoc} */
	public native int sizeOf_DWORD();

	/** {@inheritDoc} */
	public native byte[] getByteArray(NativeByteArray nativeByteArray, int length);

	/** {@inheritDoc} */
	public native int getValue_DWORD(@Nonnull DWORD dword);

	/** {@inheritDoc} */
	public native void setValue_DWORD(@Nonnull DWORD dword, int value);

}
