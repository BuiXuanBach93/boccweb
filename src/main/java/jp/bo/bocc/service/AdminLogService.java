package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtAdminLog;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author NguyenThuong on 4/14/2017.
 */
public interface AdminLogService {

    ShtAdminLog save(ShtAdminLog adminLog);

    List<PatrolHistoryDTO> convertAdminLogToPatrolHisDTO(List<ShtAdminLog> adminLogs);

    long countTotalProcessPostTimes(long postId);

    long countTotalProcessUserTimes(long userId);

    boolean pendingPostInPast(long postId);

    List<LocalDateTime> suspendPostInPast(long postId);

    List<ShtAdminLog> getAllAdminLogForPost(Long postId);

    List<ShtAdminLog> getAllAdminLogForUser(Long userId);

    ShtAdminLog saveAdminLogUser(ShmUser user, ShmAdmin shmAdmin);

    ShtAdminLog getLastAdminProcessPost(Long postId);

    ShtAdminLog getLastTimeSuspendUser(Long userId);
}
