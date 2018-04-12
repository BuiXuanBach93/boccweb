package jp.bo.bocc.controller;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Created by Namlong on 3/20/2017.
 */
public class BoccBaseController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    RequestContext requestContext;
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

    /**
     * get message from properties file with dynamic params.
     * @param key
     * @param dynamicParams
     * @return
     */
    protected String getMessage(String key, String... dynamicParams) {
          return messageSource.getMessage(key, dynamicParams, null);
    }

    /**
     * Check user exist in a transaction.
     * @return
     * @throws Exception
     */
    protected ShmUser getUserUsingApp() throws Exception {
        final ShmUser user = requestContext.getUser();
        if (user == null) {
            throw new Exception("SH_E100005");
        }
        return user;
    }

    /**
     * Check user exist in a transaction.
     * @return
     * @throws Exception
     */
    protected Long getUserIdUsingApp() throws Exception {
        final ShmUser user = requestContext.getUser();
        if (user == null) {
            throw new Exception("SH_E100005");
        }
        return user.getId().longValue();
    }

}
