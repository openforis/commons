package org.openforis.commons.gateway;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

public class BBAResponseHeaderHandler implements ResponseHeaderHandler {

	private String loginUri;

	public BBAResponseHeaderHandler(String loginUri) {
		this.loginUri = loginUri;
	}


	public void doHandle(HttpServletRequest req, HttpResponse resp) {
		String requestURI = req.getRequestURI();
		if (requestURI.equals(this.loginUri)) {
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String baa = this.generateBAA(req);
				req.getSession().setAttribute("baa", baa);
			}
		}
	}

	private String generateBAA(HttpServletRequest servletRequest) {
		String username = servletRequest.getParameter("username");
		String password = servletRequest.getParameter("password");
		byte[] encodedBytes = Base64.encodeBase64(String.format("%s:%s", username, password).getBytes());
		return "Basic " + new String(encodedBytes);
	}

}
