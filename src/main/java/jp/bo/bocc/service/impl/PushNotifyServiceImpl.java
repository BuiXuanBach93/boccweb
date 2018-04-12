package jp.bo.bocc.service.impl;

import com.amazonaws.services.sns.model.CreateTopicResult;
import jp.bo.bocc.controller.web.request.PushRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtPushNotify;
import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.enums.OSTypeEnum;
import jp.bo.bocc.enums.PushNotifyActionEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.jobs.JobHandlePushNotifyFromAdmin;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.repository.PushNotifyRepository;
import jp.bo.bocc.service.AdminService;
import jp.bo.bocc.service.PushNotifyService;
import jp.bo.bocc.service.QaService;
import jp.bo.bocc.service.UserNtfService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * @author DonBach
 */
@Service("pushNotifyService")
public class PushNotifyServiceImpl implements PushNotifyService {

	private final static Logger LOGGER = Logger.getLogger(PushNotifyServiceImpl.class.getName());

	@Autowired
	PushNotifyRepository repository;

	@Autowired
	UserNtfService userNtfService;

	@Autowired
	private SNSMobilePushService snsMobilePushService;

	@Autowired
	private QaService qaService;

	@Autowired
	AdminService adminService;

	@Value("${job.talk.flag}")
	private boolean jobFlag;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	@Qualifier("createTopicResultANDROID")
	public CreateTopicResult createTopicANDROID;

	@Autowired
	@Qualifier("createTopicResultIOS")
	public CreateTopicResult createTopicIOS;

	@Override
	public ShtPushNotify savePush(ShtPushNotify pushNotify) {
		return repository.save(pushNotify);
	}

	@Override
	@Transactional(readOnly = true)
	public ShtPushNotify getPush(Long pushId) {
		return repository.getPushByPushId(pushId);
	}

	@Override
	@Transactional(readOnly = true)
    public Page<ShtPushNotify> getPushNotifys(Pageable pageRequest, String sortType) {
        return repository.getPushNotifys(pageRequest, sortType);
    }

	@Override
	public void deletePush(Long pushId) {
		repository.delete(pushId);
	}

	@Override
	public void pushImmediate(ShtPushNotify pushNotify, ShmAdmin shmAdmin) {
		LOGGER.info("BEGIN: ======================= PushNotifyServiceImpl.pushImmediate." );
		// delete all job of pushNotify records created before
		interruptPushNotifyJob(pushNotify.getPushId());

		// action push immediate
//		List<ShmUser> users = null;
		Long qaId = null;
		if(pushNotify.getPushActionType() == ShtPushNotify.PushActionType.JUST_MESSAGE || pushNotify.getPushActionType() == ShtPushNotify.PushActionType.PUSH_AND_MESSAGE){
			LOGGER.info("======================= PushNotifyServiceImpl.createMessageToAdminBox." );
			ShtQa shtQa = createMessageToAdminBox(pushNotify, shmAdmin);
			qaId = shtQa.getQaId();
		}

		final String msgCont = pushNotify.getPushContent();
		final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
		final PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetailForPushAll(PushMsgDetail.NtfTypeEnum.ADMIN_PUSH_NOTIFY,qaId, msgCont, pushNotify.getPushActionType());

//		if(pushNotify.getPushIos() && ! pushNotify.getPushAndroid()){
//			users = userNtfService.getListIOsUserActive();
//		}
//
//		if(!pushNotify.getPushIos() && pushNotify.getPushAndroid()){
//			users = userNtfService.getListAndroidUserActive();
//		}
//
//		if(pushNotify.getPushAndroid() && pushNotify.getPushIos()){
//			users = userNtfService.getListUserActive();
//		}
//
//		if(pushNotify.getPushActionType() == ShtPushNotify.PushActionType.JUST_MESSAGE || pushNotify.getPushActionType() == ShtPushNotify.PushActionType.PUSH_AND_MESSAGE){
//			if(!CollectionUtils.isEmpty(users)) {
//				for (int i = 0; i < users.size(); i++) {
//					ShmUser shmUser = users.get(i);
//					pushMsg.setMsgContent(pushMsg.getMsgContent());
//					Long qaId = null;
//					if (pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_PUSH) {
//						ShtQa shtQa = createMessageToAdminBox(pushNotify, shmUser, shmAdmin);
//						qaId = shtQa.getQaId();
//					}else {
//						break;
//					}
//				}
//			}
//		}

		if(pushNotify.getPushIos() && ! pushNotify.getPushAndroid() && pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_MESSAGE){
			snsMobilePushService.publishTopic(OSTypeEnum.IOS, pushMsg, createTopicIOS.getTopicArn(),pushMsgDetail);
		}
		if(!pushNotify.getPushIos() && pushNotify.getPushAndroid() && pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_MESSAGE){
			snsMobilePushService.publishTopic(OSTypeEnum.ANDROID, pushMsg, createTopicANDROID.getTopicArn(),pushMsgDetail);
		}
		if(pushNotify.getPushAndroid() && pushNotify.getPushIos() && pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_MESSAGE){
			snsMobilePushService.publishTopic(OSTypeEnum.ANDROID, pushMsg, createTopicANDROID.getTopicArn(),pushMsgDetail);
			snsMobilePushService.publishTopic(OSTypeEnum.IOS, pushMsg, createTopicIOS.getTopicArn(),pushMsgDetail);
		}

		LOGGER.info("END: ======================= PushNotifyServiceImpl.pushImmediate." );
	}

	@Override
	public void pushSetTimer(ShtPushNotify pushNotify, ShmAdmin shmAdmin) {
		// create a cron job to push notify on timer
		createPushNotifyJob(schedulerFactoryBean.getScheduler(),pushNotify, shmAdmin);
	}

	@Override
	public void handleJobPushNotifyOnTimer(Long pushId, Long adminId) {
		// action push immediate
		ShtPushNotify pushNotify = repository.getPushByPushId(pushId);
		ShmAdmin shmAdmin = adminService.getAdminById(adminId);
		if(pushNotify != null){

			// change push status
			pushNotify.setPushStatus(ShtPushNotify.PushStatus.SENT);
			repository.save(pushNotify);

//			List<ShmUser> users = null;
			Long qaId = null;
			if(pushNotify.getPushActionType() == ShtPushNotify.PushActionType.JUST_MESSAGE || pushNotify.getPushActionType() == ShtPushNotify.PushActionType.PUSH_AND_MESSAGE){
				ShtQa shtQa = createMessageToAdminBox(pushNotify, shmAdmin);
				qaId = shtQa.getQaId();
			}
			final String msgCont = pushNotify.getPushContent();
			final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
			PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetailForPushAll(PushMsgDetail.NtfTypeEnum.ADMIN_PUSH_NOTIFY, qaId, msgCont, pushNotify.getPushActionType());
//			if(pushNotify.getPushIos() && ! pushNotify.getPushAndroid()){
//				users = userNtfService.getListIOsUserActive();
//			}
//
//			if(!pushNotify.getPushIos() && pushNotify.getPushAndroid()){
//				users = userNtfService.getListAndroidUserActive();
//			}
//
//			if(pushNotify.getPushAndroid() && pushNotify.getPushIos()){
//				users = userNtfService.getListUserActive();
//			}
//
//			if(pushNotify.getPushActionType() == ShtPushNotify.PushActionType.JUST_MESSAGE || pushNotify.getPushActionType() == ShtPushNotify.PushActionType.PUSH_AND_MESSAGE){
//				if(!CollectionUtils.isEmpty(users)) {
//					for (int i = 0; i < users.size(); i++) {
//						ShmUser shmUser = users.get(i);
//						pushMsg.setMsgContent(pushMsg.getMsgContent());
//						Long qaId = null;
//						if (pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_PUSH) {
//							ShtQa shtQa = createMessageToAdminBox(pushNotify, shmAdmin);
//							qaId = shtQa.getQaId();
//						}else {
//							break;
//						}
//					}
//				}
//			}
			if(pushNotify.getPushIos() && ! pushNotify.getPushAndroid() && pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_MESSAGE){
				snsMobilePushService.publishTopic(OSTypeEnum.IOS, pushMsg, createTopicIOS.getTopicArn(), pushMsgDetail);
			}
			if(!pushNotify.getPushIos() && pushNotify.getPushAndroid() && pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_MESSAGE){
				snsMobilePushService.publishTopic(OSTypeEnum.ANDROID, pushMsg, createTopicANDROID.getTopicArn(), pushMsgDetail);
			}
			if(pushNotify.getPushAndroid() && pushNotify.getPushIos() && pushNotify.getPushActionType() != ShtPushNotify.PushActionType.JUST_MESSAGE){
				snsMobilePushService.publishTopic(OSTypeEnum.ANDROID, pushMsg, createTopicANDROID.getTopicArn(), pushMsgDetail);
				snsMobilePushService.publishTopic(OSTypeEnum.IOS, pushMsg, createTopicIOS.getTopicArn(), pushMsgDetail);
			}
		}
	}

	@Override
	public void updatePush(ShtPushNotify currentPush, PushRequest pushNotify, String adminEmail) {

		currentPush.setPushTitle(pushNotify.getPushTitle());
		currentPush.setPushContent(pushNotify.getPushContent());
		currentPush.setPushContent(pushNotify.getPushContent());
		currentPush.setPushIos(pushNotify.getPushIos());
		currentPush.setPushAndroid(pushNotify.getPushAndroid());

		if(StringUtils.isNotEmpty(pushNotify.getTimerDateStr())){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
			LocalDateTime formatDateTime = LocalDateTime.parse(pushNotify.getTimerDateStr(), formatter);
			currentPush.setTimerDate(formatDateTime);
			currentPush.setSendDate(formatDateTime);
		}

		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.JUST_PUSH.value)){
			currentPush.setPushActionType(ShtPushNotify.PushActionType.JUST_PUSH);
		}
		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.JUST_MESSAGE.value)){
			currentPush.setPushActionType(ShtPushNotify.PushActionType.JUST_MESSAGE);
		}
		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.PUSH_AND_MESSAGE.value)){
			currentPush.setPushActionType(ShtPushNotify.PushActionType.PUSH_AND_MESSAGE);
		}

		if(pushNotify.getPushImmediate() != null && pushNotify.getPushImmediate().equals("IMMEDIATE")){
			currentPush.setPushImmediate(true);
			currentPush.setSendDate(LocalDateTime.now());
			currentPush.setPushStatus(ShtPushNotify.PushStatus.SENT);
		}else{
			currentPush.setPushImmediate(false);
			currentPush.setPushStatus(ShtPushNotify.PushStatus.WAITING);
		}
		if(currentPush.getPushAndroid()){
			Long androidNumber = userNtfService.countAndroidDeviceActive();
			currentPush.setAndroidNumber(androidNumber);
		}else{
			currentPush.setAndroidNumber(0L);
		}
		if(currentPush.getPushIos()){
			Long iosNumber = userNtfService.countIOSDeviceActive();
			currentPush.setIosNumber(iosNumber);
		}else{
			currentPush.setIosNumber(0L);
		}

		ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(adminEmail);
		currentPush.setAdminSender(shmAdmin);
		ShtPushNotify pushSaved = savePush(currentPush);

		try{
			// action with push type
			actionPush(pushSaved, shmAdmin);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void addPush(PushRequest pushNotify, ShmAdmin shmAdmin) {
		LOGGER.info("BEGIN: ======================= PushNotifyServiceImpl.addPush." );
		ShtPushNotify notify = new ShtPushNotify();
		notify.setAdminSender(shmAdmin);
		notify.setPushTitle(pushNotify.getPushTitle());
		notify.setPushContent(pushNotify.getPushContent());
		notify.setPushAndroid(pushNotify.getPushAndroid());
		notify.setPushIos(pushNotify.getPushIos());
		if(StringUtils.isNotEmpty(pushNotify.getTimerDateStr())){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
			LocalDateTime formatDateTime = LocalDateTime.parse(pushNotify.getTimerDateStr(), formatter);
			notify.setTimerDate(formatDateTime);
			notify.setSendDate(formatDateTime);
		}
		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.JUST_PUSH.value)){
			notify.setPushActionType(ShtPushNotify.PushActionType.JUST_PUSH);
		}
		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.JUST_MESSAGE.value)){
			notify.setPushActionType(ShtPushNotify.PushActionType.JUST_MESSAGE);
		}
		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.PUSH_AND_MESSAGE.value)){
			notify.setPushActionType(ShtPushNotify.PushActionType.PUSH_AND_MESSAGE);
		}
		if(pushNotify.getPushImmediate() != null && pushNotify.getPushImmediate().equals("WAIT")){
			notify.setPushImmediate(false);
			notify.setPushStatus(ShtPushNotify.PushStatus.WAITING);
		}else{
			notify.setPushImmediate(true);
			notify.setSendDate(LocalDateTime.now());
			notify.setPushStatus(ShtPushNotify.PushStatus.SENT);
		}
		if(notify.getPushAndroid()){
			Long androidNumber = userNtfService.countAndroidDeviceActive();
			notify.setAndroidNumber(androidNumber);
		}else{
			notify.setAndroidNumber(0L);
		}
		if(notify.getPushIos()){
			Long iosNumber = userNtfService.countIOSDeviceActive();
			notify.setIosNumber(iosNumber);
		}else{
			notify.setIosNumber(0L);
		}
		LOGGER.info("======================= PushNotifyServiceImpl.savePush." );
		ShtPushNotify pushSaved = savePush(notify);

		try{
			// action with push type
			actionPush(pushSaved, shmAdmin);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		LOGGER.info("END: ======================= PushNotifyServiceImpl.addPush." );
	}

	@Override
	public PushRequest editPush(ShtPushNotify currentPush) {
		PushRequest pushNotify = new PushRequest();
		pushNotify.setPushId(currentPush.getPushId());
		pushNotify.setPushTitle(currentPush.getPushTitle());
		pushNotify.setPushContent(currentPush.getPushContent());
		pushNotify.setPushIos(currentPush.getPushIos());
		pushNotify.setPushAndroid(currentPush.getPushAndroid());
		pushNotify.setPushStatus(currentPush.getPushStatus());
		if(currentPush.getTimerDate() != null){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
			String timerDateStr = currentPush.getTimerDate().format(formatter);
			pushNotify.setTimerDateStr(timerDateStr);
		}
		if(currentPush.getPushImmediate()){
			pushNotify.setPushImmediate("IMMEDIATE");
		}else{
			pushNotify.setPushImmediate("WAIT");
		}
		if(currentPush.getPushActionType() == ShtPushNotify.PushActionType.JUST_PUSH){
			pushNotify.setPushActionType(PushNotifyActionEnum.JUST_PUSH.value);
		}
		if(currentPush.getPushActionType() == ShtPushNotify.PushActionType.JUST_MESSAGE){
			pushNotify.setPushActionType(PushNotifyActionEnum.JUST_MESSAGE.value);
		}
		if(currentPush.getPushActionType() == ShtPushNotify.PushActionType.PUSH_AND_MESSAGE){
			pushNotify.setPushActionType(PushNotifyActionEnum.PUSH_AND_MESSAGE.value);
		}
		pushNotify.setAndroidNumber(currentPush.getAndroidNumber());
		pushNotify.setIosNumber(currentPush.getIosNumber());

		Long iosNumber = userNtfService.countIOSDeviceActive();
		Long androidNumber = userNtfService.countAndroidDeviceActive();
		pushNotify.setCurrentAndroidNumber(androidNumber);
		pushNotify.setCurrentIosNumber(iosNumber);

		return pushNotify;
	}

	@Override
	public void suspendPush(Long pushId) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_PUSH_NOTIFY_ID.getValue() + pushId, JobEnum.JOB_PUSH_NOTIFY_GROUP.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addReplicatePush(PushRequest pushNotify, String adminEmail) {
		ShtPushNotify newPush = new ShtPushNotify();
		ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(adminEmail);
		newPush.setAdminSender(shmAdmin);
		newPush.setPushTitle(pushNotify.getPushTitle());
		newPush.setPushContent(pushNotify.getPushContent());
		newPush.setPushIos(pushNotify.getPushIos());
		newPush.setPushAndroid(pushNotify.getPushAndroid());

		if(StringUtils.isNotEmpty(pushNotify.getTimerDateStr())){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
			LocalDateTime formatDateTime = LocalDateTime.parse(pushNotify.getTimerDateStr(), formatter);
			newPush.setTimerDate(formatDateTime);
			newPush.setSendDate(formatDateTime);
		}else{
			newPush.setSendDate(LocalDateTime.now());
		}

		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.JUST_PUSH.value)){
			newPush.setPushActionType(ShtPushNotify.PushActionType.JUST_PUSH);
		}
		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.JUST_MESSAGE.value)){
			newPush.setPushActionType(ShtPushNotify.PushActionType.JUST_MESSAGE);
		}
		if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals(PushNotifyActionEnum.PUSH_AND_MESSAGE.value)){
			newPush.setPushActionType(ShtPushNotify.PushActionType.PUSH_AND_MESSAGE);
		}

		if(pushNotify.getPushImmediate() != null && pushNotify.getPushImmediate().equals("IMMEDIATE")){
			newPush.setPushImmediate(true);
			newPush.setPushStatus(ShtPushNotify.PushStatus.SENT);
			newPush.setSendDate(LocalDateTime.now());
		}else{
			newPush.setPushImmediate(false);
			newPush.setPushStatus(ShtPushNotify.PushStatus.WAITING);
		}
		if(newPush.getPushAndroid()){
			Long androidNumber = userNtfService.countAndroidDeviceActive();
			newPush.setAndroidNumber(androidNumber);
		}else{
			newPush.setAndroidNumber(0L);
		}
		if(newPush.getPushIos()){
			Long iosNumber = userNtfService.countIOSDeviceActive();
			newPush.setIosNumber(iosNumber);
		}else{
			newPush.setIosNumber(0L);
		}
		ShtPushNotify pushSaved = savePush(newPush);

		try{
			// action with push type
			actionPush(pushSaved, shmAdmin);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private void actionPush(ShtPushNotify notify, ShmAdmin shmAdmin){
		if(notify.getPushImmediate()){
			// action push immediate
			pushImmediate(notify, shmAdmin);
		}else{
			// push set timer
			pushSetTimer(notify, shmAdmin);
		}
	}

	private void createPushNotifyJob(Scheduler scheduler, ShtPushNotify pushNotify, ShmAdmin shmAdmin) {
		try {

			// delete all job of pushNotify records created before
			interruptPushNotifyJob(pushNotify.getPushId());
			// create new job
			final org.quartz.JobDetail job = newJob(JobHandlePushNotifyFromAdmin.class).withIdentity(JobEnum.JOB_PUSH_NOTIFY_ID.getValue() + pushNotify.getPushId(), JobEnum.JOB_PUSH_NOTIFY_GROUP.getValue()).build();
			JobDataMap jobDataMap = job.getJobDataMap();
			jobDataMap.put("pushId", "" + pushNotify.getPushId());
			jobDataMap.put("adminId", "" + shmAdmin.getAdminId());

			CronTrigger trigger = newTrigger().withIdentity(JobEnum.JOB_PUSH_NOTIFY_ID.getValue() + pushNotify.getPushId(), JobEnum.JOB_PUSH_NOTIFY_GROUP.getValue()).withSchedule(cronSchedule(ConverterUtils.buildCronExpression(pushNotify.getTimerDate())))
					.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			LOGGER.error("ERROR: " + e.getMessage());
		}
	}

	public void interruptPushNotifyJob(Long pushId) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.deleteJob(JobKey.jobKey(JobEnum.JOB_PUSH_NOTIFY_ID.getValue() + pushId, JobEnum.JOB_PUSH_NOTIFY_GROUP.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ShtQa createMessageToAdminBox(ShtPushNotify pushNotify, ShmAdmin shmAdmin){
		ShtQa qa = new ShtQa();
		qa.setQaTitle(pushNotify.getPushTitle());
		qa.setFirstQaMsg(pushNotify.getPushContent());
		return qaService.createNotifyFromAdminPush(qa, shmAdmin);
	}
}
