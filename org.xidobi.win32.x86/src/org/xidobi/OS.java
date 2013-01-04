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
	 * See <a
	 * href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms682396(v=vs.85).aspx">CreateEvent (MSDN)</a> 
	 * 
	 * @param lpEventAttributes {@code LPSECURITY_ATTRIBUTES}
	 * @param bManualReset {@code BOOL}
	 * @param bInitialState {@code BOOL}
	 * @param lpName {@code LPCTSTR}
	 * @return {@code HANDLE}
	 */
	public static native int CreateEventA(int lpEventAttributes, boolean bManualReset, boolean bInitialState, String lpName);

}
