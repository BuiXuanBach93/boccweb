package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.controller.api.response.CounterMsgResponse;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
import jp.bo.bocc.entity.mapper.TalkPurcMsgMapper;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.FileUtils;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.repository.PostRepository;
import jp.bo.bocc.repository.TalkPurcMsgRepository;
import jp.bo.bocc.repository.TalkPurcRepository;
import jp.bo.bocc.repository.TalkQaRepository;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NguyenThuong on 4/1/2017.
 */
@Service
public class TalkPurcMsgServiceImpl implements TalkPurcMsgService {

    private final static Logger LOGGER = Logger.getLogger(TalkPurcMsgServiceImpl.class.getName());

    @Autowired
    TalkPurcRepository talkPurcRepository;

    @Autowired
    TalkPurcMsgRepository talkPurcMsgRepository;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    TalkPurcService talkPurcService;

    @Autowired
    PostService postService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserService userService;

    @Autowired
    private TalkPurcMsgService talkQaService;

    @Autowired
    private TalkQaRepository talkQaRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    ImageService imageService;

    @Value("${job.talk.wait.hour}")
    private int talkPurcWaitTime;

    @Autowired
    private SNSMobilePushService snsMobilePushService;

    @Autowired
    UserReadMsgService userReadMsgService;

    @Override
    public List<ShtTalkPurcMsg> getTalkMsgList(long talkPurcId) {
        ShtTalkPurc talkPurc = talkPurcRepository.findOne(talkPurcId);
        List<ShtTalkPurcMsg> messages = talkPurcMsgRepository.findByShtTalkPurcOrderByCreatedAtDesc(talkPurc);
        return messages;
    }

    @Override
    public Page<ShtTalkPurcMsgDTO> findTalkPurcMsgInTalkPurc(Pageable pageable, Long shtTalkPurcId) {
        Page<ShtTalkPurcMsgDTO> resultList = talkPurcMsgRepository.findTalkPurcMsgInTalkPurc(pageable, shtTalkPurcId);
        return resultList;
    }

    @Override
    public TalkPurcCreateBody sendMessage(Long talkPurcId, String msgContent, Long senderUserId, List<String> productImgList) throws Exception {
        TalkPurcCreateBody result = new TalkPurcCreateBody();
        final ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        ShmUser reciever = talkPurc.getShmUser();
        if(reciever.getId().intValue() == senderUserId.intValue() && talkPurc.getShmPost() != null){
            reciever = talkPurc.getShmPost().getShmUser();
        }
        if (talkPurc != null && reciever != null) {
            if (talkPurcService.checkUserBlocked(senderUserId, reciever.getId())) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100036", null, null));
            }

            // check user status
            if (senderUserId != null && talkPurc != null && talkPurc.getShmPost() != null && reciever != null) {
                final ShmPost shmPost = talkPurc.getShmPost();
                if (reciever != null && reciever.getStatus() == ShmUser.Status.LEFT) {
                    throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100095", null, null));
                }
                ShmUser owner = shmPost.getShmUser();
                if (owner != null && owner.getStatus() == ShmUser.Status.LEFT) {
                    throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100095", null, null));
                }
                result.setPostName(shmPost.getPostName());
                result.setTalkPurcId(talkPurcId);
                final Long ownerId = owner.getId();
                result.setOwnerPostId(ownerId);
                result.setOwnerPostNickName(owner.getNickName());
                if (senderUserId.longValue() == ownerId.longValue()) {
                    result.setEmailTo(reciever.getEmail());
                } else if (senderUserId.longValue() == reciever.getId().longValue()) {
                    result.setEmailTo(owner.getEmail());
                }
                result.setAvatarOwnerPostpath(FileUtils.buildImagePathByFileForUserAvatar(owner.getAvatar()));
            }

            ShtTalkPurcMsg shtTalkPurcMsg = new ShtTalkPurcMsg();
            shtTalkPurcMsg.setShtTalkPurc(talkPurc);
            ShmUser sender = userService.getUserById(senderUserId);
            shtTalkPurcMsg.setShmUserCreator(sender);
            shtTalkPurcMsg.setTalkPurcMsgStatus(ShtTalkPurcMsg.TalkPurcMsgStatusEnum.SENDING);
            if (CollectionUtils.isNotEmpty(productImgList)){
                msgContent = imageService.saveImageInChat(productImgList, talkPurc.getTalkPurcId());
                shtTalkPurcMsg.setTalkPurcMsgCont(msgContent);
                shtTalkPurcMsg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.IMAGE);
            }else {
                shtTalkPurcMsg.setTalkPurcMsgCont(msgContent);
                shtTalkPurcMsg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.NORMAL);
            }
            final ShtTalkPurcMsg save = talkPurcMsgRepository.save(shtTalkPurcMsg);

            result.setMsgType(save.getTalkPurcMsgType());
            result.setMsgStatus(save.getTalkPurcMsgStatus().toString());
            result.setMsgId(save.getTalkPurcMsgId());

            result.setMsgContent(msgContent);
            result.setTalkPurcMsgCreatorNickName(sender.getNickName());
            result.setCreatedAt(save.getCreatedAt());

            if (reciever != null) {
                result.setPartId(reciever.getId());
                result.setPartnerNickname(reciever.getNickName());
                result.setAvatarPartnerpath(FileUtils.buildImagePathByFileForUserAvatar(reciever.getAvatar()));
            }
        }

        return result;
    }

    @Override
    @Transactional
    public ShtTalkPurcMsg saveMsg(ShtTalkPurcMsg shtTalkPurcMsg) {
        return talkPurcMsgRepository.save(shtTalkPurcMsg);
    }

    @Override
    public void receiveNewMsg(boolean blocked, Long talkPurcId, Long userId) {
        if (blocked) {
            LOGGER.info("WARNING: msg from talkPurc - id: " + talkPurcId + " already block");
            return;
        }
        ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        if(talkPurc != null && talkPurc.getShmUser() != null){
            if(talkPurc.getShmUser().getId().intValue() == userId.intValue()){
                talkPurcMsgRepository.updateNewMsgToReceived(talkPurcId, userId, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
            }else{
                talkPurcMsgRepository.updateNewMsgToReceived(talkPurcId, userId, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
            }
        }
    }

    @Override
    public List<ShtTalkPurcMsgDTO> findTalkPurcMsgInTalkPurcWithTime(String msgTime, String isBefore, Long talkPurcId, int size, Long userIdUseApp) {
        if (StringUtils.isEmpty(msgTime)) {
            //always search msg created before present.
            isBefore = "TRUE";
        }
        ShtTalkPurc shtTalkPurc = talkPurcRepository.findOne(talkPurcId);
        final ShmPost shmPost = shtTalkPurc.getShmPost();
        ShmUser ownerPost = shmPost.getShmUser();
        List<ShtTalkPurcMsg> shtTalkPurcMsgList = null;
        List<ShtTalkPurcMsgDTO> result = null;

        ShmUser partner = shtTalkPurc.getShmUser();
        //get msg for owner post
        if (partner != null) {
            if (ownerPost.getId().longValue() == userIdUseApp.longValue()) {
            LocalDateTime userBlockTime = talkPurcService.getUserBlockTime(ownerPost.getId(), partner.getId());
                final String msgTypeForOwnerPost = ShtTalkPurcMsg.TalkPurcMsgTypeEnum.NORMAL.ordinal() + "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_SENT_FOR_OWNER.ordinal() + ","
                        + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER.ordinal() + "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.REVIEW_FOR_OWNER.ordinal()+ "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.IMAGE.ordinal();
               if (new Boolean(isBefore)) {
                   if(userBlockTime != null) {
                        shtTalkPurcMsgList = talkPurcMsgRepository.findTalkPurcMsgBeforeTime(userBlockTime, talkPurcId, size, msgTypeForOwnerPost);
                   }else {
                       shtTalkPurcMsgList = talkPurcMsgRepository.findTalkPurcMsgBeforeTime(ConverterUtils.convertStringToLocaldatetime(msgTime), talkPurcId, size, msgTypeForOwnerPost);
                   }
                } else {
                    shtTalkPurcMsgList = talkPurcMsgRepository.findTalkPurcMsgAfterTime(ConverterUtils.convertStringToLocaldatetime(msgTime), talkPurcId, size, msgTypeForOwnerPost);
                }
                result = buildListTalkPurcMsgDTOForOwnerPost(shtTalkPurcMsgList, userIdUseApp, talkPurcId);

                //get msg for partner in post.
            } else {
                LocalDateTime userBlockTime = talkPurcService.getUserBlockTime(partner.getId(), ownerPost.getId());
                final String msgTypeForBuyerPost = ShtTalkPurcMsg.TalkPurcMsgTypeEnum.NORMAL.ordinal()+","+ ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_SENT_FOR_PARTNER.ordinal() + "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_ACCEPTED.ordinal() + ","
                        + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_REJECTED.ordinal() + "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER.ordinal() + "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.REVIEW_FOR_PARTNER.ordinal()
                        + "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_NG_MSG_FOR_PARTNER.ordinal()+ "," + ShtTalkPurcMsg.TalkPurcMsgTypeEnum.IMAGE.ordinal();
                if (new Boolean(isBefore)) {
                    if(userBlockTime != null) {
                        shtTalkPurcMsgList = talkPurcMsgRepository.findTalkPurcMsgBeforeTime(userBlockTime, talkPurcId, size, msgTypeForBuyerPost);
                    }else {
                        shtTalkPurcMsgList = talkPurcMsgRepository.findTalkPurcMsgBeforeTime(ConverterUtils.convertStringToLocaldatetime(msgTime), talkPurcId, size, msgTypeForBuyerPost);
                    }
                } else {
                    shtTalkPurcMsgList = talkPurcMsgRepository.findTalkPurcMsgAfterTime(ConverterUtils.convertStringToLocaldatetime(msgTime), talkPurcId, size, msgTypeForBuyerPost);
                }
                result = buildListTalkPurcMsgDTOForPartner(shtTalkPurcMsgList, userIdUseApp, talkPurcId);
            }
        }
        return result;
    }

    private List<ShtTalkPurcMsgDTO> buildListTalkPurcMsgDTOForPartner(List<ShtTalkPurcMsg> shtTalkPurcMsgList, Long userIdUseApp, Long talkPurcId) {
        List<ShtTalkPurcMsgDTO> result = new ArrayList<>();
        ShmUser shmUser = userService.getUserById(userIdUseApp);
        ShtTalkPurc shtTalkPurc = talkPurcRepository.findOne(talkPurcId);
        final ShmPost shmPost = shtTalkPurc.getShmPost();
        if (shmPost != null) {
            ShmUser partner = shmPost.getShmUser();
            setValueForShtTalkPurcMsgDTO(shtTalkPurcMsgList, result, shmUser, shtTalkPurc, partner);
        }
        return result;
    }

    private void setValueForShtTalkPurcMsgDTO(List<ShtTalkPurcMsg> shtTalkPurcMsgList, List<ShtTalkPurcMsgDTO> result, ShmUser shmUser, ShtTalkPurc shtTalkPurc, ShmUser partner) {
        for (ShtTalkPurcMsg shtTalkPurcMsg : shtTalkPurcMsgList) {
            ShtTalkPurcMsgDTO shtTalkPurcMsgDTO = TalkPurcMsgMapper.mapEntityIntoDTO(shtTalkPurcMsg);
            if (shtTalkPurc != null) {
                shtTalkPurcMsgDTO.setPartnerNickName(partner.getNickName());
                shtTalkPurcMsgDTO.setPartnerAvatar(partner.getAvatar());
                shtTalkPurcMsgDTO.setPartnerId(partner.getId());
                shtTalkPurcMsgDTO.setUserAppId(shmUser.getId());
                shtTalkPurcMsgDTO.setUserAppAvatarPath(shmUser.getAvatar());
                shtTalkPurcMsgDTO.setUserAppNickName(shmUser.getNickName());
                shtTalkPurcMsgDTO.setShmUserCreatorId(shtTalkPurcMsg.getShmUserCreator().getId());
            }
            result.add(shtTalkPurcMsgDTO);
        }
    }

    private List<ShtTalkPurcMsgDTO> buildListTalkPurcMsgDTOForOwnerPost(List<ShtTalkPurcMsg> shtTalkPurcMsgList, Long userIdUseApp, Long talkPurcId) {
        List<ShtTalkPurcMsgDTO> result = new ArrayList<>();
        ShmUser shmUser = userService.getUserById(userIdUseApp);
        ShtTalkPurc shtTalkPurc = talkPurcRepository.findOne(talkPurcId);
        ShmUser partner = shtTalkPurc.getShmUser();
        setValueForShtTalkPurcMsgDTO(shtTalkPurcMsgList, result, shmUser, shtTalkPurc, partner);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public int countNewMsgForAllTalkByPostIdForOwnerPost(Long postId, Long userId) {
        int result = talkPurcMsgRepository.countNewMsgInTalkPurcForOwner(userId, postId);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public CounterMsgResponse counterMsgResponseForTopPage(Long userIdUsingApp) {
        CounterMsgResponse result = new CounterMsgResponse();

        result.setToTalNewMsgForOwnerPost(talkPurcMsgRepository.countNewMsgInTalkPurcForOwner(userIdUsingApp, null));

        //new msg from others post
        result.setToTalNewMsgForOthersPost(talkPurcMsgRepository.countNewMsgForAllTalkFromOthers(userIdUsingApp, null));

        //new msg from admin
        result.setToTalNewMsgFormAdmin(talkQaService.counNewMsgFromAdmin(userIdUsingApp) + userReadMsgService.countNewMsgSystemPushAll(userIdUsingApp));

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public int counNewMsgFromAdmin(Long userIdUsingApp) {
        return talkQaRepository.countNewMsgFromAdminForUser(userIdUsingApp);
    }

    @Override
    public void inActiveSystemMsgForUser(Long talkPurcId, ShtTalkPurcMsg.TalkPurcMsgTypeEnum talkPurcMsgType) {
        talkPurcMsgRepository.inActiveSystemMsgForUser(talkPurcId, talkPurcMsgType);
    }

    @Transactional(readOnly = true)
    @Override
    public int countNewMsgForAllTalkByPostIdFromOther(Long postId, Long userId) {
        int result = talkPurcMsgRepository.countNewMsgForAllTalkFromOthers(userId, postId);
        return result;
    }

    @Override
    public void handleTalkAfter48h(ShtTalkPurc talkPurc){
        if(talkPurc == null){
            return;
        }
        if(talkPurc.getTalkPurcStatus() != ShtTalkPurc.TalkPurcStatusEnum.TEND_TO_SELL
                || talkPurc.getTalkPurcTendToSellTime() == null){
            return;
        }
        LocalDateTime expiryDate = talkPurc.getTalkPurcTendToSellTime().plusHours(talkPurcWaitTime);
        LocalDateTime currentTime = LocalDateTime.now();
        if(expiryDate.isBefore(currentTime)){
            // update status of talkpurc
            talkPurc.setTalkPurcTendToSellTime(null);
            talkPurc.setTalkPurcStatus(ShtTalkPurc.TalkPurcStatusEnum.TALKING);
            talkPurcService.save(talkPurc);

            // send msg to owner
            ShtTalkPurcMsg msg = createSysMsgForOwner(talkPurc);
            talkPurcMsgService.saveMsg(msg);

            // inactive system msg for partner
            inActiveSystemMsgForUser(talkPurc.getTalkPurcId(), ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);

            // update status of post
            ShmPost shmPost = talkPurc.getShmPost();
            if(shmPost != null && shmPost.getPostSellStatus() == ShmPost.PostSellSatus.TEND_TO_SELL){
                shmPost.setPostSellStatus(ShmPost.PostSellSatus.IN_CONVERSATION);
                postService.savePost(shmPost);
            }
        }

    }

    @Override
    @Transactional
    public void handleTalkAfter48h() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime tendToSellTime = currentTime.minusHours(talkPurcWaitTime);

        List<ShtTalkPurc> listTalkExpired = talkPurcService.getTalkPurcsExpiredTendToSell(tendToSellTime);
        if(listTalkExpired == null){
            return ;
        }
        for (ShtTalkPurc talkPurc: listTalkExpired) {
            handleTalkAfter48h(talkPurc);
        }
    }

    private ShtTalkPurcMsg createSysMsgForOwner(ShtTalkPurc talkPurc){
        String partnerName = talkPurc.getShmUser().getNickName();
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        msg.setShmUserCreator(talkPurc.getShmUser());
        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10005", new Object[]{partnerName},null));
        return msg;
    }

    @Override
    public CounterMsgResponse hasNewMsgResponseForTopPage(Long userIdUsingApp) {
        CounterMsgResponse result = new CounterMsgResponse();

        final int newMsgOwnerPost = talkPurcMsgRepository.countNewMsgInTalkPurcForOwner(userIdUsingApp, null);
        if (newMsgOwnerPost > 0 ) {
            result.setNewMsgFlagInOwnerPost(true);
            result.setToTalNewMsgForOwnerPost(newMsgOwnerPost);
        }

        //new msg from others post
        final int newMsgFromOther = talkPurcMsgRepository.countNewMsgForAllTalkFromOthers(userIdUsingApp, null);
        if (newMsgFromOther > 0) {
            result.setNewMsgFlagInFromOthersPost(true);
            result.setToTalNewMsgForOthersPost(newMsgFromOther);
        }
        //new msg from admin
        final int newMsgFromAdmin = talkQaService.counNewMsgFromAdmin(userIdUsingApp);
        int newMsgPushAll = userReadMsgService.countNewMsgSystemPushAll(userIdUsingApp);
        if((newMsgFromAdmin + newMsgPushAll) > 0 ) {
            result.setNewMsgFlagInFromAdmin(true);
            result.setToTalNewMsgFormAdmin(newMsgFromAdmin + newMsgPushAll);
        }

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShtTalkPurcMsg> findAllOrderByTalkPurcMsgIdAsc() {
        return talkPurcMsgRepository.findAllByOrderByTalkPurcMsgIdAsc();
    }

    @Override
    public Long countNewMsgByUserId(Long userId) {
        return talkPurcMsgRepository.countNewMsgByUserId(userId);
    }
}
