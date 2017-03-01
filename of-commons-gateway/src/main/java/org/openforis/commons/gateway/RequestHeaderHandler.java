package org.openforis.commons.gateway;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;

public interface RequestHeaderHandler {

	public void doHandle(HttpServletRequest req1, HttpRequest req2);

}
