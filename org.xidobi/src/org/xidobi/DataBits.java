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
package org.xidobi;

/**
 * The number of data bits in each character. This can be:
 * 
 * <ul>
 * <li>5 (for Baudot code)</il>
 * <li>6 (rarely used)</il>
 * <li>7 (for true ASCII)</il>
 * <li>8 (for any kind of data, as this matches the size of a byte)</il>
 * <li>9 (rarely used)</il>
 * </ul>
 * 
 * 8 data bits are almost universally used in newer applications.
 * 
 * @author Tobias Breﬂler
 */
public enum DataBits {

	/** 5 data bits (for Baudot code) */
	DATABITS_5,

	/** 6 data bits (rarely used) */
	DATABITS_6,

	/** 7 data bits (for true ASCII) */
	DATABITS_7,

	/** 8 data bits (for any kind of data, almost universally used in newer applications) */
	DATABITS_8,

	/** 9 data bits (rarely used) */
	DATABITS_9

}
