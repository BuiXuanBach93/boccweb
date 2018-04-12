package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by DonBach on 5/25/2017.
 */
public interface UserSettingRepository extends JpaRepository<ShtUserSetting, Long> {

    @Query("SELECT st FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    List<ShtUserSetting> getByUserId(@Param("userId") Long userId);
    @Query("SELECT st.receiveEmail FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    Boolean getReceiveEmailByUserId(@Param("userId") Long userId);

    @Query("SELECT st.receivePush FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    Boolean getReceivePushByUserId(@Param("userId") Long userId);

    @Query("SELECT st.mailTalkRoomTransaction FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    Boolean checkReceivingMailInTransaction(@Param("userId") Long userId);

    @Query("SELECT st.pushTalkRoomTransaction FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    Boolean checkReceivePushTalkRoomTransaction(@Param("userId")Long userId);

    @Query("SELECT st.pushFavorite FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    Boolean checkReceivepushFavorite(@Param("userId")Long userId);

    @Query("SELECT st.pushTalkRoomFirstMsg FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    Boolean checkReceivePushTalkRoomFirstMsg(@Param("userId")Long userId);

    @Query("SELECT st.mailTalkRoomFirstMsg FROM ShtUserSetting st WHERE st.shmUser.id = :userId ")
    Boolean checkReceivingMailInTalk(@Param("userId")Long userId);
}
