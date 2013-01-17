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
 * Stop bits sent at the end of every character allow the receiving signal hardware to detect the
 * end of a character and to resynchronise with the character stream. This can be:
 * 
 * <ul>
 * <li>1 (usually all electronic devices)
 * <li>1.5</li>
 * <li>2</li>
 * </ul>
 * 
 * Electronic devices usually use one stop bit.
 * 
 * @author Tobias Breﬂler
 */
public enum StopBits {

	/** one stop bit */
	StopBits_1,

	/** one-and-one half stop bits */
	StopBits_1_5,

	/** two stop bits */
	StopBits_2

}
