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

import org.xidobi.structs.DCB;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

/**
 * This Class contains one-to-one mappings of native methods used by the OS to control serial ports.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class OS {

	public final static int GENERIC_READ = 0x80000000;
	public final static int GENERIC_WRITE = 0x40000000;

	public final static int OPEN_EXISTING = 3;

	public final static int FILE_FLAG_OVERLAPPED = 1073741824;

	public final static int INVALID_HANDLE_VALUE = -1;

	public final static int ERROR_ACCESS_DENIED = 5;
	public final static int ERROR_FILE_NOT_FOUND = 2;
	/** Overlapped I/O operation is in progress. */
	public static final int ERROR_IO_PENDING = 997;

	/**
	 * The specified object is a mutex object that was not released by the thread that owned the
	 * mutex object before the owning thread terminated. Ownership of the mutex object is granted to
	 * the calling thread and the mutex state is set to nonsignaled. If the mutex was protecting
	 * persistent state information, you should check it for consistency.
	 */
	public static final int WAIT_ABANDONED = 0x00000080;
	/** The state of the specified object is signaled. */
	public static final int WAIT_OBJECT_0 = 0x00000000;
	/** The time-out interval elapsed, and the object's state is nonsignaled. */
	public static final int WAIT_TIMEOUT = 0x00000102;
	/** The function has failed. To get extended error information, call GetLastError. */
	public static final int WAIT_FAILED = 0xFFFFFFFF;

	static {
		System.loadLibrary("lib/org.xidobi.native.x86.win32");
	}

	/** This class is not intended to be instantiated */
	private OS() {}

	/**
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa363858(v=vs.85).aspx">
	 * CreateFile (MSDN)</a>
	 * 
	 * @param lpFileName
	 *            {@code LPCTSTR}
	 * @param dwDesiredAccess
	 *            {@code DWORD}
	 * @param dwShareMode
	 *            {@code DWORD}
	 * @param lpSecurityAttributes
	 *            {@code LPSECURITY_ATTRIBUTES}
	 * @param dwCreationDisposition
	 *            {@code DWORD}
	 * @param dwFlagsAndAttributes
	 *            {@code DWORD}
	 * @param hTemplateFile
	 *            {@code HANDLE}
	 * @return {@code HANDLE}
	 */
	public static native int CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode, int lpSecurityAttributes, int dwCreationDisposition, int dwFlagsAndAttributes, int hTemplateFile);

	public static native boolean CloseHandle(int handle);

	public static native boolean GetCommState(int handle, DCB dcb);

	public static native boolean SetCommState(int handle, DCB dcb);

	/**
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms682396(v=vs.85).aspx">
	 * CreateEvent (MSDN)</a>
	 * 
	 * @param lpEventAttributes
	 *            {@code LPSECURITY_ATTRIBUTES}
	 * @param bManualReset
	 *            {@code BOOL}
	 * @param bInitialState
	 *            {@code BOOL}
	 * @param lpName
	 *            {@code LPCTSTR}
	 * @return {@code HANDLE}
	 */
	public static native int CreateEventA(int lpEventAttributes, boolean bManualReset, boolean bInitialState, String lpName);

	/**
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa365747(v=vs.85).aspx">
	 * WriteFile (MSDN)</a>
	 * 
	 * @param handle
	 *            {@code HANDLE}
	 * @param lpBuffer
	 *            {@code LPCVOID}
	 * @param nNumberOfBytesToWrite
	 *            {@code DWORD}
	 * @param lpNumberOfBytesWritten
	 *            {@code LPDWORD}
	 * @param lpOverlapped
	 *            {@code LPOVERLAPPED}
	 * @return {@code BOOL}
	 */
	public static native boolean WriteFile(int handle, byte[] lpBuffer, int nNumberOfBytesToWrite, INT lpNumberOfBytesWritten, int lpOverlapped);

	/**
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms679360(v=vs.85).aspx">
	 * GetLastError (MSDN)</a>
	 * 
	 * @return {@code DWORD}
	 */
	public static native int GetLastError();

	/**
	 * Retrieves the results of an overlapped operation on the specified file, named pipe, or
	 * communications device. To specify a timeout interval or wait on an alertable thread, use
	 * GetOverlappedResultEx.
	 * 
	 * @param handle
	 *            {@code HANDLE}
	 * @param lpOverlapped
	 *            {@code LPOVERLAPPED }
	 * @param lpNumberOfBytesTransferred
	 *            {@code LPDWORD}
	 * @param bWait
	 *            {@code BOOL}
	 * @return {@code BOOL}
	 */
	public static native boolean GetOverlappedResult(int handle, int lpOverlapped, INT lpNumberOfBytesTransferred, boolean bWait);

	/**
	 * Waits until the specified object is in the signaled state or the time-out interval elapses.
	 * <p>
	 * To enter an alertable wait state, use the WaitForSingleObjectEx function. To wait for
	 * multiple objects, use the WaitForMultipleObjects.
	 * 
	 * @param hHandle
	 *            {@code HANDLE}
	 * @param dwMilliseconds
	 *            {@code DWORD}
	 * @return {@code DWORD}
	 *         <ul>
	 *         <li>{@link #WAIT_ABANDONED} <li>{@link #WAIT_FAILED} <li>{@link #WAIT_OBJECT_0} <li>
	 *         {@link #WAIT_TIMEOUT}
	 *         </ul>
	 */
	public static native int WaitForSingleObject(int hHandle, int dwMilliseconds);

	public static native int newOverlapped();

	public static native void setOverlappedHEvent(int overlapped, int hEvent);

	public static native void deleteOverlapped(int overlapped);

}
