package io.xidobi.win32;


import static com.sun.jna.platform.win32.WinBase.EV_RXCHAR;
import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;
import static com.sun.jna.platform.win32.WinBase.PURGE_RXCLEAR;
import static com.sun.jna.platform.win32.WinBase.PURGE_TXCLEAR;
import static com.sun.jna.platform.win32.WinError.ERROR_ACCESS_DENIED;
import static com.sun.jna.platform.win32.WinError.ERROR_FILE_NOT_FOUND;
import static com.sun.jna.platform.win32.WinNT.FILE_FLAG_OVERLAPPED;
import static com.sun.jna.platform.win32.WinNT.GENERIC_READ;
import static com.sun.jna.platform.win32.WinNT.GENERIC_WRITE;
import static com.sun.jna.platform.win32.WinNT.OPEN_EXISTING;
import static io.xidobi.win32.DCBConfigurator.DCB_CONFIGURATOR;
import static io.xidobi.win32.Throwables.newIOException;
import static io.xidobi.win32.Throwables.newNativeCodeException;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortSettings;
import org.xidobi.spi.NativeCodeException;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase.DCB;
import com.sun.jna.platform.win32.WinNT.HANDLE;
/**
 * {@link SerialPort} to open a serial port.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see SerialPort
 */
public class SerialPortImpl implements SerialPort {

	

	/** the native Win32-API, never <code>null</code> */
	@Nonnull
	private final Kernel32  os;

	/** the name of this port, eg. "COM1", never <code>null</code> */
	@Nonnull
	private final String portName;

	/**
	 * configures the native DCB "struct" with the values from the serial port settings, never
	 * <code>null</code>
	 */
	@Nonnull
	private final DCBConfigurator configurator;

	/** The additional description for the serial port, maybe <code>null</code> */
	@Nullable
	private String description;

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link WinApi}.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param portName
	 *            the name of this port, must not be <code>null</code>
	 * @param description
	 *            the additional description for the serial port, maybe <code>null</code>
	 */
	public SerialPortImpl(	@Nonnull String portName,
							@Nullable String description) {
		this(portName, description,  DCB_CONFIGURATOR);
	}

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link WinApi}.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param portName
	 *            the name of this port, must not be <code>null</code>
	 * @param description
	 *            the additional description for the serial port, maybe <code>null</code>
	 * @param configurator
	 *            configures the native DCB "struct" with the values from the serial port settings,
	 *            must not be <code>null</code>
	 */
	public SerialPortImpl(	@Nonnull String portName,
							@Nullable String description,
							@Nonnull DCBConfigurator configurator) {
		this.portName = checkArgumentNotNull(portName, "portName");
		this.os = Kernel32.INSTANCE;
		this.configurator = checkArgumentNotNull(configurator, "configurator");
		this.description = description;
	}

	/** {@inheritDoc} */
	@Nonnull
	public SerialConnection open(@Nonnull SerialPortSettings settings) throws IOException {
		checkArgumentNotNull(settings, "settings");

		final HANDLE handle = tryOpen(portName);
		try {
			applySettings(handle, settings);
			clearIOBuffers(handle);
			registerRxEvent(handle);
		}
		catch (IOException e) {
			os.CloseHandle(handle);
			throw e;
		}
		catch (NativeCodeException e) {
			os.CloseHandle(handle);
			throw e;
		}

		return new SerialConnectionImpl(this, os, handle);
	}

	/**
	 * Tries to open the port and returns the handle of the port.
	 * 
	 * @return the handle of the port on success
	 * @throws IOException
	 *             if the port is already open or does not exist
	 */
	private HANDLE tryOpen(final String portName) throws IOException {
		HANDLE handle = os.CreateFile("\\\\.\\" + portName, GENERIC_READ | GENERIC_WRITE, 0, null, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, null);

		if (handle != INVALID_HANDLE_VALUE)
			return handle;

		int err = os.GetLastError();

		switch (err) {
			case ERROR_ACCESS_DENIED:
				throw new IOException("Port in use (" + portName + ")!");
			case ERROR_FILE_NOT_FOUND:
				throw new IOException("Port not found (" + portName + ")!");
		}
		throw lastError("Unable to open port (" + portName + ")!");
	}

	/**
	 * Tries to apply the {@link SerialPortSettings} to the port.
	 * 
	 * @throws IOException
	 *             if it was not possible to apply the settings e.g. if they are invalid
	 */
	private void applySettings(final HANDLE handle, final SerialPortSettings settings) throws IOException {
		final DCB dcb = new DCB();

		if (!os.GetCommState(handle, dcb))
			throw lastError("Unable to retrieve the current control settings for port (" + portName + ")!");

		configurator.configureDCB(dcb, settings);

		if (!os.SetCommState(handle, dcb))
			throw lastError("Unable to set the control settings (" + portName + ")!\r\n "+settings+"\r\n "+dcb);
	}

	/**
	 * Discards all characters from the output and input buffer of a specified communications
	 * resource.
	 * 
	 * @param handle
	 *            the handle of the port to clear
	 */
	private void clearIOBuffers(final HANDLE handle) {
		if (os.PurgeComm(handle, PURGE_RXCLEAR | PURGE_TXCLEAR))
			return;
		throw Throwables.newNativeCodeException("PurgeComm failed!", os.GetLastError());
	}

	/**
	 * Set the event mask to the given handle in order to be notified about received bytes.
	 * 
	 * @param portHandle
	 */
	private void registerRxEvent(HANDLE portHandle) {
		if (os.SetCommMask(portHandle, EV_RXCHAR))
			return;

		throw newNativeCodeException("SetCommMask failed!", os.GetLastError());
	}

	/**
	 * Returns a new {@link IOException} containing the given message and the error code that is
	 * returned by {@link WinApi#GetLastError()}.
	 */
	private IOException lastError(String message) {
		return newIOException(message, os.GetLastError());
	}

	/** {@inheritDoc} */
	@Nonnull
	public String getPortName() {
		return portName;
	}

	/** {@inheritDoc} */
	@Nullable
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return portName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerialPortImpl other = (SerialPortImpl) obj;
		return portName.equals(other.portName);
	}
	
	@Override
	public String toString() {
		return "SerialPortImpl [portName=" + getPortName() + ", description=" + getDescription() + "]";
	}
}