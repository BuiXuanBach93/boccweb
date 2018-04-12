package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserSetting;

/**
 * Created by DonBach on 5/25/2017.
 */
public interface UserSettingService {

    ShtUserSetting getUserSetting(Long id);

    ShtUserSetting saveUserSetting(ShtUserSetting userSetting);

    ShtUserSetting getByUserId(Long userId);

    void createUserSetting(ShmUser shmUser);

    /**
     * true (1) - receive, false (0) - not receive
     * @param userId
     * @return
     */
    boolean checkReceiveMailOn(Long userId);

    /**
     * * true (1) - receive, false (0) - not receive
     * @param userId
     * @return
     */
    boolean checkReceivePushOn(Long userId);

    /**
     * 1 or null - receive, 0 - not receive
     * @param partId
     * @return
     */
    boolean checkReceivingMailInTransaction(Long partId);

    boolean checkReceivePushTalkRoomFirstMsg(Long userId);

    boolean checkReceivepushFavorite(Long userId);

    boolean checkReceivePushTalkRoomTransaction(Long userId);

    /**
     * 1 or null - receive, 0 - not receive
     * @param partId
     * @return
     */
    boolean checkReceivingMailInTalk(Long partId);
}
