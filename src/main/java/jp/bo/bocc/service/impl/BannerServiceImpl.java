package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.*;
import jp.bo.bocc.enums.ImageContentTypeEnum;
import jp.bo.bocc.enums.ImageEnum;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.jobs.JobHandleActiveBanner;
import jp.bo.bocc.jobs.JobHandleExpiredBanner;
import jp.bo.bocc.repository.BannerRepository;
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
 * Created by buixu on 11/17/2017.
 */
@Service
public class BannerServiceImpl implements BannerService {

    private final static Logger LOGGER = Logger.getLogger(BannerServiceImpl.class.getName());

    private final static Long MAX_BANNER_ACTIVE = 3L;

    @Autowired
    BannerRepository repository;

    @Autowired
    ImageBuilder imageBuilder;

    @Autowired
    ImageService imageService;

    @Autowired
    Environment environment;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public Page<ShtBanner> getBanners(ShtBanner banner, Pageable pageNumber) {
        Page<ShtBanner> listBanner = repository.getListBanner(banner, pageNumber);
        if(CollectionUtils.isNotEmpty(listBanner.getContent())){
            for (ShtBanner bannerItm: listBanner.getContent()) {
                bannerItm.setImageUrl(buildImageBannerUrl(bannerItm));
            }
        }
        return listBanner;
    }

    public String buildImageBannerUrl(ShtBanner banner){
        String imageUrl = "";
        final String imageServerUrl = environment.getProperty("image.server.url");
        if(banner.getImage() != null){
            imageUrl = imageServerUrl + banner.getImage().getPath();
        }
        return imageUrl;
    }

    @Override
    public ShtBanner saveBanner(ShtBanner banner) {
        return repository.save(banner);
    }

    @Override
    public ShtBanner getBannerById(Long bannerId) {
        ShtBanner banner = null;
        try{
            banner = repository.findOne(bannerId);
            if(banner != null){
                banner.setImageUrl(buildImageBannerUrl(banner));
                return banner;
            }
        }catch (Exception ex){
            LOGGER.error("ERROR: " + ex.getMessage());
            throw ex;
        }

        return banner;
    }

    @Override
    public Long countBannerActive() {
        return repository.countBannerActive();
    }

    @Override
    public List<ShtBanner> getBannerByName(String bannerName) {
        return repository.findByBannerName(bannerName);
    }

    @Override
    public List<ShtBanner> getActiveBanner() {
        List<ShtBanner> activeBannerSetting = repository.getMaxActiveBanners(MAX_BANNER_ACTIVE);
        if(CollectionUtils.isNotEmpty(activeBannerSetting)){
            for (ShtBanner bannerItm: activeBannerSetting) {
                bannerItm.setImageUrl(buildImageBannerUrl(bannerItm));
            }
        }
        return activeBannerSetting;
    }

    @Override
    public ShrFile saveBannerImage(Long bannerId, MultipartFile imageBanner) throws IOException {
        String imageDir = imageBuilder.buildBannerImageDir(bannerId);
        String imageName = imageBuilder.getName();
        InputStream inputStream = imageBanner.getInputStream();
        String extension = imageBuilder.getImageFormat();
        if(imageBanner != null){
            String contentType = imageBanner.getContentType();
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
        ShrImage image = imageService.saveBannerImage(imageDir, imageName, extension, imageBanner);
        return image.getOriginal();
    }

    @Override
    public void settingSchedulerBanner(ShtBanner banner) {
        // setting cron job to active and expired banner
        createActiveBannerJob(schedulerFactoryBean.getScheduler(),banner);
        createExpiredBannerJob(schedulerFactoryBean.getScheduler(),banner);
    }

    @Override
    public void handleJobBannerExpired(Long bannerId) {
        ShtBanner banner = getBannerById(bannerId);
        if(banner != null && banner.getBannerStatus() != ShtBanner.BannerStatusEnum.EXPIRED){
            banner.setBannerStatus(ShtBanner.BannerStatusEnum.EXPIRED);
            saveBanner(banner);
        }
    }

    @Override
    public void handleJobBannerActive(Long bannerId) {
        ShtBanner banner = getBannerById(bannerId);
        if(banner != null && banner.getBannerStatus() == ShtBanner.BannerStatusEnum.SUSPENDED){
            // suspend all of banners out of 3 active banners
        //    List<ShtBanner> bannerActives = repository.getActiveBanners();
            List<ShtBanner> bannerMaxActives = repository.getMaxActiveBanners(MAX_BANNER_ACTIVE);
//            if(bannerActives.size() > bannerMaxActives.size()){
//                for (ShtBanner itemActive: bannerActives) {
//                    boolean isOut = true;
//                    for (ShtBanner itemMaxActive: bannerMaxActives) {
//                        if(itemActive.getBannerId().intValue() == itemMaxActive.getBannerId().intValue()){
//                            isOut = false;
//                        }
//                    }
//                    if(isOut){
//                        itemActive.setBannerStatus(ShtBanner.BannerStatusEnum.SUSPENDED);
//                        repository.save(itemActive);
//                    }
//                }
//            }

            // suspend banner has minimum fromDate in list active
            if(bannerMaxActives.size() >= MAX_BANNER_ACTIVE.intValue()){
                LocalDateTime minFromDate = bannerMaxActives.get(0).getFromDate();
                ShtBanner candidateBanner = bannerMaxActives.get(0);
                for (ShtBanner item: bannerMaxActives ) {
                    if(item.getFromDate().isBefore(minFromDate)){
                        candidateBanner = item;
                        minFromDate = item.getFromDate();
                    }
                }
                if(candidateBanner != null){
                    candidateBanner.setBannerStatus(ShtBanner.BannerStatusEnum.SUSPENDED);
                    repository.save(candidateBanner);
                }
            }
            // active current banner
            banner.setBannerStatus(ShtBanner.BannerStatusEnum.ACTIVE);
            saveBanner(banner);
        }
    }

    private void createExpiredBannerJob(Scheduler scheduler, ShtBanner banner) {
        try {

            // delete all expired banner job records created before
            interruptBannerExpiredJob(banner.getBannerId());
            // create new job
            final org.quartz.JobDetail job = newJob(JobHandleExpiredBanner.class).withIdentity(JobEnum.JOB_BANNER_EXPIRED_ID.getValue() + banner.getBannerId(), JobEnum.JOB_BANNER_EXPIRED_GROUP.getValue()).build();
            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.put("bannerId", "" + banner.getBannerId());

            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_BANNER_EXPIRED_ID.getValue() + banner.getBannerId(), JobEnum.JOB_BANNER_EXPIRED_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(banner.getToDate())))
                    .build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    private void createActiveBannerJob(Scheduler scheduler, ShtBanner banner) {
        try {

            // delete all active banner job records created before
            interruptBannerActiveJob(banner.getBannerId());
            // create new job
            final org.quartz.JobDetail job = newJob(JobHandleActiveBanner.class).withIdentity(JobEnum.JOB_BANNER_ACTIVE_ID.getValue() + banner.getBannerId(), JobEnum.JOB_BANNER_ACTIVE_GROUP.getValue()).build();
            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.put("bannerId", "" + banner.getBannerId());

            CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_BANNER_ACTIVE_ID.getValue() + banner.getBannerId(), JobEnum.JOB_BANNER_ACTIVE_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(banner.getFromDate())))
                    .build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    public void interruptBannerExpiredJob(Long bannerId) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_BANNER_EXPIRED_ID.getValue() + bannerId, JobEnum.JOB_BANNER_EXPIRED_GROUP.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interruptBannerActiveJob(Long bannerId) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_BANNER_ACTIVE_ID.getValue() + bannerId, JobEnum.JOB_BANNER_ACTIVE_GROUP.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
