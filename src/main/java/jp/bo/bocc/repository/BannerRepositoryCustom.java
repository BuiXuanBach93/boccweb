package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtCategorySetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by buixu on 11/17/2017.
 */
public interface BannerRepositoryCustom {
    Page<ShtBanner> getListBanner(ShtBanner banner, Pageable pageRequest);
}
