package org.openforis.commons.web;

import org.springframework.beans.BeanUtils;

/**
 * 
 * @author S. Ricci
 *
 */
public class SimpleObjectForm<T extends Object> {

	public SimpleObjectForm() {
	}
	
	public SimpleObjectForm(T obj) {
		initializeFromObject(obj);
	}

	public void initializeFromObject(T obj) {
		try {
			BeanUtils.copyProperties(obj, this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void copyTo(T target, String... ignoreProperties) {
		try {
			BeanUtils.copyProperties(this, target, ignoreProperties);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
