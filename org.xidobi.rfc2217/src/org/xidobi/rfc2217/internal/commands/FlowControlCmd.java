/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 28.08.2013 09:24:17
 * Erstellt von: Konrad Schulz
 */
package org.xidobi.rfc2217.internal.commands;

import static org.xidobi.FlowControl.FLOWCONTROL_NONE;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_IN;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_IN_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_IN;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_IN_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_OUT;
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_REQ;
import static org.xidobi.rfc2217.internal.RFC2217.SET_CONTROL_RESP;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.xidobi.FlowControl;

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

	/** The preferred flowcontrol. */
	private byte flowControl;

	/**
	 * Creates a new {@link FlowControlCmd}.
	 * 
	 * @param flowControl
	 *            the flowControl for this message, must not be <code>null</code>
	 */
	public FlowControlCmd(FlowControl flowControl) {
		super(SET_CONTROL_REQ);
		if (flowControl == null)
			throw new IllegalArgumentException("The parameter >flowControl< must not be null");
		checkFlowControl(flowControl);
		this.flowControl = toByte(flowControl);
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
		flowControl = input.readByte();
		if (flowControl < 0 || flowControl > 127)
			throw new IOException("Unexpected flowControl value: " + flowControl);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(flowControl);
	}

	/**
	 * Returns {@link FlowControl}-value of this control command.
	 * 
	 * @return <code>null</code>, when {@link #read(DataInput)} decoded flow control value has no
	 *         corresponding {@link FlowControl} value
	 */
	@CheckForNull
	public FlowControl getFlowControl() {
		return toEnum(flowControl);
	}

	/**
	 * Checks the flowcontrol for the illegal arguments rts/cts out and xon/xoff out.
	 * 
	 * @param flowControl
	 *            the flowcontrol to check
	 */
	private void checkFlowControl(FlowControl flowControl) {
		if (flowControl == FLOWCONTROL_RTSCTS_OUT)
			throw new IllegalArgumentException("The parameter >flowControl< must not be " + FLOWCONTROL_RTSCTS_OUT + ", use " + FLOWCONTROL_RTSCTS_IN_OUT + " instead.");
		if (flowControl == FLOWCONTROL_XONXOFF_OUT)
			throw new IllegalArgumentException("The parameter >flowControl< must not be " + FLOWCONTROL_XONXOFF_OUT + ", use " + FLOWCONTROL_XONXOFF_IN_OUT + " instead.");
	}

	/**
	 * Returns the {@link FlowControl} belonging to the assigned byte value.
	 * 
	 * @param flowControl
	 *            the input byte value
	 * @return the {@link FlowControl} belonging to the assigned byte value
	 * @throws IOException
	 *             when there was no {@link FlowControl} found to the assigned byte value
	 */
	private FlowControl toEnum(final byte flowControl) {
		switch (flowControl) {
			case 1:
				return FLOWCONTROL_NONE;
			case 2:
				return FLOWCONTROL_XONXOFF_IN_OUT;
			case 3:
				return FLOWCONTROL_RTSCTS_IN_OUT;
			case 15:
				return FLOWCONTROL_XONXOFF_IN;
			case 16:
				return FLOWCONTROL_RTSCTS_IN;
		}
		return null;
	}

	/**
	 * Returns the byte value belonging to the assigned {@link FlowControl}.
	 * 
	 * @param flowControl
	 *            the {@link FlowControl} that needs to be translated for the output byte value
	 * @return the byte value belonging to the assigned {@link FlowControl}
	 * @throws IOException
	 *             when there was no byte value found to the assigned {@link FlowControl}
	 */
	private byte toByte(FlowControl flowControl) {
		switch (flowControl) {
			case FLOWCONTROL_NONE:
				return 1;
			case FLOWCONTROL_XONXOFF_IN_OUT:
			case FLOWCONTROL_XONXOFF_OUT:
				return 2;
			case FLOWCONTROL_RTSCTS_IN_OUT:
			case FLOWCONTROL_RTSCTS_OUT:
				return 3;
			case FLOWCONTROL_XONXOFF_IN:
				return 15;
			case FLOWCONTROL_RTSCTS_IN:
				return 16;
		}
		throw new IllegalStateException("Unexpected flowControl value:" + flowControl);
	}

	public void setFlowControl(byte flowControl) {
		this.flowControl = flowControl;
	}

	@Override
	public String toString() {
		return "FlowControlCmd [flowControl=" + flowControl + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + flowControl;
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
		if (flowControl != other.flowControl)
			return false;
		return true;
	}

}
