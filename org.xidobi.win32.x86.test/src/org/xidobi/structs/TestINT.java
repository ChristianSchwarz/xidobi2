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
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertThat;

/**
 * Tests for class {@link INT}.
 * 
 * @author Tobias Breﬂler
 */
public class TestINT {

	/**
	 * Verifies that {@link INT#value} is 0, when no value was passed to constructor.
	 */
	@Test
	public void new_withoutValue() {
		INT result = new INT();
		assertThat(result.value, is(0));
	}

	/**
	 * Verifies that {@link INT#value} is <code>10</code>, when value <code>10</code> was passed to
	 * constructor.
	 */
	@Test
	public void new_withValue() {
		INT result = new INT(10);
		assertThat(result.value, is(10));
	}

	/**
	 * Verifies that {@link INT#toString()} returns a String that represents the value that was
	 * passed to the constructor.
	 */
	@Test
	public void test_toString() {
		INT obj = new INT(42);
		assertThat(obj.toString(), is("42"));
	}

	/**
	 * Verifies the contract for the <code>equals()</code> and <code>hashCode()</code> methods. In
	 * this case the hashcode for the two instances (with the same value) must be equal.
	 */
	@Test
	public void equalsHashCodeContract_hashCodeWithSameValues() {
		int same1 = new INT(12).hashCode();
		int same2 = new INT(12).hashCode();

		assertThat(same1, is(same2));
	}

	/**
	 * Verifies the contract for the <code>equals()</code> and <code>hashCode()</code> methods. In
	 * this case the hashcode for the two instances (with different values) must be unequal.
	 */
	@Test
	public void equalsHashCodeContract_hashCodeWithDifferentValues() {
		int instance = new INT(1).hashCode();
		int unequal = new INT(2).hashCode();

		assertThat(instance, is(not(unequal)));
	}

	/**
	 * Verifies the contract for the <code>equals()</code> and <code>hashCode()</code> methods. In
	 * this case the two instances with the same value must be equal.
	 */
	@Test
	public void equalsHashCodeContract_equalWithSameValues() {
		INT same1 = new INT(12);
		INT same2 = new INT(12);

		assertThat(same1.equals(same2), is(true));
	}

	/**
	 * Verifies the contract for the <code>equals()</code> and <code>hashCode()</code> methods. In
	 * this case the instance must be equal to itself.
	 */
	@Test
	public void equalsHashCodeContract_equalWithSameInstance() {
		INT instance = new INT(12);

		assertThat(instance.equals(instance), is(true));
	}

	/**
	 * Verifies the contract for the <code>equals()</code> and <code>hashCode()</code> methods. In
	 * this case the instance must be unequal to <code>null</code>.
	 */
	@Test
	public void equalsHashCodeContract_unequalWithNull() {
		INT instance = new INT(12);

		assertThat(instance.equals(null), is(false));
	}

	/**
	 * Verifies the contract for the <code>equals()</code> and <code>hashCode()</code> methods. In
	 * this case the instance must be unequal to another class.
	 */
	@Test
	public void equalsHashCodeContract_unequalWithOtherClass() {
		INT instance = new INT(12);

		assertThat(instance.equals("other class"), is(false));
	}

	/**
	 * Verifies the contract for the <code>equals()</code> and <code>hashCode()</code> methods. In
	 * this case the two instance with different values must be unequal.
	 */
	@Test
	public void equalsHashCodeContract_unequalWithDifferentValues() {
		INT instance = new INT(1);
		INT unequal = new INT(2);

		assertThat(instance.equals(unequal), is(false));
	}
}
