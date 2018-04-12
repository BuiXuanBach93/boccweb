package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.PostBodyEdit;
import jp.bo.bocc.controller.api.request.PostBodyRequest;
import jp.bo.bocc.controller.api.response.UserReviewCount;
import jp.bo.bocc.controller.web.request.PostPatrolRequest;
import jp.bo.bocc.controller.web.request.PostSearchRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.PostCsvDTO;
import jp.bo.bocc.entity.dto.PostPatrolCsvDTO;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.entity.mapper.PostMapper;
import jp.bo.bocc.enums.ImageSavedEnum;
import jp.bo.bocc.enums.PatrolActionEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.FileUtils;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.repository.*;
import jp.bo.bocc.repository.criteria.BaseSpecification;
import jp.bo.bocc.repository.criteria.SearchCriteria;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haipv on 3/14/2017.
 */
@Service
public class PostServiceImpl implements PostService {

    private final static Logger LOGGER = Logger.getLogger(PostServiceImpl.class.getName());

    private final PostRepository postRepository;

    private static final String USER_AGENT_IPHONE_TYPE = "iPhone";
    private static final String USER_AGENT_ANDROID_TYPE = "Android";
    private static final String USER_AGENT_BOCC_APP_TYPE = "WorkerMarketClient";
    private static final String USER_AGENT_ANDROID_V1_TYPE = "okhttp";

    @Autowired
    private CategoryRepository categoryRepos;
    @Autowired
    private TalkPurcMsgService talkPurcMsgService;

    @Autowired
    private CategorySettingService categorySettingService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private UserRprtRepository userRprtRepository;
    @Autowired
    private AdminPostRepository adminPostRepository;

    @Autowired
    private UserSettingService userSettintService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Autowired
    AddressService addressService;
    @Autowired
    FileService fileService;
    @Autowired
    UserService userService;
    @Autowired
    ImageService imageService;
    @Autowired
    PostService postService;
    @Autowired
    ImageBuilder imageBuilder;
    @Autowired
    TalkPurcService talkPurcService;
    @Autowired
    UserRprtService userRprtService;
    @Autowired
    UserFvrtService userFvrtService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserRevService userRevService;
    @Autowired
    AdminNgService adminNgService;
    @Autowired
    AdminService adminService;
    @Autowired
    AdminLogService adminLogService;
    @Autowired
    MailService mailService;
    @Autowired
    QaService qaService;
    @Autowired
    StorageService storageService;

    @Autowired
    TalkPurcMsgRepository talkPurcMsgRepository;

    @Autowired
    MemoPostService memoPostService;

    @Autowired
    private SNSMobilePushService snsMobilePushService;

    @Autowired
    ExportCsvService exportCsvService;

    @Autowired
    AdminCsvHstService adminCsvHstService;

//    @Autowired
//    FireBaseService fireBaseService;

    @Value("${image.server.url}")
    private String imgServer;

    @Override
    public ShmPost showPostPatrolDetailById(long postId) throws ParseException, SchedulerException {
        ShmPost searchPost = postRepository.findOne(postId);
        // add parent category
        ShmCategory shmCategory = searchPost.getPostCategory();
        if(shmCategory != null && shmCategory.getCategoryParentId() != null){
            ShmCategory parentCategory = categoryService.get(shmCategory.getCategoryParentId());
            searchPost.setPostCategoryParent(parentCategory);
        }

        // count report times
        Long reportTimes = userRprtService.countTotalReportByPostId(postId);
        searchPost.setPostReportTimes(reportTimes);

        return searchPost;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShmPostDTO> searchPost(Pageable pageable, String textSearch,
                                       String postType, Long addrProvinceId,
                                       Long addrDistrictId, boolean isSameCompany,
                                       Long userId, Long categoryIdSuper, Long categoryIdChild,
                                       String sortField, Long price, String userAgent, Long categorySettingId, Long bannerId) {
        String companyCode = buildCompanyCode(userId);
        boolean isPcWeb = true;
        /* Check if user_agent which send from PCWeb. CompanyCode = null */
        if (StringUtils.isNotEmpty(userAgent)) {
            if (userAgent.contains(USER_AGENT_ANDROID_TYPE)) {
                isPcWeb = false;
            }
            if (userAgent.contains(USER_AGENT_IPHONE_TYPE)) {
                isPcWeb = false;
            }
            if (userAgent.contains(USER_AGENT_BOCC_APP_TYPE)) {
                isPcWeb = false;
            }
            if (userAgent.contains(USER_AGENT_ANDROID_V1_TYPE)) {
                isPcWeb = false;
            }
            companyCode = isPcWeb == true ? null : companyCode;
        }

        // get group publish of current user
        Long groupId = null;
        List<ShtGroupPblDetail> groupPblDetails = userService.getGroupPblDetails(companyCode);
        if(groupPblDetails.size() > 0 && groupPblDetails.get(0).getGroupPublish() != null){
            groupId = groupPblDetails.get(0).getGroupPublish().getGroupId();
        }

        // validate category setting
        if(categorySettingId != null){
            ShtCategorySetting categorySetting = categorySettingService.getCategorySettingById(categorySettingId);
            if(categorySetting == null){
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100162",null,null));
            }
            if(categorySetting.getCategoryStatus() == ShtCategorySetting.CategoryStatusEnum.SUSPENDED){
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100162",null,null));
            }
        }

        // validate banner
        if(bannerId != null){
            ShtBanner banner = bannerService.getBannerById(bannerId);
            if(banner == null){
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100163",null,null));
            }
            if(banner.getBannerStatus() != ShtBanner.BannerStatusEnum.ACTIVE){
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100163",null,null));
            }
        }

        List<String> listHashTag = null;
        String postNameAfterDecode = textSearch;
        if (StringUtils.isNotEmpty(textSearch)) {
            textSearch = textSearch.toUpperCase();
            try {
                textSearch = postNameAfterDecode = URLDecoder.decode(textSearch.toUpperCase(), "UTF-8");
            } catch (Exception e) {
                LOGGER.warn("WARNING: " + e.getMessage());
            }
            listHashTag = jp.bo.bocc.helper.StringUtils.getHashTagValue(postNameAfterDecode);
            if (CollectionUtils.isNotEmpty(listHashTag))
                textSearch = null;
        }
        ShmPost post = null;
        List<Long> listDistrictIds = new ArrayList<>();
        if (addrProvinceId != null && addrDistrictId == null) {
            listDistrictIds = addressService.getListDistrictIdByProvinceId(addrProvinceId);
        }
        Page<ShmPost> resultWithOutFile = postRepository.searchPost(pageable, listHashTag, textSearch, postType, listDistrictIds, addrDistrictId, companyCode, categoryIdSuper, categoryIdChild, sortField, price, isSameCompany, groupId, userId, categorySettingId, bannerId);
        List<ShmPost> listPost = addExtralInfo(resultWithOutFile.getContent(), userId, ImageSavedEnum.THUMBNAIL);
        buildImageForListPost(userId, listPost);
        Page<ShmPost> result = new PageImpl<ShmPost>(listPost, pageable, resultWithOutFile.getTotalElements());

        Page<ShmPostDTO> shmPostDTOS = PostMapper.mapEntityPageIntoDTOPage(pageable, result);
        return shmPostDTOS;
    }

    private List<ShmPost> addExtralInfo(List<ShmPost> resultWithOutFile, Long userId, ImageSavedEnum thumbnail) {
        List<ShmPost> listPost = new ArrayList<>();
        for (ShmPost shmPost : resultWithOutFile) {
            final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(shmPost.getPostImages(), thumbnail);
            for (Long id : allImageIdByType) {
                ShrFile shrFile = fileService.get(id);
                if (shrFile != null)
                    shmPost.getPostThumbnailImages().add(shrFile);
            }
            shmPost.setOwnedByCurrentUser(false);
            ShmUser shmUser = shmPost.getShmUser();
            if(shmUser != null && userId != null && userId.intValue() == shmUser.getId().intValue()){
                shmPost.setOwnedByCurrentUser(true);
            }
            shmPost.setCurrentUserFavoriteStatus(ShtUserFvrt.UserFavoriteEnum.NONE);
            shmPost.setReportedByCurrentUser(false);
            ShtUserFvrt userFvrt = null;
            ShtUserRprt shtUserRprt = null;
            if (userId != null) {
                userFvrt = userFvrtService.getUserFvrt(shmPost.getPostId(), userId);
                shtUserRprt = userRprtService.getUserRprt(shmPost.getPostId(), userId);
            }
            if (userFvrt != null && userFvrt.getUserFvrtStatus() == ShtUserFvrt.UserFavoriteEnum.LIKED) {
                shmPost.setCurrentUserFavoriteStatus(ShtUserFvrt.UserFavoriteEnum.LIKED);
            }
            if(shtUserRprt != null){
                shmPost.setReportedByCurrentUser(true);
            }
            listPost.add(shmPost);
        }
        return listPost;
    }


    @Override
    public List<ShmPost> getShmPostImages(List<ShmPost> resultWithOutFile, ImageSavedEnum imageSavedEnum) {
        List<ShmPost> listPost = new ArrayList<>();
        List<ShrFile> files;
        ShrFile shrFile;
        for (ShmPost shmPost : resultWithOutFile) {
            final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(shmPost.getPostImages(), imageSavedEnum);
            files = imageSavedEnum.equals(ImageSavedEnum.ORIGINAL) ? shmPost.getPostOriginalImages() : shmPost.getPostThumbnailImages();
            for (Long id : allImageIdByType) {
                if (id != null) {
                    shrFile = fileService.get(id);
                    files.add(shrFile);
                }
            }
            listPost.add(shmPost);
        }
        return listPost;
    }

    @Transactional(readOnly = true)
    @Override
    public ShmPost getPost(long id) {
        try {
            ShmPost shmPost = postRepository.findOne(id);
            if (shmPost != null) {
            final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.ORIGINAL);
            for (Long fileId : allImageIdByType) {
                ShrFile shrFile = fileService.get(fileId);
                shmPost.getPostOriginalImages().add(shrFile);
            }
            return shmPost;
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            throw e;
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public ShmPost getPost(long id, ShmUser shmUser) {
        ShmPost shmPost = getPost(id);

        // add parent address
        ShmAddr shmAddr = shmPost.getShmAddr();
        if(shmAddr != null && shmAddr.getAddrParentId() != null){
            ShmAddr parentAddr = addressService.getAddress(shmAddr.getAddrParentId());
            shmPost.setShmAddrParent(parentAddr);
        }

        // add province for user
        ShmUser owner = shmPost.getShmUser();
        if(owner != null && owner.getAddress() != null){
            Long parentAddrId = owner.getAddress().getAddrParentId();
            ShmAddr parentAddr = null;
            if(parentAddrId != null){
                parentAddr = addressService.getAddress(parentAddrId);
            }else {
                parentAddr = owner.getAddress();
            }
            owner.setProvince(parentAddr);
        }

        // add parent category
        ShmCategory shmCategory = shmPost.getPostCategory();
        if(shmCategory != null && shmCategory.getCategoryParentId() != null){
            ShmCategory parentCategory = categoryService.get(shmCategory.getCategoryParentId());
            shmPost.setPostCategoryParent(parentCategory);
        }

        // check is first talk purc
        Long userId = null;
        Boolean isFirstTalkPurc = null;
        if (shmUser != null) {
            userId=shmUser.getId();
            isFirstTalkPurc = talkPurcService.isFirstPurchase(id, userId);
            shmPost.setIsFirstTalkPurc(isFirstTalkPurc);
            if (!isFirstTalkPurc) {
                Long talkPurcId = talkPurcService.findTalkPurcByPostIdAndPartnerId(shmPost.getPostId(), shmUser.getId());
                shmPost.setTalkPurcId(talkPurcId);
            }

            // check this post was owned by current user
            Boolean isOwnedByCurrentUser = false;
            Long ownerUserId = shmPost.getShmUser().getId();
            if(userId.intValue() == ownerUserId.intValue()){
                isOwnedByCurrentUser = true;
            }
            shmPost.setOwnedByCurrentUser(isOwnedByCurrentUser);
            /* Check current user and other user have same company with conditions */
            if (StringUtils.isNotEmpty(shmUser.getBsid()) && StringUtils.isNotEmpty(shmPost.getShmUser().getBsid())
                    && shmUser.getBsid().length() > 6 && shmPost.getShmUser().getBsid().length() > 6
                    && shmUser.getBsid().substring(0, 6).equals(shmPost.getShmUser().getBsid().substring(0, 6))) {
                shmPost.getShmUser().setIsSameCompany(true);
                // owner post, not set same company
                if(shmUser.getId().intValue() == shmPost.getShmUser().getId().intValue()){
                    shmPost.getShmUser().setIsSameCompany(false);
                }
            }

            // check post was liked, reported by current user
            ShtUserFvrt shtUserFvrt = userFvrtService.getUserFvrt(id, userId);
            ShtUserRprt shtUserRprt = userRprtService.getUserRprt(id, userId);
            if (shtUserFvrt != null && shtUserFvrt.getUserFvrtStatus() == ShtUserFvrt.UserFavoriteEnum.LIKED) {
                shmPost.setCurrentUserFavoriteStatus(ShtUserFvrt.UserFavoriteEnum.LIKED);
            }else{
                shmPost.setCurrentUserFavoriteStatus(ShtUserFvrt.UserFavoriteEnum.NONE);
            }
            if (shtUserRprt != null) {
                shmPost.setReportedByCurrentUser(true);
            } else {
                shmPost.setReportedByCurrentUser(false);
            }

            shmPost.setCurrentUserCtrlStatus(shmUser.getCtrlStatus());
            shmPost.setCurrentUserStatus(shmUser.getStatus());

            // count new msg
            Integer newMsgNumber = 0;
            newMsgNumber = talkPurcMsgService.countNewMsgForAllTalkByPostIdForOwnerPost(shmPost.getPostId(),userId);
            shmPost.setNewMsgNumber(new Long(newMsgNumber));

        }else{
            shmPost.setOwnedByCurrentUser(false);
            shmPost.getShmUser().setIsSameCompany(false);
        }

        // count review
        UserReviewCount reviewCount = new UserReviewCount();
        Long goodNumber = userRevService.countReviewByUserId(shmPost.getShmUser().getId(), ShtUserRev.UserReviewRate.GOOD);
        Long fairNumber = userRevService.countReviewByUserId(shmPost.getShmUser().getId(), ShtUserRev.UserReviewRate.FAIR);
        Long badNumber = userRevService.countReviewByUserId(shmPost.getShmUser().getId(), ShtUserRev.UserReviewRate.BAD);
        reviewCount.setGoodTypeNumber(goodNumber);
        reviewCount.setFairTypeNumber(fairNumber);
        reviewCount.setBadTypeNumber(badNumber);
        shmPost.setUserReviewCount(reviewCount);

        //count liked times
        Long postLikedTimes = userFvrtService.countLikeTimeByPostId(shmPost.getPostId());
        shmPost.setPostLikeTimes(postLikedTimes);

        // add thumb
        final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.THUMBNAIL);
        for (Long fileId : allImageIdByType) {
            ShrFile shrFile = fileService.get(fileId);
            if (shrFile != null)
                shmPost.getPostThumbnailImages().add(shrFile);
        }

        // count post of post owner
        Long postsNumber = postService.countPostByUserId(shmPost.getShmUser().getId());
        shmPost.getShmUser().setPostNumber(postsNumber);

        // count talkpurcs
        Long talkPurcsNumber = talkPurcService.countTalkByPostId(shmPost.getPostId());
        shmPost.setTalkPurcsNumber(talkPurcsNumber);

        return shmPost;
    }

    @Override
    public ShmPost editPost(ShmPost shmPost, PostBodyEdit postBodyEdit) {
        if(shmPost == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100082", null, null));
        }
        if(shmPost.getPostSellStatus() == ShmPost.PostSellSatus.TEND_TO_SELL && shmPost.getPostCtrlStatus() != ShmPost.PostCtrlStatus.SUSPENDED){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100016",null, null));
        }
        if(shmPost.getPostSellStatus() == ShmPost.PostSellSatus.SOLD){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100017",null, null));
        }
        if(postBodyEdit.getPostName() != null
                && postBodyEdit.getPostName().length() > 40){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100007",null,null));
        }
        if(postBodyEdit.getPostDescription() != null
                && postBodyEdit.getPostDescription().length() > 1000){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100010",null,null));
        }
        if (postBodyEdit.getPostHashTagVal() != null) {
            String[] arrHashTags = postBodyEdit.getPostHashTagVal().split(",");
            if (arrHashTags.length > 3) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100013",null,null));
            }
        }

        // check to push notification for users, who liked post
        boolean isDownPrice = false;
        boolean isChangeAddress = false;
        if(postBodyEdit.getPostPrice() != null && postBodyEdit.getPostPrice().intValue() < shmPost.getPostPrice().intValue()){
            isDownPrice = true;
        }
        if(postBodyEdit.getPostAddr() != null && postBodyEdit.getPostAddr().intValue() != shmPost.getShmAddr().getAddressId().intValue()){
            isChangeAddress = true;
        }

        // check NG Words
        String nGWordStr = "";
        List<String> listNgWords = adminNgService.checkNGContent(postBodyEdit.getPostName());
        List<String> listNgWordsInDes = adminNgService.checkNGContent(postBodyEdit.getPostDescription());
        for (String ngW: listNgWordsInDes) {
            if(!listNgWords.contains(ngW)){
                listNgWords.add(ngW);
            }
        }
        for (String ngW: listNgWords) {
            nGWordStr += ngW +",";
        }
        if (!StringUtils.isEmpty(nGWordStr)) {
            nGWordStr = nGWordStr.substring(0, nGWordStr.length() - 1);
        }
        if(!StringUtils.isEmpty(nGWordStr)){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100042", new Object[]{nGWordStr}, null));
        }

        if(postBodyEdit.getPostCategoryId() != null){
            ShmCategory category = categoryService.get(postBodyEdit.getPostCategoryId());
            if(category != null){
                shmPost.setPostCategory(category);
            }
        }

        /* check destination publish type is already ALL, the system not allow to edit. */
        if (shmPost.getDestinationPublishType() != null && ShmPost.DestinationPublishType.ALL.equals(shmPost.getDestinationPublishType())
                && StringUtils.isNotEmpty(postBodyEdit.getDestinationPublishType()) && !postBodyEdit.getDestinationPublishType().equalsIgnoreCase(shmPost.getDestinationPublishType().toString())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100160", null, null));
        }
        /* add destination publish type */
        if (StringUtils.isNotEmpty(postBodyEdit.getDestinationPublishType())) {

            if(postBodyEdit.getDestinationPublishType().equals(ShmPost.DestinationPublishType.ALL.toString())){
                shmPost.setDestinationPublishType(ShmPost.DestinationPublishType.ALL);
            }
            if(postBodyEdit.getDestinationPublishType().equals(ShmPost.DestinationPublishType.PRIVATE.toString())){
                shmPost.setDestinationPublishType(ShmPost.DestinationPublishType.PRIVATE);
            }
            if(postBodyEdit.getDestinationPublishType().equals(ShmPost.DestinationPublishType.GROUP.toString())){
                List<ShtGroupPblDetail> groupPblDetails = userService.getGroupPblDetails(buildCompanyCode(shmPost.getShmUser().getId()));
                if(groupPblDetails.size() > 0 && groupPblDetails.get(0).getGroupPublish() != null){
                    shmPost.setGroupId(groupPblDetails.get(0).getGroupPublish().getGroupId());
                    shmPost.setDestinationPublishType(ShmPost.DestinationPublishType.GROUP);
                }else{
                    throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100161", null, null));
                }
            }
        }

        if(postBodyEdit.getPostAddr() != null){
            Long addr = postBodyEdit.getPostAddr();
            ShmAddr shmAddr = addressService.getAddress(addr);
            shmPost.setShmAddr(shmAddr);
            shmPost.setPostAddrTxt(addressService.getFullAddress(shmAddr));
        }

        if(postBodyEdit.getPostPrice() != null){
            shmPost.setPostPrice(postBodyEdit.getPostPrice());
        }
        shmPost.setPostName(postBodyEdit.getPostName());
        shmPost.setPostDescription(postBodyEdit.getPostDescription());
        shmPost.setPostHashTagVal(postBodyEdit.getPostHashTagVal());

        // handle image edit
        final List<Long> imageOriginIds = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.ORIGINAL);
        List<ShrFile> listOriginImages = new ArrayList<>();
        for (Long fileId : imageOriginIds) {
            ShrFile shrFile = fileService.get(fileId);
            if (shrFile != null)
                listOriginImages.add(shrFile);
        }
        final List<Long> imageThumbIds = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.THUMBNAIL);
        List<ShrFile> listThumbImages = new ArrayList<>();
        for (Long fileId : imageThumbIds) {
            ShrFile shrFile = fileService.get(fileId);
            if (shrFile != null)
                listThumbImages.add(shrFile);
        }
        // handle delete image
        if(CollectionUtils.isNotEmpty(postBodyEdit.getPostRemoveImagesUrl())){
            handleDeletePostIamge(postBodyEdit.getPostRemoveImagesUrl(),shmPost,listOriginImages);
            handleDeletePostIamge(postBodyEdit.getPostRemoveImagesUrl(),shmPost,listThumbImages);
        }

        List<ShrImage> images = generatePostImage(listOriginImages, listThumbImages);
        String postImageJson = "[]";
        if(CollectionUtils.isNotEmpty(images)){
            postImageJson = FileUtils.buildImageJson(images);
        }

        // handle add image
        if(CollectionUtils.isNotEmpty(postBodyEdit.getPostAddImagesBase64())){
            postImageJson = handleAddPostImage(postBodyEdit.getPostAddImagesBase64(),shmPost,images);
        }
        // update image json
        if(CollectionUtils.isNotEmpty(postBodyEdit.getPostAddImagesBase64()) || CollectionUtils.isNotEmpty(postBodyEdit.getPostRemoveImagesUrl())){
            if(StringUtils.isEmpty(postImageJson)){
                postImageJson = "[]";
            }
            shmPost.setPostImages(postImageJson);
        }

        /*
        // handle logic update status
        if(shmPost.getPostPtrlStatus() == ShmPost.PostPtrlStatusEnum.CENSORING){
            shmPost.setPostCtrlStatus(ShmPost.PostCtrlStatus.UPDATE_AFTER_CENSORING);
            // send msg to talk if post be restore
            try{
                List<ShtTalkPurc> talkPurcs = talkPurcService.findTalksByPostIdAndStatus(shmPost, ShtTalkPurc.TalkPurcStatusEnum.TEND_TO_SELL);
                if(talkPurcs != null){
                    for (ShtTalkPurc talkPurc: talkPurcs) {
                        ShtTalkPurcMsg msg = createMsgForPartner(talkPurc);
                        talkPurcMsgService.saveMsg(msg);
                    }
                }
            }catch (Exception ex){
                LOGGER.error(ex.getMessage());
            }

            // send mail to user
            if(shmPost.getShmUser() != null){
                ShmUser shmUser = shmPost.getShmUser();
                mailService.sendEmailRestorePost(shmUser.getEmail(),shmUser.getNickName(),shmPost.getPostName());
            }

            // push notify broadcast
            String msgContent= messageSource.getMessage("PUSH_RESTORE_POST",null,null);
            PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.POST_HANDLE_OK, null, null, null, shmPost.getPostId(), null);
            snsMobilePushService.sendNotificationForUser(shmPost.getShmUser().getId(), pushMsg, pushMsgDetail);

            // send notify to mailbox
            if(shmPost.getShmUser() != null){
                ShmUser shmUser = shmPost.getShmUser();
                ShtQa qa = new ShtQa();
                qa.setQaTitle(messageSource.getMessage("QA_TITLE_RESTORE_POST",null,null));
                qa.setFirstQaMsg(messageSource.getMessage("QA_CONTENT_RESTORE_POST", new Object[]{shmUser.getNickName()}, null).replace("<postName>",shmPost.getPostName()));
                qaService.createNotifyFromAdmin(shmUser,qa);
            }

        }*/
        if(shmPost.getPostPtrlStatus() == ShmPost.PostPtrlStatusEnum.CENSORED && shmPost.getPostCtrlStatus() == ShmPost.PostCtrlStatus.ACTIVE){
            shmPost.setPostPtrlStatus(ShmPost.PostPtrlStatusEnum.UNCENSORED);
        }

        shmPost.setUserUpdateAt(LocalDateTime.now());
        ShmPost postEdited = postService.savePost(shmPost);

        // push notification to uses, who liked this post
        List<ShtUserFvrt> listUsesFvrt = userFvrtService.getUserFvrts(shmPost.getPostId());
        String msgContent = messageSource.getMessage("PUSH_POST_LIKED_CHANGE_SOMTHING",null,null);
        if(isChangeAddress){
            msgContent = messageSource.getMessage("PUSH_POST_LIKED_CHANGE_ADDR",null,null);
        }
        if(isDownPrice){
            msgContent = messageSource.getMessage("PUSH_POST_LIKED_DOWN_PRICE",null,null);
        }
        for (ShtUserFvrt userFvrt: listUsesFvrt) {
            final boolean checkReceivePushOn = userSettintService.checkReceivePushOn(userFvrt.getShmUser().getId());
            final boolean checkReceivepushFavorite = userSettintService.checkReceivepushFavorite(userFvrt.getShmUser().getId());
            if (checkReceivePushOn)
                if (checkReceivepushFavorite)
                    snsMobilePushService.sendNotificationForUserLikedPost(PushMsgDetail.NtfTypeEnum.POST_LIKED_CHANGE_INFO, userFvrt.getShmUser().getId(), shmPost.getPostId(), msgContent);
        }

        return postEdited;
    }

    private List<ShrImage> generatePostImage(List<ShrFile> listOrigin, List<ShrFile> listThumb){
        List<ShrImage> images = new ArrayList<ShrImage>();
        if(CollectionUtils.isEmpty(listOrigin) || CollectionUtils.isEmpty(listThumb)){
            return null;
        }
        for (ShrFile origin: listOrigin) {
            for (ShrFile thumb : listThumb) {
                if(origin.getDeleteFlag() == false && thumb.getDeleteFlag() == false && origin.getName().equals(thumb.getName())){
                    ShrImage image = new ShrImage(origin,thumb);
                    images.add(image);
                    break;
                }
            }
        }
        return images;
    }

    private String handleAddPostImage(List<String> imageBase64, ShmPost shmPost, List<ShrImage> images){
        for (String imageStrBase64 : imageBase64) {
            if(images == null){
                images = new ArrayList<>();
            }
            String imageDir = imageBuilder.buildPostImageDir(shmPost.getPostId());
            String imageName = imageBuilder.getName();
            byte[] imgBytes = jp.bo.bocc.helper.StringUtils.base64Decode(imageStrBase64);
            InputStream inputStream = new ByteArrayInputStream(imgBytes);
            ShrImage image = imageService.saveImage(imageDir, imageName, imageBuilder.getImageFormat(), inputStream);
            images.add(image);
        }
        return  FileUtils.buildImageJson(images);
    }

    private void handleDeletePostIamge(List<String> imageUrls, ShmPost shmPost, List<ShrFile> listImages){
        if(CollectionUtils.isEmpty(imageUrls)){
            return;
        }
        // delete file
        for (String imageUrl: imageUrls) {
            for (ShrFile fileImage: listImages) {
                if(imageUrl.contains(fileImage.getName())){
                    fileImage.setDeleteFlag(true);
                    fileService.delete(fileImage.getId());
                }
            }
        }
    }

    private ShtTalkPurcMsg createMsgForPartner(ShtTalkPurc talkPurc){
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        ShmPost shmPost = talkPurc.getShmPost();
        if(shmPost != null){
            ShmUser shmUser = talkPurc.getShmPost().getShmUser();
            if(shmUser != null){
                msg.setShmUserCreator(talkPurc.getShmPost().getShmUser());
                msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_NG_MSG_FOR_PARTNER);
                msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10014", null,null).replace("<nickName>",shmUser.getNickName()).replace("<postName>",shmPost.getPostName()));
            }
        }
        return msg;
    }

    private String buildCompanyCode(Long userId) {
        if (userId != null) {
            String bsid = userService.getUserById(userId).getBsid();
            if (StringUtils.isNotEmpty(bsid) && bsid.length() > 5)
                return bsid.substring(0, 6);
        }
        return null;
    }

    @Override
    public ShmPost savePost(ShmPost shmPost) {
        return postRepository.save(shmPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShmPost> getPostByUserId(Long userId, String bsid, Pageable pageable) {
        String owner_bsid = userService.getUserById(userId).getBsid();

        // get group publish of current user
        Long groupId = null;
        if(StringUtils.isNotEmpty(bsid) && bsid.length() > 6){
            List<ShtGroupPblDetail> groupPblDetails = userService.getGroupPblDetails(bsid.substring(0,6));
            if(groupPblDetails.size() > 0 && groupPblDetails.get(0).getGroupPublish() != null){
                groupId = groupPblDetails.get(0).getGroupPublish().getGroupId();
            }
        }

        Page<ShmPost> posts = null;
        if (StringUtils.isNotEmpty(bsid) && bsid.length() > 6 && owner_bsid.length() > 6) {
            posts = postRepository.getPostByUserId(userId, groupId, bsid.substring(0, 6), pageable);
        }else{
            posts = postRepository.getPostByUserId(userId, null, null, pageable);
        }
        List<ShmPost> listPost = addExtralInfo(posts.getContent(),userId,ImageSavedEnum.THUMBNAIL);
        Page<ShmPost> result = new PageImpl<ShmPost>(listPost, pageable, posts.getTotalElements());
        return result;
    }

    private PostSearchRequest convertSellSttToList(PostSearchRequest request) {

        List<Short> sellStt = new ArrayList<>();
        if (request.getPubSellStt() != null)
            sellStt.add(request.getPubSellStt());
        if (request.getInCnvStt() != null)
            sellStt.add(request.getInCnvStt());
        if (request.getTendSellStt() != null)
            sellStt.add(request.getTendSellStt());
        if (request.getSoldStt() != null)
            sellStt.add(request.getSoldStt());
        if (request.getDelStt() != null)
            sellStt.add(request.getDelStt());

        request.setPostSellStt(sellStt);
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShmPost> searchPostByConditions(PostSearchRequest postRequest, Pageable pageable) {
        try {
            convertSellSttToList(postRequest);
            BaseSpecification postIdSpec = null;
            if (!org.apache.commons.lang.StringUtils.isEmpty(postRequest.getPostId())) {
                List<Long> result = new ArrayList<>();
                String[] array = postRequest.getPostId().split(",");
                for (String item : array) {
                    if (StringUtils.isEmpty(item)) {
                        continue;
                    }
                    result.add(Long.parseLong(item));
                }
                if (result.size() > 0)
                    postIdSpec = new BaseSpecification(new SearchCriteria("postId", "in", result));
            }

            BaseSpecification userNameSpec = null;
            final String userName = postRequest.getUserName();
            if (!org.apache.commons.lang.StringUtils.isEmpty(userName)) {
                List<ShmUser> users = new ArrayList<>();
                List<String> firstNameLastNameList = new ArrayList<>();
                String[] subListUserName1 = null;
                if (userName.contains(",") || userName.contains("、")) {
                    subListUserName1 = userName.split("[,+、+]");
                    if (subListUserName1 != null)
                        firstNameLastNameList = Arrays.asList(subListUserName1);
                }else {
                    firstNameLastNameList.add(userName);
                }
                for (String item : firstNameLastNameList) {
                    if (item.contains(" ") || item.contains("　")) {
                        item = item.replace(" ", "").replace("　", "");
                    }
                    final List<ShmUser> userListByFirstNameAndLastName = userService.getUserListByFirstNameAndLastName(item);
                    users.addAll(userListByFirstNameAndLastName);
                }

                if (users.isEmpty()) {
                    return null;
                }
                userNameSpec = new BaseSpecification(new SearchCriteria("shmUser", "in", users));
            }

            BaseSpecification sellTypeSpec = null;
            if (postRequest.getSellType() != null) {
                sellTypeSpec = new BaseSpecification(new SearchCriteria("postType", ":", postRequest.getSellType()));
            }

            BaseSpecification buyTypeSpec = null;
            if (postRequest.getBuyType() != null) {
                buyTypeSpec = new BaseSpecification(new SearchCriteria("postType", ":", postRequest.getBuyType()));
            }

            if (postRequest.getSellType() != null && postRequest.getBuyType() != null) {
                sellTypeSpec = null;
                List<Integer> postTypeList = new ArrayList<>();
                postTypeList.add(ShmPost.PostType.BUY.ordinal());
                postTypeList.add(ShmPost.PostType.SELL.ordinal());
                buyTypeSpec = new BaseSpecification(new SearchCriteria("postType", "in", postTypeList));
            }

            BaseSpecification catParentSpec = null;
            if (postRequest.getParentCat() != null && postRequest.getChildCat() == null) {
                List<ShmCategory> categories = categoryService.findByCategoryParentId(postRequest.getParentCat());
                if (categories.isEmpty())
                    return null;
                List<Long> categoryIds = categories.stream().map(cat -> cat.getCategoryId()).collect(Collectors.toList());
                catParentSpec = new BaseSpecification(new SearchCriteria("postCategory", "in", categoryIds));
            }

            if (postRequest.getChildCat() != null) {
                catParentSpec = new BaseSpecification(new SearchCriteria("postCategory", ":", postRequest.getChildCat()));
            }

            BaseSpecification postSttSpec = null;
            if (postRequest.getPostSellStt().size() > 0) {
                postSttSpec = new BaseSpecification(new SearchCriteria("postSellStatus", "in", postRequest.getPostSellStt()));
            }

            BaseSpecification postSttSpec2 = null;
            if (postRequest.getPostSellStt().size() > 0) {
                postSttSpec2 = new BaseSpecification(new SearchCriteria("postCtrlStatus", "!=", ShmPost.PostCtrlStatus.valueOf("SUSPENDED").ordinal()));
            }
            if (postRequest.getReservedStt() != null) {
                postSttSpec2 = new BaseSpecification(new SearchCriteria("postCtrlStatus", ":", ShmPost.PostCtrlStatus.valueOf("SUSPENDED").ordinal()));
            }

            BaseSpecification postSttSpec3 = null;
            if (!postRequest.getPostSellStt().contains((short)ShmPost.PostSellSatus.DELETED.ordinal())) {
                postSttSpec3 = new BaseSpecification(new SearchCriteria("postSellStatus", "!=", ShmPost.PostSellSatus.DELETED.ordinal()));
            }
            /*When conditions with status greater than one condition and not contain delete status. Ignore case it contain itself*/
            if (postRequest.getPostSellStt().size() > 1 && !postRequest.getPostSellStt().contains((short)ShmPost.PostSellSatus.DELETED.ordinal())) {
                postSttSpec3 = null;
            }
            /*When conditions with status only contain delete flag and reserved status is null*/
            if (postRequest.getPostSellStt().size() == 1 && postRequest.getPostSellStt().contains((short)ShmPost.PostSellSatus.DELETED.ordinal()) && postRequest.getReservedStt() == null) {
                postSttSpec2 = null;
            }

            if (postRequest.getPostSellStt().size() == 0 && postRequest.getReservedStt() == null) {
                postSttSpec2 = null;
                postSttSpec3 = null;
            }

            BaseSpecification reservedAndDelSpec = null;
            if (postRequest.getPostSellStt().size() > 1 && postRequest.getPostSellStt().contains((short)ShmPost.PostSellSatus.DELETED.ordinal()) && postRequest.getReservedStt() == null) {
                List<Long> postIdSuspendAndDelList = postRepository.findPostSuspendAndDelete();
                if (postIdSuspendAndDelList.size() > 0)
                    reservedAndDelSpec = new BaseSpecification(new SearchCriteria("postId", "in", postIdSuspendAndDelList));
            }

            BaseSpecification periodDateSpec = null;
            if (postRequest.getFromDate() != null && postRequest.getToDate() == null) {
                if (postRequest.getDateType() == 1) {
                    periodDateSpec = new BaseSpecification(new SearchCriteria("createdAt", "<>", LocalDateTime.ofInstant(postRequest.getFromDate().toInstant(), ZoneId.systemDefault()),
                            LocalDate.now().atStartOfDay().plusDays(1).minusSeconds(1)));
                } else {
                    periodDateSpec = new BaseSpecification(new SearchCriteria("userUpdateAt", "<>", LocalDateTime.ofInstant(postRequest.getFromDate().toInstant(), ZoneId.systemDefault()),
                            LocalDate.now().atStartOfDay().plusDays(1).minusSeconds(1)));
                }
            }

            if (postRequest.getFromDate() == null && postRequest.getToDate() != null) {
                if (postRequest.getDateType() == 1) {
                    periodDateSpec = new BaseSpecification(new SearchCriteria("createdAt", "=<", LocalDateTime.ofInstant(postRequest.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1)));
                } else {
                    periodDateSpec = new BaseSpecification(new SearchCriteria("userUpdateAt", "=<", LocalDateTime.ofInstant(postRequest.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1)));
                }
            }

            if (postRequest.getDateType() != null && postRequest.getDateType() == 1 && postRequest.getFromDate() != null && postRequest.getToDate() != null) {
                periodDateSpec = new BaseSpecification(new SearchCriteria("createdAt", "<>", LocalDateTime.ofInstant(postRequest.getFromDate().toInstant(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(postRequest.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1)));
            }

            if (postRequest.getDateType() != null && postRequest.getDateType() == 2 && postRequest.getFromDate() != null && postRequest.getToDate() != null) {
                periodDateSpec = new BaseSpecification(new SearchCriteria("userUpdateAt", "<>", LocalDateTime.ofInstant(postRequest.getFromDate().toInstant(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(postRequest.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1)));
            }

            Page all = null;
            if (postRequest.getPostSellStt().size() == 0 || postRequest.getReservedStt() == null || !org.apache.commons.lang.StringUtils.isEmpty(postRequest.getPostId())
                    || !org.apache.commons.lang.StringUtils.isEmpty(postRequest.getUserName()))
                all = postRepository.findAll(Specifications.where(postIdSpec).and(userNameSpec).and(sellTypeSpec).and(buyTypeSpec).and(catParentSpec)
                        .and(postSttSpec).and(postSttSpec2).and(postSttSpec3).or(reservedAndDelSpec).and(periodDateSpec), pageable);
            else
                all = postRepository.findAll(Specifications.where(postIdSpec).and(userNameSpec).and(sellTypeSpec).and(buyTypeSpec)
                        .and(postSttSpec).or(postSttSpec2).and(postSttSpec3).or(reservedAndDelSpec).and(periodDateSpec).and(catParentSpec), pageable);

            List<ShmPost> posts = all.getContent();
            if(posts != null && posts.size() > 0) {
                for (ShmPost post: posts) {
                    try {
                        post.getShmUser().getId();
                    } catch (Exception e) {
                        LOGGER.error("Dirty Data by PostId = " + post.getPostId());
                        continue;
                    }
                    final ShmCategory postCategory = post.getPostCategory();
                    if (postCategory != null) {
                        Long categoryParentId = postCategory.getCategoryParentId();
                        if(categoryParentId != null){
                            final ShmCategory shmCategory = categoryRepos.findOne(categoryParentId);
                            post.setPostCategoryParent(shmCategory);
                            post.setPostReportTimes(getReportTimeForPost(post.getPostId()));
                        }
                    }
                    }
            }
            return all;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public PostBodyRequest createPost(PostBodyRequest postBodyRequest, ShmUser shmUser) throws Exception {
        // validate post info
        validatePostCreateFromClient(postBodyRequest);

        // validate user create post
        userService.checkUserStatus(shmUser);

        ShmPost shmPost = new ShmPost();
        shmPost.setPostName(postBodyRequest.getPostName());

        //Category
        ShmCategory category = categoryService.get(postBodyRequest.getPostCategoryId());
        shmPost.setPostCategory(category);
        shmPost.setPostDescription(postBodyRequest.getPostDescription());
        shmPost.setPostType(postBodyRequest.getPostType());
        shmPost.setPostPrice(postBodyRequest.getPostPrice());
        shmPost.setPostHashTagVal(postBodyRequest.getPostHashTagVal());

        /* add destination publish type */
        if (StringUtils.isEmpty(postBodyRequest.getDestinationPublishType())) {
            shmPost.setDestinationPublishType(ShmPost.DestinationPublishType.ALL);
        } else {
            if(postBodyRequest.getDestinationPublishType().equals(ShmPost.DestinationPublishType.ALL.toString())){
                shmPost.setDestinationPublishType(ShmPost.DestinationPublishType.ALL);
            }
            if(postBodyRequest.getDestinationPublishType().equals(ShmPost.DestinationPublishType.PRIVATE.toString())){
                shmPost.setDestinationPublishType(ShmPost.DestinationPublishType.PRIVATE);
            }
            if(postBodyRequest.getDestinationPublishType().equals(ShmPost.DestinationPublishType.GROUP.toString())){
                List<ShtGroupPblDetail> groupPblDetails = userService.getGroupPblDetails(buildCompanyCode(shmUser.getId()));
                if(groupPblDetails.size() > 0 && groupPblDetails.get(0).getGroupPublish() != null){
                    shmPost.setGroupId(groupPblDetails.get(0).getGroupPublish().getGroupId());
                    shmPost.setDestinationPublishType(ShmPost.DestinationPublishType.GROUP);
                }else{
                    throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100161", null, null));
                }

            }
        }

        //address
        Long addr = postBodyRequest.getPostAddr();
        ShmAddr shmAddr = addressService.getAddress(addr);
        shmPost.setShmAddr(shmAddr);
        shmPost.setPostAddrTxt(addressService.getFullAddress(shmAddr));
        shmPost.setShmUser(shmUser);
        shmPost.setCreatedAt(LocalDateTime.now());
        ShmPost postSaved = savePost(shmPost);

        long postId = postSaved.getPostId();
        List<String> imagesStrBase64 = postBodyRequest.getImagesList();
        List<ShrImage> images = new ArrayList<ShrImage>();
        if (CollectionUtils.isNotEmpty(imagesStrBase64)) {
            for (String imageStrBase64 : imagesStrBase64) {
                String imageDir = imageBuilder.buildPostImageDir(postId);
                String imageName = imageBuilder.getName();
                byte[] imgBytes = jp.bo.bocc.helper.StringUtils.base64Decode(imageStrBase64);
                InputStream inputStream = new ByteArrayInputStream(imgBytes);
                ShrImage image = imageService.saveImage(imageDir, imageName, imageBuilder.getImageFormat(), inputStream);
                images.add(image);
            }
        }

        String imageIdJson = FileUtils.buildImageJson(images);
        postSaved.setPostImages(imageIdJson);

        //Update post after storing images
        ShmPost returnPost = savePost(postSaved);

        buildResponse(postBodyRequest, images, returnPost);
        return postBodyRequest;
    }

    public Boolean createSamplePost(PostBodyRequest postBodyRequest, String unzipDirName) throws Exception {
        // validate post info
        validateCreateCommonPost(postBodyRequest);
        try{
            ShmUser shmUser = userService.findActiveUserById(postBodyRequest.getPostUserId());
            if(shmUser == null || shmUser.getStatus() != ShmUser.Status.ACTIVATED){
                throw new Exception(messageSource.getMessage("SH_E100137", new Object[]{postBodyRequest.getPostUserId()}, null));
            }
            // validate user create post
            userService.checkUserStatus(shmUser);

            ShmPost shmPost = new ShmPost();
            shmPost.setPostName(postBodyRequest.getPostName());

            //Category
            ShmCategory category = categoryService.get(postBodyRequest.getPostCategoryId());
            if(category == null){
                throw new Exception(messageSource.getMessage("SH_E100138", new Object[]{postBodyRequest.getPostCategoryId()}, null));
            }
            shmPost.setPostCategory(category);
            shmPost.setPostDescription(postBodyRequest.getPostDescription());
            shmPost.setPostType(postBodyRequest.getPostType());
            shmPost.setPostPrice(postBodyRequest.getPostPrice());
            shmPost.setPostHashTagVal(postBodyRequest.getPostHashTagVal());

            //address
            Long addr = postBodyRequest.getPostAddr();
            ShmAddr shmAddr = addressService.getAddress(addr);
            if(shmAddr == null){
                throw new Exception(messageSource.getMessage("SH_E100142", new Object[]{addr}, null));
            }
            shmPost.setShmAddr(shmAddr);
            shmPost.setPostAddrTxt(addressService.getFullAddress(shmAddr));
            shmPost.setShmUser(shmUser);
            shmPost.setPostImages("[]");
            shmPost.setCreatedAt(LocalDateTime.now());
            ShmPost postSaved = savePost(shmPost);

            long postId = postSaved.getPostId();
            List<ShrImage> images = new ArrayList<ShrImage>();
            List<File> imgInputStreams = storageService.getImgImportListFile(postBodyRequest.getPostImportNo(),unzipDirName);
            if (CollectionUtils.isNotEmpty(imgInputStreams)) {
                for (File file : imgInputStreams) {
                    String imageDir = imageBuilder.buildPostImageDir(postId);
                    String imageName = imageBuilder.getName();
                    ShrImage image = imageService.saveSampleImage(imageDir, imageName, imageBuilder.getImageFormat(), file);
                    if(image == null){
                        return false;
                    }
                    images.add(image);
                }
            }

            if (postBodyRequest.getPostType() == ShmPost.PostType.SELL) {
                if(category != null && category.getImageRequired() == true && (CollectionUtils.isEmpty(images))){
                    throw new Exception(messageSource.getMessage("SH_E100144", new Object[]{postBodyRequest.getPostName()}, null));
                }
            }

            String imageIdJson = FileUtils.buildImageJson(images);
            postSaved.setPostImages(imageIdJson);

            //Update post after storing images
            ShmPost returnPost = savePost(postSaved);

            buildResponse(postBodyRequest, images, returnPost);
        }catch (Exception ex){
            throw ex;
        }
        return true;
    }

    private void buildResponse(PostBodyRequest postBodyRequest, List<ShrImage> images, ShmPost returnPost) {
        postBodyRequest.setPostId(returnPost.getPostId());
        postBodyRequest.setShmUser(returnPost.getShmUser());
        postBodyRequest.setShmAddr(returnPost.getShmAddr());
        List<ShrFile> listOrigin = new ArrayList<>();
        images.forEach(item -> {
            listOrigin.add(item.getOriginal());
        });
        postBodyRequest.setImages(listOrigin);
        postBodyRequest.setPostHashTagVal(returnPost.getPostHashTagVal());
        postBodyRequest.setImagesList(null);
        postBodyRequest.setPostAddrTxt(addressService.getFullAddress(returnPost.getShmAddr()));
    }

    private void validateCreateCommonPost(PostBodyRequest postBodyRequest) {
        if (postBodyRequest.getPostCategoryId() == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100006", null, null));
        }
        if (postBodyRequest.getPostName() == null || (postBodyRequest.getPostName().trim()).isEmpty()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100007", null, null));
        }
        if (postBodyRequest.getPostName().length() > 40) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100008", null, null));
        }
        if (postBodyRequest.getPostDescription() != null &&  postBodyRequest.getPostDescription().length() > 1000) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E1000010", null, null));
        }

        // check NG Words
        String nGWordStr = "";
        List<String> listNgWords = adminNgService.checkNGContent(postBodyRequest.getPostName());
        List<String> listNgWordsInDes = adminNgService.checkNGContent(postBodyRequest.getPostDescription());
        for (String ngW: listNgWordsInDes) {
            if(!listNgWords.contains(ngW)){
                listNgWords.add(ngW);
            }
        }
        for (String ngW: listNgWords) {
            nGWordStr += ngW +",";
        }
        if (!StringUtils.isEmpty(nGWordStr)) {
            nGWordStr = nGWordStr.substring(0, nGWordStr.length() - 1);
        }

        if(!StringUtils.isEmpty(nGWordStr)){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100042", new Object[]{nGWordStr},null));
        }

        if (postBodyRequest.getPostAddr() == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100011", null, null));
        }
        if (postBodyRequest.getPostPrice() == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100012", null, null));
        }
        if (postBodyRequest.getPostHashTagVal() != null) {
            String[] arrHashTags = postBodyRequest.getPostHashTagVal().split(",");
            if (arrHashTags.length > 3) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100013", null, null));
            }
        }
        if (postBodyRequest.getPostType() == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100014", null, null));
        }
    }

    private void validatePostCreateFromClient(PostBodyRequest postBodyRequest){
        validateCreateCommonPost(postBodyRequest);
        if (postBodyRequest.getPostType() == ShmPost.PostType.SELL) {
            ShmCategory shmCategory = categoryService.get(postBodyRequest.getPostCategoryId());
            if(shmCategory != null && shmCategory.getImageRequired() == true
                    && (postBodyRequest.getImagesList() == null || postBodyRequest.getImagesList().size() == 0)){
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100015", null, null));
            }
        }
    }

    @Override
    public void deletePost(ShmPost shmPost) throws Exception {

        // send msg to talk room
        if(shmPost.getPostSellStatus() == ShmPost.PostSellSatus.SOLD){
            List<ShtTalkPurc> talkPurcs = talkPurcService.findTalksByPostIdAndStatus(shmPost, ShtTalkPurc.TalkPurcStatusEnum.CLOSED);
            if(talkPurcs != null){
                for (ShtTalkPurc talkPurc: talkPurcs) {
                    ShtTalkPurcMsg msg = createMsgDeletedPost(talkPurc);
                    ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                //    fireBaseService.sendNewMsgInToFireBaseDB(TalkPurcServiceImpl.buildTalkPurcInfo(shmPost.getShmUser(), msgSaved));
                }
            }
        }

        shmPost.setPostSellStatus(ShmPost.PostSellSatus.DELETED);
        postRepository.save(shmPost);
    }

    private ShtTalkPurcMsg createMsgDeletedPost(ShtTalkPurc talkPurc){
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        msg.setShmUserCreator(talkPurc.getShmPost().getShmUser());
        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_SENT_FOR_PARTNER);
        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10012", null, null));
        return  msg;
    }

    @Transactional(readOnly = true)
    @Override
    public long getCountPostById(long id) throws Exception {

        return postRepository.countPostById(id);
    }

    @Override
    public long getPostUserId(long id) throws Exception {

        ShmPost shmPost = postRepository.findOne(id);
        final ShmUser shmUser = shmPost.getShmUser();
        if (shmUser == null) {
            throw new Exception(messageSource.getMessage("SH_E100049", new Object[]{shmPost.getPostId()}, null));
        }
        long postUserId = shmUser.getId();
        return postUserId;
    }

    @Override
    public long countPostByPostIdAndPostSellStatus(Long userId, int postStatus) {
        return postRepository.countPostByPostIdAndPostSellStatus(userId,postStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public void exportPostCSV(PostSearchRequest request, PageRequest pageRequest, HttpServletResponse response) throws IOException {
        Page<ShmPost> posts = postService.searchPostByConditions(request, pageRequest);
        List<PostCsvDTO> data = convertToPostCsvDto(posts.getContent());

        String[] header = {"投稿ID",	"投稿名", "投稿者ID", "投稿者名", "大カテゴリ", "中カテゴリ", "ステータス", "投稿登録日",	"更新日", "価格", "通報回数"};
        String[] properties = {"postId", "postName", "userId", "userName", "catParent", "catChild", "status", "csvCreatedAt", "csvUpdatedAt", "price", "reportTime"};
        exportCsvService.exportCSV(response, "post.csv", header, properties, data);
    }

    private List<PostCsvDTO> convertToPostCsvDto(List<ShmPost> postList) {
        List<PostCsvDTO> csvDTOList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        PostCsvDTO dto;
        for (ShmPost post : postList) {
            dto = new PostCsvDTO();
            dto.setPostId(post.getPostId());
            dto.setPostName(post.getPostName());
            dto.setUserId(post.getShmUser().getId());
            dto.setUserName(post.getShmUser().getFirstName() + post.getShmUser().getLastName());
            dto.setPostType(post.getPostType().equals(ShmPost.PostType.BUY) ? "出品" : "リクエスト");
            if (post.getPostCategoryParent() != null)
                dto.setCatParent(post.getPostCategoryParent().getCategoryName());
            dto.setCatChild(post.getPostCategory().getCategoryName());
            dto.setCsvCreatedAt(" " + post.getCreatedAt().format(formatter));
            dto.setCsvUpdatedAt(" " + (post.getUserUpdateAt() == null ? post.getCreatedAt().format(formatter) : post.getUserUpdateAt().format(formatter)));
            dto.setPrice(post.getPostPrice());
            dto.setReportTime(post.getPostReportTimes());
            String status = "";
            if (ShmPost.PostSellSatus.PUBLIC.equals(post.getPostSellStatus()) && !ShmPost.PostCtrlStatus.SUSPENDED.equals(post.getPostCtrlStatus()) && !ShmPost.PostSellSatus.DELETED.equals(post.getPostSellStatus()))
                status = "公開中";
            if (ShmPost.PostSellSatus.IN_CONVERSATION.equals(post.getPostSellStatus()) && !ShmPost.PostCtrlStatus.SUSPENDED.equals(post.getPostCtrlStatus()) && !ShmPost.PostSellSatus.DELETED.equals(post.getPostSellStatus()))
                status = "取引中";
            if (ShmPost.PostSellSatus.TEND_TO_SELL.equals(post.getPostSellStatus()) && !ShmPost.PostCtrlStatus.SUSPENDED.equals(post.getPostCtrlStatus()) && !ShmPost.PostSellSatus.DELETED.equals(post.getPostSellStatus()))
                status = "取引意思確認中";
            if (ShmPost.PostSellSatus.SOLD.equals(post.getPostSellStatus()) && !ShmPost.PostCtrlStatus.SUSPENDED.equals(post.getPostCtrlStatus()) && !ShmPost.PostSellSatus.DELETED.equals(post.getPostSellStatus()))
                status = "取引完了";
            if (ShmPost.PostCtrlStatus.SUSPENDED.equals(post.getPostCtrlStatus()) && !ShmPost.PostSellSatus.DELETED.equals(post.getPostSellStatus()))
                status = "強制停止・違反";
            if (ShmPost.PostSellSatus.DELETED.equals(post.getPostSellStatus()))
                status = "削除済";
            dto.setStatus(status);

            csvDTOList.add(dto);
        }

        return csvDTOList;
    }

    @Override
    public long countPostByPostIdAndPostCtrlStatus(Long userId, int postCtrlStatus) {
        return postRepository.countPostByPostIdAndPostCtrlStatus(userId, postCtrlStatus);
    }

    /**
     * Get only one ShmPostImage
     */
    @Override
    public ShmPost getShmPostImage(ShmPost post, ImageSavedEnum imageSavedEnum) {
        ShrFile file;
        List<ShrFile> files = imageSavedEnum.equals(ImageSavedEnum.ORIGINAL) ? post.getPostOriginalImages() : post.getPostThumbnailImages();
        final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(post.getPostImages(), imageSavedEnum);
        List<String> postOriginalImagePaths = new ArrayList<>();
        for (Long id : allImageIdByType) {
            if (id != null) {
                file = fileService.get(id);
                postOriginalImagePaths.add(imgServer + FileUtils.buildImagePathByFileForPost(file));
                files.add(file);
            }
        }

        post.setPostOriginalImagePaths(postOriginalImagePaths);
        final ShmCategory postCategory = post.getPostCategory();
        if (postCategory != null) {
            final ShmCategory shmCategory = categoryRepos.findOne(postCategory.getCategoryParentId());
            post.setPostCategoryParent(shmCategory);
        }
        return post;
    }

    @Override
    @Transactional(readOnly = true)
    public long countPostByPostIdAndPostType(Long userId, int postType) {
        return postRepository.countPostByPostIdAndPostType(userId, postType);
    }

    @Override
    public Page<ShmPostDTO> searchOwnPostHasConversation(Pageable pageable, Long userId) {
        Page<Object[]> resultRaw = postRepository.getOwnerPostHaveTalkPurc(userId, pageable);
        Page<ShmPostDTO> result = buildResultForPostHasConversation(resultRaw.getContent(), pageable, userId, true, resultRaw.getTotalElements());
        return result;
    }

    private Page<ShmPostDTO> buildResultForPostHasConversation(List<Object[]> resultRaw, Pageable pageable, Long userId, boolean isOwnPost, long totalElements) {
        List<ShmPostDTO> resultList = new ArrayList<>();
        for (Object[] o : resultRaw){
            ShmPostDTO shmPostDTO = new ShmPostDTO();
            final Long postId = ConverterUtils.getLongValue(o[0]);
            shmPostDTO.setPostId(postId);
            shmPostDTO.setPostName(String.valueOf(o[1]));
            shmPostDTO.setPostDescription(String.valueOf(o[2]));
            shmPostDTO.setPostCategoryId(ConverterUtils.getLongValue(o[3]));
            shmPostDTO.setPostPrice(ConverterUtils.getLongValue(o[4]));
            shmPostDTO.setPostLikeTimes(ConverterUtils.getLongValue(o[5]));
            shmPostDTO.setPostReportTimes(ConverterUtils.getLongValue(o[6]));
            shmPostDTO.setPostType(ShmPost.PostType.values()[ConverterUtils.getIntValue(o[7])]);
            final String postImages = String.valueOf(o[8]);
            shmPostDTO.setPostImages(postImages);
            if (StringUtils.isNotEmpty(postImages))
                shmPostDTO.setPostThumbnailImages(buildListShrFileForPost(postImages,ImageSavedEnum.THUMBNAIL));
            shmPostDTO.setPostAddrId(ConverterUtils.getLongValue(o[9]));
            shmPostDTO.setPostAddrTxt(String.valueOf(o[10]));
            shmPostDTO.setPostSellStatus(ShmPost.PostSellSatus.values()[ConverterUtils.getIntValue(o[11])]);
            shmPostDTO.setPostHashTagVal(String.valueOf(o[12]));
            Integer ctrlStatus = ConverterUtils.getIntValue(String.valueOf(o[13]));
            shmPostDTO.setPostCtrlStatus(ShmPost.PostCtrlStatus.values()[ctrlStatus]);
            if (isOwnPost) {
                final int totalNewMsgInTalkPurcForPost = talkPurcMsgService.countNewMsgForAllTalkByPostIdForOwnerPost(postId, userId);
                shmPostDTO.setTotalNewMsgInTalkPurcForPost(totalNewMsgInTalkPurcForPost);
            }else {
                final int totalNewMsgInTalkPurcForPost = talkPurcMsgService.countNewMsgForAllTalkByPostIdFromOther(postId, userId);
                shmPostDTO.setTotalNewMsgInTalkPurcForPost(totalNewMsgInTalkPurcForPost);
                shmPostDTO.setTalkPurcIdOfCurrentUser(talkPurcService.findTalkPurcByPostIdAndPartnerId(postId, userId));
            }

            //count liked times
            Long postLikedTimes = userFvrtService.countLikeTimeByPostId(postId);
            shmPostDTO.setPostLikeTimes(postLikedTimes);
            // Get destination public type
            ShmPost post = postRepository.findOne(postId);
            shmPostDTO.setDestinationPublishType(post.getDestinationPublishType() == null ? ShmPost.DestinationPublishType.ALL.toString() : post.getDestinationPublishType().toString());
            resultList.add(shmPostDTO);
        }
        Page<ShmPostDTO> result = new PageImpl<ShmPostDTO>(resultList, pageable, totalElements);
        return result;
    }

    @Override
    public Page<ShmPostDTO> searchPostFromOthersHasConversation(Pageable pageable, Long userId) {
        Page<Object[]> resultRaw = postRepository.getPostFromOtherHavingConversation(userId, pageable);
        Page<ShmPostDTO> result = buildResultForPostHasConversation(resultRaw.getContent(), pageable, userId, false, resultRaw.getTotalElements());
        return result;
    }

    private PostPatrolRequest groupCtrlStt(PostPatrolRequest request) {
        List<Short> ctrlStt = new ArrayList<>();
        if (request.getPendingStt() != null)
            ctrlStt.add(request.getPendingStt());
        if (request.getNgStt() != null )
            ctrlStt.add(request.getNgStt());
        if (request.getRepairedStt() != null)
            ctrlStt.add(request.getRepairedStt());

        request.setCtrlStt(ctrlStt);
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShmPostDTO> searchPostPatrols(PostPatrolRequest request) throws ParseException {
        List<ShmPostDTO> dtoList = null;
        try {
            request = groupCtrlStt(request);
            List<Object[]> postList = postRepository.getPostPatrolList(request);
            dtoList = new ArrayList<>();
            List<Long> distinctList = new ArrayList<>();
            ShmPostDTO dto;

            for (Object[] obj : postList) {
                dto = new ShmPostDTO();
                Long postId = Long.valueOf(String.valueOf(obj[9]));
                dto.setPostId(postId);
                if (distinctList.contains(postId)) {
                    request.setStartPage(request.getEndPage() + 1);
                    request.setEndPage(request.getStartPage() + 1);
                    List<Object[]> subObject = postRepository.getPostPatrolList(request);
                    if (subObject.size() > 0) {
                        obj = subObject.get(0);
                        postId = Long.valueOf(String.valueOf(obj[9]));
                    } else
                        continue;
                }

                Long operatorId = null;
                if (!String.valueOf(obj[10]).equals("null"))
                    operatorId = Long.parseLong(String.valueOf(obj[10]));
                ShtAdminLog adminLog = adminLogService.getLastAdminProcessPost(postId);
                String lastOperatorName = (adminLog != null && adminLog.getShmAdmin() != null) ? adminLog.getShmAdmin().getAdminName() : "";
                if (!org.apache.commons.lang.StringUtils.isEmpty(request.getOperatorNames()) && adminLog != null && adminLog.getShmAdmin() != null && !adminLog.getShmAdmin().getAdminId().equals(operatorId))
                    continue;
                dto.setAdminName(String.valueOf(obj[11]).equals("null") ? null : lastOperatorName);
                dto.setPostName(String.valueOf(obj[1]));
                dto.setUserNickName(String.valueOf(obj[2]));
                dto.setPtrlStatus(ShmPost.PostPtrlStatusEnum.values()[Integer.valueOf(String.valueOf(obj[3]))].toString());
                String updateAtStr = String.valueOf(obj[4]);
                String createAtStr = String.valueOf(obj[12]);
                SimpleDateFormat formatter = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                dto.setUserUpdatedAt(updateAtStr.equalsIgnoreCase("null") ? formatter.parse(createAtStr).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : formatter.parse(updateAtStr).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                dto.setCtrlStatus(ShmPost.PostCtrlStatus.values()[Integer.valueOf(String.valueOf(obj[5]))].toString());
                dto.setPostReportTimes(Long.parseLong(String.valueOf(obj[6])));
                dto.setMaxRecord(Integer.valueOf(String.valueOf(obj[8])));

                boolean isPendingInPast = adminLogService.pendingPostInPast(postId);
                if (request.getPendingStt() != null && !isPendingInPast)
                    continue;
                dto.setPendingInPast(isPendingInPast == true ? "保留中" : "");
                List<LocalDateTime> lastTimeSuspendPost = adminLogService.suspendPostInPast(postId);
                boolean isSuspendInPast = lastTimeSuspendPost.size() > 0 && lastTimeSuspendPost.get(0).isBefore(dto.getUserUpdatedAt());
                if (request.getRepairedStt() != null && !isSuspendInPast)
                    continue;
                dto.setSuspendInPast(isSuspendInPast == true ? "修正完了" : "");
                distinctList.add(postId);
                if (String.valueOf(obj[0]) != null && !String.valueOf(obj[0]).equals("[]")) {
                    List<String> imagePaths = buildListImagePathsForPost(String.valueOf(obj[0]), ImageSavedEnum.ORIGINAL);
                    dto.setPostThumbnailImagePaths(imagePaths);
                    dto.setPostOriginalImagePaths(imagePaths);
                }
                dto.setLastReportUser(userRprtService.getNicknameLastUserReportPost(postId));
                dtoList.add(dto);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }

        return dtoList;
    }

    @Override
    public List<ShmPost> getPostsByUserId(Long userId) {
        ShmUser user = userService.getUserById(userId);
        if (user == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, messageSource.getMessage("SH_E100021", null, null));

        return postRepository.findByShmUser(user);
    }

    public Long countPostByUserId(long userId) {
        return postRepository.countPostByUserId(userId);
    }

    @Override
    public void calculateLikeTimes(Long postId, long i) {
        final int currentLikeTimeByPostId = postRepository.getCurrentLikeTimeByPostId(postId);
        postRepository.updateLikeTime(postId, currentLikeTimeByPostId + i);
    }

    @Override
    @Transactional
    public ShmPost processPostToOke(Long postId, String adminEmail) throws Exception {
        ShmPost post = postRepository.findOne(postId);
        boolean sendEmailAndNotifyFlag = false;
        if (post == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, messageSource.getMessage("SH_E100024", null, null));
        if (post.getPostPtrlStatus().equals(ShmPost.PostPtrlStatusEnum.CENSORED) && post.getPostCtrlStatus().equals(ShmPost.PostCtrlStatus.ACTIVE))
            return null;
        if (post.getPostCtrlStatus() == ShmPost.PostCtrlStatus.SUSPENDED)
            sendEmailAndNotifyFlag = true;

        post.setPostPtrlStatus(ShmPost.PostPtrlStatusEnum.CENSORED);
        post.setPostCtrlStatus(ShmPost.PostCtrlStatus.ACTIVE);
        post.setIsInPatrol(false);
        post.setTimePatrol(LocalDateTime.now());
        postRepository.save(post);

        ShmAdmin admin = adminService.getAdminByEmail(adminEmail);
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, messageSource.getMessage("SH_E100024", null, null));

        ShtAdminLog adminLog = new ShtAdminLog();
        adminLog.setShmAdmin(admin);
        adminLog.setAdminLogTitle(String.valueOf(PatrolActionEnum.PATROL_POST_OK.ordinal()));
        adminLog.setShmPost(post);
        adminLog.setAdminLogType(PatrolActionEnum.PATROL_POST_OK);
        adminLog.setAdminLogCont(messageSource.getMessage("SH_E100043", null, null));
        adminLogService.save(adminLog);
//        memoPostService.interruptJob(postId);

        if (sendEmailAndNotifyFlag) {

            // send notify to mailbox
            ShmUser shmUser = userService.getUserById(post.getShmUser().getId());
            Long qaId = null;
            if(post != null && shmUser != null){
                ShtQa qa = new ShtQa();
                qa.setQaTitle(messageSource.getMessage("QA_TITLE_RESTORE_POST",null,null));
                qa.setFirstQaMsg(messageSource.getMessage("QA_CONTENT_RESTORE_POST", null, null).replace("<postName>",post.getPostName()));
                qaId = qaService.createNotifyFromAdmin(shmUser,qa, admin).getQaId();
            }

            final String msgCont = messageSource.getMessage("PUSH_POST_OKE_AFTER_SUSPEND", null, null);
            final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.POST_HANDLE_OK, null, null, null, null, qaId, null, post.getShmUser().getId());
            snsMobilePushService.sendNotificationForUser(post.getShmUser().getId(), pushMsg, pushMsgDetail);
            mailService.sendEmailRestorePost(shmUser.getEmail(),shmUser.getNickName(),post.getPostName());

            List<ShtTalkPurc> talkPurcs = talkPurcService.findTalksByPostIdAndStatus(post, ShtTalkPurc.TalkPurcStatusEnum.TEND_TO_SELL);
            if(talkPurcs != null && talkPurcs.size() > 0){
                for (ShtTalkPurc talkPurc: talkPurcs) {
                    ShtTalkPurcMsg msg = createMsgForPartner(talkPurc);
                    ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                 //   fireBaseService.sendNewMsgInToFireBaseDB(TalkPurcServiceImpl.buildTalkPurcInfo(post.getShmUser(), msgSaved));
                }
            }
        }

        return post;
    }

    @Override
    public boolean isOwnerPostOfTalkPurc(Long userId) {
        if (postRepository.isOwnerPost(userId) != null){
            return true;
        }
        return false;
    }

    @Override
    public ShtUserRprt findReportPostByUser(Long userId, Long postId) {
        return userRprtService.getUserRprt(postId,userId);
    }

    @Transactional(readOnly = true)
    @Override
    public ShmPost getPostPatrolSequent() throws ParseException, SchedulerException {
        ShmPost post = null;
        try {
            post = postRepository.findPostPatrol();
            if (post != null ) {
                ShmCategory shmCategory = post.getPostCategory();
                if (shmCategory != null && shmCategory.getCategoryParentId() != null) {
                    ShmCategory parentCategory = categoryService.get(shmCategory.getCategoryParentId());
                    post.setPostCategoryParent(parentCategory);
                }

                Long reportTimes = userRprtService.countTotalReportByPostId(post.getPostId());
                post.setPostReportTimes(reportTimes);

                // return no record if post was OK
                if(post.getPostCtrlStatus() == ShmPost.PostCtrlStatus.ACTIVE
                        && post.getPostPtrlStatus() == ShmPost.PostPtrlStatusEnum.CENSORED){
                    return null;
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            throw e;
        }
        return post;
    }

    @Override
    public List<ShmPost> buildListImagePathsForListPost(List<ShmPost> content) {
        List<ShmPost> listPost = new ArrayList<>();
        ShrFile shrFile;
        for (ShmPost shmPost : content) {
            final List<Long> allImageIdOriginal = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.ORIGINAL);
            List<String> postOriginalImagePaths = new ArrayList<>();
            for (Long id : allImageIdOriginal) {
                if (id != null) {
                    shrFile = fileService.get(id);
                    postOriginalImagePaths.add(imgServer + FileUtils.buildImagePathByFileForPost(shrFile));
                }
            }
            shmPost.setPostOriginalImagePaths(postOriginalImagePaths);

            final List<Long> allImageIdThumb = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.THUMBNAIL);
            List<String> postThumbImagePaths = new ArrayList<>();
            for (Long id : allImageIdThumb) {
                if (id != null) {
                    shrFile = fileService.get(id);
                    postThumbImagePaths.add(imgServer + FileUtils.buildImagePathByFileForPost(shrFile));
                }
            }
            shmPost.setPostThumbnailImagePaths(postThumbImagePaths);

            listPost.add(shmPost);
        }
        return listPost;
    }

    @Override
    public ShmPost buildListImagePathsForPost(ShmPost content) {
        content.setPostOriginalImagePaths(getPostImagePaths(content,ImageSavedEnum.ORIGINAL));
        content.setPostThumbnailImagePaths(getPostImagePaths(content,ImageSavedEnum.THUMBNAIL));
        return content;
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalPostByUserIdList(List<Long> userIdList) {
        return postRepository.countTotalPostByUserIdList(userIdList);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalPostMatchingByUserIdList(List<Long> userIdList) {
        return postRepository.countTotalPostMatchingByUserIdList(userIdList);
    }

    private List<String> getPostImagePaths(ShmPost shmPost, ImageSavedEnum imageSavedEnum){
        List<String> postImgPaths = null;
        try {
            postImgPaths = new ArrayList<>();
            if (shmPost != null) {
                final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(shmPost.getPostImages(), imageSavedEnum);
                for (Long id : allImageIdByType) {
                    if (id != null) {
                        ShrFile shrFile = fileService.get(id);
                        postImgPaths.add(imgServer + FileUtils.buildImagePathByFileForPost(shrFile));
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return postImgPaths;
    }

    @Override
    public List<String> buildListImagePathsForPost(String postImages, ImageSavedEnum original) {
        final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(postImages, original);
        List<String> postOriginalImagePaths = new ArrayList<>();
        for (Long id : allImageIdByType) {
            if (id != null) {
                ShrFile shrFile = fileService.get(id);
                postOriginalImagePaths.add(imgServer + FileUtils.buildImagePathByFileForPost(shrFile));
            }
        }
        return postOriginalImagePaths;
    }

    @Override
    public List<ShrFile> buildListShrFileForPost(String postImages, ImageSavedEnum original) {
        final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(postImages, original);
        List<ShrFile> postOriginalImagePaths = new ArrayList<>();
        for (Long id : allImageIdByType) {
            if (id != null) {
                ShrFile shrFile = fileService.get(id);
                postOriginalImagePaths.add(shrFile);
            }
        }
        return postOriginalImagePaths;
    }

    @Override
    public List<Long> getListPostIdForOwnerPostHavingConversation(Long userIdUsingApp) {
        return postRepository.getListPostIdForOwnerPostHavingConversation(userIdUsingApp);
    }

    @Override
    public List<Long> getListPostIdFormOthersHavingConversation(Long userIdUsingApp) {
        return postRepository.getListPostIdFormOthersHavingConversation(userIdUsingApp);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTransactionByPostType(Long userId, int postType) {
        return postRepository.countPostByPostIdAndPostTypeAndPartnerId(userId, postType);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUniqueTransactionByPostType(Long userId, int postType) {
        return postRepository.countUniquePostByPostIdAndPostTypeAndPartnerId(userId, postType);
    }

    @Override
    public List<ShmPost> getPostsOverTimeToPatrol(LocalDateTime limitTime, boolean isPatrol) {
        return postRepository.findByTimePatrolGreaterThanAndIsInPatrol(limitTime, isPatrol);
    }

    @Override
    public Long countTotalPostsByUserId(Long userId) {
        return postRepository.countPostByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public void exportPostPatrolCsv(PostPatrolRequest request, HttpServletResponse response) throws IOException, ParseException {
        List<ShmPostDTO> dtoList = searchPostPatrols(request);
        List<PostPatrolCsvDTO> csvDTOList = new ArrayList<>();
        PostPatrolCsvDTO csvDTO;
        for (ShmPostDTO dto : dtoList) {
            csvDTO = new PostPatrolCsvDTO();
            csvDTO.setAdminName(dto.getAdminName());
            csvDTO.setPostId(dto.getPostId());
            csvDTO.setTotalComment(adminLogService.countTotalProcessPostTimes(dto.getPostId()));
            ShtMemoPost memoPost = memoPostService.getMemoNewestByPostId(dto.getPostId());
            if (memoPost != null && csvDTO.getTotalComment() > 0)
                csvDTO.setCommentNewest(memoPost.getContent());

            csvDTOList.add(csvDTO);
        }

        String[] header = {"投稿ID", "チェック者", "対応コメント数", "対応コメント"};
        String[] properties = {"postId", "adminName", "totalComment", "commentNewest"};
        exportCsvService.exportCSV(response, "post_patrol.csv", header, properties, csvDTOList);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShmPostDTO> getPostHistory(Pageable pageable, String postType, Long userId, String sortField, Long price) {
        ShmPost post =null;
        Page<ShmPost> resultWithOutFile = postRepository.getPostHistory(pageable, postType, sortField, price, userId);
        List<ShmPost> listPost = addExtralInfo(resultWithOutFile.getContent(), userId, ImageSavedEnum.THUMBNAIL);
        buildImageForListPost(userId, listPost);
        Page<ShmPost> result = new PageImpl<ShmPost>(listPost, pageable, resultWithOutFile.getTotalElements());

        Page<ShmPostDTO> shmPostDTOS = PostMapper.mapEntityPageIntoDTOPage(pageable, result);
        return shmPostDTOS;
    }

    private void buildImageForListPost(Long userId, List<ShmPost> listPost) {
        if(listPost != null && userId != null){
            for (ShmPost shmPost: listPost) {
                if(shmPost.getShmUser() != null && shmPost.getShmUser().getId().intValue() == userId.intValue()){
                    shmPost.setOwnedByCurrentUser(true);
                }else{
                    shmPost.setOwnedByCurrentUser(false);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShmPostDTO> getFavoritePostHistory(Pageable pageable, String postType, Long userId, String sortField, Long price) {
        ShmPost post =null;
        Page<ShmPost> resultWithOutFile = postRepository.getFavoritePostHistory(pageable, postType, sortField, price, userId);
        if (resultWithOutFile == null) {
            return null;
        }
        List<ShmPost> listPost = addExtralInfo(resultWithOutFile.getContent(), userId, ImageSavedEnum.THUMBNAIL);
        buildImageForListPost(userId, listPost);
        Page<ShmPost> result = new PageImpl<ShmPost>(listPost, pageable, resultWithOutFile.getTotalElements());

        Page<ShmPostDTO> shmPostDTOS = PostMapper.mapEntityPageIntoDTOPage(pageable, result);
        return shmPostDTOS;
    }

    @Override
    public boolean checkPostStatus(ShmPost shmPost) throws Exception {
        if(shmPost == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100024", null, null));
        }
        if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.PUBLIC){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100025", null, null));
        }
        if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.SOLD){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100026", null, null));
        }
        if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.DELETED){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100027", null, null));
        }
        if (shmPost.getPostCtrlStatus() == ShmPost.PostCtrlStatus.SUSPENDED){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100028", null, null));
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public long getReportTimeForPost(long postId) {
        return userRprtRepository.countReportTime(postId);
    }

    @Override
    public void savePostPatrol(ShmPost post, ShmAdmin shmAdmin) {
        ShtAdminPost shtAdminPost = adminPostRepository.getDataByAdminIdAndPostId(shmAdmin.getAdminId(), post.getPostId());
        if (shtAdminPost == null) {
            shtAdminPost = new ShtAdminPost();
            shtAdminPost.setShmAdmin(shmAdmin);
            shtAdminPost.setShmPost(post);
            adminPostRepository.save(shtAdminPost);
        }
    }

    @Override
    public void cleanDataNotProcessByAdmin(Long adminId) {
        List<ShtAdminPost> shtAdminPostList = adminPostRepository.getDataNotProcessByAdmin(adminId);
        shtAdminPostList.forEach(item -> adminPostRepository.delete(item));
        shtAdminPostList.forEach(item -> {
            if (item.getShmPost() != null) {

                ShmPost shmPost = postRepository.findOne(item.getShmPost().getPostId());
                if (shmPost != null) {
                    shmPost.setIsInPatrol(false);
                    postRepository.save(shmPost);
                }
            }
        });

    }

    @Override
    public boolean checkPostInPatrolProcess(Long postId, Long adminId) {
        ShtAdminPost shtAdminPost = adminPostRepository.checkPostInPatrolProcess(postId, adminId);
        if (shtAdminPost != null)
            return true;
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public ShmPostDTO getDefaultValueUserForPost(Long userId) {
        ShmPostDTO result = new ShmPostDTO();
        ShmUser shmUser = userService.getUserById(userId);
        ShmAddr address = shmUser.getAddress();
        if (address != null && address.getAddrParentId() != null) {
            result.setOwnerPostAddressDistrictId(address.getAddressId());
            result.setOwnerPostAddressDistrictTxt(address.getAreaName());
            result.setOwnerPostAddressDistrictCode(address.getAreaCode());
            Long addrParentId = address.getAddrParentId();
            ShmAddr addressParent = addressService.getAddress(addrParentId);
            if (addressParent != null) {
                result.setOwnerPostAddressProvinceId(addressParent.getAddressId());
                result.setOwnerPostAddressProvinceTxt(addressParent.getAreaName());
                result.setOwnerPostAddressProvinceCode(addressParent.getAreaCode());
            }
        }

        return result;
    }

    @Override
    public void updatePostLikedTimes(Long postId, Long likedTimes) {
        postRepository.updateLikeTime(postId, likedTimes);
    }

    @Override
    public List<ShmPost> getPostByGroupId(Long groupId) {
        return postRepository.findByGroupId(groupId);
    }
}
