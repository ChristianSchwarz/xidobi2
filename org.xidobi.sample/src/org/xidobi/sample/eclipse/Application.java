package org.xidobi.sample.eclipse;

import static java.lang.Long.MAX_VALUE;
import static java.lang.System.out;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	public Object start(IApplicationContext context) throws Exception {

		SerialPortFinder finder = SerialPortProvider.getSerialPortFinder();
		
		Set<SerialPort> ports = finder.getAll();
		for (SerialPort port : ports) {
			out.println(port);
			if ("COM1".equals(port.getPortName()))
				connect(port).awaitTermination(MAX_VALUE, DAYS);
			
		}
		
		return EXIT_OK;
	}

	/**
	 * @param port
	 * @return 
	 * @throws IOException 
	 */
	private ScheduledExecutorService connect(SerialPort port) throws IOException {
		
		SerialConnection connection = port.open(from9600_8N1().create());
		ScheduledExecutorService ex = newScheduledThreadPool(2);
		ex.scheduleAtFixedRate(write(connection), 0, 1, SECONDS);
		ex.scheduleWithFixedDelay(read(connection), 0, 1, MILLISECONDS);
		return ex;
	}

	/**
	 * @param connection 
	 * @return
	 */
	private Runnable read(final SerialConnection connection) {
		return new Runnable() {
			
			public void run() {
				try {
					byte[] bytes = connection.read();
					System.out.println(new String(bytes));
				}
				catch (Exception e) {
					e.printStackTrace();
					new RuntimeException(e);
				}
			}
		};
	}

	/**
	 * @param connection 
	 * @return
	 */
	private Runnable write(final SerialConnection connection) {
		return new Runnable() {
			
			public void run() {
				try {
					connection.write("Hello Wörld!".getBytes());
				}
				catch (Exception e) {
					e.printStackTrace();
					new RuntimeException(e);
				}
			}
		};
	}

	public void stop() {

	}
}
