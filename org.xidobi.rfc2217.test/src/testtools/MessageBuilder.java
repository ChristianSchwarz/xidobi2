/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 09:28:36
 * Erstellt von: Christian Schwarz 
 */
package testtools;

import javax.annotation.Nonnegative;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SERVER_OFFSET;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE;

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
	public static int[] buildSetBaudRateRequest(@Nonnegative int baudRate) {
		
		return new ByteArrayBuilder()//
		.putByte(COM_PORT_OPTION)//
		.putByte(SET_BAUDRATE)//
		.putInt(baudRate)//
		.toIntArray();
	}
	/**
	 * Creates the binary form of a "set baud command" response message, using the given baud rate.
	 * 
	 * @param baudRate
	 * @return the binary form
	 */
	public static int[] buildSetBaudRateResponse(@Nonnegative int baudRate) {
		
		return new ByteArrayBuilder()//
		.putByte(COM_PORT_OPTION)//
		.putByte(SET_BAUDRATE+SERVER_OFFSET)//
		.putInt(baudRate)//
		.toIntArray();
	}
}