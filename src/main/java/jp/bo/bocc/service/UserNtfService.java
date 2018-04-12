package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserNtf;

import java.util.List;

/**
 * Created by Namlong on 5/18/2017.
 */
public interface UserNtfService {
    ShtUserNtf save(ShtUserNtf shtUserNtf);
    ShtUserNtf updateNtfStatus(long userNtfId);
    void deleteCurrentDeviceToken(Long userId, String userNtfDeviceToken);
    Long countAndroidDeviceActive();
    Long countIOSDeviceActive();
    List<ShmUser> getListUserActive();
    List<ShmUser> getListAndroidUserActive();
    List<ShmUser> getListIOsUserActive();
    void deleteUserDeviceToken(Long userId, String userNtfDeviceToken);

    void createSamepleUseNtf();

    List<ShtUserNtf> getAllDeviceTokenForActiveUserNotSubscribed();

    /**
     * update subscribed is true.
     * @param userNtfId
     */
    void subscribeDeviceToken(Long userNtfId);
}
