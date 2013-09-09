/*
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static testtools.MessageBuilder.buffer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Tests the class {@link SignatureControlCmd}
 * 
 * @author Christin Nitsche
 */
public class TestSignatureControlCmd {

	@Rule
	public ExpectedException exception = none();

	@Mock
	private DataOutput output;

	private SignatureControlCmd cmd;
	@Mock
	private DataInput input;

	private String iac;

	@Before
	public void setUp() throws IOException {
		initMocks(this);
		iac = Character.toString((char) 255);
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
		new SignatureControlCmd((String) null);
	}

	/**
	 * Checks whether the signatur is read correctly.
	 */
	@Test
	public void read_Signatur() throws Exception {
		cmd = new SignatureControlCmd(buffer().putBytes("version 1.0").toDataInput());
		assertThat(cmd.getSignature(), is("version 1.0"));
	}

	/**
	 * Check whether the encoded message is correct.
	 */
	@Test
	public void write_signatur() throws Exception {
		cmd = new SignatureControlCmd("version 1.0");
		cmd.write(output);
		verify(output).writeChars("version 1.0");
	}

	/**
	 * Checks whether the signatur is read correctly, when an IAC present. If an IAC character
	 * appears in the text it must be translated to IAC-IAC.
	 */
	@Test
	public void read_SignaturWithOnlyIac() throws Exception {
		cmd = new SignatureControlCmd(buffer().putBytes(iac).toDataInput());
		assertThat(cmd.getSignature(), is(iac + iac));
	}

	/**
	 * Check whether the encoded message is correct, when an IAC present.If an IAC character appears
	 * in the text it must be translated to IAC-IAC.
	 */
	@Test
	public void write_signaturWithOnlyIac() throws Exception {
		cmd = new SignatureControlCmd(iac);
		cmd.write(output);
		verify(output).writeChars(iac);
	}

	/**
	 * Checks whether the signatur is read correctly, when an IAC present.If an IAC character
	 * appears in the text it must be translated to IAC-IAC.
	 */
	@Test
	public void read_SignaturWithIac() throws Exception {
		cmd = new SignatureControlCmd(buffer().putBytes("version" + iac + "1.0" + iac).toDataInput());
		assertThat(cmd.getSignature(), is("version" + iac + iac + "1.0" + iac + iac));
	}

	/**
	 * Check whether the encoded message is correct, when an IAC present.If an IAC character appears
	 * in the text it must be translated to IAC-IAC.
	 */
	@Test
	public void write_signaturWithIac() throws Exception {
		cmd = new SignatureControlCmd("version" + iac + "1.0" + iac);
		cmd.write(output);
		verify(output).writeChars("version" + iac + iac + "1.0" + iac + iac);
	}

	/**
	 * Checks whether the commands equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void equalCommands() throws Exception {
		SignatureControlCmd cmd = new SignatureControlCmd("version 1.0");
		SignatureControlCmd cmd2 = new SignatureControlCmd("version 1.0");
		assertThat(cmd.equals(cmd2), is(true));
	}

	/**
	 * Checks whether the commands not equal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notEqualCommands() throws Exception {
		SignatureControlCmd cmd = new SignatureControlCmd("version 1.0");
		SignatureControlCmd cmd2 = new SignatureControlCmd("version 2.0");
		assertThat(cmd.equals(cmd2), is(false));
	}

	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		SignatureControlCmd cmd = new SignatureControlCmd("version 1.0");
		assertThat(cmd.toString(), is("SignatureControlCmd [signature=version 1.0]"));
	}
}
