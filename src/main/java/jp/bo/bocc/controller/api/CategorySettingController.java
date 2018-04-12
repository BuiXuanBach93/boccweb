package jp.bo.bocc.controller.api;

import jp.bo.bocc.entity.ShmCategory;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.service.CategoryService;
import jp.bo.bocc.service.CategorySettingService;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by DonBach on 11/28/2017.
 */
@RestController
public class CategorySettingController {
    @Autowired
    CategorySettingService service;

    @GetMapping("/category-setting")
    @AccessTokenAuthentication
    @ResponseBody
    public ResponseEntity<List<ShtCategorySetting>> getCategories(){
        ResponseEntity<List<ShtCategorySetting>> responseEntity = null;
        List<ShtCategorySetting> list = service.getActiveCategorySetting();
        return new ResponseEntity<List<ShtCategorySetting>>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/category-setting-no-login")
    @NoAuthentication
    @ResponseBody
    public ResponseEntity<List<ShtCategorySetting>> getCategoriesNoSetting(){
        ResponseEntity<List<ShtCategorySetting>> responseEntity = null;
        List<ShtCategorySetting> list = service.getNoLoginActiveCategorySetting();
        return new ResponseEntity<List<ShtCategorySetting>>(list, HttpStatus.OK);
    }
}
