package jp.bo.bocc.repository;

import jp.bo.bocc.controller.web.request.PostPatrolRequest;
import jp.bo.bocc.entity.ShmPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Namlong on 3/22/2017.
 */
public interface PostRepositoryCustom {

    /**
     * Search post
     *
     * @param pageable
     * @param listHashTag
     * @param textSearch
     * @param postType
     * @param listDistrictId
     * @param addrDistrictId
     * @param bsid
     * @param categoryIdSuper
     * @param categoryIdChild
     * @return
     */
    Page<ShmPost> searchPost(Pageable pageable, final List<String> listHashTag, String textSearch, final String postType, final List<Long> listDistrictId, final Long addrDistrictId, final String bsid, Long categoryIdSuper, Long categoryIdChild, String sortField, Long price, boolean isSameCompany, Long groupId, Long userId, Long categorySettingId, Long bannerId);

    /**
     * Get all Posts and Admin_log
     */
    List<Object[]> getPostPatrolList(PostPatrolRequest postSearchRequest);

    /**
     * count post of user
     *
     * @param userId
     * @return
     */
    Long countPostByUserId(Long userId);

    List<Long> getPostIdByUserId(Long userId);

    /**
     * * get all posts from others having conversation
     *
     * @param userId
     * @param pageable
     * @return
     */
    Page<Object[]> getPostFromOtherHavingConversation(Long userId, Pageable pageable);

    Long countTotalPostByUserIdList(List<Long> userIdList);

    Long countTotalPostMatchingByUserIdList(List<Long> userIdList);

    /**
     * get all owner posts have talk purcs
     *
     * @param userId
     * @param pageable
     * @return
     */
    public Page<Object[]> getOwnerPostHaveTalkPurc(Long userId, Pageable pageable);

    /**
     * get list postId having conversation.
     *
     * @param userIdUsingApp
     * @return
     */
    List<Long> getListPostIdForOwnerPostHavingConversation(Long userIdUsingApp);

    /**
     * get list postId from others having conversation.
     *
     * @param userIdUsingApp
     * @return
     */
    List<Long> getListPostIdFormOthersHavingConversation(Long userIdUsingApp);

    /**
     * get all posts by user
     * @param pageable
     * @param postType
     * @param sortField
     * @param price
     * @param userId
     * @return
     */
    Page<ShmPost> getPostHistory(Pageable pageable, String postType, String sortField, Long price, Long userId);

    /**
     * get all posts user liked
     * @param pageable
     * @param postType
     * @param sortField
     * @param price
     * @param userId
     * @return
     */
    Page<ShmPost> getFavoritePostHistory(Pageable pageable, String postType, String sortField, Long price, Long userId);

    /**
     * get all post of user
     * @param userId
     * @param pageable
     * @return
     */
    Page<ShmPost> getPostByUserId(Long userId, Long groupId, String companyId, Pageable pageable);

    Long getPostNumberByMonth(String month);

    Long getOwnerPostByMonth(String month);

    Long getPartnerByMonth(String month);

    Long getTransByMonth(String month);

    Long getActorPerMonth(String month);


    Long getPostNumberPerDay(String day);

    Long getOwnerPostPerDay(String day);

    Long getPartnerPerDay(String day);

    Long getTransPerDay(String day);

    Long getActorPerDay(String day);
}
