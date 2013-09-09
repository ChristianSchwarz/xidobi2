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
package org.xidobi.rfc2217.internal;

/**
 * RFC 2217 constants.
 * 
 * @author Christian Schwarz
 */
@SuppressWarnings("javadoc")
public final class RFC2217 {

	// COM-PORT-OPTION telnet option
	public static final int COM_PORT_OPTION = 44;

	/** COM-PORT-OPTION commands */
	public static final int SIGNATURE_REQ = 0;
	public static final int SET_BAUDRATE_REQ = 1;
	public static final int SET_DATASIZE_REQ = 2;
	public static final int SET_PARITY_REQ = 3;
	public static final int SET_STOPSIZE_REQ = 4;
	public static final int SET_CONTROL_REQ = 5;
	public static final int PURGE_DATA_REQ = 12;

	public static final int SIGNATURE_RESP = 100;
	public static final int SET_BAUDRATE_RESP = 101;
	public static final int SET_DATASIZE_RESP = 102;
	public static final int SET_PARITY_RESP = 103;
	public static final int SET_STOPSIZE_RESP = 104;
	public static final int SET_CONTROL_RESP = 105;
	public static final int PURGE_DATA_RESP = 12;

	private RFC2217() {
	}
}