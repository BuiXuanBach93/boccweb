package jp.bo.bocc.controller.api;

import jp.bo.bocc.entity.ShmCategory;
import jp.bo.bocc.service.CategoryService;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by DonBach on 3/15/2017.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    CategoryService service;

    @GetMapping("/{id}")
    @NoAuthentication
    @ResponseBody
    public ShmCategory getCategory(@PathVariable long id) {
        return service.get(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @NoAuthentication
    @ResponseBody
    public ResponseEntity<List<ShmCategory>> getCategories(@RequestParam(value ="parentId", required = false) Long parentId,
                                                           @RequestParam(value ="isAllIncluded", required = false) Boolean isAllIncluded){
        ResponseEntity<List<ShmCategory>> responseEntity = null;
        List<ShmCategory> list = null;
        if(parentId == null){
            list = service.getCategories(true);
        }else{
            list = service.findByCategoryParentId(parentId);
        }
        if(list != null && isAllIncluded != null && isAllIncluded == true){
            ShmCategory allCategory = new ShmCategory();
            allCategory.setCategoryName("すべて");
            allCategory.setCategoryId(0L);
            list.add(0,allCategory);
        }
        if(list.isEmpty()){
            return new ResponseEntity<List<ShmCategory>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<ShmCategory>>(list, HttpStatus.OK);
    }
}
