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

/**
 * Provides access to the windows registry.
 * 
 * @author Tobias Breﬂler
 */
public class Registry {
	
	  public static final int HKEY_CURRENT_USER = 0x80000001;
	  public static final int HKEY_LOCAL_MACHINE = 0x80000002;
	  
	  public static final int ERROR_SUCCESS = 0;
	  public static final int ERROR_FILE_NOT_FOUND = 2;
	  public static final int ERROR_ACCESS_DENIED = 5;

	  private static final int KEY_ALL_ACCESS = 0xf003f;
	  private static final int KEY_READ = 0x20019;
	  
}
