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
package org.xidobi.sample.eclipse;

import static java.lang.Long.MAX_VALUE;
import static java.lang.System.out;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.xidobi.SerialPortSettings.from9600_8N1;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortFinder;
import org.xidobi.SerialPortProvider;

/**
 * This class controls all aspects of the application's execution.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class Application implements IApplication {
	
	/** {@inheritDoc} */
	public Object start(IApplicationContext context) throws Exception {

		SerialPortFinder finder = SerialPortProvider.getSerialPortFinder();

		Set<SerialPort> ports = finder.getAll();
		for (SerialPort port : ports) {
			out.println(port);
			if ("COM75".equals(port.getPortName()))
				connect(port).awaitTermination(MAX_VALUE, DAYS);
		}

		return EXIT_OK;
	}

	/** Connects the serial port an starts write and read tests. */
	private ScheduledExecutorService connect(SerialPort port) throws IOException {

		SerialConnection connection = port.open(from9600_8N1().create());
		ScheduledExecutorService ex = newScheduledThreadPool(2);
		ex.scheduleAtFixedRate(write(connection), 0, 1, SECONDS);
		ex.scheduleWithFixedDelay(read(connection), 0, 1, SECONDS);
		return ex;
	}

	/** Returns the runnable for the read test */
	private Runnable read(final SerialConnection connection) {
		return new Runnable() {

			public void run() {
				try {
					if (!connection.isClosed()) {
						byte[] bytes = connection.read();
						System.out.println(new String(bytes));
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};
	}

	private int i = 0;

	/** Returns the runnable for the write test */
	private Runnable write(final SerialConnection connection) {
		return new Runnable() {

			public void run() {
				try {
					if (!connection.isClosed())
						connection.write(("\"Hello World!\", was said for the " + (i++) + ". time.").getBytes());
				}
				catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	/** {@inheritDoc} */
	public void stop() {}
}
