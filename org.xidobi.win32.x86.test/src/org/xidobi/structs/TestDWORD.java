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
package org.xidobi.structs;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.WinApi;

/**
 * Tests the class {@link DWORD}.
 * 
 * @author Tobias Breﬂler
 */
public class TestDWORD {

	/** A pointer to the byte array struct. */
	private static final int POINTER = 3;
	/** The prefered size of the byte array. */
	private static final int SIZE = 4;

	/** Class under test. */
	private DWORD dword;

	@Mock
	private WinApi os;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(os.sizeOf_DWORD()).thenReturn(SIZE);
		when(os.malloc(SIZE)).thenReturn(POINTER);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>os == null</code>.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullOS() {
		new DWORD(null);
	}

	/**
	 * Verifies that the construction of a {@link DWORD} allocates memory on the heap via the
	 * WIN-API.
	 */
	@Test
	public void new_allocatesByteArray() {
		dword = new DWORD(os);

		verify(os, times(1)).malloc(SIZE);
	}

	/**
	 * Verifies that the method {@link DWORD#dispose()} frees the memory of the {@link DWORD} struct
	 * via the WIN-API.
	 */
	@Test
	public void dispose_freesOVERLAPPEDstruct() {
		dword = new DWORD(os);
		dword.dispose();

		assertThat(dword.isDisposed(), is(true));
		verify(os).free(POINTER);
	}

	/**
	 * Verifies that {@link DWORD#getValue()} returns the value for the allocated memory via the
	 * WIN-API.
	 */
	@Test
	public void getValue() {
		dword = new DWORD(os);
		when(os.getValue_DWORD(dword)).thenReturn(200);

		assertThat(dword.getValue(), is(200));
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when the {@link DWORD} is disposed
	 * and method {@link DWORD#getValue()} is called.
	 */
	@Test(expected = IllegalStateException.class)
	public void getValue_whenDisposed() {
		dword = new DWORD(os);
		dword.dispose();

		dword.getValue();
	}

	/**
	 * Verifies that {@link DWORD#setValue(int)} sets the value for the allocated memory via the
	 * WIN-API.
	 */
	@Test
	public void setValue() {
		dword = new DWORD(os);

		dword.setValue(200);

		verify(os, times(1)).setValue_DWORD(dword, 200);
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when the {@link DWORD} is disposed
	 * and method {@link DWORD#setValue(int)} is called.
	 */
	@Test(expected = IllegalStateException.class)
	public void setValue_whenDisposed() {
		dword = new DWORD(os);
		dword.dispose();

		dword.setValue(200);
	}

}
