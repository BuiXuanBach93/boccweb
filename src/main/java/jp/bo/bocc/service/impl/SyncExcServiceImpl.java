package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShrSysConfig;
import jp.bo.bocc.entity.ShtSyncExc;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.enums.SysConfigEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.jobs.JobHandleSyncTendToLeaveUser;
import jp.bo.bocc.jobs.JobHandleSyncLeftUser;
import jp.bo.bocc.service.SyncExcService;
import jp.bo.bocc.repository.SyncExcRepository;
import jp.bo.bocc.service.SysConfigService;
import jp.bo.bocc.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import jp.bo.bocc.entity.dto.ShmUserBsDTO;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * @author bachbx
 */
@Service
public class SyncExcServiceImpl implements SyncExcService {

	private final static Logger LOGGER = Logger.getLogger(SyncExcServiceImpl.class.getName());

	@Autowired
	SyncExcRepository syncExcRepository;

	@Autowired
	UserService userService;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	SysConfigService sysConfigService;

	@Value("${job.user.left.wait.day}")
	private int userLeftWaitDay;

	@Value("${job.sync.user.hour}")
	private int jobHour;

	@Value("${job.sync.user.custom.time}")
	private int customTimer;

	@Override
	public ShtSyncExc saveSyncExc(ShtSyncExc syncExc) {
		return syncExcRepository.save(syncExc);
	}

	@Override
	public ShtSyncExc getSyncExcById(Long syncExcId) {
		return syncExcRepository.findOne(syncExcId);
	}

    @Override
    public void handleSyncUserTendToLeave() throws SchedulerException, ParseException {

		Long syncExcId = handleSyncTendToLeaveLogic();
		// create SYNC_LEFT cron job
		createSyncLeftjob(syncExcId);
		// create SYNC_TEND_TO_LEAVE cron job
		createSyncTendToLeaveJob();
	}

	@Override
	public void handleSyncUserLeft(Long syncExcId) {
		// update SyncExc record
		ShtSyncExc shtSyncExc = getSyncExcById(syncExcId);
		shtSyncExc.setStatus(ShtSyncExc.SyncStatus.DONE);
		saveSyncExc(shtSyncExc);

		// handle left logic for each user
		List<ShmUser> tendToLeaveUsers = userService.getTendToLeaveUserBySyncExcId(syncExcId);
		for (ShmUser shmUser: tendToLeaveUsers) {
			userService.handleSyncUserLeft(shmUser);
		}
	}

	@Override
	public void updateConfigureDate(int newConfigDate) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		ShrSysConfig sysConfig = sysConfigService.getSysConfig(SysConfigEnum.SYNC_EXC_DATE_MONTHLY);
		int currentConfigDate = sysConfig.getSysConfigValue();
		LocalDateTime currentTimerDate = getTimeStampByConfigDate(currentConfigDate);
		String currentTimerDateStr = currentTimerDate.format(formatter);
		try {
			// interrupt current job
			boolean isExists = scheduler.checkExists(JobKey.jobKey(JobEnum.JOB_SYNC_TEND_TO_LEAVE_ID.getValue() + currentTimerDateStr, JobEnum.JOB_SYNC_TEND_TO_LEAVE_GROUP.getValue()));
			if(isExists){
				interruptSyncTendToLeaveJob(currentTimerDateStr);
			}
			// create new job
			LocalDateTime newTimerDate = getTimeStampByConfigDate(newConfigDate);
			String newTimerDateStr = newTimerDate.format(formatter);
			if(customTimer == 1){
				newTimerDate = LocalDateTime.now().plusMinutes(10);
			}
			final org.quartz.JobDetail job = newJob(JobHandleSyncTendToLeaveUser.class).withIdentity(JobEnum.JOB_SYNC_TEND_TO_LEAVE_ID.getValue() + newTimerDateStr, JobEnum.JOB_SYNC_TEND_TO_LEAVE_GROUP.getValue()).build();
			JobDataMap jobDataMap = job.getJobDataMap();
			CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_SYNC_TEND_TO_LEAVE_ID.getValue() + newTimerDateStr, JobEnum.JOB_SYNC_TEND_TO_LEAVE_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(newTimerDate)))
					.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void syncUsersManually() {

		// check is existing job tend to leave in next month, if not have to re create
		createSyncTendToLeaveJob();

		// tend to leave missing users on last TEND_TO_LEAVE JOB
		Long syncExcId = handleSyncManuallyLogic();
		if(syncExcId != null){
			createSyncLeftjob(syncExcId);
		}

		// left messing user on last LEFT JOB
		handleMissingSyncUserLeft();
	}

	public void handleMissingSyncUserLeft() {
		// handle left logic for each user
		List<ShmUser> tendToLeaveUsers = userService.getMissingLeftSyncUsersBeforeNow();
		for (ShmUser shmUser: tendToLeaveUsers) {
			userService.handleSyncUserLeft(shmUser);
		}
	}

	Long handleSyncManuallyLogic(){
		String lastSyncDate = syncExcRepository.getLastDateSyncData();
		Long lastSyncId = syncExcRepository.getLastSyncExcId();
		ShtSyncExc lastSyncExc = null;
		if(lastSyncId != null){
			lastSyncExc = syncExcRepository.findOne(lastSyncId);
		}
		List<Long> userIds = null;
		if(StringUtils.isEmpty(lastSyncDate)){
			userIds = syncExcRepository.getUserDataFromBSSystemFirstTime();
		}else {
			userIds = syncExcRepository.getUserDataFromBSSystem(lastSyncExc);
		}
		if(CollectionUtils.isEmpty(userIds)){
			return null;
		}
		// create new SyncExc record
		ShtSyncExc shtSyncExc = new ShtSyncExc();
		shtSyncExc.setTendToLeaveDate(LocalDateTime.now());
		LocalDateTime leftDate = getFirstDayOfNextMonth();
		shtSyncExc.setLeftDate(leftDate);
		shtSyncExc.setStatus(ShtSyncExc.SyncStatus.START);
		if(lastSyncExc != null && lastSyncExc.getQueryFromDate() != null && lastSyncExc.getQueryToDate() != null){
			shtSyncExc.setQueryFromDate(lastSyncExc.getQueryFromDate());
			shtSyncExc.setQueryToDate(lastSyncExc.getQueryToDate());
		}else{
			shtSyncExc.setQueryFromDate(LocalDateTime.of(2000,1,1,0,0));
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime fistDayOfMonth = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);
			shtSyncExc.setQueryToDate(fistDayOfMonth);
		}
		ShtSyncExc shtSyncExcSaved = saveSyncExc(shtSyncExc);
		// handle tend to leave for each user
		for (Long userId : userIds ) {
			ShmUser shmUser = userService.findUserById(userId);
			if(shmUser != null){
				userService.handleSyncUserTendToLeave(shmUser, shtSyncExcSaved.getSyncExcId());
			}
		}
		// update sync data
		shtSyncExcSaved.setUserNumber(new Long(userIds.size()));
		saveSyncExc(shtSyncExcSaved);

		 return shtSyncExcSaved.getSyncExcId();
	}

	Long handleSyncTendToLeaveLogic(){
		String lastSyncDate = syncExcRepository.getLastDateSyncData();
		// create new SyncExc record
		ShtSyncExc shtSyncExc = new ShtSyncExc();
		if(StringUtils.isEmpty(lastSyncDate)){
			shtSyncExc.setQueryFromDate(LocalDateTime.of(2000,1,1,0,0));
		}else{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime fromDate = LocalDateTime.from(LocalDate.parse(lastSyncDate, formatter).atStartOfDay());
			shtSyncExc.setQueryFromDate(fromDate);
		}
		shtSyncExc.setQueryToDate(LocalDateTime.now());
		shtSyncExc.setTendToLeaveDate(LocalDateTime.now());
		LocalDateTime leftDate = getFirstDayOfNextMonth();
		shtSyncExc.setLeftDate(leftDate);
		shtSyncExc.setStatus(ShtSyncExc.SyncStatus.START);
		ShtSyncExc shtSyncExcSaved = saveSyncExc(shtSyncExc);

		List<Long> userIds = syncExcRepository.getUserDataFromBSSystem(lastSyncDate);

		// handle tend to leave for each user
		for (Long userId : userIds ) {
			ShmUser shmUser = userService.findUserById(userId);
			if(shmUser != null){
				userService.handleSyncUserTendToLeave(shmUser, shtSyncExcSaved.getSyncExcId());
			}
		}
		// update sync data
		shtSyncExcSaved.setUserNumber(new Long(userIds.size()));
		saveSyncExc(shtSyncExcSaved);

		return shtSyncExcSaved.getSyncExcId();
	}

	LocalDateTime getTimeStampByConfigDate(int configDate){
		LocalDateTime currentTimerDate = null;
		int currentDateInt = LocalDateTime.now().getDayOfMonth();
		LocalDateTime currentDate = LocalDateTime.now();
		int lastDayOfMonth = getLastDayOfCurrentMonth();
		int lastDayOfNextMonth = getLastDayOfNextMonth();
		if(currentDateInt < configDate && configDate <= lastDayOfMonth){
			if(configDate > lastDayOfMonth){
				configDate = lastDayOfMonth;
			}
			currentTimerDate = LocalDateTime.of(currentDate.getYear(), currentDate.getMonthValue(), configDate, jobHour, 0);
		}else{
			if(configDate > lastDayOfNextMonth){
				configDate = lastDayOfNextMonth;
			}
			LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
			currentTimerDate = LocalDateTime.of(nextMonth.getYear(), nextMonth.getMonthValue(), configDate, jobHour, 0);
		}
		return  currentTimerDate;
	}

	int getLastDayOfCurrentMonth(){
		LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
		int currentDay = nextMonth.getDayOfMonth();
		return nextMonth.plusDays(-currentDay).getDayOfMonth();
	}
	int getLastDayOfNextMonth(){
		LocalDateTime next2Month = LocalDateTime.now().plusMonths(2);
		int currentDay = next2Month.getDayOfMonth();
		return next2Month.plusDays(-currentDay).getDayOfMonth();
	}

	LocalDateTime getFirstDayOfNextMonth(){
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextMonth = now.plusMonths(1);
		LocalDateTime firstDay = LocalDateTime.of(now.getYear(), nextMonth.getMonthValue(), 1, jobHour, 0);
		return firstDay;
	}

	public void interruptSyncTendToLeaveJob(String timerStr) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_SYNC_TEND_TO_LEAVE_ID.getValue() + timerStr, JobEnum.JOB_SYNC_TEND_TO_LEAVE_GROUP.getValue()));
			LOGGER.info("### SYNC USER: INTERRUPT JOB TEND TO LEAVE : " + timerStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void createSyncLeftjob(Long syncExcId) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		final org.quartz.JobDetail job = newJob(JobHandleSyncLeftUser.class).withIdentity(JobEnum.JOB_SYNC_LEFT_ID.getValue() + syncExcId.intValue(), JobEnum.JOB_SYNC_LEFT_GROUP.getValue()).build();
		JobDataMap jobDataMap = job.getJobDataMap();
		jobDataMap.put("syncExcId", syncExcId.toString());
		LocalDateTime leftDate = getFirstDayOfNextMonth();
		if(customTimer == 1){
			// job will be execute after 10 minute  - just for test evn
			leftDate = LocalDateTime.now().plusMinutes(10);
		}
		try {
			CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_SYNC_LEFT_ID.getValue() + syncExcId.intValue(), JobEnum.JOB_SYNC_LEFT_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(leftDate)))
				.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			LOGGER.info("### SYNC USER: CREATE LEFT JOB : " + leftDate.format(formatter));
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	void createSyncTendToLeaveJob(){
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		String timerDateStr = "";
		LocalDateTime timerDate = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		ShrSysConfig sysConfig = sysConfigService.getSysConfig(SysConfigEnum.SYNC_EXC_DATE_MONTHLY);
		if (sysConfig != null && sysConfig.getSysConfigValue() > 0 && sysConfig.getSysConfigValue() <= 31) {
			int configDate = sysConfig.getSysConfigValue();
			int lastDayOfNextMonth = getLastDayOfNextMonth();
			if(configDate > lastDayOfNextMonth){
				configDate = lastDayOfNextMonth;
			}
			LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
			timerDate = LocalDateTime.of(nextMonth.getYear(), nextMonth.getMonthValue(), configDate, jobHour, 0);
			timerDateStr = timerDate.format(formatter);
		}

		if(StringUtils.isEmpty(timerDateStr)){
			return;
		}
		if(customTimer == 1){
			// job will be execute after 10 minutes - just for test evn
			timerDate = LocalDateTime.now().plusMinutes(10);
		}
		try {
			boolean isExists = scheduler.checkExists(JobKey.jobKey(JobEnum.JOB_SYNC_TEND_TO_LEAVE_ID.getValue() + timerDateStr, JobEnum.JOB_SYNC_TEND_TO_LEAVE_GROUP.getValue()));
			if(isExists){
				return;
			}
			final org.quartz.JobDetail job = newJob(JobHandleSyncTendToLeaveUser.class).withIdentity(JobEnum.JOB_SYNC_TEND_TO_LEAVE_ID.getValue() + timerDateStr, JobEnum.JOB_SYNC_TEND_TO_LEAVE_GROUP.getValue()).build();
			JobDataMap jobDataMap = job.getJobDataMap();
			CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_SYNC_TEND_TO_LEAVE_ID.getValue() + timerDateStr, JobEnum.JOB_SYNC_TEND_TO_LEAVE_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(timerDate)))
					.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();

			LOGGER.info("### SYNC USER: CREATE TEND TO LEAVE JOB : " + timerDateStr);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
