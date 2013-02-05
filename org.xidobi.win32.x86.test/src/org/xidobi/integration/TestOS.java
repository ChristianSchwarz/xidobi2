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
package org.xidobi.integration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.xidobi.OS;
import org.xidobi.WinApi;

/**
 * Integration test for class {@link OS}.
 * 
 * @author Tobias Breﬂler
 */
public class TestOS {

	private WinApi os = OS.OS;

	/**
	 * Verifies that no exception is thrown, when <code>malloc</code>, <code>memset</code> and
	 * <code>free</code> are called.
	 */
	@Test
	public void mallocMemsetAndFree() {
		int pointer = os.malloc(1024);
		os.memset(pointer, 0, 1024);
		os.free(pointer);
	}

	/**
	 * Verifies that {@link OS#sizeOf_DWORD()} returns 4.
	 */
	@Test
	public void sizeOf_DWORD() {
		int size = os.sizeOf_DWORD();
		assertThat(size, is(4));
	}

	/**
	 * Verifies that {@link OS#sizeOf_OVERLAPPED()} returns 20.
	 */
	@Test
	public void sizeOf_OVERLAPPED() {
		int size = os.sizeOf_OVERLAPPED();
		assertThat(size, is(20));
	}

	/**
	 * Verifies that {@link OS#sizeOf_HKEY()} returns 4.
	 */
	@Test
	public void sizeOf_HKEY() {
		int size = os.sizeOf_HKEY();
		assertThat(size, is(4));
	}

}
