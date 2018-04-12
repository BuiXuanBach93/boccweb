package jp.bo.bocc.controller.api;

import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.entity.ShtUserNtf;
import jp.bo.bocc.service.BannerService;
import jp.bo.bocc.service.CategorySettingService;
import jp.bo.bocc.service.UserNtfService;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import jp.bo.bocc.system.apiconfig.resolver.JsonArg;
import jp.bo.bocc.system.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by DonBach on 11/28/2017.
 */
@RestController
public class BannerController {
    @Autowired
    BannerService service;

    @Autowired
    UserNtfService userNtfService;

    @RequestMapping(value = "banners", method = RequestMethod.GET)
    @NoAuthentication
    @ResponseBody
    public ResponseEntity<List<ShtBanner>> getBanners(){
        ResponseEntity<List<ShtBanner>> responseEntity = null;
        List<ShtBanner> list = service.getActiveBanner();
        return new ResponseEntity<List<ShtBanner>>(list, HttpStatus.OK);
    }

    @PostMapping(value = "create-user-ntf")
    @NoAuthentication
    @ResponseBody
    public ResponseEntity<List<ShtUserNtf>> createUserNtf(@JsonArg String password){
        ResponseEntity<List<ShtUserNtf>> responseEntity = null;
        if(StringUtils.isEmpty(password) || !password.equals("Kd7keQPs9u")){
            throw new BadRequestException("Invalid password!");
        }
        for(int i = 0; i < 1000; i ++){
            userNtfService.createSamepleUseNtf();
        }
        return null;
    }

//    @RequestMapping(value = "banner-page", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
//    @ResponseBody
//    @NoAuthentication
//    public String getBannerPage(){
//        ResponseEntity<List<ShtBanner>> responseEntity = null;
//        List<ShtBanner> list = service.getActiveBanner();
//        return "<div>abc</div>";
//    }

}
