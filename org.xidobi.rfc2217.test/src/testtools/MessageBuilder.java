/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 09:28:36
 * Erstellt von: Christian Schwarz 
 */
package testtools;

import javax.annotation.Nonnegative;

import org.xidobi.rfc2217.internal.RFC2217;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_BAUDRATE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_DATASIZE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_PARITY_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_STOPSIZE_RESP;
import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SIGNATURE_RESP;

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
	public static ByteBuffer baudRateRequest(@Nonnegative int baudRate) {

		return buildComPortCommand(SET_BAUDRATE_REQ)//
		.putInt(baudRate);

	}

	/**
	 * Creates the binary form of a "set baud command" response message, using the given baud rate.
	 * 
	 * @param baudRate
	 * @return the binary form
	 */
	public static ByteBuffer baudRateResponse(@Nonnegative int baudRate) {
		return buildComPortCommand(SET_BAUDRATE_RESP)//
		.putInt(baudRate);

	}

	/**
	 * Creates the binary form of a "set baud command" response message, using the given baud rate.
	 * 
	 * @param databits
	 * @return the binary form
	 */
	public static ByteBuffer dataBitsRequest(@Nonnegative int databits) {
		return buildComPortCommand(SET_DATASIZE_REQ)//
		.putByte(databits);
	}

	/**
	 * Creates the binary form of a "set baud command" response message, using the given baud rate.
	 * 
	 * @param databits
	 * @return the binary form
	 */
	public static ByteBuffer dataBitsResponse(@Nonnegative int databits) {
		return buildComPortCommand(SET_DATASIZE_RESP)//
		.putByte(databits);
	}

	/**
	 * Creates the binary form of an "set parity command" , using the given parity.
	 * 
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
	public static ByteBuffer parityResponse(int parity) {
		return buildComPortCommand(SET_PARITY_RESP)//
		.putByte(parity);
	}
	public static ByteBuffer parityRequest(int parity) {
		return buildComPortCommand(SET_PARITY_REQ)//
		.putByte(parity);
	}
	public static ByteBuffer stopBitsRequest(int stopbits) {
		return buildComPortCommand(SET_STOPSIZE_REQ)//
		.putByte(stopbits);
	}
	public static ByteBuffer stopBitsResponse(int stopbits) {
		return buildComPortCommand(SET_STOPSIZE_RESP)//
		.putByte(stopbits);
	}
	public static ByteBuffer flowControlRequest(int flowControl) {
		return buildComPortCommand(SET_CONTROL_REQ)//
		.putByte(flowControl);
	}
	public static ByteBuffer flowControlResponse(int flowControl) {
		return buildComPortCommand(SET_CONTROL_RESP)//
		.putByte(flowControl);
	}

	public static ByteBuffer signatureResponse(String signatue){
		return buildComPortCommand(SIGNATURE_RESP)//
			.putBytes(signatue);
	}
	
	public static ByteBuffer signatureRequest(String signatue){
		return buildComPortCommand(SIGNATURE_REQ)//
			.putBytes(signatue);
	}

	/**
	 * Creates a new byte-buffer, containing a pre build com port option header and the given
	 * command-code. This method is commonly used to build command-option request or responses.
	 * 
	 * @param commandCode
	 * @return the binary form
	 */
	public static ByteBuffer buildComPortCommand(int commandCode) {
		return new ByteBuffer().putByte(COM_PORT_OPTION).putByte(commandCode);
	}

	/**
	 * Creates a new empty byte-buffer.
	 * 
	 * @return an empty byte-buffer
	 */
	public static ByteBuffer buffer() {
		return new ByteBuffer();
	}

	/**
	 * Creates a new byte-buffer containig the given byte values.
	 * 
	 * @param bytes
	 * @return an byte-buffer containig the given byte values
	 */
	public static ByteBuffer buffer(int... bytes) {
		return new ByteBuffer().putBytes(bytes);
	}
}