package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.BaseNGRequest;
import jp.bo.bocc.controller.web.request.PostMemoRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.enums.NgEnum;
import jp.bo.bocc.enums.PatrolActionEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.jobs.JobUpdatePostPatrolAfter24h;
import jp.bo.bocc.repository.MemoPostRepository;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
 * Created by NguyenThuong on 4/20/2017.
 */
@Service
public class MemoPostServiceImpl implements MemoPostService {

    private final static Logger LOGGER = Logger.getLogger(MemoPostServiceImpl.class.getName());

    @Autowired
    MemoPostRepository memoPostRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PostService postService;

    @Autowired
    AdminLogService adminLogService;

    @Autowired
    TalkPurcService talkPurcService;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

//    @Autowired
//    FireBaseService fireBaseService;

    @Value("${job.patrol.wait.hour}")
    private int patrolWaitTime;

    @Value("${job.patrol.flag}")
    private boolean flag;

    @Override
    @Transactional(readOnly = true)
    public ShtMemoPost getMemoNewestByPostId(long postId) {
        return memoPostRepository.findNewestCommentByPostId(postId);
    }

    @Override
    @Transactional
    public void updatePostPatrols(boolean isPatrol) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime overTime = now.minusHours(patrolWaitTime);
        List<ShmPost> posts = postService.getPostsOverTimeToPatrol(overTime, true);

        for (ShmPost post : posts) {
            post.setIsInPatrol(false);
            postService.savePost(post);
        }
    }

    @Override
    public void createJobSchedule(Long postId, LocalDateTime now) throws SchedulerException, ParseException {
        if (flag) {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail job = newJob(JobUpdatePostPatrolAfter24h.class).withIdentity(JobEnum.JOB_POST_PATROL_ID.getValue() + postId, JobEnum.JOB_POST_PATROL_GROUP.getValue()).build();
            LocalDateTime runTime = now.plusHours(patrolWaitTime);
            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_POST_PATROL_TRIGGER.getValue() + postId, JobEnum.JOB_POST_PATROL_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(runTime))).build();

            try {
                if (!scheduler.checkExists(job.getKey())) {
                    scheduler.scheduleJob(job, trigger);
                    scheduler.start();
                }
            } catch (SchedulerException ex) {
                throw ex;
            }
        }
    }

    @Override
    public void interruptJob(Long postId) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_POST_PATROL_ID.getValue() + postId, JobEnum.JOB_POST_PATROL_GROUP.getValue()));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ShtMemoPost saveMemoPost(ShmAdmin admin, PostMemoRequest postMemoRequest) {
        ShmPost post = postService.getPost(postMemoRequest.getPostId());
        if (post == null)
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));

        ShtMemoPost shtMemoPost = new ShtMemoPost();
        shtMemoPost.setAdmin(admin);
        shtMemoPost.setContent(postMemoRequest.getContent());
        shtMemoPost.setPost(post);
        shtMemoPost = memoPostRepository.save(shtMemoPost);

        return shtMemoPost;
    }

    @Override
    public ShtMemoPost processPost(ShmAdmin admin, PostMemoRequest postMemoRequest) throws SchedulerException {
        ShmPost post = postService.getPost(postMemoRequest.getPostId());
        if (post == null)
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));

        ShtMemoPost memoPost = new ShtMemoPost();
        memoPost.setPost(post);
        if (!postMemoRequest.getContent().isEmpty()) {
            memoPost.setAdmin(admin);
            memoPost.setContent(postMemoRequest.getContent());
            memoPost.setShmAdminName(admin.getAdminName());
            memoPost = memoPostRepository.save(memoPost);
        }

        String title = ConverterUtils.convertReasonCodeToString(postMemoRequest);
        String reason = buildMessageSuspendForUser(postMemoRequest);
        Short patrolType = postMemoRequest.getPatrolType();
        if (!title.isEmpty()) {
            ShtAdminLog adminLog = new ShtAdminLog();
            post.setPostCtrlStatus(ShmPost.PostCtrlStatus.RESERVED);
            post.setPostPtrlStatus(ShmPost.PostPtrlStatusEnum.UNCENSORED);
            adminLog.setAdminLogType(PatrolActionEnum.PATROL_POST_RESERVED);
            if (patrolType == 2) {
                post.setPostCtrlStatus(ShmPost.PostCtrlStatus.SUSPENDED);
                post.setPostPtrlStatus(ShmPost.PostPtrlStatusEnum.CENSORING);
                adminLog.setAdminLogType(PatrolActionEnum.PATROL_POST_SUSPENDED);

                // send msg to talk if post be NG report
                try{
                    List<ShtTalkPurc> talkPurcs = talkPurcService.findTalkListByPostId(post.getPostId());
                    if(talkPurcs != null){
                        for (ShtTalkPurc talkPurc: talkPurcs) {
                            ShtTalkPurcMsg msg = createNGMsgForPartner(talkPurc,reason);
                            ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                          //  fireBaseService.sendNewMsgInToFireBaseDB(TalkPurcServiceImpl.buildTalkPurcInfo(post.getShmUser(), msgSaved));
                        }
                    }
                }catch (Exception ex){
                    LOGGER.error(ex.getMessage());
                }
            }

            post.setIsInPatrol(false);
            post.setTimePatrol(LocalDateTime.now());
            postService.savePost(post);

            if (postMemoRequest.getDifReason() != null)
                adminLog.setAdminLogCont(postMemoRequest.getDifReason());
            adminLog.setAdminLogTitle(title);
            adminLog.setShmPost(post);
            adminLog.setShmAdmin(admin);
            adminLogService.save(adminLog);
            interruptJob(post.getPostId());

        /* in pending case not require reason */
        }else if (title.isEmpty() && patrolType != null && patrolType == 1) {
            post.setPostCtrlStatus(ShmPost.PostCtrlStatus.RESERVED);
            post.setPostPtrlStatus(ShmPost.PostPtrlStatusEnum.UNCENSORED);
            post.setIsInPatrol(false);
            post.setTimePatrol(LocalDateTime.now());
            postService.savePost(post);

            ShtAdminLog adminLog = new ShtAdminLog();
            if (postMemoRequest.getDifReason() != null)
                adminLog.setAdminLogCont(postMemoRequest.getDifReason());
            adminLog.setAdminLogTitle(title);
            adminLog.setShmPost(post);
            adminLog.setAdminLogType(PatrolActionEnum.PATROL_POST_RESERVED);
            adminLog.setShmAdmin(admin);
            adminLogService.save(adminLog);
            interruptJob(post.getPostId());
        }

        return memoPost;
    }

    private ShtTalkPurcMsg createNGMsgForPartner(ShtTalkPurc talkPurc, String reason){
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        msg.setShmUserCreator(talkPurc.getShmPost().getShmUser());
        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_NG_MSG_FOR_PARTNER);
        String postName = "";
        String ownerName = "";
        if(talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null){
            postName = talkPurc.getShmPost().getPostName();
            ownerName = talkPurc.getShmPost().getShmUser().getNickName();
        }
        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10011", null,null).replace("<postName>",postName).replace("<ownerName>",ownerName).replace("<reason>",reason));
        return msg;
    }

    @Override
    public String buildMessageSuspendForUser(BaseNGRequest baseNGRequest) {
        String reason = "";
        if (baseNGRequest.getNotSuitable() != null)
            reason = "・" + NgEnum.NOT_SUITABLE.value() +  "\n" ;
        if (baseNGRequest.getSensitiveInf() != null)
            reason += "・" + NgEnum.SENSITIVE_INF.value() + "\n" ;
        if (baseNGRequest.getSlander() != null)
            reason += "・" + NgEnum.SLANDER.value()  + "\n" ;
        if (baseNGRequest.getCheat() != null)
            reason += "・" + NgEnum.CHEAT.value()  + "\n" ;
        if (baseNGRequest.getOther() != null)
            reason += "・その他、" + baseNGRequest.getDifReason() + "\n";
        return reason;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatrolHistoryDTO> getPatrolHistoryByPostId(Long postId) {
        ShmPost post = postService.getPost(postId);
        if (post == null)
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));

        List<ShtAdminLog> postHis = adminLogService.getAllAdminLogForPost(postId);
        List<ShtMemoPost> memoPosts = memoPostRepository.findByPostOrderByUpdatedAt(post);
        List<PatrolHistoryDTO> adminLogHist = adminLogService.convertAdminLogToPatrolHisDTO(postHis);
        List<PatrolHistoryDTO> memoHist = convertMemoPostToPatrolHisDTO(memoPosts);
        adminLogHist.addAll(memoHist);
        adminLogHist.sort((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()));

        return adminLogHist;
    }

    private List<PatrolHistoryDTO> convertMemoPostToPatrolHisDTO(List<ShtMemoPost> memoPosts) {
        List<PatrolHistoryDTO> hisList = new ArrayList<>();
        PatrolHistoryDTO dto;
        for (ShtMemoPost memo : memoPosts) {
            ShmAdmin admin = memo.getAdmin();
            if (admin != null ) {
                dto = new PatrolHistoryDTO();
                dto.setContent(memo.getContent());
                dto.setShmAdminId(admin.getAdminId());
                dto.setShmAdminName(admin.getAdminName());
                dto.setCreatedAt(memo.getCreatedAt());
                hisList.add(dto);
            }else {
                LOGGER.error("ERROR: admin does not exist in ShtMemoPost. ShtMemoPost ID: " + memo.getId());
            }
        }

        return hisList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatrolHistoryDTO> getMemoPostListByPostId(Long postId) {
        ShmPost post = postService.getPost(postId);
        if (post == null)
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));

        return convertMemoPostToPatrolHisDTO(memoPostRepository.findByPostOrderByUpdatedAt(post));
    }
}
