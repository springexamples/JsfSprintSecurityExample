package testing.ss;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;

/**
 * Based on code from DefaultDirectStrategy and
 * https://gist.github.com/banterCZ/5160269
 * 
 * @author mhewedy
 *
 */
public class JsfRedirectStrategy implements RedirectStrategy {

	private static final String FACES_REQUEST_HEADER = "faces-request";

	protected final Log logger = LogFactory.getLog(getClass());

	private boolean contextRelative;

	/**
	 * Redirects the response to the supplied URL.
	 * <p>
	 * If <tt>contextRelative</tt> is set, the redirect value will be the value
	 * after the request context path. Note that this will result in the loss of
	 * protocol information (HTTP or HTTPS), so will cause problems if a
	 * redirect is being performed to change to HTTPS, for example.
	 */
	public void sendRedirect(HttpServletRequest request,
			HttpServletResponse response, String url) throws IOException {
		String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
		redirectUrl = response.encodeRedirectURL(redirectUrl);

		boolean ajaxRedirect = "partial/ajax".equals(request
				.getHeader(FACES_REQUEST_HEADER));
		if (ajaxRedirect) {

			String ajaxRedirectXml = createAjaxRedirectXml(redirectUrl);
			logger.debug("Ajax partial response to redirect: "
					+ ajaxRedirectXml);

			response.setContentType("text/xml");
			response.getWriter().write(ajaxRedirectXml);
		} else {
			logger.debug("Non-ajax redirecting to '" + redirectUrl + "'");
			response.sendRedirect(redirectUrl);
		}
	}

	private String calculateRedirectUrl(String contextPath, String url) {
		if (!UrlUtils.isAbsoluteUrl(url)) {
			if (contextRelative) {
				return url;
			} else {
				return contextPath + url;
			}
		}

		// Full URL, including http(s)://

		if (!contextRelative) {
			return url;
		}

		// Calculate the relative URL from the fully qualified URL, minus the
		// last
		// occurrence of the scheme and base context.
		url = url.substring(url.lastIndexOf("://") + 3); // strip off scheme
		url = url.substring(url.indexOf(contextPath) + contextPath.length());

		if (url.length() > 1 && url.charAt(0) == '/') {
			url = url.substring(1);
		}

		return url;
	}

	/**
	 * If <tt>true</tt>, causes any redirection URLs to be calculated minus the
	 * protocol and context path (defaults to <tt>false</tt>).
	 */
	public void setContextRelative(boolean useRelativeContext) {
		this.contextRelative = useRelativeContext;
	}

	// from https://gist.github.com/banterCZ/5160269
	private String createAjaxRedirectXml(String redirectUrl) {
		return new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
				.append("<partial-response><redirect url=\"")
				.append(redirectUrl)
				.append("\"></redirect></partial-response>").toString();
	}

}
