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
	
}
