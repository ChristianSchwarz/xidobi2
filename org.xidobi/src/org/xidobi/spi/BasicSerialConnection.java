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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.xidobi.SerialPort;

/**
 * @author Christian Schwarz
 */
public class BasicSerialConnection extends AbstractSerialConnection {

	private Reader reader;
	private Writer writer;

	protected BasicSerialConnection(SerialPort port,
									Reader reader,
									Writer writer) {
		super(port);
		this.reader = reader;
		this.writer = writer;
	}

	@Override
	protected void writeInternal(@Nonnull byte[] data) throws IOException {
		writer.write(data);
	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {
		return reader.read();
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	protected void closeInternal() throws IOException {
		try {
			writer.close();
		}
		finally {
			reader.close();
		}
	}

}
