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

/**
 * Utility to provide a convenient way to build a sequence of byte's, that can converted to a desired format. 
 * @author Christian Schwarz
 *
 */
public class ByteBuffer {
	
	private final ByteArrayOutputStream bo = new ByteArrayOutputStream();
	private final DataOutput o = new DataOutputStream(bo);

	/** Appends the lower ordered byte fo the given int to this byte-buffer. The byte-buffer grows by one byte.*/
	public ByteBuffer putByte(int v) {
		try {
			o.writeByte(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}

	/** Appends the given int to this byte-buffer. The byte-buffer grows by four byte.*/
	public ByteBuffer putInt(int v) {
		try {
			o.writeInt(v);
		}
		catch (IOException cantHappen) {}
		return this;
	}
	/** Appends the lower ordered byte fo the all given int's to this byte-buffer. The byte-buffer grows by number of arguments.*/
	public ByteBuffer putBytes(int... v) {
		for (int i : v)
			putByte(i);

		return this;
	}

	/** Returns this byte-buffer as byte[].*/
	public byte[] toByteArray() {
		return bo.toByteArray();
	}
	
	/** Returns this byte-buffer as int[], every int of the array repesents one signed byte*/
	public int[] toIntArray() {

		final byte[] bytes = bo.toByteArray();
		final int[] r = new int[bytes.length];
		for (int i = 0; i < r.length; i++)
			r[i] = (byte) (bytes[i] & 0xff);

		return r;
	}
	
	/** Returns this byte-buffer as {@link DataInput}.*/
	public DataInput toDataInput(){
		return new DataInputStream(new ByteArrayInputStream(toByteArray()));
	}
	
	
}