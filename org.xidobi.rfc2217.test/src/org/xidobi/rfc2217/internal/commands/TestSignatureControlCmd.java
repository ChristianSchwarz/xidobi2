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
import static org.xidobi.Parity.PARITY_SPACE;
import static testtools.MessageBuilder.buffer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import com.google.common.testing.EqualsTester;

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
		exception.expectMessage("The parameter >signature< must not be null");
		new SignatureControlCmd((String) null);
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
	 * Check whether the encoded message is correct, when an IAC present.If an IAC character appears
	 * in the text it must be translated to IAC-IAC.
	 */
	@Test
	public void write_signaturWithOnlyIac() throws Exception {
		cmd = new SignatureControlCmd(iac);
		cmd.write(output);
		verify(output).writeChars(iac+iac);
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
	 * Checks whether the signatur is read correctly.
	 */
	@Test
	public void read_Signatur() throws Exception {
		cmd = new SignatureControlCmd(buffer().putBytes("version 1.0").toDataInput());
		assertThat(cmd.getSignature(), is("version 1.0"));
	}

	/**
	 * When no bytes are contained in the {@link DataInput} the content must be a empty string
	 * @throws IOException 
	 */
	@Test
	public void read_empty() throws IOException{
		final DataInput emptyInput = buffer().toDataInput();
		cmd = new SignatureControlCmd(emptyInput);
		assertThat(cmd.getSignature(), is(""));
	}
	
	/**
	 * Checks whether the signatur is read correctly, when an IAC present. If an IAC character
	 * appears in the text it must be translated to IAC-IAC.
	 */
	@Test
	public void read_signaturWithOneIac() throws Exception {
		cmd = new SignatureControlCmd(buffer().putBytes(iac).toDataInput());
		assertThat(cmd.getSignature(), is(iac));
	}

	/**
	 * Checks whether the signatur is read correctly, when an IAC present.If an IAC character
	 * appears in the text it must be translated to IAC-IAC.
	 */
	@Test
	public void read_signaturWithIac() throws Exception {
		cmd = new SignatureControlCmd(buffer().putBytes(iac + iac).toDataInput());
		assertThat(cmd.getSignature(), is(iac));
	}

	/**
	 * Checks the equals/hashCode contract.
	 *  
	 * @throws Exception
	 */
	//@formatter:off
	@Test
	public void equalsHashCode() throws Exception {
		new EqualsTester()
		.addEqualityGroup(new SignatureControlCmd(""),
		                  new SignatureControlCmd(""),
		                  new SignatureControlCmd(buffer().toDataInput()),
		                  new SignatureControlCmd(buffer().toDataInput()))
			                  
		.addEqualityGroup(new SignatureControlCmd(buffer().putByte(111).toDataInput()),
		                  new SignatureControlCmd(buffer().putByte(111).toDataInput()))
		.testEquals();
	}
	//@formatter:on

	/**
	 * Checks whether the String command is correct.
	 */
	@Test
	public void commandToString() throws Exception {
		SignatureControlCmd cmd = new SignatureControlCmd("version 1.0");
		assertThat(cmd.toString(), is("SignatureControlCmd [signature=version 1.0]"));
	}
}
