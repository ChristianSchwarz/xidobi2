/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 09:44:31
 * Erstellt von: Christian Schwarz 
 */
package testtools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

class ByteArrayBuilder {

	private ByteArrayOutputStream bo = new ByteArrayOutputStream();
	private DataOutput o = new DataOutputStream(bo);

	/**
	 * Use IntArrayBuilder
	 */
	public ByteArrayBuilder() {}
	
	public ByteArrayBuilder putByte(int v) {
		try {
			o.writeByte(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}

	public ByteArrayBuilder putInt(int v) {
		try {
			o.writeInt(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}

	public int[] toIntArray() {

		final byte[] bytes = bo.toByteArray();
		final int[] r = new int[bytes.length];
		for (int i = 0; i < r.length; i++)
			r[i] = (byte) (bytes[i] & 0xff);

		return r;
	}
}