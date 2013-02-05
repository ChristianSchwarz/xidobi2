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
 * Tests the class {@link OVERLAPPED}.
 * 
 * @author Tobias Breﬂler
 */
public class TestOVERLAPPED {

	/** Some pointer to the OVERLAPPED struct. */
	private static final int A_OVERLAPPED_POINTER = 3;
	/** Size of an OVERLAPPED struct. */
	private static final int SIZEOF_OVERLAPPED = 2;

	/** Class under test. */
	private OVERLAPPED overlapped;

	@Mock
	private WinApi os;

	/** expected exceptions */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		initMocks(this);

		when(os.sizeOf_OVERLAPPED()).thenReturn(SIZEOF_OVERLAPPED);
		when(os.malloc(SIZEOF_OVERLAPPED)).thenReturn(A_OVERLAPPED_POINTER);
	}

	/**
	 * Verifies that an {@link IllegalArgumentException} is thrown, when <code>os == null</code>.
	 */
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void new_withNullOS() {
		new OVERLAPPED(null);
	}

	/**
	 * Verifies that the construction of a OVERLAPPED allocates memory of a OVERLAPPED struct via
	 * the WIN-API.
	 */
	@Test
	public void new_allocatesOVERLAPPEDstruct() {
		overlapped = new OVERLAPPED(os);

		verify(os, times(1)).sizeOf_OVERLAPPED();
		verify(os, times(1)).malloc(SIZEOF_OVERLAPPED);
	}

	/**
	 * Verifies that the method {@link OVERLAPPED#dispose()} frees the memory of the OVERLAPPED
	 * struct via the WIN-API.
	 */
	@Test
	public void dispose_freesOVERLAPPEDstruct() {
		overlapped = new OVERLAPPED(os);
		overlapped.dispose();

		assertThat(overlapped.isDisposed(), is(true));
		verify(os).free(A_OVERLAPPED_POINTER);
	}

	/**
	 * Verifies that {@link OVERLAPPED#toString()} returns a String with <code>hEvent == 0</code>,
	 * when no <code>hEvent</code> was set before.
	 */
	@Test
	public void toString_withoutHEvent() {
		overlapped = new OVERLAPPED(os);
		assertThat(overlapped.toString(), is("OVERLAPPED [hEvent=0]"));
	}

	/**
	 * Verifies that {@link OVERLAPPED#toString()} returns a String with a <code>hEvent</code>, when
	 * <code>hEvent</code> was set before.
	 */
	@Test
	public void toString_withHEvent() {
		overlapped = new OVERLAPPED(os);
		overlapped.hEvent = 100;
		assertThat(overlapped.toString(), is("OVERLAPPED [hEvent=100]"));
	}

}
