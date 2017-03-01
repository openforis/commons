/**
 * 
 */
package org.openforis.commons.web;

import java.util.Map;

/**
 * @author S. Ricci
 * @author M. Togna
 * 
 */
public class Response {

	public enum Status {
		OK, ERROR;
	}

	Status status;
	Object object;
	Map<String, Object> objects;
	String errorMessage;
	
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
