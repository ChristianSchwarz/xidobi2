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
package org.xidobi.rfc2217.internal;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;

/**
 * Tests the class {@link WriterImpl}.
 * 
 * @author Peter-René Jeschke
 */
public class TestWriterImpl {

	@Rule
	public ExpectedException exception = none();

	private WriterImpl writer;

	@Mock
	private OutputStream out;

	@Before
	public void setup() {
		initMocks(this);
		writer = new WriterImpl(out);
	}

	/**
	 * Expects to throw an {@link IllegalArgumentException} if the argument for the constructor is
	 * null.
	 **/
	@SuppressWarnings("unused")
	@Test
	public void new_withNull() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >outputStream< must not be null!");
		new WriterImpl(null);
	}

	/**
	 * When the writer is requested to write bytes, the bytes must be written to the outputstream
	 * and the stream must be flushed.
	 */
	@Test
	public void write_writesData() throws IOException {
		byte[] data = new byte[] { 1, 2, 3, 4, 5 };
		writer.write(data);

		verify(out).write(data);
		verify(out).flush();
	}

	/**
	 * When <code>null</code> is supplied as data to write(), an {@link IllegalArgumentException}
	 * must be thrown.
	 */
	@Test
	public void write_withNull() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >data< must not be null!");
		writer.write(null);
	}

}
