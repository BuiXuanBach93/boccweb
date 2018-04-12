package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.controller.api.request.ShmUserUpdateRequest;
import jp.bo.bocc.controller.api.request.ShtUserRprtBodyRequest;
import jp.bo.bocc.controller.api.request.UserRequestBody;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.entity.dto.ShmUserForViewDTO;
import jp.bo.bocc.entity.mapper.UserMapper;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.*;
import jp.bo.bocc.service.impl.UserServiceImpl;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.RegAuthentication;
import jp.bo.bocc.system.apiconfig.resolver.JsonArg;
import jp.bo.bocc.system.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author manhnt
 */

@RestController
public class UserController extends BoccBaseController {

    private final static Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private TalkPurcService talkPurcService;

    @Autowired
    private RequestContext requestContext;

    @Autowired
    UserRevService userRevService;
    @Autowired
    private MailService mailService;

    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    SNSMobilePushService snsMobilePushService;

    @GetMapping("/users/{id:\\d+}")
    @ResponseBody
    @AccessTokenAuthentication
    public ShmUser getUser(@PathVariable long id) {
        ShmUser user = requestContext.getUser();
        if (user.getId() != id) {
            throw new BadRequestException("Invalid token");
        }
        return user;
    }


    /**
     * STEP 1: Verify bs account
     *
     * @param bsid
     * @param bspw
     * @return 200 OK if BS Account is valid
     */
    @PostMapping("/verification")
    @ResponseBody
    @NoAuthentication
    public void verifyBsAccount(@JsonArg String bsid, @JsonArg String bspw) {
        userService.verifyBsAccount(bsid, bspw);
    }

    /**
     * STEP 2: New registration
     *
     * @param email new email
     * @return userId and registrationToken (201 Created)
     */
    @PostMapping("/users")
    @ResponseBody
    @NoAuthentication
    public ResponseEntity<Map<String, Object>> newRegistration(@JsonArg String bsid, @JsonArg String bspw, @JsonArg String email) {
        ShtUserToken regToken = userService.newRegistration(bsid, bspw, email);

        LinkedHashMap<String, Object> responseBody = new LinkedHashMap<String, Object>() {{
            put("userId", regToken.getUser().getId());
            put("registrationToken", regToken.getTokenString());
        }};

        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    /*************************************** START USING REGISTRATION TOKEN ***************************************/

    /**
     * STEP 3: Activate email
     *
     * @param activationToken email-activation token
     * @return 200 OK
     */
    @PostMapping("/users/{id:\\d+}/emails/activations")
    @ResponseBody
    @RegAuthentication
    public void activateEmail(@PathVariable("id") Long userId, @JsonArg String activationToken) {
        ShmUser requestUser = requestContext.getUser();
        if (!userId.equals(requestUser.getId())) {
            throw new BadRequestException("Invalid user id");
        }
        userService.activateEmail(requestUser, activationToken);
    }

    /**
     * STEP 4: Input phone number
     *
     * @param phoneNumber
     * @return 201 Created
     */
    @PostMapping("/users/{id:\\d+}/phones")
    @ResponseBody
    @RegAuthentication
    public ResponseEntity newPhoneNumber(@PathVariable("id") Long userId, @JsonArg("phone") String phoneNumber) {

        if (!userId.equals(requestContext.getUser().getId())) {
            throw new BadRequestException("Invalid user id");
        }
        userService.newPhoneNumber(requestContext.getUser(), phoneNumber);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * STEP 5: Activate phone number
     *
     * @param activationCode
     * @return 200 OK
     */
    @PostMapping("/users/{id:\\d+}/phones/activations")
    @ResponseBody
    @RegAuthentication
    public void activatePhone(@PathVariable("id") Long userId, @JsonArg String activationCode) {
        if (!userId.equals(requestContext.getUser().getId())) {
            throw new BadRequestException("Invalid user id");
        }
        userService.activatePhone(requestContext.getUser(), activationCode);
    }

    /**
     * STEP 6 & 7: Input password and update profile
     *
     * @param newUser user info with new password
     * @return 200 OK
     */
    @PatchMapping("/users/{id:\\d+}")
    @ResponseBody
    @RegAuthentication
    @AccessTokenAuthentication
    public Object update(@PathVariable("id") Long userId, @RequestBody ShmUser newUser) throws Exception {
        if (!userId.equals(requestContext.getUser().getId())) {
            throw new BadRequestException("Invalid user id");
        }

        newUser.setId(userId);
        ShtUserToken token = requestContext.getToken();

        // Validate password
        userService.validateUserPassword(newUser.getPassword());

        ShmUser savedUser = userService.save(newUser, token.getTokenType() == ShtUserToken.TokenType.REGISTRATION_TOKEN ? token : null);
        if (requestContext.getToken().getTokenType() == ShtUserToken.TokenType.REGISTRATION_TOKEN
                && savedUser.getStatus() == ShmUser.Status.ACTIVATED) {

            //user finish registration
            Pair<ShtUserToken, ShtUserToken> tokenPair = tokenService.createAccessToken(savedUser);
            try{
                if (StringUtils.isNotEmpty(newUser.getDeviceToken())) {
                    userService.saveDeviceTokenRegistration(userId, newUser.getDeviceToken(), newUser.getOsType());
                }
            }catch (Exception ex){
                LOGGER.error("Error: " + ex.getMessage());
            }
            return new LinkedHashMap<String, Object>() {{
                put("accessToken", tokenPair.getLeft().getTokenString());
                put("refreshToken", tokenPair.getRight().getTokenString());
            }};
        }

        // create user setting
        userSettingService.createUserSetting(savedUser);

        return savedUser;
    }

    @PutMapping("/users/active-email")
    @AccessTokenAuthentication
    public void activeNewEmail(@JsonArg String activationToken, @JsonArg String email) {
        ShmUser requestUser = requestContext.getUser();
        userService.activateNewEmail(requestUser, activationToken, email);
    }

    @PutMapping("/users/change-email")
    @AccessTokenAuthentication
    public ResponseEntity<Void> changeEmail(@JsonArg String email) throws Exception {
        ShmUser shmUser = requestContext.getUser();
        userService.createEmailActiveToken(shmUser, email);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PutMapping("/users/active-phone")
    @AccessTokenAuthentication
    public void activeNewPhone(@JsonArg String activationCode, @JsonArg String phoneNumber) {
        ShmUser requestUser = requestContext.getUser();
        requestUser.setPhone(phoneNumber);
        userService.activateNewPhone(requestUser, activationCode);
    }

    @PostMapping("/users/change-phone")
    @AccessTokenAuthentication
    public Map<String, String> changePhoneNumber(@JsonArg String phoneNumber) {
        return userService.sendPhoneActivationCode(requestContext.getUser(), phoneNumber);
    }

    @PostMapping("/users/forgot-password")
    @NoAuthentication
    public ResponseEntity forgotPassword(@JsonArg String email, @JsonArg String phoneNumber) {
        userService.forgotPassword(email, phoneNumber);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/users/edit")
    @ResponseBody
    @AccessTokenAuthentication
    public Object editUser(@RequestBody ShmUserUpdateRequest newUser) throws Exception {
        Long userId = getUserIdUsingApp();
        if (!userId.equals(requestContext.getUser().getId())) {
            throw new BadRequestException("Invalid user id");
        }

        ShmUser savedUser = userService.editUserInfo(userId, newUser);
        if (savedUser.getAddress() != null && savedUser.getAddress().getAddrParentId() != null) {
            ShmAddr province = addressService.getAddress(savedUser.getAddress().getAddrParentId());
            savedUser.setProvince(province);
        }
        return savedUser;
    }

    @PutMapping("/users/tend-to-leave")
    @AccessTokenAuthentication
    public ResponseEntity<Void> tendToLeave(@RequestBody UserRequestBody userRequestBody) throws Exception {
        ShmUser shmUser = requestContext.getUser();
        userService.tendToLeave(userRequestBody, shmUser);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PutMapping("/users/change-password")
    @AccessTokenAuthentication
    public Map<String, Object> changePassword(@RequestBody UserRequestBody userRequestBody) throws Exception {
        ShmUser shmUser = requestContext.getUser();
        ShtUserToken userToken = requestContext.getToken();
        return userService.changePassword(userRequestBody, shmUser, userToken);
    }

    @PostMapping("/users/favorite")
    @AccessTokenAuthentication
    public ResponseEntity<Void> likePost(@RequestParam(value = "postId") Long postId, @RequestParam(value = "status") String status) throws Exception {
        final ShmUser shmUser = getUserUsingApp();
        userService.likePost(shmUser.getId(), postId, status);
        ShmPost shmPost = postService.getPost(postId);
        final ShmUser ownerPost = shmPost.getShmUser();
        final boolean checkTurnOffPush = userSettingService.checkReceivePushOn(ownerPost.getId());
        final boolean checkPushFavorite = userSettingService.checkReceivepushFavorite(ownerPost.getId());
        if (checkTurnOffPush) {
            if (checkPushFavorite) {
                final ShtUserFvrt.UserFavoriteEnum userFavoriteEnum = ShtUserFvrt.UserFavoriteEnum.valueOf(status);
                if (userFavoriteEnum == ShtUserFvrt.UserFavoriteEnum.LIKED) {
                    String msgContent = getMessage("PUSH_USER_FAVORITE");
                    if (ownerPost != null)
                        snsMobilePushService.sendNotificationForUser(PushMsgDetail.NtfTypeEnum.USER_FAVORITE, ownerPost.getId(), postId, msgContent);
                }
            }
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @PostMapping("/user/create-reports")
    @AccessTokenAuthentication
    public ResponseEntity<Void> createReport(@RequestBody ShtUserRprtBodyRequest shtUserRprtBodyRequest) throws Exception {
        final ShmUser shmUser = getUserUsingApp();
        ShmPost shmPost = postService.getPost(shtUserRprtBodyRequest.getPostId());
        final ShtUserRprt.UserReportTypeEnum userRprtType = ShtUserRprt.UserReportTypeEnum.valueOf(shtUserRprtBodyRequest.getRprtType());
        final String rprtCont = shtUserRprtBodyRequest.getRprtCont();
        userService.createReport(shmUser, shmPost, userRprtType, rprtCont);
        mailService.sendEmailForReport(shmUser.getEmail(), shmPost.getPostName(), userRprtType.value(), rprtCont, shmUser.getNickName());
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }


    @PostMapping(value = "/talk/purcs/block")
    @AccessTokenAuthentication
    public ResponseEntity<Void> blockUser(@RequestParam("talkPurcId") Long talkPurcId) throws Exception {
        ShmUser fromUser = getUserUsingApp();
        userService.blockUser(fromUser, talkPurcId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping(value = "/talk/purcs/mute")
    @AccessTokenAuthentication
    public ResponseEntity<Void> muteMsg(@RequestParam("talkPurcId") Long talkPurcId) throws Exception {
        ShmUser fromUser = getUserUsingApp();
        userService.muteUser(fromUser, talkPurcId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping(value = "/users/mypage")
    @AccessTokenAuthentication
    public ResponseEntity<ShmUserDTO> getUser() throws Exception {
        Long userId = getUserIdUsingApp();
        if (userId == null) {
            return new ResponseEntity<ShmUserDTO>(HttpStatus.NOT_FOUND);
        }
        ShmUserDTO userDTO = userService.getUserProfile(userId);
        String bsId = userDTO.getUserBsid();
        if (StringUtils.isNotEmpty(bsId) && bsId.length() > 6) {
            String legalId = bsId.substring(0, 6);
            List<ShtGroupPblDetail> pblDetails = userService.getGroupPblDetails(legalId);
            if (pblDetails.size() > 0 && pblDetails.get(0).getGroupPublish() != null) {
                userDTO.setPublishGroupName(pblDetails.get(0).getGroupPublish().getGroupName());
            }
        }
        userDTO.setIsSameCompany(false);
        return new ResponseEntity<ShmUserDTO>(userDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/users/userpage/{userId}")
    @AccessTokenAuthentication
    public ResponseEntity<ShmUserDTO> getUser(@PathVariable("userId") Long userId) throws Exception {
        ShmUserDTO userDTO = userService.getUserProfile(userId);
        if (userDTO == null) {
            return new ResponseEntity<ShmUserDTO>(HttpStatus.NOT_FOUND);
        }
		/* Check current user and other user have same company with conditions */
        ShmUser currentUser = getUserUsingApp();
        if (StringUtils.isNotEmpty(userDTO.getUserBsid()) && StringUtils.isNotEmpty(currentUser.getBsid())
                && userDTO.getUserBsid().length() > 6 && currentUser.getBsid().length() > 6
                && userDTO.getUserBsid().substring(0, 6).equals(currentUser.getBsid().substring(0, 6))) {
            userDTO.setIsSameCompany(true);
            // do not set same campany for owner
            if (currentUser.getId().intValue() == userId.intValue()) {
                userDTO.setIsSameCompany(false);
            }
        } else {
            userDTO.setIsSameCompany(false);
        }
        return new ResponseEntity<ShmUserDTO>(userDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/users/userpage-no-login/{userId}")
    @NoAuthentication
    public ResponseEntity<ShmUserDTO> getUserNoLogin(@PathVariable("userId") Long userId) throws Exception {
        ShmUserDTO userDTO = userService.getUserProfile(userId);
        if (userDTO == null) {
            return new ResponseEntity<ShmUserDTO>(HttpStatus.NOT_FOUND);
        }
        userDTO.setIsSameCompany(false);
        return new ResponseEntity<ShmUserDTO>(userDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/users/reset-pw")
    @NoAuthentication
    public void resetPassword(@JsonArg String email, @JsonArg("phone") String phoneNumber) {
        userService.resetPassword(email, phoneNumber);
    }

    @GetMapping(value = "/users/extra-info")
    @AccessTokenAuthentication
    public ShmUserForViewDTO getDetailUserInfo(@RequestParam("userId") Long userId) throws ParseException, SchedulerException {
        final ShmUserDTO shmUserDTO = userService.getUserDTOById(userId);
        ShmUserForViewDTO result = UserMapper.mapUserDTOIntoUserForOtherViewDTO(shmUserDTO);
        Long goodNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.GOOD);
        Long fairNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.FAIR);
        Long badNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.BAD);
        result.setGoodReviewNumber(goodNumber);
        result.setNormalReviewNumber(fairNumber);
        result.setBadReviewNumber(badNumber);
        return result;
    }

    @GetMapping(value = "/users/favorite-history")
    @AccessTokenAuthentication
    public Page<ShmPostDTO> getPostHistory(@RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size,
                                           @RequestParam(value = "postType", required = false) String postType,
                                           @RequestParam(value = "sortField", required = false) String sortField,
                                           @RequestParam(value = "price", required = false) Long price) {
        Page<ShmPostDTO> result = null;
        Pageable pageable = createPage(page, size);
        ShmUser user = requestContext.getUser();
        result = postService.getFavoritePostHistory(pageable, postType, user.getId(), sortField, price);
        return result;
    }

    @GetMapping("/users/{userId}/check-deleted")
    @ResponseBody
    @AccessTokenAuthentication
    public boolean checkUserDeleted(@PathVariable long userId) {
        return userService.chechkUserDeleted(userId);
    }

}
