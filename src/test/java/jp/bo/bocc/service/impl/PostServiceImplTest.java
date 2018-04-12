package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.PostBodyRequest;
import jp.bo.bocc.controller.web.request.PostSearchRequest;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.service.AddressService;
import jp.bo.bocc.service.PostService;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import jp.bo.bocc.system.exception.ServiceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Namlong on 3/14/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class PostServiceImplTest {

    @Autowired
    PostService postService;

    @Autowired
    AddressService addressService;
    @Autowired
    UserService userService;

    ShmPost shmPost;


    @Before
    public void setup() {
    }


    @Test
    @Rollback
    public void testCreatePostSuccessfully() throws Exception {
        PostBodyRequest postBodyRequest = new PostBodyRequest();
        postBodyRequest.setPostName("Buy a tivi");
        postBodyRequest.setPostDescription("Sony");
        postBodyRequest.setPostCategoryId(1000L);
        postBodyRequest.setPostPrice(1000L);
        postBodyRequest.setPostType(ShmPost.PostType.BUY);
        ShmUser shmUser = userService.getUserById(2L);
        postBodyRequest.setShmUser(shmUser);
        postBodyRequest.setPostAddr(2L);
        PostBodyRequest result =postService.createPost(postBodyRequest, shmUser);

        assertEquals(postBodyRequest.getPostName(), result.getPostName());
        assertEquals(postBodyRequest.getPostDescription(), result.getPostDescription());
        assertEquals(postBodyRequest.getPostCategoryId(), result.getPostCategoryId());
        assertEquals(postBodyRequest.getPostPrice(), result.getPostPrice());
        assertEquals(postBodyRequest.getShmUser(), result.getShmUser());
        assertEquals(postBodyRequest.getPostType(), result.getPostType());
    }

    @Test
    @Transactional
    public void testCreatePostFailed() throws Exception {
        PostBodyRequest postBodyRequest = new PostBodyRequest();
        postBodyRequest.setPostDescription("ti vi sony");
        postBodyRequest.setPostCategoryId(1000L);
        postBodyRequest.setPostPrice(1000L);
        postBodyRequest.setPostType(ShmPost.PostType.BUY);
        ShmUser shmUser = userService.getUserById(2L);
        postBodyRequest.setShmUser(shmUser);
        try{
            PostBodyRequest result =postService.createPost(postBodyRequest, shmUser);
        }catch (Exception e) {
            if (e instanceof ServiceException)
                assertFalse(false);
        }
        postBodyRequest.setPostName("Buy a tivi");
        try{
            PostBodyRequest result =postService.createPost(postBodyRequest, shmUser);
        }catch (Exception e) {
            if (e instanceof ServiceException)
                assertFalse(false);
        }
        postBodyRequest.setShmAddr(addressService.getAddress(2L));
        try{
            PostBodyRequest result =postService.createPost(postBodyRequest, shmUser);
        }catch (Exception e) {
            if (e instanceof ServiceException)
                assertFalse(false);
        }
    }

//    @Test
//    public void getPost() {
//        PostSet postSet = new PostSet();
//        assertEquals(postSet.myPost , postService.getPost(postSet.myPost.getPostId()));
//    }

    @Test
    @Rollback
    public void deletePost() throws Exception {
        ShmPost shmPost = postService.getPost(100L);
        postService.deletePost(shmPost);
        ShmPost deletedPost = postService.getPost(100L);
        assertEquals(deletedPost.getPostSellStatus(), ShmPost.PostSellSatus.DELETED);
    }


    /**
     * Insert data with script R__SAMPLE_SHM_POST.sql
     * Expect: 1 record
     * @throws Exception
     */
    @Test
    public void searchPost() throws Exception {
        String postName = null;
        Long postAddrDistId = null;
        boolean sameCompanyFlag = false;
        String postType = null;
        Pageable pageable = new PageRequest(0,10);
        Long categoryId = null;
        Long categoryIdChild = null;
        final Page<ShmPostDTO> shmPostDTOS = postService.searchPost(pageable, postName, postType, postAddrDistId, postAddrDistId, sameCompanyFlag, 555L, categoryId, categoryIdChild, null, null, null, 4L, null);
        final List<ShmPostDTO> content = shmPostDTOS.getContent();
        assertEquals(2, content.size());
    }

    /**
     * Insert data with script R__SAMPLE_SHM_POST.sql
     * The user creating post have the same company.
     * Expect: 4 record
     * @throws Exception
     */
    @Test
    public void searchPostSameCompany() throws Exception {
        String postName = null;
        Long postAddrProvCode = 201L;
        Long postAddrDistCode = 201L;
        boolean sameCompanyFlag = true;
        String postType = "SELL";
        Long userId = 2L;
        Pageable pageable = new PageRequest(0, 100);
        Long categoryId = null;
        Long categoryIdChild = null;
        final Page<ShmPostDTO> shmPostDTOS = postService.searchPost(pageable, postName, postType, postAddrDistCode, postAddrDistCode, sameCompanyFlag, userId, categoryId, categoryIdChild, null,0L, null, null, null);
        final List<ShmPostDTO> content = shmPostDTOS.getContent();
        assertEquals(2, content.size());
    }

//    public class PostSet{
//        ShmPost myPost = null;
//
//        public PostSet(){
//            ShmPost shmPost = new ShmPost();
//            shmPost.setPostName("Post name to test");
//            shmPost.setPostImages("1,2,3,4");
//            shmPost.setPostPrice(1234L);
//            ShmPost savedPost = postService.savePost(shmPost);
//            myPost = postService.getPost(savedPost.getPostId());
//        }
//    }

    @Test
    public void testSearchPostByConditions() {
        PageRequest pageRequest = new PageRequest(0, 20);

        PostSearchRequest searchRequest = new PostSearchRequest();
        searchRequest.setPostId("2");
        Page<ShmPost> posts = postService.searchPostByConditions(searchRequest, pageRequest);
        assertEquals(posts.getTotalElements(), 1);

        searchRequest = new PostSearchRequest();
        searchRequest.setUserName("KimQuy");
        posts = postService.searchPostByConditions(searchRequest, pageRequest);
        assertNull(posts);
    }

    /**
     * get own posts that have conversation.
     * Prepare data: The user has post having conversation.
     */
    @Test
    public void testSearchOwnPostHasConversation(){
        final Page<ShmPostDTO> shmPostDTOS = postService.searchOwnPostHasConversation(new PageRequest(0, 100), 111L);
        assertTrue(shmPostDTOS.getContent().size()>0);
    }

    /**
     * get posts from others that have conversation with the user.
     * Prepare data: The user has having conversation with other users.
     */
    @Test
    public void testSearchPostFromOthersHasConversation(){
        final Page<ShmPostDTO> shmPostDTOS = postService.searchPostFromOthersHasConversation(new PageRequest(0, 100), 2L);
        assertTrue(shmPostDTOS.getContent().size()>0);
    }

    @Test
    public void testGetPostHistory() {
        final Page<ShmPostDTO> result = postService.getPostHistory(new PageRequest(0, 100), "ALL", 2L, null, 0L);
        Assert.assertTrue(result.getContent().size()>0);
    }

    @Test
    public void testGetFavoritePostHistory() {
        final Page<ShmPostDTO> result = postService.getFavoritePostHistory(new PageRequest(0, 100), "ALL", 11111L, "createdAtDesc", null);
        Assert.assertTrue(result.getContent().size()>0);
    }
}
