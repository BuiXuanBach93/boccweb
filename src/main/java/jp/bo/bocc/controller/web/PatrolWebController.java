package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.*;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author NguyenThuong on 3/29/2017.
 */
@Controller
public class PatrolWebController extends BoccBaseWebController {

    private final static Logger LOGGER = Logger.getLogger(PatrolWebController.class.getName());

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Value("${max.sequent.number.default}")
    private int maxSequentNumber;

    @Autowired
    PostService postService;

    @Autowired
    QaService qaService;

    @Autowired
    UserService userService;

    @Autowired
    MemoPostService memoPostService;

    @Autowired
    AdminService adminService;

    @Autowired
    AddressService addressService;

    @Autowired
    AdminCsvHstService adminCsvHstService;

    @Autowired
    UserRprtService userRprtService;

    @Autowired
    private MemoUserService memoUserService;

    @Value("${image.server.url}")
    private String imgServer;

    @Autowired
    private SNSMobilePushService snsMobilePushService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailService mailService;

    @Autowired
    private AdminLogService adminLogService;

    @RequestMapping(value = "post-patrol", method = RequestMethod.GET)
    public String getPostsPatrol(Model model, @ModelAttribute("postPatrolRequest") PostPatrolRequest request) throws ParseException, SchedulerException {
        ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        request.setSize(maxRecordsInPage);
        if (validatePostPatrolRequest(request))
            request.setPostStatus(0L);
        if (request.getFromCompleteAt() != null && request.getToCompleteAt() != null && request.getToCompleteAt().before(request.getFromCompleteAt()) ) {
            model.addAttribute("completedAtError", getMessage("SH_E100048"));
            return "post-patrol";
        }
        if (request.getFromUpdateAt() != null && request.getToUpdateAt() != null && request.getToUpdateAt().before(request.getFromUpdateAt()) ) {
            model.addAttribute("updatedAtError", getMessage("SH_E100048"));
            return "post-patrol";
        }

        //clean data not processed
        postService.cleanDataNotProcessByAdmin(shmAdmin.getAdminId());

        request.setStartPage(request.getPage() * request.getSize());
        request.setEndPage(request.getPage() * request.getSize() + request.getSize());
        List<ShmPostDTO> postPatrol = postService.searchPostPatrols(request);

        if (postPatrol.size() > 0) {
            long totalElms = postPatrol.get(0).getMaxRecord();
            int totalPage = Math.round(totalElms / maxRecordsInPage);
            if (totalElms % maxRecordsInPage == 0)
                totalPage = totalPage - 1;

            model.addAttribute("posts", postPatrol);
            model.addAttribute("curPage", request.getPage());
            model.addAttribute("totalPage", totalPage);
            model.addAttribute("totalElms", totalElms < maxRecordsInPage ? postPatrol.size() : totalElms);
            model.addAttribute("curElms", postPatrol.size());
            model.addAttribute("startElement", (request.getPage() * maxRecordsInPage) + 1);
            model.addAttribute("curElements", (request.getPage() * maxRecordsInPage) + postPatrol.size());
        }

        return "post-patrol";
    }

    @RequestMapping(value = "/posts-patrol/csv", method = RequestMethod.GET)
    public void exportPostPatrolCSV(Model model, @ModelAttribute("postPatrolRequest") PostPatrolRequest request, HttpServletResponse response) throws IOException, ParseException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        request.setSize(maxRecordsInPage);
        request.setStartPage(request.getPage() * request.getSize());
        request.setEndPage(request.getPage() * request.getSize() + request.getSize());
        if (validatePostPatrolRequest(request))
            request.setPostStatus(0L);

        postService.exportPostPatrolCsv(request, response);
        adminCsvHstService.save(admin, ShtAdminCsvHst.CSV_TYPE.POST_PATROL);
    }

    private boolean validatePostPatrolRequest(PostPatrolRequest request) {
        if (request.getPostStatus() == null && request.getPendingStt() == null && request.getNgStt() == null && request.getReportStt() == null
                && request.getFromUpdateAt() == null && request.getToUpdateAt() == null && request.getOperatorNames() == null && request.getFromUpdateAt() == null && request.getToCompleteAt() == null)
            return true;
        return false;
    }

    @RequestMapping(value = "post-patrol-sequent", method = RequestMethod.GET)
    public String getPostPatrolSequent(Model model, @RequestParam("sequentNumber") int sequentNumber) throws Exception {
        ShmPost postSequent = null;
        try {
            ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
            //clean data not processed
            postService.cleanDataNotProcessByAdmin(shmAdmin.getAdminId());
            postSequent = postService.getPostPatrolSequent();
            if (postSequent == null) {
                LOGGER.error("ERROR: There are no posts to censor.");
                model.addAttribute("errorMsg", getMessage("SH_E100089"));
                return "post-patrol-sequent";
            } else {
                ShmPost result = postService.buildListImagePathsForPost(postSequent);
                ShmUser user = result.getShmUser();
                String postOwnerAvatarPath = null;
                if (user != null) {
                    postOwnerAvatarPath = userService.buildOriginalAvatarPathForUser(user.getAvatar());
                    if (user.getAddress() != null)
                        user.getAddress().setFullAreaName(addressService.getFullAddress(user.getAddress()));
                }
                model.addAttribute("postOwnerAvatarPath", postOwnerAvatarPath);

                long countPost = 0;
                countPost = postService.getCountPostById(postSequent.getShmUser().getId());

                model.addAttribute("countPost", countPost);
                model.addAttribute("postDetail", result);
                model.addAttribute("isPatrolSequent", 1);
                model.addAttribute("sequentNumber", sequentNumber);
                model.addAttribute("maxSequentNumber", maxSequentNumber);
            //block post from other admins
            postSequent.setIsInPatrol(true);
            postService.savePost(postSequent);
            postService.savePostPatrol(postSequent, shmAdmin);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            if (postSequent != null){
                postSequent.setDeleteFlag(true);
                postService.savePost(postSequent);
            }
            model.addAttribute("errorMsg", getMessage("SH_E000002", postSequent.getPostId().toString()));
        }
        return "post-patrol-sequent";
    }

    @RequestMapping(value = "post-patrol-detail", method = RequestMethod.GET)
    public String getPostPatrolDetail(Model model, @RequestParam("postId") Long postId) throws Exception {
        try {
        ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        ShmPost post = null;
            post = postService.showPostPatrolDetailById(postId);
            if (post == null) {
                LOGGER.error("ERROR: The post does not exist. Post Id: " + postId);
                model.addAttribute("errorMsg", getMessage("SH_E100089"));
                return "post-patrol-detail";
            } else {
                if (postService.checkPostInPatrolProcess(postId, shmAdmin.getAdminId())) {
                    model.addAttribute("patrol_warning", getMessage("SH_E100118"));
                    return "redirect:post-patrol";
                }
                ShmPost result = postService.buildListImagePathsForPost(post);
                String postOwnerAvatarPath = null;
                final ShmUser shmUser = result.getShmUser();
                if(shmUser != null){
                    postOwnerAvatarPath = userService.buildOriginalAvatarPathForUser(shmUser.getAvatar());
                    model.addAttribute("postOwnerAvatarPath", postOwnerAvatarPath);
                    if(shmUser.getAddress() != null){
                        ShmAddr shmAddr = shmUser.getAddress();
                        shmUser.getAddress().setFullAreaName(addressService.getFullAddress(shmAddr));
                    }
                }

                long countPost = postService.getCountPostById(post.getShmUser().getId());
                model.addAttribute("countPost", countPost);
                model.addAttribute("postDetail", result);
                model.addAttribute("isPatrolSequent", 0);
                model.addAttribute("sequentNumber", 0);

                //block post from other admins without post was oke.
                if (!post.getPostPtrlStatus().equals(ShmPost.PostPtrlStatusEnum.CENSORED)) {
                    post.setIsInPatrol(true);
                    postService.savePostPatrol(post, shmAdmin);
                }
            }

        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            model.addAttribute("errorMsg", getMessage("SH_E000001"));
        }
        return "post-patrol-detail";
    }

    @RequestMapping(value = "/web/process-post-patrol", method = RequestMethod.POST)
    @ResponseBody
    public ShtMemoPost processPost(@RequestBody PostMemoRequest postMemoRequest) throws SchedulerException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");
        ShtMemoPost memoPost = memoPostService.processPost(admin, postMemoRequest);
        memoPost.setSequentNumber(postMemoRequest.getSequentNumber());

        if (postMemoRequest.getPatrolType() != null && (postMemoRequest.getPatrolType().intValue() == 2)) {
            // send notify to mailbox
            ShmUser shmUser = userService.getUserById(memoPost.getPost().getShmUser().getId());
            Long qaId = null;
            String reason = memoPostService.buildMessageSuspendForUser(postMemoRequest);
            if(shmUser != null){
                ShtQa qa = new ShtQa();
                qa.setQaTitle(messageSource.getMessage("QA_TITLE_SUSPENDED_POST",null,null));
                qa.setFirstQaMsg(messageSource.getMessage("QA_CONTENT_SUSPENDED_POST", null, null).replace("<postName>",memoPost.getPost().getPostName()).replace("<reason>",reason));
                qaId = qaService.createNotifyFromAdmin(shmUser,qa, admin).getQaId();
            }

            mailService.sendEmailSuspendPost(memoPost.getPost().getPostName(), shmUser.getEmail(), shmUser.getNickName(),reason);
            final String msgCont = messageSource.getMessage("PUSH_POST_SUSPENDED", null, null);
            final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.POST_HANDLE_SUSPENDED, null, null, null, null, qaId, null,memoPost.getPost().getShmUser().getId() );
            snsMobilePushService.sendNotificationForUserFromAdmin(memoPost.getPost().getShmUser().getId(), pushMsg, pushMsgDetail);
        }

        return memoPost;
    }

    @RequestMapping(value = "/web/post/patrol-history", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PatrolHistoryDTO>> getMemoListOfUser(@RequestParam("postId") Long postId) {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        List<PatrolHistoryDTO> memoPosts = memoPostService.getPatrolHistoryByPostId(postId);
        return new ResponseEntity<>(memoPosts, HttpStatus.OK);
    }

    @RequestMapping(value = "/web/post/report-hostory", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ShtUserRprt>> getReportOfPost(@RequestParam("postId") Long postId) {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        List<ShtUserRprt> reported = userRprtService.getPostReports(postId);
        return new ResponseEntity<>(reported, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/web/memo-post", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PatrolHistoryDTO>> getMemoPost(@RequestParam(value = "postId") Long postId) {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        List<PatrolHistoryDTO> memoPostList = memoPostService.getMemoPostListByPostId(postId);
        return new ResponseEntity<>(memoPostList, HttpStatus.OK);
    }

    @RequestMapping(value = "/post-patrol/ok", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ShmPost> processPostToOke(Model model, @RequestParam("postId") Long postId, @RequestParam("sequentNumber") int sequentNumber) throws Exception {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        ShmPost post = postService.processPostToOke(postId, getEmail());
        if (post == null)
            return new ResponseEntity<ShmPost>(post, HttpStatus.CONFLICT);
        post.setSequentNumber(sequentNumber);
        return new ResponseEntity<ShmPost>(post, HttpStatus.OK);
    }

    @RequestMapping(value = "/patrol/handle-user", method = RequestMethod.GET)
    public String goPatrolHandleUserPage(Model model) throws Exception {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        model.addAttribute("totalItems", 0);
        model.addAttribute("userPatrolRequest", new UserPatrolRequest());
        return "patrol-handle-user";
    }

    @RequestMapping(value = "patrol/handle-user/search/{pageIndex}", method = RequestMethod.GET)
    @ResponseBody
    public Page<ShmUserDTO> searchPatrolHandleUserPage(Model model, @ModelAttribute("") UserPatrolRequest userPatrolRequest) throws Exception {
        ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        final String msg = validateUserRequest(userPatrolRequest);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(msg)) {
            return null;
        }

        //clean data not processed
        userService.cleanDataNotProcessedByAdmin(shmAdmin.getAdminId());

        Page<ShmUserDTO> result = userService.getUserListForPatrolSite(userPatrolRequest, createPage100Item(userPatrolRequest.getPageIndex()));
        if (CollectionUtils.isNotEmpty(result.getContent())) {
            model.addAttribute("totalItems", result.getTotalElements());
        }
        model.addAttribute("curPage", userPatrolRequest.getPageIndex());
        model.addAttribute("userList", result.getContent());
        model.addAttribute("totalPage", result.getTotalPages());
        return result;
    }

    @RequestMapping(value = "/users-patrol/csv", method = RequestMethod.GET)
    public void exportUsersPatrolCSV(Model model, @ModelAttribute("postPatrolRequest") UserPatrolRequest request, HttpServletResponse response) throws IOException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        userService.exportUserPatrolCsv(admin, request, createPage100Item(request.getPageIndex()), response);
        adminCsvHstService.save(admin, ShtAdminCsvHst.CSV_TYPE.USER_PATROL);
    }

    private String validateUserRequest(UserPatrolRequest request) {
        if (request.getUpdatedAtFrom() != null && request.getUpdatedAtTo() != null && request.getUpdatedAtTo().before(request.getUpdatedAtFrom()) ) {
            return getMessage("SH_E100048");
        }
        if (request.getCensoredFrom() != null && request.getCensoredTo() != null && request.getCensoredTo().before(request.getCensoredFrom()) ) {
            return getMessage("SH_E100048");
        }

        return null;
    }

    @RequestMapping(value = "patrol/handle-user/{userId}", method = RequestMethod.GET)
    public String searchDetailUser(Model model, @PathVariable("userId") Long userId, HttpServletRequest httpRequest) throws Exception {
        ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        try {
        ShmUserDTO shmUserDTO = userService.getUserDTOById(userId);
            if (userService.chechkUserDeleted(userId)){
                if(userService.chechkUserDeleted(userId)) {
                    model.addAttribute("errorMsg", getMessage("SH_E100132"));
                    return "redirect:/backend/user-detail?userId=" + userId;
                }
            }
            if (shmUserDTO == null) {
                model.addAttribute("errorMsg", getMessage("SH_E100088"));
                return "patrol-handle-user-detail";
            }

            //Check user is processing or not
            if (shmUserDTO.getIsInPatrol() != null && shmUserDTO.getIsInPatrol()) {
                if (!userService.checkUserInProcessByCurrentAdmin(shmAdmin.getAdminId(), userId)) {
                    model.addAttribute("errorMsg", getMessage("SH_E100128"));
                    return "redirect:/backend/patrol/handle-user";
                }
            }

            ShmUser user = userService.getUserById(userId);
            if (!user.getPtrlStatus().equals(ShmUser.PtrlStatus.CENSORED) && !user.getCtrlStatus().equals(ShmUser.CtrlStatus.OK)) {
                user.setIsInPatrol(true);
                userService.save(user);
                userService.saveUserPatrol(user, shmAdmin);
            }

            shmUserDTO.setUserAvatarPath(userService.buildOriginalAvatarPathForUser(shmUserDTO.getAvatar()));
            shmUserDTO.setTotalPost(postService.getCountPostById(userId));
            model.addAttribute("user", shmUserDTO);
            model.addAttribute("isPatrolSequent", 0);
            model.addAttribute("sequentNumber", 0);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            throw e;
        }
        return "patrol-handle-user-detail";
    }

    @RequestMapping(value = "user-patrol-sequent", method = RequestMethod.GET)
    public String getUserPatrolSequent(Model model, @RequestParam("sequentNumber") int sequentNumber) throws Exception {
        ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        ShmUserDTO userDTO = null;
        try {
            //clean data not processed
            userService.cleanDataNotProcessedByAdmin(shmAdmin.getAdminId());
            userDTO = userService.getUserPatrolSequent();

            if (userDTO == null) {
                model.addAttribute("errorMsg", getMessage("SH_E100091"));
                return "user-patrol-sequent";
            } else {
                userDTO.setUserAvatarPath(userService.buildOriginalAvatarPathForUser(userDTO.getAvatar()));
                userDTO.setTotalPost(postService.countTotalPostsByUserId(userDTO.getUserId()));
                model.addAttribute("user", userDTO);
                model.addAttribute("isPatrolSequent", 1);
                model.addAttribute("sequentNumber", sequentNumber);
                model.addAttribute("maxSequentNumber", maxSequentNumber);
            }
            //Block user from other admins
            ShmUser shmUser = userService.getUserById(userDTO.getUserId());
            shmUser.setIsInPatrol(true);
            userService.save(shmUser);
            userService.saveUserPatrol(shmUser, shmAdmin);

        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            model.addAttribute("errorMsg", getMessage("SH_E000002", userDTO.getUserId().toString()));
        }
        return "user-patrol-sequent";
    }

    @RequestMapping(value = "patrol/handle-user/user-not-ok", method = RequestMethod.GET)
    @ResponseBody
    public Long getNextUserNotOk(@RequestParam(value = "currentUserId", required = true) Long userId) {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        Long nextUserId = userService.getNextUserId(userId);
        return nextUserId;
    }

    @RequestMapping(value = "patrol/handle-user/user-ok", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ShmUser> censoreUserOk(@RequestParam Long userId, @RequestParam("sequentNumber") int sequentNumber) throws SchedulerException {
        ShmUser user = null;
        try {
            ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
            String email = getEmail();
            user = userService.censoredUserOk(userId, email);

            adminLogService.saveAdminLogUser(user,shmAdmin);

            if (user == null)
                return new ResponseEntity<ShmUser>(user, HttpStatus.CONFLICT);
            user.setSequentNumber(sequentNumber);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            throw e;
        }
        return new ResponseEntity<ShmUser>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "patrol/handle-user/user-memmo", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PatrolHistoryDTO>> getMemoUser(@RequestParam(value = "userId") Long userId) {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        List<PatrolHistoryDTO> memoUserList = memoUserService.getAllMemoUserByUserId(userId);
        return new ResponseEntity<>(memoUserList, HttpStatus.OK);
    }

    @RequestMapping(value = "/web/process-user-patrol", method = RequestMethod.POST)
    @ResponseBody
    public PatrolHistoryDTO processUserNGorPending(@RequestBody UserNGRequest userNGRequest) throws SchedulerException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");
        PatrolHistoryDTO memoUser = memoUserService.processUserNg(admin, userNGRequest);
        memoUser.setSequentNumber(userNGRequest.getSequentNumber());

        if (ShmUser.CtrlStatus.SUSPENDED.toString().equals(userNGRequest.getCtrlStatus())) {
            // send notify to mailbox
            ShmUser shmUser = userService.getUserById(userNGRequest.getUserId());
            Long qaId = null;
            String reason = memoPostService.buildMessageSuspendForUser(userNGRequest);
            if(shmUser != null){
                ShtQa qa = new ShtQa();
                qa.setQaTitle(messageSource.getMessage("QA_TITLE_SUSPENDED_USER",null,null));
                qa.setFirstQaMsg(messageSource.getMessage("QA_CONTENT_SUSPENDED_USER", null, null).replace("<reason>",reason));
                qaId = qaService.createNotifyFromAdmin(shmUser,qa, admin).getQaId();
            }

            mailService.sendEmailSuspendUser(shmUser.getNickName(), shmUser.getEmail(),reason);
            final String msgCont = messageSource.getMessage("PUSH_USER_SUSPENDED", null, null);
            final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_HANDLE_SUSPENDED, null, null, null, null, qaId, null, userNGRequest.getUserId());
            snsMobilePushService.sendNotificationForUser(userNGRequest.getUserId(), pushMsg, pushMsgDetail);

        }
        return memoUser;
    }

    @RequestMapping(value = "patrol/handle-user/user-memmo-all", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PatrolHistoryDTO>> getAllMemoWithUser(@RequestParam("userId") Long userId) {
        adminService.getAdminForSuperAdminAndAdminAndSitePatrol(getEmail());
        List<PatrolHistoryDTO> memoPosts = memoUserService.getAllHisProcessUserByUserId(userId);
        return new ResponseEntity<>(memoPosts, HttpStatus.OK);
    }

}
