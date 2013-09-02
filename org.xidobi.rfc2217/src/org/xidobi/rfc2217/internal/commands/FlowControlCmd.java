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
	private FlowControl flowControl;

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
		checkForRTSCTSOut_XonXoffOut(flowControl);
		this.flowControl = flowControl;
	}

	/**
	 * Creates a new {@link DataBitsControlCmd}-Response, that is decoded from the given
	 * <i>input</i>.
	 * 
	 * @param input
	 *            the input where the command must be read from, must not be <code>null</code>
	 * @throws IOException
	 */
	public FlowControlCmd(@Nonnull DataInput input) throws IOException {
		super(SET_CONTROL_RESP, input);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		final byte byteValue = input.readByte();
		FlowControl flowControl = toEnum(byteValue);
		this.flowControl = flowControl;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(toByte(flowControl));
	}

	/**
	 * Returns the flowcontrol.
	 * 
	 * @return the flowControl
	 */
	public FlowControl getFlowControl() {
		return flowControl;
	}

	/**
	 * Checks the flowcontrol for the illegal arguments rts/cts out and xon/xoff out.
	 * 
	 * @param flowControl
	 *            the flowcontrol to check
	 */
	private void checkForRTSCTSOut_XonXoffOut(FlowControl flowControl) {
		FlowControl control = null;
		if (flowControl == FLOWCONTROL_RTSCTS_OUT)
			control = FLOWCONTROL_RTSCTS_IN_OUT;
		else if (flowControl == FLOWCONTROL_XONXOFF_OUT)
			control = FLOWCONTROL_XONXOFF_IN_OUT;
		if (control != null)
			throw new IllegalArgumentException("The parameter >flowControl< must not be " + flowControl + ", use " + control + " instead.");
	}

	/**
	 * Returns the {@link FlowControl} belonging to the assigned byte value.
	 * 
	 * @param flowControl
	 *            the input byte value
	 * @return the {@link FlowControl} belonging to the assigned byte value
	 * 
	 * @throws IOException
	 *             when there was no {@link FlowControl} found to the assigned byte value
	 */
	private FlowControl toEnum(final byte flowControl) throws IOException {
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
		throw new IOException("Unexpected flowControl value: " + flowControl);
	}

	/**
	 * Returns the byte value belonging to the assigned {@link FlowControl}.
	 * 
	 * @param flowControl
	 *            the {@link FlowControl} that needs to be translated for the output byte value
	 * @return the byte value belonging to the assigned {@link FlowControl}
	 * 
	 * @throws IOException
	 *             when there was no byte value found to the assigned {@link FlowControl}
	 */
	private int toByte(FlowControl flowControl) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flowControl == null) ? 0 : flowControl.hashCode());
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

	@Override
	public String toString() {
		return "FlowControlCmd [flowControl=" + flowControl + "]";
	}
}
