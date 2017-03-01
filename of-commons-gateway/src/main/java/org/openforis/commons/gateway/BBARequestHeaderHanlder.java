package org.openforis.commons.gateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;

public class BBARequestHeaderHanlder implements RequestHeaderHandler {

	public void doHandle(HttpServletRequest req1, HttpRequest req2) {
		HttpSession session = req1.getSession();
		Object baa = session.getAttribute("baa");
		if (baa != null) {
			req2.setHeader(HttpHeaders.AUTHORIZATION, session.getAttribute("baa").toString());
		}
	}

}
