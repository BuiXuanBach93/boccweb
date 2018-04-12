package jp.bo.bocc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NguyenThuong on 4/7/2017.
 */
public class BoccBaseWebController {

    @Autowired
    MessageSource messageSource;

    @Value("${page.size}")
    protected int pageSize;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    HttpServletResponse httpServletResponse;

    /**
     * Create page: 100 items/page.
     *
     * @param currentIndex
     * @param sizePerPage
     * @return
     */
    protected Pageable createPage(int currentIndex, int sizePerPage) {
        return new PageRequest(currentIndex, sizePerPage);
    }

    protected Pageable createPage100Item(int currentIndex) {
        return new PageRequest(currentIndex, pageSize);
    }

    /**
     * get message from properties file with dynamic params.
     * @param key
     * @param dynamicParams
     * @return
     */
    protected String getMessage(String key, String... dynamicParams) {
        return messageSource.getMessage(key, dynamicParams, null);
    }

    protected String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    protected  String getEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
