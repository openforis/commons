package org.openforis.commons.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.validation.ObjectError;

public class ValidationResponse extends Response {

	private List<ObjectError> errors;

	public ValidationResponse(List<ObjectError> errors) {
		objects = new HashMap<String, Object>();
		if (errors != null && !errors.isEmpty()) {
			setStatus(Status.ERROR);
			this.errors = errors;
		} else {
			setStatus(Status.OK);
			this.errors = new ArrayList<ObjectError>();
		}
	}
	
	public List<ObjectError> getErrors() {
		return errors;
	}
	
	void addError(ObjectError objectError) {
		this.errors.add( objectError );
	}

	public void setErrors(List<ObjectError> errors) {
		this.errors = errors;
	}
}
