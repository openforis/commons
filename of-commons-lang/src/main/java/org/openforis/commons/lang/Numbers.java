package org.openforis.commons.lang;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author S. Ricci
 *
 */
public class Numbers {
	

	public static boolean isNumber(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Number) {
			return true;
		}
		if (value instanceof String) {
			try {
				Double.parseDouble((String) value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
			
		}
		return false;
	}

	public static int toInt(Object value) {
		return toInt(value, 0);
	}
	
	public static int toInt(Object value, int defaultValue) {
		Integer result = toIntegerObject(value);
		return result == null ? defaultValue : result;
	}
	
	public static Integer toIntegerObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		if (value instanceof String) {
			if (StringUtils.isBlank((String) value)) {
				return null;
			}
			try {
				return Integer.parseInt((String) value);
			} catch(NumberFormatException e) {
				Double doubleVal = toDoubleObject(value);
				if (doubleVal == null || doubleVal % 1 != 0) {
					return null;
				} else {
					return Integer.valueOf(doubleVal.intValue());
				}
			}
		}
		return null;
	}

	public static double toDouble(Object value) {
		return toDouble(value, 0);
	}
	
	public static double toDouble(Object value, double defaultValue) {
		Double result = toDoubleObject(value);
		return result == null ? defaultValue : result;
	}
	
	public static Double toDoubleObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		if (value instanceof String) {
			if (StringUtils.isBlank((String) value)) {
				return null;
			}
			try {
				return Double.parseDouble((String) value);
			} catch(NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	public static long toLong(Object value) {
		return toInt(value, 0);
	}
	
	public static long toLong(Object value, long defaultValue) {
		Long result = toLongObject(value);
		return result == null ? defaultValue : result;
	}
	
	public static Long toLongObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		if (value instanceof String) {
			if (StringUtils.isBlank((String) value)) {
				return null;
			}
			try {
				return Long.parseLong((String) value);
			} catch(NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
}
