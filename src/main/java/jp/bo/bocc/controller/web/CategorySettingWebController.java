package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.CategorySettingRequest;
import jp.bo.bocc.controller.web.request.GroupPublishRequest;
import jp.bo.bocc.controller.web.validator.AdminNgValidator;
import jp.bo.bocc.controller.web.validator.CategorySettingValidator;
import jp.bo.bocc.controller.web.validator.GroupPublishValidator;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by buixu on 11/14/2017.
 */
@Controller
public class CategorySettingWebController extends BoccBaseWebController{

    @Autowired
    CategorySettingService categorySettingService;

    @Autowired
    PostService postService;

    @Autowired
    AdminService adminService;

    public static int PAGE_SIZE = 20;

    @RequestMapping(value = "list-category-setting", method = RequestMethod.GET)
    public String listGroupPublish(@RequestParam(value = "pageNumber", required = false) Integer pageNumber, Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtCategorySetting categorySetting = new ShtCategorySetting();
        if (pageNumber == null)
            pageNumber = 0;
        final Pageable pageSelect = new PageRequest(pageNumber, PAGE_SIZE);
        Page<ShtCategorySetting> page = categorySettingService.getCategorySetting(categorySetting, pageSelect);
        List<ShtCategorySetting> categorySettings = page.getContent();

        if(categorySettings.size()==0) {
            String nullResult = "データはありません !";
            model.addAttribute("nullResult", nullResult);
        }

        int sizePage = page.getTotalPages();

        model.addAttribute("sizeResult", categorySettings.size());
        model.addAttribute("listCategorySettings", categorySettings);
        int current = page.getNumber();
        model.addAttribute("deploymentLog", page);
        model.addAttribute("currentIndex", current);

        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("curPage", current);
        model.addAttribute("startElement", ((pageNumber) * PAGE_SIZE) + 1);
        model.addAttribute("curElements", ((pageNumber) * PAGE_SIZE) + categorySettings.size());

        return "list-category-setting";
    }

    @RequestMapping(value = "/create-category-setting")
    public String createCategorySetting(Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        model.addAttribute("categorySetting", new ShtCategorySetting());
        return "create-category-setting";
    }

    @RequestMapping(value = "/validate-category-setting", method = RequestMethod.POST)
    public @ResponseBody CategorySettingValidator validateCategorySetting(Model model, @RequestBody CategorySettingRequest request) {
        return validateCategorySetting(request);
    }

    @RequestMapping(value = "/reset-category-setting", method = RequestMethod.POST)
    public String resetCategorySetting(Model model, @RequestBody CategorySettingRequest request) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        categorySettingService.resetDefaultCategorySetting();
        return "list-category-setting";
    }

    @RequestMapping(value = "/add-category-setting", method = RequestMethod.POST)
    public @ResponseBody CategorySettingValidator addCategorySetting(Model model, @RequestBody CategorySettingRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        CategorySettingValidator validator = validateCategorySetting(request);
        if(validator.getError() == 1){
            return validator;
        }
        ShtCategorySetting categorySetting = new ShtCategorySetting();
        categorySetting.setShmAdmin(shmAdmin);
        categorySetting.setCategoryName(request.getCategoryName());
        categorySetting.setIsDefault(false);
        categorySetting.setCategoryStatus(ShtCategorySetting.CategoryStatusEnum.SUSPENDED);

        if(request.getFilterTypePrivate() != null && request.getFilterTypePrivate()){
            categorySetting.setCategoryFilterType(ShtCategorySetting.CategoryFilterTypeEnum.PRIVATE);
        }
        if(request.getFilterTypeKeyword() != null && request.getFilterTypeKeyword()){
            categorySetting.setCategoryFilterType(ShtCategorySetting.CategoryFilterTypeEnum.KEYWORD);
            categorySetting.setKeywords(request.getFilterText());
        }
        if(request.getFilterTypePostId() != null && request.getFilterTypePostId()){
            categorySetting.setCategoryFilterType(ShtCategorySetting.CategoryFilterTypeEnum.POST_ID);
            categorySetting.setPostIds(request.getFilterText());
        }

        if(request.getPublishTypeAll() != null && request.getPublishTypeAll()){
            categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.ALL);
        }
        if(request.getPublishTypeBuy() != null && request.getPublishTypeBuy()){
            categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.BUY);
        }
        if(request.getPublishTypeSell() != null && request.getPublishTypeSell()){
            categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.SELL);
        }

        if(request.getFilterTypePostId() != null && request.getFilterTypePostId()){
            String postIdStrs = request.getFilterText();
            String publishType = "";
            Boolean isContainSell = false;
            Boolean isContainBuy = false;
            String[] postIds = postIdStrs.split(",");
            for(int i = 0 ; i < postIds.length ; i++){
                Long postId = new Long(postIds[i].trim());
                ShmPost shmPost = postService.getPost(postId);
                if(shmPost != null){
                    if(shmPost.getPostType() == ShmPost.PostType.SELL){
                        isContainSell = true;
                    }else{
                        isContainBuy = true;
                    }
                }
            }
            if(isContainSell && isContainBuy){
                categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.ALL);
            }else if(isContainSell && ! isContainBuy){
                categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.SELL);
            }else {
                categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.BUY);
            }

            categorySetting.setPostIds(request.getFilterText());
        }

        categorySettingService.saveCategorySetting(categorySetting);

        return validator;
    }

    private CategorySettingValidator validateCategorySetting(CategorySettingRequest request){
        CategorySettingValidator validator = new CategorySettingValidator();
        if(StringUtils.isEmpty(request.getCategoryName()) || StringUtils.isEmpty(request.getCategoryName().trim())){
            validator.setError(1);
            validator.setErrorMsg("カテゴリ名を入力してください");
            return validator;
        }
        List<ShtCategorySetting> categorySettings = categorySettingService.getCategorySettingByName(request.getCategoryName());
        if(CollectionUtils.isNotEmpty(categorySettings) && request.getCategorySettingId() == null){
            validator.setError(1);
            validator.setErrorMsg("このカテゴリ名が存在していますので、他のカテゴリ名を入力してください");
            return validator;
        }else if(CollectionUtils.isNotEmpty(categorySettings) && request.getCategorySettingId() !=null){
            for (ShtCategorySetting cate: categorySettings) {
                if(cate.getCategorySettingId().intValue() != request.getCategorySettingId().intValue()){
                    validator.setError(1);
                    validator.setErrorMsg("このカテゴリ名が存在していますので、他のカテゴリ名を入力してください");
                    return validator;
                }
            }
        }
        if((request.getFilterTypePrivate() == null || request.getFilterTypePrivate() == false) && (request.getFilterTypeKeyword() == null || request.getFilterTypeKeyword() == false)
                && (request.getFilterTypePostId() == null || request.getFilterTypePostId() == false)){
            validator.setError(1);
            validator.setErrorMsg("表示方法を選択してください");
            return validator;
        }
        if(request.getFilterTypeKeyword() != null && request.getFilterTypeKeyword() && (StringUtils.isEmpty(request.getFilterText()) || StringUtils.isEmpty(request.getFilterText().trim()))){
            validator.setError(1);
            validator.setErrorMsg("キーワードを入力してください");
            return validator;
        }
        if(request.getFilterTypePostId() != null && request.getFilterTypePostId() && (StringUtils.isEmpty(request.getFilterText()) || StringUtils.isEmpty(request.getFilterText().trim()))){
            validator.setError(1);
            validator.setErrorMsg("投稿IDを入力してください");
            return validator;
        }

        if(request.getFilterTypePostId() != null && request.getFilterTypePostId()){
            String postIdStrs = request.getFilterText();
            String[] postIds = postIdStrs.split(",");
            if(postIds.length <= 0){
                validator.setError(1);
                validator.setErrorMsg("PostIds invalid");
                return validator;
            }
            for(int i = 0; i < postIds.length ; i++){
                String pattern = "([０-９]+)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(postIds[i].trim());
                if(m.find()){
                    validator.setError(1);
                    validator.setErrorMsg("入力されたIDに全角文字が含まれている可能性があります");
                    return validator;
                }
                if(!StringUtils.isNumeric(postIds[i].trim())){
                    validator.setError(1);
                    validator.setErrorMsg("投稿IDには数字を入力してください " + postIds[i]);
                    return validator;
                }
            }

            for(int i = 0 ; i < postIds.length ; i++){
                Long postId = new Long(postIds[i].trim());
                ShmPost shmPost = postService.getPost(postId);
                if(shmPost == null){
                    validator.setError(1);
                    validator.setErrorMsg("表示できない商品・内容が含まれています（削除か停止されています）: " + postIds[i]);
                    return validator;
                }
                if(shmPost.getPostSellStatus() == ShmPost.PostSellSatus.DELETED){
                    validator.setError(1);
                    validator.setErrorMsg("表示できない商品・内容が含まれています（削除か停止されています）: " + postIds[i]);
                    return validator;
                }
                if(shmPost.getPostCtrlStatus() == ShmPost.PostCtrlStatus.SUSPENDED){
                    validator.setError(1);
                    validator.setErrorMsg("表示できない商品・内容が含まれています（削除か停止されています）: " + postIds[i]);
                    return validator;
                }
            }
        }

        return validator;
    }

    @RequestMapping(value = "/edit-category-setting")
    public String editCategorySetting(Model model, @RequestParam Long categorySettingId) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtCategorySetting categorySetting = categorySettingService.getCategorySettingById(categorySettingId);
        CategorySettingRequest categorySettingRequest = new CategorySettingRequest();
        if(categorySetting == null){
            return "list-group-publish";
        }
        categorySettingRequest.setCategorySettingId(categorySettingId);
        categorySettingRequest.setCategoryName(categorySetting.getCategoryName());
        categorySettingRequest.setIsDefault(categorySetting.getIsDefault());
        if(categorySetting.getCategoryFilterType() == ShtCategorySetting.CategoryFilterTypeEnum.PRIVATE){
            categorySettingRequest.setFilterTypePrivate(true);
        }
        if(categorySetting.getCategoryFilterType() == ShtCategorySetting.CategoryFilterTypeEnum.KEYWORD){
            categorySettingRequest.setFilterTypeKeyword(true);
            categorySettingRequest.setFilterText(categorySetting.getKeywords());
        }
        if(categorySetting.getCategoryFilterType() == ShtCategorySetting.CategoryFilterTypeEnum.POST_ID){
            categorySettingRequest.setFilterTypePostId(true);
            categorySettingRequest.setFilterText(categorySetting.getPostIds());
        }
        if(categorySetting.getCategoryPublishType() == ShtCategorySetting.CategoryPublishTypeEnum.ALL){
            categorySettingRequest.setPublishTypeAll(true);
        }
        if(categorySetting.getCategoryPublishType() == ShtCategorySetting.CategoryPublishTypeEnum.SELL){
            categorySettingRequest.setPublishTypeSell(true);
        }
        if(categorySetting.getCategoryPublishType() == ShtCategorySetting.CategoryPublishTypeEnum.BUY){
            categorySettingRequest.setPublishTypeBuy(true);
        }

        model.addAttribute("categorySetting", categorySettingRequest);
        return "edit-category-setting";
    }

    @RequestMapping(value = "/update-category-setting", method = RequestMethod.POST)
    public @ResponseBody CategorySettingValidator updateCategorySetting(Model model, @RequestBody CategorySettingRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        CategorySettingValidator validator = validateCategorySetting(request);
        if(validator.getError() == 1){
            return  validator;
        }
        ShtCategorySetting categorySetting = categorySettingService.getCategorySettingById(request.getCategorySettingId());
        if(categorySetting == null){
            validator.setError(1);
            validator.setErrorMsg("Category not found");
            return validator;
        }
        categorySetting.setShmAdmin(shmAdmin);
        categorySetting.setCategoryName(request.getCategoryName());

        if(request.getFilterTypePrivate() != null && request.getFilterTypePrivate()){
            categorySetting.setCategoryFilterType(ShtCategorySetting.CategoryFilterTypeEnum.PRIVATE);
        }
        if(request.getFilterTypeKeyword() != null && request.getFilterTypeKeyword()){
            categorySetting.setCategoryFilterType(ShtCategorySetting.CategoryFilterTypeEnum.KEYWORD);
            categorySetting.setKeywords(request.getFilterText());
        }
        if(request.getFilterTypePostId() != null && request.getFilterTypePostId()){
            categorySetting.setCategoryFilterType(ShtCategorySetting.CategoryFilterTypeEnum.POST_ID);
            categorySetting.setPostIds(request.getFilterText());
        }

        if(request.getPublishTypeAll() != null && request.getPublishTypeAll()){
            categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.ALL);
        }
        if(request.getPublishTypeBuy() != null && request.getPublishTypeBuy()){
            categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.BUY);
        }
        if(request.getPublishTypeSell() != null && request.getPublishTypeSell()){
            categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.SELL);
        }

        if(request.getFilterTypePostId() != null && request.getFilterTypePostId()){
            String postIdStrs = request.getFilterText();
            Boolean isContainSell = false;
            Boolean isContainBuy = false;
            String[] postIds = postIdStrs.split(",");
            for(int i = 0 ; i < postIds.length ; i++){
                Long postId = new Long(postIds[i].trim());
                ShmPost shmPost = postService.getPost(postId);
                if(shmPost != null){
                    if(shmPost.getPostType() == ShmPost.PostType.SELL){
                        isContainSell = true;
                    }else{
                        isContainBuy = true;
                    }
                }
            }
            if(isContainSell && isContainBuy){
                categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.ALL);
            }else if(isContainSell && ! isContainBuy){
                categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.SELL);
            }else {
                categorySetting.setCategoryPublishType(ShtCategorySetting.CategoryPublishTypeEnum.BUY);
            }

            categorySetting.setPostIds(request.getFilterText());
        }

        categorySettingService.saveCategorySetting(categorySetting);
        return validator;
    }

    @RequestMapping(value = "/default-category-setting", method = RequestMethod.PUT)
    public @ResponseBody CategorySettingValidator defaultCategorySetting(Model model, @RequestBody CategorySettingRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        CategorySettingValidator validator = new CategorySettingValidator();
        if(request.getCategorySettingId() != null){
            ShtCategorySetting categorySetting = categorySettingService.getCategorySettingById(request.getCategorySettingId());
            if(categorySetting.getCategoryStatus() == ShtCategorySetting.CategoryStatusEnum.SUSPENDED){
                validator.setError(1);
                validator.setErrorMsg("停止中のカテゴリはデフォルトには設定できません");
                return validator;
            }
            categorySettingService.resetDefaultCategorySetting();
            categorySetting.setIsDefault(true);
            categorySettingService.saveCategorySetting(categorySetting);
        }
        return validator;
    }

    @RequestMapping(value = "/suspend-category-setting", method = RequestMethod.PUT)
    public @ResponseBody CategorySettingValidator suspendCategorySetting(Model model, @RequestBody CategorySettingRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        CategorySettingValidator validator = new CategorySettingValidator();
        if(request.getCategorySettingId() != null){
            Long activeCategory = categorySettingService.countCategorySettingActive();
            ShtCategorySetting categorySetting = categorySettingService.getCategorySettingById(request.getCategorySettingId());
            if(activeCategory > 4 && categorySetting.getCategoryStatus() == ShtCategorySetting.CategoryStatusEnum.SUSPENDED){
                validator.setError(1);
                validator.setErrorMsg("表示中のものを１つ以上非表示にしてください");
                return validator;
            }
            if(categorySetting.getCategoryStatus() == ShtCategorySetting.CategoryStatusEnum.SUSPENDED){
                categorySetting.setCategoryStatus(ShtCategorySetting.CategoryStatusEnum.ACTIVE);
            }else{
                categorySetting.setCategoryStatus(ShtCategorySetting.CategoryStatusEnum.SUSPENDED);
                categorySetting.setIsDefault(false);
            }
            categorySettingService.saveCategorySetting(categorySetting);
        }
        return validator;
    }
}
