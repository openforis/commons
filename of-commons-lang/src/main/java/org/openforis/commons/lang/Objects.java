package org.openforis.commons.lang;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * 
 * @author S. Ricci
 *
 */
public class Objects {

	public static <O extends Cloneable> List<O> clone(List<O> list) {
		if (list == null) {
			return null;
		}
		List<O> clone = new ArrayList<O>(list.size());
		for (Object item : list) {
			@SuppressWarnings("unchecked")
			O clonedItem = (O) ObjectUtils.cloneIfPossible(item);
			clone.add(clonedItem);
		}
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	public static <V> V getPropertyValue(Object obj, String propertyName) {
		try {
			Object value = FieldUtils.readField(obj, propertyName, true);
			return (V) value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean deepEquals(DeepComparable o1, DeepComparable o2) {
		return deepEquals(o1, o2, false);
	}
	
	public static boolean deepEquals(DeepComparable o1, DeepComparable o2, boolean ignoreId) {
		if (o1 == o2) {
			return true;
		} else if (o1 == null || o2 == null) {
			return false;
		} else if (o1 instanceof IdentifiableDeepComparable && o2 instanceof IdentifiableDeepComparable) {
			return ((IdentifiableDeepComparable) o1).deepEquals((IdentifiableDeepComparable) o2, ignoreId);
		} else {
			return o1.deepEquals(o2);
		}
	}
	
	public static <T> T newInstance(Class<T> type, Object... parameters) {
		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		for (Object param : parameters) {
			parameterTypes.add(param.getClass());
		}
		try {
			Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes.toArray(new Class<?>[parameterTypes.size()]));
			return constructor.newInstance(parameters);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static <T> T defaultIfNull(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}
}
