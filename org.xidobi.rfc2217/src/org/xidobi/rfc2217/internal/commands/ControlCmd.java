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

import org.xidobi.rfc2217.internal.RFC2217;

/**
 * @author Christian Schwarz
 */
public interface ControlCmd {

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
	public abstract byte getCommandCode();

}