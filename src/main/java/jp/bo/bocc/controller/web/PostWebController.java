package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.PostSearchRequest;
import jp.bo.bocc.controller.web.validator.PostSearchValidator;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author NguyenThuong on 3/21/2017.
 */

@Controller
public class PostWebController extends BoccBaseWebController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    PostService postService;

    @Autowired
    CategoryService service;

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    PostSearchValidator postSearchValidator;

    @Autowired
    AdminService adminService;

    @Autowired
    AdminCsvHstService adminCsvHstService;

    @Value("${page.size}")
    private int maxRecordsInPage;

    @RequestMapping(value = "/web/categories", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ShmCategory>> getCategories(@RequestParam(value ="parentId", required = false) Long parentId){
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        List<ShmCategory> list = service.findByCategoryParentId(parentId);
        return new ResponseEntity<List<ShmCategory>>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    public String getPost(Model model, @ModelAttribute("postSearch") PostSearchRequest request, BindingResult bindingResult) {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        postSearchValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return "post-search";
        }

        Map<Long, String> parentCategories = getParentCategories();
        model.addAttribute("parentCats", parentCategories);

        if(request != null && request.getParentCat() != null){
            Map<Long, String> childCategories = getChildCategories(request.getParentCat());
            model.addAttribute("childCats", childCategories);
        }

        PageRequest pageRequest = new PageRequest(request.getPage(), maxRecordsInPage, Sort.Direction.DESC, "userUpdateAt");
        Page<ShmPost> posts;
        try {
            posts = postService.searchPostByConditions(request, pageRequest);
        } catch (Exception e) {
            model.addAttribute("posts", null);
            model.addAttribute("dataError", getMessage("SH_E100139"));
            return "post-search";
        }

        if (posts != null) {
            List<ShmPost> postResult = postService.buildListImagePathsForListPost(posts.getContent());
            model.addAttribute("totalPage", posts.getTotalPages() - 1);
            model.addAttribute("totalElements", posts.getTotalElements());
            model.addAttribute("posts", postResult);
            model.addAttribute("curPage", request.getPage());
            model.addAttribute("startElement", (request.getPage() * maxRecordsInPage) + 1);
            model.addAttribute("curElements", (request.getPage() * maxRecordsInPage) + postResult.size());
        }
        return "post-search";
    }

    @RequestMapping(value = "/posts/csv", method = RequestMethod.GET)
    public void exportPostCSV(Model model, @ModelAttribute("postSearch") PostSearchRequest request, HttpServletResponse response) throws IOException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        PageRequest pageRequest = new PageRequest(request.getPage(), maxRecordsInPage, Sort.Direction.DESC, "userUpdateAt");

        postService.exportPostCSV(request, pageRequest, response);
        adminCsvHstService.save(admin, ShtAdminCsvHst.CSV_TYPE.POST);
    }

    private Map<Long, String> getParentCategories() {
        List<ShmCategory> categories = categoryService.getCategories(true);
        Map<Long, String> result = categories.stream().collect(Collectors.toMap(ShmCategory::getCategoryId, ShmCategory::getCategoryName));
        return result.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e2, LinkedHashMap::new));
    }

    private Map<Long, String> getChildCategories(Long parentCategoryId) {
        List<ShmCategory> categories = categoryService.findByCategoryParentId(parentCategoryId);
        Map<Long, String> result = categories.stream().collect(Collectors.toMap(ShmCategory::getCategoryId, ShmCategory::getCategoryName));
        return result.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e2, LinkedHashMap::new));
    }

    @RequestMapping("/post-detail")
    public String postDetail(@RequestParam long id, Model model) throws Exception {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        ShmPost post = null;
        try {
            post = postService.getPost(id);
            if (post != null) {
                ShmPost result = postService.buildListImagePathsForPost(post);

                long countPost = postService.getCountPostById(result.getShmUser().getId());
                final ShmUser shmUser = result.getShmUser();
                String postOwnerAvatarPath = null;
                if (shmUser != null)
                    postOwnerAvatarPath = userService.buildOriginalAvatarPathForUser(shmUser.getAvatar());

                // get full address
                if (shmUser != null && shmUser.getAddress() != null) {
                    ShmAddr shmAddr = shmUser.getAddress();
                    shmUser.getAddress().setFullAreaName(addressService.getFullAddress(shmAddr));
                }
                post.getPostHashTagVal();
                post.setPostReportTimes(postService.getReportTimeForPost(post.getPostId()));
                model.addAttribute("postOwnerAvatarPath", postOwnerAvatarPath);
                model.addAttribute("countPost", countPost);
                model.addAttribute("postDetail", result);
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", getMessage("SH_E100089"));
            return "post-detail";
        }
        return "post-detail";
    }

}
