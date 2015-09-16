package org.openforis.commons.web;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author S. Ricci
 *
 */
public class Forms {

	public static <T, F extends SimpleObjectForm<T>> List<F> toForms(List<T> items, Class<F> formType) {
		List<F> forms = new ArrayList<F>(items.size());
		for (T item : items) {
			forms.add(toForm(item, formType));
		}
		return forms;
	}
	
	public static <T, F extends SimpleObjectForm<T>> F toForm(T item, Class<F> formType) {
		try {
			Constructor<F> constructor = formType.getDeclaredConstructor(item.getClass());
			F form = constructor.newInstance(item);
			return form;
		} catch (Exception e) {
			throw new RuntimeException("Error creating form objects: " + e.getMessage(), e);
		}
	}
	
}
