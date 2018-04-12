package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.entity.mapper.TalkPurcMapper;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.jobs.JobHandleTalkPurcAfter48h;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.repository.TalkPurcMsgRepository;
import jp.bo.bocc.repository.TalkPurcRepository;
import jp.bo.bocc.repository.UserTalkPurcRepository;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Namlong on 3/27/2017.
 */
@Service
public class TalkPurcServiceImpl implements TalkPurcService {

    private final static Logger LOGGER = Logger.getLogger(PostServiceImpl.class.getName());
    private final TalkPurcRepository talkPurcRepository;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @Value("${mail.admin}")
    private String mailAdminAddress;

    @Autowired
    private TalkPurcMsgRepository talkPurcMsgRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private UserTalkPurcRepository userBlockRepository;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

//    @Autowired
//    FireBaseService fireBaseService;


    @Value("${job.talk.flag}")
    private boolean jobFlag;

    @Value("${job.talk.wait.hour}")
    private int waitTime;

    @Autowired
    private SNSMobilePushService snsMobilePushService;
    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    public TalkPurcServiceImpl(TalkPurcRepository talkPurcRepository) {
        this.talkPurcRepository = talkPurcRepository;
    }

    @Override
    public Long findTalkPurcByPostIdAndPartnerId(Long postId, Long partnerId) {
        final Long talkPurcByPostIdAndPartnerId = talkPurcRepository.findTalkPurcByPostIdAndPartnerId(postId, partnerId);
        return talkPurcByPostIdAndPartnerId;
    }

    @Override
    public boolean isFirstPurchase(Long postId, Long partnerId) {
        if (findTalkPurcByPostIdAndPartnerId(postId, partnerId) != null)
            return false;
        return true;
    }

    @Override
    public ShtTalkPurcDTO createTalkPurc(ShmUser shmUser, TalkPurcCreateBody talkPurcCreateBodyRequest) throws Exception {
        // validate user create talk
        userService.checkUserStatus(shmUser);

        final Long postId = talkPurcCreateBodyRequest.getPostId();
        ShtTalkPurc shtTalkPurc = new ShtTalkPurc();
        ShmPost shmPost = postService.getPost(postId);

        // validate post owner
        final ShmUser ownerPost = shmPost.getShmUser();
        if (ownerPost.getId().longValue() == shmUser.getId().longValue()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100115", null, null));
        }
        if (shmPost != null && ownerPost != null) {
            userService.checkUserStatus(ownerPost);
        }

        // validate post status
        validatePostStatus(shmPost);
        if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.SOLD) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100026", null, null));
        }

        if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.PUBLIC) {
            shmPost.setPostSellStatus(ShmPost.PostSellSatus.IN_CONVERSATION);
            postService.savePost(shmPost);
        }

        shtTalkPurc.setShmPost(shmPost);
        final Long partId = talkPurcCreateBodyRequest.getPartId();
        ShmUser shmPartnerUser = userService.getUserById(partId);
        shtTalkPurc.setShmUser(shmPartnerUser);
        ShtTalkPurc talkPurcSaved = talkPurcRepository.save(shtTalkPurc);

        // create message
        ShtTalkPurcMsg shtTalkPurcMsg = new ShtTalkPurcMsg();
        shtTalkPurcMsg.setShtTalkPurc(talkPurcSaved);
        final String msgContent = talkPurcCreateBodyRequest.getMsgContent();
        shtTalkPurcMsg.setTalkPurcMsgCont(msgContent);
        shtTalkPurcMsg.setShmUserCreator(shmUser);
        final ShtTalkPurcMsg save = talkPurcMsgRepository.save(shtTalkPurcMsg);
        ShtTalkPurcDTO shtTalkPurcDTO = new ShtTalkPurcDTO();
        final Long talkPurcId = talkPurcSaved.getTalkPurcId();
        shtTalkPurcDTO.setTalkPurcId(talkPurcId);
        if (ownerPost != null)
            shtTalkPurcDTO.setEmailOwnerPost(ownerPost.getEmail());
        shtTalkPurcDTO.setPostName(shmPost.getPostName());
        shtTalkPurcDTO.setOwnerPostNickName(ownerPost.getNickName());
        shtTalkPurcDTO.setOwnerPostId(ownerPost.getId());
        shtTalkPurcDTO.setTalkPurcMsgCreator(save.getShmUserCreator().getId());
        shtTalkPurcDTO.setTalkPurcMsgCreatorNickName(save.getShmUserCreator().getNickName());
        shtTalkPurcDTO.setMsgContent(msgContent);
        shtTalkPurcDTO.setPartnerId(talkPurcId);
        shtTalkPurcDTO.setCreatedAt(save.getCreatedAt());
        shtTalkPurcDTO.setMsgId(save.getTalkPurcMsgId());
        shtTalkPurcDTO.setMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.NORMAL);
        return shtTalkPurcDTO;
    }

    @Override
    public Page<ShtTalkPurcDTO> findAllTalkPurcByPostId(Pageable pageable, Long postId) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShtTalkPurcDTO> findAllTalkPurcHasConversation(Pageable pageable, Long postId, ShmUser userInApp) {
        List<ShtTalkPurc> listFromDB = talkPurcRepository.getListTalkPurcByPostId(postId);
        Page<ShtTalkPurcDTO> result = null;
        for (ShtTalkPurc shtTalkPurc : listFromDB) {
            result = buildResult(listFromDB, pageable, userInApp, postId);
        }
        return result;
    }

    private Page<ShtTalkPurcDTO> buildResult(List<ShtTalkPurc> listFromDB, Pageable pageable, ShmUser userInApp, Long postId) {
        final int totalElement = listFromDB.size();
        List<ShtTalkPurcDTO> shtTalkPurcDTOList = new ArrayList<>();
        final Long userIdUserApp = userInApp.getId();
        for (ShtTalkPurc shtTalkPurc : listFromDB) {
            ShmUser partnerUser = shtTalkPurc.getShmUser();
            ShmPost shmPost = shtTalkPurc.getShmPost();
             ShmUser ownerPost = shmPost.getShmUser();
            if (partnerUser != null & shmPost != null && ownerPost != null) {
                ShtTalkPurcDTO shtTalkPurcDTO = TalkPurcMapper.mapEntityIntoDTO(shtTalkPurc);
                int totalMsgFromOther = 0;
                final Long partnerId = partnerUser.getId();
                boolean checkUserBlock = false;
                if (partnerId.longValue() == userIdUserApp.longValue()) {
                    checkUserBlock = checkUserBlocked(partnerId, shmPost.getShmUser().getId());
                }else if (ownerPost.getId() == userIdUserApp.longValue()) {
                    checkUserBlock = checkUserBlocked(ownerPost.getId(),partnerId );
                }

                if (partnerId.longValue() != userIdUserApp.longValue()) {
                    if (!checkUserBlock)
                        totalMsgFromOther = talkPurcMsgRepository.countNewMsgInTalkSentForOwnerPost(userIdUserApp, shtTalkPurc.getTalkPurcId());
                } else {
                    if (!checkUserBlock)
                        totalMsgFromOther = talkPurcMsgRepository.countNewMsgInTalkSentForPartnerPost(userIdUserApp, shtTalkPurc.getTalkPurcId());
                }
                shtTalkPurcDTO.setTotalNewMsgFromOther(totalMsgFromOther);
                shtTalkPurcDTO.setUserAppAvatarPath(userInApp.getAvatar());
                shtTalkPurcDTO.setPartnerAvatarPath(partnerUser.getAvatar());
                shtTalkPurcDTO.setPartnerNickName(partnerUser.getNickName());

                shtTalkPurcDTO.setOwnerCtrlStatus(ownerPost.getCtrlStatus());
                shtTalkPurcDTO.setPartnerCtrlStatus(shtTalkPurc.getShmUser().getCtrlStatus());
                shtTalkPurcDTO.setOwnerStatus(ownerPost.getStatus());
                shtTalkPurcDTO.setPartnerStatus(shtTalkPurc.getShmUser().getStatus());

                Boolean isOwner = false;
                if (shmPost != null && shmPost.getShmUser().getId() != null && userIdUserApp.intValue() == shmPost.getShmUser().getId().intValue()) {
                    isOwner = true;
                }
                ShtTalkPurcMsg shtTalkPurcMsg = talkPurcMsgRepository.getLatestMsgInTalkPurcForUser(shtTalkPurc.getTalkPurcId(), isOwner);
                if (shtTalkPurcMsg.getShmUserCreator().getId().longValue() == userInApp.getId().longValue()) {
                    shtTalkPurcDTO.setLastMsgIsOwnerPost(true);
                }
                String talkPurcMsgCont = "";
                if (!checkUserBlock) {
                    if (shtTalkPurcMsg.getTalkPurcMsgType() == ShtTalkPurcMsg.TalkPurcMsgTypeEnum.IMAGE)
                        talkPurcMsgCont = "画像を添付しています";
                    else
                        talkPurcMsgCont = shtTalkPurcMsg.getTalkPurcMsgCont();
                }else {
                    shtTalkPurcMsg = talkPurcMsgRepository.getLatestMsgInTalkPurcReceivedAfterBlocktime(shtTalkPurc.getTalkPurcId(), isOwner);
                    if (shtTalkPurcMsg != null) {
                        if (shtTalkPurcMsg.getTalkPurcMsgType() == ShtTalkPurcMsg.TalkPurcMsgTypeEnum.IMAGE)
                            talkPurcMsgCont = "画像を添付しています";
                        else
                            talkPurcMsgCont = shtTalkPurcMsg.getTalkPurcMsgCont();

                    }
                }
                    shtTalkPurcDTO.setLastMesageInTalkPurcList(talkPurcMsgCont);
                shtTalkPurcDTOList.add(shtTalkPurcDTO);
            }
        }
        Page<ShtTalkPurcDTO> result = new PageImpl<ShtTalkPurcDTO>(shtTalkPurcDTOList, pageable, totalElement);
        return result;
    }

    @Override
    public List<ShtTalkPurc> findTalksByPostId(ShmPost post) {
        List<ShtTalkPurc> talkPurcList = talkPurcRepository.findByShmPostOrderByUpdatedAtDesc(post);
        String avatar;
        for (ShtTalkPurc talkPurc : talkPurcList) {
            if(talkPurc.getShmUser() == null){
                continue;
            }
            avatar = userService.buildOriginalAvatarPathForUser(talkPurc.getShmUser().getAvatar());
            talkPurc.setPartnerAvatar(avatar);
        }
        return talkPurcList;
    }

    @Override
    public List<ShtTalkPurc> findTalksByPostIdAndStatus(ShmPost post, ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus) {
        return talkPurcRepository.findListTalkPurcIdByPostIdAndStatus(post.getPostId(), talkPurcStatus);
    }

    @Override
    public List<ShtTalkPurc> findTalksByOwnerIdAndStatus(Long userId, ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus) {
        return talkPurcRepository.findTalksByOwnerIdAndStatus(userId, talkPurcStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShtTalkPurc> findTalkListByPostId(Long postId) {
        return talkPurcRepository.findTalkListByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShtTalkPurc> findTalksByOwnerIdOpen(Long userId) {
        return talkPurcRepository.findTalksByOwnerIdOpen(userId);
    }

    @Override
    public List<ShtTalkPurc> findTalksByPartnerIdAndStatus(Long partnerId, ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus) {
        return talkPurcRepository.findTalksByPartnerIdAndStatus(partnerId, talkPurcStatus);
    }

    @Override
    public List<ShtTalkPurc> findTalksByPartnerIdOpen(Long partnerId) {
        return talkPurcRepository.findTalksByPartnerIdOpen(partnerId);
    }

    @Override
    public Long countTalkByPostId(long postId) {
        return talkPurcRepository.countTalkPurcByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public ShtTalkPurc getTalkPurcById(long id) {
        return talkPurcRepository.getTalkPurcById(id);
    }

    @Override
    public void acceptOrderRequest(ShtTalkPurc talkPurc, ShmUser currentUser) throws Exception {

        validateTalkPurc(talkPurc);
        userService.checkUserStatus(currentUser);
        if (talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
            validatePostStatus(talkPurc.getShmPost());
            userService.checkUserStatus(talkPurc.getShmPost().getShmUser());
        }

        talkPurc.setTalkPurcStatus(ShtTalkPurc.TalkPurcStatusEnum.CLOSED);
        talkPurcRepository.save(talkPurc);

        ShmPost shmPost = talkPurc.getShmPost();
        shmPost.setPostSellStatus(ShmPost.PostSellSatus.SOLD);
        shmPost.setPartnerId(talkPurc.getShmUser().getId());
        postService.savePost(shmPost);

        String postOwnerEmail = talkPurc.getShmPost().getShmUser().getEmail();
        Long postOwnerId = talkPurc.getShmPost().getShmUser().getId();
        String partnerName = talkPurc.getShmUser().getNickName();
        Long partnerId = talkPurc.getShmUser().getId();
        final ShmUser ownerPost = shmPost.getShmUser();
        String ownerName = ownerPost.getNickName();
        String postName = shmPost.getPostName();

        // send message accepted to owner
        ShtTalkPurcMsg acceptOrderMsg = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_ACCEPTED);
        acceptOrderMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10008", null, null));
        ShtTalkPurcMsg acceptOrderMsgSaved = talkPurcMsgRepository.save(acceptOrderMsg);
      //  fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, acceptOrderMsgSaved));

        ShtTalkPurcMsg systemOrderMsg = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
        systemOrderMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10002", new Object[]{partnerName, partnerName}, null));
        ShtTalkPurcMsg systemOrderMsgSaved = talkPurcMsgRepository.save(systemOrderMsg);
      //  fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, systemOrderMsgSaved));

        // inactive system message for partner
        talkPurcMsgRepository.inActiveSystemMsgForUser(talkPurc.getTalkPurcId(), ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);

        // send message accepted to partner
        ShtTalkPurcMsg systemOrderMsgForPartner = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
        systemOrderMsgForPartner.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10003", new Object[]{ownerName, ownerName}, null));
        ShtTalkPurcMsg systemOrderMsgForPartnerSaved = talkPurcMsgRepository.save(systemOrderMsgForPartner);
      //  fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, systemOrderMsgForPartnerSaved));

        // send close message to other talk room
        List<ShtTalkPurc> listTalkOther = talkPurcRepository.getListOtherTalkByPostId(shmPost.getPostId(), talkPurc.getTalkPurcId());
        if (listTalkOther != null) {
            for (ShtTalkPurc otherTalk : listTalkOther) {
                ShtTalkPurcMsg systemForOtherTalkMsg = createTalkPurcMsg(otherTalk, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
                systemForOtherTalkMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10004", new Object[]{ownerName}, null));
                ShtTalkPurcMsg systemForOtherTalkMsgSaved = talkPurcMsgRepository.save(systemForOtherTalkMsg);
             //   fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, systemForOtherTalkMsgSaved));

                otherTalk.setTalkPurcStatus(ShtTalkPurc.TalkPurcStatusEnum.CLOSED);
                talkPurcRepository.save(otherTalk);
            }
        }
        // send mail to owner post
        mailService.sendEmailAcceptOrderRequest(postOwnerEmail, postName, partnerName, ownerName, postOwnerId);

        // interrupt job
        interruptJob(talkPurc.getTalkPurcId());
    }

    public void interruptJob(Long talkPurcId) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        // Tell quartz to schedule the job using trigger
        try {
            scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_TALK_PURC_ID.getValue() + talkPurcId, JobEnum.JOB_TALK_PURC_GROUP.getValue()));
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void rejectOrderRequest(ShtTalkPurc talkPurc, ShmUser currentUser) throws Exception {
        validateTalkPurc(talkPurc);
        userService.checkUserStatus(currentUser);
        if (talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null) {
            validatePostStatus(talkPurc.getShmPost());
            userService.checkUserStatus(talkPurc.getShmPost().getShmUser());
        }

        talkPurc.setTalkPurcStatus(ShtTalkPurc.TalkPurcStatusEnum.TALKING);
        talkPurcRepository.save(talkPurc);

        ShmPost shmPost = talkPurc.getShmPost();
        if (shmPost != null && shmPost.getPostSellStatus() == ShmPost.PostSellSatus.TEND_TO_SELL) {
            shmPost.setPostSellStatus(ShmPost.PostSellSatus.IN_CONVERSATION);
            postService.savePost(shmPost);
        }

        String partnerName = talkPurc.getShmUser().getNickName();

        ShtTalkPurcMsg systemOrderMsg = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
        systemOrderMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10005", new Object[]{partnerName}, null));
        ShtTalkPurcMsg systemOrderSaved = talkPurcMsgRepository.save(systemOrderMsg);
    //    fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, systemOrderSaved));

        // send message reject msg to partner
        ShtTalkPurcMsg rejectOrderMsg = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_REJECTED);
        rejectOrderMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10016", null, null));
        ShtTalkPurcMsg rejectOrderMsgSaved = talkPurcMsgRepository.save(rejectOrderMsg);
    //    fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, rejectOrderMsgSaved));

        // inactive system message for partner
        talkPurcMsgRepository.inActiveSystemMsgForUser(talkPurc.getTalkPurcId(), ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);

        // interrupt job
        interruptJob(talkPurc.getTalkPurcId());
    }

    @Override
    public ShtTalkPurc getTalkPurc(long id) {
        return talkPurcRepository.getOne(id);
    }

    @Override
    public void sendOrderRequest(ShtTalkPurc talkPurc, ShmUser currentUser, boolean mutedBlockFlag) throws Exception {

        validateTalkPurcBeforeTendToSell(talkPurc);
        userService.checkUserStatus(currentUser);
        userService.checkUserStatus(talkPurc.getShmUser());

        ShmPost shmPost = talkPurc.getShmPost();
        validatePostStatus(shmPost);

        if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.TEND_TO_SELL) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100016", null, null));
        }

        talkPurc.setTalkPurcTendToSellTime(LocalDateTime.now());
        talkPurc.setTalkPurcStatus(ShtTalkPurc.TalkPurcStatusEnum.TEND_TO_SELL);
        talkPurcRepository.save(talkPurc);

        shmPost.setPostSellStatus(ShmPost.PostSellSatus.TEND_TO_SELL);
        postService.savePost(shmPost);

        String partnerEmail = talkPurc.getShmUser().getEmail();
        String partnerName = talkPurc.getShmUser().getNickName();
        Long partnerId =  talkPurc.getShmUser().getId();
        String ownerName = shmPost.getShmUser().getNickName();
        String postName = shmPost.getPostName();

        ShtTalkPurcMsg sendOrderForOwnerMsg = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_SENT_FOR_OWNER);
        sendOrderForOwnerMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10007", null, null));
        ShtTalkPurcMsg sendOrderMsgForOwnerSaved = talkPurcMsgRepository.save(sendOrderForOwnerMsg);
//        if(!mutedBlockFlag)
//        fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, sendOrderMsgForOwnerSaved));

        ShtTalkPurcMsg sendOrderForPartnerMsg = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.ORDER_SENT_FOR_PARTNER);
        sendOrderForPartnerMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10009", null, null));
        ShtTalkPurcMsg sendOrderMsgForPartnerSaved = talkPurcMsgRepository.save(sendOrderForPartnerMsg);
//        if(!mutedBlockFlag)
//        fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, sendOrderMsgForPartnerSaved));

        ShtTalkPurcMsg systemOrderMsg = createTalkPurcMsg(talkPurc, currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
        systemOrderMsg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10001", new Object[]{ownerName}, null));
        ShtTalkPurcMsg systemOrderMsgSaved = talkPurcMsgRepository.save(systemOrderMsg);
//        if(!mutedBlockFlag)
//        fireBaseService.sendNewMsgInToFireBaseDB(buildTalkPurcInfo(currentUser, systemOrderMsgSaved));

        // send mail to partner
        boolean checkTurnOnEmail = userSettingService.checkReceivingMailInTransaction(partnerId);
        if(checkTurnOnEmail)
            mailService.sendEmailSendOrderRequest(partnerEmail, postName, partnerName, ownerName, partnerId);

        //Create job
        createJobScheduler(talkPurc.getTalkPurcId(), LocalDateTime.now());
    }

    public static TalkPurcCreateBody buildTalkPurcInfo(ShmUser creator, ShtTalkPurcMsg talkPurcMsg){
        TalkPurcCreateBody talkPurcCreateBody = new TalkPurcCreateBody();
        talkPurcCreateBody.setTalkPurcId(talkPurcMsg.getShtTalkPurc().getTalkPurcId());
        talkPurcCreateBody.setTalkPurcMsgCreatorNickName(creator.getNickName());
        talkPurcCreateBody.setMsgId(talkPurcMsg.getTalkPurcMsgId());
        talkPurcCreateBody.setMsgType(talkPurcMsg.getTalkPurcMsgType());
        talkPurcCreateBody.setCreatedAt(talkPurcMsg.getCreatedAt());
        talkPurcCreateBody.setMsgContent(talkPurcMsg.getTalkPurcMsgCont());
        return talkPurcCreateBody;
    }

    private void createJobScheduler(Long talkPurcId, LocalDateTime now) throws ParseException, SchedulerException {
        if (jobFlag) {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            // define the job and tie it
            JobDetail job = newJob(JobHandleTalkPurcAfter48h.class).withIdentity(JobEnum.JOB_TALK_PURC_ID.getValue() + talkPurcId, JobEnum.JOB_TALK_PURC_GROUP.getValue()).build();

            // computer a time that is on the next round minute
            LocalDateTime runTime = now.plusHours(waitTime);

            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_TALK_PURC_TRIGGER.getValue() + talkPurcId, JobEnum.JOB_TALK_PURC_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(runTime)))
                    .build();

            // Tell quartz to schedule the job using trigger
            try {
                scheduler.scheduleJob(job, trigger);

                // Start up the scheduler (nothing can actually run until the scheduler has been started)
                scheduler.start();
            } catch (SchedulerException e) {
                LOGGER.error("ERROR: " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public void validatePostStatus(ShmPost shmPost) {
        if (shmPost == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100024", null, null));
        }
        if (shmPost.getPostSellStatus() == ShmPost.PostSellSatus.DELETED) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100027", null, null));
        }
        if (shmPost.getPostCtrlStatus() == ShmPost.PostCtrlStatus.SUSPENDED) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100028", null, null));
        }
    }

    private ShtTalkPurcMsg createTalkPurcMsg(ShtTalkPurc talkPurc, ShmUser currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum msgTypeEnum) {
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        msg.setShmUserCreator(currentUser);
        msg.setTalkPurcMsgType(msgTypeEnum);
        msg.setTalkPurcMsgCont(msgTypeEnum.name());
        return msg;
    }

    @Override
    public ShmUser findPartnerInTalkPurc(Long talkPurcId) {
        ShmUser shmUser = talkPurcRepository.findPartnerInTalkPurc(talkPurcId);
        return shmUser;
    }

    @Override
    public Long countNewMsgNumberByPostId(Long postId, Long userId) {
        return talkPurcRepository.countNewMsgNumberByPostId(postId,userId);
    }

    @Override
    public Long countTalkPurcByPostId(long postId) {
        return talkPurcRepository.countTalkPurcByPostId(postId);
    }

    @Override
    public Long countTalkPurcTendToSellByPostId(long postId) {
        return talkPurcRepository.countTalkPurcTendToSellByPostId(postId);
    }

    @Override
    public ShtTalkPurc save(ShtTalkPurc talkPurc) {
        return talkPurcRepository.save(talkPurc);
    }

    @Override
    public List<ShtTalkPurc> getTalkPurcsExpiredTendToSell(LocalDateTime tendToSellTime) {
        return talkPurcRepository.getTalkPurcsExpiredTendToSell(tendToSellTime);
    }

    @Override
    public void validateTalkPurc(ShtTalkPurc talkPurc) throws Exception {
        if (talkPurc == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100018", null, null));
        }
        if (talkPurc.getTalkPurcStatus() == ShtTalkPurc.TalkPurcStatusEnum.CLOSED) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100020", null, null));
        }

        userService.checkUserStatus(talkPurc.getShmUser());
        postService.checkPostStatus(talkPurc.getShmPost());
    }

    @Override
    public void validateTalkPurcBeforeTendToSell(ShtTalkPurc talkPurc) throws Exception {
        validateTalkPurc(talkPurc);
        if (talkPurc.getTalkPurcStatus() == ShtTalkPurc.TalkPurcStatusEnum.TEND_TO_SELL) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100019", null, null));
        }
    }

    @Override
    public LocalDateTime getUserBlockTime(Long fromUser, Long toUser) {
        LocalDateTime userBlockTime = userBlockRepository.getUserBlockTime(fromUser, toUser);
        return userBlockTime;
    }

    @Override
    public List<Long> findAllTalkPurcIdByPostId(Long postId) {
        return talkPurcRepository.findListTalkPurcIdByPostId(postId);
    }

    @Override
    public int coutNewMsgInTalkpurc(Long ownerPostId, Long talkPurcId) {
        return talkPurcMsgRepository.countNewMsgInTalkSentForOwnerPost(ownerPostId, talkPurcId);
    }

    @Override
    public boolean checkUserBlocked(Long senderUserId, Long partnerId) {
        Long counter= userBlockRepository.countUserBlock(senderUserId, partnerId);
        if (counter != null && counter.longValue() >0)
            return true;
        return false;
    }

    @Override
    public boolean checkUserMutedPartner(Long senderUserId, Long partnerId) {
        Long counter= userBlockRepository.countUserMute(senderUserId, partnerId);
        if (counter != null && counter.longValue() >0)
            return true;
        return false;
    }

    @Override
    public boolean checkUserMutedOrBlocked(Long senderUserId, Long recepientId) {
        Long counter= userBlockRepository.countUserMutedOrBlockd(senderUserId, recepientId);
        if (counter != null && counter.longValue() >0)
            return true;
        return false;
    }
}
