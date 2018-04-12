package jp.bo.bocc.system.webconfig;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.service.AdminService;
import jp.bo.bocc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NguyenThuong on 6/12/2017.
 */
public class LoginInterceptor implements HandlerInterceptor {

    private final static Logger LOGGER = Logger.getLogger(LoginInterceptor.class.getName());

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return true;
        }
        String adminEmail = auth.getName();
        ShmAdmin admin = adminService.getAdminByEmail(adminEmail);

        if (StringUtils.isNotEmpty(adminEmail)) {
            if (!adminEmail.equals("anonymousUser") && admin == null) {
                try {
                    if (!httpServletRequest.getPathInfo().contains("login")) {
                        httpServletResponse.sendRedirect("login");
                        return true;
                    }
                } catch (Exception e) {
                    LOGGER.error("ERROR: " + e.getMessage());
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
