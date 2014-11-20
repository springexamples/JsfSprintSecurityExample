package testing.ss;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Based on code from LoginUrlAuthenticationEntryPoint
 * 
 * @author mhewedy
 *
 */
// see http://forum.spring.io/forum/spring-projects/security/88829-is-it-possible-to-change-spring-security-3-redirects-from-full-urls-to-relative-urls
@SuppressWarnings("deprecation")
public class JsfLoginUrlAuthenticationEntryPoint extends
		LoginUrlAuthenticationEntryPoint {

	Log log = LogFactory.getLog(getClass());

	private RedirectStrategy redirectStrategy;

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {

		String redirectUrl = null;

		if (isUseForward()) {

			if (isForceHttps() && "http".equals(request.getScheme())) {
				// First redirect the current request to HTTPS.
				// When that request is received, the forward to the login page
				// will be used.
				redirectUrl = buildHttpsRedirectUrlForRequest(request);
			}

			if (redirectUrl == null) {
				String loginForm = determineUrlToUseForThisRequest(request,
						response, authException);

				log.debug("Server side forward to: " + loginForm);

				RequestDispatcher dispatcher = request
						.getRequestDispatcher(loginForm);

				dispatcher.forward(request, response);

				return;
			}
		} else {
			// redirect to login page. Use https if forceHttps true

			redirectUrl = buildRedirectUrlToLoginPage(request, response,
					authException);
		}
		redirectStrategy.sendRedirect(request, response, redirectUrl);
	}

}
