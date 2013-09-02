/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 02.09.2013 11:47:57
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Tests the class {@link ReaderImpl}.
 * 
 * @author Peter-René Jeschke
 */
public class TestReaderImpl {

	@Rule
	public ExpectedException exception = none();

	private ReaderImpl reader;

	@Mock
	private InputStream input;

	@Before
	public void setup() {
		initMocks(this);
		reader = new ReaderImpl(input);
	}

	/**
	 * Expects to throw an {@link IllegalArgumentException} if the argument for the constructor is
	 * null.
	 **/
	@SuppressWarnings("unused")
	@Test
	public void new_withNull() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Argument >inputStream< must not be null!");
		new ReaderImpl(null);
	}

	/**
	 * When the input is smaller than the buffer(4096), an array with the length of the input must
	 * be returned.
	 */
	@Test
	public void read_smallerThanBuffer() throws IOException {
		when(input.read(any(byte[].class))).thenAnswer(readIntoBuffer(5));
		byte[] expectedResult = { 0, 1, 2, 3, 4 };
		assertThat(reader.read(), is(expectedResult));
	}

	/**
	 * When the input is smaller than the buffer(4096), an array with the length of the input must
	 * be returned.
	 */
	@Test
	public void read_EOS() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("End of stream was detected while reading from TCP.");

		when(input.read(any(byte[].class))).thenAnswer(readIntoBuffer(-1));
		byte[] emptyArray = new byte[0];
		assertThat(reader.read(), is(emptyArray));
	}

	/**
	 * Creates a new Answer for Mokito that reads incrementing bytes into the first argument of the
	 * method (expecting a byte-array).
	 */
	private Answer<Integer> readIntoBuffer(final int length) {
		return new Answer<Integer>() {

			public Integer answer(InvocationOnMock invocation) throws Throwable {
				byte[] buffer = (byte[]) invocation.getArguments()[0];
				for (byte i = 0; i < length; i++) {
					buffer[i] = i;
				}
				return length;
			}
		};
	}
}
