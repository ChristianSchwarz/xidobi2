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

import org.junit.Test;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

/**
 * Tests the class COMSTAT.
 * 
 * @author Tobias Breﬂler
 */
public class TestCOMSTAT {

	private COMSTAT comstat;

	/**
	 * Verifies that {@link COMSTAT#toString()} returns a String with <code>cbInQue == 0</code> and
	 * <code>cbOutQue == 0</code>, when no value was set before.
	 */
	@Test
	public void toString_withoutValuesSetBefore() {
		comstat = new COMSTAT();
		assertThat(comstat.toString(), is("COMSTAT [cbInQue=0, cbOutQue=0]"));
	}

	/**
	 * Verifies that {@link OVERLAPPED#toString()} returns a String with a <code>hEvent</code>, when
	 * <code>hEvent</code> was set before.
	 */
	@Test
	public void toString_withValuesSetBefore() {
		comstat = new COMSTAT();
		comstat.cbInQue = 100;
		comstat.cbOutQue = 200;
		assertThat(comstat.toString(), is("COMSTAT [cbInQue=100, cbOutQue=200]"));
	}

}
