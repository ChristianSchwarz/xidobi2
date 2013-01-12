package org.xidobi;

import java.io.IOException;
import java.util.concurrent.Executor;

public interface SerialPort {

	void write(byte[] data) throws IOException;
	
	void setReceiver(Receiver receiver);
	void setReceiver(Receiver receiver, Executor executor);
}
