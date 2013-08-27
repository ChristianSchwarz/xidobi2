/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 09:28:36
 * Erstellt von: Christian Schwarz 
 */
package testtools;

import javax.annotation.Nonnegative;

import org.xidobi.DataBits;
import org.xidobi.rfc2217.internal.RFC2217;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SERVER_OFFSET;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY;

/**
 * Provides static methods to build com port control messages in binary form.
 * 
 * @author Christian Schwarz
 * 
 */
public class MessageBuilder {

	/** This class is not intendet to be instanciated! */
	private MessageBuilder() {}

	/**
	 * Creates the binary form of a "set baud command" request message, using the given baud rate.
	 * 
	 * @param baudRate
	 * @return the binary form
	 */
	public static ByteBuffer buildSetBaudRateRequest(@Nonnegative int baudRate) {

		return buildComPortCommand(SET_BAUDRATE)//
		.putInt(baudRate);

	}

	/**
	 * Creates the binary form of a "set baud command" response message, using the given baud rate.
	 * 
	 * @param baudRate
	 * @return the binary form
	 */
	public static ByteBuffer buildSetBaudRateResponse(@Nonnegative int baudRate) {
		return buildComPortCommand(SET_BAUDRATE + SERVER_OFFSET)//
		.putInt(baudRate);

	}

	/**
	 * Creates the binary form of a "set baud command" response message, using the given baud rate.
	 * 
	 * @param baudRate
	 * @return the binary form
	 */
	public static ByteBuffer buildDataBitsRequest(@Nonnegative int databits) {
		return buildComPortCommand(SET_DATASIZE)//
		.putByte(databits);
	}

	/**
	 * Creates the binary form of a "set baud command" response message, using the given baud rate.
	 * 
	 * @param baudRate
	 * @return the binary form
	 */
	public static ByteBuffer buildDataBitsResponse(@Nonnegative int databits) {
		return buildComPortCommand(SET_DATASIZE + SERVER_OFFSET)//
		.putByte(databits);
	}

	/**
	 * Creates the binary form of an "set parity command" , using the given parity.
	 * @param parity
	 *            <ul>
	 *            <li>0 Request Current Data Size
	 *            <li>1 NONE
	 *            <li>2 ODD
	 *            <li>3 EVEN
	 *            <li>4 MARK
	 *            </ul>
	 * 
	 * @return the binary form
	 */
	public static ByteBuffer buildSetParityResponse(int parity) {
		return buildComPortCommand(SET_PARITY + SERVER_OFFSET)//
		.putByte(parity);
	}
	
	/** Creates a new byte-buffer, containing a pre build com port option header and the given command-code. This method is commonly used to build command-option request or responses.*/
	public static ByteBuffer buildComPortCommand(int commandCode) {
		return new ByteBuffer().putByte(COM_PORT_OPTION).putByte(commandCode);
	}
	
	/** Creates a new empty byte-buffer. */
	public static ByteBuffer buffer() {
		return new ByteBuffer();
	}

	
	/** Creates a new byte-buffer containig the given byte values. */
	public static ByteBuffer buffer(int... bytes) {
		return new ByteBuffer().putBytes(bytes);
	}

	

}