package jp.bo.bocc.system.apiconfig;

import jp.bo.bocc.system.config.Profiles;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Web application intializer for API
 * @author manhnt
 */
public class ApiWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected String getServletName() {
		return "ApiDispatcherServlet";
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { ApiConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/api/v1/*" };
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.setInitParameter("spring.profiles.active", Profiles.WEB);
	}
}