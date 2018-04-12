package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.UserRevBodyRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.DateUtils;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.repository.UserRevRepository;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ServiceException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by DonBach on 4/4/2017.
 */
@Service
public class UserRevServiceImpl implements UserRevService {

    @Autowired
    UserRevRepository repo;

    @Autowired
    MessageSource messageSource;

    @Autowired
    TalkPurcService talkPurcService;

    @Autowired
    UserService userService;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    MailService mailService;
    @Autowired
    private SNSMobilePushService snsMobilePushService;

    @Override
    public ShtUserRev getUserRev(long id) {
        ShtUserRev userRev = repo.getOne(id);
        return  userRev;
    }

    @Override
    public ShtUserRev getUserRevById(long id) {
        return repo.getUserRevById(id);
    }

    @Override
    public ShtUserRev saveUserRev(ShtUserRev shtUserRev) {
        return repo.save(shtUserRev);
    }

    @Override
    public Long countReviewByUserId(Long userId, ShtUserRev.UserReviewRate userReviewRate) {
        return repo.countReviewByUserId(userId, userReviewRate);
    }

    @Override
    public List<ShtUserRev> getReviewByUserId(Long userId, ShtUserRev.UserReviewRate userReviewType) {
        return repo.getReviewByUserId(userId,userReviewType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShtUserRev> getReviewToUserId(Long userId,Pageable pageable, ShtUserRev.UserReviewRate userReviewType) {
        Page<ShtUserRev> reviewToUser = repo.getReviewToUser(userId, pageable, userReviewType);

        //HOTFIX: need to update after client udpate
        reviewToUser.getContent().forEach(item -> {
            LocalDateTime createdAt = item.getCreatedAt();
            item.setCreatedAt(createdAt.minusHours(9));
            item.setCreatedAtTxt(ConverterUtils.formatLocalDateTimeToStr(createdAt));
        });
        return reviewToUser;
    }

    @Override
    public Long getReviewByTalkPurcId(Long talkPurcId, Long fromUserId) {
        return repo.getReviewByTalkPurcId(talkPurcId, fromUserId);
    }

    @Override
    public void validateUserRevRequest(UserRevBodyRequest userRevBodyRequest) {
        if(userRevBodyRequest.getTalkPurcId() == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100029",null,null));
        }
        if(userRevBodyRequest.getUserRevRate() == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100030",null,null));
        }
        if(userRevBodyRequest.getUserRevCont() == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100031",null,null));
        }
        if(userRevBodyRequest.getUserRevCont().length() > 300){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100032",null,null));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalReviewOtherPeople(long userId) {
        return repo.countReviewByFromUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalReviewMe(long userId) {
        return repo.countReviewByToUser(userId);
    }

    @Override
    public void interruptReviewRemindJob(Long talkPurcId, Boolean isReviewOnwer) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            if(isReviewOnwer){
                scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_REVIEW_FOR_OWNER_POST.getValue() + talkPurcId, JobEnum.JOB_REVIEW_GROUP.getValue()));
            }else{
                scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_REVIEW_FOR_PARTNER.getValue() + talkPurcId, JobEnum.JOB_REVIEW_GROUP.getValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShtUserRev createReview(UserRevBodyRequest userRevBodyRequest, ShmUser currentUser) {
        long talkPurcId = userRevBodyRequest.getTalkPurcId();
        ShtTalkPurc talkPurc = talkPurcService.getTalkPurcById(talkPurcId);
        if(talkPurc == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100029",null,null));
        }
        ShmUser partner = talkPurc.getShmUser();
        if(currentUser.getCtrlStatus() == ShmUser.CtrlStatus.SUSPENDED){
            throw new ServiceException(HttpStatus.BAD_REQUEST, messageSource.getMessage("SH_E100023",null,null ));
        }

        ShmPost shmPost = talkPurc.getShmPost();
        talkPurcService.validatePostStatus(shmPost);

        ShtUserRev.UserReviewType reviewType =  ShtUserRev.UserReviewType.OWNER_TO_PARTNER;
        ShmUser fromUser = null;
        ShmUser toUser = null;
        ShtTalkPurcMsg msgAfterReview = null;

        if(partner != null && currentUser != null && shmPost != null){
            if(partner.getId().intValue() == currentUser.getId().intValue()){
                reviewType =  ShtUserRev.UserReviewType.PARTNER_TO_OWNER;
                fromUser = currentUser;
                toUser = shmPost.getShmUser();
                msgAfterReview = createTalkMsgAfterReview(talkPurc,currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.REVIEW_FOR_OWNER);
            }else{
                reviewType =  ShtUserRev.UserReviewType.OWNER_TO_PARTNER;
                fromUser = currentUser;
                toUser = partner;
                msgAfterReview = createTalkMsgAfterReview(talkPurc,currentUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum.REVIEW_FOR_PARTNER);
            }
            if(msgAfterReview != null){
                msgAfterReview.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10010",null,null ));
                talkPurcMsgService.saveMsg(msgAfterReview);
            }
        }

        ShtUserRev userRev = new ShtUserRev();
        userRev.setShtTalkPurc(talkPurc);
        userRev.setUserRevRate(userRevBodyRequest.getUserRevRate());
        userRev.setUserRevCont(userRevBodyRequest.getUserRevCont());
        userRev.setUserRevType(reviewType);
        userRev.setFromShmUser(fromUser);
        userRev.setToShmUser(toUser);

        ShtUserRev shtUserRevSaved = saveUserRev(userRev);
        return shtUserRevSaved;
    }

    private ShtTalkPurcMsg createTalkMsgAfterReview(ShtTalkPurc talkPurc, ShmUser fromUser, ShtTalkPurcMsg.TalkPurcMsgTypeEnum msgTypeEnum) {
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        msg.setShmUserCreator(fromUser);
        msg.setTalkPurcMsgType(msgTypeEnum);
        return msg;
    }

    @Override
    public Long getTotalReviewForUserByUserId(Long userId) {
        return repo.getTotalReviewByUserId(userId);
    }
}
