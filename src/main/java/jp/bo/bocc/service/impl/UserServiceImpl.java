package jp.bo.bocc.service.impl;

import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jp.bo.bocc.controller.api.request.ShmUserUpdateRequest;
import jp.bo.bocc.controller.api.request.UserRequestBody;
import jp.bo.bocc.controller.web.request.UserPatrolRequest;
import jp.bo.bocc.controller.web.request.UserSearchRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.entity.dto.UserCsvDTO;
import jp.bo.bocc.entity.dto.UserPatrolCsvDTO;
import jp.bo.bocc.entity.mapper.UserMapper;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.enums.OSTypeEnum;
import jp.bo.bocc.enums.PlatformEnum;
import jp.bo.bocc.helper.*;
import jp.bo.bocc.jobs.JobHandleUserLeft;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.repository.*;
import jp.bo.bocc.repository.criteria.BaseSpecification;
import jp.bo.bocc.repository.criteria.SearchCriteria;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.apiconfig.bean.AppContext;
import jp.bo.bocc.system.config.Log;
import jp.bo.bocc.system.exception.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author manhnt
 */

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFvrtService userFvrtService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private QaService qaService;

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private PostService postService;

    @Autowired
    private MailService mailService;

    @Autowired
    UserRprtService userRprtService;

    @Autowired
    TalkPurcService talkPurcService;

    @Autowired
    UserTalkPurcRepository userBlockRepository;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    GroupPblDetailService groupPblDetailService;

    @Value("${mail.admin}")
    private String mailAdmin;

    @Value("${job.user.left.wait.day}")
    private int userLeftWaitDay;

    @Value("${job.sync.user.hour}")
    private int jobHour;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private UserRevService userRevService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRevRepository userRevRepo;

    @Autowired
    private UserTalkPurcRepository userTalkPurcRepository;

    @Autowired
    private TalkPurcMsgService talkPurcMsgService;

    @Value("${image.server.url}")
    private String imgServer;
    @Autowired
    AdminService adminService;
    @Autowired
    AdminLogService adminLogService;

    @Autowired
    MemoUserService memoUserService;
    @Autowired
    private UserNtfRepository userNtfRepository;
    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private SNSMobilePushService snsMobilePushService;

    @Autowired
    ExportCsvService exportCsvService;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Value("${bs.mno}")
    private String bsMno;

    @Value("${bs.id.blackList}")
    private String bsIdBlackList;

    @Autowired
    @Qualifier("createPlatformApplicationResultAPNS")
    private CreatePlatformApplicationResult platformApplicationResultAPNS;

    @Autowired
    @Qualifier("createPlatformApplicationResultAPNS_SANDBOX")
    private CreatePlatformApplicationResult platformApplicationResultAPNS_SANDBOX;

    @Autowired
    @Qualifier("createPlatformApplicationResultGCM")
    private CreatePlatformApplicationResult platformApplicationResultGCM;

    @Autowired
    @Qualifier("createTopicResultANDROID")
    public CreateTopicResult createTopicANDROID;

    @Autowired
    @Qualifier("createTopicResultIOS")
    public CreateTopicResult createTopicIOS;

    @Value("${user.system.id}")
    private Long systemUserId;

//    @Autowired
//    FireBaseService fireBaseService;

    @Transactional(readOnly = true)
    @Override
    public ShmUser getUserById(long id) throws ServiceException {
        ShmUser user = userRepository.findOne(id);
        if (user == null) throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));
        Long postNumber = postService.countPostByUserId(id);
        user.setPostNumber(postNumber);
        return user;
    }

    @Override
    public ShmUser getSystemUser() {
        return userRepository.findOne(systemUserId);
    }

    @Override
    public ShmUser findUserById(Long userId) {
        return userRepository.findOne(userId);
    }

    /**
     * Find user with specified email & password
     *
     * @param email
     * @param password
     * @return null if no user with such password found.
     */
    @Transactional(readOnly = true)
    @Override
    public ShmUser findUserByEmailAndPassword(String email, String password) {
        List<ShmUser> users;
        try {
            users = userRepository.findByEmailAndPassword(email, StringUtils.getMD5(password));
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException(messageSource.getMessage("SH_E100054", null, null), e);
        }

        if (users.isEmpty()) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            List<ShmUser> activeUsers = users
                    .stream().filter(u ->
                            u.getStatus() == ShmUser.Status.ACTIVATED).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(activeUsers) && activeUsers.size() == 1) {
                return activeUsers.get(0);
            } else {
                return null;
            }
        }
    }

    @Override
    public void tendToLeave(UserRequestBody userRequestBody, ShmUser currentUser) throws Exception {
        ShmUser shmUser = findUserByEmailAndPassword(userRequestBody.getEmail(), userRequestBody.getPassword());
        if (shmUser == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100119", null, null));
        }
        if (shmUser.getId().intValue() != currentUser.getId().intValue()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100119", null, null));
        }
        if (currentUser.getStatus() == ShmUser.Status.LEFT) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100097", null, null));
        }
        if (currentUser.getStatus() != ShmUser.Status.ACTIVATED) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100100", null, null));
        }
        if (postService.countPostByPostIdAndPostSellStatus(shmUser.getId(), ShmPost.PostSellSatus.TEND_TO_SELL.ordinal()) > 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100098", null, null));
        }
        if (postService.countPostByPostIdAndPostCtrlStatus(shmUser.getId(), ShmPost.PostCtrlStatus.SUSPENDED.ordinal()) > 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100099", null, null));
        }

        // handle when user tend to leave
        handleUserLeft(currentUser.getId());

        try{
            unSubscribeTopic(currentUser.getId());
        }catch (Exception ex){
            LOGGER.error("Error: " + ex.getMessage());
        }
    }

    @Override
    public void handleSyncUserTendToLeave(ShmUser shmUser, Long syncId){
           // save user status
        shmUser.setStatus(ShmUser.Status.TEND_TO_LEAVE);
        shmUser.setTendToLeaveDate(LocalDateTime.now());
        LocalDateTime leftDate = getFirstDayOfNextMonth();
        shmUser.setLeftDate(leftDate);
        shmUser.setSyncExcId(syncId);
        save(shmUser);

        // send email to user
        mailService.sendEmailTendToLeave(shmUser.getEmail(),shmUser.getNickName(),leftDate);

		//push notify to user
		String msgContentForUser = messageSource.getMessage("PUSH_TEND_TO_LEAVE",null,null);
		String msgContentForOwner= messageSource.getMessage("PUSH_TEND_TO_LEAVE_FOR_OWNER", null, null);
		String msgContentForPartner=messageSource.getMessage("PUSH_TEND_TO_LEAVE_FOR_PARTNER", null, null);

		PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContentForUser);
		PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_TEND_TO_LEAVE, null, null, null, null, null);
		snsMobilePushService.sendNotificationForUser(shmUser.getId(), pushMsg, pushMsgDetail);

        try {
            // send msg to talk room and push notification
            List<ShtTalkPurc> openOwnerTalks = talkPurcService.findTalksByOwnerIdOpen(shmUser.getId());
            if (openOwnerTalks != null && openOwnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openOwnerTalks) {
                    if (talkPurc != null && talkPurc.getShmUser() != null) {
                        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
                        msg.setShtTalkPurc(talkPurc);
                        msg.setShmUserCreator(shmUser);
                        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
                        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10019", new Object[]{talkPurc.getShmPost().getShmUser().getNickName(),leftDate.getMonthValue(), leftDate.getDayOfMonth()}, null));
                        talkPurcMsgService.saveMsg(msg);

                        // push notification to partner
                        PushMsgCommon pushMsgForPartner = PushUtils.buildPushMsgCommon(msgContentForPartner);
                        PushMsgDetail pushMsgDetailForPartner = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_TEND_TO_LEAVE_FOR_PARTNER, null, null, talkPurc.getTalkPurcId(), null, null);
                        snsMobilePushService.sendNotificationForUser(talkPurc.getShmUser().getId(), pushMsgForPartner, pushMsgDetailForPartner);
                    }
                }
            }

            List<ShtTalkPurc> openPartnerTalks = talkPurcService.findTalksByPartnerIdOpen(shmUser.getId());
            if (openPartnerTalks != null && openPartnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openPartnerTalks) {
                    if (talkPurc != null && talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
                        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
                        msg.setShtTalkPurc(talkPurc);
                        msg.setShmUserCreator(shmUser);
                        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
                        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10019", new Object[]{talkPurc.getShmUser().getNickName(), leftDate.getMonthValue(), leftDate.getDayOfMonth()}, null));
                        talkPurcMsgService.saveMsg(msg);

                        // push notification to owner
                        PushMsgCommon pushMsgForOwner = PushUtils.buildPushMsgCommon(msgContentForOwner);
                        PushMsgDetail pushMsgDetailForOwner = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_TEND_TO_LEAVE_FOR_OWNER, null, null, talkPurc.getTalkPurcId(), null, null);
                        snsMobilePushService.sendNotificationForUser(talkPurc.getShmPost().getShmUser().getId(), pushMsgForOwner, pushMsgDetailForOwner);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: Handle tend to leave for USER_ID :" + shmUser.getId());
        }

        // send message to tab from admin
        ShtQa qa = new ShtQa();
        qa.setQaTitle(messageSource.getMessage("QA_TITLE_TEND_TO_LEAVE", null, null));
        qa.setFirstQaMsg(messageSource.getMessage("QA_CONTENT_TEND_TO_LEAVE", new Object[]{ leftDate.getMonthValue(), leftDate.getDayOfMonth()},null).replace("<nickName>", shmUser.getNickName()));
        qaService.createNotifyFromAdmin(shmUser, qa, null);
    }

    @Override
    public void handleSyncUserLeft(ShmUser user) {
        // save user status
        user.setStatus(ShmUser.Status.LEFT);
        save(user);

        try {
            // send msg to talk room
            List<ShtTalkPurc> openOwnerTalks = talkPurcService.findTalksByOwnerIdOpen(user.getId());
            if (openOwnerTalks != null && openOwnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openOwnerTalks) {
                    if (talkPurc != null && talkPurc.getShmUser() != null) {
                        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
                        msg.setShtTalkPurc(talkPurc);
                        msg.setShmUserCreator(user);
                        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
                        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10020", new Object[]{talkPurc.getShmPost().getShmUser().getNickName()}, null));
                        ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                    }
                }
            }

            List<ShtTalkPurc> openPartnerTalks = talkPurcService.findTalksByPartnerIdOpen(user.getId());
            if (openPartnerTalks != null && openPartnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openPartnerTalks) {
                    if (talkPurc != null && talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
                        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
                        msg.setShtTalkPurc(talkPurc);
                        msg.setShmUserCreator(user);
                        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
                        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10021", new Object[]{talkPurc.getShmUser().getNickName()}, null));
                        ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                    }
                }
            }

            //push notify to user
            String msgContentForOwner = messageSource.getMessage("PUSH_USER_LEFT_FOR_OWNER", null, null);
            String msgContentForPartner = messageSource.getMessage("PUSH_USER_LEFT_FOR_PARTNER", null, null);

            if (openOwnerTalks != null && openOwnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openOwnerTalks) {
                    if (talkPurc != null && talkPurc.getShmUser() != null) {
                        PushMsgCommon pushMsgForPartner = PushUtils.buildPushMsgCommon(msgContentForPartner);
                        PushMsgDetail pushMsgDetailForPartner = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_TEND_TO_LEAVE_DONE_FOR_PARTNER, null, null, talkPurc.getTalkPurcId(), null, null, null, talkPurc.getShmUser().getId());
                        snsMobilePushService.sendNotificationForUser(talkPurc.getShmUser().getId(), pushMsgForPartner, pushMsgDetailForPartner);
                    }
                }
            }

            if (openPartnerTalks != null && openPartnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openPartnerTalks) {
                    if (talkPurc != null && talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
                        PushMsgCommon pushMsgForOwner = PushUtils.buildPushMsgCommon(msgContentForOwner);
                        PushMsgDetail pushMsgDetailForOwner = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_TEND_TO_LEAVE_DONE_FOR_OWNER, null, null, talkPurc.getTalkPurcId(), null, null, null, talkPurc.getShmPost().getShmUser().getId());
                        snsMobilePushService.sendNotificationForUser(talkPurc.getShmPost().getShmUser().getId(), pushMsgForOwner, pushMsgDetailForOwner);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<ShmUser> getMissingLeftSyncUsersBeforeNow() {
        return userRepository.getMissingLeftSyncUsersBeforeNow();
    }

    LocalDateTime getFirstDayOfNextMonth(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonth = now.plusMonths(1);
        LocalDateTime firstDay = LocalDateTime.of(now.getYear(), nextMonth.getMonthValue(), 1, jobHour, 0);
        return firstDay;
    }


    @Override
    public void unSubscribeTopic(Long id) {
        final List<ShtUserNtf> allSubscribedARNByUserId = userNtfRepository.getAllSubscribeARNByUserId(id);
        for (ShtUserNtf item : allSubscribedARNByUserId) {
            snsMobilePushService.unSubscribeTopic(item.getUserNtfSubscribedARN());
            item.setUserNtfSubscribed(false);
            userNtfRepository.save(item);
        }
    }

    @Override
    public Map<String, Object> changePassword(UserRequestBody userRequestBody, ShmUser currentUser, ShtUserToken userToken) throws Exception {
        ShmUser shmUser = findUserByEmailAndPassword(currentUser.getEmail(), userRequestBody.getPassword());
        if (shmUser == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100125", null, null));
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userRequestBody.getNewPassword())
                || org.apache.commons.lang3.StringUtils.isEmpty(userRequestBody.getRetypePassword())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100116", null, null));
        }
        if (!userRequestBody.getNewPassword().equals(userRequestBody.getRetypePassword())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100117", null, null));
        }
        // validate new password
        validateUserPassword(userRequestBody.getNewPassword());

        ShmUser user = getUserById(currentUser.getId());
        user.setPassword(StringUtils.getMD5(userRequestBody.getNewPassword()));
        save(user);

        // delete current device token
        if (userRequestBody.getDeviceToken() != null) {
            userNtfRepository.deleteCurrentDeviceToken(currentUser.getId(), userRequestBody.getDeviceToken());
        }

        // update all token of user to expired except current token
        tokenService.expiredUserTokenElse(user.getId(), userToken.getTokenString());
        // create new access token
        Pair<ShtUserToken, ShtUserToken> tokenPair = tokenService.createAccessToken(user);
        return serializeAccessToken(tokenPair);
    }

    private Map<String, Object> serializeAccessToken(Pair<ShtUserToken, ShtUserToken> tokenPair) {
        ShtUserToken accessToken = tokenService.getToken(tokenPair.getLeft().getTokenString());
        ShtUserToken refreshToken = tokenPair.getRight();
        return new LinkedHashMap<String, Object>() {{
            put("accessToken", accessToken.getTokenString());
            put("refreshToken", refreshToken.getTokenString());
        }};
    }

    private final String MINFO_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<MINFO>\n" +
            " <UID>%1$s</UID>\n" +
            " <PWD>%2$s</PWD>\n" +
            " <MNO>%3$s</MNO>\n" +
            " <SCD></SCD>\n" +
            " <SHA></SHA>\n" +
            "</MINFO>";

    private static final Pattern BSID_REGEX = Pattern.compile("<UID>(\\d{15})</UID>", Pattern.CASE_INSENSITIVE);

    //STEP 1: BS Account
    @Override
    public String verifyBsAccount(String bsidOrEmail, String bsPassword) {
        try {
            //check bsId in black list
            if (checkBsIdInBlackList(bsidOrEmail)) {
                throw new BadRequestException(messageSource.getMessage("SH_E100149", null, null));
            }
            final String bsVerificationURL = AppContext.getEnv().getProperty("bs.verification.url");
            HttpResponse<String> response = Unirest.get(bsVerificationURL)
                    .queryString("chk", AppContext.getEnv().getProperty("bs.verification.chk"))
                    .queryString("minfo", String.format(MINFO_TEMPLATE, bsidOrEmail, bsPassword, bsMno))
                    .asString();

            if (response.getStatus() / 100 == 2 && response.getBody().contains("<RTN>OK</RTN>")) {
//                if (StringUtils.isNumber(bsidOrEmail)) {
//                    return bsidOrEmail;
//                } else {
                Matcher matcher = BSID_REGEX.matcher(response.getBody());
                if (matcher.find()) {
                    final String group = matcher.group(1);

                    //check bsId in black list
                    if (checkBsIdInBlackList(group)) {
                        throw new BadRequestException(messageSource.getMessage("SH_E100149", null, null));
                    }
                    return group;
                } else {
                    throw new InternalServerErrorException(messageSource.getMessage("SH_E100055", null, null));
                }
//                }
            } else if (response.getStatus() / 100 == 2 && response.getBody().contains("<MCD>001</MCD>") && response.getBody().contains("<RTN>NG</RTN>")) {
                throw new ForbiddenException(messageSource.getMessage("SH_E100051", null, null));
            } else if (response.getStatus() / 100 == 2 && response.getBody().contains("<MCD>006</MCD>") && response.getBody().contains("<RTN>NG</RTN>")) {
                throw new ForbiddenException(messageSource.getMessage("SH_E100149", null, null));
            } else if (response.getStatus() / 100 == 2 && response.getBody().contains("<MCD>003</MCD>") && response.getBody().contains("<RTN>NG</RTN>")) {
                throw new ForbiddenException(messageSource.getMessage("SH_E100151", null, null));
            } else {
                throw new ForbiddenException(messageSource.getMessage("SH_E100056", null, null));
            }
            /* Response example (in case of OK)
            <?xml version="1.0" encoding="utf-8"?>
			<RESULTDATA>
			  <UID>111050000000007</UID>
			  <PID></PID>
			  <RTN>OK</RTN>
			  <MCD></MCD>
			  <MSG></MSG>
			</RESULTDATA>
			*/
        } catch (UnirestException e) {
            throw new InternalServerErrorException(messageSource.getMessage("SH_E100057", null, null));
        }
    }

    //STEP2: Input Email
    @Override
    @Transactional(noRollbackFor = SafeException.class)
    public ShtUserToken newRegistration(String bsidOrEmail, String bsPassword, String email) {

        String bsid = verifyBsAccount(bsidOrEmail, bsPassword);

        //check bsId in black list
        if (checkBsIdInBlackList(bsid)) {
            throw new BadRequestException(messageSource.getMessage("SH_E100149", null, null));
        }

        checkRegisteringEmail(bsid, email); //Check if user are continuing registration

        //Each BSID can create at most 10 account
        verifyBsidLimitAccountCreation(bsid);

        ShmUser user = new ShmUser();
        user.setBsid(bsid);
        user.setEmail(email);
        user.setStatus(ShmUser.Status.EMAIL_UNACTIVATED);
        userRepository.save(user);

        ShtUserToken activationToken = tokenService.createEmailActivationToken(user);
        sendActivationEmail(email, activationToken);

        return tokenService.createRegistrationToken(user); //Also contain infomation about user
    }

    /**
     * Check bsId in black list. <br/>
     * bsid in black list - return true, else return false.
     *
     * @param bsid
     * @return
     */
    private boolean checkBsIdInBlackList(String bsid) {
        List<String> listBlackListBsid = Arrays.asList(bsIdBlackList.split(","));
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(bsid)) {
            if (bsid.length() >= 6) {
                bsid = bsid.substring(0, 6);
                if (listBlackListBsid.contains(bsid)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ShtUserToken createEmailActiveToken(ShmUser shmUser, String email) {
        List<ShmUser> users = userRepository.findByEmail(email);
        if (users != null && users.size() > 0) {
            if (users.stream()
                    .filter(u -> u.getStatus() != ShmUser.Status.LEFT)
                    .count() > 0) {
                throw new BadRequestException(messageSource.getMessage("SH_E100127", null, null));
            }
        }
        shmUser.setEmail(email);
        ShtUserToken activationToken = tokenService.createEmailActivationToken(shmUser);
        sendActivationEmailChanged(email, activationToken);
        return activationToken;
    }

    //STEP3: Activate Email
    @Override
    public void activateEmail(ShmUser user, String activationToken) {
        ShtUserToken token = tokenService.getToken(activationToken);

        if (token == null || !token.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100058", null, null));
        }

        //Check if status = email_unactivated
        switch (user.getStatus()) {
            case EMAIL_UNACTIVATED:
                break; //OK
            default:
                throw cannotCompleteRequestException(user);
        }

        //Check if token expired
        if (token.isExpired()) {
            throw new BadRequestException(messageSource.getMessage("SH_E100130", null, null));
        }

        //expire the token
        token.setExpireIn(0);

        //Each BSID can create at most 10 account
        verifyBsidLimitAccountCreation(user.getBsid());

        //activate email
        user.setStatus(ShmUser.Status.SMS_UNACTIVATED);
        userRepository.save(user);
    }

    @Override
    public void activateNewEmail(ShmUser user, String activationToken, String email) {
        ShtUserToken token = tokenService.getToken(activationToken);

        if (token == null || !token.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100058", null, null));
        }
        //Check if token expired
        if (token.isExpired()) {
            throw new BadRequestException(messageSource.getMessage("SH_E100130", null, null));
        }

        //expire the token
        token.setExpireIn(0);

        //activate email
        ShmUser shmUser = getUserById(user.getId());
        shmUser.setEmail(email);
        userRepository.save(shmUser);
    }

    //STEP4: Input Phone Number
    @Override
    public void newPhoneNumber(ShmUser user, String phoneNumber) {

        LOGGER.info("BEGIN: Send SMS registration to phone number: " + phoneNumber + ". UserID: " + user.getId());

        //Validate phone number
        validatePhoneNumber(phoneNumber);

        //Check if status = sms_unactivated
        switch (user.getStatus()) {
            case SMS_UNACTIVATED:
                break; //OK
            default:
                throw cannotCompleteRequestException(user);
        }

        //Check if phone number is used by another user
        List<ShmUser> users = userRepository.findByPhone(phoneNumber);
        if (users.stream()
                .filter(u ->
                        //Phone number is activated somewhere
                        u.getStatus().compareTo(ShmUser.Status.SMS_UNACTIVATED) > 0 &&
                                u.getStatus() != ShmUser.Status.LEFT
                )
                .count() > 0) {
            throw new ResourceConflictException(messageSource.getMessage("SH_E100060", null, null));
        }

        //Check if user submitted phone above 3 times today
        List<ShtMsgSms> messages = smsService.findSmsByUserId(user.getId());
        long numMsgSentToday = messages.stream().filter(m -> m.getCreatedAt().isAfter(LocalDate.now().atStartOfDay())).count();
        if (numMsgSentToday >= 3) {
            throw new BadRequestException(messageSource.getMessage("SH_E100061", null, null));
        }

        user.setPhone(phoneNumber);
        sendActivationCode(phoneNumber);
        LOGGER.info("END: Send SMS registration to phone number: " + phoneNumber + ". UserID: " + user.getId());
        userRepository.save(user);
    }

    @Override
    public Map<String, String> sendPhoneActivationCode(ShmUser user, String phoneNumber) {
        //Validate phone number
        validatePhoneNumber(phoneNumber);
        List<ShmUser> users = userRepository.findByPhone(phoneNumber);
        if (users.stream()
                .filter(u ->
                        //Phone number is activated somewhere
                        u.getStatus().compareTo(ShmUser.Status.SMS_UNACTIVATED) > 0 &&
                                u.getStatus() != ShmUser.Status.LEFT
                )
                .count() > 0) {
            throw new ResourceConflictException(messageSource.getMessage("SH_E100060", null, null));
        }

        //Check if user submitted phone above 3 times today
        List<ShtMsgSms> messages = smsService.findSmsByUserId(user.getId());
        long numMsgSentToday = messages.stream().filter(m -> m.getCreatedAt().isAfter(LocalDate.now().atStartOfDay())).count();
        if (numMsgSentToday >= 3) {
            String errMsg = messageSource.getMessage("SH_E100061", null, null);
            return new LinkedHashMap<String, String>() {{
                put("errorMsg", errMsg);
                put("errorType", "OVER_BUDGET_DAILY_SMS");
            }};
        }
        sendActivationCode(phoneNumber);
        return new LinkedHashMap<String, String>() {{
        }};
    }

    //STEP5: Activate Phone Number
    @Override
    public void activatePhone(ShmUser user, String activationCode) {
        List<ShmUser> users = userRepository.findByPhone(user.getPhone());
        if (users.stream()
                .filter(u ->
                        //Phone number is activated somewhere
                        u.getStatus().compareTo(ShmUser.Status.SMS_UNACTIVATED) > 0 &&
                                u.getStatus() != ShmUser.Status.LEFT
                )
                .count() > 0) {
            throw new ResourceConflictException(messageSource.getMessage("SH_E100060", null, null));
        }

        ShtMsgSms sms = smsService.getRegistrationSMSByPhoneNo(user.getPhone(), user);
        if (sms == null || !sms.getContent().equals(activationCode)) {
            throw new BadRequestException(messageSource.getMessage("SH_E100062", null, null));
        }

        if (sms.getCreatedAt().plusMinutes(30L).isBefore(LocalDateTime.now())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100063", null, null));
        }

        //Validation Success. Change user status
        user.setStatus(ShmUser.Status.NO_PASSWORD);
        userRepository.save(user);
    }

    @Override
    public void activateNewPhone(ShmUser user, String activationCode) {
        ShtMsgSms sms = smsService.getRegistrationSMSByPhoneNo(user.getPhone(), user);
        if (sms == null || !sms.getContent().equals(activationCode)) {
            throw new BadRequestException(messageSource.getMessage("SH_E100062", null, null));
        }

        if (sms.getCreatedAt().plusMinutes(30L).isBefore(LocalDateTime.now())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100063", null, null));
        }
        userRepository.save(user);
    }

    //STEP6: Input Password & STEP7: Input Profile
    @Override
    public ShmUser save(ShmUser newUser, ShtUserToken regToken) throws ServiceException {
        try {
            if (newUser.getId() == null) {
                // Create new user (for testing only)
                hashPassword(newUser);
                return userRepository.save(newUser);
            }

            ShmUser oldUser = userRepository.findOne(newUser.getId());

            if (oldUser.getStatus() == ShmUser.Status.ACTIVATED || oldUser.getStatus() == ShmUser.Status.TEND_TO_LEAVE) {
                if (regToken != null) {
                    //Forbidden: User registering. Require access token here
                    throw new ForbiddenException(messageSource.getMessage("SH_E100064", null, null));
                }
                // Normal update profile
                return updateUser(oldUser, newUser);
            } else if (oldUser.getStatus() == ShmUser.Status.NO_PASSWORD) {
                // Save password only (registration)
                ShmUser passwordActivatedUser = new ShmUser();
                passwordActivatedUser.setPassword(newUser.getPassword());
                passwordActivatedUser.setStatus(ShmUser.Status.NO_PROFILE);
                return updateUser(oldUser, passwordActivatedUser);
            } else if (oldUser.getStatus() == ShmUser.Status.NO_PROFILE) {
                // Update profile (registration)
                newUser.setStatus(ShmUser.Status.ACTIVATED);
                ShmUser savedUser = updateUser(oldUser, newUser);
                //Expire registration token
                if (regToken != null) regToken.setExpireIn(0);
                return savedUser;
            } else {
                throw cannotCompleteRequestException(newUser);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException(messageSource.getMessage("SH_E100065", null, null));
        }
    }

    @Override
    public void forgotPassword(String email, String phoneNumber) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(email)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100145", null, null));
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(phoneNumber)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100146", null, null));
        }
        List<ShmUser> users = userRepository.findByEmail(email);
        if (CollectionUtils.isEmpty(users)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100152", null, null));
        } else {
            final List<ShmUser> collect = users.stream().filter(item -> phoneNumber.equals(item.getPhone())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect))
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100148", null, null));
        }
        users = users.stream().filter(item -> item.getStatus() == ShmUser.Status.ACTIVATED).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(users)) {
            for (ShmUser shmUser : users) {
//            if(shmUser.getStatus() == ShmUser.Status.LEFT){
//                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100124", null, null));
//            }
//            if (shmUser.getStatus() != ShmUser.Status.ACTIVATED) {
//                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100120", null, null));
//            }

                int passwordLength = Integer.parseInt(AppContext.getEnv().getProperty("user.default_password.length"));
                String newPassword = StringUtils.randomPassword(passwordLength);
                mailService.sendEmailForgotPassword(email, shmUser.getNickName(), newPassword);

                // update password
                try {
                    shmUser.setPassword(StringUtils.getMD5(newPassword));
                    save(shmUser);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100120", null, null));
        }
    }

    @Override
    public void delete(long userId) {
        if (userRepository.exists(userId)) {
            userRepository.delete(userId);
        } else {
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));
        }
    }

    private UserSearchRequest convertUserSttToList(UserSearchRequest request) {
        List<Short> userStt = new ArrayList<>();
        if (request.getActiveStt() != null)
            userStt.add(request.getActiveStt());
        if (request.getLeftStt() != null)
            userStt.add(request.getLeftStt());
        if (request.getReservedStt() != null)
            userStt.add(request.getReservedStt());
        request.setUserStt(userStt);
        return request;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShmUser> searchUserByConditions(UserSearchRequest user, Pageable pageable) {

        convertUserSttToList(user);
        BaseSpecification firstNameSpec = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(user.getFirstName())) {
            firstNameSpec = new BaseSpecification(new SearchCriteria("firstName", "like_in", StringUtils.convertStringToList(user.getFirstName(), ",")));
        }

        BaseSpecification lastNameSpec = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(user.getLastName())) {
            lastNameSpec = new BaseSpecification(new SearchCriteria("lastName", "like_in", StringUtils.convertStringToList(user.getLastName(), ",")));
        }

        BaseSpecification maleSpec = null;
        if (user.getMale() != null) {
            maleSpec = new BaseSpecification(new SearchCriteria("gender", ":", user.getMale()));
        }

        BaseSpecification femaleSpec = null;
        if (user.getFemale() != null) {
            femaleSpec = new BaseSpecification(new SearchCriteria("gender", ":", user.getFemale()));
        }

        if (user.getFemale() != null && user.getMale() != null) {
            maleSpec = null;
            List<Integer> gender = new ArrayList<>();
            gender.add(ShmUser.Gender.FEMALE.ordinal());
            gender.add(ShmUser.Gender.MALE.ordinal());
            femaleSpec = new BaseSpecification(new SearchCriteria("gender", "in", gender));
        }

        BaseSpecification birthDaySpec = null;
        if (user.getBirthDay() != null) {
            birthDaySpec = new BaseSpecification(new SearchCriteria("dateOfBirth", ":", user.getBirthDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
        }

        BaseSpecification idSpec = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(user.getLegalId())) {
            idSpec = new BaseSpecification(new SearchCriteria("bsid", "%=", user.getLegalId()));
        }

        BaseSpecification bsidSpec = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(user.getBsid())) {
            bsidSpec = new BaseSpecification(new SearchCriteria("bsid", ":", user.getBsid()));
        }

        BaseSpecification activeSpec = null;
        if (user.getActiveStt() != null) {
            activeSpec = new BaseSpecification(new SearchCriteria("status", ":", user.getActiveStt()));
        }

        BaseSpecification ctrlStatusSpec = null;
        ctrlStatusSpec = new BaseSpecification(new SearchCriteria("ctrlStatus", "!=", ShmUser.CtrlStatus.SUSPENDED.ordinal()));
        if (user.getReservedStt() != null) {
            ctrlStatusSpec = new BaseSpecification(new SearchCriteria("ctrlStatus", ":", ShmUser.CtrlStatus.SUSPENDED.ordinal()));
        }

        BaseSpecification leftSpec = new BaseSpecification(new SearchCriteria("status", "!=", ShmUser.Status.LEFT));
        if (user.getLeftStt() != null) {
            leftSpec = new BaseSpecification(new SearchCriteria("status", ":", user.getLeftStt()));
        }

        BaseSpecification phoneNumberSpec = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(user.getPhoneNumber())) {
            phoneNumberSpec = new BaseSpecification(new SearchCriteria("phone", ":", user.getPhoneNumber()));
        }

        BaseSpecification addrSpec = null;
        if (user.getProvinceId() != null && user.getDistrictId() == null) {
            List<ShmAddr> categories = addressService.getAddresses(user.getProvinceId());
            if (categories.isEmpty())
                return null;
            List<Long> addrIds = categories.stream().map(cat -> cat.getAddressId()).collect(Collectors.toList());
            addrSpec = new BaseSpecification(new SearchCriteria("address", "in", addrIds));
        }

        if (user.getDistrictId() != null) {
            addrSpec = new BaseSpecification(new SearchCriteria("address", ":", user.getDistrictId()));
        }

        BaseSpecification emailSpec = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(user.getEmail())) {
            emailSpec = new BaseSpecification(new SearchCriteria("email", ":", user.getEmail()));
        }

        BaseSpecification periodDateSpec = null;
        Integer userType = user.getUserType();
        if (userType != null) {
            int userTypevalue = userType.intValue();
            if (userTypevalue == 0) {
                if (user.getFromDate() != null && user.getToDate() != null) {
                    LocalDateTime startTime = LocalDateTime.ofInstant(user.getFromDate().toInstant(), ZoneId.systemDefault());
                    LocalDateTime endTime = LocalDateTime.ofInstant(user.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1);
                    periodDateSpec = new BaseSpecification(new SearchCriteria("createdAt", "<>", startTime, endTime));
                }
                if (user.getFromDate() != null && user.getToDate() == null) {
                    LocalDateTime value = LocalDateTime.ofInstant(user.getFromDate().toInstant(), ZoneId.systemDefault());
                    periodDateSpec = new BaseSpecification(new SearchCriteria("createdAt", ">=", value));
                }
                if (user.getFromDate() == null && user.getToDate() != null) {
                    LocalDateTime value = LocalDateTime.ofInstant(user.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1);
                    periodDateSpec = new BaseSpecification(new SearchCriteria("createdAt", "=<", value));
                }
            } else if (userTypevalue == 2) {
                if (user.getFromDate() != null && user.getToDate() != null) {
                    LocalDateTime startTime = LocalDateTime.ofInstant(user.getFromDate().toInstant(), ZoneId.systemDefault());
                    LocalDateTime endTime = LocalDateTime.ofInstant(user.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1);
                    periodDateSpec = new BaseSpecification(new SearchCriteria("leftDate", "<>", startTime, endTime));
                }
                if (user.getFromDate() != null && user.getToDate() == null) {
                    LocalDateTime value = LocalDateTime.ofInstant(user.getFromDate().toInstant(), ZoneId.systemDefault());
                    periodDateSpec = new BaseSpecification(new SearchCriteria("leftDate", ">=", value));
                }
                if (user.getFromDate() == null && user.getToDate() != null) {
                    LocalDateTime value = LocalDateTime.ofInstant(user.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1);
                    periodDateSpec = new BaseSpecification(new SearchCriteria("leftDate", "=<", value));
                }
            }
        }

        Page all = null;

        if (user.getUserStt().size() == 0) {
            ctrlStatusSpec = null;
            leftSpec = null;
            user.getUserStt().add((short) 4);
            user.getUserStt().add((short) 6);
            activeSpec = new BaseSpecification(new SearchCriteria("status", "in", user.getUserStt()));
            all = userRepository.findAll(Specifications.where(firstNameSpec).and(lastNameSpec).and(femaleSpec).and(maleSpec).and(birthDaySpec).and(idSpec)
                    .and(bsidSpec).and(activeSpec).and(leftSpec).and(ctrlStatusSpec).and(phoneNumberSpec).and(emailSpec).and(periodDateSpec).and(addrSpec), pageable);
        }
        if (user.getUserStt().size() == 1) {
            if (user.getLeftStt() != null)
                ctrlStatusSpec = null;
            all = userRepository.findAll(Specifications.where(firstNameSpec).and(lastNameSpec).and(femaleSpec).and(maleSpec).and(birthDaySpec).and(idSpec)
                    .and(bsidSpec).and(activeSpec).and(leftSpec).and(ctrlStatusSpec).and(phoneNumberSpec).and(emailSpec).and(periodDateSpec).and(addrSpec), pageable);
        }
        if (user.getUserStt().size() > 1) {
            if (user.getActiveStt() == null || user.getUserStt().size() == 3) {
                all = userRepository.findAll(Specifications.where(firstNameSpec).and(lastNameSpec).and(femaleSpec).and(maleSpec).and(birthDaySpec).and(idSpec)
                        .and(bsidSpec).and(activeSpec).or(leftSpec).or(ctrlStatusSpec).and(phoneNumberSpec).and(emailSpec).and(periodDateSpec).and(addrSpec), pageable);
            }
            if (user.getLeftStt() == null) {
                all = userRepository.findAll(Specifications.where(firstNameSpec).and(lastNameSpec).and(femaleSpec).and(maleSpec).and(birthDaySpec).and(idSpec)
                        .and(bsidSpec).and(activeSpec).or(ctrlStatusSpec).and(leftSpec).and(phoneNumberSpec).and(emailSpec).and(periodDateSpec).and(addrSpec), pageable);
            }
            if (user.getReservedStt() == null) {
                List<Long> userIds = userRepository.getUserSuspendAndLeft();
                BaseSpecification temp = null;
                if (userIds.size() > 0) {
                    temp = new BaseSpecification(new SearchCriteria("id", "in", userIds));
                }
                all = userRepository.findAll(Specifications.where(firstNameSpec).and(lastNameSpec).and(femaleSpec).and(maleSpec).and(birthDaySpec).and(idSpec)
                        .and(bsidSpec).and(activeSpec).or(leftSpec).and(ctrlStatusSpec).or(temp).and(phoneNumberSpec).and(emailSpec).and(periodDateSpec).and(addrSpec), pageable);
            }
        }

        if (userType != null && userType == 1) {
            List<ShmUser> result = new ArrayList<>();
            List<ShmUser> userList = all.getContent();
            for (ShmUser element : userList) {
                ShtAdminLog adminLog = adminLogService.getLastTimeSuspendUser(element.getId());
                if (adminLog == null) {
                    continue;
                }
                LocalDateTime createdAt = adminLog.getCreatedAt();
                if (user.getFromDate() != null && user.getToDate() != null) {
                    LocalDateTime startTime = LocalDateTime.ofInstant(user.getFromDate().toInstant(), ZoneId.systemDefault());
                    LocalDateTime endTime = LocalDateTime.ofInstant(user.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1);
                    if (createdAt.isBefore(startTime) || createdAt.isAfter(endTime)) {
                        continue;
                    }
                }
                if (user.getFromDate() != null && user.getToDate() == null) {
                    LocalDateTime startTime = LocalDateTime.ofInstant(user.getFromDate().toInstant(), ZoneId.systemDefault());
                    if (createdAt.isBefore(startTime)) {
                        continue;
                    }
                }
                if (user.getFromDate() == null && user.getToDate() != null) {
                    LocalDateTime endTime = LocalDateTime.ofInstant(user.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1);
                    if (createdAt.isAfter(endTime)) {
                        continue;
                    }
                }
                result.add(element);
            }
            int subTotal = all.getContent().size() - result.size();
            all = new PageImpl<ShmUser>(result, pageable, all.getTotalElements() > maxRecordsInPage ? all.getTotalElements() - subTotal : result.size());
        }

        return all;
    }

    /**
     * ********************************* PRIVATE METHOD ***************************************
     */
    private void checkRegisteringEmail(String bsid, String email) {
        validateEmail(email);

        List<ShmUser> users = userRepository.findByEmail(email);

        List<ShmUser> registeringUsers = users.stream()
                .filter(u ->
                        u.getBsid().equals(bsid) &&
                                (u.getStatus() == ShmUser.Status.SMS_UNACTIVATED ||
                                        u.getStatus() == ShmUser.Status.NO_PASSWORD ||
                                        u.getStatus() == ShmUser.Status.NO_PROFILE)
                ).collect(Collectors.toList());

        if (registeringUsers.size() > 0) {

            if (registeringUsers.size() != 1)
                Log.SERVICE_LOG.error("2 or more registration process of the same user!!!");

            ShmUser regUser = registeringUsers.get(0);
            SafeException safeForbiddenException = new SafeException(HttpStatus.FORBIDDEN, messageSource.getMessage("SH_E100066", null, null));

            //Return registration info in previous session
            ShtUserToken regToken = tokenService.createRegistrationToken(regUser);
            safeForbiddenException.errorDetail.put("userId", regUser.getId());
            safeForbiddenException.errorDetail.put("registrationToken", regToken.getTokenString());
            safeForbiddenException.errorDetail.put("completed", meaningInRegistrationStep(regUser.getStatus()));
            throw safeForbiddenException;
        }

        if (users.stream()
                .filter(u ->
                        u.getStatus() == ShmUser.Status.ACTIVATED ||
                                u.getStatus() == ShmUser.Status.TEND_TO_LEAVE ||
                                (
                                        !u.getBsid().equals(bsid) &&
                                                u.getStatus().compareTo(ShmUser.Status.EMAIL_UNACTIVATED) > 0 &&
                                                u.getStatus() != ShmUser.Status.LEFT
                                ) //email activated somewhere
                )
                .count() > 0) {
            throw new ResourceConflictException(messageSource.getMessage("SH_E100067", null, null));
        }
    }

    private void verifyBsidLimitAccountCreation(String bsid) {
        List<ShmUser> users = userRepository.findByBsid(bsid);
        int maxAccountCreation = Integer.valueOf(AppContext.getEnv().getProperty("bs.account_creation_limit"));
        if (users.stream()
                .filter(u ->
                        u.getStatus() != ShmUser.Status.EMAIL_UNACTIVATED &&
                                u.getStatus() != ShmUser.Status.LEFT
                )
                .count() >= maxAccountCreation) {
            throw new ForbiddenException(messageSource.getMessage("SH_E100068", new Object[]{maxAccountCreation}, null));
        }
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private void validateEmail(String email) {
        //Validate email pattern
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

        if (!matcher.find()) {
            throw new BadRequestException(messageSource.getMessage("SH_E100069", null, null));
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            throw new BadRequestException(messageSource.getMessage("SH_E100070", null, null));
        }

        if (!phoneNumber.startsWith("070") && !phoneNumber.startsWith("080") && !phoneNumber.startsWith("090")) {
            throw new BadRequestException(messageSource.getMessage("SH_E100071", null, null));
        }

        //Success
        return;
    }

    private void validateUserInfo(ShmUser user) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(user.getNickName())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100072", null, null));
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(user.getFirstName())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100073", null, null));
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(user.getLastName())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100074", null, null));
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(user.getDescription())) {
            if (user.getDescription().length() > 200)
                throw new BadRequestException(messageSource.getMessage("SH_E100075", null, null));
        }

        if (user.getDateOfBirth() != null && user.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100076", null, null));
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(user.getPassword())) {
            validatePassword(user.getPassword());
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(user.getEmail())) {
            validateEmail(user.getEmail());
        }

    }

    private void validateUserInfo(ShmUserUpdateRequest user) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(user.getNickName())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100072", null, null));
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(user.getFirstName())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100073", null, null));
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(user.getLastName())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100074", null, null));
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(user.getDescription())) {
            if (user.getDescription().length() > 200)
                throw new BadRequestException(messageSource.getMessage("SH_E100075", null, null));
        }

        if (user.getDateOfBirth() != null && user.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new BadRequestException(messageSource.getMessage("SH_E100076", null, null));
        }

    }

    private static final Pattern PASSWORD_REGEX = Pattern.compile("^[A-Za-z0-9]+$");

    private void validatePassword(String password) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(password)) {
            throw new BadRequestException(messageSource.getMessage("SH_E100077", null, null));
        }

        if (password.length() < 8 || password.length() > 20) {
            throw new BadRequestException(messageSource.getMessage("SH_E100078", null, null));
        }

    }

    @Value("${web.base.url}")
    private String webBaseUrl;

    @Override
    public void sendActivationEmail(String email, ShtUserToken activationToken) {
        Log.SERVICE_LOG.info("Email activation token: " + activationToken.getTokenString());
        try {
            String template = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(AppContext.getEnv().getProperty("user.reg.mail.template")), "utf8");
            String title = MailUtils.getEmailTitle(template);
            String bodyTemplate = MailUtils.getEmailContent(template);
            String body = bodyTemplate.replace("web.base.url", webBaseUrl);
            body = body.replace("{{token}}", activationToken.getTokenString());
            mailService.sendEmail(mailAdmin, email, title, body);
        } catch (Exception e) {
            Log.SERVICE_LOG.error("ERROR: Email activation token: " + e.getMessage());
        }
    }

    @Override
    public void sendActivationEmailChanged(String email, ShtUserToken activationToken) {
        Log.SERVICE_LOG.info("Email activation token change email: " + activationToken.getTokenString());
        try {
            String template = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(AppContext.getEnv().getProperty("user.change.mail.template")), "utf8");
            String title = MailUtils.getEmailTitle(template);
            String bodyTemplate = MailUtils.getEmailContent(template);
            String body = bodyTemplate.replace("web.base.url", webBaseUrl);
            body = body.replace("{{token}}", activationToken.getTokenString());
            mailService.sendEmail(mailAdmin, email, title, body);
        } catch (Exception e) {
            Log.SERVICE_LOG.error("ERROR: Email activation token change email: " + e.getMessage());
        }
    }

    private void sendActivationCode(String phoneNumber) {
        try {
            String template = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(AppContext.getEnv().getProperty("user.reg.sms.template")), "utf8");
            String activationCode = StringUtils.randomNumberSeq(Integer.valueOf(AppContext.getEnv().getProperty("sms.activation.code.length")));
            Log.SERVICE_LOG.info("SMS activation code: " + activationCode);
            smsService.sendRegistrationCode(phoneNumber, template.replace("{{code}}", activationCode), activationCode);
        } catch (Exception e) {
            Log.SERVICE_LOG.error("Cannot read sms template file. ERROR " + e.getMessage());
        }

    }

    private ShmUser updateUser(ShmUser user, ShmUser newUser) throws NoSuchAlgorithmException {

        if (newUser.getId() != null) {
            // Update user info
            validateUserInfo(newUser);
        } else {
            //Update password only
            validatePassword(newUser.getPassword());
        }

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(newUser.getNickName())) {

            // Check conflict nickName
            List<ShmUser> usersByNickName = userRepository.findByNickName(newUser.getNickName());
            if (usersByNickName.stream()
                    .filter(u ->
                            //Nickname was use somewhere
                            u.getStatus() == ShmUser.Status.ACTIVATED
                                    && u.getId().intValue() != user.getId().intValue()).count() > 0) {
                throw new ResourceConflictException(messageSource.getMessage("SH_E100079", null, null));
            }
        }

        hashPassword(newUser);
        user.merge(newUser);

        if (user.getAddress() != null) {
            //Resolve user's address
            Long addressId = user.getAddress().getAddressId();
            ShmAddr address = addressService.getAddress(addressId);
            if (address == null) {
                throw new ResourceNotFoundException(messageSource.getMessage("SH_E100080", null, null));
            }
            user.setAddress(address);
        } else {
            Long addressId = newUser.getAddressId();
            if (addressId != null) {
                ShmAddr address = addressService.getAddress(addressId);
                if (address == null) {
                    throw new ResourceNotFoundException(messageSource.getMessage("SH_E100080", null, null));
                }
                user.setAddress(address);
            }
        }

        ShmUser savedUser = userRepository.save(user);

        if (newUser.getAvatarRaw() != null) {
            //Save the image
            byte[] data = StringUtils.base64Decode(newUser.getAvatarRaw());
            String avatarDir = String.format("%s%s/%s/", AppContext.getEnv().getProperty("user.base_dir"), savedUser.getId(), "avatar");
            ShrImage avatarImage = imageService.saveImage(avatarDir, "avatar", "png", new ByteArrayInputStream(data));

            //Update user's avatar
            savedUser.setAvatar(avatarImage.getThumbnail());
        }

        return savedUser;
    }

    private ShmUser updateUser(ShmUser user, ShmUserUpdateRequest newUser) throws NoSuchAlgorithmException {

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(newUser.getNickName())) {
            // Check conflict nickName
            List<ShmUser> usersByNickName = userRepository.findByNickName(newUser.getNickName());
            if (usersByNickName.stream()
                    .filter(u ->
                            //Nickname was use somewhere
                            u.getStatus() == ShmUser.Status.ACTIVATED
                                    && u.getId().intValue() != user.getId().intValue()).count() > 0) {
                throw new ResourceConflictException(messageSource.getMessage("SH_E100079", null, null));
            }
        }
        if (newUser.getIsUpdateAvatar() != null && newUser.getIsUpdateAvatar()) {
            setAvatarUserFromAvatarRaw(newUser.getAvatarRaw(), user);
        }

        if (newUser.getAddressId() != null) {
            final ShmAddr address = addressService.getAddress(newUser.getAddressId());
            if (address == null) {
                throw new ResourceNotFoundException(messageSource.getMessage("SH_E100080", null, null));
            }
            user.setAddress(address);
        } else {
            user.setAddress(null);
        }
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setNickName(newUser.getNickName());
        user.setGender(newUser.getGender());
        user.setDateOfBirth(newUser.getDateOfBirth());
        user.setDescription(newUser.getDescription());
        user.setCareer(newUser.getCareer());
        user.setJob(newUser.getJob());

         /*
        // check status logic
        if (user.getPtrlStatus() == ShmUser.PtrlStatus.CENSORING) {
          //  user.setCtrlStatus(ShmUser.CtrlStatus.UPDATED);

            // send msg to talkroom
           try {
                List<ShtTalkPurc> talkPurcs = talkPurcService.findTalksByOwnerIdAndStatus(user.getId(), ShtTalkPurc.TalkPurcStatusEnum.TEND_TO_SELL);
                if (talkPurcs != null) {
                    for (ShtTalkPurc talkPurc : talkPurcs) {
                        ShtTalkPurcMsg msg = createMsgForPartner(talkPurc);
                        talkPurcMsgService.saveMsg(msg);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            // send mail to user
            mailService.sendEmailRestoreAccount(user.getEmail(), user.getNickName());

            // push notify broadcast
            String msgContent = messageSource.getMessage("PUSH_RESTORE_USER", null, null);
            PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_HANDLE_OK, null, null, null, null, null);
            snsMobilePushService.sendNotificationForUser(user.getId(), pushMsg, pushMsgDetail);

            // send notify to mailbox
            if (user != null) {
                ShtQa qa = new ShtQa();
                qa.setQaTitle(messageSource.getMessage("QA_TILTE_RESTORE_USER", null, null));
                qa.setFirstQaMsg(messageSource.getMessage("QA_CONTENT_RESTORE_USER", null, null).replace("<nickName>", user.getNickName()));
                qaService.createNotifyFromAdmin(user, qa);
            }
        } else   */

        if (user.getPtrlStatus() == ShmUser.PtrlStatus.CENSORED) {
            user.setPtrlStatus(ShmUser.PtrlStatus.UNCENSORED);
        }

        user.setUserUpdateAt(LocalDateTime.now());
        ShmUser savedUser = userRepository.save(user);
        return savedUser;
    }

    private ShtTalkPurcMsg createMsgForPartner(ShtTalkPurc talkPurc) {
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        if (talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
            ShmUser shmUser = talkPurc.getShmPost().getShmUser();
            msg.setShmUserCreator(talkPurc.getShmPost().getShmUser());
            msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_NG_MSG_FOR_PARTNER);
            msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10015", null, null).replace("<nickName>", shmUser.getNickName()));
        }
        return msg;
    }

    private void setAvatarUserFromAvatarRaw(String avatarRaw, ShmUser user) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(avatarRaw)) {
            //Save the image
            byte[] data = StringUtils.base64Decode(avatarRaw);
            String avatarDir = String.format("%s%s/%s/", AppContext.getEnv().getProperty("user.base_dir"), user.getId(), "avatar");
            ShrImage avatarImage = imageService.saveImage(avatarDir, "avatar", "png", new ByteArrayInputStream(data));

            //Update user's avatar
            user.setAvatar(avatarImage.getThumbnail());
        } else {
            user.setAvatar(null);
        }
    }

    private void hashPassword(ShmUser user) throws NoSuchAlgorithmException {
        user.setPassword(user.getPassword() == null ? null : StringUtils.getMD5(user.getPassword()));
    }

    //Hard to name this function
    static String meaningInRegistrationStep(ShmUser.Status status) {
        switch (status) {
            case EMAIL_UNACTIVATED:
            case LEFT:
                return "none";
            case SMS_UNACTIVATED:
                return "email";
            case NO_PASSWORD:
                return "sms";
            case NO_PROFILE:
                return "password";
            default:
                return "all";
        }
    }

    private ForbiddenException cannotCompleteRequestException(ShmUser user) {
        ForbiddenException forbiddenException = new ForbiddenException(messageSource.getMessage("SH_E100081", null, null));
        forbiddenException.errorDetail.put("completed", meaningInRegistrationStep(user.getStatus()));
        return forbiddenException;
    }

    @Override
    public List<ShmUser> findUserByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    @Override
    public void likePost(Long userId, Long postId, String status) throws Exception {
        // validate user hit like button
        ShmUser shmUser = getUserById(userId);
        checkUserStatus(shmUser);

        ShmPost shmPost = postService.getPost(postId);
        if (shmPost != null && shmPost.getShmUser() != null) {
            // validate owner
            checkUserStatus(shmPost.getShmUser());

            // cannot like owner post
            if (userId.intValue() == shmPost.getShmUser().getId().intValue()) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100087", null, null));
            }
            // validate post status
            talkPurcService.validatePostStatus(shmPost);
        }

        ShtUserFvrt userFvrt = userFvrtService.getUserFvrt(postId, userId);
        final ShtUserFvrt.UserFavoriteEnum userFavoriteEnum = ShtUserFvrt.UserFavoriteEnum.valueOf(status);
        if (userFvrt == null) {
            userFvrt = new ShtUserFvrt();
            userFvrt.setShmUser(userRepository.getOne(userId));
            userFvrt.setShmPost(postService.getPost(postId));
        } else {
            if (userFvrt.getUserFvrtStatus() == userFavoriteEnum) {
                Log.SERVICE_LOG.warn("WARNING: status already updated. Status: " + status);
                return;
            }
        }
        userFvrtService.updateUserFavoriteStatus(userFvrt, status);

        // update post liked times for post record
        Long postLikedTimes = userFvrtService.countLikeTimeByPostId(postId);
        postService.updatePostLikedTimes(postId, postLikedTimes);

    }

    @Override
    public void createReport(ShmUser shmUser, ShmPost shmPost, ShtUserRprt.UserReportTypeEnum userRprtType, String userRprtCont) throws Exception {
        // validate user hit report button
        if (shmUser.getCtrlStatus() == ShmUser.CtrlStatus.SUSPENDED) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100023", null, null));
        }
        if (shmPost != null && shmPost.getShmUser() != null) {
            checkUserStatus(shmPost.getShmUser());
        }
        ShtUserRprt currentShtUserRprt = userRprtService.getUserRprt(shmPost.getPostId(), shmUser.getId());
        if (currentShtUserRprt != null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100094", null, null));
        }
        // validate post status
        talkPurcService.validatePostStatus(shmPost);

        ShtUserRprt shtUserRprt = new ShtUserRprt();
        shtUserRprt.setShmPost(shmPost);
        shtUserRprt.setShmUser(shmUser);
        shtUserRprt.setUserRprtType(userRprtType);
        shtUserRprt.setUserRprtCont(userRprtCont);

        // set status of report like current post status
        if (shmPost.getPostPtrlStatus() == ShmPost.PostPtrlStatusEnum.CENSORED) {
            shtUserRprt.setReportPtrlStatus(ShtUserRprt.ReportPatrolStatus.CENSORED);
        } else if (shmPost.getPostPtrlStatus() == ShmPost.PostPtrlStatusEnum.CENSORING) {
            shtUserRprt.setReportPtrlStatus(ShtUserRprt.ReportPatrolStatus.CENSORING);
        } else {
            shtUserRprt.setReportPtrlStatus(ShtUserRprt.ReportPatrolStatus.UNCENSORED);
        }
        userRprtService.save(shtUserRprt);

        // update post report time
        Long countReportTimes = userRprtService.countTotalReportByPostId(shmPost.getPostId());
        ShmPost postReported = postService.getPost(shmPost.getPostId());
        postReported.setPostReportTimes(countReportTimes);
        if (postReported.getPostPtrlStatus() == ShmPost.PostPtrlStatusEnum.CENSORED) {
            postReported.setPostPtrlStatus(ShmPost.PostPtrlStatusEnum.UNCENSORED);
        }
        postService.savePost(postReported);
    }

    @Override
    public void blockUser(ShmUser fromUser, Long talkPurcId) {
        final ShmUser partnerInTalkPurc = talkPurcService.findPartnerInTalkPurc(talkPurcId);
        final ShtTalkPurc shtTalkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        //Block from owner post
        if (partnerInTalkPurc.getId().longValue() != fromUser.getId().longValue()) {
            ShtUserTalkPurc userBlock = getUserTalkPurc(fromUser.getId(), partnerInTalkPurc.getId());
            userBlock = buildShtUserBlock(fromUser, partnerInTalkPurc, shtTalkPurc, userBlock);
            userBlockRepository.save(userBlock);

            //Block from partner
        } else if (partnerInTalkPurc.getId().longValue() == fromUser.getId().longValue()) {
            ShmPost shmPost = shtTalkPurc.getShmPost();
            if (shmPost != null && shmPost.getShmUser() != null) {
                ShmUser owner = shmPost.getShmUser();
                if (owner != null) {
                    ShtUserTalkPurc userBlock = getUserTalkPurc(fromUser.getId(), owner.getId());
                    userBlock = buildShtUserBlock(fromUser, shmPost.getShmUser(), shtTalkPurc, userBlock);
                    userBlockRepository.save(userBlock);
                }
            }
        }
    }

    @Override
    public void muteUser(ShmUser fromUser, Long talkPurcId) {
        final ShmUser partnerInTalkPurc = talkPurcService.findPartnerInTalkPurc(talkPurcId);
        final ShtTalkPurc shtTalkPurc = talkPurcService.getTalkPurcById(talkPurcId);

        //Mute from owner post
        if (partnerInTalkPurc.getId().longValue() != fromUser.getId().longValue()) {
            ShtUserTalkPurc userBlock = getUserTalkPurc(fromUser.getId(), partnerInTalkPurc.getId());
            userBlock = buildShtUserMute(fromUser, partnerInTalkPurc, shtTalkPurc, userBlock);
            userBlockRepository.save(userBlock);

            //Mute from partner
        } else if (partnerInTalkPurc.getId().longValue() == fromUser.getId().longValue()) {
            ShmPost shmPost = shtTalkPurc.getShmPost();
            if (shmPost != null && shmPost.getShmUser() != null) {
                ShmUser owner = shmPost.getShmUser();
                if (owner != null) {
                    ShtUserTalkPurc userBlock = getUserTalkPurc(fromUser.getId(), owner.getId());
                    userBlock = buildShtUserMute(fromUser, shmPost.getShmUser(), shtTalkPurc, userBlock);
                    userBlockRepository.save(userBlock);
                }
            }
        }

    }

    @Override
    public ShtUserTalkPurc getUserTalkPurc(Long fromUser, Long toUser) {
        List<ShtUserTalkPurc> userTalkPurcs = userTalkPurcRepository.findByUserFromAndUserTo(fromUser, toUser);
        if (CollectionUtils.isEmpty(userTalkPurcs)) {
            return null;
        } else {
            // delete to last one if more than 1 record have been found
            if (userTalkPurcs.size() > 1) {
                int size = userTalkPurcs.size();
                for (int i = 1; i < size; i++) {
                    ShtUserTalkPurc shtUserTalkPurc = userTalkPurcs.get(i);
                    shtUserTalkPurc.setDeleteFlag(true);
                    userBlockRepository.save(shtUserTalkPurc);
                }
            }
            return userTalkPurcs.get(0);
        }
    }

    @Override
    public List<ShtGroupPblDetail> getGroupPblDetails(String legalId) {
        List<ShtGroupPblDetail> pblDetails = groupPblDetailService.getGroupDetailByLegalId(legalId);
        return pblDetails;
    }

    private ShtUserTalkPurc buildShtUserMute(ShmUser fromUser, ShmUser partnerInTalkPurc, ShtTalkPurc shtTalkPurc, ShtUserTalkPurc userMute) {
        if (userMute == null) {
            userMute = new ShtUserTalkPurc();
            userMute.setShmUserFrom(fromUser);
            userMute.setShmUserTo(partnerInTalkPurc);
            userMute.setUserTalkPurcMute(true);
        } else {
            if (userMute.isUserTalkPurcMute())
                userMute.setUserTalkPurcMute(false);
            else
                userMute.setUserTalkPurcMute(true);
        }
        return userMute;
    }

    private ShtUserTalkPurc buildShtUserBlock(ShmUser fromUser, ShmUser partnerInTalkPurc, ShtTalkPurc shtTalkPurc, ShtUserTalkPurc userBlock) {
        if (userBlock == null) {
            userBlock = new ShtUserTalkPurc();
            userBlock.setShmUserFrom(fromUser);
            userBlock.setShmUserTo(partnerInTalkPurc);
            userBlock.setUserTalkPurcBlock(true);
            userBlock.setUserBlockedTIme(LocalDateTime.now());
        } else {
            if (userBlock.isUserTalkPurcBlock()) {
                userBlock.setUserTalkPurcBlock(false);
            } else {
                userBlock.setUserTalkPurcBlock(true);
                userBlock.setUserBlockedTIme(LocalDateTime.now());
            }
        }
        return userBlock;
    }

    @Override
    public List<ShmUser> getUserByFirstNameOrLastName(String userName) {
        return userRepository.findLikeByFirstNameOrLastName(userName);
    }

    @Transactional(readOnly = true)
    @Override
    public ShmUserDTO getUserPatrolSequent() throws ParseException, SchedulerException {
        ShmUserDTO dto = null;
        try {
            ShmUser userSequent = userRepository.findUserPatrol();
            if (userSequent != null) {
                dto = UserMapper.mapEntityIntoDTO(userSequent);
                setAddressForUserDTO(userSequent, dto);
                dto.setAge(getCurrentAgeForUser(userSequent.getDateOfBirth()));
            }
        } catch (Exception e) {
            throw e;
        }
        return dto;
    }

    private ShmUserDTO setAddressForUserDTO(ShmUser user, ShmUserDTO dto) {
        if (user.getAddress() != null) {
            Long addressId = user.getAddress().getAddressId();
            ShmAddr shmAddr = addressService.getAddress(addressId);
            ShmAddr province = addressService.getAddress(shmAddr.getAddrParentId());
            dto.setDistrict(user.getAddress());
            dto.setProvince(province);
        }
        return dto;
    }

    @Override
    public ShmUserDTO getUserProfile(Long userId) {
        ShmUser user = userRepository.findOne(userId);
        if (user == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100053", null, null));
        }
        ShmUserDTO result = UserMapper.mapEntityIntoDTO(user);
        Long goodNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.GOOD);
        Long fairNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.FAIR);
        Long badNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.BAD);
        setAddressForUserDTO(user, result);
        result.setAvatar(user.getAvatar());
        result.setCurrentUserStatus(user.getStatus());
        result.setCurrentUserCtrlStatus(user.getCtrlStatus());
        result.setCareer(user.getCareer());
        result.setGoodReviewNumber(goodNumber);
        result.setNormalReviewNumber(fairNumber);
        result.setBadReviewNumber(badNumber);
        Long postNumber = postService.countPostByUserId(userId);
        result.setPostNumber(postNumber);
        result.setUserBsid(user.getBsid());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShmUserDTO> getUserListForPatrolSite(UserPatrolRequest userPatrolRequest, Pageable pageable) {
        boolean filterPendingStt = false;
        //check 保留中
        final String pending = userPatrolRequest.getPending();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(pending)) {
            filterPendingStt = true;
        }

        boolean filterUpdatedAfterCensoring = false;
        //check 修正完了
        final String userUpdatedAt = userPatrolRequest.getUserUpdatedAt();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(userUpdatedAt)) {
            filterUpdatedAfterCensoring = true;
        }

        Date updatedAtFrom = userPatrolRequest.getUpdatedAtFrom();
        Date censoredFrom = userPatrolRequest.getCensoredFrom();
        updatedAtFrom = DateUtils.setTimeToStartTimeInDay(updatedAtFrom);
        censoredFrom = DateUtils.setTimeToStartTimeInDay(censoredFrom);
        Date updatedAtTo = userPatrolRequest.getUpdatedAtTo();
        Date censoredTo = userPatrolRequest.getCensoredTo();
        updatedAtTo = DateUtils.setTimeToEndTimeInDay(updatedAtTo);
        censoredTo = DateUtils.setTimeToEndTimeInDay(censoredTo);
        Page<ShmUserDTO> result = userRepository.getUserListForPatrolSite(userPatrolRequest.getProcessStatus(),
                updatedAtFrom, updatedAtTo,
                userPatrolRequest.getPatrolAdminNames(), censoredFrom,
                censoredTo, pageable, imgServer, filterPendingStt, filterUpdatedAfterCensoring);
        return result;
    }

    @Override
    public void resetPassword(String email, String phoneNumber) {
        List<ShmUser> users = userRepository.findByEmailAndPhone(email, phoneNumber)
                .stream().filter(u ->
                        u.getStatus() == ShmUser.Status.ACTIVATED ||
                                u.getStatus() == ShmUser.Status.TEND_TO_LEAVE).collect(Collectors.toList());

        if (users.size() == 0) {
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));
        } else if (users.size() > 1) {
            Log.SERVICE_LOG.error("Duplicated user with same email");
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));
        } else {
            // Do reset password

            ShmUser user = users.get(0);

            //Expire all current access tokens
            List<ShtUserToken> accessTokens = tokenService.findAccessTokenByUser(user);
            accessTokens.forEach(t -> t.setExpireIn(0));

            int passwordLength = Integer.parseInt(AppContext.getEnv().getProperty("user.default_password.length"));
            String newPassword = StringUtils.randomPassword(passwordLength);
            ShmUser newPasswordUser = new ShmUser();
            newPasswordUser.setPassword(newPassword);


            //Set new password
            try {
                updateUser(user, newPasswordUser);
            } catch (NoSuchAlgorithmException e) {
                throw new InternalServerErrorException(messageSource.getMessage("SH_E100054", null, null));
            }

            //Send email to user
            mailService.sendEmail(mailAdmin, email, "[Worker's Market][Reset password]", "Your new password is: " + newPassword);
        }
    }

    @Override
    public List<ShmUser> getUserListByFirstNameAndLastName(List<String> fullName) {
        return userRepository.getUserListByFirstNameAndLastName(fullName);
    }

    @Override
    public long getReviewForUserByUserId(Long userId) {
        final Long totalReviewByUserId = userRevRepo.getTotalReviewByUserId(userId);
        if (totalReviewByUserId == null)
            return 0;
        return totalReviewByUserId;
    }

    @Transactional(readOnly = true)
    @Override
    public String buildOriginalAvatarPathForUser(ShrFile avatar) {
        return imgServer + FileUtils.buildImagePathByFileForUserAvatar(avatar);
    }

    @Override
    public ShmUser save(ShmUser user) {
        return userRepository.save(user);
    }

    @Override
    public ShmUser editUserInfo(Long userId, ShmUserUpdateRequest newUser) throws NoSuchAlgorithmException {
        ShmUser oldUser = userRepository.findOne(userId);
        return updateUser(oldUser, newUser);
    }

    @Transactional(readOnly = true)
    @Override
    public ShmUserDTO getUserDTOById(Long userId) throws ParseException, SchedulerException {
        final ShmUser shmUser = userRepository.findOne(userId);
        if (shmUser != null) {
            Long postNumber = postService.countPostByUserId(userId);
            shmUser.setPostNumber(postNumber);
        }
        if (shmUser != null) {
            final ShmUserDTO shmUserDTO = UserMapper.mapEntityIntoDTO(shmUser);
            final ShmAddr address = shmUser.getAddress();
            if (address != null) {
                ShmAddr province = addressService.getAddress(address.getAddrParentId());
                shmUserDTO.setDistrict(shmUser.getAddress());
                shmUserDTO.setProvince(province);
                shmUserDTO.setAddressTxt(buildAddressTxt(address));
            }
            shmUserDTO.setAge(getCurrentAgeForUser(shmUser.getDateOfBirth()));
            return shmUserDTO;
        } else
            return null;
    }

    private int getCurrentAgeForUser(LocalDate dateOfBirth) {
        if (dateOfBirth != null)
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        else
            return 0;
    }

    private String buildAddressTxt(ShmAddr address) {
        return addressService.getFullAddress(address);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNextUserId(Long userId) {
        Pageable pageable = new PageRequest(0, 1);
        final Page<Long> nextUserId = userRepository.getNextUserId(userId, pageable);
        Long result = nextUserId.getContent().get(0);
        return result;
    }

    @Override
    public List<ShmUser> getUsersOverTimeToPatrol(LocalDateTime limitTime, boolean isPatrol) {

        return userRepository.findByTimePatrolGreaterThanAndIsInPatrol(limitTime, isPatrol);
    }

    @Override
    public ShmUser censoredUserOk(Long userId, String adminEmail) throws SchedulerException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndSitePatrol(adminEmail);
        ShmUser shmUser = userRepository.findOne(userId);
        boolean sendEmailAndNotifyFlag = false;
        if (shmUser.getCtrlStatus().equals(ShmUser.CtrlStatus.OK) && shmUser.getPtrlStatus().equals(ShmUser.PtrlStatus.CENSORED))
            return null;
        if (shmUser.getCtrlStatus() == ShmUser.CtrlStatus.SUSPENDED) {
            sendEmailAndNotifyFlag = true;
        }
        shmUser.setCtrlStatus(ShmUser.CtrlStatus.OK);
        shmUser.setPtrlStatus(ShmUser.PtrlStatus.CENSORED);
        shmUser.setIsInPatrol(false);
        shmUser.setTimePatrol(LocalDateTime.now());
        userRepository.save(shmUser);

        if (sendEmailAndNotifyFlag) {
            // send notify to mailbox
            Long qaId = null;
            if (shmUser != null) {
                ShtQa qa = new ShtQa();
                qa.setQaTitle(messageSource.getMessage("QA_TILTE_RESTORE_USER", null, null));
                qa.setFirstQaMsg(messageSource.getMessage("QA_CONTENT_RESTORE_USER", null, null).replace("<nickName>", shmUser.getNickName()));
                qaId = qaService.createNotifyFromAdmin(shmUser, qa, admin).getQaId();
            }

            final String msgCont = messageSource.getMessage("PUSH_USER_OKE_AFTER_SUSPEND", null, null);
            final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_HANDLE_OK, null, null, null, null, qaId, null, shmUser.getId());
            snsMobilePushService.sendNotificationForUser(shmUser.getId(), pushMsg, pushMsgDetail);
            mailService.sendEmailRestoreAccount(shmUser.getEmail(), shmUser.getNickName());

            try {
                List<ShtTalkPurc> talkPurcs = talkPurcService.findTalksByOwnerIdAndStatus(shmUser.getId(), ShtTalkPurc.TalkPurcStatusEnum.TEND_TO_SELL);
                if (talkPurcs != null) {
                    for (ShtTalkPurc talkPurc : talkPurcs) {
                        ShtTalkPurcMsg msg = createMsgForPartner(talkPurc);
                        ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                        //    fireBaseService.sendNewMsgInToFireBaseDB(TalkPurcServiceImpl.buildTalkPurcInfo(shmUser, msgSaved));
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("ERROR: " + ex.getMessage());
            }
        }

        return shmUser;
    }

    @Override
    public boolean checkUserStatus(ShmUser shmUser) throws Exception {
        if (shmUser == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100021", null, null));
        }
        if (shmUser.getStatus() == ShmUser.Status.TEND_TO_LEAVE) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100022", null, null));
        }
        if (shmUser.getCtrlStatus() == ShmUser.CtrlStatus.SUSPENDED) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100023", null, null));
        }
        return true;
    }

    @Override
    public Map<String, List<Long>> getUserListRegistInMonth(LocalDate startDate, LocalDate endDate) {
        return userRepository.findUserListRegistInMonth(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public void exportUserPatrolCsv(ShmAdmin admin, UserPatrolRequest request, Pageable pageable, HttpServletResponse response) throws IOException {
        List<ShmUserDTO> userDTOList = getUserListForPatrolSite(request, pageable).getContent();
        List<UserPatrolCsvDTO> userPatrolCsvDTOList = new ArrayList<>();
        UserPatrolCsvDTO userPatrolCsvDTO;
        for (ShmUserDTO dto : userDTOList) {
            userPatrolCsvDTO = new UserPatrolCsvDTO();
            userPatrolCsvDTO.setAdminName(dto.getPatrolAdminName());
            userPatrolCsvDTO.setNickName(dto.getUserNickName());
            userPatrolCsvDTO.setTotalComment(adminLogService.countTotalProcessUserTimes(dto.getUserId()));
            ShtMemoUser memoUser = memoUserService.getNewestMemoByUserId(dto.getUserId());
            if (memoUser != null && userPatrolCsvDTO.getTotalComment() > 0)
                userPatrolCsvDTO.setCommentNewest(memoUser.getContent());

            userPatrolCsvDTOList.add(userPatrolCsvDTO);
        }

        String[] header = {"ユーザーのニックネーム", "チェック者", "対応コメント数", "対応コメント"};
        String[] properties = {"nickName", "adminName", "totalComment", "commentNewest"};
        exportCsvService.exportCSV(response, "user_patrol.csv", header, properties, userPatrolCsvDTOList);
    }

    @Override
    @Transactional(readOnly = true)
    public void exportUserCSV(UserSearchRequest userRequest, Pageable pageable, HttpServletResponse response) throws IOException {
        Page<ShmUser> users = searchUserByConditions(userRequest, pageable);
        List<ShmUser> userList = users.getContent();
        List<UserCsvDTO> dtoList = new ArrayList<>();
        UserCsvDTO dto;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        for (ShmUser user : userList) {
            dto = new UserCsvDTO();
            dto.setUserId(user.getId());
            dto.setNickName(user.getNickName());
            dto.setName(user.getFirstName() + user.getLastName());
            dto.setPhoneNumber(user.getPhone());
            dto.setBsid(user.getBsid());
            dto.setLegalId(user.getBsid().substring(0, 6));
            if (user.getAddress() != null) {
                dto.setDistrict(user.getAddress().getAreaName());
                dto.setProvince(addressService.getAddress(user.getAddress().getAddrParentId()).getAreaName());
            }
            if (user.getGender() != null)
                dto.setSex(ShmUser.Gender.FEMALE.equals(user.getGender()) ? "女性" : "男性");
            if (user.getDateOfBirth() != null)
                dto.setBirthDayTxt(" " + user.getDateOfBirth().format(formatter));
            dto.setCareer(user.getCareer());
            dto.setDescription(user.getDescription());
            dto.setSuspendFlag(user.getCtrlStatus().equals(ShmUser.CtrlStatus.SUSPENDED) ? "停止中" : "");
            dto.setTotalSellProduct(postService.countPostByPostIdAndPostType(user.getId(), ShmPost.PostType.SELL.ordinal()));
            dto.setTotalRequestProduct(postService.countPostByPostIdAndPostType(user.getId(), ShmPost.PostType.BUY.ordinal()));
            dto.setTotalSellTransaction(postService.countTransactionByPostType(user.getId(), ShmPost.PostType.SELL.ordinal()));
            dto.setTotalRequestTransaction(postService.countTransactionByPostType(user.getId(), ShmPost.PostType.BUY.ordinal()));
            dto.setTotalSellTransactionUnique(postService.countUniqueTransactionByPostType(user.getId(), ShmPost.PostType.SELL.ordinal()));
            dto.setTotalRequestTransactionUnique(postService.countUniqueTransactionByPostType(user.getId(), ShmPost.PostType.BUY.ordinal()));
            dto.setTotalTransaction(dto.getTotalRequestTransaction() + dto.getTotalSellTransaction());
            dto.setTotalTransactionUnique(dto.getTotalRequestTransactionUnique() + dto.getTotalSellTransactionUnique());
            dto.setTotalReviewMe(userRevService.countTotalReviewMe(user.getId()));
            dto.setTotalReportMe(userRprtService.countTotalReportMe(user.getId()));
            if (user.getLeftDate() != null)
                dto.setLeftDate(" " + user.getLeftDate().format(formatter));

            dtoList.add(dto);
        }

        String[] header = {
                "BoccID"
                , "ニックネーム"
                , "氏名"
                , "電話番号"
                , "法人ID"
                , "BSID"
                , "住所①"
                , "住所②"
                , "性別"
                , "生年月日"
                , "職業"
                , "自己紹介"
                , "出品数"
                , "依頼数"
                , "【出品】取引人数"
                , "【出品】ユニーク人数"
                , "【依頼】取引人数"
                , "【依頼】ユニーク人数"
                , "総取引人数"
                , "出品・依頼ユニーク人数"
                , "評価受取数"
                , "違反通報受取数"
                , "ユーザー停止フラグ"
                , "BOCC退会日"};
        String[] properties = {"userId", "nickName", "name", "phoneNumber", "legalId", "bsid", "province", "district", "sex", "birthDayTxt", "career", "description", "totalSellProduct",
                "totalRequestProduct", "totalSellTransaction", "totalSellTransactionUnique", "totalRequestTransaction", "totalRequestTransactionUnique", "totalTransaction", "totalTransactionUnique",
                "totalReviewMe", "totalReportMe", "suspendFlag", "leftDate"};

        exportCsvService.exportCSV(response, "user.csv", header, properties, dtoList);
    }

    @Override
    public void validateUserPassword(String password) throws Exception {
        if (org.apache.commons.lang3.StringUtils.isEmpty(password)) {
            return;
        }
        if (password.length() < 8) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100085", null, null));
        }

        String digitRegex = "\\d+";
        String letterRegex = "[a-zA-Z]+";

        if (password.matches(digitRegex) || password.matches(letterRegex)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100086", null, null));
        }

        String symbolRegex = "[" + "-/@#!*$%^&.'_+={}()" + "]+";
        if (password.matches(symbolRegex)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100129", null, null));
        }
    }

    @Override
    public void saveDeviceTokenRegistration(Long userId, String deviceToken, String osType) {
        ShmUser shmUser = userRepository.findOne(userId);
        ShtUserNtf shtUserNtf = new ShtUserNtf();
        shtUserNtf.setShmUser(shmUser);
        shtUserNtf.setUserNtfDeviceToken(deviceToken);
        shtUserNtf.setUserNtfOsType(OSTypeEnum.valueOf(osType));
        CreatePlatformEndpointResult platformEndpointResult = null;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(deviceToken)) {
            SubscribeResult subscribeResult = null;
            if (OSTypeEnum.IOS.equals(OSTypeEnum.valueOf(osType))) {
                platformEndpointResult = snsMobilePushService.createPlatformEndpoint(
                        PlatformEnum.APNS,
                        null,
                        deviceToken, platformApplicationResultAPNS.getPlatformApplicationArn());
                subscribeResult = snsMobilePushService.subscribeTopic(createTopicIOS.getTopicArn(), platformEndpointResult.getEndpointArn());
            } else if (OSTypeEnum.ANDROID.equals(OSTypeEnum.valueOf(osType))) {
                platformEndpointResult = snsMobilePushService.createPlatformEndpoint(
                        PlatformEnum.GCM,
                        null,
                        deviceToken, platformApplicationResultGCM.getPlatformApplicationArn());
                subscribeResult = snsMobilePushService.subscribeTopic(createTopicANDROID.getTopicArn(), platformEndpointResult.getEndpointArn());
            }
            shtUserNtf.setUserNtfSubscribed(true);
            if (subscribeResult != null)
                shtUserNtf.setUserNtfSubscribedARN(subscribeResult.getSubscriptionArn());
        }
        userNtfRepository.save(shtUserNtf);
    }

    @Override
    public void saveDeviceTokenLogin(Long id, String deviceToken, String OSType) {
        try{
            CreatePlatformEndpointResult platformEndpointResult = null;
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(deviceToken)) {
                ShmUser shmUser = userRepository.findOne(id);

                SubscribeResult subscribeResult = null;
                if (OSTypeEnum.IOS.equals(OSTypeEnum.valueOf(OSType))) {
                    platformEndpointResult = snsMobilePushService.createPlatformEndpoint(
                            PlatformEnum.APNS,
                            null,
                            deviceToken, platformApplicationResultAPNS.getPlatformApplicationArn());
                    subscribeResult = snsMobilePushService.subscribeTopic(createTopicIOS.getTopicArn(), platformEndpointResult.getEndpointArn());
                } else if (OSTypeEnum.ANDROID.equals(OSTypeEnum.valueOf(OSType))) {
                    platformEndpointResult = snsMobilePushService.createPlatformEndpoint(
                            PlatformEnum.GCM,
                            null,
                            deviceToken, platformApplicationResultGCM.getPlatformApplicationArn());
                    subscribeResult = snsMobilePushService.subscribeTopic(createTopicANDROID.getTopicArn(), platformEndpointResult.getEndpointArn());
                }

                if (userNtfRepository.getUserAndDeviceTokenExisted(id, deviceToken) == 0) {
                    ShtUserNtf shtUserNtf = new ShtUserNtf();
                    shtUserNtf.setShmUser(shmUser);
                    shtUserNtf.setUserNtfDeviceToken(deviceToken);
                    shtUserNtf.setUserNtfOsType(OSTypeEnum.valueOf(OSType));
                    shtUserNtf.setUserNtfSubscribed(true);
                    if (subscribeResult != null)
                        shtUserNtf.setUserNtfSubscribedARN(subscribeResult.getSubscriptionArn());
                    userNtfRepository.save(shtUserNtf);
                }
            }
        }catch (Exception ex){
            LOGGER.error("ERROR: " + ex.getMessage());
        }

    }

    @Override
    public void handleUserLeft(Long userId) {
        // save user status
        ShmUser user = getUserById(userId);
        user.setStatus(ShmUser.Status.LEFT);
        user.setLeftDate(LocalDateTime.now());
        save(user);

        try {
            // send msg to talk room
            List<ShtTalkPurc> openOwnerTalks = talkPurcService.findTalksByOwnerIdOpen(user.getId());
            if (openOwnerTalks != null && openOwnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openOwnerTalks) {
                    if (talkPurc != null && talkPurc.getShmUser() != null) {
                        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
                        msg.setShtTalkPurc(talkPurc);
                        msg.setShmUserCreator(user);
                        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
                        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10020", new Object[]{talkPurc.getShmPost().getShmUser().getNickName()}, null));
                        ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                        //   fireBaseService.sendNewMsgInToFireBaseDB(TalkPurcServiceImpl.buildTalkPurcInfo(user, msgSaved));
                    }
                }
            }

            List<ShtTalkPurc> openPartnerTalks = talkPurcService.findTalksByPartnerIdOpen(user.getId());
            if (openPartnerTalks != null && openPartnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openPartnerTalks) {
                    if (talkPurc != null && talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
                        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
                        msg.setShtTalkPurc(talkPurc);
                        msg.setShmUserCreator(user);
                        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
                        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10021", new Object[]{talkPurc.getShmUser().getNickName()}, null));
                        ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                        //    fireBaseService.sendNewMsgInToFireBaseDB(TalkPurcServiceImpl.buildTalkPurcInfo(user, msgSaved));
                    }
                }
            }

            //push notify to user
            String msgContentForOwner = messageSource.getMessage("PUSH_USER_LEFT_FOR_OWNER", null, null);
            String msgContentForPartner = messageSource.getMessage("PUSH_USER_LEFT_FOR_PARTNER", null, null);

            if (openOwnerTalks != null && openOwnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openOwnerTalks) {
                    if (talkPurc != null && talkPurc.getShmUser() != null) {
                        PushMsgCommon pushMsgForPartner = PushUtils.buildPushMsgCommon(msgContentForPartner);
                        PushMsgDetail pushMsgDetailForPartner = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_TEND_TO_LEAVE_DONE_FOR_PARTNER, null, null, talkPurc.getTalkPurcId(), null, null, null, talkPurc.getShmUser().getId());
                        snsMobilePushService.sendNotificationForUser(talkPurc.getShmUser().getId(), pushMsgForPartner, pushMsgDetailForPartner);
                    }
                }
            }

            if (openPartnerTalks != null && openPartnerTalks.size() > 0) {
                for (ShtTalkPurc talkPurc : openPartnerTalks) {
                    if (talkPurc != null && talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
                        PushMsgCommon pushMsgForOwner = PushUtils.buildPushMsgCommon(msgContentForOwner);
                        PushMsgDetail pushMsgDetailForOwner = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.USER_TEND_TO_LEAVE_DONE_FOR_OWNER, null, null, talkPurc.getTalkPurcId(), null, null, null, talkPurc.getShmPost().getShmUser().getId());
                        snsMobilePushService.sendNotificationForUser(talkPurc.getShmPost().getShmUser().getId(), pushMsgForOwner, pushMsgDetailForOwner);
                    }
                }
            }
        } catch (Exception ex) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100136", null, null));
        }


    }

    private void createUserLeftJob(Scheduler scheduler, long userId) {
        try {
            final org.quartz.JobDetail job = newJob(JobHandleUserLeft.class).withIdentity(JobEnum.JOB_USER_LEFT.getValue() + userId, JobEnum.JOB_USER_LEFT_GROUP.getValue()).build();
            LocalDateTime runTime = LocalDateTime.now().plusDays(userLeftWaitDay);
            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.put("userId", "" + userId);

            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_USER_LEFT_TRIGGER.getValue() + userId, JobEnum.JOB_USER_LEFT_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(runTime)))
                    .build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void cleanDataNotProcessedByAdmin(Long adminId) {
        List<ShtAdminUser> shtAdminUserList = adminUserRepository.getDataNotProcessByAdmin(adminId);
        shtAdminUserList.forEach(item -> adminUserRepository.delete(item));
        shtAdminUserList.forEach(item -> {
            if (item.getShmUser() != null) {
                ShmUser shmUser = userRepository.findOne(item.getShmUser().getId());
                if (shmUser != null) {
                    shmUser.setIsInPatrol(false);
                    userRepository.save(shmUser);
                }
            }
        });
    }

    @Override
    public void saveUserPatrol(ShmUser shmUser, ShmAdmin shmAdmin) {
        ShtAdminUser shtAdminUser = adminUserRepository.getUserProcessingByAdmin(shmAdmin.getAdminId(), shmUser.getId());
        if (shtAdminUser == null) {
            shtAdminUser = new ShtAdminUser();
            shtAdminUser.setShmAdmin(shmAdmin);
            shtAdminUser.setShmUser(shmUser);
            adminUserRepository.save(shtAdminUser);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean chechkUserDeleted(long userId) {
        if (ShmUser.Status.LEFT == getUserById(userId).getStatus() || true == getUserById(userId).getDeleteFlag()) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkUserInProcessByCurrentAdmin(Long adminId, Long userId) {
        ShtAdminUser shtAdminUser = adminUserRepository.getUserProcessingByAdmin(adminId, userId);
        if (shtAdminUser != null) {
            return true;
        }
        return false;
    }

    @Override
    public ShmUser findActiveUserById(Long postUserId) {
        return userRepository.findByIdAndStatus(postUserId, ShmUser.Status.ACTIVATED);
    }

    @Override
    public List<ShmUser> getUserListByFirstNameAndLastName(String firstNameLastName) {
        return userRepository.getUserListByFirstNameAndLastName(firstNameLastName);
    }

    @Override
    public boolean unSubscribeTopic(Long id, String deviceToken) {
        try {
            ShtUserNtf subscribe = userNtfRepository.findByUserIdAndDeviceToken(id, deviceToken);
            if (subscribe != null) {
                snsMobilePushService.unSubscribeTopic(subscribe.getUserNtfSubscribedARN());
//                subscribe.setUserNtfSubscribed(false);
//                subscribe.setDeleteFlag(true);
//                userNtfRepository.save(subscribe);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<String> getListBsFromOtherSchema() {
        return userRepository.getBsFromOtherSchema();
    }

    @Override
    public List<ShmUser> getActiveUsers() {
        return userRepository.findByStatus(ShmUser.Status.ACTIVATED);
    }

    @Override
    public List<ShmUser> getActiveUsersByBsId(String bsid) {
        return userRepository.findByStatusAndBsid(ShmUser.Status.ACTIVATED, bsid);
    }

    @Override
    public List<ShmUser> getActiveUserByLegalId(String legalId) {
        return userRepository.getActiveUserByLegalId(legalId);
    }

    @Override
    public List<ShmUser> getTendToLeaveUserBySyncExcId(Long syncExcId) {
        return userRepository.getTendToLeaveUserBySyncExcId(syncExcId);
    }
}
