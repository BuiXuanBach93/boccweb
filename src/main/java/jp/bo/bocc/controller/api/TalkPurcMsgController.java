package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.controller.api.response.CounterMsgResponse;
import jp.bo.bocc.controller.api.response.TalkPurcDetailResponse;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Namlong on 4/1/2017.
 */
@RestController
public class TalkPurcMsgController extends BoccBaseController {

    @Autowired
    PostService postService;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    TalkPurcService talkPurcService;

    @Autowired
    UserRevService userRevService;

    @Autowired
    FileService fileService;

    @Autowired
    UserService userService;

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

    @GetMapping(value = "/talk/purcs/{talkPurcId}")
    @AccessTokenAuthentication
    public TalkPurcDetailResponse getTalkPurcMsgDetail(@PathVariable(value = "talkPurcId") Long talkPurcId,
                                                       @RequestParam(value = "msgTime", required = false) String msgTime,
                                                       @RequestParam("isBefore") String isBefore, @RequestParam("size") int size) throws Exception {

        Long userId = getUserIdUsingApp();
        List<ShtTalkPurcMsgDTO> shtTalkPurcMsgDTOS = talkPurcMsgService.findTalkPurcMsgInTalkPurcWithTime(msgTime, isBefore, talkPurcId, size, userId);
        ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        ShmPost shmPost = talkPurc.getShmPost();
        ShmUser ownerPost = shmPost.getShmUser();

        boolean blocked = false;
        boolean muted = false;
        Long ownerId = ownerPost.getId();
        ShmUser partner = talkPurc.getShmUser();
        if (partner != null) {
            if (ownerId.longValue() == getUserUsingApp().getId().longValue()) {
                blocked = talkPurcService.checkUserBlocked(ownerId, partner.getId());
                muted = talkPurcService.checkUserMutedPartner(ownerId, partner.getId());
            } else if (ownerPost.getId().longValue() != getUserUsingApp().getId().longValue()) {
                blocked = talkPurcService.checkUserBlocked(partner.getId(), ownerId);
                muted = talkPurcService.checkUserMutedPartner(partner.getId(), ownerId);
            }
        }

        //update msg status to received.
        if (shtTalkPurcMsgDTOS != null) {
            talkPurcMsgService.receiveNewMsg(blocked, talkPurcId, userId);
        }
        TalkPurcDetailResponse response = new TalkPurcDetailResponse();

        // handle if talk tend to sell after 48h
        talkPurcMsgService.handleTalkAfter48h(talkPurc);
        if (talkPurc != null && shmPost != null && ownerPost != null) {
            if (shmPost != null) {
                final List<Long> allImageIdByType = FileUtils.getAllImageIdByType(shmPost.getPostImages(), ImageSavedEnum.THUMBNAIL);
                for (Long id : allImageIdByType) {
                    ShrFile shrFile = fileService.get(id);
                    if (shrFile != null)
                        shmPost.getPostThumbnailImages().add(shrFile);
                }
                ShmPostDTO postDTO = PostMapper.mapEntityIntoDTO(talkPurc.getShmPost());
                if (userId.intValue() == ownerPost.getId().intValue()) {
                    postDTO.setOwnedByCurrentUser(true);
                    if (talkPurc.getShmUser() != null) {
                        response.setPartnerName(talkPurc.getShmUser().getNickName());
                    }
                } else {
                    postDTO.setOwnedByCurrentUser(false);
                    if (ownerPost != null) {
                        response.setPartnerName(ownerPost.getNickName());
                    }
                }
                response.setCanReview(false);
                if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.SOLD) {
                    if (postDTO.getOwnedByCurrentUser() && shmPost.getPartnerId() != null
                            && shmPost.getPartnerId().intValue() == talkPurc.getShmUser().getId().intValue()) {
                        response.setCanReview(true);
                    }
                    if (!postDTO.getOwnedByCurrentUser() && shmPost.getPartnerId() != null
                            && shmPost.getPartnerId().intValue() == userId.intValue()) {
                        response.setCanReview(true);
                    }
                }

                response.setTalkPurcStatus(talkPurc.getTalkPurcStatus());
                response.setShmPost(postDTO);
                response.setOwnerCtrlStatus(ownerPost.getCtrlStatus());
                response.setPartnerCtrlStatus(talkPurc.getShmUser().getCtrlStatus());
                response.setOwnerStatus(ownerPost.getStatus());
                response.setPartnerStatus(talkPurc.getShmUser().getStatus());
                response.setTendToSellTime(talkPurc.getTalkPurcTendToSellTime());
            }
            if (userRevService.getReviewByTalkPurcId(talkPurcId, userId) > 0) {
                response.setIsReviewed(true);
            } else {
                response.setIsReviewed(false);
            }
            response.setListTalkPurcMsg(shtTalkPurcMsgDTOS);
            if (blocked) {
                response.setBlocked(true);
            }
            if (muted) {
                response.setMuted(true);
            }

        }

        return response;
    }

    /**
     * @param talkPurcCreateBodyRequest
     * @return
     * @throws Exception
     */

    @PostMapping(value = "/talk/purcs-send-msg")
    @AccessTokenAuthentication
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TalkPurcCreateBody> sendTalkMessage(@RequestBody TalkPurcCreateBody talkPurcCreateBodyRequest) throws Exception {
        Long userId = getUserIdUsingApp();
        TalkPurcCreateBody talkPurcCreateBody = talkPurcMsgService.sendMessage(talkPurcCreateBodyRequest.getTalkPurcId(), talkPurcCreateBodyRequest.getMsgContent(), userId, talkPurcCreateBodyRequest.getProductImgList());
        final Long partId = talkPurcCreateBody.getPartId();
        if (partId != null) {

            final String msgCont = messageSource.getMessage("PUSH_SEND_MSG_TALK", new Object[]{getUserUsingApp().getNickName()}, null);
            PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);

            ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcCreateBodyRequest.getTalkPurcId());
            Long recieverId = talkPurc.getShmUser().getId();

            //check userInApp and partnerInTalk
            if (recieverId.intValue() == userId.intValue()) {
                recieverId = talkPurc.getShmPost().getShmUser().getId();
            }

            Long ownerPostId = talkPurcCreateBody.getOwnerPostId();
            String receiverName = "";

            //partner send msg, notify owner post
            if (ownerPostId != null && userId.longValue() != ownerPostId.longValue()) {
                boolean mutedBlockFlag = talkPurcService.checkUserMutedOrBlocked(ownerPostId, userId);
                boolean checkTurnOnNotification = userSettingService.checkReceivePushOn(ownerPostId);
                boolean checkTurnOnNotificationTalk = userSettingService.checkReceivePushTalkRoomFirstMsg(ownerPostId);
                boolean checkTurnOnEmail = userSettingService.checkReceivingMailInTalk(ownerPostId);
                if (!mutedBlockFlag) {

                    //upload message to realtime database
                    //   fireBaseService.sendNewMsgInToFireBaseDB(talkPurcCreateBody);
                    if (checkTurnOnNotification) {
                        if (checkTurnOnNotificationTalk) {
                            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.TR_SEND_MSG, talkPurcCreateBody.getMsgContent(), null, talkPurcCreateBody.getTalkPurcId(), null, null, null, ownerPostId);
                            snsMobilePushService.sendNotificationForUserWhenSendMsg(ownerPostId, pushMsg, pushMsgDetail);
                        }
                    }
                    if (checkTurnOnEmail) {
                        receiverName = talkPurcCreateBody.getOwnerPostNickName();
                        mailService.sendEmailChatNormal(talkPurcCreateBody, receiverName, getUserUsingApp().getNickName(), talkPurcCreateBodyRequest.getMsgContent());
                    }
                }

                //owner send msg, notify partner
            } else if (ownerPostId != null && userId.longValue() == ownerPostId.longValue()) {
                boolean mutedBlockFlag = talkPurcService.checkUserMutedOrBlocked(partId, userId);
                boolean checkTurnOnNotification = userSettingService.checkReceivePushOn(partId);
                boolean checkTurnOnNotificationFirstMsg = userSettingService.checkReceivePushTalkRoomFirstMsg(partId);
                boolean checkTurnOnEmail = userSettingService.checkReceivingMailInTalk(partId);
                if (!mutedBlockFlag) {

                    //upload message to realtime database
                    //   fireBaseService.sendNewMsgInToFireBaseDB(talkPurcCreateBody);
                    if (checkTurnOnNotification) {
                        if (checkTurnOnNotificationFirstMsg) {
                            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.TR_SEND_MSG, talkPurcCreateBody.getMsgContent(), null, talkPurcCreateBody.getTalkPurcId(), null, null, null, partId);
                            snsMobilePushService.sendNotificationForUserWhenSendMsg(partId, pushMsg, pushMsgDetail);
                        }
                    }
                    if (checkTurnOnEmail) {
                        receiverName = talkPurcCreateBody.getPartnerNickname();
                        mailService.sendEmailChatNormal(talkPurcCreateBody, receiverName, getUserUsingApp().getNickName(), talkPurcCreateBodyRequest.getMsgContent());
                    }
                }
            }
        }
        return new ResponseEntity<TalkPurcCreateBody>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/talk/purcs-msg/count-new-msg")
    @AccessTokenAuthentication
    public CounterMsgResponse counterMsgResponseForTopPage() throws Exception {
        final Long userIdUsingApp = getUserIdUsingApp();
        return talkPurcMsgService.hasNewMsgResponseForTopPage(userIdUsingApp);

    }
}
