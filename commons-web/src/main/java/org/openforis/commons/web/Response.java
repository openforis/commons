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
	private Object object;
	private Map<String, Object> objects;
	private String errorMessage;
	private List<ObjectError> errors;
	
	public Response() {
		this(null);
	}

	public Response(List<ObjectError> errors) {
		objects = new HashMap<String, Object>();
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
	
	public void setOkStatus() {
		setStatus(Status.OK);
	}
	
	public boolean isStatusError() {
		return status == Status.ERROR;
	}
	
	public void setErrorStatus() {
		setStatus(Status.ERROR);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public Map<String, Object> getObjects() {
		return objects;
	}
	
	public void addObject(String key, Object object){
		this.objects.put(key, object);
	}
	
}
