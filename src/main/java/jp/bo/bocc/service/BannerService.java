package jp.bo.bocc.service;


import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtCategorySetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
public interface BannerService {

    Page<ShtBanner> getBanners(ShtBanner banner, Pageable pageNumber);

    ShtBanner saveBanner(ShtBanner banner);

    ShtBanner getBannerById(Long bannerId);

    Long countBannerActive();

    List<ShtBanner> getBannerByName(String bannerName);

    List<ShtBanner> getActiveBanner();

    ShrFile saveBannerImage(Long bannerId, MultipartFile imageBanner) throws IOException;

    void handleJobBannerExpired(Long bannerId);

    void handleJobBannerActive(Long bannerId);

    void settingSchedulerBanner(ShtBanner banner);
}
