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
package org.xidobi.spi;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface for I/O operations.
 * 
 * @author Tobias Breﬂler
 */
public interface IoOperation extends Closeable {

	/**
	 * The implementation must release all native resources.
	 * <p>
	 * This method will be called by {@link BasicSerialConnection#close()} if the port is not
	 * closed.
	 * <p>
	 * <b>IMPORTANT:</b> Dont call this method yourself! Otherwise there is no guaratee that the
	 * port is currently open!
	 */
	void close() throws IOException;

	/**
	 * Disposes all resources that was allocated by this I/O operation.
	 * <p>
	 * This method will be called by {@link BasicSerialConnection#close()} if the port is not
	 * closed.
	 * <p>
	 * <b>IMPORTANT:</b> Dont call this method yourself! Otherwise there is no guaratee that the
	 * port is currently open!
	 */
	void dispose();

}
