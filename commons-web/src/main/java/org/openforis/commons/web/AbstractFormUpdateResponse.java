package org.openforis.commons.web;

import java.util.List;

import org.springframework.validation.ObjectError;


/**
 * 
 * @author S. Ricci
 *
 */
public abstract class AbstractFormUpdateResponse<F extends PersistedObjectForm<?>> extends Response {

	private F form;
	
	public AbstractFormUpdateResponse(List<ObjectError> errors) {
		this(null, errors);
	}

	public AbstractFormUpdateResponse(F form) {
		this(form, null);
	}

	public AbstractFormUpdateResponse(F form, List<ObjectError> errors) {
		super(errors);
		this.form = form;
	}

	public F getForm() {
		return form;
	}
	
}
