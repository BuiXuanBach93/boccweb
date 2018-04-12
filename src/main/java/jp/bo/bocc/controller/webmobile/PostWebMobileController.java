package jp.bo.bocc.controller.webmobile;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.service.PostService;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Namlong on 8/1/2017.
 */
@RestController
public class PostWebMobileController extends BoccBaseController {
    private final static Logger LOGGER = Logger.getLogger(PostWebMobileController.class.getName());

    /**
     * Search post without login
     */
    @Autowired
    PostService postService;
    @GetMapping(value = "/webmobile/posts")
    @NoAuthentication
    public Page<ShmPostDTO> searchPost(@RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size, @RequestParam(value = "textSearch", required = false) String textSearch, @RequestParam(value="postType", required = false) String postType,
                                       @RequestParam(value="categoryIdSuper", required = false) Long categoryIdSuper,
                                       @RequestParam(value="categoryIdChild", required = false) Long categoryIdChild,
                                       @RequestParam(value="addrProvinceId", required = false) Long addrProvinceId, @RequestParam(value="addrDistrictId", required = false) Long addrDistrictId,
                                       @RequestParam(value="isSameCompany", required = false) boolean isSameCompany,
                                       @RequestParam(value="sortField", required = false) String sortField,
                                       @RequestParam(value="price", required = false) Long price,
                                       @RequestParam(value="categorySettingId", required = false) Long categorySettingId,
                                       @RequestParam(value="bannerId", required = false) Long bannerId,
                                       HttpServletRequest servletRequest){
        Page<ShmPostDTO> result = null;
        try {
            Pageable pageable = createPage(page,size);
            String userAgent = servletRequest.getHeader("User-Agent");
            result = postService.searchPost(pageable, textSearch, postType, addrProvinceId, addrDistrictId, isSameCompany, null, categoryIdSuper, categoryIdChild, sortField, price, userAgent, categorySettingId, bannerId);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * Show post detail without login
     * @param id
     * @return
     */
    @RequestMapping(value = "/webmobile/posts/{id}", method = RequestMethod.GET)
    @NoAuthentication
    public ShmPost getPost(@PathVariable long id) {
        return postService.getPost(id, null);
    }

    /**
     * Get list post of user
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/webmobile/posts/get-by-user")
    @NoAuthentication
    public Page<ShmPost> searchPost(@RequestParam(value = "userId", required = true) Long userId, @RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size){
        Page<ShmPost> result = null;
        try {
            Pageable pageable = createPage(page,size);
            result = postService.getPostByUserId(userId, null, pageable);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }
}
