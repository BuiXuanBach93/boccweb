package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
@Repository
public interface CategorySettingRepository extends JpaRepository<ShtCategorySetting, Long>, CategorySettingRepositoryCustom {

    @Modifying
    @Transactional
    @Query("UPDATE ShtCategorySetting stc SET stc.isDefault = 0 WHERE stc.isDefault = 1")
    void resetDefaultCategorySetting();

    @Query("SELECT COUNT(stc) FROM ShtCategorySetting stc WHERE stc.categoryStatus = 1")
    Long countCategorySettingActive();

    List<ShtCategorySetting> findByCategoryName(String categoryName);

    @Query("SELECT stc FROM ShtCategorySetting stc WHERE stc.categoryStatus = 1 AND ROWNUM <= 5")
    List<ShtCategorySetting> getActiveCategorySetting();

    @Query("SELECT stc FROM ShtCategorySetting stc WHERE stc.categoryStatus = 1 AND stc.categoryFilterType <> 0 AND ROWNUM <= 5")
    List<ShtCategorySetting> getActiveCategorySettingNoLogin();
}
