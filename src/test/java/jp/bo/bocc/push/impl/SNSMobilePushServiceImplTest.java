package jp.bo.bocc.push.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import jp.bo.bocc.enums.OSTypeEnum;
import jp.bo.bocc.enums.PlatformEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Namlong on 5/18/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class SNSMobilePushServiceImplTest {
    @Autowired
    SNSMobilePushService snsMobilePushService;

    @Test
    public void sendAndroidAppNotification() throws Exception {
        String serverAPIKey = "AIzaSyDlFBXktFaGYa8a3XqNw9INHIVEjJZbeKk";
        String applicationName = "HelloApp";
//        String registrationId = "d_G5PrZpSo8:APA91bHwfcZH6xQR4XidxJ8X8FelxJbb86AFy4bLDjO6sETAID74Wsm_ZD_h-9lckZBqk9b7GKql3FcYP9Vs8OFghoxbgP-_tFefBfzJHM0vx21KowBa43lkniuNXiUt7aOzilVjZ9lx";
//        String registrationId = "dJv0h8j2lp4:APA91bGZyCWXJrCk5sUBoFbkthOKMwXrbtz8wUwdK4JPMw97KRKubLZLK48Cx3rr-22lQ4dAgCItZlAcY2iobf0JQYiPgweZK26m_QrwmR-FIW3fgyr53Sbbk8YklIni1Nc8jXgZ02Zy";
        String registrationId = "diqGZJ_BZl0:APA91bEpiAXkCL_kDgadZw0CtD5FSTx270gxRR4iKgE2WiJqZoxSKuBP5j9KxaAEayKqG9N3h5ItEwlluuYmwd6IUAt-a0JlIaG9OlJlz1ZtoZUsd1XR8ZTQVtjVqvN5sQTUMgIgGiTg";
        String tokenList = "fRdyTCqIdtk:APA91bFGOqDCi5founjOYZvEjj0-cf4ztsVTfz2hUtwDVN9t1VPNrpCRdZPhmhQyEsh1VcAa77bdU4v_LTGnRTysJLaqJJRiX8k3Im6sxCk_w_fG2RmQnZdSnxUEUPZ7804wfDu_DAMH";
        String[] strArr = tokenList.split(",");
        PushMsgCommon pushMsg = new PushMsgCommon();
        pushMsg.setMsgContent("PUSSSSSSSSSSSSSSSSSSSH");
        PushMsgDetail pushMsgDetail = new PushMsgDetail();
        pushMsgDetail.setTalkPurcId(1L);
        pushMsgDetail.setPostId(120L);
        pushMsgDetail.setQaId(1L);
        pushMsgDetail.setMsgChat("AAAAAAAAAAAAAAAAAAAAAAA");
        for (String str : strArr) {
            snsMobilePushService.sendAppNotification(PlatformEnum.GCM, "", serverAPIKey, applicationName, str, pushMsg, pushMsgDetail);
        }

    }

    @Test
    public void sendIOSAppNotification() throws Exception {
        String certificate = "-----BEGIN CERTIFICATE-----\nMIIGJjCCBQ6gAwIBAgIIZ9PzpkvF2iYwDQYJKoZIhvcNAQELBQAwgZYxCzAJBgNV\nBAYTAlVTMRMwEQYDVQQKDApBcHBsZSBJbmMuMSwwKgYDVQQLDCNBcHBsZSBXb3Js\nZHdpZGUgRGV2ZWxvcGVyIFJlbGF0aW9uczFEMEIGA1UEAww7QXBwbGUgV29ybGR3\naWRlIERldmVsb3BlciBSZWxhdGlvbnMgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkw\nHhcNMTcwNTI5MDkzNjM0WhcNMTgwNjI4MDkzNjM0WjCBlDElMCMGCgmSJomT8ixk\nAQEMFWpwLmNvLmJlbmVmaXRvbmUuYm9jYzEzMDEGA1UEAwwqQXBwbGUgUHVzaCBT\nZXJ2aWNlczoganAuY28uYmVuZWZpdG9uZS5ib2NjMRMwEQYDVQQLDApZWFBDN0JL\nRFY0MRQwEgYDVQQKDAtCZW5lZml0IE9uZTELMAkGA1UEBhMCVVMwggEiMA0GCSqG\nSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCve1ILPI06DNm42shsdozcGn9STg8NaeZJ\npbJaAlgfjwzhUuoKZC49O3pj/BYozWvE5iiNkJHF1XIz/Kn652YNL46qneV8HvHL\nQNDAK7TvvgenqBArQ2b20OD8nUYpPN68WSlB5dM3BROrBZpG1M9JlUJhI6hNeOB0\nIVf/4sf1uoCKY+vdBdQP79OAxmzgTXrc9SuCyaJJ2mc5hBeZq7wDCM4pU3wi/yoE\nbJYxvA8hRjTvQ3ATY+/4IqvxquRarKRGdnXEoxP2kYuM8iqAy0YScXIza7dIu3tE\nwjCplIIdRddtt6FHXz9PdBgocmJDCngkhLRK2+19EV0xqOMWhe+JAgMBAAGjggJ2\nMIICcjAdBgNVHQ4EFgQU9HKZOvzLLf/VCPy8DfhC5WJWELowDAYDVR0TAQH/BAIw\nADAfBgNVHSMEGDAWgBSIJxcJqbYYYIvs67r2R1nFUlSjtzCCARwGA1UdIASCARMw\nggEPMIIBCwYJKoZIhvdjZAUBMIH9MIHDBggrBgEFBQcCAjCBtgyBs1JlbGlhbmNl\nIG9uIHRoaXMgY2VydGlmaWNhdGUgYnkgYW55IHBhcnR5IGFzc3VtZXMgYWNjZXB0\nYW5jZSBvZiB0aGUgdGhlbiBhcHBsaWNhYmxlIHN0YW5kYXJkIHRlcm1zIGFuZCBj\nb25kaXRpb25zIG9mIHVzZSwgY2VydGlmaWNhdGUgcG9saWN5IGFuZCBjZXJ0aWZp\nY2F0aW9uIHByYWN0aWNlIHN0YXRlbWVudHMuMDUGCCsGAQUFBwIBFilodHRwOi8v\nd3d3LmFwcGxlLmNvbS9jZXJ0aWZpY2F0ZWF1dGhvcml0eTAwBgNVHR8EKTAnMCWg\nI6Ahhh9odHRwOi8vY3JsLmFwcGxlLmNvbS93d2RyY2EuY3JsMA4GA1UdDwEB/wQE\nAwIHgDATBgNVHSUEDDAKBggrBgEFBQcDAjAQBgoqhkiG92NkBgMBBAIFADAQBgoq\nhkiG92NkBgMCBAIFADCBhgYKKoZIhvdjZAYDBgR4MHYMFWpwLmNvLmJlbmVmaXRv\nbmUuYm9jYzAFDANhcHAMGmpwLmNvLmJlbmVmaXRvbmUuYm9jYy52b2lwMAYMBHZv\naXAMImpwLmNvLmJlbmVmaXRvbmUuYm9jYy5jb21wbGljYXRpb24wDgwMY29tcGxp\nY2F0aW9uMA0GCSqGSIb3DQEBCwUAA4IBAQAp70gUc5ToIZuulX0p71qmoKLbMbqM\nsQQ58rIImXDxxO7l5JvSq5fSO4DPxA8hs6kkGKQHL2g64nWyHzCWkscl/dNzyBkZ\nOLjJlJU3OByniyTBLJDkmlISqJvTb/FYFIh4t7G2XX3mHAGmdgnuN5XTOxqpdJIK\nkk4pDGp1Tkd4M/+eEXczF9j7HCOkq11GfhcGF/YajbVhF4yVJgJ09UKyanjcuqGQ\nf6Mx+d/1OKxOuEaEbxZ5LOgyxjPZcvpTMLoxjYY2dOWNkGwd4OwTbB2dSUDbiMBl\nJ84jlnZP8lGmNeSOxGG+5Z8MH2jXZBBqpIgC7wLr4ownymBfWhfwVnwr\n-----END CERTIFICATE-----\n";
        String privateKey = "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCve1ILPI06DNm4\n2shsdozcGn9STg8NaeZJpbJaAlgfjwzhUuoKZC49O3pj/BYozWvE5iiNkJHF1XIz\n/Kn652YNL46qneV8HvHLQNDAK7TvvgenqBArQ2b20OD8nUYpPN68WSlB5dM3BROr\nBZpG1M9JlUJhI6hNeOB0IVf/4sf1uoCKY+vdBdQP79OAxmzgTXrc9SuCyaJJ2mc5\nhBeZq7wDCM4pU3wi/yoEbJYxvA8hRjTvQ3ATY+/4IqvxquRarKRGdnXEoxP2kYuM\n8iqAy0YScXIza7dIu3tEwjCplIIdRddtt6FHXz9PdBgocmJDCngkhLRK2+19EV0x\nqOMWhe+JAgMBAAECggEAN6E4mSdQ/h4kx11UPE44yW38/vKQAwiXYLGOoMotdZO7\n2ZKXb0PjBLZeTmQUAktc5sawBHDYRYcs9R4cJNZGm9d/usbjgT2uGWkqxEEW0wnD\nE3tL5OsxMP121Z5mJ5yMX9lEIwfgtYh+e7EC/4FnVURkDrdnG2g+f5Iye4RBL7yx\nn/t4i+03nqObSdx5nStBCW+uhUNkzOZ9A+xfwFQGhrEpgxRoZ48tQ/7Wf8ag8i1b\nLF+QnXKktLtqdqWR5LQ7hymXUw3wJkpq5rChbILPBhWOr/G2pJRYxQG8mEbtMTJs\nvX1FroSZuPXe6yukOVj00R3j5mNfEMqQm7iIvZrYAQKBgQDasTZUxu1Ic1iwMSts\nb8hQ5t/ANWGg0FrPOdjcC8caBVbiqHYitVAP3n0oct0McqkR+lvL0dtcqZTKEoPO\n5FBCZeBrhoP2BKijb1FPynBNZOqp4kRrADzGFWNkzyMtnTpxBr4ZiwXWQfpBm/GQ\niXNmuUxpCdySO6m450hs03XeKQKBgQDNawG3uN8IQwtVnZTY4/2L4yX69tVmHchq\n0k6gCwmDmKi/+rq024CD4V/4joGNXhlGhWHpfh39LzSufRHiLNjIDtp4S0QLEKI2\nqqVjLPzjFC98DO853uJNK+On8nOg5P2GvGoppfHOBhZ5xtQGNECj4EtB1iae7Ofe\n9sVZG0ryYQKBgDUXLF9sY5JhHji31xjeEj1BrzQUQX0u17zTCEhzO4SfozYnO3a1\nmZggpTW6nNs2wsCwjLRwX9ag/JQJ5qHFX46e+Nphr4t8GgyDK7Q5KwY+55EScxWe\n2yIjcxu4BYw/TT6/Kks9Ks+W+NAQsZYrKC7Z8SjW0gtWHJ/c1gJFW6nxAoGAHzce\nmLOgONovU8BRiTAELIjFEcEGprDp7smspwdmV7gQFLReQPOGMQRDmDrXlwrwnlXQ\njjHbDslooQdie1NDUgtnyZXubhwF+nauEUcZ2swLdzdj9xBWpVGBE9l1FsMVf68M\nFu4fs6YbLkA3ogW4uO5xoTyQu9/WUHyBesv59KECgYAtgpqeJxYNWsKa4uAI5Nec\n2MlciPjoSu47urBsEp1Zfavl866eeQ7rptK/U3xDwV3F5k0WlHzAbRWKMPYP2D9v\nUKk3XlwjH6UEzNJm4IMp1aceqYMPGZHcXvUu0E0Y3s8q1xRe6YbPgqNrew+wHZ99\nE1rK3ZHzvBO2Xb2aZAH+xg==\n-----END PRIVATE KEY-----\n";
        String deviceToken = "abf1d49cb5a617eef2052efac16c6b5ab7bf11dcabd0fd86704e7c7368ff540b";
        /*String tokenList = "0275cc52261e172fa48e239176edc30d83b22d46e07ca5fcfdb1d391d704d97c," +
                "2b8ec529667f1ae988cc806144cfda56210a440b2c238624cc89a526d1f323b6," +
                "4462e398cdaa9e06c603826c340efd935f5449202571139c0571b2fca1e1188f," +
                "46ce91154621c8c4af94f5683dfa958bbc7dd4c57755a15218293a2bcc38aecf," +
                "bd140348a888051cb5888fe72e8dd9bbe5369b2456123a4e85bc89e329a29391," +
                "cfc1439e133fc54998235c9b2f50e6ee202a4c440a74043564623cdcdb5133d7," +
                "dap4Tru6rr0:APA91bFuwZWUKTSOnKr9k1ut580s0Zl4xBN49K9HQXlqeNyuo5flxNcb6BKOvO07mZlP9MiP3nARh9PDNwcsLzbUWyca7LUIEJUnVmiG8bCORP39l6epaVM_8BpfMcgw5gW_bgtUJE30," +
                "fb7272ff9e721e300bb499f70ebedeb53b71d04f7081e7b1f67b4049efff54f9";*/
//        --ff450b42deab870e8ae3aee8c582729c407f3fe25cbee1e8195bf796e591c27d
        String tokenList = "27210614027d38e0383e52c55fc5dded0089b6088e4701b4a90555652468ef64,27210614027d38e0383e52c55fc5dded0089b6088e4701b4a90555652468ef64,27210614027d38e0383e52c55fc5dded0089b6088e4701b4a90555652468ef64" +
                ",27210614027d38e0383e52c55fc5dded0089b6088e4701b4a90555652468ef64,27210614027d38e0383e52c55fc5dded0089b6088e4701b4a90555652468ef64";
        String[] strArr = tokenList.split(",");
        String applicationName = "ios";
        PushMsgCommon pushMsg = new PushMsgCommon();
        PushMsgDetail pushMsgDetail = new PushMsgDetail();
        pushMsgDetail.setTalkPurcId(206L);
        pushMsgDetail.setPostId(774L);
        pushMsgDetail.setQaId(1L);
        pushMsgDetail.setMsgChat("AAAAAAAAAAAAAAAAAAAAAAA");
//        snsMobilePushService.sendAppNotification(PlatformEnum.APNS, certificate, privateKey, applicationName, deviceToken, pushMsg, pushMsgDetail);
        for (int i = 0; i < strArr.length; i++) {
            String str = strArr[i];
            pushMsg.setMsgContent("AI DAY???" + i);
            snsMobilePushService.sendAppNotification(PlatformEnum.APNS, certificate, privateKey, applicationName, str, pushMsg, pushMsgDetail);
        }
    }

    @Test
    public void sendNotificationInFirstTalk() throws Exception {
//        snsMobilePushService.sendNotificationInFirstTalk(2L,218L,"ABC",139L);
//        snsMobilePushService.sendNotificationInFirstTalk(187L,785L,"EEEEEEEE",214L);
        PushMsgCommon pushMsg = new PushMsgCommon();
        pushMsg.setMsgContent("HELLLLLLLLLLLLLLLLLL");
        PushMsgDetail pushMsgDetail = new PushMsgDetail();
        pushMsgDetail.setTalkPurcId(206L);
        pushMsgDetail.setPostId(774L);
        pushMsgDetail.setQaId(1L);

        snsMobilePushService.sendNotificationForUser(555L, pushMsg, pushMsgDetail);
    }

    @Test
    public void testCreateTopic() {
        try {
            CreateTopicRequest createTopicRequest = new CreateTopicRequest("TESTTOPIC");

            final CreateTopicResult createTopicResult = snsMobilePushService.createTopic(createTopicRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    private AmazonSNS amazonSNS;

    @Test
    public void testSubscribeTopic() {
        try {
            PushMsgCommon pushMsg = new PushMsgCommon();
            CreateTopicRequest createTopicRequest = new CreateTopicRequest("TESTTOPIC1");
            final CreateTopicResult createTopicResult = snsMobilePushService.createTopic(createTopicRequest);
            CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(
                    PlatformEnum.APNS,
                    pushMsg.getMsgContent(),
                    "27210614027d38e0383e52c55fc5dded0089b6088e4701b4a90555652468ef64", platformApplicationResultAPNS.getPlatformApplicationArn());

            final String topicArn = createTopicResult.getTopicArn();
            final String endpointArn = platformEndpointResult.getEndpointArn();
            snsMobilePushService.subscribeTopic(topicArn, endpointArn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSubscribeTopicIOS() {
        try {
            PushMsgCommon pushMsg = new PushMsgCommon();
            CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(
                    PlatformEnum.APNS,
                    pushMsg.getMsgContent(),
                    "e9c2d00ada715eb668f7e7e1d53c43f6350487723beaec836f64bfe48c18a2f7", platformApplicationResultAPNS.getPlatformApplicationArn());

            final String topicArn = "arn:aws:sns:ap-northeast-1:451434263945:BOCC_TOPIC_FOR_IOS";
            final String endpointArn = platformEndpointResult.getEndpointArn();
            snsMobilePushService.subscribeTopic(topicArn, endpointArn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CreatePlatformEndpointResult createPlatformEndpoint(
            PlatformEnum platform, String customData, String platformToken,
            String applicationArn) {
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
        platformEndpointRequest.setCustomUserData(customData);
        String token = platformToken;
        platformEndpointRequest.setToken(token);
        platformEndpointRequest.setPlatformApplicationArn(applicationArn);
        return amazonSNS.createPlatformEndpoint(platformEndpointRequest);
    }

    @Test
    public void testPublishTopic() {
        String testtopic4 = null;
        CreateTopicResult createTopicRenitsult = null;
        try {

            final String topicArnAndroid = "arn:aws:sns:ap-northeast-1:451434263945:BOCC_TOPIC_FOR_ADNROID";
            final String topicArnIos = "arn:aws:sns:ap-northeast-1:451434263945:BOCC_TOPIC_FOR_IOS";

            PublishRequest request = new PublishRequest();
            PushMsgCommon pushMsgCommon = new PushMsgCommon();
            pushMsgCommon.setMsgContent("LAST ONE");

            snsMobilePushService.publishTopic(OSTypeEnum.ANDROID, pushMsgCommon, topicArnAndroid, null);
            snsMobilePushService.publishTopic(OSTypeEnum.IOS, pushMsgCommon, topicArnIos, null);
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }

    @Test
    public void testPublishTopic2() {
        String testtopic4 = null;
        CreateTopicResult createTopicResult = null;
        try {

            final String topicArn = "arn:aws:sns:ap-northeast-1:451434263945:BOCC_TOPIC_FOR_ADNROID";
            final String endpointArn = "arn:aws:sns:ap-northeast-1:451434263945:endpoint/GCM/BOCC_PUSH_ALL_GCM/9503a845-24c7-399c-827e-9a3a6bd2ee3c";
            PublishRequest request = new PublishRequest();
            Map<PlatformEnum, Map<String, MessageAttributeValue>> attributesMap = new HashMap<PlatformEnum, Map<String, MessageAttributeValue>>();
            attributesMap.put(PlatformEnum.GCM, addGCMNotificationAttributes(""));
            Map<String, MessageAttributeValue> notificationAttributes = getValidNotificationAttributes(attributesMap
                    .get(PlatformEnum.GCM));
            if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
                request.setMessageAttributes(notificationAttributes);
            }
            request.setMessageStructure("json");
            PushMsgCommon pushMsg = new PushMsgCommon();
            pushMsg.setMsgContent("FFFFFFFFFFFFFF");
            PushMsgDetail pushMsgDetail = new PushMsgDetail();
            String message = buildAndroidMessage(PlatformEnum.GCM, pushMsg, pushMsgDetail);
            Map<String, String> messageMap = new HashMap<String, String>();
            messageMap.put(PlatformEnum.GCM.name(), message);
            messageMap.put("default", "DKM DEFAULT ");
            message = ConverterUtils.jsonify(messageMap);
            request.setTopicArn(topicArn);
            request.setMessage(message);
            amazonSNS.publish(request);
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
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

    private String buildAndroidMessage(PlatformEnum platform, PushMsgCommon pushMsgCommon, PushMsgDetail pushMsgDetail) {
        Map<String, Object> androidMessageMap = new HashMap<String, Object>();
        androidMessageMap.put("collapse_key", "\u3010Worker's Market\u3011");
        androidMessageMap.put("data", buildContentMsgAndroid(pushMsgCommon, pushMsgDetail));

//        // count new msg
//        Long newMsg = countUnReadMessageOfUser(pushMsgDetail.getReceiverId());
//        androidMessageMap.put("badge", newMsg);

        androidMessageMap.put("delay_while_idle", true);
        androidMessageMap.put("time_to_live", 604800);
        androidMessageMap.put("dry_run", false);
        return ConverterUtils.jsonify(androidMessageMap);
    }

    private Map<String, Object> buildContentMsgAndroid(PushMsgCommon pushMsgCommon, PushMsgDetail pushMsgDetail) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("badge", "BADGE");
        result.put("msgChat", "FFFFFFFFFFFFFFFFFFFFFFFCK2");
        result.put("message", pushMsgCommon.getMsgContent());
        return result;
    }
}
