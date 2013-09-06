/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 06.09.2013 15:04:09
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;

/**
 * Tests the class {@link ControlRequestEncoder}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestControlRequestEncoder {

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	public ControlCmd message;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * When null is passed an {@link IllegalArgumentException} must be thrown.
	 * 
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void encode_null() throws IOException {
		ControlRequestEncoder.encode(null);
	}

	/**
	 * When an message is passed to encoded the given message, the resulting binary form must start
	 * with  the com-port-option (44) followed by the command-code (in this case the dummy
	 * number 99) and lasty the content of the given message (in this case 1,2,3). 
	 */
	@Test
	public void encode_message() throws Exception {
		doAnswer(writeToDataOutput(1, 2, 3)).when(message).write(any(DataOutput.class));

		when(message.getCommandCode()).thenReturn((byte) 99);

		int[] bytes = ControlRequestEncoder.encode(message);
		assertThat(bytes, is(new int[] { 44, 99, 1, 2, 3 }));
	}

	// ////////////////////////////////////
	/**
	 * writes the given byte-values to the {@link DataOutput}
	 */
	private Answer<?> writeToDataOutput(final int... bytes) {
		return new Answer<Void>() {

			public Void answer(InvocationOnMock invocation) throws Throwable {
				DataOutput out = (DataOutput) invocation.getArguments()[0];
				for (int b : bytes)
					out.write(b);

				return null;
			}
		};
	}
}
