package jp.bo.bocc.service;


import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.entity.ShtGroupPublish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
public interface CategorySettingService {

    Page<ShtCategorySetting> getCategorySetting(ShtCategorySetting categorySetting, Pageable pageNumber);

    ShtCategorySetting saveCategorySetting(ShtCategorySetting categorySetting);

    ShtCategorySetting getCategorySettingById(Long categorySettingId);

    void resetDefaultCategorySetting();

    Long countCategorySettingActive();

    List<ShtCategorySetting> getCategorySettingByName(String categoryName);

    List<ShtCategorySetting> getActiveCategorySetting();

    List<ShtCategorySetting> getNoLoginActiveCategorySetting();
}
