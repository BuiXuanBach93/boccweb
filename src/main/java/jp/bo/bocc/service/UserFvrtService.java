package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShtUserFvrt;

import java.util.List;

/**
 * Created by DonBach on 3/31/2017.
 */
public interface UserFvrtService {
    ShtUserFvrt getUserFvrt(long postId, long userId);

    /**
     *  like, dislike a post.
     *
     * @param shtUserFvrt
     * @param status
     * @return
     */
    ShtUserFvrt updateUserFavoriteStatus(ShtUserFvrt shtUserFvrt, String status);

    Long countLikeTimeByPostId(Long postId);

    List<ShtUserFvrt> getUserFvrts(Long postId);
}
