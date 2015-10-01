package org.openforis.commons.web;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author S. Ricci
 *
 */
public class HttpResponses {

	public static void setNoContentStatus(HttpServletResponse response) {
		response.setStatus(SC_NO_CONTENT);
	}
}
