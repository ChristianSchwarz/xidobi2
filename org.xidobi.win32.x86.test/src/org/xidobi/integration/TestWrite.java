package org.xidobi.integration;

import java.io.IOException;

import org.junit.Test;
import org.xidobi.OS;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortHandle;
import org.xidobi.SerialPortHandleImpl;
import org.xidobi.SerialPortSettings;

import static org.xidobi.SerialPortSettings.bauds;

public class TestWrite {

	@Test
	public void test() throws IOException {
		OS os = OS.OS;
		SerialPortHandle portHandle = new SerialPortHandleImpl(os, "XXX");
		while(true) {
			System.out.println("-----------------");
			SerialPort connection = null;
			try {
				connection = portHandle.open(bauds(9600).create());
				connection.write("Hallo".getBytes());
			}
			finally {
				if (connection!=null)
					connection.close();

			}
		}
	}

}