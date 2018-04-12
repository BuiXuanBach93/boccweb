package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.controller.api.request.PostBodyEdit;
import jp.bo.bocc.controller.api.request.PostBodyRequest;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by haipv on 3/14/2017.
 */
@RestController
public class PostController extends BoccBaseController {

    private final static Logger LOGGER = Logger.getLogger(PostController.class.getName());

    @Autowired
    PostService postService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    RequestContext requestContext;

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;
    @Autowired
    AdminNgService adminNgService;

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public ShmPost getPost(@PathVariable long id) {
        ShmUser shmUser = requestContext.getUser();
        return postService.getPost(id, shmUser);
    }

    @RequestMapping(value = "/posts-no-login/{id}", method = RequestMethod.GET)
    @NoAuthentication
    public ShmPost getPostNoLogin(@PathVariable long id) {
        ShmUser shmUser = requestContext.getUser();
        return postService.getPost(id, shmUser);
    }

    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    @AccessTokenAuthentication
    @ResponseBody
    public ResponseEntity<PostBodyRequest> createPost(@RequestBody PostBodyRequest postBodyRequest) throws Exception{
        ShmUser shmUser = requestContext.getUser();
        PostBodyRequest bodyRequest =  postService.createPost(postBodyRequest, shmUser);
        return new ResponseEntity<PostBodyRequest>(bodyRequest, HttpStatus.CREATED);
    }

    @GetMapping(value = "/posts")
    @AccessTokenAuthentication
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
            ShmUser user = requestContext.getUser();
            Long userId =  null;
            String userAgent = servletRequest.getHeader("User-Agent");
            if (user != null )
                userId = user.getId();
            result = postService.searchPost(pageable, textSearch, postType, addrProvinceId, addrDistrictId, isSameCompany, userId, categoryIdSuper, categoryIdChild, sortField, price, userAgent, categorySettingId, bannerId);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }


    @GetMapping(value = "/posts-no-login")
    @NoAuthentication
    public Page<ShmPostDTO> searchPostNoLogin(@RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size, @RequestParam(value = "textSearch", required = false) String textSearch, @RequestParam(value="postType", required = false) String postType,
                                       @RequestParam(value="categoryIdSuper", required = false) Long categoryIdSuper,
                                       @RequestParam(value="categoryIdChild", required = false) Long categoryIdChild,
                                       @RequestParam(value="addrProvinceId", required = false) Long addrProvinceId, @RequestParam(value="addrDistrictId", required = false) Long addrDistrictId,
                                       @RequestParam(value="isSameCompany", required = false) boolean isSameCompany,
                                       @RequestParam(value="sortField", required = false) String sortField,
                                       @RequestParam(value="price", required = false) Long price,
                                       @RequestParam(value="categorySettingId", required = false) Long categorySettingId,
                                       @RequestParam(value="bannerId", required = false) Long bannerId){
        Page<ShmPostDTO> result = null;
        try {
            Pageable pageable = createPage(page,size);
            ShmUser user = requestContext.getUser();
            Long userId =  null;
            if (user != null )
                userId = user.getId();
            result = postService.searchPost(pageable, textSearch, postType, addrProvinceId, addrDistrictId, isSameCompany, userId, categoryIdSuper, categoryIdChild, sortField, price, null,categorySettingId, bannerId);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }


    @GetMapping(value = "/posts/get-by-user")
    @AccessTokenAuthentication
    public Page<ShmPost> searchPost(@RequestParam(value = "userId", required = true) Long userId, @RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size){
        Page<ShmPost> result = null;
        try {
            Pageable pageable = createPage(page,size);
            ShmUser user = requestContext.getUser();
            result = postService.getPostByUserId(userId, user.getBsid(), pageable);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    @GetMapping(value = "/posts/get-by-user-no-login")
    @NoAuthentication
    public Page<ShmPost> searchPostNoLogin(@RequestParam(value = "userId", required = true) Long userId, @RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size){
        Page<ShmPost> result = null;
        try {
            Pageable pageable = createPage(page,size);
            ShmUser user = requestContext.getUser();
            result = postService.getPostByUserId(userId, null, pageable);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    @GetMapping(value = "/posts/get-home")
    @NoAuthentication
    public Page<ShmPostDTO> searchPost(@RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size, @RequestParam(value = "textSearch", required = false) String textSearch, @RequestParam(value="postType", required = false) String postType,
                                       @RequestParam(value="categoryIdSuper", required = false) Long categoryIdSuper,
                                       @RequestParam(value="categoryIdChild", required = false) Long categoryIdChild){
        Page<ShmPostDTO> result = null;
        try {
            Pageable pageable = createPage(page,size);
            ShmUser user = requestContext.getUser();
            if(user == null){
                user = userService.getUserById(2L);
            }
            Long userId = null;
            if (user != null )
                userId = user.getId();
            result = postService.searchPost(pageable, textSearch, postType, null, null, false, userId, categoryIdSuper, categoryIdChild, null,null, null, null, null);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    @DeleteMapping(value = "/posts/{id}")
    @AccessTokenAuthentication
    public ResponseEntity<ShmPost> deletePost(@PathVariable("id") long id) throws Exception {
        ShmPost shmPost = postService.getPost(id);
        if(shmPost == null){
            return  new ResponseEntity<ShmPost>(HttpStatus.NOT_FOUND);
        }
        if(shmPost.getPostSellStatus() == ShmPost.PostSellSatus.TEND_TO_SELL){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100016"));
        }
        postService.deletePost(shmPost);
        return new ResponseEntity<ShmPost>(HttpStatus.OK);
    }

    @PutMapping(value = "/posts/{id}")
    @AccessTokenAuthentication
    public ResponseEntity<ShmPost> editPost(@PathVariable("id") long id, @RequestBody PostBodyEdit postBodyEdit){
        ShmPost shmPost = postService.getPost(id);
        postService.editPost(shmPost,postBodyEdit);
        ShmPost edited = getPost(shmPost.getPostId());
        return new ResponseEntity<ShmPost>(edited, HttpStatus.OK);
    }

    @GetMapping(value = "/posts/owner/active-talks")
    @AccessTokenAuthentication
    public Page<ShmPostDTO> searchOwnPostHasConversation(@RequestParam(value = "page", required = true) int page,@RequestParam(value = "size", required = true) int size) throws Exception {

        Page<ShmPostDTO> result = null;
        try {
            Pageable pageable = createPage(page,size);
            ShmUser user = requestContext.getUser();
            Long userId = null;
            if (user != null )
                userId = user.getId();
            else
                throw new Exception(getMessage("SH_E100005"));
            result = postService.searchOwnPostHasConversation(pageable, userId);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    @GetMapping(value = "/posts/from-others/active-talks")
    @AccessTokenAuthentication
    public Page<ShmPostDTO> searchPostFromOthersUserHasConversation(@RequestParam(value = "page", required = true) int page,@RequestParam(value = "size", required = true) int size) throws Exception {

        Page<ShmPostDTO> result = null;
        try {
            Pageable pageable = createPage(page,size);
            ShmUser user = requestContext.getUser();
            Long userId = null;
            if (user != null )
                userId = user.getId();
            else
                throw new Exception(getMessage("SH_E100005"));
            result = postService.searchPostFromOthersHasConversation(pageable, userId);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    @GetMapping(value = "/posts/history")
    @AccessTokenAuthentication
    public Page<ShmPostDTO> getPostHistory(@RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size,
                                           @RequestParam(value="postType", required = false) String postType,
                                       @RequestParam(value="sortField", required = false) String sortField,
                                       @RequestParam(value="price", required = false) Long price){
        Page<ShmPostDTO> result = null;
        try {
            Pageable pageable = createPage(page,size);
            ShmUser user = requestContext.getUser();
            Long userId =  null;
            if (user != null )
                userId = user.getId();
            result = postService.getPostHistory(pageable, postType, userId, sortField, price);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    @GetMapping(value = "/")
    public ShmPostDTO getCampaignPost() {

        return null;
    }

    @GetMapping(value = "/posts/pre-create")
    @AccessTokenAuthentication
    public ShmPostDTO preCreatePost() throws Exception{
        ShmPostDTO result = null;
        try {
            Long userId =  getUserIdUsingApp();
            result = postService.getDefaultValueUserForPost(userId);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

}
