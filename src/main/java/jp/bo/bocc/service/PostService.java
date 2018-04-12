package jp.bo.bocc.service;

import jp.bo.bocc.controller.api.request.PostBodyEdit;
import jp.bo.bocc.controller.api.request.PostBodyRequest;
import jp.bo.bocc.controller.web.request.PostPatrolRequest;
import jp.bo.bocc.controller.web.request.PostSearchRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.enums.ImageSavedEnum;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by haipv on 3/14/2017.
 */
public interface PostService {

    ShmPost showPostPatrolDetailById(long postId) throws ParseException, SchedulerException;

    Page<ShmPostDTO> searchPost(Pageable pageable, String postName, String postType, Long addrProvinceId, Long addrDistrictId, boolean isSameCompany, Long userId, Long categoryIdSuper, Long categoryIdChild, String sortField, Long price, String userAgent, Long categorySettingId, Long bannerId);

    List<ShmPost> getShmPostImages(List<ShmPost> resultWithOutFile, ImageSavedEnum imageSavedEnum);

    ShmPost getPost(long id);

    ShmPost getPost(long id, ShmUser shmUser);

    ShmPost editPost(ShmPost shmPost, PostBodyEdit postBodyEdit);

    ShmPost savePost(ShmPost shmPost);

    Page<ShmPost> getPostByUserId(Long userId, String bsid, Pageable pageable);

    Page<ShmPost> searchPostByConditions(PostSearchRequest postRequest, Pageable pageable);

    PostBodyRequest createPost(PostBodyRequest postBodyRequest, ShmUser shmUser) throws Exception;

    Boolean createSamplePost(PostBodyRequest postBodyRequest, String unzipDirName) throws Exception;

    void deletePost(ShmPost shmPost) throws Exception;

    long getCountPostById(long id) throws Exception;

    long getPostUserId(long id) throws Exception;

    long countPostByPostIdAndPostSellStatus(Long userId, int postStatus);

    long countPostByPostIdAndPostCtrlStatus(Long userId, int postCtrlStatus);

    void exportPostCSV(PostSearchRequest request, PageRequest pageRequest, HttpServletResponse response) throws IOException;

    /**
     * Get list own post by userId.
     *
     * @param pageable
     * @param userId
     * @return
     */
    Page<ShmPostDTO> searchOwnPostHasConversation(Pageable pageable, Long userId);

    /**
     * Get list posts from others having conversation.
     *
     * @param pageable
     * @param userId
     * @return
     */
    Page<ShmPostDTO> searchPostFromOthersHasConversation(Pageable pageable, Long userId);
    List<ShmPostDTO> searchPostPatrols(PostPatrolRequest request) throws ParseException;

    /**
     * count post of user
     */
    Long countPostByUserId(long userId);

    List<ShmPost> getPostsByUserId(Long userId);

    long countPostByPostIdAndPostType(Long userId, int postType);

    ShmPost getShmPostImage(ShmPost resultWithOutFile, ImageSavedEnum imageSavedEnum);

    /**
     * calculate like time for post.
     * @param postId
     * @param i
     */
    void calculateLikeTimes(Long postId, long i);

    /**
     * Process
     */
    ShmPost processPostToOke(Long postId, String adminEmail) throws Exception;

    /**
     * if user is Post's owner, return true
     * @param userId
     * @return
     */
    boolean isOwnerPostOfTalkPurc(Long userId);

    /**
     * Find report a post by user.
     * @param id
     * @param postId
     * @return
     */
    ShtUserRprt findReportPostByUser(Long id, Long postId);

    ShmPost getPostPatrolSequent() throws ParseException, SchedulerException;

    /**
     * build list image path.
     * @param content
     * @return
     */
    List<ShmPost> buildListImagePathsForListPost(List<ShmPost> content);

    ShmPost buildListImagePathsForPost(ShmPost content);

    long countTotalPostByUserIdList(List<Long> userIdList);

    long countTotalPostMatchingByUserIdList(List<Long> userIdList);

    List<String> buildListImagePathsForPost(String postImages, ImageSavedEnum original);

    List<ShrFile> buildListShrFileForPost(String postImages, ImageSavedEnum original);

    List<Long> getListPostIdForOwnerPostHavingConversation(Long userIdUsingApp);

    List<Long> getListPostIdFormOthersHavingConversation(Long userIdUsingApp);

    Long countTotalPostsByUserId(Long userId);

    List<ShmPost> getPostsOverTimeToPatrol(LocalDateTime limitTime, boolean isPatrol);

    long countTransactionByPostType(Long userId, int postType);

    long countUniqueTransactionByPostType(Long userId, int postType);

    /**
     * get post history
     * @param pageable
     * @param postType
     * @param userId
     * @param sortField
     * @param price
     * @return
     */
    Page<ShmPostDTO> getPostHistory(Pageable pageable, String postType, Long userId, String sortField, Long price);

    void exportPostPatrolCsv(PostPatrolRequest request, HttpServletResponse response) throws IOException, ParseException;

    /**
     * get all posts that user already liked.
     * @param pageable
     * @param postType
     * @param userId
     * @param sortField
     * @param price
     * @return
     */
    Page<ShmPostDTO> getFavoritePostHistory(Pageable pageable, String postType, Long userId, String sortField, Long price);

    boolean checkPostStatus(ShmPost shmPost) throws Exception;

    long getReportTimeForPost(long postId);

    void savePostPatrol(ShmPost post, ShmAdmin shmAdmin);

    void cleanDataNotProcessByAdmin(Long adminId);

    /**
     * Check post was being processed by other admin.
     * @param postId
     * @param adminId
     * @return
     */
    boolean checkPostInPatrolProcess(Long postId, Long adminId);

    /**
     * get default owner's infor for post.
     * @param userId
     * @return
     */
    ShmPostDTO getDefaultValueUserForPost(Long userId);

    void updatePostLikedTimes(Long postId, Long likedTimes);

    List<ShmPost> getPostByGroupId(Long groupId);
}
