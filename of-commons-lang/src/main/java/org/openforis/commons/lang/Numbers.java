package org.openforis.commons.lang;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author S. Ricci
 *
 */
public class Numbers {

	public static int toInt(String value) {
		return toInt(value, 0);
	}
	
	public static int toInt(String value, int defaultValue) {
		Integer result = toIntegerObject(value);
		return result == null ? defaultValue : result;
	}
	
	public static Integer toIntegerObject(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			Double doubleVal = toDoubleObject(value);
			if (doubleVal == null || doubleVal % 1 != 0) {
				return null;
			} else {
				return Integer.valueOf(doubleVal.intValue());
			}
		}
	}

	public static double toDouble(String value) {
		return toDouble(value, 0);
	}
	
	public static double toDouble(String value, double defaultValue) {
		Double result = toDoubleObject(value);
		return result == null ? defaultValue : result;
	}
	
	public static Double toDoubleObject(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return Double.parseDouble(value);
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	public static long toLong(String value) {
		return toInt(value, 0);
	}
	
	public static long toLong(String value, long defaultValue) {
		Long result = toLongObject(value);
		return result == null ? defaultValue : result;
	}
	
	public static Long toLongObject(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return Long.parseLong(value);
		} catch(NumberFormatException e) {
			return null;
		}
	}
}
