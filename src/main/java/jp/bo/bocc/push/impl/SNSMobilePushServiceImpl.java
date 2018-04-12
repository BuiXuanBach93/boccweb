package jp.bo.bocc.push.impl;


import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.ShtUserNtf;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.enums.OSTypeEnum;
import jp.bo.bocc.enums.PlatformEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.jobs.JobReviewAfter7Days;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.repository.UserNtfRepository;
import jp.bo.bocc.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Namlong on 5/8/2017.
 */
@Service
public class SNSMobilePushServiceImpl implements SNSMobilePushService {

    private final static Logger LOGGER = Logger.getLogger(SNSMobilePushServiceImpl.class.getName());

    @Autowired
    AmazonSNS amazonSNS;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    TalkQaService talkQaService;

    @Value("${android.server.api.key}")
    private String serverAPIKey;

    @Value("${application.name}")
    private String applicationName;

    @Value("${ios.certificate}")
    private String certificate;

    @Value("${ios.private.key}")
    private String privateKey;

    @Value("${time.to.live}")
    private int timeToLive;

    @Value("${apns.flag}")
    private boolean apnsFlag;

    @Value("${apns.sandbox.flag}")
    private boolean apnsSandboxFlag;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private UserNtfRepository userNtfRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired UserReadMsgService userReadMsgService;

    @Value("${push.title}")
    private String pushTitle;
    @Autowired
    private QaService qaService;
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Value("${job.review.wait.day}")
    private int waitDay;

    public Map<PlatformEnum, Map<String, MessageAttributeValue>> attributesMap = new HashMap<PlatformEnum, Map<String, MessageAttributeValue>>();

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
    UserNtfService userNtfService;

    @Autowired
    @Qualifier("createTopicResultANDROID")
    public CreateTopicResult createTopicANDROID;

    @Autowired
    @Qualifier("createTopicResultIOS")
    public CreateTopicResult createTopicIOS;

    @Override
    public void subscribeTopicForExistingUser() {
        try {
            List<ShtUserNtf> shtUserNtfs = userNtfService.getAllDeviceTokenForActiveUserNotSubscribed();
            CreatePlatformEndpointResult platformEndpointResult = null;
            SubscribeResult subscribeResult = null;
            for (ShtUserNtf shtUserNtfItem : shtUserNtfs) {
                if (OSTypeEnum.IOS.equals(shtUserNtfItem.getUserNtfOsType())) {
                    platformEndpointResult = createPlatformEndpoint(
                            PlatformEnum.APNS,
                            null,
                            shtUserNtfItem.getUserNtfDeviceToken(), platformApplicationResultAPNS.getPlatformApplicationArn());
                     subscribeResult = subscribeTopic(createTopicIOS.getTopicArn(), platformEndpointResult.getEndpointArn());
                } else if (OSTypeEnum.ANDROID.equals(shtUserNtfItem.getUserNtfOsType())) {
                    platformEndpointResult = createPlatformEndpoint(
                            PlatformEnum.GCM,
                            null,
                            shtUserNtfItem.getUserNtfDeviceToken(), platformApplicationResultGCM.getPlatformApplicationArn());
                    subscribeResult = subscribeTopic(createTopicANDROID.getTopicArn(), platformEndpointResult.getEndpointArn());
                }

                shtUserNtfItem.setUserNtfSubscribed(true);
                if (subscribeResult != null){
                    shtUserNtfItem.setUserNtfSubscribedARN(subscribeResult.getSubscriptionArn());
                }
                userNtfService.save(shtUserNtfItem);
//                userNtfService.subscribeDeviceToken(shtUserNtfItem.getUserNtfId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setAmazonSNS(AmazonSNS amazonSNS) {
        this.amazonSNS = amazonSNS;
    }

    @Override
    public CreateTopicResult createTopic(CreateTopicRequest createTopicRequest) {
        LOGGER.info("BEGIN: createTopic");
        CreateTopicResult createTopicResult = null;
        try {
            createTopicResult = amazonSNS.createTopic(createTopicRequest);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("ERROR: - " + e.getMessage());
        }
        LOGGER.info("END: createTopic");
        return createTopicResult;
    }

    @Override
    public SubscribeResult subscribeTopic(String topicArn, String endpoint) {
        LOGGER.info("BEGIN: subscribeTopic");
        SubscribeResult subscribeResult = null;
        try {
            //subscribe to an SNS topic
            SubscribeRequest subRequest = new SubscribeRequest(topicArn, "application", endpoint);
             subscribeResult = amazonSNS.subscribe(subRequest);
            //get request id for SubscribeRequest from SNS metadata
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("ERROR: subscribeTopic");
        }
        LOGGER.info("END: subscribeTopic. SubscribeResult result ARN:  " + subscribeResult.getSubscriptionArn());
        return subscribeResult;
    }

    @Override
    public void unSubscribeTopic(String subscribeARN) {
        LOGGER.info("BEGIN: unSubscribeTopic");
        try {
            //subscribe to an SNS topic
            UnsubscribeRequest subRequest = new UnsubscribeRequest(subscribeARN);
            amazonSNS.unsubscribe(subRequest);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("ERROR: unSubscribeTopic");
        }
        LOGGER.info("END: unSubscribeTopic");
    }

    @Override
    public PublishResult publishTopic(OSTypeEnum osTypeEnum, PushMsgCommon pushMsgCommon, String topicARN, PushMsgDetail pushMsgDetail) {
        LOGGER.info("BEGIN: publishTopic");
        PublishResult publishResult = null;
        String msg = "";
        try {
            Map<String, String> messageMap = new HashMap<String, String>();
            messageMap.put("default", "");
            if (osTypeEnum == OSTypeEnum.ANDROID) {
                messageMap.put(PlatformEnum.GCM.name(), buildAndroidMessage(pushMsgCommon,pushMsgDetail));
                msg = ConverterUtils.jsonify(messageMap);
            }else if (osTypeEnum == OSTypeEnum.IOS) {
                messageMap.put(PlatformEnum.APNS.name(), buildAppleMessage(pushMsgCommon,pushMsgDetail));
                msg = ConverterUtils.jsonify(messageMap);
            }
            //publish to an SNS topic
            PublishRequest publishRequest = new PublishRequest(topicARN, msg);
            publishRequest.setMessageStructure("json");
            publishResult = amazonSNS.publish(publishRequest);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("ERROR: publishTopic");
        }
        LOGGER.info("END: publishTopic. ");
        return publishResult;
    }

    /**
     * @param platform
     * @param principal       (ios: certificate)
     * @param serverAPIKey    (ios: privateKey)
     * @param applicationName
     * @param registrationId  (ios: deviceToken)
     * @param pushMsg
     * @return
     */
    @Override
    public boolean sendAppNotification(PlatformEnum platform, String principal, String serverAPIKey, String applicationName, String registrationId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        String platformApplicationArn = null;
        try {

            // Create Platform Application. This corresponds to an app on a
            // platform.
            CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
                    applicationName, platform, principal, serverAPIKey);

            // The Platform Application Arn can be used to uniquely identify the
            // Platform Application.
            platformApplicationArn = platformApplicationResult
                    .getPlatformApplicationArn();

            // Create an Endpoint. This corresponds to an app on a device.
            CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(
                    platform,
                    pushMsg.getMsgContent(),
                    registrationId, platformApplicationArn);

            createPlatform(pushMsg.getMsgContent());

            // Publish a push notification to an Endpoint.
            PublishResult publishResult = publish(
                    platformEndpointResult.getEndpointArn(), platform, attributesMap, pushMsg, pushMsgDetail);

            LOGGER.info("Published! \n{MessageId="
                    + publishResult.getMessageId() + "}");

        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            return false;
        } finally {
            if (StringUtils.isNotEmpty(platformApplicationArn)) {
                LOGGER.info("Remove platform - platformApplicationArn: " + platformApplicationArn);
                // Delete the Platform Application since we will no longer be using it.
                deletePlatformApplication(platformApplicationArn);
            }
        }

        return true;
    }

    private CreatePlatformApplicationResult createPlatformApplication(
            String applicationName, PlatformEnum platform, String principal,
            String credential) {
        CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("PlatformPrincipal", principal);
        attributes.put("PlatformCredential", credential);
        platformApplicationRequest.setAttributes(attributes);
        platformApplicationRequest.setName(applicationName);
        platformApplicationRequest.setPlatform(platform.name());
        return amazonSNS.createPlatformApplication(platformApplicationRequest);
    }

    private void createPlatform(String messageContent) {
        attributesMap.put(PlatformEnum.GCM, addGCMNotificationAttributes(messageContent));
        if (apnsFlag)
            attributesMap.put(PlatformEnum.APNS, addAPNSNotificationAttributes(messageContent));
        if (apnsSandboxFlag)
            attributesMap.put(PlatformEnum.APNS_SANDBOX, addAPNSSANDBOXNotificationAttributes(messageContent));
    }

    private PublishResult publish(String endpointArn, PlatformEnum platform,
                                  Map<PlatformEnum, Map<String, MessageAttributeValue>> attributesMap, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        PublishRequest publishRequest = new PublishRequest();
        Map<String, MessageAttributeValue> notificationAttributes = getValidNotificationAttributes(attributesMap
                .get(platform));
        if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
            publishRequest.setMessageAttributes(notificationAttributes);
        }
        publishRequest.setMessageStructure("json");
        String message = buildMessage(platform, pushMsg, pushMsgDetail);
        Map<String, String> messageMap = new HashMap<String, String>();
        messageMap.put(platform.name(), message);
        message = ConverterUtils.jsonify(messageMap);

        // For direct publish to mobile end points, topicArn is not relevant.
        publishRequest.setTargetArn(endpointArn);

        publishRequest.setMessage(message);
        return amazonSNS.publish(publishRequest);
    }

    private String buildMessage(PlatformEnum platform, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        switch (platform) {
            case APNS:
                return buildAppleMessage(pushMsg, pushMsgDetail);
            case APNS_SANDBOX:
                return buildAppleMessage(pushMsg, pushMsgDetail);
            case GCM:
                return buildAndroidMessage(pushMsg, pushMsgDetail);
            default:
                throw new IllegalArgumentException("Platform not supported : "
                        + platform.name());
        }
    }

    private String buildAndroidMessage(PushMsgCommon pushMsgCommon, PushMsgDetail pushMsgDetail) {
        Map<String, Object> androidMessageMap = new HashMap<String, Object>();
        androidMessageMap.put("collapse_key", pushTitle);
        androidMessageMap.put("data", buildContentMsgAndroid(pushMsgCommon, pushMsgDetail));

//        // count new msg
//        Long newMsg = countUnReadMessageOfUser(pushMsgDetail.getReceiverId());
//        androidMessageMap.put("badge", newMsg);

        androidMessageMap.put("delay_while_idle", true);
        androidMessageMap.put("time_to_live", timeToLive);
        androidMessageMap.put("dry_run", false);
        return ConverterUtils.jsonify(androidMessageMap);
    }

    private Map<String, Object> buildContentMsgAndroid(PushMsgCommon pushMsgCommon, PushMsgDetail pushMsgDetail) {
        Map<String, Object> result = new HashMap<String, Object>();
        result = buildContentMsg(pushMsgDetail);
        result.put("message", pushMsgCommon.getMsgContent());
        return result;
    }

    private Map<String, Object> buildContentMsg(PushMsgDetail pushMsgDetail) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (pushMsgDetail != null) {
            result.put("ntfType", pushMsgDetail.getNtfType());
            final String msgChat = pushMsgDetail.getMsgChat();
            if (StringUtils.isNotEmpty(msgChat)) {
                result.put("msgChat", msgChat);
            }
            if (StringUtils.isNotEmpty(pushMsgDetail.getDataRef()))
                result.put("dataRef", pushMsgDetail.getDataRef());
            if (pushMsgDetail.getTalkPurcId() != null)
                result.put("talkPurcId", pushMsgDetail.getTalkPurcId());
            if (pushMsgDetail.getPostId() != null)
                result.put("postId", pushMsgDetail.getPostId());
            if (pushMsgDetail.getQaId() != null)
                result.put("qaId", pushMsgDetail.getQaId());
            if (pushMsgDetail.getPushId() != null) {
                result.put("pushId", pushMsgDetail.getPushId());
            }
            if (pushMsgDetail.getAdminId() != null) {
                result.put("adminId", pushMsgDetail.getAdminId());
            }
            if (pushMsgDetail.getReceiverId() != null) {
                Long newMsg = countUnReadMessageOfUser(pushMsgDetail.getReceiverId());
                result.put("badge", newMsg);
            }
        }else {
            result.put("badge", 0);
        }
        return result;
    }

    private String buildAppleMessage(PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        Map<String, Object> appleMessageMap = new HashMap<String, Object>();
        Map<String, Object> appMessageMap = new HashMap<String, Object>();
        appMessageMap.put("alert", pushMsg.getMsgContent());

        // count new msg
        Long newMsg = 0L;
        if (pushMsgDetail != null)
            newMsg = countUnReadMessageOfUser(pushMsgDetail.getReceiverId());
        appMessageMap.put("badge", newMsg.intValue());
        //    appMessageMap.put("badge", 0);
        appMessageMap.put("sound", "default");
        appMessageMap.put("content-available", 1);
        appleMessageMap.put("aps", appMessageMap);
        appleMessageMap.put("data", buildContentMsgIOS(pushMsg, pushMsgDetail));
        return ConverterUtils.jsonify(appleMessageMap);

    }

    private Map<String, Object> buildContentMsgIOS(PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        Map<String, Object> result = new HashMap<String, Object>();
        result = buildContentMsg(pushMsgDetail);
        result.put("title", pushTitle);
        return result;
    }

    private Long countUnReadMessageOfUser(Long userId) {
        if (userId == null) {
            return 0L;
        }
        Long countTalkMsg = talkPurcMsgService.countNewMsgByUserId(userId);
        Long countQaMsg = talkQaService.countNewMsgByUserId(userId);
        int countFromAdminMsg = userReadMsgService.countNewMsgSystemPushAll(userId);
        Long result = countTalkMsg + countQaMsg + countFromAdminMsg;
        return result;
    }

    private void deletePlatformApplication(String applicationArn) {
        DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
        request.setPlatformApplicationArn(applicationArn);
        amazonSNS.deletePlatformApplication(request);
    }

    @Override
    public CreatePlatformEndpointResult createPlatformEndpoint(
            PlatformEnum platform, String customData, String platformToken,
            String applicationArn) {
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
        platformEndpointRequest.setCustomUserData(customData);
        String token = platformToken;
        platformEndpointRequest.setToken(token);
        platformEndpointRequest.setPlatformApplicationArn(applicationArn);
        return amazonSNS.createPlatformEndpoint(platformEndpointRequest);
    }

    public static Map<String, MessageAttributeValue> getValidNotificationAttributes(
            Map<String, MessageAttributeValue> notificationAttributes) {
        Map<String, MessageAttributeValue> validAttributes = new HashMap<String, MessageAttributeValue>();

        if (notificationAttributes == null) return validAttributes;

        for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes
                .entrySet()) {
            if (!StringUtils.isBlank(entry.getValue().getStringValue())) {
                validAttributes.put(entry.getKey(), entry.getValue());
            }
        }
        return validAttributes;
    }

    private static Map<String, MessageAttributeValue> addGCMNotificationAttributes(String messageContent) {
        Map<String, MessageAttributeValue> notificationAttributes = new HashMap<String, MessageAttributeValue>();
        notificationAttributes.put("GCM.DeployStatus",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue("1"));
        notificationAttributes.put("GCM.MessageKey",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue(messageContent));
        notificationAttributes.put("GCM.MessageType",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue("0"));
        return notificationAttributes;
    }

    private static Map<String, MessageAttributeValue> addAPNSNotificationAttributes(String messageContent) {
        Map<String, MessageAttributeValue> notificationAttributes = new HashMap<String, MessageAttributeValue>();
        notificationAttributes.put("APNS.DeployStatus",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue("1"));
        notificationAttributes.put("APNS.MessageKey",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue(messageContent));
        notificationAttributes.put("APNS.MessageType",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue("0"));
        return notificationAttributes;
    }

    private static Map<String, MessageAttributeValue> addAPNSSANDBOXNotificationAttributes(String messageContent) {
        Map<String, MessageAttributeValue> notificationAttributes = new HashMap<String, MessageAttributeValue>();
        notificationAttributes.put("APNS_SANDBOX.DeployStatus",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue("1"));
        notificationAttributes.put("APNS_SANDBOX.MessageKey",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue(messageContent));
        notificationAttributes.put("APNS_SANDBOX.MessageType",
                new MessageAttributeValue().withDataType("String")
                        .withStringValue("0"));
        return notificationAttributes;
    }

    @Override
    public boolean sendNotification(ShtUserNtf userNtf, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        LOGGER.info("###LOG_PUSH START PUSH TO USER_ID : " + userNtf.getShmUser().getId() + " DEVICE TOKEN: " + userNtf.getUserNtfDeviceToken());
        try {
            final String userNtfDeviceToken = userNtf.getUserNtfDeviceToken();
            if (userNtf.getUserNtfOsType() == OSTypeEnum.ANDROID) {
                sendAppNotification(PlatformEnum.GCM, "", serverAPIKey, applicationName, userNtfDeviceToken, pushMsg, pushMsgDetail);
            }
            if (apnsFlag && userNtf.getUserNtfOsType() == OSTypeEnum.IOS)
                sendAppNotification(PlatformEnum.APNS, certificate, privateKey, applicationName, userNtfDeviceToken, pushMsg, pushMsgDetail);
            if (apnsSandboxFlag && userNtf.getUserNtfOsType() == OSTypeEnum.IOS)
                sendAppNotification(PlatformEnum.APNS_SANDBOX, certificate, privateKey, applicationName, userNtfDeviceToken, pushMsg, pushMsgDetail);
        } catch (Exception e) {
            LOGGER.error("###LOG_PUSH ERROR PUSH TO DEVICE TOKEN : " + userNtf.getShmUser().getId() + " DEVICE TOKEN: " + userNtf.getUserNtfDeviceToken() + " error :" + e.getMessage());
            return false;
        }
        LOGGER.info("###LOG_PUSH END PUSH TO DEVICE TOKEN: : " + userNtf.getShmUser().getId() + " DEVICE TOKEN: " + userNtf.getUserNtfDeviceToken());
        return true;
    }

    @Override
    @Async
    public void sendNotificationForUser(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        LOGGER.info("###LOG_PUSH : BEGIN get all android device token for user. User Id: " + userId);
        if (!userSettingService.checkReceivePushOn(userId)) {
            LOGGER.info("###LOG_PUSH : END User turn off notification.");
            return;
        } else {

            List<ShtUserNtf> allUserNtfDevice = userNtfRepository.getAllDeviceTokenByUserId(userId);
            if (CollectionUtils.isNotEmpty(allUserNtfDevice)) {
                for (ShtUserNtf deviceToken : allUserNtfDevice) {
                    sendNotification(deviceToken, pushMsg, pushMsgDetail);
                }
            } else {
                LOGGER.info("###LOG_PUSH : WARNING: No device used by user having userId: " + userId);
            }
        }
        LOGGER.info("###LOG_PUSH : END Send push for android users. User Id: " + userId);
    }

    @Override
    @Async
    public void sendNotificationForUserFromAdmin(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        LOGGER.info("###LOG_PUSH : BEGIN get all android device token for user. User Id: " + userId);

            List<ShtUserNtf> allUserNtfDevice = userNtfRepository.getAllDeviceTokenByUserId(userId);
            if (CollectionUtils.isNotEmpty(allUserNtfDevice)) {
                for (ShtUserNtf deviceToken : allUserNtfDevice) {
                    sendNotification(deviceToken, pushMsg, pushMsgDetail);
                }
            } else {
                LOGGER.info("###LOG_PUSH : WARNING: No device used by user having userId: " + userId);
            }
        LOGGER.info("###LOG_PUSH : END Send push for android users. User Id: " + userId);
    }

    @Override
    @Async
    public void sendNotificationForAndroidUser(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        LOGGER.info("###LOG_PUSH : BEGIN get all android device token for user. User Id: " + userId);
        if (!userSettingService.checkReceivePushOn(userId)) {
            LOGGER.info("###LOG_PUSH : END User turn off notification.");
            return;
        } else {
            List<ShtUserNtf> allUserNtfDevice = userNtfRepository.getAllAndroidDeviceTokenByUserId(userId);
            if (CollectionUtils.isNotEmpty(allUserNtfDevice)) {
                for (ShtUserNtf deviceToken : allUserNtfDevice) {
                    sendNotification(deviceToken, pushMsg, pushMsgDetail);
                }
            } else {
                LOGGER.info("###LOG_PUSH : WARNING: No device used by user having userId: " + userId);
            }
        }
        LOGGER.info("###LOG_PUSH : END Send push for android users. User Id: " + userId);
    }

    @Override
    @Async
    public void sendNotificationForIOsUser(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        LOGGER.info("###LOG_PUSH : BEGIN get all ios device token for user. User Id: " + userId);
        if (!userSettingService.checkReceivePushOn(userId)) {
            LOGGER.info("###LOG_PUSH : END User turn off notification.");
            return;
        } else {
            List<ShtUserNtf> allUserNtfDevice = userNtfRepository.getAllIOsDeviceTokenByUserId(userId);
            if (CollectionUtils.isNotEmpty(allUserNtfDevice)) {
                for (ShtUserNtf deviceToken : allUserNtfDevice) {
                    sendNotification(deviceToken, pushMsg, pushMsgDetail);
                }
            } else {
                LOGGER.info("###LOG_PUSH : WARNING: No device used by user having userId:" + userId);
            }
        }
        LOGGER.info("###LOG_PUSH : END Send push for android users. User Id:" + userId);
    }

    @Override
    @Async
    public void sendNotificationInFirstTalk(Long userCreateTalkId, Long postId, String creatorTalkNickname, Long talkPurcId) {
        final ShmPost post = postService.getPost(postId);
        final ShmUser shmUser = post.getShmUser();
        if (shmUser != null) {
            PushMsgCommon pushMsg = new PushMsgCommon();
            String msgContent = messageSource.getMessage("PUSH_FIRST_TALK", new Object[]{creatorTalkNickname}, null);
            pushMsg.setMsgContent(msgContent);
            PushMsgDetail pushMsgDetail = new PushMsgDetail();
            pushMsgDetail.setTalkPurcId(talkPurcId);
            pushMsgDetail.setNtfType(PushMsgDetail.NtfTypeEnum.TR_FIRST_CONTACT);
            pushMsgDetail.setReceiverId(shmUser.getId());
            sendNotificationForUser(shmUser.getId(), pushMsg, pushMsgDetail);
        }
    }

    @Override
    @Async
    public void sendNotificationForUser(PushMsgDetail.NtfTypeEnum userFavorited, Long userId, Long postId, String msgContent) {
        final ShmPost post = postService.getPost(postId);
        final ShmUser shmUser = post.getShmUser();
        if (shmUser != null) {
            PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(userFavorited, null, null, null, postId, null, null, shmUser.getId());
            sendNotificationForUser(shmUser.getId(), pushMsg, pushMsgDetail);
        }
    }

    @Override
    @Async
    public void sendNotificationForUserLikedPost(PushMsgDetail.NtfTypeEnum pushType, Long userId, Long postId, String msgContent) {
        PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
        PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(pushType, null, null, null, postId, null, null, userId);
        sendNotificationForUser(userId, pushMsg, pushMsgDetail);
    }

    @Override
    @Async
    public void processAfterAcceptOrder(ShtTalkPurc talkPurc, ShmUser shmUser) throws SchedulerException {
        //Create review job
        final ShmUser partner = talkPurc.getShmUser();
        final ShmPost shmPost = talkPurc.getShmPost();
        ShmUser ownerPost = shmPost.getShmUser();

        if (shmPost != null && ownerPost != null && partner != null) {
            String msgContent = messageSource.getMessage("PUSH_REPLY_ORDER_REQUEST_ACCEPT", new Object[]{shmPost.getPostName()}, null);
            PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.TR_ORDER_REQUEST_REPLY, null, null, talkPurc.getTalkPurcId(), null, null, null, ownerPost.getId());
            sendNotificationForUser(ownerPost.getId(), pushMsg, pushMsgDetail);

            long ownerPostId = ownerPost.getId();
            final long partnerId = partner.getId();
            createReviewJobAfterAcceptRequest(ownerPostId, partnerId, shmPost.getPostName());
        }
    }


    private void createReviewJobAfterAcceptRequest(long ownerPostId, long partnerId, String postName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        createReviewJobForOwnerPost(scheduler, partnerId, postName);
        createReviewJobForPartner(scheduler, ownerPostId, postName);

    }

    /**
     * Send notification for partner after 7 days.
     *
     * @param scheduler
     * @param ownerPostId
     */
    private void createReviewJobForPartner(Scheduler scheduler, long ownerPostId, String postName) {
        try {
            // define the job and tie it
            final org.quartz.JobDetail job = newJob(JobReviewAfter7Days.class).withIdentity(JobEnum.JOB_REVIEW_FOR_PARTNER.getValue() + ownerPostId, JobEnum.JOB_REVIEW_GROUP.getValue()).build();

            // computer a time that is on the next round minute
            LocalDateTime runTime = LocalDateTime.now().plusDays(waitDay);
            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.put("postName", postName);
            jobDataMap.put("userId", "" + ownerPostId);

            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_REVIEW_FOR_PARTNER.getValue() + ownerPostId, JobEnum.JOB_REVIEW_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(runTime)))
                    .build();

            // Tell quartz to schedule the job using trigger
            scheduler.scheduleJob(job, trigger);

            // Start up the scheduler (nothing can actually run until the scheduler has been started)
            scheduler.start();
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    /**
     * Send notification for owner post after 7 days.
     *
     * @param scheduler
     * @param partnerId
     */
    private void createReviewJobForOwnerPost(Scheduler scheduler, long partnerId, String postName) {
        try {
            // define the job and tie it
            final org.quartz.JobDetail job = newJob(JobReviewAfter7Days.class).withIdentity(JobEnum.JOB_REVIEW_FOR_OWNER_POST.getValue() + partnerId, JobEnum.JOB_REVIEW_GROUP.getValue()).build();

            // computer a time that is on the next round minute
            LocalDateTime runTime = LocalDateTime.now().plusDays(waitDay);
            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.put("postName", postName);
            jobDataMap.put("userId", "" + partnerId);
            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_REVIEW_FOR_OWNER_POST.getValue() + partnerId, JobEnum.JOB_REVIEW_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(runTime)))
                    .build();

            // Tell quartz to schedule the job using trigger
            scheduler.scheduleJob(job, trigger);

            // Start up the scheduler (nothing can actually run until the scheduler has been started)
            scheduler.start();
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void sendNotificationForUser(PushMsgDetail.NtfTypeEnum ntfTypeEnum, Long userId, String message) {
        LOGGER.info("BEGIN: get all device token for user. User Id: " + userId);
        if (!userSettingService.checkReceivePushOn(userId)) {
            LOGGER.info("END: User turn off notification.");
            return;
        } else {
            List<ShtUserNtf> allUserNtfDevice = userNtfRepository.getAllDeviceTokenByUserId(userId);
            if (CollectionUtils.isNotEmpty(allUserNtfDevice)) {
                PushMsgCommon pushMsg = new PushMsgCommon();
                pushMsg.setMsgContent(message);
                for (ShtUserNtf deviceToken : allUserNtfDevice) {
                    sendNotification(deviceToken, pushMsg, null);
                }
            } else {
                LOGGER.info("WARNING: No device used by user having userId: " + userId);
            }
        }
        LOGGER.info("END: get all device token for user. User Id: " + userId);
    }

    @Override
    @Async
    public void sendNotificationForUserWhenSendMsg(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail) {
        LOGGER.info("BEGIN: Send msg in talk purc. User Id: " + userId);
        sendNotificationForUser(userId, pushMsg, pushMsgDetail);
        LOGGER.info("END: Send msg in talk purc. User Id: " + userId);
    }
}
