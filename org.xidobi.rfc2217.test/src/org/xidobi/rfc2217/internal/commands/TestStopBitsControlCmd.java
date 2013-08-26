package org.xidobi.rfc2217.internal.commands;

import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Tests the class {@link StopBitsControlCmd}.
 * 
 * @author Christin Nitsche
 * 
 */
public class TestStopBitsControlCmd {
	@Rule
	public ExpectedException exception = none();

	private StopBitsControlCmd cmd;

	@Mock
	private DataInput input;

	@Before
	public void setUp() throws IOException {
		initMocks(this);
		when(input.readByte()).thenReturn((byte) 2);

		cmd = new StopBitsControlCmd(2);

	}

	/**
	 * Checks whether the stopsize is read correctly.
	 */
	@Test
	public void read_isCorrect() throws Exception {
		cmd = new StopBitsControlCmd(input);
		assertThat(cmd.getStopBits(), Matchers.is(2));
	}

	/**
	 * When the stopsize is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	public void read_invalidStopsize() throws Exception {
		exception.expect(IOException.class);
		exception.expectMessage("The received stopsize is invalid! Expected a value greater or equal to 1, got: >-1<");

		when(input.readByte()).thenReturn((byte) -1);
		new StopBitsControlCmd(input);
	}

	/**
	 * When a stopsize that is smaller than 1 is supplied to the constructor, an
	 * {@link IllegalArgumentException} should be thrown.
	 * 
	 */
	@Test
	public void new_withNegativeStopsize() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The stopsize must not be less than 1! Got: >-2<");
		new StopBitsControlCmd(-2);
	}



	/**
	 * Checks whether the encoded message is correct.
	 */
	@Test
	public void write_correctData() throws Exception {
		DataOutput output = Mockito.mock(DataOutput.class);
		cmd.write(output);

		verify(output).writeByte(2); 	// The stop bits
	}
}
