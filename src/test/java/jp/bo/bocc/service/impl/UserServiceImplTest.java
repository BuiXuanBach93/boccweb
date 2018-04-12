package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.UserPatrolRequest;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserToken;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.helper.StringUtils;
import jp.bo.bocc.repository.TokenRepository;
import jp.bo.bocc.repository.UserNtfRepository;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.system.config.Profiles;
import jp.bo.bocc.system.config.TestConfig;
import jp.bo.bocc.system.exception.ForbiddenException;
import jp.bo.bocc.system.exception.ResourceConflictException;
import jp.bo.bocc.system.exception.SafeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author manhnt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles({Profiles.APP, Profiles.TEST})
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenRepository tokenRepository;

//    //eror code 001
//    private static final String bsid = "088888910000006";
//    private static final String bspw = "999999";

//    //eror code 003
//    private static final String bsid = "101248000000199";
//    private static final String bspw = "999999";

//    //eror code 006
//    private static final String bsid = "088888910000005";
//    private static final String bspw = "999999";

    private static final String bsid = "111050000000007";
    private static final String bspw = "111050";
    private static final String bsemail = "y.nemoto@benefit-one.co.jp";
    private static final String bsemailpw = "wk123456";
//    private static final String bsemail = "beneone.test01@gmail.com";
//        private static final String bsemailpw = "benefit0315";
    @Autowired
    private UserNtfRepository userNtfRepository;

    @Test
    public void testVerifyBsAccount() throws Exception {
        userService.verifyBsAccount(bsid, bspw);
    }

    @Test(expected = ForbiddenException.class)
    public void testVerifyBsAccountFail() throws Exception {
        userService.verifyBsAccount(bsemail, bsemailpw);
    }

    @Test
    public void testNewRegistration() throws Exception {
        ShtUserToken regToken = userService.newRegistration(bsid, bspw, "st.john@example.com");
        assertEquals(ShtUserToken.TokenType.REGISTRATION_TOKEN, regToken.getTokenType());
        assertEquals("st.john@example.com", regToken.getUser().getEmail());
        assertEquals(bsid, regToken.getUser().getBsid());
        assertEquals(ShmUser.Status.EMAIL_UNACTIVATED, regToken.getUser().getStatus());
    }

    @Test(expected = ResourceConflictException.class)
    public void testNewRegistrationFailConflictEmail() throws Exception {

        createSampleUser("AnotherBsId", "st.john@example.com", ShmUser.Status.ACTIVATED);
        userService.newRegistration(bsid, bspw, "st.john@example.com");
    }

    @Test(expected = ForbiddenException.class)
    public void testNewRegistrationFailBSIDLimit() throws Exception {

        createSampleUser(bsid, "st.john1@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john2@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john3@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john4@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john5@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john6@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john7@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john8@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john9@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john10@example.com", ShmUser.Status.ACTIVATED);

        ShtUserToken regToken = userService.newRegistration(bsid, bspw, "st.john@example.com");

    }

    @Test
    public void testValidatePassword() throws Exception{
        String passw = "123asdqwe%@";
        userService.validateUserPassword(passw);
    }

    @Test
    public void testNewRegistrationSuccess() throws Exception {

        createSampleUser(bsid, "st.john1@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john2@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john3@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john4@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john5@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john6@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john7@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john8@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john9@example.com", ShmUser.Status.ACTIVATED);
        createSampleUser(bsid, "st.john10@example.com", ShmUser.Status.LEFT);

        ShtUserToken regToken = userService.newRegistration(bsid, bspw, "st.john@example.com");
    }

    @Test
    public void testNewRegistrationContinue() throws Exception {

        ShmUser sampleUser = createSampleUser("FOO", "st.john@example.com", ShmUser.Status.SMS_UNACTIVATED);
        try {
            ShtUserToken regToken = userService.newRegistration("FOO", "secret", "st.john@example.com");
        } catch (SafeException e) {
            assertThat(e.errorDetail, hasEntry("userId", sampleUser.getId()));
            assertThat(e.errorDetail, hasKey("registrationToken"));
            assertThat(e.errorDetail, hasEntry("completed", UserServiceImpl.meaningInRegistrationStep(sampleUser.getStatus())));
        }
    }

    @Test
    public void testActivateEmail() throws Exception {
        ShtUserToken regToken = userService.newRegistration(bsid, bspw, "st.john@example.com");
        List<ShtUserToken> mailActivationTokens = tokenRepository.findByUserAndTokenType(regToken.getUser(), ShtUserToken.TokenType.MAIL_ACTIVE_TOKEN);

        assertThat(mailActivationTokens.size(), is(1));

        ShtUserToken token = mailActivationTokens.get(0);
        assertThat(token.isExpired(), is(false));

        userService.activateEmail(regToken.getUser(), token.getTokenString());

        assertThat(regToken.getUser().getStatus(), is(ShmUser.Status.SMS_UNACTIVATED));
        assertThat(token.isExpired(), is(true));
    }

    private ShmUser createSampleUser(String bsid, String email, ShmUser.Status status) {
        ShmUser user = new ShmUser();
        user.setBsid(bsid);
        user.setEmail(email);
        user.setStatus(status);
        return userService.save(user, null);
    }

    /**
     * Exist ShmtUser with id =2 and ShmPost with postId =2.
     *
     * @throws Exception
     */
    @Test
//	@Transactional
//	@Rollback(value = false)
    public void likePost() throws Exception {
        userService.likePost(3L, 2L, "LIKED");
        Assert.assertTrue(true);
    }

    /**
     * Exist ShmtUser with id =2 and ShmPost with postId =2.
     *
     * @throws Exception
     */
    @Test
//	@Transactional
//	@Rollback(value = false)
    public void unLikePost() throws Exception {
        userService.likePost(3L, 2L, "NONE");
        Assert.assertTrue(true);
    }

    /**
     * Prepare data: there is a talk(talk_id=1) with user1 (user_id =1) and user2 (user_id=2)
     * user2 block user1.
     * talk_id =1
     * Expect: insert SHT_USER_TALK_PURC table, user_block_status =1,user2 block user 1 in talk (talk_id=1).
     */
    @Test
    @Rollback(value = false)
    public void testBlockuser() {
        ShmUser shmUser = new ShmUser(2L);
        userService.blockUser(shmUser, 1L);
    }

    /**
     * Prepare data: there is a talk(talk_id=1) with user1 (user_id =1) and user2 (user_id=2)
     * user2 block user1.
     * talk_id =1
     * Expect: insert SHT_USER_TALK_PURC table, user_block_status =1,user2 block user 1 in talk (talk_id=1).
     */
    @Test
    @Rollback(value = false)
    public void testMuteuser() {
        ShmUser shmUser = new ShmUser(436L);
        userService.muteUser(shmUser, 421L);
    }
    @Test
    public void testSendEmail() {
        userService.sendActivationEmail("abc@gmail.com", new ShtUserToken());
    }

    /**
     * getUser profile
     * exist user having Id = 1.
     */
    @Test
    public void testGetUserProfile() {
        final ShmUserDTO userProfile = userService.getUserProfile(1L);
        Assert.assertTrue(userProfile.getUserId() == 1);
    }

    @Test
    public void testResetPassword() throws NoSuchAlgorithmException {
        ShmUser sampleUser = new ShmUser();
        sampleUser.setBsid(bsid);
        sampleUser.setEmail("st.john4@example.com");
        sampleUser.setStatus(ShmUser.Status.ACTIVATED);
        sampleUser.setPhone("09011111111");
        sampleUser.setPassword("hogehoge");
        ShmUser oldUser = userService.save(sampleUser, null);

        userService.resetPassword("st.john4@example.com", "09011111111");

        ShmUser savedUser = userService.getUserById(oldUser.getId());

        assertThat(savedUser.getPassword(), not(StringUtils.getMD5("hogehoge")));
    }

    @Test
    public void testGetUserListForPatrolSite() {
        UserPatrolRequest userPatrolRequest = new UserPatrolRequest();
        userPatrolRequest.setCensoredFrom(new Date());
        userPatrolRequest.setCensoredTo(new Date());
        final Page<ShmUserDTO> userListForPatrolSite = userService.getUserListForPatrolSite(userPatrolRequest, new PageRequest(0,1));
        Assert.assertTrue(userListForPatrolSite.getContent().size() > 0);
    }

    @Test
    public void testGetUserListForPatrolSite1() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        UserPatrolRequest userPatrolRequest = new UserPatrolRequest();
        userPatrolRequest.setUpdatedAtFrom(sdf.parse("2017/04/21 22:00"));
        final Page<ShmUserDTO> userListForPatrolSite = userService.getUserListForPatrolSite(userPatrolRequest, new PageRequest(0,1));
        Assert.assertTrue(userListForPatrolSite.getContent().size()>0);
    }

    @Test
    public void testGetUserListForPatrolSiteSearchReport() throws ParseException {
        UserPatrolRequest userPatrolRequest = new UserPatrolRequest();
        userPatrolRequest.setReportByOthers("REPORT_BY_OTHERS");
        final Page<ShmUserDTO> userListForPatrolSite = userService.getUserListForPatrolSite(userPatrolRequest, new PageRequest(0,1));
        Assert.assertTrue(userListForPatrolSite.getContent().size()==0);
    }

    @Test
    public void testGetUserDeviceToken() {
        final Long userAndDeviceTokenExisted = userNtfRepository.getUserAndDeviceTokenExisted(2L, "fZ-mZg3k2LI:APA91bHtYiICKsapNmvRnkT-AP-lq7HzmgWdfv6j3vYTJ0RuFV9iH5OJb_Hgi4nTn8zJ9HuuBCe01OyWuS4kkQ8VmHsFmSftZM1bWnpnJf-BXhfXiPhUnCqXz_ZV0OEer2AiY67EERVa");
        System.out.printf(userAndDeviceTokenExisted + "");

    }

    @Test
    public void testGetUserBlocked() {
//        final int userAndDeviceTokenExisted = userService.findUserTalkPurcBlock(2L, 2L);
//        System.out.printf(userAndDeviceTokenExisted + "");

    }

    @Test
    public void testnewPhoneNumber() {
        final ShmUser userById = userService.getUserById(369);
        userService.newPhoneNumber(userById, "09055554444");
    }

    @Test
    public void testUnsubscribe(){
        userService.unSubscribeTopic(83L,"bfede41b413a59ee8b5813ef20ace7fc18ddbce3ed3e2dba26af2b7c4cfba6f1");
    }

    @Test
    public void testGetBSId(){
        List list = userService.getListBsFromOtherSchema();
        if(list.size() <= 0){
            return;
        }
    }

}
