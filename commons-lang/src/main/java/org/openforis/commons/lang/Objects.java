package org.openforis.commons.lang;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 
 * @author S. Ricci
 *
 */
public class Objects {

	public static <O extends Cloneable> List<O> clone(List<O> list) {
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
		Method readMethod;
		try {
			readMethod = new PropertyDescriptor(propertyName, obj.getClass()).getReadMethod();
			Object value = readMethod.invoke(obj);
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
	
}
