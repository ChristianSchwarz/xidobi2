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
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.xidobi.FlowControl;

import static org.xidobi.FlowControl.FLOWCONTROL_NONE;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_IN;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_IN_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_IN;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_IN_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_OUT;
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_RESP;
import static org.xidobi.spi.Preconditions.checkArgument;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

//@formatter:off
/**
 * IAC SB COM-PORT-OPTION SET-CONTROL <value> IAC SE
 *     This command is sent by the client to the access server to set
 *     special com port options. The command can also be sent to query
 *     the current option value. The value is one octet (byte). The
 *     value is an index into the following value table:
 *	<table>
 *   	<tr><th>Value	</th><th>Control Commands 										</th></tr>
 *      <tr><td> 0 		</td><td> Request Com Port Flow Control Setting (outbound/both) </td></tr>
 *      <tr><td> 1		</td><td> Use No Flow Control (outbound/both) 					</td></tr>
 *      <tr><td> 2 		</td><td> Use XON/XOFF Flow Control (outbound/both) 			</td></tr>
 *      <tr><td> 3		</td><td> Use HARDWARE Flow Control (outbound/both) 			</td></tr>
 *   </table>
 * 
 * @author Konrad Schulz
 */
//@formatter:on
public class FlowControlCmd extends AbstractControlCmd {

	/** The flowcontrol value of this control command, as defined in RFC2217. */
	private final byte flowControlRfc2217;
	/**
	 * The flowcontrol value of this control command, as defined in xidobi, or <code>null</code> if
	 * no equivalent mapping to {@link #flowControlRfc2217} exist
	 */
	private final FlowControl flowControlXidobi;

	//@formatter:off
	private final static BiMap<FlowControl, Byte> MAP = new BiMap<FlowControl, Byte>() {{
		put(FLOWCONTROL_NONE,			(byte) 1);

		put(FLOWCONTROL_XONXOFF_OUT,	(byte) 2);
		put(FLOWCONTROL_XONXOFF_IN_OUT,	(byte) 2);
		// ^ don't change the order of these two lines above, otherwise the correct mapping will be broken
		put(FLOWCONTROL_XONXOFF_IN,		(byte) 15);


		put(FLOWCONTROL_RTSCTS_OUT,		(byte) 3);
		put(FLOWCONTROL_RTSCTS_IN_OUT,	(byte) 3);
		// ^ don't change the order of these two lines above, otherwise the correct mapping will be broken
		put(FLOWCONTROL_RTSCTS_IN,		(byte) 16);
	}};
	//@formatter:on

	/**
	 * Creates a new {@link FlowControlCmd}.
	 * 
	 * @param flowControl
	 *            the flowControl for this message, must not be <code>null</code>
	 */
	public FlowControlCmd(FlowControl flowControl) {
		super(SET_CONTROL_REQ);
		checkArgumentNotNull(flowControl, "flowControl");
		checkArgument(flowControl != FLOWCONTROL_RTSCTS_OUT, "flowControl", FLOWCONTROL_RTSCTS_OUT + " is not allowed, use " + FLOWCONTROL_RTSCTS_IN_OUT + " instead.");
		checkArgument(flowControl != FLOWCONTROL_XONXOFF_OUT, "flowControl", FLOWCONTROL_XONXOFF_OUT + " is not allowed, use " + FLOWCONTROL_XONXOFF_IN_OUT + " instead.");

		flowControlXidobi = flowControl;
		flowControlRfc2217 = MAP.getRfc2217Equivalent(flowControl);
	}

	/**
	 * Decodes the {@link FlowControl} value from the first byte of the <i>input</i>. The values
	 * 0-127 are supported, if any other value is read an {@link IOException} will be thrown.
	 * 
	 * @param input
	 *            the input where the command must be read from, must not be <code>null</code>
	 * @throws IOException
	 */
	public FlowControlCmd(@Nonnull DataInput input) throws IOException {
		super(SET_CONTROL_RESP);
		flowControlRfc2217 = input.readByte();
		if (flowControlRfc2217 < 0 || flowControlRfc2217 > 127)
			throw new IOException("Unexpected Flow Control value: " + flowControlRfc2217);
		flowControlXidobi = MAP.getXidobiEquivalent(flowControlRfc2217);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(flowControlRfc2217);
	}

	/**
	 * Returns {@link FlowControl}-value of this control command.
	 * 
	 * @return <code>null</code>, when {@link #read(DataInput)} decoded flow control value has no
	 *         corresponding {@link FlowControl} value
	 */
	@CheckForNull
	public FlowControl getFlowControl() {
		return flowControlXidobi;
	}

	@Override
	public String toString() {
		return "FlowControlCmd [flowControl=" + flowControlRfc2217 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + flowControlRfc2217;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlowControlCmd other = (FlowControlCmd) obj;
		if (flowControlRfc2217 != other.flowControlRfc2217)
			return false;
		return true;
	}

}
