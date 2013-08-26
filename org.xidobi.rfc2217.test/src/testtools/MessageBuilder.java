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
		return buildComPortCommand(SET_BAUDRATE+SERVER_OFFSET)//
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
		return buildComPortCommand(SET_DATASIZE+SERVER_OFFSET)//
			.putByte(databits);
	}
	
	
	
	public static ByteBuffer buffer(){
		return new ByteBuffer();
	}
	
	public static ByteBuffer buffer(int ...bytes){
		return new ByteBuffer().putBytes(bytes);
	}
	
	public static ByteBuffer buildComPortCommand(int command){
		return new ByteBuffer().putByte(COM_PORT_OPTION).putByte(command);
	}
}