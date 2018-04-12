/**
 * 
 */
package jp.bo.bocc.system.config;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import jp.bo.bocc.entity.ShrSysConfig;
import jp.bo.bocc.enums.JobEnum;
import jp.bo.bocc.enums.PlatformEnum;
import jp.bo.bocc.enums.SysConfigEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.jobs.JobHandleSyncTendToLeaveUser;
import jp.bo.bocc.service.SysConfigService;
import jp.bo.bocc.system.apiconfig.bean.AppContext;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Created by haipv on 3/5/2017.
 *
 */
@Configuration
@ComponentScan(basePackages={"jp.bo.bocc.service", "jp.bo.bocc.jobs", "jp.bo.bocc.push"})
@PropertySource(value = { "classpath:db/db.properties", "classpath:app.properties", "classpath:appEnv.properties"})
@Import({ PersistenceConfig.class, SecurityConfig.class})
@EnableAsync
public class AppConfig {

	@Autowired
	private Environment env;

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;


	@Autowired
	SysConfigService sysConfigService;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public CacheManager cacheManager()
	{
		return new ConcurrentMapCacheManager();
	}
	
	@Bean
	@Profile("!" + Profiles.TEST) //TEST not activated
	public JavaMailSenderImpl javaMailSenderImpl() {
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost(env.getProperty("smtp.host"));
		mailSenderImpl.setPort(env.getProperty("smtp.port", Integer.class));
		mailSenderImpl.setProtocol(env.getProperty("smtp.protocol"));
		mailSenderImpl.setUsername(env.getProperty("smtp.username"));
		mailSenderImpl.setPassword(env.getProperty("smtp.password"));

		Properties javaMailProps = new Properties();
		javaMailProps.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
		javaMailProps.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));

		mailSenderImpl.setJavaMailProperties(javaMailProps);

		return mailSenderImpl;
	}

	@Bean(name = "messageSource")
	public MessageSource configureMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setCacheSeconds(5);
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public ImageBuilder getImageBuilder(){
		ImageBuilder builder = new ImageBuilder();
		builder.setRootDir(env.getProperty("image.root").replace("file:",""));
		builder.setDateFormat(env.getProperty("image.dateformat"));
		builder.setName(env.getProperty("image.name"));
		builder.setThumbWidth(Integer.parseInt(env.getProperty("image.thumb.width")));
		builder.setThumbHeight(Integer.parseInt(env.getProperty("image.thumb.height")));
		builder.setThumbDir(env.getProperty("image.thumb.dir"));
		builder.setImageFormat(env.getProperty("image.format"));
		builder.setImgImportTempDir(env.getProperty("image.directory.temp"));
		return builder;
	}

	@Bean
	public AppContext apiContext() {
		return new AppContext();
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) throws IOException, ParseException {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setDataSource(dataSource);
		scheduler.setQuartzProperties(quartzProperties());
		scheduler.setTransactionManager(transactionManager);
		return scheduler;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public AmazonSNS buildAmazonClient(){
		ClientConfiguration cfg = new ClientConfiguration();
		AWSCredentials creds = new BasicAWSCredentials(env.getProperty("amazon.access.key"), env.getProperty("amazon.secret.key"));
		AmazonSNS sns = AmazonSNSClient.builder()
				.withRegion(env.getProperty("amazon.region"))
				.withClientConfiguration(cfg)
				.withCredentials(new AWSStaticCredentialsProvider(creds))
				.build();
		// Create Platform Application. This corresponds to an app on a

		return sns;
	}

	@Bean
	@Qualifier("createPlatformApplicationResultAPNS")
	public CreatePlatformApplicationResult buildAplatformApplicationAPNS(){
		// platform.
		CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
				env.getProperty("amazon.application.name.apns"), PlatformEnum.APNS, env.getProperty("ios.certificate"), env.getProperty("ios.private.key"));
		return platformApplicationResult;
	}

	@Bean
	@Qualifier("createPlatformApplicationResultAPNS_SANDBOX")
	public CreatePlatformApplicationResult buildAplatformApplicationAPNS_SANDBOX(){
		// platform.
		CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
				env.getProperty("amazon.application.name.apns.sandbox"), PlatformEnum.APNS_SANDBOX, env.getProperty("ios.certificate"), env.getProperty("ios.private.key"));
		return platformApplicationResult;
	}

	@Bean
	@Qualifier("createPlatformApplicationResultGCM")
	public CreatePlatformApplicationResult buildAplatformApplicationGCM(){
		// platform.
		CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
				env.getProperty("amazon.application.name.gcm"), PlatformEnum.GCM, "", env.getProperty("android.server.api.key"));
		return platformApplicationResult;
	}

	private CreatePlatformApplicationResult createPlatformApplication(
			String applicationName, PlatformEnum platform, String principal,
			String credential) {
		CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("PlatformPrincipal", principal);
		attributes.put("PlatformCredential", credential);
		platformApplicationRequest.setAttributes(attributes);
		platformApplicationRequest.setName(applicationName);
		platformApplicationRequest.setPlatform(platform.name());
		return buildAmazonClient().createPlatformApplication(platformApplicationRequest);
	}

	@Bean
	@Qualifier("createTopicResultANDROID")
	public CreateTopicResult createTopicANDROID() {
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(env.getProperty("amazon.topic.all.android"));
		CreateTopicResult createTopicResult = null;
		try {
			createTopicResult = buildAmazonClient().createTopic(createTopicRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return createTopicResult;
	}

	@Bean
	@Qualifier("createTopicResultIOS")
	public CreateTopicResult createTopicIOS() {
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(env.getProperty("amazon.topic.all.ios"));
		CreateTopicResult createTopicResult = null;
		try {
			createTopicResult = buildAmazonClient().createTopic(createTopicRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return createTopicResult;
	}


	@Bean
	@Qualifier("createCronJobSyncData")
	public String createCronJobSyncDataUserTendToLeave() {
		createSyncTendToLeaveUserJob(schedulerFactoryBean.getScheduler());
		return "";
	}


	/*@Bean
	public DatabaseRealtime buildAccessToken() throws Exception{

		// initialize app firebase
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredential(FirebaseCredentials.fromCertificate(classLoader.getResourceAsStream(env.getProperty("firebase.account.json"))))
				.setDatabaseUrl(env.getProperty("db.realtime"))
				.build();
		FirebaseApp.initializeApp(options);

		// generate accesstoken to access firebase's database
		GoogleCredential googleCred = GoogleCredential.fromStream(classLoader.getResourceAsStream(env.getProperty("firebase.account.json")));
		GoogleCredential scoped = googleCred.createScoped(
				Arrays.asList(
						"https://www.googleapis.com/auth/firebase.database",  // or use firebase.database.readonly for read-only access
						"https://www.googleapis.com/auth/userinfo.email"
				)
		);
		scoped.refreshToken();
		String token = scoped.getAccessToken();

		return new DatabaseRealtime(token);
	}

	@Bean
	@Scope("prototype")
	public TaskListener buildTaskListener() {
		return new TaskListener();
	}
*/

	private void createSyncTendToLeaveUserJob(Scheduler scheduler) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			String timerDateStr = "";
			String jobHourStr = env.getProperty("job.sync.user.hour");
			String customTimer = env.getProperty("job.sync.user.custom.time");
			int jobHour = 0;
			if(StringUtils.isNotEmpty(jobHourStr)){
				jobHour = Integer.parseInt(jobHourStr);
			}
			LocalDateTime timerDate = null;
			ShrSysConfig sysConfig = sysConfigService.getSysConfig(SysConfigEnum.SYNC_EXC_DATE_MONTHLY);
			if (sysConfig != null && sysConfig.getSysConfigValue() > 0 && sysConfig.getSysConfigValue() <= 31) {
				int currentDateInt = LocalDateTime.now().getDayOfMonth();
				int configDate = sysConfig.getSysConfigValue();
				LocalDateTime currentDate = LocalDateTime.now();
				int lastDayOfMonth = getLastDayOfCurrentMonth();
				int lastDayOfNextMonth = getLastDayOfNextMonth();
				if(currentDateInt < configDate && configDate <= lastDayOfMonth){
					if(configDate > lastDayOfMonth){
						configDate = lastDayOfMonth;
					}
					timerDate = LocalDateTime.of(currentDate.getYear(), currentDate.getMonthValue(), configDate, jobHour, 0);
					timerDateStr = timerDate.format(formatter);
				}else{
					if(configDate > lastDayOfNextMonth){
						configDate = lastDayOfNextMonth;
					}
					LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
					timerDate = LocalDateTime.of(nextMonth.getYear(), nextMonth.getMonthValue(), configDate, jobHour, 0);
					timerDateStr = timerDate.format(formatter);
				}
			}
			if(StringUtils.isEmpty(timerDateStr)){
				return;
			}
			if(StringUtils.isNotEmpty(customTimer) && Integer.parseInt(customTimer) == 1){
				// job will be execute after 10 minutes - just for test env
				timerDate = LocalDateTime.now().plusMinutes(10);
			}
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

			System.out.println("### SYNC USER BEAN: INTERRUPT JOB TEND TO LEAVE :" + timerDateStr);

		} catch (Exception e) {

		}
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
}
