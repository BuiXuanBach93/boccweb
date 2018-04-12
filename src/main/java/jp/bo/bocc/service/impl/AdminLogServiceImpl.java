package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtAdminLog;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;
import jp.bo.bocc.enums.NgEnum;
import jp.bo.bocc.enums.PatrolActionEnum;
import jp.bo.bocc.repository.AdminLogRepository;
import jp.bo.bocc.service.AdminLogService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NguyenThuong on 4/14/2017.
 */
@Service
public class AdminLogServiceImpl implements AdminLogService {

    private final static Logger LOGGER = Logger.getLogger(AdminLogServiceImpl.class.getName());

    @Autowired
    AdminLogRepository adminLogRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public long countTotalProcessPostTimes(long postId) {
        return adminLogRepository.countTotalProcessPostByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalProcessUserTimes(long userId) {
        return adminLogRepository.countTotalProcessUserByUserId(userId);
    }

    @Override
    public boolean pendingPostInPast(long postId) {
        List<Long> timePatrols = adminLogRepository.countPostEverPending(postId);
        return timePatrols.size() > 0 ? true : false;
    }

    @Override
    public List<LocalDateTime> suspendPostInPast(long postId) {
        return adminLogRepository.getLastDateSuspendPost(postId);
    }

    @Override
    public List<PatrolHistoryDTO> convertAdminLogToPatrolHisDTO(List<ShtAdminLog> adminLogs) {
        List<PatrolHistoryDTO> histDTO = new ArrayList<>();
        PatrolHistoryDTO dto;
        if (CollectionUtils.isNotEmpty(adminLogs)) {
            for (ShtAdminLog adminLog : adminLogs) {
                ShmAdmin shmAdmin = adminLog.getShmAdmin();
                if (adminLog.getShmAdmin() != null) {
                    dto = new PatrolHistoryDTO();
                    dto.setCreatedAt(adminLog.getCreatedAt());
                    dto.setShmAdminId(shmAdmin.getAdminId());
                    dto.setShmAdminName(shmAdmin.getAdminName());
                    String adminLogTitle = adminLog.getAdminLogTitle();
                    PatrolActionEnum adminLogType = adminLog.getAdminLogType();
                    if (adminLogType == PatrolActionEnum.PATROL_USER_OK || adminLogType == PatrolActionEnum.PATROL_POST_OK) {
                        dto.setContent("【OK】対応完了しました。");
                    } else {
                        if (StringUtils.isNotEmpty(adminLogTitle)) {
                            String[] reasons = adminLogTitle.split(",");
                            String content = "";
                            if (adminLogType == PatrolActionEnum.PATROL_POST_RESERVED || adminLogType == PatrolActionEnum.PATROL_USER_RESERVED)
                                content = "【保留】違反理由：";
                            else
                                content = "【NG】違反理由：";
                            for (String rs : reasons) {
                                content = content + "、 " + NgEnum.values()[Integer.parseInt(rs.trim())].value();
                            }
                            if (adminLog.getAdminLogCont() != null)
                                content = content + "（" + adminLog.getAdminLogCont() + "）";
                            dto.setContent(content.replace("：、", "："));
                        }
                    }
                    histDTO.add(dto);
                } else {
                    LOGGER.error("ERROR: Dirty data, admin doest not exist. Admin log Id: " + adminLog.getAdminLogId());
                }
            }
        }

        return histDTO;
    }

    @Override
    public ShtAdminLog save(ShtAdminLog adminLog) {
        return adminLogRepository.save(adminLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShtAdminLog> getAllAdminLogForPost(Long postId) {
        return adminLogRepository.findByShmPost_PostIdOrderByCreatedAtDesc(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShtAdminLog> getAllAdminLogForUser(Long userId) {
        return adminLogRepository.findByShmUser_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ShtAdminLog getLastAdminProcessPost(Long postId) {
        List<ShtAdminLog> adminLogs = adminLogRepository.getLastAdminProcessPost(postId);
        return adminLogs.size() > 0 ? adminLogs.get(0) : null;
    }

    @Override
    public ShtAdminLog saveAdminLogUser(ShmUser user, ShmAdmin shmAdmin) {
        ShtAdminLog adminLog = buildAdminLog(shmAdmin, user, ShmUser.CtrlStatus.OK.toString());
        adminLog.setAdminLogType(PatrolActionEnum.PATROL_USER_OK);
        String okCont = PatrolActionEnum.PATROL_USER_OK.toString();
        adminLog.setAdminLogTitle(okCont);
        adminLog.setAdminLogCont(okCont);
        adminLogRepository.save(adminLog);
        return null;
    }

    @Override
    public ShtAdminLog getLastTimeSuspendUser(Long userId) {
        List<ShtAdminLog> adminLogs = adminLogRepository.getLastTimeSuspendUser(userId);
        return adminLogs.size() > 0 ? adminLogs.get(0) : null;
    }

    private ShtAdminLog buildAdminLog(ShmAdmin admin, ShmUser shmUser, String ctrlStatus) {
        ShtAdminLog adminLog = new ShtAdminLog();
        adminLog.setShmAdmin(admin);
        adminLog.setShmUser(shmUser);
        if (PatrolActionEnum.PATROL_USER_OK.toString().equals(ctrlStatus)) {
            adminLog.setAdminLogTitle(String.valueOf(PatrolActionEnum.PATROL_USER_OK.ordinal()));
            adminLog.setAdminLogCont(messageSource.getMessage("SH_E100047", null, null));
        }
        return adminLog;
    }
}
