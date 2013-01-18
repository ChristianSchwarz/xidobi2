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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.OS;
import org.xidobi.WinApi;

/**
 * Tests the class {@link HKEY}.
 * 
 * @author Tobias Breﬂler
 */
public class TestHKEY {

	/** Some pointer to the HKEY struct. */
	private static final int A_HKEY_POINTER = 3;
	/** Size of an HKEY struct. */
	private static final int SIZEOF_HKEY = 2;

	/** Class under test. */
	private HKEY hkey;

	@Mock
	private WinApi os;

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
		new HKEY(null);
	}

	/**
	 * Verifies that the construction of a HKEY allocates memory of a HKEY struct via the WIN-API.
	 */
	@Test
	public void new_allocatesHKEYstruct() {
		when(os.sizeOf_HKEY()).thenReturn(SIZEOF_HKEY);
		when(os.malloc(SIZEOF_HKEY)).thenReturn(A_HKEY_POINTER);

		hkey = new HKEY(os);

		verify(os, times(1)).sizeOf_HKEY();
		verify(os, times(1)).malloc(SIZEOF_HKEY);
	}

	/**
	 * Verifies that the method {@link HKEY#dispose()} frees the memory of the HKEY struct via the
	 * WIN-API.
	 */
	@Test
	public void dispose_freesHKEYstruct() {
		when(os.sizeOf_HKEY()).thenReturn(SIZEOF_HKEY);
		when(os.malloc(SIZEOF_HKEY)).thenReturn(A_HKEY_POINTER);

		hkey = new HKEY(os);
		hkey.dispose();

		assertThat(hkey.isDisposed(), is(true));
		verify(os).free(A_HKEY_POINTER);
	}

}
