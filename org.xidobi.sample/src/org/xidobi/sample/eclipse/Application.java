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

import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.xidobi.SerialPortSettings.from9600_8N1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

		System.out.println("Available serial ports:");

		Set<SerialPort> ports = finder.getAll();
		for (SerialPort port : ports) {
			System.out.println(" " + port.getPortName() + " (" + port.getDescription() + ")");
		}

		System.out.print("Please enter the name of the port you want to test: ");

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String portName = reader.readLine();

		SerialPort port = finder.get(portName);

		while (true) {
			try {
				connect(port).awaitTermination(MAX_VALUE, DAYS);
				System.out.println("Restarting connection...");
			}
			catch (Exception e) {
				// Do nothing!
			}
		}
	}

	/** Connects the serial port an starts write and read tests. */
	private ScheduledExecutorService connect(SerialPort port) throws IOException {

		SerialConnection connection = port.open(from9600_8N1().create());

		System.out.println("Connected to " + port.getPortName() + ".");

		ScheduledExecutorService ex = newScheduledThreadPool(3);

		ex.scheduleAtFixedRate(write(connection, ex), 0, 1, SECONDS);
		ex.scheduleWithFixedDelay(read(connection, ex), 0, 1, SECONDS);
		ex.scheduleAtFixedRate(close(connection, ex), 15, 15, SECONDS);

		return ex;
	}

	/** Returns the runnable for the read test */
	private Runnable read(final SerialConnection connection, final ScheduledExecutorService ex) {
		return new Runnable() {
			public void run() {
				try {
					if (!connection.isClosed()) {
						byte[] read = connection.read();
						System.out.println("Read: " + new String(read));
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					ex.shutdownNow();
					throw new RuntimeException(e);
				}
			}
		};
	}

	private int i = 0;

	/** Returns the runnable for the write test */
	private Runnable write(final SerialConnection connection, final ScheduledExecutorService ex) {
		return new Runnable() {
			public void run() {
				try {
					if (!connection.isClosed()) {
						String string = "\"Hello World!\", was said for the " + (i++) + ". time.";
						connection.write(string.getBytes());
						System.out.println("Written: " + string);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					ex.shutdownNow();
					throw new RuntimeException(e);
				}
			}	
		};
	}

	/** Returns the runnable that closes the connection when RETURN is pressed. */
	private Runnable close(final SerialConnection connection, final ScheduledExecutorService ex) {
		return new Runnable() {
			public void run() {
				try {
					System.err.println("Closing port...");
					connection.close();
					System.out.println("Port closed!");
					ex.shutdownNow();
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
