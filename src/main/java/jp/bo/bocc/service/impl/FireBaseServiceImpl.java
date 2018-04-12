package jp.bo.bocc.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.tasks.OnSuccessListener;
import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.enums.ChatRoomTypeEnum;
import jp.bo.bocc.enums.FirebaseRestMethodEnum;
import jp.bo.bocc.firebase.DatabaseRealtime;
import jp.bo.bocc.firebase.FirebaseResponse;
import jp.bo.bocc.firebase.TaskListener;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.DateUtils;
import jp.bo.bocc.helper.FileUtils;
import jp.bo.bocc.helper.JacksonUtils;
import jp.bo.bocc.repository.UserRepository;
import jp.bo.bocc.service.FireBaseService;
import jp.bo.bocc.service.TalkPurcMsgService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Namlong on 8/2/2017.
 */
@Service
public class FireBaseServiceImpl implements FireBaseService {

    private final static Logger LOGGER = Logger.getLogger(FireBaseServiceImpl.class.getName());

//    @Value("${db.realtime}")
//    private String dbRealtimeUrl;
//
//    @Value("${firebase.account.json}")
//    private String firebaseAccountJson;

    @Value("${max.msg}")
    private Long maxMsg;

    final String DB_REALTIME_PATH_URL_TALKPURCHASE = "chats/talkPurchases/tpid";
    final String DB_REALTIME_ACCESS_TOKEN = ".json?access_token=";
    final String DB_REALTIME_PATH_URL_TALKPURCHASE_USER = "chats/talkPurchases/users/tpid";
    final String MSG_SENDER_NICKNAME = "sender";
    final String MSG_CONTENT = "msgContent";
    final String MSG_TYPE = "msgType";
    final String MSG_AVATAR_PARTNER_PATH = "avatarPartnerPath";
    final String MSG_AVATAR_OWNER_PATH = "avatarOwnerPath";
    final String USER_OWNER_POST_ID = "ownerPostId";
    final String USER_PARTNER_ID = "partnerId";
    private static final String MSG_CREATED_AT = "createdAt";
    private static final String MSG_INDEX = "/msg";

//    @Autowired
//    DatabaseRealtime databaseRealtime;
//
//    @Autowired
//    private UserRepository userRepo;
//
//    @Autowired
//    private TalkPurcMsgService talkPurcMsgService;
//
//    @Override
//    public boolean sendNewMsgInToFireBaseDB(TalkPurcCreateBody talkPurcCreateBody) throws Exception {
//        LOGGER.info("BEGIN: sendNewMsgInToFireBaseDB");
//        boolean responseAddMemberToGroup = false;
//        try {
//            final String accessToken = databaseRealtime.getAccessToken();
//            responseAddMemberToGroup = processChat(ChatRoomTypeEnum.TALK_PURCHASE, accessToken, talkPurcCreateBody);
//
//        } catch (Exception e) {
//            LOGGER.error("ERROR:" + e.getMessage());
//            throw e;
//        }
//        LOGGER.info("END: sendNewMsgInToFireBaseDB");
//        return responseAddMemberToGroup;
//    }
//
//    /**
//     * Process chat
//     * @param chatRoomTypeEnum
//     * @param accessToken
//     * @param object
//     * @return
//     * @throws Exception
//     */
//    public boolean processChat(ChatRoomTypeEnum chatRoomTypeEnum, String accessToken, Object object) throws Exception {
//        boolean result = false;
//        if (chatRoomTypeEnum == ChatRoomTypeEnum.TALK_PURCHASE) {
//            if (object instanceof TalkPurcCreateBody) {
//                TalkPurcCreateBody talkPurcCreateBody = (TalkPurcCreateBody) object;
//                //create message in a talk purchase
//                final long msgPriority = maxMsg + talkPurcCreateBody.getMsgId();
//                final String msgNumber = MSG_INDEX + msgPriority;
//                final String talkPurcUrl = buildTalkpurcMsgUrl(accessToken, talkPurcCreateBody.getTalkPurcId(), msgNumber);
//                HttpPut request = new HttpPut(talkPurcUrl);
//
//                Map<String, Object> dataMap = buildChatMsgContent(talkPurcCreateBody);
//
//                request.setEntity(buildEntityFromDataMap(dataMap));
//                HttpResponse httpResponseMsg = this.makeRequest(request);
//                // process the response
//                FirebaseResponse response = processResponse(FirebaseRestMethodEnum.PUT, httpResponseMsg);
//                result = response.getSuccess();
//            } else if (object instanceof ShtTalkPurcDTO) {
//                ShtTalkPurcDTO shtTalkPurcDTO = (ShtTalkPurcDTO) object;
//                FirebaseResponse responseUser = createUserInTalk(accessToken, shtTalkPurcDTO);
//
//                //create first message in a talk purchase
//                final long msgPriority = maxMsg + shtTalkPurcDTO.getMsgId();
//                final String msgNumber = MSG_INDEX + msgPriority;
//                final String talkPurcUrl = buildTalkpurcMsgUrl(accessToken, shtTalkPurcDTO.getTalkPurcId(), msgNumber);
//                HttpPut request = new HttpPut(talkPurcUrl);
//
//                Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
//                dataMap.put(MSG_SENDER_NICKNAME, shtTalkPurcDTO.getTalkPurcMsgCreatorNickName());
//                dataMap.put(MSG_CONTENT, shtTalkPurcDTO.getMsgContent());
//                dataMap.put(MSG_CREATED_AT, DateUtils.convertLocalDateTimeToStringWithTime(shtTalkPurcDTO.getCreatedAt()));
//                dataMap.put(MSG_TYPE, shtTalkPurcDTO.getMsgType().name());
//
//                request.setEntity(buildEntityFromDataMap(dataMap));
//                HttpResponse httpResponseMsg = this.makeRequest(request);
//                // process the response
//                FirebaseResponse responseMsg = processResponse(FirebaseRestMethodEnum.PUT, httpResponseMsg);
//                final boolean responseResult = responseUser.getSuccess() && responseMsg.getSuccess();
//                result = responseResult;
//            }
//        }
//        return result;
//    }
//
//    private Map<String, Object> buildChatMsgContent(TalkPurcCreateBody talkPurcCreateBody) {
//        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
//        dataMap.put(MSG_SENDER_NICKNAME, talkPurcCreateBody.getTalkPurcMsgCreatorNickName());
//        dataMap.put(MSG_CONTENT, talkPurcCreateBody.getMsgContent());
//        dataMap.put(MSG_CREATED_AT, DateUtils.convertLocalDateTimeToStringWithTime(talkPurcCreateBody.getCreatedAt()));
//        final ShtTalkPurcMsg.TalkPurcMsgTypeEnum msgType = talkPurcCreateBody.getMsgType();
//        if (msgType != null)
//            dataMap.put(MSG_TYPE, msgType.name());
//        dataMap.put(MSG_AVATAR_PARTNER_PATH, talkPurcCreateBody.getAvatarPartnerpath());
//        dataMap.put(MSG_AVATAR_OWNER_PATH, talkPurcCreateBody.getAvatarPartnerpath());
//
//        return dataMap;
//    }
//
//    private FirebaseResponse createUserInTalk(String accessToken, ShtTalkPurcDTO shtTalkPurcDTO) throws Exception {
//        //create user in a talk purchase
//        final String talkPurcUserUrl = buildTalkpurcUserUrl(accessToken, shtTalkPurcDTO.getTalkPurcId());
//        HttpPut requestUser = new HttpPut(talkPurcUserUrl);
//        Map<String, Object> dataMapUser = new LinkedHashMap<String, Object>();
//        dataMapUser.put(USER_OWNER_POST_ID, shtTalkPurcDTO.getOwnerPostId());
//        dataMapUser.put(USER_PARTNER_ID, shtTalkPurcDTO.getPartnerId());
//
//        requestUser.setEntity(buildEntityFromDataMap(dataMapUser));
//        HttpResponse httpResponseUser = this.makeRequest(requestUser);
//        // process the response
//        return processResponse(FirebaseRestMethodEnum.PUT, httpResponseUser);
//    }
//
//    @Override
//    public boolean createFirstTalkPurcIntoFirebaseDB(ShtTalkPurcDTO shtTalkPurcDTO) throws Exception {
//        LOGGER.info("BEGIN: createFirstTalkPurcIntoFirebaseDB");
//        boolean result =false;
//        try {
//            final String accessToken = databaseRealtime.getAccessToken();
//            result = processChat(ChatRoomTypeEnum.TALK_PURCHASE, accessToken, shtTalkPurcDTO);
//        } catch (Exception e) {
//            LOGGER.error("ERROR:" + e.getMessage());
//            throw e;
//        }
//        LOGGER.info("END: createFirstTalkPurcIntoFirebaseDB");
//        return result;
//    }
//
//    /**
//     * Process response
//     *
//     * @param method
//     * @param httpResponse
//     * @return
//     * @throws Exception
//     */
//    private FirebaseResponse processResponse(FirebaseRestMethodEnum method, HttpResponse httpResponse) throws Exception {
//
//        FirebaseResponse response = null;
//
//        // sanity-checks
//        if (method == null) {
//
//            String msg = "method cannot be null";
//            LOGGER.error(msg);
//            throw new Exception(msg);
//        }
//        if (httpResponse == null) {
//
//            String msg = "httpResponse cannot be null";
//            LOGGER.error(msg);
//            throw new Exception(msg);
//        }
//
//        // get the response-entity
//        HttpEntity entity = httpResponse.getEntity();
//
//        // get the response-code
//        int code = httpResponse.getStatusLine().getStatusCode();
//
//        // set the response-success
//        boolean success = false;
//        switch (method) {
//            case DELETE:
//                if (httpResponse.getStatusLine().getStatusCode() == 204
//                        && "No Content".equalsIgnoreCase(httpResponse.getStatusLine().getReasonPhrase())) {
//                    success = true;
//                }
//                break;
//            case PATCH:
//            case PUT:
//            case POST:
//            case GET:
//                if (httpResponse.getStatusLine().getStatusCode() == 200
//                        && "OK".equalsIgnoreCase(httpResponse.getStatusLine().getReasonPhrase())) {
//                    success = true;
//                }
//                break;
//            default:
//                break;
//
//        }
//
//        // get the response-body
//        Writer writer = new StringWriter();
//        if (entity != null) {
//
//            try {
//
//                InputStream is = entity.getContent();
//                char[] buffer = new char[1024];
//                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//                int n;
//                while ((n = reader.read(buffer)) != -1) {
//                    writer.write(buffer, 0, n);
//                }
//
//            } catch (Throwable t) {
//
//                String msg = "unable to read response-content; read up to this point: '" + writer.toString() + "'";
//                writer = new StringWriter(); // don't want to later give jackson partial JSON it might choke on
//                LOGGER.error(msg);
//                throw new Exception(msg, t);
//
//            }
//        }
//
//        // convert response-body to map
//        Map<String, Object> body = null;
//        try {
//
//            body = JacksonUtils.getJsonStringAsMap(writer.toString());
//
//        } catch (Exception jue) {
//
//            String msg = "unable to convert response-body into map; response-body was: '" + writer.toString() + "'";
//            LOGGER.error(msg);
//            throw new Exception(msg, jue);
//        }
//
//        // build the response
//        response = new FirebaseResponse(success, code, body, writer.toString());
//
//        return response;
//    }
//
//    /**
//     * Send curl request to database realtime on firebase.
//     *
//     * @param request
//     * @return
//     * @throws Exception
//     */
//    private HttpResponse makeRequest(HttpPut request) throws Exception {
//
//        HttpResponse response = null;
//
//        // sanity-check
//        if (request == null) {
//
//            String msg = "request cannot be null";
//            LOGGER.error(msg);
//            throw new Exception(msg);
//        }
//
//        try {
//
//            HttpClient client = HttpClientBuilder.create().build();
//            response = client.execute(request);
//
//        } catch (Throwable t) {
//
//            String msg = "unable to receive response from request(" + request.getMethod() + ") @ " + request.getURI();
//            LOGGER.error(msg);
//            throw new Exception(msg, t);
//
//        }
//
//        return response;
//    }
//
//    /**
//     * build user in talk purchase
//     *
//     * @param accessToken
//     * @param talkPurcId
//     * @return
//     */
//    private String buildTalkpurcUserUrl(String accessToken, Long talkPurcId) {
//        String result = dbRealtimeUrl + DB_REALTIME_PATH_URL_TALKPURCHASE_USER + talkPurcId + DB_REALTIME_ACCESS_TOKEN + accessToken;
//        return result;
//    }
//
//    private StringEntity buildEntityFromDataMap(Map<String, Object> dataMap) throws Exception {
//        String jsonData = JacksonUtils.getJsonStringFromMap(dataMap);
//        final StringEntity result = ConverterUtils.buildEntityFromJsonData(jsonData);
//        return result;
//    }
//
//    /**
//     * build endpoint for REST API
//     *
//     * @param accessToken
//     * @param talkPurcId
//     * @param msgNumber
//     * @return
//     */
//    private String buildTalkpurcMsgUrl(String accessToken, Long talkPurcId, String msgNumber) {
//        String result = dbRealtimeUrl + DB_REALTIME_PATH_URL_TALKPURCHASE + talkPurcId + msgNumber + DB_REALTIME_ACCESS_TOKEN + accessToken;
//        return result;
//    }
//    @Autowired
//    TaskListener taskListener;
//
//    @Override
//    public void createCustomToken(Long userId) throws Exception{
//        LOGGER.info("BEGIN: create listener token for device ");
//        try {
//            String uid = userId + "" +System.currentTimeMillis();
//            FirebaseAuth.getInstance().createCustomToken(uid)
//                    .addOnSuccessListener(new OnSuccessListener<String>() {
//                        @Override
//                        public void onSuccess(String customToken) {
//                            taskListener.setCustomToken(customToken);
//                        }
//                    });
//        } catch (Exception e) {
//            LOGGER.error("ERROR: " + e.getMessage());
//            throw e;
//        }
//        LOGGER.info("END: create listener token for device ");
//    }
//
//    @Override
//    public String getTokenForClient() throws InterruptedException {
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            LOGGER.error("ERROR: " +e.getMessage());
//            throw e;
//        }
//        return taskListener.getCustomToken();
//    }
//
//
//    @Override
//    public void migrationTalkPurchaseMsg() throws Exception {
//        final String accessToken = databaseRealtime.getAccessToken();
//        final List<ShtTalkPurcMsg> allOrderByTalkPurcMsgIdAsc = talkPurcMsgService.findAllOrderByTalkPurcMsgIdAsc();
//        for (ShtTalkPurcMsg talkPurcMsg : allOrderByTalkPurcMsgIdAsc) {
//            ShtTalkPurcDTO shtTalkPurcDTO = new ShtTalkPurcDTO();
//            final ShtTalkPurc shtTalkPurc = talkPurcMsg.getShtTalkPurc();
//            if (shtTalkPurc != null) {
//                shtTalkPurcDTO.setTalkPurcId(shtTalkPurc.getTalkPurcId());
//                final ShmPost shmPost = shtTalkPurc.getShmPost();
//                if (shmPost != null && shmPost.getShmUser() != null) {
//                    final ShmUser ownerPost = shmPost.getShmUser();
//                    shtTalkPurcDTO.setOwnerPostId(ownerPost.getId());
//
//                    final ShmUser partner = shtTalkPurc.getShmUser();
//                    if (partner != null)
//                        shtTalkPurcDTO.setPartnerId(partner.getId());
//
//                    //create user in talkpurchase
//                    createUserInTalk(accessToken, shtTalkPurcDTO);
//
//                    //insert message in talk purchase
//                    TalkPurcCreateBody talkPurcCreateBody = new TalkPurcCreateBody();
//                    talkPurcCreateBody.setTalkPurcId(shtTalkPurc.getTalkPurcId());
//                    talkPurcCreateBody.setCreatedAt(talkPurcMsg.getCreatedAt());
//                    talkPurcCreateBody.setMsgId(talkPurcMsg.getTalkPurcMsgId());
//                    talkPurcCreateBody.setMsgContent(talkPurcMsg.getTalkPurcMsgCont());
//                    talkPurcCreateBody.setMsgType(talkPurcMsg.getTalkPurcMsgType());
//                    final ShmUser shmUserCreator = talkPurcMsg.getShmUserCreator();
//                    if (shmUserCreator != null)
//                        talkPurcCreateBody.setTalkPurcMsgCreatorNickName(shmUserCreator.getNickName());
//                    if (shmPost != null && shmPost.getShmUser() != null) {
//                        talkPurcCreateBody.setAvatarOwnerPostpath(FileUtils.buildImagePathByFileForUserAvatar(ownerPost.getAvatar()));
//                        talkPurcCreateBody.setAvatarPartnerpath(FileUtils.buildImagePathByFileForUserAvatar(partner.getAvatar()));
//                    }
//                    sendNewMsgInToFireBaseDB(talkPurcCreateBody);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void migrationTalkQAMsg() {
//
//    }
}
