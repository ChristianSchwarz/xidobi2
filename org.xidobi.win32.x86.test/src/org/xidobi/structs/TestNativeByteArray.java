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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
 * Tests the class {@link NativeByteArray}.
 * 
 * @author Tobias Breﬂler
 */
public class TestNativeByteArray {

	/** A pointer to the byte array struct. */
	private static final int POINTER = 3;
	/** The prefered size of the byte array. */
	private static final int LENGTH = 5;

	private static final byte[] DATA = new byte[5];

	/** Class under test. */
	private NativeByteArray byteArray;

	@Mock
	private WinApi win;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>os == null</code>.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullOS() {
		new NativeByteArray(null, LENGTH);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>length == 0</code>.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withLength0() {
		new NativeByteArray(null, 0);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>length</code> is
	 * negative.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNegativeLength() {
		new NativeByteArray(null, -1);
	}

	/**
	 * Verifies that the construction of a {@link NativeByteArray} allocates memory of a byte array
	 * on the heap via the WIN-API.
	 */
	@Test
	public void new_allocatesByteArray() {
		when(win.malloc(LENGTH)).thenReturn(POINTER);

		byteArray = new NativeByteArray(win, LENGTH);

		verify(win, times(1)).malloc(LENGTH);
	}

	/**
	 * Verifies that the method {@link NativeByteArray#dispose()} frees the memory of the
	 * {@link NativeByteArray} struct via the WIN-API.
	 */
	@Test
	public void dispose_freesOVERLAPPEDstruct() {
		when(win.malloc(LENGTH)).thenReturn(POINTER);

		byteArray = new NativeByteArray(win, LENGTH);
		byteArray.dispose();

		assertThat(byteArray.isDisposed(), is(true));
		verify(win).free(POINTER);
	}

	/**
	 * Verifies that {@link NativeByteArray#size()} returns the length that was passed to the
	 * constructor.
	 */
	@Test
	public void length() {
		byteArray = new NativeByteArray(win, LENGTH);
		assertThat(byteArray.size(), is(LENGTH));
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when the {@link NativeByteArray} is
	 * disposed and method {@link NativeByteArray#size()} is called.
	 */
	@Test(expected = IllegalStateException.class)
	public void length_whenDisposed() {
		byteArray = new NativeByteArray(win, LENGTH);
		byteArray.dispose();

		byteArray.size();
	}

	/**
	 * Verifies that {@link NativeByteArray#getByteArray()} returns the byte array for the allocated
	 * memory via the WIN-API.
	 */
	@Test
	public void getByteArray() {
		when(win.getByteArray(any(NativeByteArray.class), eq(LENGTH))).thenReturn(DATA);

		byteArray = new NativeByteArray(win, LENGTH);

		assertThat(byteArray.getByteArray(), is(DATA));
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when the {@link NativeByteArray} is
	 * disposed and method {@link NativeByteArray#getByteArray()} is called.
	 */
	@Test(expected = IllegalStateException.class)
	public void getByteArray_whenDisposed() {
		byteArray = new NativeByteArray(win, LENGTH);
		byteArray.dispose();

		byteArray.getByteArray();
	}

	/**
	 * Verifies that {@link NativeByteArray#getByteArray()} returns the byte array of the passed
	 * size for the allocated memory via the WIN-API.
	 */
	@Test
	public void getByteArray_withSize() {
		byte[] data = new byte[2];

		when(win.getByteArray(any(NativeByteArray.class), eq(2))).thenReturn(data);

		byteArray = new NativeByteArray(win, 5);

		assertThat(byteArray.getByteArray(2), is(data));
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when the passed
	 * <code>size</code> is greater than the length of the {@link NativeByteArray}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getByteArray_withTooHighSize() {
		byteArray = new NativeByteArray(win, LENGTH);
		byteArray.getByteArray(LENGTH + 2);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when the passed
	 * <code>size == 0</code>.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getByteArray_withSize0() {
		byteArray = new NativeByteArray(win, LENGTH);
		byteArray.getByteArray(0);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when a negative
	 * <code>size</code> is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getByteArray_withNegativeSize() {
		byteArray = new NativeByteArray(win, LENGTH);
		byteArray.getByteArray(-1);
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when the {@link NativeByteArray} is
	 * disposed and method {@link NativeByteArray#getByteArray(int)} is called.
	 */
	@Test(expected = IllegalStateException.class)
	public void getByteArray_withSizeWhenDisposed() {
		byteArray = new NativeByteArray(win, LENGTH);
		byteArray.dispose();

		byteArray.getByteArray(LENGTH);
	}

}
