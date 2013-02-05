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
import static org.xidobi.WinApi.NULL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.WinApi;

/**
 * Tests the class {@link Pointer}
 * 
 * @author Tobias Breﬂler
 */
public class TestPointer {

	/** Some pointer to the allocated memory. */
	private static final int DUMMY_POINTER = 3;
	/** Size of the memory. */
	private static final int DUMMY_SIZE = 2;

	/** Class under test. */
	private Pointer pointer;

	@Mock
	private WinApi win;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);
		when(win.malloc(DUMMY_SIZE)).thenReturn(DUMMY_POINTER);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>win == null</code>.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullWinApi() {
		new Pointer(null, DUMMY_SIZE);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>length == 0</code>.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withLength0() {
		new Pointer(win, 0);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>length</code> is
	 * negative.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNegativeLength() {
		new Pointer(win, -1);
	}

	/**
	 * Verifies that the construction of a {@link Pointer} allocates memory via the WIN-API.
	 */
	@Test
	public void new_allocatesMemory() {

		pointer = new Pointer(win, DUMMY_SIZE);

		verify(win, times(1)).malloc(DUMMY_SIZE);
	}
	/**
	 * Verifies that an {@link OutOfMemoryError} is thrown if {@link WinApi#malloc(int)} returns {@link WinApi#NULL}.
	 */
	@Test
	public void new_outOfMemory() {
		when(win.malloc(DUMMY_SIZE)).thenReturn(NULL);
		exception.expect(OutOfMemoryError.class);
		exception.expectMessage("Unable to allocate "+DUMMY_SIZE+"bytes of memory for type: "+Pointer.class.getSimpleName());


		pointer = new Pointer(win, DUMMY_SIZE);
	}

	/**
	 * Verifies that the method {@link Pointer#dispose()} frees the memory via the WIN-API.
	 */
	@Test
	public void dispose_freesMemory() {

		pointer = new Pointer(win, DUMMY_SIZE);
		pointer.dispose();

		assertThat(pointer.isDisposed(), is(true));
		verify(win).free(DUMMY_POINTER);
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when the pointer is already
	 * disposed.
	 */
	@Test(expected = IllegalStateException.class)
	public void dispose_whenAlreadyDisposed() {
		pointer = new Pointer(win, DUMMY_SIZE);
		pointer.dispose();

		pointer.dispose();
	}

	/**
	 * Verifies that {@link Pointer#size()} returns the size that was passed to the constructor.
	 */
	@Test
	public void size() {
		pointer = new Pointer(win, DUMMY_SIZE);

		assertThat(pointer.size(), is(DUMMY_SIZE));
	}

	/**
	 * Verifies that an {@link IllegalStateException} is thrown, when {@link Pointer#size()} is
	 * called and the {@link Pointer} was already disposed.
	 */
	@Test(expected = IllegalStateException.class)
	public void size_whenDisposed() {
		pointer = new Pointer(win, DUMMY_SIZE);

		pointer.dispose();

		assertThat(pointer.size(), is(DUMMY_SIZE));
	}

}
