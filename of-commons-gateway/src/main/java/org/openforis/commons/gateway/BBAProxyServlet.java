package org.openforis.commons.gateway;

import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class BBAProxyServlet extends ProxyServlet {

	protected static final String P_LOGIN_URI = "loginUri";

	private String loginUri;

	@Override
	public void init() throws ServletException {
		this.loginUri = getServletConfig().getInitParameter(P_LOGIN_URI);
		super.requestHandler = new BBARequestHeaderHanlder();
		super.responseHandler = new BBAResponseHeaderHandler(loginUri);
		super.init();
	}

}
