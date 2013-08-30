package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.Parity.PARITY_MARK;
import static org.xidobi.Parity.PARITY_ODD;
import static testtools.MessageBuilder.buffer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.cglib.core.Converter;

import testtools.ByteBuffer;
import testtools.MessageBuilder;

/**
 * Tests the class {@link SignaturControlCmd}
 * 
 * @author Christin Nitsche
 * 
 */
public class TestSignaturControlCmd {

	@Rule
	public ExpectedException exception = none();

	@Mock
	private DataOutput output;

	private SignaturControlCmd cmd;
	@Mock
	private DataInput input;

	@Before
	public void setUp() throws IOException {
		initMocks(this);
	}

	/**
	 * When a <code>null</code> signatur is supplied to the constructor, an
	 * {@link IllegalArgumentException} must ne thrown.
	 * 
	 * @throws Exception
	 */
	@Test
	public void new_withNull() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The parameter >signatur< must not be null");
		new SignaturControlCmd((String) null);
	}

	/**
	 * Checks whether the signatur is read correctly.
	 */
	@Test
	public void read_Signatur() throws Exception {
		cmd = new SignaturControlCmd(buffer().putBytes("version 1.0").toDataInput());
		assertThat(cmd.getSignatur(), is("version 1.0"));
	}

	/**
	 * Check whether the encoded message is correct.
	 */
	@Test
	public void write_signatur() throws Exception {
		cmd = new SignaturControlCmd("version 1.0");
		cmd.write(output);
		verify(output).writeChars("version 1.0");
	}

	/**
	 * Checks whether the commands equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		SignaturControlCmd cmd = new SignaturControlCmd("version 1.0");
		SignaturControlCmd cmd2 = new SignaturControlCmd("version 1.0");
		assertThat(cmd.equals(cmd2), is(true));
	}

	/**
	 * Checks whether the commands not equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		SignaturControlCmd cmd = new SignaturControlCmd("version 1.0");
		SignaturControlCmd cmd2 = new SignaturControlCmd("version 2.0");
		assertThat(cmd.equals(cmd2), is(false));
	}

	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		SignaturControlCmd cmd = new SignaturControlCmd("version 1.0");
		assertThat(cmd.toString(), is("SignaturControlCmd [signatur=version 1.0]"));
	}
}
