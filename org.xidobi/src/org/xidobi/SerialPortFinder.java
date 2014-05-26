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
package org.xidobi;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Interface for classes which can find serial ports that are installed on the
 * system.
 * <p>
 * <b>IMPORTANT</b> Platform integrators must provide an implementation of this
 * interface! The implementing class must be named
 * {@code org.xidobi.SerialPortFinderImpl}, in order to be instantiable for the
 * {@link SerialPortProvider}!
 * 
 * @author Tobias Bre�ler
 * 
 * @see SerialPort
 * @see SerialPortProvider
 */
public interface SerialPortFinder {

	/**
	 * Returns a {@link Set} with all serial ports that are installed on the
	 * system.
	 * 
	 * @return a {@link Set} with serial ports or an empty {@link Set} if no
	 *         ports are available, but never <code>null</code>
	 */
	@Nonnull
	Set<SerialPort> getAll();

	/**
	 * Returns the serial port with the given {@code portName} if it exists,
	 * otherwise <code>null</code>.
	 * 
	 * @param portName
	 *            the name of the serial port (e.g. "COM1" or "tty0"), must not
	 *            be <code>null</code>
	 * @return the serial port or <code>null</code>, if the port not exists
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>portName==null</code>
	 */
	@CheckForNull
	SerialPort get(@Nonnull String portName);

}
