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

/**
 * Contains information about a communications device. This structure is filled by the
 * {@code ClearCommError} function.
 * <p>
 * <i>Please see <a
 * href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa363200(v=vs.85).aspx">COMSTAT
 * structure (MSDN)</a> for detailed information!</i>
 * 
 * @author Tobias Breﬂler
 */
public class COMSTAT {

	// -- WE DON'T NEED THESE FIELDS AT THE MOMENT! ------------------------------------------------
	// /**
	// * If this member is TRUE, transmission is waiting for the CTS (clear-to-send) signal to be
	// * sent.
	// */
	// public int fCtsHold;
	// /**
	// * If this member is TRUE, transmission is waiting for the DSR (data-set-ready) signal to be
	// * sent.
	// */
	// public int fDsrHold;
	// /**
	// * If this member is TRUE, transmission is waiting for the RLSD (receive-line-signal-detect)
	// * signal to be sent.
	// */
	// public int fRlsdHold;
	// /** If this member is TRUE, transmission is waiting because the XOFF character was received.
	// */
	// public int fXoffHold;
	// /**
	// * If this member is TRUE, transmission is waiting because the XOFF character was transmitted.
	// * (Transmission halts when the XOFF character is transmitted to a system that takes the next
	// * character as XON, regardless of the actual character.)
	// */
	// public int fXoffSent;
	// /** If this member is TRUE, the end-of-file (EOF) character has been received. */
	// public int fEof;
	// /**
	// * If this member is TRUE, there is a character queued for transmission that has come to the
	// * communications device by way of the TransmitCommChar function. The communications device
	// * transmits such a character ahead of other characters in the device's output buffer.
	// */
	// public int fTxim;
	// /** Reserved; do not use. */
	// public int fReserved;
	// ---------------------------------------------------------------------------------------------

	/**
	 * {@code DWORD} - The number of bytes received by the serial provider but not yet read by a
	 * ReadFile operation.
	 */
	public int cbInQue;

	/**
	 * {@code DWORD} - The number of bytes of user data remaining to be transmitted for all write
	 * operations. This value will be zero for a nonoverlapped write.
	 */
	public int cbOutQue;

	@Override
	public String toString() {
		return "COMSTAT [cbInQue=" + cbInQue + ", cbOutQue=" + cbOutQue + "]";
	}

}
