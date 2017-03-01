package org.openforis.commons.gateway;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;

public interface ResponseHeaderHandler {

	public void doHandle(HttpServletRequest req, HttpResponse resp);

}
