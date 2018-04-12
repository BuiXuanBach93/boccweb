package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.UserNGRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.enums.PatrolActionEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.jobs.JobUpdateUserPatrolAfter24h;
import jp.bo.bocc.repository.MemoUserRepository;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ResourceNotFoundException;
import org.apache.commons.lang3.StringUtils;
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
 * @author NguyenThuong on 4/5/2017.
 */
@Service
public class MemoUserServiceImpl implements MemoUserService {

    private final static Logger LOGGER = Logger.getLogger(MemoUserServiceImpl.class.getName());

    @Autowired
    MemoUserRepository memoUserRepository;

    @Autowired
    UserService userService;

    @Autowired
    AdminService adminService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    AdminLogService adminLogService;

    @Autowired
    TalkPurcService talkPurcService;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    MemoPostService memoPostService;

//    @Autowired
//    FireBaseService fireBaseService;

    @Value("${job.patrol.wait.hour}")
    private int patrolWaitTime;

    @Value("${job.patrol.flag}")
    private boolean flag;

    @Override
    public PatrolHistoryDTO processUserNg(ShmAdmin admin, UserNGRequest request) throws SchedulerException {
        ShmUser user = userService.getUserById(request.getUserId());
        if (user == null)
            throw new ResourceNotFoundException(messageSource.getMessage("SH_E100053", null, null));

        ShtMemoUser memoUser = new ShtMemoUser();
        if (StringUtils.isNotEmpty(request.getContent())) {
            memoUser.setAdmin(admin);
            memoUser.setContent(request.getContent());
            memoUser.setUser(user);
            memoUser = memoUserRepository.save(memoUser);
        }

        String title = ConverterUtils.convertReasonCodeToString(request);
        String reason = memoPostService.buildMessageSuspendForUser(request);
        String ctrlStatus = request.getCtrlStatus();
        String difReason = request.getDifReason();
        if (ShmUser.CtrlStatus.SUSPENDED.toString().equals(ctrlStatus)) {
            ShtAdminLog adminLog = new ShtAdminLog();
            user.setCtrlStatus(ShmUser.CtrlStatus.SUSPENDED);
            user.setPtrlStatus(ShmUser.PtrlStatus.CENSORING);

            // send msg to talk if owner be NG report
            try{
                List<ShtTalkPurc> talkPurcs = talkPurcService.findTalksByOwnerIdOpen(user.getId());
                if(talkPurcs != null){
                    for (ShtTalkPurc talkPurc: talkPurcs) {
                        ShtTalkPurcMsg msg = createNGMsgForPartner(talkPurc, reason);
                        ShtTalkPurcMsg msgSaved = talkPurcMsgService.saveMsg(msg);
                    //    fireBaseService.sendNewMsgInToFireBaseDB(TalkPurcServiceImpl.buildTalkPurcInfo(user, msgSaved));
                    }
                }
            } catch (Exception ex){
                LOGGER.error(ex.getMessage());
            }

            // send msg to talk if partner be NG report
            try{
                List<ShtTalkPurc> talkPurcs = talkPurcService.findTalksByPartnerIdOpen(user.getId());
                if(talkPurcs != null){
                    for (ShtTalkPurc talkPurc: talkPurcs) {
                        ShtTalkPurcMsg msg = createNGMsgForOwner(talkPurc, reason);
                        talkPurcMsgService.saveMsg(msg);
                    }
                }
            } catch (Exception ex){
                LOGGER.error(ex.getMessage());
            }

            user.setIsInPatrol(false);
            user.setTimePatrol(LocalDateTime.now());
            userService.save(user);

            if (org.apache.commons.lang3.StringUtils.isNotEmpty(difReason)) adminLog.setAdminLogCont(difReason);
            adminLog.setAdminLogTitle(title);
            adminLog.setShmUser(user);
            adminLog.setAdminLogType(PatrolActionEnum.PATROL_USER_SUSPENDED);
            adminLog.setShmAdmin(admin);
            adminLogService.save(adminLog);
        }else if (ShmUser.CtrlStatus.PENDING.toString().equals(ctrlStatus)) {
            user.setPtrlStatus(ShmUser.PtrlStatus.UNCENSORED);
            user.setCtrlStatus(ShmUser.CtrlStatus.PENDING);
            user.setIsInPatrol(false);
            user.setTimePatrol(LocalDateTime.now());
            userService.save(user);

            ShtAdminLog adminLog = new ShtAdminLog();
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(difReason)) adminLog.setAdminLogCont(difReason);
            adminLog.setAdminLogTitle(title);
            adminLog.setShmUser(user);
            adminLog.setAdminLogType(PatrolActionEnum.PATROL_USER_RESERVED);
            adminLog.setShmAdmin(admin);
            adminLogService.save(adminLog);
        }
        return convertMemoUserToPatrolHisDTO(memoUser);
    }

    private ShtTalkPurcMsg createNGMsgForPartner(ShtTalkPurc talkPurc, String reason){
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        msg.setShmUserCreator(talkPurc.getShmPost().getShmUser());
        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_NG_MSG_FOR_PARTNER);
        String ownerName = "";
        if(talkPurc.getShmPost() != null && talkPurc.getShmPost().getShmUser() != null){
            ownerName = talkPurc.getShmPost().getShmUser().getNickName();
        }
        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10013", null,null).replace("<ownerName>",ownerName).replace("<reason>",reason));
        return msg;
    }

    private ShtTalkPurcMsg createNGMsgForOwner(ShtTalkPurc talkPurc, String reason){
        ShtTalkPurcMsg msg = new ShtTalkPurcMsg();
        msg.setShtTalkPurc(talkPurc);
        msg.setShmUserCreator(talkPurc.getShmUser());
        msg.setTalkPurcMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
        String partnerName = "";
        if(talkPurc.getShmUser() != null){
            partnerName = talkPurc.getShmUser().getNickName();
        }
        msg.setTalkPurcMsgCont(messageSource.getMessage("TALK_MSG_10022", null,null).replace("<nickName>",partnerName).replace("<reason>",reason));
        return msg;
    }


    @Override
    @Transactional
    public void updateUserPatrol() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime overTime = now.minusHours(patrolWaitTime);

        List<ShmUser> users = userService.getUsersOverTimeToPatrol(overTime, true);
        for (ShmUser user : users) {
            user.setTimePatrol(null);
            user.setIsInPatrol(false);
            userService.save(user);
        }
    }

    @Override
    public void createJobSchedule(Long userId, LocalDateTime now) throws ParseException, SchedulerException {
        if (flag) {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail job = newJob(JobUpdateUserPatrolAfter24h.class).withIdentity(JobEnum.JOB_USER_PATROL_ID.getValue() + userId, JobEnum.JOB_USER_PATROL_GROUP.getValue()).build();
            LocalDateTime runTime = now.plusHours(patrolWaitTime);
            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_USER_PATROL_TRIGGER.getValue() + userId, JobEnum.JOB_USER_PATROL_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(runTime))).build();

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
    public void interruptJob(Long userId) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        try {
            scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_USER_PATROL_ID.getValue() + userId, JobEnum.JOB_USER_PATROL_GROUP.getValue()));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public PatrolHistoryDTO saveMemoUser(Long userId, ShmAdmin admin, String content) {
        ShmUser user = userService.getUserById(userId);
        if (user == null)
            throw new ResourceNotFoundException("");

        ShtMemoUser memoUser = new ShtMemoUser();
        memoUser.setUser(user);
        memoUser.setAdmin(admin);
        memoUser.setContent(content);
        memoUser = memoUserRepository.save(memoUser);
        return convertMemoUserToPatrolHisDTO(memoUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatrolHistoryDTO> getAllHisProcessUserByUserId(Long userId) {

        ShmUser user = userService.getUserById(userId);
        if (user == null) {
            LOGGER.error("ERROR: user does not exist. UserId: " + userId);
            throw new ResourceNotFoundException("");
        }

        List<ShtAdminLog> userHis = adminLogService.getAllAdminLogForUser(userId);
        List<ShtMemoUser> userMemoList = memoUserRepository.findByUserOrderByUpdatedAt(user);
        List<PatrolHistoryDTO> patrolAndMemoList = adminLogService.convertAdminLogToPatrolHisDTO(userHis);
        List<PatrolHistoryDTO> memoDtoList = convertMemoUserToPatrolHisDTO(userMemoList);
        patrolAndMemoList.addAll(memoDtoList);
        patrolAndMemoList.sort((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()));

        return patrolAndMemoList;
    }

    private PatrolHistoryDTO convertMemoUserToPatrolHisDTO(ShtMemoUser memoUser) {
        ShmAdmin admin = memoUser.getAdmin();
        PatrolHistoryDTO userDto = new PatrolHistoryDTO();
        if(admin != null) {
            userDto.setCreatedAt(memoUser.getCreatedAt());
            userDto.setContent(memoUser.getContent());
            userDto.setShmAdminId(admin.getAdminId());
            userDto.setShmAdminName(admin.getAdminName());
        }else {
            LOGGER.error("ERROR: admin does not exist in ShtMemoPost. ShtMemoPost ID: " + memoUser.getId());
        }

        return userDto;
    }

    private List<PatrolHistoryDTO> convertMemoUserToPatrolHisDTO(List<ShtMemoUser> memoUsers) {
        List<PatrolHistoryDTO> hisList = new ArrayList<>();
        PatrolHistoryDTO userDto;
        for (ShtMemoUser memoUser : memoUsers) {
            ShmAdmin admin = memoUser.getAdmin();
            if(admin != null) {
            userDto = new PatrolHistoryDTO();
            userDto.setCreatedAt(memoUser.getCreatedAt());
            userDto.setContent(memoUser.getContent());
            userDto.setShmAdminId(admin.getAdminId());
            userDto.setShmAdminName(admin.getAdminName());
            hisList.add(userDto);
            }else {
                LOGGER.error("ERROR: admin does not exist in ShtMemoPost. ShtMemoPost ID: " + memoUser.getId());
            }
        }

        return hisList;
    }

    @Override
    @Transactional(readOnly = true)
    public ShtMemoUser getNewestMemoByUserId(long userId) {
        return memoUserRepository.findNewestCommentByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatrolHistoryDTO> getAllMemoUserByUserId(Long userId) {

        ShmUser user = userService.getUserById(userId);
        if (user == null)
            throw new ResourceNotFoundException("");

        List<ShtMemoUser> memoUsers = memoUserRepository.findByUserOrderByUpdatedAt(user);
        return convertMemoUserToPatrolHisDTO(memoUsers);
    }
}
