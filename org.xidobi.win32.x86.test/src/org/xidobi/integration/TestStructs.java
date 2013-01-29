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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.xidobi.OS;
import org.xidobi.WinApi;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.Pointer;

/**
 * Integration test for classes {@link Pointer}, {@link NativeByteArray} and other Java
 * representations of structs.
 * 
 * @author Tobias Breﬂler
 */
public class TestStructs {

	private WinApi win;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		win = OS.OS;
	}

	/**
	 * Verifies that a pointer can be created and disposed many times without crashing the VM.
	 */
	@Test(timeout = 1500)
	public void allocatePointerAndDisposeLoop() {
		for (int i = 0; i < 500_000; i++) {
			Pointer pointer = new Pointer(win, 5000);
			pointer.dispose();
		}
	}

	/**
	 * Verifies that a pointer can be created and disposed many times parallel without crashing the
	 * VM.
	 */
	@Test(timeout = 1500)
	public void allocatePointersParallelAndThenDisposeLoop() {
		Pointer[] pointers = new Pointer[500_000];

		for (int i = 0; i < 500_000; i++)
			pointers[i] = new Pointer(win, 1024);

		for (int i = 0; i < 500_000; i++)
			pointers[i].dispose();
	}

	/**
	 * Verifies that a {@link NativeByteArray} can be created and disposed many times without
	 * crashing the VM. Additionally the {@link NativeByteArray#getByteArray()} method is called.
	 */
	@Test(timeout = 1500)
	public void allocateNativeByteArrayAndDisposeLoop() {
		for (int i = 0; i < 500_000; i++) {
			NativeByteArray byteArray = new NativeByteArray(win, 1024);

			byte[] result = byteArray.getByteArray();
			assertThat(result, is(notNullValue()));
			assertThat(result.length, is(1024));

			byteArray.dispose();
		}
	}

	/**
	 * Verifies that a {@link DWORD} can be created and disposed many times without crashing the VM.
	 * Additionally the {@link DWORD#setValue(int)} and {@link DWORD#getValue()} methods are called.
	 */
	@Test(timeout = 1500)
	public void allocateDWORDAndSetAndGetValueLoop() {
		for (int i = 0; i < 100_000; i++) {
			DWORD dword = new DWORD(win);
			assertThat(dword.getValue(), is(0));
			dword.setValue(100);
			assertThat(dword.getValue(), is(100));
		}
	}
}
