package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserNtf;
import jp.bo.bocc.enums.OSTypeEnum;
import jp.bo.bocc.repository.UserNtfRepository;
import jp.bo.bocc.repository.UserRepository;
import jp.bo.bocc.service.UserNtfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Created by Namlong on 5/18/2017.
 */
@Service
public class UserNtfSerivceImpl implements UserNtfService {

    @Autowired
    UserNtfRepository userNtfRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ShtUserNtf save(ShtUserNtf shtUserNtf) {
        return userNtfRepository.save(shtUserNtf);
    }

    @Override
    public ShtUserNtf updateNtfStatus(long userNtfId) {
        ShtUserNtf shtUserNtf = userNtfRepository.findOne(userNtfId);
        return userNtfRepository.save(shtUserNtf);
    }

    @Override
    public void deleteCurrentDeviceToken(Long userId, String userNtfDeviceToken) {
        userNtfRepository.deleteCurrentDeviceToken(userId,userNtfDeviceToken);
    }

    @Override
    public Long countAndroidDeviceActive() {
        return userNtfRepository.countAndroidDeviceActive();
    }

    @Override
    public Long countIOSDeviceActive() {
        return userNtfRepository.countIOSDeviceActive();
    }

    @Override
    public List<ShmUser> getListUserActive() {
        return userNtfRepository.getListUserActive();
    }

    @Override
    public List<ShmUser> getListAndroidUserActive() {
        return userNtfRepository.getListAndroidUserActive();
    }

    @Override
    public List<ShmUser> getListIOsUserActive() {
        return userNtfRepository.getListIOsUserActive();
    }

    @Override
    @Transactional
    public void deleteUserDeviceToken(Long userId, String userNtfDeviceToken) {
        userNtfRepository.deleteUserDeviceToken(userId,userNtfDeviceToken);
    }

    @Override
    public void createSamepleUseNtf() {
        ShtUserNtf userNtf = new ShtUserNtf();
        ShmUser user = userRepository.findOne(98L);
        userNtf.setShmUser(user);
        userNtf.setUserNtfDeviceToken("9d9c477457146601dbb0b5673962638c325eb3a988bb9bd276874eac71e5bd4d");
        userNtf.setUserNtfOsType(OSTypeEnum.IOS);
        userNtfRepository.save(userNtf);

    }

    @Override
    public List<ShtUserNtf> getAllDeviceTokenForActiveUserNotSubscribed() {
        return userNtfRepository.getAllDeviceTokenForActiveUserNotSubscribed();
    }

    /**
     *
     * @param userNtfId
     */
    @Override
    public void subscribeDeviceToken(Long userNtfId) {
        userNtfRepository.subscribeDeviceToken(userNtfId);
    }
}
