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
package org.xidobi.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

import org.xidobi.OS;

/**
 * Provides access to the windows registry.
 * 
 * @author Tobias Breﬂler
 */
public class Registry {

	/** The handle to the registry key LOCAL_MACHINE. */
	public static final int HKEY_LOCAL_MACHINE = 0x80000002;

	/** Security mask that grants access for reading keys. */
	public static final int KEY_READ = 0x20019;

	/** System root node. */
	private static Preferences systemRoot = Preferences.systemRoot();

	/** <code>WindowsRegOpenKey(int, btye[], int) : int[]</code> */
	private static Method _RegOpenKey;
	/** <code>WindowsRegCloseKey(int) : int</code> */
	private static Method _RegCloseKey;
	/** <code>WindowsRegEnumValue(int, int, int) : byte[]</code> */
	private static Method _RegEnumValue;

	static {
		Class<? extends Preferences> clazz = systemRoot.getClass();
		try {
			_RegOpenKey = clazz.getDeclaredMethod("WindowsRegOpenKey", new Class[] { int.class, byte[].class, int.class });
			_RegOpenKey.setAccessible(true);

			_RegCloseKey = clazz.getDeclaredMethod("WindowsRegCloseKey", new Class[] { int.class });
			_RegCloseKey.setAccessible(true);

			_RegEnumValue = clazz.getDeclaredMethod("WindowsRegEnumValue", new Class[] { int.class, int.class, int.class });
			_RegEnumValue.setAccessible(true);
		}
		catch (NoSuchMethodException e) {}
		catch (SecurityException e) {}
	}

	/**
	 * Opens the specified registry key. Note that key names are not case sensitive.
	 * 
	 * @param hkey
	 *            A handle to an open registry key.
	 * @param subKey
	 *            The name of the registry key to be opened. This key must be a subkey of the key
	 *            identified by the hKey parameter. Key names are not case sensitive. If this
	 *            parameter is NULL or a pointer to an empty string, the function returns the same
	 *            handle that was passed in.
	 * @param securityMask
	 *            A mask that specifies the desired access rights to the key to be opened. The
	 *            function fails if the security descriptor of the key does not permit the requested
	 *            access for the calling process. See {@link #KEY_READ}.
	 * @return
	 * 
	 * @throws IllegalAccessException
	 *             if this Method object is enforcing Java language access control and the
	 *             underlying method is inaccessible.
	 * @throws IllegalArgumentException
	 *             if the method is an instance method and the specified object argument is not an
	 *             instance of the class or interface declaring the underlying method (or of a
	 *             subclass or implementor thereof); if the number of actual and formal parameters
	 *             differ; if an unwrapping conversion for primitive arguments fails; or if, after
	 *             possible unwrapping, a parameter value cannot be converted to the corresponding
	 *             formal parameter type by a method invocation conversion.
	 * @throws InvocationTargetException
	 *             if the underlying method throws an exception.
	 */
	public static int[] RegOpenKey(int hkey, byte[] subKey, int securityMask) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (int[]) _RegOpenKey.invoke(systemRoot, hkey, subKey, securityMask);
	}

	/**
	 * Closes a handle to the specified registry key.
	 * 
	 * @param hkey
	 *            A handle to the open key to be closed. The handle must have been opened by the
	 *            RegCreateKeyEx, RegCreateKeyTransacted, RegOpenKeyEx, RegOpenKeyTransacted, or
	 *            RegConnectRegistry function.
	 * @return If the function succeeds, the return value is {@link OS#ERROR_SUCCESS}. If the
	 *         function fails, the return value is a nonzero error code.
	 * 
	 * @throws IllegalAccessException
	 *             if this Method object is enforcing Java language access control and the
	 *             underlying method is inaccessible.
	 * @throws IllegalArgumentException
	 *             if the method is an instance method and the specified object argument is not an
	 *             instance of the class or interface declaring the underlying method (or of a
	 *             subclass or implementor thereof); if the number of actual and formal parameters
	 *             differ; if an unwrapping conversion for primitive arguments fails; or if, after
	 *             possible unwrapping, a parameter value cannot be converted to the corresponding
	 *             formal parameter type by a method invocation conversion.
	 * @throws InvocationTargetException
	 *             if the underlying method throws an exception.
	 */
	public static int RegCloseKey(int hkey) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Integer) _RegCloseKey.invoke(systemRoot, hkey);
	}

	/**
	 * Enumerates the values for the specified open registry key. The function copies one indexed
	 * value name and data block for the key each time it is called.
	 * 
	 * @param hkey
	 *            A handle to an open registry key. The key must have been opened with the
	 *            KEY_QUERY_VALUE access right.
	 * @param valueIndex
	 *            The index of the value to be retrieved. This parameter should be zero for the
	 *            first call to the RegEnumValue function and then be incremented for subsequent
	 *            calls. Because values are not ordered, any new value will have an arbitrary index.
	 *            This means that the function may return values in any order.
	 * @param maxValueNameLength
	 *            A variable that specifies the size of the buffer pointed to by the lpValueName
	 *            parameter, in characters. When the function returns, the variable receives the
	 *            number of characters stored in the buffer, not including the terminating null
	 *            character.
	 * @return The data for the value entry.
	 * 
	 * @throws IllegalAccessException
	 *             if this Method object is enforcing Java language access control and the
	 *             underlying method is inaccessible.
	 * @throws IllegalArgumentException
	 *             if the method is an instance method and the specified object argument is not an
	 *             instance of the class or interface declaring the underlying method (or of a
	 *             subclass or implementor thereof); if the number of actual and formal parameters
	 *             differ; if an unwrapping conversion for primitive arguments fails; or if, after
	 *             possible unwrapping, a parameter value cannot be converted to the corresponding
	 *             formal parameter type by a method invocation conversion.
	 * @throws InvocationTargetException
	 *             if the underlying method throws an exception.
	 */
	public static byte[] RegEnumValue(int hkey, int valueIndex, int maxValueNameLength) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (byte[]) _RegEnumValue.invoke(systemRoot, hkey, valueIndex, maxValueNameLength);
	}

}
