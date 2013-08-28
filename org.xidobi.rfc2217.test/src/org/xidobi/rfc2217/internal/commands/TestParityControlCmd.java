/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 23.08.2013 15:02:21
 * Erstellt von: Peter-Ren� Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.Parity.PARITY_EVEN;
import static org.xidobi.Parity.PARITY_MARK;
import static org.xidobi.Parity.PARITY_NONE;
import static org.xidobi.Parity.PARITY_ODD;
import static org.xidobi.Parity.PARITY_SPACE;
import static testtools.MessageBuilder.buffer;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.Parity;

/**
 * Tests the class {@link ParityControlCmd}.
 * 
 * @author Peter-Ren� Jeschke
 */
public class TestParityControlCmd {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ParityControlCmd cmd;

	@Mock
	private DataOutput output;

	@Before
	@SuppressWarnings("javadoc")
	public void setup() throws IOException {
		initMocks(this);
	}

	/**
	 * When a <code>null</code> parity is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_withNegativeDatasize() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The parameter >parity< must not be null");
		new ParityControlCmd((Parity) null);
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_NONE}.
	 */
	@Test
	public void read_parityNone() throws IOException {
		cmd = new ParityControlCmd(buffer(1).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_NONE));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_ODD}.
	 */
	@Test
	public void read_parityOdd() throws IOException {
		cmd = new ParityControlCmd(buffer(2).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_ODD));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_EVEN}.
	 */
	@Test
	public void read_parityEven() throws IOException {
		cmd = new ParityControlCmd(buffer(3).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_EVEN));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_MARK}.
	 */
	@Test
	public void read_parityMark() throws IOException {
		cmd = new ParityControlCmd(buffer(4).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_MARK));
	}

	/**
	 * Checks whether the parity is read correctly, {@link Parity#PARITY_SPACE}.
	 */
	@Test
	public void read_paritySpace() throws IOException {
		cmd = new ParityControlCmd(buffer(5).toDataInput());

		assertThat(cmd.getParity(), is(PARITY_SPACE));
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_NONE}.
	 */
	@Test
	public void write_parityNone() throws IOException {
		cmd = new ParityControlCmd(PARITY_NONE);
		cmd.write(output);

		verify(output).writeByte(1);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_ODD}.
	 */
	@Test
	public void write_parityOdd() throws IOException {
		cmd = new ParityControlCmd(PARITY_ODD);
		cmd.write(output);

		verify(output).writeByte(2);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_EVEN}.
	 */
	@Test
	public void write_parityEven() throws IOException {
		cmd = new ParityControlCmd(PARITY_EVEN);
		cmd.write(output);

		verify(output).writeByte(3);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_MARK}.
	 */
	@Test
	public void write_parityMark() throws IOException {
		cmd = new ParityControlCmd(PARITY_MARK);
		cmd.write(output);

		verify(output).writeByte(4);
	}

	/**
	 * Checks whether the encoded message is correct, {@link Parity#PARITY_SPACE}.
	 */
	@Test
	public void write_paritySpace() throws IOException {
		cmd = new ParityControlCmd(PARITY_SPACE);
		cmd.write(output);

		verify(output).writeByte(5);
	}

	/**
	 * When the parity is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	@SuppressWarnings("unused")
	public void read_invalidParity() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected parity value: 6");

		new ParityControlCmd(buffer(6).toDataInput());
	}

	/**
	 * When the parity is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	public void write_invalidParity() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected parity value: 6");

		cmd = new ParityControlCmd(buffer(6).toDataInput());
		cmd.write(output);
	}
}