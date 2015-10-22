package org.openforis.commons.collection;

import java.lang.reflect.Array;

/**
 * 
 * @author S. Ricci
 *
 */
public class ArrayUtils {

	public static <T extends Object> T[] join(Class<T> type, T[]... arrays) {
		int totalLenght = 0;
		for (T[] arr : arrays) {
			totalLenght += arr.length;
		}
		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(type, totalLenght);
		int currPos = 0;
		for (T[] arr : arrays) {
			System.arraycopy(arr, 0, result, currPos, arr.length);
			currPos += arr.length;
		}
		return result;
	}
	
}
