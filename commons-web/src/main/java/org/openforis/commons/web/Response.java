/**
 * 
 */
package org.openforis.commons.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.ObjectError;

/**
 * @author S. Ricci
 * @author M. Togna
 * 
 */
public class Response {

	public enum Status {
		OK, ERROR;
	}

	private Status status;
	private List<ObjectError> errors;
	private Map<String, Object> fields;
	
	public Response() {
		this(null);
	}

	public Response(List<ObjectError> errors) {
		fields = new HashMap<String, Object>();
		if (errors != null && !errors.isEmpty()) {
			setStatus(Status.ERROR);
			this.errors = errors;
		} else {
			setStatus(Status.OK);
			this.errors = new ArrayList<ObjectError>();
		}
	}

	public Status getStatus() {
		return status;
	}

	void setStatus(Status status) {
		this.status = status;
	}

	public boolean isStatusOk() {
		return status == Status.OK;
	}
	
	void setOkStatus() {
		setStatus(Status.OK);
	}
	
	void setErrorStatus() {
		setStatus(Status.ERROR);
	}
	
	public List<ObjectError> getErrors() {
		return errors;
	}
	
	void addError(ObjectError objectError) {
		this.errors.add( objectError );
	}

	public boolean hasErrors() {
		return status == Status.ERROR;
	}

	public Map<String, Object> getFields() {
		return fields;
	}
	
	public void addField(String name, Object object){
		this.fields.put(name, object);
	}
	
}
