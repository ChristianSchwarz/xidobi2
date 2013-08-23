/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 13:19:26
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
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
 * Tests the class {@link DatasizeControlCmd}.
 * 
 * @author Peter-René Jeschke
 */
public class TestDatasizeControlCmd {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private DatasizeControlCmd cmd;

	@Mock
	private DataInput input;

	@Before
	public void setup() throws IOException {
		initMocks(this);

		when(input.readByte()).thenReturn((byte) 3);

		cmd = new DatasizeControlCmd(3);
	}

	/**
	 * When a negative datasize is supplied to the constructor, an {@link IllegalArgumentException}
	 * should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_withNegativeDatasize() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The dataSize must not be less than 1! Got: >-3<");

		new DatasizeControlCmd(-3);
	}

	/**
	 * Checks whether the dataSize is read correctly.
	 */
	@Test
	public void read_isCorrect() throws IOException {
		cmd = new DatasizeControlCmd(input);

		assertThat(cmd.getDataSize(), is(3));
	}

	/**
	 * When the dataSize is invalid, an {@link IOException} should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void read_invalidDataSize() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The received datasize is invalid! Expected a value greater or equal to 1, got: >-3<");

		when(input.readByte()).thenReturn((byte) -3);

		new DatasizeControlCmd(input);
	}

	/**
	 * Checks whether the encoded message is correct.
	 */
	@Test
	public void write_correctData() throws IOException {
		DataOutput output = mock(DataOutput.class);
		cmd.write(output);

		InOrder orderedVerification = inOrder(output);

		orderedVerification.verify(output).write(44); // COM-PORT-OPTION
		orderedVerification.verify(output).write(2); // SET-DATASIZE
		orderedVerification.verify(output).writeByte(3); // The datasize
	}

}
