package org.openforis.commons.web;


/**
 * 
 * @author S. Ricci
 *
 */
public class PersistedObjectForm<T extends Object> extends SimpleObjectForm<T> {

	private Integer id;
	
	public PersistedObjectForm() {
	}
	
	public PersistedObjectForm(T obj) {
		super(obj);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
