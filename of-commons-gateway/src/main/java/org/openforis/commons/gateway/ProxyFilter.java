package org.openforis.commons.gateway;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyFilter implements Filter {

	protected static final String P_MODULE_CONTAINER_PATH = "moduleContainerPath";
	protected static final String P_PROXY_SHARED_KEY_NAME = "proxySharedKeyName";
	protected static final String P_PROXY_SHARED_KEY_VALUE = "proxySharedKeyValue";

	private String moduleContainerPath;
	private String proxySharedKeyName;
	private String proxySharedKeyValue;

	public void init(FilterConfig config) throws ServletException {
		this.moduleContainerPath = config.getInitParameter(P_MODULE_CONTAINER_PATH);
		this.proxySharedKeyName = config.getInitParameter(P_PROXY_SHARED_KEY_NAME);
		this.proxySharedKeyValue = config.getInitParameter(P_PROXY_SHARED_KEY_VALUE);
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) req;
		HttpServletResponse servletResponse = (HttpServletResponse) res;
		String requestURI = servletRequest.getRequestURI();
		String newRequestURI = requestURI.replaceFirst(this.moduleContainerPath + "/", "");
		String value = servletRequest.getHeader(this.proxySharedKeyName);
		if (requestURI.contains(this.moduleContainerPath)) {
			if (value != null) {
				if (value.equals(this.proxySharedKeyValue)) {
					chain.doFilter(req, res);
				} else {
					chain.doFilter(req, res);
				}
			} else {
				servletResponse.sendRedirect(newRequestURI);
			}
		} else {
			chain.doFilter(req, res);
		}
	}

	public void destroy() {
	}

}