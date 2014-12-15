package org.openforis.commons.web;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * 
 * @author S. Ricci
 *
 */
public class PersistedObjectForm<T extends Object> {

	private Integer id;
	
	public PersistedObjectForm() {
	}
	
	public PersistedObjectForm(T obj) {
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
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
