/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 22.08.2013 09:44:31
 * Erstellt von: Christian Schwarz 
 */
package testtools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteBuffer {

	private ByteArrayOutputStream bo = new ByteArrayOutputStream();
	private DataOutput o = new DataOutputStream(bo);

	/**
	 * Use IntArrayBuilder
	 */
	public ByteBuffer() {}

	public ByteBuffer putByte(int v) {
		try {
			o.writeByte(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}

	public ByteBuffer putInt(int v) {
		try {
			o.writeInt(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}

	public ByteBuffer putBytes(int... v) {
		for (int i : v)
			putByte(i);

		return this;
	}

	public byte[] toByteArray() {
		return bo.toByteArray();
	}
	
	public int[] toIntArray() {

		final byte[] bytes = bo.toByteArray();
		final int[] r = new int[bytes.length];
		for (int i = 0; i < r.length; i++)
			r[i] = (byte) (bytes[i] & 0xff);

		return r;
	}
	
	public DataInput toDataInput(){
		return new DataInputStream(new ByteArrayInputStream(toByteArray()));
	}
	
	
}