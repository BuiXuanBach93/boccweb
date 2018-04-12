package jp.bo.bocc.repository;

import jp.bo.bocc.entity.dto.ShmUserBsDTO;
import jp.bo.bocc.entity.ShtSyncExc;

import java.util.List;

/**
 * Created by DonBach
 */
public interface SyncExcRepositoryCustom {
    int countNewMsgSystemPushAll(Long userId);

    String getLastDateSyncData();

    List<Long> getUserDataFromBSSystem(String lastSyncDate);

    List<Long> getUserDataFromBSSystem(ShtSyncExc lastSyncExc);

    List<Long> getUserDataFromBSSystemFirstTime();

    Long getLastSyncExcId();
}
