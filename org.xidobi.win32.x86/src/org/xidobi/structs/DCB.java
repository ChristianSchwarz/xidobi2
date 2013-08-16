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
 * Defines the control setting for a serial communications device.
 * <p>
 * <i>Please see <a
 * href="http://msdn.microsoft.com/en-us/library/windows/desktop/aa363214(v=vs.85).aspx">DCB
 * structure (MSDN)</a> for detailed information!</i>
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class DCB {

	// WE DON'T NEED THIS FIELDS AT THE MOMENT: --------------------------------------------------
	//
	// /** Baudrate: 110 */
	// public final static int CBR_110 = 110;
	// /** Baudrate: 300 */
	// public final static int CBR_300 = 300;
	// /** Baudrate: 600 */
	// public final static int CBR_600 = 600;
	// /** Baudrate: 1200 */
	// public final static int CBR_1200 = 1200;
	// /** Baudrate: 2400 */
	// public final static int CBR_2400 = 2400;
	// /** Baudrate: 4800 */
	// public final static int CBR_4800 = 4800;
	// /** Baudrate: 9600 */
	// public final static int CBR_9600 = 9600;
	// /** Baudrate: 14400 */
	// public final static int CBR_14400 = 14400;
	// /** Baudrate: 19200 */
	// public final static int CBR_19200 = 19200;
	// /** Baudrate: 38400 */
	// public final static int CBR_38400 = 38400;
	// /** Baudrate: 57600 */
	// public final static int CBR_57600 = 57600;
	// /** Baudrate: 115200 */
	// public final static int CBR_115200 = 115200;
	// /** Baudrate: 128000 */
	// public final static int CBR_128000 = 128000;
	// /** Baudrate: 256000 */
	// public final static int CBR_256000 = 256000;
	//
	// -------------------------------------------------------------------------------------------

	/** Disables the DTR line when the device is opened and leaves it disabled. */
	public final static int DTR_CONTROL_DISABLE = 0x00;
	/** Enables the DTR line when the device is opened and leaves it on. */
	public final static int DTR_CONTROL_ENABLE = 0x01;
	/**
	 * Enables DTR handshaking. If handshaking is enabled, it is an error for the application to
	 * adjust the line by using the EscapeCommFunction function.
	 */
	public final static int DTR_CONTROL_HANDSHAKE = 0x02;

	/** Disables the RTS line when the device is opened and leaves it disabled. */
	public final static int RTS_CONTROL_DISABLE = 0x00;
	/** Enables the RTS line when the device is opened and leaves it on. */
	public final static int RTS_CONTROL_ENABLE = 0x01;
	/**
	 * Enables RTS handshaking. The driver raises the RTS line when the "type-ahead" (input) buffer
	 * is less than one-half full and lowers the RTS line when the buffer is more than
	 * three-quarters full. If handshaking is enabled, it is an error for the application to adjust
	 * the line by using the EscapeCommFunction function.
	 */
	public final static int RTS_CONTROL_HANDSHAKE = 0x02;
	/**
	 * Specifies that the RTS line will be high if bytes are available for transmission. After all
	 * buffered bytes have been sent, the RTS line will be low.
	 */
	public final static int RTS_CONTROL_TOGGLE = 0x03;

	/** Parity: None */
	public final static int NOPARITY = 0;
	/** Parity: Odd */
	public final static int ODDPARITY = 1;
	/** Parity: Even */
	public final static int EVENPARITY = 2;
	/** Parity: Mark */
	public final static int MARKPARITY = 3;
	/** Parity: Space */
	public final static int SPACEPARITY = 4;

	/** 1 stop bit. */
	public final static int ONESTOPBIT = 0;
	/** 1.5 stop bits. */
	public final static int ONE5STOPBITS = 1;
	/** 2 stop bits. */
	public final static int TWOSTOPBITS = 2;

	/**
	 * {@code DWORD} - The length of the structure, in bytes. The caller must set this member to
	 * sizeof(DCB).
	 */
	public int DCBlength;

	/**
	 * {@code DWORD} - The baud rate at which the communications device operates.
	 */
	public int BaudRate;

	/**
	 * {@code DWORD} - If this member is TRUE, binary mode is enabled. Windows does not support
	 * nonbinary mode transfers, so this member must be TRUE.
	 */
	public int fBinary = 1;

	/**
	 * {@code DWORD} - If this member is TRUE, parity checking is performed and errors are reported.
	 */
	public int fParity = 1;

	/**
	 * {@code DWORD} - If this member is TRUE, the CTS (clear-to-send) signal is monitored for
	 * output flow control. If this member is TRUE and CTS is turned off, output is suspended until
	 * CTS is sent again.
	 */
	public int fOutxCtsFlow = 1;

	/**
	 * {@code DWORD} - If this member is TRUE, the DSR (data-set-ready) signal is monitored for
	 * output flow control. If this member is TRUE and DSR is turned off, output is suspended until
	 * DSR is sent again.
	 */
	public int fOutxDsrFlow = 1;

	/**
	 * {@code DWORD} - The DTR (data-terminal-ready) flow control. This member can be one of the
	 * following values.
	 */
	public int fDtrControl = 2;

	/**
	 * {@code DWORD} - If this member is TRUE, the communications driver is sensitive to the state
	 * of the DSR signal. The driver ignores any bytes received, unless the DSR modem input line is
	 * high.
	 */
	public int fDsrSensitivity = 1;

	/**
	 * {@code DWORD} - If this member is TRUE, transmission continues after the input buffer has
	 * come within XoffLim bytes of being full and the driver has transmitted the XoffChar character
	 * to stop receiving bytes. If this member is FALSE, transmission does not continue until the
	 * input buffer is within XonLim bytes of being empty and the driver has transmitted the XonChar
	 * character to resume reception.
	 */
	public int fTXContinueOnXoff = 1;

	/**
	 * {@code DWORD} - Indicates whether XON/XOFF flow control is used during transmission. If this
	 * member is TRUE, transmission stops when the XoffChar character is received and starts again
	 * when the XonChar character is received.
	 */
	public int fOutX = 1;

	/**
	 * {@code DWORD} - Indicates whether XON/XOFF flow control is used during reception. If this
	 * member is TRUE, the XoffChar character is sent when the input buffer comes within XoffLim
	 * bytes of being full, and the XonChar character is sent when the input buffer comes within
	 * XonLim bytes of being empty.
	 */
	public int fInX = 1;

	/**
	 * {@code DWORD} - Indicates whether bytes received with parity errors are replaced with the
	 * character specified by the ErrorChar member. If this member is TRUE and the fParity member is
	 * TRUE, replacement occurs.
	 */
	public int fErrorChar = 1;

	/**
	 * {@code DWORD} - If this member is TRUE, null bytes are discarded when received.
	 */
	public int fNull = 1;

	/**
	 * {@code DWORD} - The RTS (request-to-send) flow control. This member can be one of the
	 * following values.
	 */
	public int fRtsControl = 2;

	/**
	 * {@code DWORD} - If this member is TRUE, the driver terminates all read and write operations
	 * with an error status if an error occurs. The driver will not accept any further
	 * communications operations until the application has acknowledged the error by calling the
	 * ClearCommError function.
	 */
	public int fAbortOnError = 1;

	/**
	 * {@code DWORD} - Reserved; do not use.
	 */
	public int fDummy2 = 17;

	/**
	 * {@code WORD} - Reserved; must be zero.
	 */
	public short wReserved;

	/**
	 * {@code WORD} - The minimum number of bytes in use allowed in the input buffer before flow
	 * control is activated to allow transmission by the sender. This assumes that either XON/XOFF,
	 * RTS, or DTR input flow control is specified in the fInX, fRtsControl, or fDtrControl members.
	 */
	public short XonLim;

	/**
	 * {@code WORD} - The minimum number of free bytes allowed in the input buffer before flow
	 * control is activated to inhibit the sender. Note that the sender may transmit characters
	 * after the flow control signal has been activated, so this value should never be zero. This
	 * assumes that either XON/XOFF, RTS, or DTR input flow control is specified in the fInX,
	 * fRtsControl, or fDtrControl members. The maximum number of bytes in use allowed is calculated
	 * by subtracting this value from the size, in bytes, of the input buffer.
	 */
	public short XoffLim;

	/**
	 * {@code BYTE} - The number of bits in the bytes transmitted and received.The number of data
	 * bits must be 5 to 8 bits.The use of 5 data bits with 2 stop bits is an invalid combination,
	 * as is 6, 7, or 8 data bits with 1.5 stop bits.
	 */
	public byte ByteSize;

	/**
	 * {@code BYTE} - The parity scheme to be used. Values are:
	 * <ul>
	 * <li>{@link #EVENPARITY}</li>
	 * <li>{@link #NOPARITY}</li>
	 * <li>{@link #MARKPARITY}</li>
	 * <li>{@link #ODDPARITY}</li>
	 * <li>{@link #SPACEPARITY}</li>
	 * </ul>
	 */
	public byte Parity;

	/**
	 * {@code BYTE} - The number of stop bits to be used. The use of 5 data bits with 2 stop bits is
	 * an invalid combination, as is 6, 7, or 8 data bits with 1.5 stop bits.This member can be one
	 * of the following values.
	 */
	public byte StopBits;

	/**
	 * {@code char} - The value of the XON character for both transmission and reception.
	 */
	public char XonChar;

	/**
	 * {@code char} - The value of the XOFF character for both transmission and reception.
	 */
	public char XoffChar;

	/**
	 * {@code char} - The value of the character used to replace bytes received with a parity error.
	 */
	public char ErrorChar;

	/**
	 * {@code char} - The value of the character used to signal the end of data.
	 */
	public char EofChar;

	/**
	 * {@code char} - The value of the character used to signal an event.
	 */
	public char EvtChar;

	/**
	 * {@code WORD} - Reserved; do not use.
	 */
	public short wReserved1;

}
