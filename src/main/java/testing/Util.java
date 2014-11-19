package testing;

import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class Util {

	static Log log = LogFactory.getLog(Util.class);

	public static void addMessage(String msg) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(msg));
	}

	public static String getParameter(String param) {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		if (request != null) {
			return request.getParameter(param);
		}
		return null;
	}

	public static String getSavedUrl() {
		HttpServletRequest request = ((HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest());

		SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(
				request, (HttpServletResponse) FacesContext
						.getCurrentInstance().getExternalContext()
						.getResponse());

		if (savedRequest != null) {
			try {
				URL url = new URL(savedRequest.getRedirectUrl());
				return url.getFile().substring(
						request.getContextPath().length());
			} catch (Exception e) {
				log.error(e.getMessage() + " Using default URL");
			}
		}
		return "admin/index.xhtml?faces-redirect=true";
	}
}
