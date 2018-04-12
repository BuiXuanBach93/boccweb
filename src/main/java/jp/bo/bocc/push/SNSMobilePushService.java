package jp.bo.bocc.push;

import com.amazonaws.services.sns.model.*;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.ShtUserNtf;
import jp.bo.bocc.enums.OSTypeEnum;
import jp.bo.bocc.enums.PlatformEnum;
import org.quartz.SchedulerException;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by Namlong on 5/18/2017.
 */
public interface SNSMobilePushService {

    /**
     * registrationId (ios: deviceToken), messageContent: will be sent from mobile device.
     *
     * @param platform
     * @param principal       (ios: certificate)
     * @param serverAPIKey    (ios: privateKey)
     * @param applicationName
     * @param registrationId  (ios: deviceToken)
     * @param pushMsg
     * @return
     */
    boolean sendAppNotification(PlatformEnum platform, String principal, String serverAPIKey, String applicationName, String registrationId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail);

    CreatePlatformEndpointResult createPlatformEndpoint(
            PlatformEnum platform, String customData, String platformToken,
            String applicationArn);

    /**
     * registrationId (ios: deviceToken), messageContent: will be sent from mobile device.
     *
     * @param userNtf
     * @param pushMsg
     * @return
     */
    boolean sendNotification(ShtUserNtf userNtf, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail);

    void sendNotificationForUser(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail);

    /**
     * Do push to admin although the user alredy turn off push.
     * @param userId
     * @param pushMsg
     * @param pushMsgDetail
     */
    @Async
    void sendNotificationForUserFromAdmin(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail);

    void sendNotificationForAndroidUser(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail);

    void sendNotificationForIOsUser(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail);

    /**
     * Send notification in first talk.
     * @param userCreateTalkId
     * @param postId
     * @param creatorTalk
     */
    void sendNotificationInFirstTalk(Long userCreateTalkId, Long postId, String creatorTalk, Long talkPurcId);

    /**
     * Send message for user having post liked by another. 3rd argument can be: postId, talkPurcId, qaId.
     * @param userFavorited
     * @param userId
     * @param id
     * @param msgContent
     */
    void sendNotificationForUser(PushMsgDetail.NtfTypeEnum userFavorited, Long userId, Long id, String msgContent);

    void sendNotificationForUserLikedPost(PushMsgDetail.NtfTypeEnum pushType, Long userId, Long id, String msgContent);

    void sendNotificationForUser(PushMsgDetail.NtfTypeEnum ntfTypeEnum, Long userId, String message);

    void processAfterAcceptOrder(ShtTalkPurc talkPurc, ShmUser shmUser) throws SchedulerException;

    /**
     * send msg in talk.
     * @param userId
     * @param pushMsg
     * @param pushMsgDetail
     */
    void sendNotificationForUserWhenSendMsg(Long userId, PushMsgCommon pushMsg, PushMsgDetail pushMsgDetail);

    /**
     * Create a topic an AWZ condole.
     * @param createTopicRequest
     */
    CreateTopicResult createTopic(CreateTopicRequest createTopicRequest);

    /**
     * subscrible into a topic in awz.
     * @param topicArn
     * @param endpoint
     */
    SubscribeResult subscribeTopic(String topicArn, String endpoint);

    void unSubscribeTopic(String endpoint);

    /**
     * publish into a topic in awz.
     * @param osTypeEnum
     * @param pushMsgCommon
     * @param topicARN
     * @param pushMsgDetail
     * @return
     */
    PublishResult publishTopic(OSTypeEnum osTypeEnum, PushMsgCommon pushMsgCommon, String topicARN, PushMsgDetail pushMsgDetail);

    void subscribeTopicForExistingUser();
}
