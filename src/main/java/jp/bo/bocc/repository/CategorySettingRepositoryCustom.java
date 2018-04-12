package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.entity.ShtGroupPublish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by buixu on 11/17/2017.
 */
public interface CategorySettingRepositoryCustom {
    Page<ShtCategorySetting> getListCategorySetting(ShtCategorySetting categorySetting, Pageable pageRequest);
}
