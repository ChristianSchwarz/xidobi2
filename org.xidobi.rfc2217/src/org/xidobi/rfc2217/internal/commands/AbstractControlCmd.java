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
package org.xidobi.rfc2217.internal.commands;

import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.xidobi.rfc2217.internal.RFC2217;

/**
 * Baseclass for configuration commands defined in <a
 * href="https://www.ietf.org/rfc/rfc2217.txt">RFC 2217</a>.
 * 
 * @author Christian Schwarz
 * @author Peter-René Jeschke
 */
public abstract class AbstractControlCmd<T_Xidobi, T_Rfc2217> implements ControlCmd {

	/** The code for the command. */
	private final byte commandCode;

	private Map<T_Xidobi, T_Rfc2217> mapXidobiToRfc2217 = new HashMap<T_Xidobi, T_Rfc2217>();
	private Map<T_Rfc2217, T_Xidobi> mapRfc2217ToXidobi = new HashMap<T_Rfc2217, T_Xidobi>();
	/**
	 * This constructor is used by subclasses to create a new message.
	 * 
	 * @param commandCode
	 *            the code of this command, must either be between [0..12] or [100..112]
	 * @exception IllegalArgumentException
	 *                if the commandCode is neither between [0..12] nor [100..112]
	 */
	AbstractControlCmd(int commandCode) {
		if (!((commandCode >= 0 && commandCode <= 12) || (commandCode >= 100 && commandCode <= 112)))
			throw new IllegalArgumentException("The command code must be in the range [0..12] or [100..112]! Got: " + commandCode);
		this.commandCode = (byte) commandCode;
	}

	/**
	 * Adds a mapping of an equivalen xidobi-RFC2217 value pair. 
	 * @param xidobiValue
	 * @param rfc2217Value
	 */
	final void addEquivalents(@Nullable T_Xidobi xidobiValue,@Nullable T_Rfc2217 rfc2217Value){
		mapXidobiToRfc2217.put(xidobiValue, rfc2217Value);
		mapRfc2217ToXidobi.put(rfc2217Value, xidobiValue);
	}
	
	/** Returns the equivalent xidobi value of the given RFC2217 value, or null	 */
	final T_Xidobi getXidobiEquivalent(T_Rfc2217 value){
		return mapRfc2217ToXidobi.get(value);
	}
	
	/** Returns the equivalent RFC2217 value of the given xidobi value, or null	 */
	final T_Rfc2217 getRfc2217Equivalent(T_Xidobi value){
		return mapXidobiToRfc2217.get(value);
	}
	
	/**
	 * Subclasses implement this method to encode the contents of this command.
	 * 
	 * @param output
	 *            the output where the encoded message must be written to
	 * @throws IOException
	 *             if the output can't be written to
	 */
	public abstract void write(DataOutput output) throws IOException;

	/**
	 * Returns the code of this command as defined in RFC2217.
	 * 
	 * @return the code of this command
	 * @see RFC2217
	 */
	public final byte getCommandCode() {
		return commandCode;
	}

}
