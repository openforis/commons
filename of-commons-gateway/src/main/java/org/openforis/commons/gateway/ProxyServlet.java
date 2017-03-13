package org.openforis.commons.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

@SuppressWarnings("serial")
public class ProxyServlet extends HttpServlet {

	protected static final String P_MODULE_CONTAINER_PATH = "moduleContainerPath";
	protected static final String P_PROXY_SHARED_KEY_NAME = "proxySharedKeyName";
	protected static final String P_PROXY_SHARED_KEY_VALUE = "proxySharedKeyValue";

	private static final String METHOD_DELETE = "DELETE";
	private static final String METHOD_HEAD = "HEAD";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_OPTIONS = "OPTIONS";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_TRACE = "TRACE";

	private String moduleContainerPath;
	private String proxySharedKeyName;
	private String proxySharedKeyValue;
	private HttpClient proxyClient;

	protected RequestHeaderHandler requestHandler;
	protected ResponseHeaderHandler responseHandler;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.moduleContainerPath = config.getInitParameter(P_MODULE_CONTAINER_PATH);
		this.proxySharedKeyName = config.getInitParameter(P_PROXY_SHARED_KEY_NAME);
		this.proxySharedKeyValue = config.getInitParameter(P_PROXY_SHARED_KEY_VALUE);
		this.proxyClient = HttpClients.createDefault();
	}

	@Override
	protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
		InputStream requestInput = null;
		InputStream responseInput = null;
		OutputStream responseOutput = null;
		try {
			String requestURI = servletRequest.getRequestURI();
			if (!requestURI.equals("/")) {
				if (requestURI.length() > 1 && !requestURI.substring(1).contains("/")) {
					if (!requestURI.substring(1).contains(".")) {
						servletResponse.sendRedirect(requestURI += "/");
					}
				} else {
					URI uri = this.getURI(servletRequest);
					HttpResponse proxyResponse = null;
					String method = servletRequest.getMethod();
					HttpHost httpHost = URIUtils.extractHost(uri);
					HttpEntityEnclosingRequest proxyRequest = new BasicHttpEntityEnclosingRequest(method, uri.toString());
					if (requestHandler != null) {
						requestHandler.doHandle(servletRequest, proxyRequest);
					}
					this.copyRequestHeader(servletRequest, proxyRequest);
					this.copyRequestBody(servletRequest, proxyRequest);
					proxyResponse = this.proxyClient.execute(httpHost, proxyRequest);
					if (proxyResponse != null) {
						int statusCode = proxyResponse.getStatusLine().getStatusCode();
						if (statusCode == 304) {
							this.copyResponseHeader(proxyResponse, servletResponse);
							servletResponse.setStatus(304);
							servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
						} else {
							if (responseHandler != null) {
								responseHandler.doHandle(servletRequest, proxyResponse);
							}
							this.copyResponseHeader(proxyResponse, servletResponse);
							HttpEntity responseEntity = proxyResponse.getEntity();
							responseInput = responseEntity.getContent();
							responseOutput = servletResponse.getOutputStream();
							IOUtils.copy(responseInput, responseOutput);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (requestInput != null) responseInput.close();
			if (responseInput != null) responseInput.close();
			if (responseOutput != null) responseInput.close();
		}
	}

	private URI getURI(HttpServletRequest servletRequest) throws URISyntaxException {
		URIBuilder builder = new URIBuilder();
		builder.setScheme(servletRequest.getScheme()).setHost(servletRequest.getServerName()).setPort(servletRequest.getServerPort());
		builder.setPath(this.moduleContainerPath + servletRequest.getRequestURI());
		String method = servletRequest.getMethod();
		if (method.equals(METHOD_GET)) {
			Enumeration<String> parameterNames = servletRequest.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String key = (String) parameterNames.nextElement();
				String value = servletRequest.getParameter(key);
				builder.addParameter(key, value);
			}
		}
		return builder.build();
	}

	private void copyRequestHeader(HttpServletRequest servletRequest, HttpRequest proxyRequest) {
		Enumeration<String> headerNames = servletRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			if (!key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
				String value = servletRequest.getHeader(key);
				proxyRequest.setHeader(key, value);
			}
		}
		proxyRequest.setHeader(this.proxySharedKeyName, this.proxySharedKeyValue);
	}

	private void copyRequestBody(HttpServletRequest servletRequest, HttpEntityEnclosingRequest proxyRequest) throws NumberFormatException, IOException {
		String contentLength = servletRequest.getHeader(HttpHeaders.CONTENT_LENGTH);
		if (contentLength == null) {
			contentLength = "-1";
		}
		HttpEntity requestEntity = new InputStreamEntity(servletRequest.getInputStream(), Long.parseLong(contentLength));
		proxyRequest.setEntity(requestEntity);
	}

	private void copyResponseHeader(HttpResponse proxyResponse, HttpServletResponse servletResponse) {
		Header[] headers = proxyResponse.getAllHeaders();
		for (Header header: headers) {
			String key = (String) header.getName();
			String value = header.getValue();
			servletResponse.setHeader(key, value);
		}
	}

}
