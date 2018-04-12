package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.controller.api.response.TalkPurcResponse;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.entity.mapper.PostMapper;
import jp.bo.bocc.enums.ImageSavedEnum;
import jp.bo.bocc.helper.FileUtils;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
@RestController
public class TalkPurcController extends BoccBaseController {
    private final static Logger LOGGER = Logger.getLogger(TalkPurcController.class.getName());

    @Autowired
    TalkPurcService talkPurcService;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    FileService fileService;
    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    UserFvrtService userFvrtService;

    @Autowired
    RequestContext requestContext;

    @Autowired
    private SNSMobilePushService snsMobilePushService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private MailService mailService;

    @Autowired
    private UserSettingService userSettingService;
//    @Autowired
//    FireBaseService fireBaseService;

    @PostMapping(value = "/talk/purcs-create")
    @AccessTokenAuthentication
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ShtTalkPurcDTO> createTalkPurc(@RequestBody TalkPurcCreateBody talkPurcCreateBodyRequest) throws Exception {
        ShtTalkPurcDTO talkPurc = null;
        try {
            ShmUser shmUser = getUserUsingApp();
            talkPurcCreateBodyRequest.setPartId(shmUser.getId());
            if (talkPurcService.findTalkPurcByPostIdAndPartnerId(talkPurcCreateBodyRequest.getPostId(), talkPurcCreateBodyRequest.getPartId()) != null)
                return new ResponseEntity<ShtTalkPurcDTO>(HttpStatus.CONFLICT);
            talkPurc = talkPurcService.createTalkPurc(shmUser, talkPurcCreateBodyRequest);

            //check partner already block user
            boolean mutedBlockFlag = talkPurcService.checkUserMutedOrBlocked(talkPurc.getOwnerPostId(), shmUser.getId());

            final boolean partnerTurnOnPush = userSettingService.checkReceivePushTalkRoomFirstMsg(talkPurc.getOwnerPostId());

            if (talkPurc != null && !mutedBlockFlag) {

                //create msg into DB realtime
                //  fireBaseService.createFirstTalkPurcIntoFirebaseDB(talkPurc);
                if (partnerTurnOnPush)
                    snsMobilePushService.sendNotificationInFirstTalk(talkPurc.getOwnerPostId(), talkPurcCreateBodyRequest.getPostId(), shmUser.getNickName(), talkPurc.getTalkPurcId());

                if (userSettingService.checkReceivingMailInTalk(talkPurc.getOwnerPostId()))
                    mailService.sendEmailForFirstContact(talkPurc, shmUser.getNickName(), talkPurcCreateBodyRequest.getMsgContent());

                // push notification for user, who liked this post
                List<ShtUserFvrt> listUsesFvrt = userFvrtService.getUserFvrts(talkPurcCreateBodyRequest.getPostId());
                String msgContent = messageSource.getMessage("PUSH_POST_LIKED_CHANGE_STATUS", null, null);
                for (ShtUserFvrt userFvrt : listUsesFvrt) {
                    if (userFvrt.getShmUser().getId().intValue() != shmUser.getId().intValue()) {
                        snsMobilePushService.sendNotificationForUserLikedPost(PushMsgDetail.NtfTypeEnum.POST_LIKED_CHANGE_INFO, userFvrt.getShmUser().getId(), talkPurcCreateBodyRequest.getPostId(), msgContent);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            throw e;
        }

        return new ResponseEntity<ShtTalkPurcDTO>(talkPurc, HttpStatus.CREATED);
    }

    @GetMapping(value = "/talk/purcs")
    @AccessTokenAuthentication
    public TalkPurcResponse getAllTalkPurcInconversation(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("postId") Long postId) {
        Pageable pageable = createPage(page, size);
        ShmUser userInApp = requestContext.getUser();
        Page<ShtTalkPurcDTO> result = talkPurcService.findAllTalkPurcHasConversation(pageable, postId, userInApp);
        TalkPurcResponse talkPurcResponse = new TalkPurcResponse();
        talkPurcResponse.setListTalkPurc(result);
        ShmPost shmPost = postService.getPost(postId);
        if (shmPost != null) {
            final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.THUMBNAIL);
            for (Long id : allImageIdByType) {
                ShrFile shrFile = fileService.get(id);
                if (shrFile != null)
                    shmPost.getPostThumbnailImages().add(shrFile);
            }
            ShmPostDTO postDTO = PostMapper.mapEntityIntoDTO(shmPost);
            if (userInApp.getId().intValue() == shmPost.getShmUser().getId().intValue()) {
                postDTO.setOwnedByCurrentUser(true);
            } else {
                postDTO.setOwnedByCurrentUser(false);
            }
            talkPurcResponse.setShmPostDTO(postDTO);
        }

        return talkPurcResponse;
    }

    @PostMapping(value = "/talk/purcs/send-order-request")
    @AccessTokenAuthentication
    public ResponseEntity<Void> sendOrderRequest(@RequestParam("talkPurcId") Long talkPurcId) throws Exception {
        ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        ShmUser shmUser = requestContext.getUser();
        final ShmUser partner = talkPurc.getShmUser();
        final ShmPost shmPost = talkPurc.getShmPost();

        //check partner already block user
        boolean mutedBlockFlag = talkPurcService.checkUserMutedOrBlocked(partner.getId(), shmUser.getId());

        talkPurcService.sendOrderRequest(talkPurc, shmUser, mutedBlockFlag);
        final boolean partnerTurnOnPush = userSettingService.checkReceivePushTalkRoomTransaction(partner.getId());
        if (partner != null && shmPost != null && !mutedBlockFlag) {
            if (partnerTurnOnPush) {
                String msgContent = messageSource.getMessage("PUSH_SEND_ORDER_REQUEST", new Object[]{shmPost.getPostName()}, null);
                PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
                PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.TR_ORDER_REQUEST_SEND, null, null, talkPurcId, null, null, null, partner.getId());
                snsMobilePushService.sendNotificationForUser(partner.getId(), pushMsg, pushMsgDetail);
            }
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping(value = "/talk/purcs/accept-order-request")
    @AccessTokenAuthentication
    public ResponseEntity<Void> acceptOrderRequest(@RequestParam("talkPurcId") Long talkPurcId) throws Exception {
        ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        ShmUser shmUser = requestContext.getUser();
        talkPurcService.acceptOrderRequest(talkPurc, shmUser);
        ShmPost shmPost = talkPurc.getShmPost();
        ShmUser ownerPost = shmPost.getShmUser();
        boolean muteBlockFlag = false;
        if (shmPost != null && ownerPost != null)
            muteBlockFlag = talkPurcService.checkUserMutedOrBlocked(ownerPost.getId(), shmUser.getId());
        final boolean partnerTurnOnPush = userSettingService.checkReceivePushTalkRoomTransaction(ownerPost.getId());
        //Scheduler mail, push
        if (!muteBlockFlag)
            if (partnerTurnOnPush)
                snsMobilePushService.processAfterAcceptOrder(talkPurc, shmUser);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping(value = "/talk/purcs/reject-order-request")
    @AccessTokenAuthentication
    public ResponseEntity<Void> rejectOrderRequest(@RequestParam("talkPurcId") Long talkPurcId) throws Exception {
        ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        ShmUser shmUser = requestContext.getUser();
        talkPurcService.rejectOrderRequest(talkPurc, shmUser);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
