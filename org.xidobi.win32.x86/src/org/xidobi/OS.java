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
import org.xidobi.structs.HKEY;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

/**
 * This class contains one-to-one mappings of native methods used by the OS to control serial ports.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class OS {

	/** Opens port for input. */
	public final static int GENERIC_READ = 0x80000000;
	/** Opens port for output. */
	public final static int GENERIC_WRITE = 0x40000000;

	/**
	 * Opens a file or device, only if it exists. If the specified file or device does not exist,
	 * the function fails and the last-error code is set to ERROR_FILE_NOT_FOUND (2).
	 */
	public final static int OPEN_EXISTING = 3;

	/**
	 * The file or device is being opened or created for asynchronous I/O. When subsequent I/O
	 * operations are completed on this handle, the event specified in the OVERLAPPED structure will
	 * be set to the signaled state. If this flag is specified, the file can be used for
	 * simultaneous read and write operations. If this flag is not specified, then I/O operations
	 * are serialized, even if the calls to the read and write functions specify an OVERLAPPED
	 * structure.
	 */
	public final static int FILE_FLAG_OVERLAPPED = 0x40000000;

	/** Invalid handle value. */
	public final static int INVALID_HANDLE_VALUE = -1;

	/** No errors. */
	public final static int ERROR_SUCCESS = 0;
	/** Access denied or port busy. */
	public final static int ERROR_ACCESS_DENIED = 5;
	/** File not found or port unavailable. */
	public final static int ERROR_FILE_NOT_FOUND = 2;
	/** Overlapped I/O operation is in progress. */
	public static final int ERROR_IO_PENDING = 997;
	/** No more data is available. Indicates in an enumeration that no more elements are available. */
	public static final int ERROR_NO_MORE_ITEMS = 259;

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

	/** Combines the STANDARD_RIGHTS_WRITE, KEY_SET_VALUE, and KEY_CREATE_SUB_KEY access rights. */
	public static final int KEY_WRITE = 0x20006;
	/** Equivalent to {@link #KEY_READ}. */
	public static final int KEY_EXECUTE = 0x20019;
	/**
	 * Combines the STANDARD_RIGHTS_READ, KEY_QUERY_VALUE, KEY_ENUMERATE_SUB_KEYS, and KEY_NOTIFY
	 * values.
	 */
	public static final int KEY_READ = 0x20019;

	/**
	 * Registry entries subordinate to this key define the physical state of the computer, including
	 * data about the bus type, system memory, and installed hardware and software. It contains
	 * subkeys that hold current configuration data, including Plug and Play information (the Enum
	 * branch, which includes a complete list of all hardware that has ever been on the system),
	 * network logon preferences, network security information, software-related information (such
	 * as server names and the location of the server), and other system information.
	 */
	public static final int HKEY_LOCAL_MACHINE = 0x80000002;

	static {
		System.loadLibrary("lib/xidobi");
	}

	/** This class is not intended to be instantiated */
	private OS() {}

	/**
	 * Creates or opens a file or I/O device. The most commonly used I/O devices are as follows:
	 * file, file stream, directory, physical disk, volume, console buffer, tape drive,
	 * communications resource, mailslot, and pipe. The function returns a handle that can be used
	 * to access the file or device for various types of I/O depending on the file or device and the
	 * flags and attributes specified.
	 * <p>
	 * To perform this operation as a transacted operation, which results in a handle that can be
	 * used for transacted I/O, use the CreateFileTransacted function.
	 * <p>
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

	/**
	 * Closes an open object handle.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms724211(v=vs.85).aspx">
	 * CloseHandle (MSDN)</a>
	 * 
	 * @param handle
	 *            {@code HANDLE}
	 * @return <ul>
	 *         <li> <code>true</code> on success <li> <code>false</code> on failure, for more
	 *         information call {@link #GetLastError()}
	 *         </ul>
	 */
	public static native boolean CloseHandle(int handle);

	/**
	 * Retrieves the current control settings for a specified communications device.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa363260(v=vs.85).aspx">
	 * GetCommState (MSDN)</a>
	 * 
	 * @param handle
	 *            {@code HANDLE}
	 * @param dcb
	 *            {@code LPDCB}
	 * @return <ul>
	 *         <li> <code>true</code> on success <li> <code>false</code> on failure, for more
	 *         information call {@link #GetLastError()}
	 *         </ul>
	 */
	public static native boolean GetCommState(int handle, DCB dcb);

	/**
	 * Configures a communications device according to the specifications in a device-control block
	 * (a DCB structure). The function reinitializes all hardware and control settings, but it does
	 * not empty output or input queues.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa363436(v=vs.85).aspx">
	 * SetCommState (MSDN)</a>
	 * 
	 * @param handle
	 *            {@code HANDLE}
	 * @param dcb
	 *            {@code LPDCB}
	 * @return <ul>
	 *         <li> <code>true</code> on success <li> <code>false</code> on failure, for more
	 *         information call {@link #GetLastError()}
	 *         </ul>
	 */
	public static native boolean SetCommState(int handle, DCB dcb);

	/**
	 * Creates or opens a named or unnamed event object.
	 * <p>
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
	 * Writes data to the specified file or input/output (I/O) device.
	 * <p>
	 * This function is designed for both synchronous and asynchronous operation.
	 * <p>
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
	public static native boolean WriteFile(int handle, byte[] lpBuffer, int nNumberOfBytesToWrite, INT lpNumberOfBytesWritten, OVERLAPPED lpOverlapped);

	/**
	 * Reads data from the specified file or input/output (I/O) device. Reads occur at the position
	 * specified by the file pointer if supported by the device.
	 * <p>
	 * This function is designed for both synchronous and asynchronous operations.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa365467(v=vs.85).aspx">
	 * ReadFile (MSDN)</a>
	 * 
	 * @param handle
	 *            {@code HANDLE}
	 * @param lpBuffer
	 *            {@code LPCVOID}
	 * @param nNumberOfBytesToRead
	 *            {@code DWORD}
	 * @param lpNumberOfBytesRead
	 *            {@code LPDWORD}
	 * @param lpOverlapped
	 *            {@code LPOVERLAPPED}
	 * @return {@code BOOL}
	 */
	public static native boolean ReadFile(int handle, byte[] lpBuffer, int nNumberOfBytesToRead, INT lpNumberOfBytesRead, OVERLAPPED lpOverlapped);

	/**
	 * Retrieves the calling thread's last-error code value. The last-error code is maintained on a
	 * per-thread basis. Multiple threads do not overwrite each other's last-error code.
	 * <p>
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
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms683209(v=vs.85).aspx">
	 * GetOverlappedResult (MSDN)</a>
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
	public static native boolean GetOverlappedResult(int handle, OVERLAPPED lpOverlapped, INT lpNumberOfBytesTransferred, boolean bWait);

	/**
	 * Waits until the specified object is in the signaled state or the time-out interval elapses.
	 * <p>
	 * To enter an alertable wait state, use the WaitForSingleObjectEx function. To wait for
	 * multiple objects, use the WaitForMultipleObjects.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms687032(v=vs.85).aspx">
	 * WaitForSingleObject (MSDN)</a>
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

	/**
	 * Opens the specified registry key. Note that key names are not case sensitive.
	 * <p>
	 * To perform transacted registry operations on a key, call the RegOpenKeyTransacted function.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms724897(v=vs.85).aspx">
	 * RegOpenKeyEx (MSDN)</a>
	 * 
	 * @param hKey
	 *            {@code HKEY}
	 * @param lpSubKey
	 *            {@code LPCTSTR}
	 * @param ulOptions
	 *            {@code DWORD}
	 * @param samDesired
	 *            {@code REGSAM}
	 *            <ul>
	 *            <li> {@link #KEY_WRITE} <li> {@link #KEY_READ} <li> {@link #KEY_EXECUTE}
	 *            </ul>
	 * @param phkResult
	 *            {@code PHKEY}
	 * @return {@code LONG}
	 */
	public static native int RegOpenKeyExA(int hKey, String lpSubKey, int ulOptions, int samDesired, HKEY phkResult);

	/**
	 * Closes a handle to the specified registry key.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms724837(v=vs.85).aspx">
	 * RegCloseKey (MSDN)</a>
	 * 
	 * @param hKey
	 *            {@code HKEY}
	 * @return {@code LONG}
	 */
	public static native int RegCloseKey(HKEY hKey);

	/**
	 * Enumerates the values for the specified open registry key. The function copies one indexed
	 * value name and data block for the key each time it is called.
	 * <p>
	 * See <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms724865(v=vs.85).aspx">
	 * RegEnumValue (MSDN)</a>
	 * 
	 * @param hKey
	 *            {@code HKEY}
	 * @param dwIndex
	 *            {@code DWORD}
	 * @param lpValueName
	 *            {@code LPTSTR}
	 * @param lpcchValueName
	 *            {@code LPDWORD}
	 * @param lpReserved
	 *            {@code LPDWORD}
	 * @param lpType
	 *            {@code LPDWORD}
	 * @param lpData
	 *            {@code LPBYTE}
	 * @param lpcbData
	 *            {@code LPDWORD}
	 * @return {@code LONG}
	 */
	public static native int RegEnumValue(HKEY hKey, int dwIndex, String lpValueName, INT lpcchValueName, int lpReserved, INT lpType, byte[] lpData, INT lpcbData);

	/**
	 * Returns a pointer to the allocated memory of the given size.
	 * 
	 * @param size
	 *            the size of the memory
	 * @return pointer to the allocated memory
	 */
	public static native int malloc(int size);

	/**
	 * Frees the memory of the given pointer.
	 * 
	 * @param pointer
	 *            pointer to the memory
	 */
	public static native void free(int pointer);

	/**
	 * Size of an OVERLAPPED struct.
	 * 
	 * @return the size of the OVERLAPPED struct
	 */
	public static native int sizeOf_OVERLAPPED();

	/**
	 * Size of an HKEY struct.
	 * 
	 * @return the size of the OVERLAPPED struct
	 */
	public static native int sizeOf_HKEY();

}
