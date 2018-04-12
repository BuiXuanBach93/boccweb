package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.BannerRequest;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShrImage;
import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtBannerPage;
import jp.bo.bocc.enums.ImageContentTypeEnum;
import jp.bo.bocc.enums.ImageEnum;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.jobs.JobHandleActiveBanner;
import jp.bo.bocc.jobs.JobHandleExpiredBanner;
import jp.bo.bocc.repository.BannerPageRepository;
import jp.bo.bocc.repository.BannerRepository;
import jp.bo.bocc.service.BannerPageService;
import jp.bo.bocc.service.BannerService;
import jp.bo.bocc.service.ImageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by buixu on 12/29/2017.
 */
@Service
public class BannerPageServiceImpl implements BannerPageService {

    @Autowired
    BannerPageRepository repository;

    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    ImageService imageService;

    @Autowired
    ImageBuilder imageBuilder;

    @Autowired
    Environment environment;

    @Override
    public ShtBannerPage saveBannerPage(ShtBannerPage page) {
        return repository.save(page);
    }

    @Override
    public ShtBannerPage getBannerPageById(Long pageId) {
        ShtBannerPage page = repository.findOne(pageId);
        if(page != null){
            page.setImageUrl(buildImageBannerUrl(page));
            page.setImageUrlPrv(buildImageBannerUrlPrv(page));
        }
        return page;
    }

    @Override
    public String buildPrivewPageUrl(Long pageId) {
        String baseUrl = environment.getProperty("banner.page.preview.base.url");
        String url = baseUrl + pageId;
        return url;
    }

    @Override
    public String buildPageUrl(Long pageId) {
        String baseUrl = environment.getProperty("banner.page.base.url");
        String url = baseUrl + pageId;
        return url;
    }

    @Override
    public String buildImagePageUrl(Long pageId) {
        String imageUrl = "";
        ShtBannerPage page = getBannerPageById(pageId);
        final String imageServerUrl = environment.getProperty("image.server.url");
        if(page.getImage() != null){
            imageUrl = imageServerUrl + page.getImage().getPath();
        }
        return imageUrl;
    }

    @Override
    public ShtBannerPage getPageByBannerId(Long bannerId) {
        List<ShtBannerPage> bannerPages = repository.findByBannerId(bannerId);
        if(bannerPages.size() > 0){
            return  bannerPages.get(0);
        }
        return  null;
    }

    public String buildImageBannerUrl(ShtBannerPage page){
        String imageUrl = "";
        final String imageServerUrl = environment.getProperty("image.server.url");
        if(page.getImage() != null){
            imageUrl = imageServerUrl + page.getImage().getPath();
        }
        return imageUrl;
    }

    public String buildImageBannerUrlPrv(ShtBannerPage page){
        String imageUrl = "";
        final String imageServerUrl = environment.getProperty("image.server.url");
        if(page.getImage() != null){
            imageUrl = imageServerUrl + page.getImagePrv().getPath();
        }
        return imageUrl;
    }

    @Override
    public ShtBannerPage storeBannerPage(BannerRequest request, MultipartFile pageImage) throws IOException {
        ShtBannerPage bannerPage = new ShtBannerPage();
        bannerPage.setIsActive(false);
        bannerPage.setPageTitle(request.getPageTitle());
        bannerPage.setPageTitlePrv(request.getPageTitle());
        bannerPage.setPageContent(request.getPageContent());
        bannerPage.setPageContentPrv(request.getPageContent());
        bannerPage.setTitleColor(request.getTitleColor());
        bannerPage.setTitleColorPrv(request.getTitleColor());
        bannerPage.setBackgroundColor(request.getBackgroundColor());
        bannerPage.setBackgroundColorPrv(request.getBackgroundColor());
        ShtBannerPage pageSaved = repository.save(bannerPage);

        // save image
        ShrFile originFile = saveBannerPageImage(pageSaved.getPageId(), pageImage);
        pageSaved.setImage(originFile);
        pageSaved.setImagePrv(originFile);
        return repository.save(pageSaved);
    }

    @Override
    public ShtBannerPage updateBannerPageImage(ShtBannerPage bannerPage, MultipartFile pageImage) throws IOException {
        // update image
        ShrFile originFile = saveBannerPageImage(bannerPage.getPageId(), pageImage);
        bannerPage.setImage(originFile);
        bannerPage.setImagePrv(originFile);
        return repository.save(bannerPage);
    }

    @Override
    public ShrFile saveBannerPageImage(Long pageId, MultipartFile imagePage) throws IOException {
        String imageDir = imageBuilder.buildBannerPageImageDir(pageId);
        String imageName = imageBuilder.getName();
        String extension = imageBuilder.getImageFormat();
        if(imagePage != null){
            String contentType = imagePage.getContentType();
            if(contentType.equals(ImageContentTypeEnum.GIF.value)){
                extension = ImageEnum.GIF.value;
            }
            if(contentType.equals(ImageContentTypeEnum.JPG.value)){
                extension = ImageEnum.JPG.value;
            }
            if(contentType.equals(ImageContentTypeEnum.PNG.value)){
                extension = ImageEnum.PNG.value;
            }
        }
        ShrImage image = imageService.saveBannerImage(imageDir, imageName, extension, imagePage);
        return image.getOriginal();
    }
}
