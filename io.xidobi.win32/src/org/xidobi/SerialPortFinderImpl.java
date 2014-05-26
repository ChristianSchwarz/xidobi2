package org.xidobi;

import static com.sun.jna.Native.getLastError;
import static com.sun.jna.platform.win32.Advapi32Util.registryGetStringValue;
import static com.sun.jna.platform.win32.Kernel32Util.formatMessageFromLastErrorCode;
import static com.sun.jna.platform.win32.SetupApi.DICS_FLAG_GLOBAL;
import static com.sun.jna.platform.win32.SetupApi.GUID_DEVINTERFACE_COMPORT;
import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;
import static com.sun.jna.platform.win32.WinNT.KEY_QUERY_VALUE;
import static com.sun.jna.platform.win32.WinNT.REG_SZ;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.SetupApi;

import static com.sun.jna.platform.win32.SetupApi.*;

import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.IntByReference;

public class SerialPortFinderImpl implements SerialPortFinder {

	private final static SetupApi setupApi = SetupApi.INSTANCE;
	private final static Advapi32 advapi32 = Advapi32.INSTANCE;

	private final Map<String,SerialPort> ports = new WeakHashMap<String, SerialPort>();
	
	@Override
	public SerialPort get(String portName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<SerialPort> getAll() {
		//PortName -> Description
		Map<String, String> portDescs = readPortnamesAndDescription();
		
		
		for(Entry<String,String> portDesc : portDescs.entrySet()){
			String portName = portDesc.getValue();
			ports.containsKey(portName);
		}
		System.out.println(ports);
//		return null;

	}

	private Map<String, String> readPortnamesAndDescription() {
		Map<String, String> ports = new HashMap();

		HANDLE hDevInfoSet = getComPortInfoSetHandle();
		try {
			// Finally do the enumeration

			int index = 0;
			while (true) {
				// Enumerate the current device

				SP_DEVINFO_DATA devInfo = getDeviceInfoData(hDevInfoSet, index++);
				if (devInfo == null)
					break;

				String portName = getPortName(hDevInfoSet, devInfo);
				if (portName == null)
					continue;
				String description = getDescription(hDevInfoSet, devInfo);
				ports.put(portName, description);

			}
		} finally {
			setupApi.SetupDiDestroyDeviceInfoList(hDevInfoSet);
		}
		return ports;
	}

	private static SP_DEVINFO_DATA getDeviceInfoData(HANDLE hDevInfoSet, int index) {

		SP_DEVINFO_DATA devInfo = new SP_DEVINFO_DATA();
		boolean succeed = setupApi.SetupDiEnumDeviceInfo(hDevInfoSet, index, devInfo);
		if (succeed)

			return devInfo;
		return null;
	}

	private static HANDLE getComPortInfoSetHandle() {
		// Now create a "device information set" which is required to enumerate
		// all the ports
		HANDLE hDevInfoSet = setupApi.SetupDiGetClassDevs(GUID_DEVINTERFACE_COMPORT, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);
		if (hDevInfoSet == INVALID_HANDLE_VALUE) {
			int err = getLastError();
			throw new LastErrorException(err + "(0x" + Integer.toHexString(err) + ") " + formatMessageFromLastErrorCode(err));
		}
		return hDevInfoSet;
	}

	private static String getPortName(HANDLE hDevInfoSet, SP_DEVINFO_DATA devInfo) {
		// Get the registry key which stores the ports settings
		HKEY hDeviceKey = setupApi.SetupDiOpenDevRegKey(hDevInfoSet, devInfo, DICS_FLAG_GLOBAL, 0, SetupApi.DIREG_DEV, KEY_QUERY_VALUE);
		if (hDeviceKey == INVALID_HANDLE_VALUE)
			return null;

		try {
			return registryGetStringValue(hDeviceKey, "PortName");

		} finally {
			advapi32.RegCloseKey(hDeviceKey);
		}
	}

	private static String getDescription(HANDLE hDevInfoSet, SP_DEVINFO_DATA devInfo) {
		int descSize = 1024;
		Memory desc = new Memory(descSize);

		IntByReference requiredSize = new IntByReference();
		IntByReference type = new IntByReference();
		boolean success = setupApi.SetupDiGetDeviceRegistryProperty(hDevInfoSet, devInfo, SPDRP_DEVICEDESC, type, desc, descSize, requiredSize);
		if (!success)
			return null;
		if (type.getValue() != REG_SZ)
			return null;

		char[] bytes = desc.getCharArray(0, requiredSize.getValue());
		return new String(bytes).trim();

	}

	


}
