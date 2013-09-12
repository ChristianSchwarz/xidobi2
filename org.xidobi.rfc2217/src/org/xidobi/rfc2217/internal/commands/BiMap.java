/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 12.09.2013 10:49:48
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal.commands;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Used by {@link AbstractControlCmd} implementations to map xidobi and rfc2217 bidirectional
 * @author Christian Schwarz
 *
 */
public class BiMap<T_Xidobi, T_Rfc2217> {

	private Map<T_Xidobi, T_Rfc2217> mapXidobiToRfc2217 = new HashMap<T_Xidobi, T_Rfc2217>();
	private Map<T_Rfc2217, T_Xidobi> mapRfc2217ToXidobi = new HashMap<T_Rfc2217, T_Xidobi>();
	/**
	 * Adds a mapping of an equivalen xidobi-RFC2217 value pair. 
	 * @param xidobiValue
	 * @param rfc2217Value
	 */
	final void addEquivalents(@Nullable T_Xidobi xidobiValue,@Nullable T_Rfc2217 rfc2217Value){
		mapXidobiToRfc2217.put(xidobiValue, rfc2217Value);
		mapRfc2217ToXidobi.put(rfc2217Value, xidobiValue);
	}
	
	/** Returns the equivalent xidobi value of the given RFC2217 value, or null	 */
	final T_Xidobi getXidobiEquivalent(T_Rfc2217 value){
		return mapRfc2217ToXidobi.get(value);
	}
	
	/** Returns the equivalent RFC2217 value of the given xidobi value, or null	 */
	final T_Rfc2217 getRfc2217Equivalent(T_Xidobi value){
		return mapXidobiToRfc2217.get(value);
	}
}
