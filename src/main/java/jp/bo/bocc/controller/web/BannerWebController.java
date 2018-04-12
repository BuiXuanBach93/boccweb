package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.BannerRequest;
import jp.bo.bocc.controller.web.response.BannerPageResponse;
import jp.bo.bocc.controller.web.validator.BannerValidator;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by buixu on 11/14/2017.
 */
@Controller
public class BannerWebController extends BoccBaseWebController{

    @Autowired
    BannerService bannerService;

    @Autowired
    BannerPageService bannerPageService;

    @Autowired
    PostService postService;

    @Autowired
    AdminService adminService;

    @Autowired
    CategorySettingService categorySettingService;

    public static int PAGE_SIZE = 20;

   @RequestMapping(value = "list-banner", method = RequestMethod.GET)
    public String listBanner(@RequestParam(value = "pageNumber", required = false) Integer pageNumber, Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtBanner banner = new ShtBanner();
        if (pageNumber == null)
            pageNumber = 0;
        final Pageable pageSelect = new PageRequest(pageNumber, PAGE_SIZE);
        Page<ShtBanner> page = bannerService.getBanners(banner, pageSelect);
        List<ShtBanner> banners = page.getContent();

        if(banners.size()==0) {
            String nullResult = "データはありません !";
            model.addAttribute("nullResult", nullResult);
        }

       for ( ShtBanner bannerItm: banners) {
           if(bannerItm.getDestinationType() == ShtBanner.DestinationTypeEnum.WEB){
               bannerItm.setBannerDestination(bannerItm.getWebUrl());
           }
           if(bannerItm.getDestinationType() == ShtBanner.DestinationTypeEnum.CATEGORY){
               bannerItm.setBannerDestination(bannerItm.getCategoryId().toString());
           }
           if(bannerItm.getDestinationType() == ShtBanner.DestinationTypeEnum.POST_ID){
               bannerItm.setBannerDestination(bannerItm.getPostIds());
           }
           if(bannerItm.getDestinationType() == ShtBanner.DestinationTypeEnum.KEYWORD){
               bannerItm.setBannerDestination(bannerItm.getKeywords());
           }
       }

        model.addAttribute("sizeResult", banners.size());
        model.addAttribute("listBanners", banners);
        int current = page.getNumber();
        model.addAttribute("deploymentLog", page);
        model.addAttribute("currentIndex", current);

        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("curPage", current);
        model.addAttribute("startElement", ((pageNumber) * PAGE_SIZE) + 1);
        model.addAttribute("curElements", ((pageNumber) * PAGE_SIZE) + banners.size());

        return "list-banner";
    }


    @RequestMapping(value = "/create-banner")
    public String createBanner(Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());

        BannerRequest request = new BannerRequest();
        request.setTitleColor("#000000");
        request.setBackgroundColor("#FFFFFF");

        model.addAttribute("banner", request);
        return "create-banner";
    }



    @RequestMapping(value = "/validate-banner", method = RequestMethod.POST)
    public @ResponseBody BannerValidator validateBanner(Model model, @RequestBody BannerRequest request) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        return validateBanner(request);
    }

    @RequestMapping(value = "/add-banner-page", method = RequestMethod.POST)
    @ResponseBody
    public BannerPageResponse addBannerPage(Model model, @ModelAttribute("banner") BannerRequest request, @RequestParam(value = "imagePage", required = false) MultipartFile imagePage) throws IOException {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        BannerPageResponse pageResponse = new BannerPageResponse();
        // check build banner page
        if(request.getUrlType() != null){
            if(request.getUrlType().intValue() == 1){
                if(request.getPageId() == null){
                    ShtBannerPage bannerPage = bannerPageService.storeBannerPage(request, imagePage);
                    pageResponse.setPageId(bannerPage.getPageId());
                    pageResponse.setPageUrl(bannerPageService.buildPrivewPageUrl(bannerPage.getPageId()));
                }else{
                    // Just update data preview, do not update real data
                    ShtBannerPage bannerPage = bannerPageService.getBannerPageById(request.getPageId());
                    if(request.getIsChangeImagePage() != null && request.getIsChangeImagePage().intValue() == 1){
                        ShrFile image = bannerPageService.saveBannerPageImage(request.getPageId(), imagePage);
                        bannerPage.setImagePrv(image);
                    }else{
                        bannerPage.setImagePrv(bannerPage.getImage());
                    }
                    bannerPage.setTitleColorPrv(request.getTitleColor());
                    bannerPage.setBackgroundColorPrv(request.getBackgroundColor());
                    bannerPage.setPageTitlePrv(request.getPageTitle());
                    bannerPage.setPageContentPrv(request.getPageContent());
                    bannerPageService.saveBannerPage(bannerPage);

                    pageResponse.setPageId(request.getPageId());
                    pageResponse.setPageUrl(bannerPageService.buildPrivewPageUrl(request.getPageId()));
                }
            }
        }
        return pageResponse;
    }

    @RequestMapping(value = "/add-banner", method = RequestMethod.POST)
    public String addBanner(Model model, @ModelAttribute("banner") BannerRequest request, @RequestParam(value = "imageBanner", required = false) MultipartFile bannerImage,  @RequestParam(value = "imagePage", required = false) MultipartFile imagePage) throws IOException {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        BannerValidator validator = validateBanner(request);
        if(validator.getError() == 1){
            return "redirect:/backend/list-banner?pageNumber=0";
        }
        ShtBanner banner = new ShtBanner();
        banner.setShmAdmin(shmAdmin);
        banner.setBannerName(request.getBannerName());
        banner.setBannerStatus(ShtBanner.BannerStatusEnum.SUSPENDED);

        if(request.getDesType() != null){
            if(request.getDesType().intValue() == 0){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.WEB);
                banner.setWebUrl(request.getDestinationText());
            }
            if(request.getDesType().intValue() == 1){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.CATEGORY);
                banner.setCategoryId(new Long(request.getDestinationText().trim()));
            }
            if(request.getDesType().intValue() == 2){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.POST_ID);
                banner.setPostIds(request.getDestinationText());
            }
            if(request.getDesType().intValue() == 3){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.KEYWORD);
                banner.setKeywords(request.getDestinationText());
            }
        }

        // check build banner page
        if(request.getUrlType() != null){
            if(request.getUrlType().intValue() == 0){
                banner.setIsBuildPage(false);
            }else {
                banner.setIsBuildPage(true);
                if(request.getPageId() == null){
                    // store banner page
                    ShtBannerPage bannerPage = bannerPageService.storeBannerPage(request,imagePage);
                    bannerPage.setIsActive(true);
                    bannerPageService.saveBannerPage(bannerPage);

                    banner.setBannerPageId(bannerPage.getPageId());
                    banner.setWebUrl(bannerPageService.buildPageUrl(bannerPage.getPageId()));
                }else {
                    // active banner page
                    ShtBannerPage bannerPage = bannerPageService.getBannerPageById(request.getPageId());
                    if(bannerPage != null){
                        if(request.getIsChangeImagePage() != null && request.getIsChangeImagePage().intValue() == 1){
                            ShrFile image = bannerPageService.saveBannerPageImage(request.getPageId(), imagePage);
                            bannerPage.setImagePrv(image);
                        }else{
                            bannerPage.setImagePrv(bannerPage.getImage());
                        }
                        bannerPage.setTitleColorPrv(request.getTitleColor());
                        bannerPage.setBackgroundColorPrv(request.getBackgroundColor());
                        bannerPage.setPageTitlePrv(request.getPageTitle());
                        bannerPage.setPageContentPrv(request.getPageContent());

                        bannerPage.setIsActive(true);
                        bannerPage.setPageTitle(bannerPage.getPageTitlePrv());
                        bannerPage.setPageContent(bannerPage.getPageContentPrv());
                        bannerPage.setTitleColor(bannerPage.getTitleColorPrv());
                        bannerPage.setBackgroundColor(bannerPage.getBackgroundColorPrv());
                        bannerPage.setImage(bannerPage.getImagePrv());
                        bannerPageService.saveBannerPage(bannerPage);

                        banner.setBannerPageId(request.getPageId());
                        banner.setWebUrl(bannerPageService.buildPageUrl(bannerPage.getPageId()));
                    }
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime fromDate = LocalDateTime.parse(request.getFromDate(), formatter);
        banner.setFromDate(fromDate);
        LocalDateTime toDate = LocalDateTime.parse(request.getToDate(), formatter);
        banner.setToDate(toDate);
        ShtBanner bannerSaved = bannerService.saveBanner(banner);

        // store image
        ShrFile image = bannerService.saveBannerImage(bannerSaved.getBannerId(), bannerImage);
        bannerSaved.setImage(image);
        bannerService.saveBanner(bannerSaved);

        // setting cron job
        bannerService.settingSchedulerBanner(bannerSaved);

        return "redirect:/backend/list-banner?pageNumber=0";
    }


    private BannerValidator validateBanner(BannerRequest request){
        BannerValidator validator = new BannerValidator();
        if(StringUtils.isEmpty(request.getBannerName()) || StringUtils.isEmpty(request.getBannerName().trim())){
            validator.setError(1);
            validator.setErrorMsg("特集名を入力してください");
            return validator;
        }
        if((StringUtils.isEmpty(request.getDestinationText()) || StringUtils.isEmpty(request.getDestinationText().trim()))
                && (request.getUrlType() != null && request.getUrlType().intValue() == 0)){
            validator.setError(1);
            validator.setErrorMsg("特集方法特集方法を選択してください。");
            return validator;
        }
        if(request.getUrlFillType() != null && request.getUrlFillType()
                && request.getDesTypeWebUrl() != null && request.getDesTypeWebUrl()){
            if(StringUtils.isNotEmpty(request.getDestinationText()) && !request.getDestinationText().contains("http://")
                    && !request.getDestinationText().contains("https://")){
                validator.setError(1);
                validator.setErrorMsg("有効な URLを入力してください");
                return validator;
            }
        }
        if(request.getDesTypeCategoryId() != null && request.getDesTypeCategoryId()){
            String desText = request.getDestinationText();
            String pattern = "([０-９]+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(desText.trim());
            if(m.find()){
                validator.setError(1);
                validator.setErrorMsg("入力されたIDに全角文字が含まれている可能性があります");
                return validator;
            }
            if(!StringUtils.isNumeric(desText.trim())){
                validator.setError(1);
                validator.setErrorMsg("カテゴリIDには数字を入力してください " + desText);
                return validator;
            }

            ShtCategorySetting categorySetting = categorySettingService.getCategorySettingById(new Long(desText.trim()));
            if(categorySetting == null){
                validator.setError(1);
                validator.setErrorMsg("入力したカテゴリーIDが存在していません。");
                return validator;
            }
            if(categorySetting.getCategoryStatus() == ShtCategorySetting.CategoryStatusEnum.SUSPENDED){
                validator.setError(1);
                validator.setErrorMsg("入力したカテゴリーIDが停止されています。");
                return validator;
            }
            if(categorySetting.getCategoryFilterType() == ShtCategorySetting.CategoryFilterTypeEnum.PRIVATE){
                validator.setError(1);
                validator.setErrorMsg("限定投稿は特集バナー遷移先に設定できません");
                return validator;
            }
        }
        if(request.getDesTypePostId() != null && request.getDesTypePostId()){
            String postIdStrs = request.getDestinationText();
            String[] postIds = postIdStrs.split(",");
            if(postIds.length <= 0){
                validator.setError(1);
                validator.setErrorMsg("表示できない商品・内容が含まれています（削除か停止されています）");
                return validator;
            }
            for(int i = 0; i < postIds.length ; i++) {
                String pattern = "([０-９]+)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(postIds[i].trim());
                if (m.find()) {
                    validator.setError(1);
                    validator.setErrorMsg("入力されたIDに全角文字が含まれている可能性があります");
                    return validator;
                }
                if (!StringUtils.isNumeric(postIds[i].trim())) {
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
        if(request.getDesTypeKeyword() != null && request.getDesTypeKeyword()){
            String keywords = request.getDestinationText();
            String[] postIds = keywords.split(",");
            if(postIds.length <= 0){
                validator.setError(1);
                validator.setErrorMsg("キーワードを入力してください");
                return validator;
            }
        }

        if(StringUtils.isEmpty(request.getFromDate())){
            validator.setError(1);
            validator.setErrorMsg("表示開始日を入力してください");
            return validator;
        }
        if(StringUtils.isEmpty(request.getToDate())){
            validator.setError(1);
            validator.setErrorMsg("表示終了日を入力してください");
            return validator;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime fromDate = LocalDateTime.parse(request.getFromDate(), formatter);
        LocalDateTime toDate = LocalDateTime.parse(request.getToDate(), formatter);
        if(toDate.isBefore(fromDate)){
            validator.setError(1);
            validator.setErrorMsg("開始日を終了日の後に設定することはできません。");
            return validator;
        }

        if(toDate.isBefore(LocalDateTime.now())){
            validator.setError(1);
            validator.setErrorMsg("終了日がシステム日付以前のものは登録出来ない");
            return validator;
        }

        return validator;
    }



    @RequestMapping(value = "/edit-banner")
    public String editBanner(Model model, @RequestParam Long bannerId) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtBanner banner = bannerService.getBannerById(bannerId);
        BannerRequest bannerRequest = new BannerRequest();
        if(banner == null){
            return "redirect:/backend/list-banner?pageNumber=0";
        }
        bannerRequest.setBannerId(bannerId);
        bannerRequest.setBannerName(banner.getBannerName());
        if(banner.getDestinationType() == ShtBanner.DestinationTypeEnum.WEB){
            bannerRequest.setDestinationText(banner.getWebUrl());
            bannerRequest.setDesTypeWebUrl(true);
            if(banner.getBannerPageId() != null){
                bannerRequest.setImagePageUrl(bannerPageService.buildImagePageUrl(banner.getBannerPageId()));
            }
        }
        if(banner.getDestinationType() == ShtBanner.DestinationTypeEnum.CATEGORY){
            bannerRequest.setDestinationText(banner.getCategoryId().toString());
            bannerRequest.setDesTypeCategoryId(true);
        }
        if(banner.getDestinationType() == ShtBanner.DestinationTypeEnum.POST_ID){
            bannerRequest.setDestinationText(banner.getPostIds());
            bannerRequest.setDesTypePostId(true);
        }
        if(banner.getDestinationType() == ShtBanner.DestinationTypeEnum.KEYWORD){
            bannerRequest.setDestinationText(banner.getKeywords());
            bannerRequest.setDesTypeKeyword(true);
        }
        bannerRequest.setImageUrl(banner.getImageUrl());
        if(banner.getBannerPageId() != null){
            ShtBannerPage bannerPage = bannerPageService.getBannerPageById(banner.getBannerPageId());
            bannerRequest.setPageId(bannerPage.getPageId());
            bannerRequest.setPageTitle(bannerPage.getPageTitle());
            bannerRequest.setPageContent(bannerPage.getPageContent());
            bannerRequest.setTitleColor(bannerPage.getTitleColor());
            bannerRequest.setBackgroundColor(bannerPage.getBackgroundColor());
            bannerRequest.setIsChangeImagePage(0L);
        }else{
            bannerRequest.setTitleColor("#000000");
            bannerRequest.setBackgroundColor("#FFFFFF");
        }
        bannerRequest.setIsBuildPage(banner.getIsBuildPage());
        bannerRequest.setIsChangeImage(0L);
        if(banner.getFromDate() != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String fromDate = banner.getFromDate().format(formatter);
            bannerRequest.setFromDate(fromDate);
        }
        if(banner.getToDate() != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String toDate = banner.getToDate().format(formatter);
            bannerRequest.setToDate(toDate);
        }

        model.addAttribute("banner", bannerRequest);
        return "edit-banner";
    }

    @RequestMapping(value = "/update-banner", method = RequestMethod.POST)
    public String updateBanner(Model model, @ModelAttribute("banner") BannerRequest request, @RequestParam(value = "imageBanner", required = false) MultipartFile bannerImage, @RequestParam(value = "imagePage", required = false) MultipartFile imagePage) throws IOException {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        BannerValidator validator = validateBanner(request);
        if(validator.getError() == 1){
            return "redirect:/backend/list-banner?pageNumber=0";
        }
        ShtBanner banner = bannerService.getBannerById(request.getBannerId());
        if(banner == null){
            validator.setError(1);
            validator.setErrorMsg("Banner not found");
            return "";
        }
        banner.setShmAdmin(shmAdmin);
        banner.setBannerName(request.getBannerName());
        if(request.getDesType() != null){
            if(request.getDesType().intValue() == 0){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.WEB);
                banner.setWebUrl(request.getDestinationText());
            }
            if(request.getDesType().intValue() == 1){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.CATEGORY);
                banner.setCategoryId(new Long(request.getDestinationText().trim()));
            }
            if(request.getDesType().intValue() == 2){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.POST_ID);
                banner.setPostIds(request.getDestinationText());
            }
            if(request.getDesType().intValue() == 3){
                banner.setDestinationType(ShtBanner.DestinationTypeEnum.KEYWORD);
                banner.setKeywords(request.getDestinationText());
            }
        }

        // check build banner page
        if(request.getUrlType() != null){
            if(request.getUrlType().intValue() == 0){
                banner.setIsBuildPage(false);
            }else {
                banner.setIsBuildPage(true);
                if(request.getPageId() == null){
                    // store banner page
                    ShtBannerPage bannerPage = bannerPageService.storeBannerPage(request,imagePage);
                    bannerPage.setIsActive(true);
                    bannerPageService.saveBannerPage(bannerPage);

                    banner.setBannerPageId(bannerPage.getPageId());
                    banner.setWebUrl(bannerPageService.buildPageUrl(bannerPage.getPageId()));
                }else {
                    // active banner page
                    ShtBannerPage bannerPage = bannerPageService.getBannerPageById(request.getPageId());
                    if(bannerPage != null){
                        bannerPage.setIsActive(true);
                        bannerPage.setPageTitle(request.getPageTitle());
                        bannerPage.setPageContent(request.getPageContent());
                        bannerPage.setTitleColor(request.getTitleColor());
                        bannerPage.setBackgroundColor(request.getBackgroundColor());
                        bannerPage.setPageTitlePrv(request.getPageTitle());
                        bannerPage.setPageContentPrv(request.getPageContent());
                        bannerPage.setTitleColorPrv(request.getTitleColor());
                        bannerPage.setBackgroundColorPrv(request.getBackgroundColor());
                        ShtBannerPage bannerPageSaved = bannerPageService.saveBannerPage(bannerPage);
                        if(request.getIsChangeImagePage() != null && request.getIsChangeImagePage().intValue() == 1 && imagePage != null){
                            bannerPageService.updateBannerPageImage(bannerPageSaved, imagePage);
                        }
                        banner.setBannerPageId(request.getPageId());
                        banner.setWebUrl(bannerPageService.buildPageUrl(bannerPage.getPageId()));
                    }
                }   
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime fromDate = LocalDateTime.parse(request.getFromDate(), formatter);
        banner.setFromDate(fromDate);
        LocalDateTime toDate = LocalDateTime.parse(request.getToDate(), formatter);
        banner.setToDate(toDate);
        // edit todate change banner status to suspended
        if(toDate.isAfter(LocalDateTime.now()) && banner.getBannerStatus() == ShtBanner.BannerStatusEnum.EXPIRED){
            banner.setBannerStatus(ShtBanner.BannerStatusEnum.SUSPENDED);
        }
        ShtBanner bannerSaved = bannerService.saveBanner(banner);
        // store image
        if(request.getIsChangeImage().intValue() == 1){
            ShrFile image = bannerService.saveBannerImage(bannerSaved.getBannerId(), bannerImage);
            bannerSaved.setImage(image);
            bannerService.saveBanner(bannerSaved);
        }

        // setting cron job
        bannerService.settingSchedulerBanner(bannerSaved);

        return "redirect:/backend/list-banner?pageNumber=0";
    }

    @RequestMapping(value = "/suspend-banner", method = RequestMethod.PUT)
    public @ResponseBody BannerValidator suspendBanner(Model model, @RequestBody BannerRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        BannerValidator validator = new BannerValidator();
        if(request.getBannerId() != null){
            Long activeBannerNumber = bannerService.countBannerActive();
            ShtBanner banner = bannerService.getBannerById(request.getBannerId());
            if(banner.getBannerStatus() == ShtBanner.BannerStatusEnum.EXPIRED){
                validator.setError(1);
                validator.setErrorMsg("この特集バナーの表示期間が終了しましたので、終了時間を修正してください");
                return validator;
            }
            if(activeBannerNumber > 2 && banner.getBannerStatus() == ShtBanner.BannerStatusEnum.SUSPENDED){
                validator.setError(1);
                validator.setErrorMsg("最大3件しか表示設定できないため、表示中のものを１つ以上非表示にしてください");
                return validator;
            }
            if(banner.getBannerStatus() == ShtBanner.BannerStatusEnum.SUSPENDED){
                if(banner.getFromDate().isAfter(LocalDateTime.now())){
                    validator.setError(1);
                    validator.setErrorMsg("掲載日時前で「表示」に変更できません。");
                    return validator;
                }
            }
            if(banner.getBannerStatus() == ShtBanner.BannerStatusEnum.SUSPENDED){
                banner.setBannerStatus(ShtBanner.BannerStatusEnum.ACTIVE);
            }else if(banner.getBannerStatus() == ShtBanner.BannerStatusEnum.ACTIVE){
                banner.setBannerStatus(ShtBanner.BannerStatusEnum.SUSPENDED);
            }
            bannerService.saveBanner(banner);
        }
        return validator;
    }

    @RequestMapping(value = "/banner-page", method = RequestMethod.GET)
    public String redirectToBannerPage(Model model, @RequestParam("pageId") Long pageId) {
        ShtBannerPage bannerPage = bannerPageService.getBannerPageById(pageId);
        if(!bannerPage.getIsActive()){
            return "";
        }
        String title = bannerPage.getPageTitle();
        String content = bannerPage.getPageContent();
        String imageUrl = bannerPage.getImageUrl();
        String titleColor = bannerPage.getTitleColor();
        String backgroundColor = bannerPage.getBackgroundColor();
        String displayTitle = "block";
        if(StringUtils.isEmpty(title) || StringUtils.isEmpty(title.trim())){
            displayTitle = "none";
        }
        model.addAttribute("title", title);
//        model.addAttribute("titleColor", titleColor);
        model.addAttribute("titleDisplay", displayTitle);
        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("content",content);
        model.addAttribute("backgroundColor", backgroundColor);

        return "banner-page";
    }

    @RequestMapping(value = "/preview-banner-page", method = RequestMethod.GET)
    public String previewBannerPage(Model model, @RequestParam("pageId") Long pageId) {
        ShtBannerPage bannerPage = bannerPageService.getBannerPageById(pageId);
        String title = bannerPage.getPageTitlePrv();
        String content = bannerPage.getPageContentPrv();
        String imageUrl = bannerPage.getImageUrlPrv();
        String titleColor = bannerPage.getTitleColorPrv();
        String backgroundColor = bannerPage.getBackgroundColorPrv();
        String displayTitle = "block";
        if(StringUtils.isEmpty(title) || StringUtils.isEmpty(title.trim())){
            displayTitle = "none";
        }
        model.addAttribute("title", title);
//        model.addAttribute("titleColor", titleColor);
        model.addAttribute("titleDisplay", displayTitle);
        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("content",content);
        model.addAttribute("backgroundColor", backgroundColor);

        return "preview-banner-page";
    }

}
