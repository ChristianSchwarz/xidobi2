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
package testtools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Utility to provide a convenient way to build a sequence of byte's, that can converted to a
 * desired format.
 * 
 * @author Christian Schwarz
 * 
 */
public class ByteBuffer {

	private final ByteArrayOutputStream bo = new ByteArrayOutputStream();
	private final DataOutput o = new DataOutputStream(bo);

	/**
	 * Appends the lower ordered byte fo the given int to this byte-buffer. The byte-buffer grows by
	 * one byte.
	 * 
	 * @param v
	 *            the byte to append
	 * @return this
	 */
	public ByteBuffer putByte(int v) {
		try {
			o.writeByte(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}

	
	/**
	 * Appends the lower ordered byte fo the given int to this byte-buffer. The byte-buffer grows by
	 * one byte.
	 * 
	 * @param v
	 *            the String to append
	 * @return this
	 */
	public ByteBuffer putBytes(String v) {
		try {
			o.writeBytes(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}
	/**
	 * Appends the given int to this byte-buffer. The byte-buffer grows by four byte.
	 * 
	 * @param v
	 *            the int to append
	 * @return this
	 */
	public ByteBuffer putInt(int v) {
		try {
			o.writeInt(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}

	/**
	 * Appends the lower ordered byte fo the all given int's to this byte-buffer. The byte-buffer
	 * grows by number of arguments.
	 * 
	 * @param v
	 *            the byte values to add
	 * @return this
	 */
	public ByteBuffer putBytes(int... v) {
		for (int i : v)
			putByte(i);

		return this;
	}

	/**
	 * Returns this byte-buffer as byte[].
	 * 
	 * @return this byte-buffer as byte[]
	 */
	public byte[] toByteArray() {
		return bo.toByteArray();
	}

	/**
	 * Returns this byte-buffer as int[], every int of the array repesents one signed byte
	 * 
	 * @return this byte-buffer as int[]
	 */
	public int[] toIntArray() {

		final byte[] bytes = bo.toByteArray();
		final int[] r = new int[bytes.length];
		for (int i = 0; i < r.length; i++)
			r[i] = (byte) (bytes[i] & 0xff);

		return r;
	}

	/**
	 * Returns this byte-buffer as {@link DataInput}.
	 * 
	 * @return byte-buffer as {@link DataInput}
	 */
	public DataInput toDataInput() {
		return new DataInputStream(new ByteArrayInputStream(toByteArray()));
	}

}