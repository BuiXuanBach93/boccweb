package jp.bo.bocc.service;

import jp.bo.bocc.controller.web.request.UserNGRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtMemoUser;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author NguyenThuong on 4/5/2017.
 */
public interface MemoUserService {

    List<PatrolHistoryDTO> getAllHisProcessUserByUserId(Long userId);

    PatrolHistoryDTO saveMemoUser(Long userId, ShmAdmin admin, String content);

    PatrolHistoryDTO processUserNg(ShmAdmin admin, UserNGRequest request) throws SchedulerException;

    void updateUserPatrol();

    void createJobSchedule(Long userId, LocalDateTime now) throws ParseException, SchedulerException;

    void interruptJob(Long userId) throws SchedulerException;

    List<PatrolHistoryDTO> getAllMemoUserByUserId(Long userId);

    ShtMemoUser getNewestMemoByUserId(long userId);
}
