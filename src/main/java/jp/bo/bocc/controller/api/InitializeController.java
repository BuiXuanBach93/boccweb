package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.controller.api.response.InitResponse;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Namlong on 4/24/2017.
 */
@RestController
public class InitializeController extends BoccBaseController{


    /**
     * Implement some data for splash screen.
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/init")
    @AccessTokenAuthentication
    public InitResponse initializeApp() throws Exception {
        InitResponse result = new InitResponse();
        return result;
    }
}
