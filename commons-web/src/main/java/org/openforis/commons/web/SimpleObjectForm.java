package org.openforis.commons.web;

import org.apache.commons.beanutils.PropertyUtils;

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
			PropertyUtils.copyProperties(this, obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void copyTo(T obj) {
		try {
			PropertyUtils.copyProperties(obj, this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
