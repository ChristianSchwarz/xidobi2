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
 * Parity is a method of detecting errors in transmission. When parity is used with a serial port,
 * an extra data bit is sent with each data character, arranged so that the number of 1 bits in each
 * character, including the parity bit, is always odd or always even. If a byte is received with the
 * wrong number of 1s, then it must have been corrupted. However, an even number of errors can pass
 * the parity check.
 * <p>
 * The parity can be:
 * 
 * <ul>
 * <li>None</li>
 * <li>Odd</li>
 * <li>Even</li>
 * <li>Mark</li>
 * </ul>
 * 
 * @author Tobias Breﬂler
 */
public enum Parity {

	/** no parity bit is sent at all */
	Parity_None,

	/** ensures that at least one state transition occurs in each character */
	Parity_Odd,

	/** TODO describe value */
	Parity_Even,

	/** the parity bit is always set to the mark signal condition (logical 1) */
	Parity_Mark,

	/** space parity always sends the parity bit in the space signal condition */
	Parity_Space

}
