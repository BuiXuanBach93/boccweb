package jp.bo.bocc.service;

import jp.bo.bocc.controller.api.request.ShmUserUpdateRequest;
import jp.bo.bocc.controller.api.request.UserRequestBody;
import jp.bo.bocc.controller.web.request.UserPatrolRequest;
import jp.bo.bocc.controller.web.request.UserSearchRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.system.exception.ServiceException;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author manhnt
 */
public interface UserService {

	ShmUser getUserById(long id) throws ServiceException;

	ShmUser getSystemUser();

	ShmUser findUserById(Long userId);

	ShmUser findUserByEmailAndPassword(String email, String password);

	void tendToLeave(UserRequestBody userRequestBody, ShmUser currentUser) throws Exception;

    void unSubscribeTopic(Long id);

    Map<String, Object> changePassword(UserRequestBody userRequestBody, ShmUser currentUser, ShtUserToken userToken) throws Exception;

	/**
	 * Validate BS account
	 * @param bsidOrEmail
	 * @param bsPassword
	 * @return BSID
	 */
	String verifyBsAccount(String bsidOrEmail, String bsPassword);

	ShtUserToken newRegistration(String bsid, String bsPassword, String email);

	ShtUserToken createEmailActiveToken(ShmUser shmUser, String email);

	/**
	 * Update user's info.
	 * @param user
	 * @param regToken if in registration phase
	 * @return
	 * @throws ServiceException
	 */
	ShmUser save(ShmUser user, ShtUserToken regToken) throws ServiceException;

	void forgotPassword(String email, String phoneNumber);

	void activateEmail(ShmUser user, String activationToken);

	void activateNewEmail(ShmUser user, String activationToken, String email);

	void delete(long userId) throws ServiceException;

	Page<ShmUser> searchUserByConditions(UserSearchRequest user, Pageable pageable);
	/**
	 * Submit phone number to receive activation code via SMS
	 * @param user
	 * @param phoneNumber
	 */
	void newPhoneNumber(ShmUser user, String phoneNumber);

	Map<String, String> sendPhoneActivationCode(ShmUser user, String phoneNumber);

	void activatePhone(ShmUser user, String activationCode);

	void activateNewPhone(ShmUser user, String activationCode);

    void sendActivationEmail(String email, ShtUserToken activationToken);

	void sendActivationEmailChanged(String email, ShtUserToken activationToken);

    List<ShmUser> findUserByFirstName(String firstName);

	/**
	 * User like a post.
	 * @param id
	 * @param postId
	 * @param status
	 */
    void likePost(Long id, Long postId, String status) throws Exception;

	/**
	 * Create report.
	 * @param user
	 * @param postId
	 * @param userRprtType
	 * @param userRprtCont
	 */
    void createReport(ShmUser user, ShmPost postId, ShtUserRprt.UserReportTypeEnum userRprtType, String userRprtCont) throws Exception;

    void blockUser(ShmUser fromUser, Long talkPurcId);

	void muteUser(ShmUser fromUser, Long talkPurcId);

	/**
	 * Get user profile.
	 * @param userId
	 * @return
	 */
	ShmUserDTO getUserProfile(Long userId);

	List<ShmUser> getUserByFirstNameOrLastName(String userName);

	ShmUserDTO getUserPatrolSequent() throws ParseException, SchedulerException;

	/**
	 * Get list user for patrol admin.
	 * @param userPatrolRequest
	 * @param pageable
     * @return
	 */
    Page<ShmUserDTO> getUserListForPatrolSite(UserPatrolRequest userPatrolRequest, Pageable pageable);

	/**
	 * Create new random password for user and send it via email
	 * @param email
	 * @param phoneNumber
	 */
	void resetPassword(String email, String phoneNumber);

	/**
	 * get total review for user.
	 * @param userId
	 * @return
	 */
	long getReviewForUserByUserId(Long userId);

	List<ShmUser> getUserListByFirstNameAndLastName(List<String> fullName);

	List<ShmUser> getUserListByFirstNameAndLastName(String firstNameLastName);

	/**
	 * build
	 * @param avatar
	 * @return
	 */
    String buildOriginalAvatarPathForUser(ShrFile avatar);

	ShmUser save(ShmUser user);

	ShmUser editUserInfo(Long userId, ShmUserUpdateRequest newUser) throws NoSuchAlgorithmException;

    ShmUserDTO getUserDTOById(Long userId) throws ParseException, SchedulerException;

    Long getNextUserId(Long userId);

    List<ShmUser> getUsersOverTimeToPatrol(LocalDateTime limitTime, boolean isPatrol);

	/**
	 * Patrol censore user.
	 * @param userId
	 * @param adminEmail
	 */
	ShmUser censoredUserOk(Long userId, String adminEmail) throws SchedulerException;

	boolean checkUserStatus(ShmUser shmUser) throws Exception;

	void validateUserPassword(String password) throws Exception;

	void exportUserCSV(UserSearchRequest userRequest, Pageable pageable, HttpServletResponse response) throws IOException;

	void exportUserPatrolCsv(ShmAdmin admin, UserPatrolRequest request, Pageable pageable, HttpServletResponse response) throws IOException;

	Map<String, List<Long>> getUserListRegistInMonth(LocalDate startDate, LocalDate endDate);

	/**
	 * Create device token when user finish registration.
	 *
     * @param id
     * @param deviceToken
     * @param osType
     */
    void saveDeviceTokenRegistration(Long id, String deviceToken, String osType);

	/**
	 * Check user device token when user login.
     * @param id
     * @param deviceToken
     * @param OSType
     */
	void saveDeviceTokenLogin(Long id, String deviceToken, String OSType);

	void handleUserLeft(Long userId);

    /**
     * clean data that was not processed by admin.
     * @param adminId
     */
    void cleanDataNotProcessedByAdmin(Long adminId);

    void saveUserPatrol(ShmUser user, ShmAdmin shmAdmin);

    boolean chechkUserDeleted(long userId);

    /**
     * If user was been processing by the admin then return TRUE, else return FALSE.
     * @param adminId
     * @param userId
     * @return
     */
    boolean checkUserInProcessByCurrentAdmin(Long adminId, Long userId);

	ShmUser findActiveUserById(Long postUserId);

	ShtUserTalkPurc getUserTalkPurc(Long fromUser, Long toUser);

	List<ShtGroupPblDetail> getGroupPblDetails(String legalId);

	/**
	 * Unsubscribe user by userId and device token.
	 * @param id
	 * @param deviceToken
	 */
    boolean unSubscribeTopic(Long id, String deviceToken);

	List<String> getListBsFromOtherSchema();

	List<ShmUser> getActiveUsers();

	List<ShmUser> getActiveUsersByBsId(String bsid);

	List<ShmUser> getActiveUserByLegalId(String legalId);

	List<ShmUser> getTendToLeaveUserBySyncExcId(Long syncExcId);

	void handleSyncUserTendToLeave(ShmUser shmUser, Long syncId);

	void handleSyncUserLeft(ShmUser user);

	List<ShmUser> getMissingLeftSyncUsersBeforeNow();
}
