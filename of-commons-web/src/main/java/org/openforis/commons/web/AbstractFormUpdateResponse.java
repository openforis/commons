package org.openforis.commons.web;

/**
 * 
 * @author S. Ricci
 *
 */
public abstract class AbstractFormUpdateResponse<F extends SimpleObjectForm<?>> extends Response {

	private F form;
	
	public AbstractFormUpdateResponse(F form) {
		this.form = form;
	}

	public F getForm() {
		return form;
	}
	
}
