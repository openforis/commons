package org.openforis.commons.web;



/**
 * 
 * @author S. Ricci
 *
 */
public class PersistedObjectForm<I extends Number, T extends Object> extends SimpleObjectForm<T> {

	private I id;
	
	public PersistedObjectForm() {
	}
	
	public PersistedObjectForm(T obj) {
		super(obj);
	}

	public I getId() {
		return id;
	}

	public void setId(I id) {
		this.id = id;
	}
	
}
