package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.service.FireBaseService;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Namlong on 8/3/2017.
 */
@RestController
public class FirebaseController extends BoccBaseController {

//    @Autowired
//    FireBaseService fireBaseService;
//
//    /**
//     * Implement some data for splash screen.
//     * @return
//     * @throws Exception
//     */
//    @GetMapping(value = "/token/dbCustomToken")
//    @AccessTokenAuthentication
//    public String createAccessTokenFirebaseDB() throws Exception {
//        Long userId = getUserIdUsingApp();
//        fireBaseService.createCustomToken(userId);
//        final String result = fireBaseService.getTokenForClient();
//        return result;
//    }
}
