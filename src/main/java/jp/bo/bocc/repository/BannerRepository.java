package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtCategorySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
@Repository
public interface BannerRepository extends JpaRepository<ShtBanner, Long>, BannerRepositoryCustom {

    @Query("SELECT COUNT(stb) FROM ShtBanner stb WHERE stb.bannerStatus = 1")
    Long countBannerActive();

    List<ShtBanner> findByBannerName(String categoryName);

    @Query("SELECT stb FROM ShtBanner stb WHERE stb.bannerStatus = 1 AND ROWNUM <= ?1")
    List<ShtBanner> getMaxActiveBanners(Long max);


    @Query("SELECT stb FROM ShtBanner stb WHERE stb.bannerStatus = 1")
    List<ShtBanner> getActiveBanners();
}
