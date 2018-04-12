package jp.bo.bocc.jobs;

import jp.bo.bocc.service.BannerService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Created by DonBach on 11/12/2017.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobHandleExpiredBanner implements Job {
    private final static Logger LOGGER = Logger.getLogger(JobHandleExpiredBanner.class.getName());
    @Getter
    @Setter
    private String jobName;

    @Autowired
    BannerService bannerService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("START JOB: EXPIRED BANNER" );
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long bannerId =jobDataMap.getLong("bannerId");
        bannerService.handleJobBannerExpired(bannerId);
        LOGGER.info("END JOB: EXPIRED BANNER" );
    }
}
