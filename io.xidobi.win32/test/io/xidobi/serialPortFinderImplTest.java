package io.xidobi;

import static org.junit.Assert.*;
import static org.xidobi.SerialPortSettings.from9600bauds8N1;

import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortFinder;
import org.xidobi.SerialPortFinderImpl;
import org.xidobi.SerialPortProvider;
import org.xidobi.SerialPortSettings;

public class serialPortFinderImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		SerialPortFinder finder = SerialPortProvider.getSerialPortFinder();
		Set<SerialPort> ports = finder.getAll();
		System.out.println(ports);
		
		SerialConnection c = finder.get("COM1").open(from9600bauds8N1().create());
		c.read();
	}

}
