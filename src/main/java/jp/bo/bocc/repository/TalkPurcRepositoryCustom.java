package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtTalkPurc;

import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public interface TalkPurcRepositoryCustom {

    /**
     * count talk purcs of post
     * @param postId
     * @return
     */
    Long countTalkPurcByPostId(Long postId);

    /**
     * List all talk in conversation.
     * @param postId
     * @return
     */
    List<Object[]> getListTalkByPostId(Long postId);

    ShtTalkPurc getTalkPurcById(long id);

    Long countNewMsgNumberByPostId(long postId, long userId);
}
