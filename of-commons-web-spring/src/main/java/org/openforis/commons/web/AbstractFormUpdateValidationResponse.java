package org.openforis.commons.web;

import java.util.List;

import org.springframework.validation.ObjectError;


/**
 * 
 * @author S. Ricci
 *
 */
public abstract class AbstractFormUpdateValidationResponse<F extends SimpleObjectForm<?>> extends ValidationResponse {

	private F form;
	
	public AbstractFormUpdateValidationResponse(List<ObjectError> errors) {
		this(null, errors);
	}

	public AbstractFormUpdateValidationResponse(F form) {
		this(form, null);
	}

	public AbstractFormUpdateValidationResponse(F form, List<ObjectError> errors) {
		super(errors);
		this.form = form;
	}

	public F getForm() {
		return form;
	}
	
}
