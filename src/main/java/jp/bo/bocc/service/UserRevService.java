package jp.bo.bocc.service;

import jp.bo.bocc.controller.api.request.UserRevBodyRequest;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserRev;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by DonBach on 4/4/2017.
 */
public interface UserRevService {
    ShtUserRev getUserRev(long id);
    ShtUserRev getUserRevById(long id);
    ShtUserRev saveUserRev(ShtUserRev shtUserRev);
    Long countReviewByUserId(Long userId, ShtUserRev.UserReviewRate userReviewRate);
    List<ShtUserRev> getReviewByUserId(Long userId, ShtUserRev.UserReviewRate userReviewType);
    Page<ShtUserRev> getReviewToUserId(Long userId, Pageable pageable, ShtUserRev.UserReviewRate userReviewType);
    Long getReviewByTalkPurcId(Long talkPurcId, Long fromUserId);
    void validateUserRevRequest(UserRevBodyRequest userRevBodyRequest);
    ShtUserRev createReview(UserRevBodyRequest userRevBodyRequest, ShmUser currentUser);

    long countTotalReviewOtherPeople(long userId);

    long countTotalReviewMe(long userId);

    void interruptReviewRemindJob(Long talkPurcId,Boolean isReviewOnwer);

    /**
     * get total review for user.
     * @param userId
     * @return
     */
    Long getTotalReviewForUserByUserId(Long userId);
}
