package jp.bo.bocc.service;

import jp.bo.bocc.controller.web.request.BaseNGRequest;
import jp.bo.bocc.controller.web.request.PostMemoRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtMemoPost;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by NguyenThuong on 4/20/2017.
 */
public interface MemoPostService {

    List<PatrolHistoryDTO> getMemoPostListByPostId(Long postId);

    List<PatrolHistoryDTO> getPatrolHistoryByPostId(Long postId);

    ShtMemoPost processPost(ShmAdmin admin, PostMemoRequest postMemoRequest) throws SchedulerException;

    ShtMemoPost saveMemoPost(ShmAdmin admin, PostMemoRequest postMemoRequest);

    void interruptJob(Long postId) throws SchedulerException;

    void createJobSchedule(Long postId, LocalDateTime now) throws SchedulerException, ParseException;

    void updatePostPatrols(boolean isPatrol);

    ShtMemoPost getMemoNewestByPostId(long postId);

    String buildMessageSuspendForUser(BaseNGRequest baseNGRequest);

}
