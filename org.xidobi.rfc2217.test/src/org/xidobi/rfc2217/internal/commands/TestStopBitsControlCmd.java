package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
import org.xidobi.DataBits;
import org.xidobi.StopBits;

/**
 * Tests the class {@link StopBitsControlCmd}.
 * 
 * @author Christin Nitsche
 * @author Konrad Schulz
 */
@SuppressWarnings("javadoc")
public class TestStopBitsControlCmd {

	@Rule
	public ExpectedException exception = none();

	private StopBitsControlCmd cmd;

	@Mock
	private DataOutput output;

	@Before
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
	 * 
	 */
	@Test
	public void write_invalidStopBits() throws IOException {
		cmd = new StopBitsControlCmd(buffer(10).toDataInput());
		cmd.write(output);
		verify(output).writeByte(10); 
	}

	/**
	 * When the stopbits {@link #read(DataInput)} decoded value, has no corresponding
	 * {@link StopBits} value, should be return a <code>null</code> value.
	 * 
	 * @throws Exception
	 */
	@Test
	public void stopBits_null() throws Exception {
		cmd = new StopBitsControlCmd(buffer(10).toDataInput());
		cmd.write(output);
		assertThat(cmd.getStopBits(), is(nullValue()));
	}
	
	/**
	 * Checks whether the commands equal.
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		StopBitsControlCmd cmd =   new StopBitsControlCmd(STOPBITS_1_5);
		StopBitsControlCmd cmd2 =   new StopBitsControlCmd(STOPBITS_1_5);
		assertThat(cmd.equals(cmd2),is(true));
	}
	/**
	 * Checks whether the commands not equal.
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		StopBitsControlCmd cmd =   new StopBitsControlCmd(STOPBITS_1_5);
		StopBitsControlCmd cmd2 =  new StopBitsControlCmd(STOPBITS_1);
		assertThat(cmd.equals(cmd2), is(false));
	}
	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		StopBitsControlCmd cmd =   new StopBitsControlCmd(STOPBITS_1_5);
		assertThat(cmd.toString(), is("StopBitsControlCmd [stopBits=2]"));
	}
}
