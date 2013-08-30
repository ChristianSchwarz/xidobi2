/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 28.08.2013 10:52:14
 * Erstellt von: Konrad Schulz
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.DataBits.DATABITS_5;
import static org.xidobi.DataBits.DATABITS_6;
import static org.xidobi.FlowControl.FLOWCONTROL_NONE;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_IN;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_IN_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_RTSCTS_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_IN;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_IN_OUT;
import static org.xidobi.FlowControl.FLOWCONTROL_XONXOFF_OUT;
import static testtools.MessageBuilder.buffer;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.DataBits;
import org.xidobi.FlowControl;

/**
 * Tests the class {@link FlowControlCmd}.
 * 
 * @author Konrad Schulz
 */
@SuppressWarnings("javadoc")
public class TestFlowControlCmd {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private FlowControlCmd cmd;

	@Mock
	private DataOutput output;

	@Before
	public void setUp() throws IOException {
		initMocks(this);
	}

	/**
	 * When a <code>null</code> flowControl is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNull() {
		new FlowControlCmd((FlowControl) null);
	}

	/**
	 * When {@link FlowControl#FLOWCONTROL_RTSCTS_OUT} is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_with_RTSCTS_out() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The parameter >flowControl< must not be FLOWCONTROL_RTSCTS_OUT, use FLOWCONTROL_RTSCTS_IN_OUT instead.");

		new FlowControlCmd(FLOWCONTROL_RTSCTS_OUT);
	}

	/**
	 * When {@link FlowControl#FLOWCONTROL_XONXOFF_OUT} is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_with_XONXOFF_out() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The parameter >flowControl< must not be FLOWCONTROL_XONXOFF_OUT, use FLOWCONTROL_XONXOFF_IN_OUT instead.");

		new FlowControlCmd(FLOWCONTROL_XONXOFF_OUT);
	}

	/**
	 * Checks whether the flowControl is read correctly, {@link FlowControl#FLOWCONTROL_NONE}.
	 */
	@Test
	public void read_flowControlNone() throws IOException {
		cmd = new FlowControlCmd(buffer(1).toDataInput());

		assertThat(cmd.getFlowControl(), is(FLOWCONTROL_NONE));
	}

	/**
	 * Checks whether the flowControl is read correctly, {@link FlowControl#FLOWCONTROL_RTSCTS_IN}.
	 */
	@Test
	public void read_flowControlRTSCTSIn() throws IOException {
		cmd = new FlowControlCmd(buffer(16).toDataInput());

		assertThat(cmd.getFlowControl(), is(FLOWCONTROL_RTSCTS_IN));
	}

	/**
	 * Checks whether the flowControl is read correctly,
	 * {@link FlowControl#FLOWCONTROL_RTSCTS_IN_OUT}.
	 */
	@Test
	public void read_flowControlRTSCTSInOut() throws IOException {
		cmd = new FlowControlCmd(buffer(3).toDataInput());

		assertThat(cmd.getFlowControl(), is(FLOWCONTROL_RTSCTS_IN_OUT));
	}

	/**
	 * Checks whether the flowControl is read correctly, {@link FlowControl#FLOWCONTROL_XONXOFF_IN}.
	 */
	@Test
	public void read_flowControlXonXoffIn() throws IOException {
		cmd = new FlowControlCmd(buffer(15).toDataInput());

		assertThat(cmd.getFlowControl(), is(FLOWCONTROL_XONXOFF_IN));
	}

	/**
	 * Checks whether the flowControl is read correctly,
	 * {@link FlowControl#FLOWCONTROL_XONXOFF_IN_OUT}.
	 */
	@Test
	public void read_flowControlXonXoffInOut() throws IOException {
		cmd = new FlowControlCmd(buffer(2).toDataInput());

		assertThat(cmd.getFlowControl(), is(FLOWCONTROL_XONXOFF_IN_OUT));
	}

	/**
	 * Checks whether the encoded message is correct, {@link FlowControl#FLOWCONTROL_NONE}.
	 */
	@Test
	public void write_flowControlNone() throws IOException {
		new FlowControlCmd(FLOWCONTROL_NONE).write(output);

		verify(output).writeByte(1);
	}

	/**
	 * Checks whether the encoded message is correct, {@link FlowControl#FLOWCONTROL_RTSCTS_IN}.
	 */
	@Test
	public void write_flowControlRTSCTSIn() throws IOException {
		new FlowControlCmd(FLOWCONTROL_RTSCTS_IN).write(output);

		verify(output).writeByte(16);
	}

	/**
	 * Checks whether the encoded message is correct, {@link FlowControl#FLOWCONTROL_RTSCTS_IN_OUT}.
	 */
	@Test
	public void write_flowControlRTSCTSInOut() throws IOException {
		new FlowControlCmd(FLOWCONTROL_RTSCTS_IN_OUT).write(output);

		verify(output).writeByte(3);
	}

	/**
	 * Checks whether the encoded message is correct, {@link FlowControl#FLOWCONTROL_XONXOFF_IN}.
	 */
	@Test
	public void write_flowControlXonXoffIn() throws IOException {
		new FlowControlCmd(FLOWCONTROL_XONXOFF_IN).write(output);

		verify(output).writeByte(15);
	}

	/**
	 * Checks whether the encoded message is correct, {@link FlowControl#FLOWCONTROL_XONXOFF_IN_OUT}
	 * .
	 */
	@Test
	public void write_flowControlXonXoffInOut() throws IOException {
		new FlowControlCmd(FLOWCONTROL_XONXOFF_IN_OUT).write(output);

		verify(output).writeByte(2);
	}

	/**
	 * When the dataBits is invalid, an {@link IOException} must be thrown.
	 */
	@Test
	public void read_invalidDataBits() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected flowControl value: -3");

		cmd = new FlowControlCmd(buffer(-3).toDataInput());
	}

	/**
	 * When the flowControl is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	public void write_invalidFlowControl() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected flowControl value: -3");

		cmd = new FlowControlCmd(buffer(-3).toDataInput());
		cmd.write(output);
	}

	/**
	 * When the databits {@link #read(DataInput)} decoded value, has no corresponding
	 * {@link FlowControl} value, should be return a <code>null</code> value.
	 * 
	 * @throws Exception
	 */
	@Test
	public void getFlowControl_null() throws Exception {
		cmd = new FlowControlCmd(buffer(10).toDataInput());
		cmd.write(output);
		assertThat(cmd.getFlowControl(), is(nullValue()));
	}
	
	/**
	 * Checks whether the commands equal.
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		FlowControlCmd cmd =   new FlowControlCmd(FLOWCONTROL_RTSCTS_IN);
		FlowControlCmd cmd2 =   new FlowControlCmd(FLOWCONTROL_RTSCTS_IN);
		assertThat(cmd.equals(cmd2),is(true));
	}
	/**
	 * Checks whether the commands not equal.
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		FlowControlCmd cmd =   new FlowControlCmd(FLOWCONTROL_NONE);
		FlowControlCmd cmd2 =   new FlowControlCmd(FLOWCONTROL_RTSCTS_IN_OUT);
		assertThat(cmd.equals(cmd2), is(false));
	}
	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		FlowControlCmd cmd =   new FlowControlCmd(FLOWCONTROL_XONXOFF_IN_OUT);
		assertThat(cmd.toString(), is("FlowControlCmd [flowControl=2]"));
	}

}
