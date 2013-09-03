/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 13:19:26
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.DataBits;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.xidobi.DataBits.DATABITS_5;
import static org.xidobi.DataBits.DATABITS_6;
import static org.xidobi.DataBits.DATABITS_7;
import static org.xidobi.DataBits.DATABITS_8;
import static org.xidobi.DataBits.DATABITS_9;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;
import static testtools.MessageBuilder.buffer;

/**
 * Tests the class {@link DataBitsControlCmd}.
 * 
 * @author Peter-René Jeschke
 * @author Konrad Schulz
 */
@SuppressWarnings("javadoc")
public class TestDataBitsControlCmd {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private DataBitsControlCmd cmd;

	@Mock
	private DataOutput output;

	@Before
	public void setUp() throws IOException {
		initMocks(this);
	}

	/**
	 * When a <code>null</code> dataBits is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_null() {
		new DataBitsControlCmd((DataBits) null);
	}

	/**
	 * Checks whether the dataBits is read correctly.
	 */
	@Test
	public void read_dataBits_5() throws IOException {
		cmd = new DataBitsControlCmd(buffer(5).toDataInput());

		assertThat(cmd.getDataBits(), is(DATABITS_5));
	}

	/**
	 * Checks whether the dataBits is read correctly, {@link DataBits#DATABITS_6}.
	 */
	@Test
	public void read_dataBits_6() throws IOException {
		cmd = new DataBitsControlCmd(buffer(6).toDataInput());

		assertThat(cmd.getDataBits(), is(DATABITS_6));
	}

	/**
	 * Checks whether the dataBits is read correctly, {@link DataBits#DATABITS_7}.
	 */
	@Test
	public void read_dataBits_7() throws IOException {
		cmd = new DataBitsControlCmd(buffer(7).toDataInput());

		assertThat(cmd.getDataBits(), is(DATABITS_7));
	}

	/**
	 * Checks whether the dataBits is read correctly, {@link DataBits#DATABITS_8}.
	 */
	@Test
	public void read_dataBits_8() throws IOException {
		cmd = new DataBitsControlCmd(buffer(8).toDataInput());

		assertThat(cmd.getDataBits(), is(DATABITS_8));
	}

	/**
	 * Checks whether the dataBits is read correctly, {@link DataBits#DATABITS_9}.
	 */
	@Test
	public void read_dataBits_9() throws IOException {
		cmd = new DataBitsControlCmd(buffer(9).toDataInput());

		assertThat(cmd.getDataBits(), is(DATABITS_9));
	}

	/**
	 * Checks whether the encoded message is correct, {@link DataBits#DATABITS_5}.
	 */
	@Test
	public void write_dataBits_5() throws IOException {
		new DataBitsControlCmd(DATABITS_5).write(output);

		verify(output).writeByte(5); // The dataBits
	}

	/**
	 * Checks whether the encoded message is correct, {@link DataBits#DATABITS_6}.
	 */
	@Test
	public void write_dataBits_6() throws IOException {
		new DataBitsControlCmd(DATABITS_6).write(output);

		verify(output).writeByte(6); // The dataBits
	}

	/**
	 * Checks whether the encoded message is correct, {@link DataBits#DATABITS_7}.
	 */
	@Test
	public void write_dataBits_7() throws IOException {
		new DataBitsControlCmd(DATABITS_7).write(output);

		verify(output).writeByte(7); // The dataBits
	}

	/**
	 * Checks whether the encoded message is correct, {@link DataBits#DATABITS_8}.
	 */
	@Test
	public void write_dataBits_8() throws IOException {
		new DataBitsControlCmd(DATABITS_8).write(output);

		verify(output).writeByte(8); // The dataBits
	}

	/**
	 * Checks whether the encoded message is correct, {@link DataBits#DATABITS_9}.
	 */
	@Test
	public void write_dataBits_9() throws IOException {
		new DataBitsControlCmd(DATABITS_9).write(output);

		verify(output).writeByte(9); // The dataBits
	}

	/**
	 * When the dataBits is invalid, an {@link IOException} must be thrown.
	 */
	@Test
	public void read_invalidDataBits() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected dataBits value: -3");

		cmd = new DataBitsControlCmd(buffer(-3).toDataInput());
	}

	/**
	 * 
	 */
	@Test
	public void write_invalidDataBits() throws IOException {
		cmd = new DataBitsControlCmd(buffer(10).toDataInput());
		cmd.write(output);
		verify(output).writeByte(10); 
	}

	/**
	 * When the databits {@link #read(DataInput)} decoded value, has no corresponding
	 * {@link DataBits} value, should be return a <code>null</code> value.
	 * 
	 * @throws Exception
	 */
	@Test
	public void dataBits_null() throws Exception {
		cmd = new DataBitsControlCmd(buffer(10).toDataInput());
		cmd.write(output);
		assertThat(cmd.getDataBits(), is(nullValue()));
	}

	/**
	 * Checks whether the commands equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		DataBitsControlCmd cmd = new DataBitsControlCmd(DATABITS_5);
		DataBitsControlCmd cmd2 = new DataBitsControlCmd(DATABITS_5);
		assertThat(cmd.equals(cmd2), is(true));
	}

	/**
	 * Checks whether the commands not equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		DataBitsControlCmd cmd = new DataBitsControlCmd(DATABITS_5);
		DataBitsControlCmd cmd2 = new DataBitsControlCmd(DATABITS_6);
		assertThat(cmd.equals(cmd2), is(false));
	}

	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		DataBitsControlCmd cmd = new DataBitsControlCmd(DATABITS_5);
		assertThat(cmd.toString(), is("DataBitsControlCmd [dataBits=5]"));
	}

}
