package jp.bo.bocc.system.config;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Namlong on 6/21/2017.
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final static Logger LOGGER = Logger.getLogger(CustomLogoutSuccessHandler.class.getName());

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null && authentication.getDetails() != null) {
            try {
                httpServletRequest.getSession().invalidate();
                LOGGER.info("User Successfully Logout. User - " + authentication.getName());
                //you can add more codes here when the user successfully logs out,
                //such as updating the database for last active.
            } catch (Exception e) {
                LOGGER.error("ERROR: logout " + e.getMessage());
            }
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        //redirect to login
        httpServletResponse.sendRedirect("/backend/login");
    }
}
