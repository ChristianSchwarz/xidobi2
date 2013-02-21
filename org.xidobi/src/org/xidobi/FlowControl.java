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
 * Flow control is the process of managing the rate of data transmission between two nodes to
 * prevent a fast sender from outrunning a slow receiver. It provides a mechanism for the receiver
 * to control the transmission speed, so that the receiving node is not overwhelmed with data from
 * transmitting node.
 * <p>
 * The flow control can be:
 * <ul>
 * <li>none</li>
 * <li>RTS/CTS In</li>
 * <li>RTS/CTS Out</li>
 * <li>RTS/CTS In & Out</li>
 * <li>XON/XOFF In</li>
 * <li>XON/XOFF Out</li>
 * <li>XON/XOFF In & Out</li>
 * </ul>
 * 
 * @author Tobias Breﬂler
 */
public enum FlowControl {

	/** no flow control */
	FLOWCONTROL_NONE,

	/** RTS/CTS In */
	FLOWCONTROL_RTSCTS_IN,

	/** RTS/CTS Out */
	FLOWCONTROL_RTSCTS_OUT,

	/** RTS/CTS In & Out */
	FLOWCONTROL_RTSCTS_IN_OUT,

	/** XON/XOFF In */
	FLOWCONTROL_XONXOFF_IN,

	/** XON/XOFF Out */
	FLOWCONTROL_XONXOFF_OUT,

	/** XON/XOFF In & Out */
	FLOWCONTROL_XONXOFF_IN_OUT;

}
