/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 23.08.2013 15:02:21
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;

/**
 * Tests the class {@link ParityControlCmd}.
 * 
 * @author Peter-René Jeschke
 */
public class TestParityControlCmd {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ParityControlCmd cmd;

	@Mock
	private DataInput input;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		when(input.readByte()).thenReturn((byte) 3);

		
	}

	/**
	 * When a negative pairty is supplied to the constructor, an {@link IllegalArgumentException}
	 * should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_withNegativeDatasize() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The parity must not be negative! Got: >-3<");

		new ParityControlCmd(-3);
	}

	/**
	 * Checks whether the parity is read correctly.
	 */
	@Test
	public void read_isCorrect() throws IOException {
		cmd = new ParityControlCmd(input);

		assertThat(cmd.getParity(), is(3));
	}

	/**
	 * When the parity is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	public void read_invalidParity() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The received parity is invalid! Expected a value greater or equal to 0, got: >-3<");

	
		when(input.readByte()).thenReturn((byte) -3);

		new ParityControlCmd(input);
	}

	/**
	 * Checks whether the encoded message is correct.
	 */
	@Test
	public void write_correctData() throws IOException {
		
		cmd = new ParityControlCmd(3);
		DataOutput output = mock(DataOutput.class);
		
		cmd.write(output);

		verify(output).writeByte(3); // The parity
	}
}
