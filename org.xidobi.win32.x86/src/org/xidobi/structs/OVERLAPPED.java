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
package org.xidobi.structs;

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import org.xidobi.WinApi;

/**
 * Java representation of the C-struct OVERLAPPED.
 * <p>
 * The OVERLAPPED struct contains information used in asynchronous (or overlapped) input and output
 * (I/O).
 * <p>
 * <i>Please see <a
 * href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms684342(v=vs.85).aspx">OVERLAPPED
 * structure (MSDN)</a> for detailed information!</i>
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class OVERLAPPED extends Pointer {

	// /**
	// * {@code ULONG_PTR} - The error code for the I/O request. When the request is issued, the
	// * system sets this member to STATUS_PENDING to indicate that the operation has not yet
	// started.
	// * When the request is completed, the system sets this member to the error code for the
	// * completed request. The Internal member was originally reserved for system use and its
	// * behavior may change.
	// */
	// private long Internal;
	//
	// /**
	// * {@code ULONG_PTR} - The number of bytes transferred for the I/O request. The system sets
	// this
	// * member if the request is completed without errors. The InternalHigh member was originally
	// * reserved for system use and its behavior may change.
	// */
	// private long InternalHigh;
	//
	// /**
	// * {@code DWORD} - The low-order portion of the file position at which to start the I/O
	// request,
	// * as specified by the user. This member is nonzero only when performing I/O requests on a
	// * seeking device that supports the concept of an offset (also referred to as a file pointer
	// * mechanism), such as a file. Otherwise, this member must be zero.
	// */
	// private int Offset;
	//
	// /**
	// * {@code DWORD} - The high-order portion of the file position at which to start the I/O
	// * request, as specified by the user. This member is nonzero only when performing I/O requests
	// * on a seeking device that supports the concept of an offset (also referred to as a file
	// * pointer mechanism), such as a file. Otherwise, this member must be zero.
	// */
	// private int OffsetHigh;
	//
	// /** {@code PVOID} - Reserved for system use; do not use after initialization to zero. */
	// private int Pointer;

	/** {@code HANDLE} - Event handle */
	public int hEvent;

	/**
	 * Creates a new instance on the heap.
	 * <p>
	 * <b>Note:</b> The instance must be disposed, when it isn't used anymore!
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public OVERLAPPED(WinApi win) {
		super(win, sizeOfOVERLAPPED(win));
		win.memset(cPointer, 0, sizeOfOVERLAPPED(win));
	}

	/** Returns the size of an OVERLAPPED struct. */
	private static int sizeOfOVERLAPPED(WinApi win) {
		checkArgumentNotNull(win, "win");
		return win.sizeOf_OVERLAPPED();
	}

	@Override
	public String toString() {
		return "OVERLAPPED [hEvent=" + hEvent + "]";
		// return "OVERLAPPED [Internal=" + Internal + ", InternalHigh=" + InternalHigh +
		// ", Offset=" + Offset + ", OffsetHigh=" + OffsetHigh + ", Pointer=" + Pointer +
		// ", hEvent=" + hEvent + "]";
	}

}
