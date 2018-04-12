package jp.bo.bocc.service;


import jp.bo.bocc.controller.web.request.BannerRequest;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtBannerPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by buixu on 12/29/2017.
 */
public interface BannerPageService {

    ShtBannerPage saveBannerPage(ShtBannerPage page);

    ShtBannerPage getBannerPageById(Long pageId);

    String buildPrivewPageUrl(Long pageId);

    String buildPageUrl(Long pageId);

    String buildImagePageUrl(Long pageId);

    ShtBannerPage getPageByBannerId(Long bannerId);

    ShtBannerPage storeBannerPage(BannerRequest request, MultipartFile pageImage) throws IOException;

    ShtBannerPage updateBannerPageImage(ShtBannerPage bannerPage, MultipartFile pageImage) throws IOException;

    ShrFile saveBannerPageImage(Long pageId, MultipartFile imagePage) throws IOException;
}
