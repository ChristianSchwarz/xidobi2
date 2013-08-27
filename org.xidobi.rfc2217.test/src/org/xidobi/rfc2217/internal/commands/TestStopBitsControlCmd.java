package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.StopBits.STOPBITS_1;
import static org.xidobi.StopBits.STOPBITS_1_5;
import static org.xidobi.StopBits.STOPBITS_2;
import static testtools.MessageBuilder.buffer;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.StopBits;

/**
 * Tests the class {@link StopBitsControlCmd}.
 * 
 * @author Christin Nitsche
 * @author Konrad Schulz
 */
public class TestStopBitsControlCmd {

	@Rule
	public ExpectedException exception = none();

	private StopBitsControlCmd cmd;

	@Mock
	private DataOutput output;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() throws IOException {
		initMocks(this);
	}

	/**
	 * When a <code>null</code> stopBit is supplied to the constructor, an
	 * {@link IllegalArgumentException} must be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_withNullStopBits() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The parameter >stopBits< must not be null");
		new StopBitsControlCmd((StopBits) null);
	}

	/**
	 * Checks whether the stopBit is read correctly, {@link StopBits#STOPBITS_1}.
	 */
	@Test
	public void read_stopBits_1() throws IOException {
		cmd = new StopBitsControlCmd(buffer(1).toDataInput());

		assertThat(cmd.getStopBits(), is(STOPBITS_1));
	}

	/**
	 * Checks whether the stopBit is read correctly, {@link StopBits#STOPBITS_1_5}.
	 */
	@Test
	public void read_stopBits_1_5() throws IOException {
		cmd = new StopBitsControlCmd(buffer(2).toDataInput());

		assertThat(cmd.getStopBits(), is(STOPBITS_1_5));
	}

	/**
	 * Checks whether the stopBit is read correctly, {@link StopBits#STOPBITS_2}.
	 */
	@Test
	public void read_stopBits_2() throws IOException {
		cmd = new StopBitsControlCmd(buffer(3).toDataInput());

		assertThat(cmd.getStopBits(), is(STOPBITS_2));
	}

	/**
	 * Checks whether the encoded message is correct, {@link StopBits#STOPBITS_1}.
	 */
	@Test
	public void write_stopBits_1() throws IOException {
		cmd = new StopBitsControlCmd(STOPBITS_1);
		cmd.write(output);

		verify(output).writeByte(1);
	}

	/**
	 * Checks whether the encoded message is correct, {@link StopBits#STOPBITS_1_5}.
	 */
	@Test
	public void write_stopBits_1_5() throws IOException {
		cmd = new StopBitsControlCmd(STOPBITS_1_5);
		cmd.write(output);

		verify(output).writeByte(2);
	}

	/**
	 * Checks whether the encoded message is correct, {@link StopBits#STOPBITS_2}.
	 */
	@Test
	public void write_stopBits_2() throws IOException {
		cmd = new StopBitsControlCmd(STOPBITS_2);
		cmd.write(output);

		verify(output).writeByte(3);
	}

	/**
	 * When the parity is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	@SuppressWarnings("unused")
	public void read_invalidStopBits() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected stopBits value: 6");

		new StopBitsControlCmd(buffer(6).toDataInput());
	}

	/**
	 * When the parity is invalid, an {@link IOException} should be thrown.
	 */
	@Test
	public void write_invalidStopBits() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Unexpected stopBits value: 6");

		cmd = new StopBitsControlCmd(buffer(6).toDataInput());
		cmd.write(output);
	}
}
