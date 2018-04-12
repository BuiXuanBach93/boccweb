package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtKpiStorage;
import jp.bo.bocc.helper.GGAUtils;
import jp.bo.bocc.repository.KpiStorageRepository;
import jp.bo.bocc.repository.PostRepository;
import jp.bo.bocc.repository.UserRepository;
import jp.bo.bocc.service.KpiStorageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by buixu on 12/19/2017.
 */
@Service
public class KpiStorageServiceImpl implements KpiStorageService {

    @Autowired
    KpiStorageRepository kpiStorageRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ShtKpiStorage saveKpi(ShtKpiStorage kpiStorage) {
        return kpiStorageRepository.save(kpiStorage);
    }

    @Override
    public ShtKpiStorage getKpiStorageById(Long kpiId) {
        return kpiStorageRepository.findOne(kpiId);
    }

    @Override
    public List<ShtKpiStorage> getByQueryTime(String queryTime) {
        return kpiStorageRepository.findByQueryTime(queryTime);
    }

    @Override
    public ShtKpiStorage getKpiByDay(String day) {
        ShtKpiStorage dailyResponse = new ShtKpiStorage();
        dailyResponse.setQueryTime(day);
        dailyResponse.setKpiType(ShtKpiStorage.KpiTypeEmum.DAILY);
        List<ShtKpiStorage> kpiFromDB = kpiStorageRepository.findByQueryTime(day);
        if(StringUtils.isNotEmpty(day) && day.length() == "yyyy/MM/dd".length()){
            DateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
            String currentDate = formater.format(new Date());
            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(new Date());
            tempCalendar.add(Calendar.DATE, -1);
            String yesterday = formater.format(tempCalendar.getTime()).toUpperCase();
            if(currentDate.equals(day) || yesterday.equals(day)){
                // collect new data
                dailyResponse = queryKpiDailyByDay(day);
                if(CollectionUtils.isNotEmpty(kpiFromDB)){
                    for (ShtKpiStorage kpi: kpiFromDB) {
                        kpi.setQueryTime(day);
                        kpi.setKpiType(ShtKpiStorage.KpiTypeEmum.DAILY);
                        kpi.setRegNumber(dailyResponse.getRegNumber());
                        kpi.setDlNumber(dailyResponse.getDlNumber());
                        kpi.setRegRatio(dailyResponse.getRegRatio());
                        kpi.setOwnerNumber(dailyResponse.getOwnerNumber());
                        kpi.setPostRatio(dailyResponse.getPostRatio());
                        kpi.setPostNumber(dailyResponse.getPostNumber());
                        kpi.setPartnerNumber(dailyResponse.getPartnerNumber());
                        kpi.setTransNumber(dailyResponse.getTransNumber());
                        kpi.setRegRatio(dailyResponse.getTransRatio());
                        kpi.setActorNumber(dailyResponse.getActorNumber());
                        kpiStorageRepository.save(kpi);
                    }
                }else{
                    kpiStorageRepository.save(dailyResponse);
                }
            }else {
                // get data from kpi storage
                if(CollectionUtils.isNotEmpty(kpiFromDB)){
                    return  kpiFromDB.get(0);
                }else{
                    // if no data selected, insert new data to db
                    dailyResponse = queryKpiDailyByDay(day);
                    kpiStorageRepository.save(dailyResponse);
                }
            }
        }

        return dailyResponse;
    }

    private ShtKpiStorage queryKpiDailyByDay(String day){
        ShtKpiStorage dailyResponse = new ShtKpiStorage();
        dailyResponse.setQueryTime(day);
        dailyResponse.setKpiType(ShtKpiStorage.KpiTypeEmum.DAILY);
        int dlNumber = GGAUtils.countNewUser(day.replace("/","-"),day.replace("/","-"));
        dailyResponse.setDlNumber(new Long(dlNumber));
        dailyResponse.setRegNumber(userRepository.getUserNumberRegisByDay(day));

        if(dailyResponse.getDlNumber().intValue() > 0){
            double regisRatio = (double) dailyResponse.getRegNumber()/dailyResponse.getDlNumber();
            double regisRatioRound =  100 * (double) Math.round(regisRatio * 10000) / 10000;
            dailyResponse.setRegRatio(regisRatioRound);
        }else{
            dailyResponse.setRegRatio(new Double(0));
        }
        dailyResponse.setPostNumber(postRepository.getPostNumberPerDay(day));
        dailyResponse.setOwnerNumber(postRepository.getOwnerPostPerDay(day));
        dailyResponse.setActorNumber(postRepository.getActorPerDay(day));
        if(dailyResponse.getOwnerNumber().intValue() > 0){
            double postRatio = (double) dailyResponse.getPostNumber()/dailyResponse.getOwnerNumber();
            double postRatioRound = (double) Math.round(postRatio * 100) / 100;
            dailyResponse.setPostRatio(postRatioRound);
        }else{
            dailyResponse.setPostRatio(new Double(0));
        }

        dailyResponse.setPartnerNumber(postRepository.getPartnerPerDay(day));
        dailyResponse.setTransNumber(postRepository.getTransPerDay(day));
        if(dailyResponse.getPartnerNumber().intValue() > 0){
            double transRatio = (double) dailyResponse.getTransNumber()/dailyResponse.getPartnerNumber();
            double transRatioRound = (double) Math.round(transRatio * 100) / 100;
            dailyResponse.setTransRatio(transRatioRound);
        }else{
            dailyResponse.setTransRatio(new Double(0));
        }
        return dailyResponse;
    }

    private ShtKpiStorage queryKpiDailyByMonth(String month) throws ParseException {
        ShtKpiStorage monthlyResponse = new ShtKpiStorage();
        monthlyResponse.setQueryTime(month);
        monthlyResponse.setKpiType(ShtKpiStorage.KpiTypeEmum.MONTHLY);
        String firstDayOfMonth = month + "/01";
        DateFormat formaterYYMM = new SimpleDateFormat("yyyy/MM/dd");
        Date convertedDate = formaterYYMM.parse(firstDayOfMonth);
        Calendar c = Calendar.getInstance();
        c.setTime(convertedDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDayOfMonth = formaterYYMM.format(c.getTime());

        int dlNumber = GGAUtils.countNewUser(firstDayOfMonth.replace("/","-"), lastDayOfMonth.replace("/","-"));
        monthlyResponse.setDlNumber(new Long(dlNumber));
        monthlyResponse.setRegNumber(userRepository.getUserNumberRegisByMonth(month));

        if(monthlyResponse.getDlNumber().intValue() > 0){
            double regisRatio = (double) monthlyResponse.getRegNumber()/monthlyResponse.getDlNumber();
            double regisRatioRound =  100 * (double) Math.round(regisRatio * 10000) / 10000;
            monthlyResponse.setRegRatio(regisRatioRound);
        }else{
            monthlyResponse.setRegRatio(new Double(0));
        }
        monthlyResponse.setPostNumber(postRepository.getPostNumberByMonth(month));
        monthlyResponse.setOwnerNumber(postRepository.getOwnerPostByMonth(month));
        monthlyResponse.setActorNumber(postRepository.getActorPerMonth(month));
        if(monthlyResponse.getOwnerNumber().intValue() > 0){
            double postRatio = (double) monthlyResponse.getPostNumber()/monthlyResponse.getOwnerNumber();
            double postRatioRound = (double) Math.round(postRatio * 100) / 100;
            monthlyResponse.setPostRatio(postRatioRound);
        }else{
            monthlyResponse.setPostRatio(new Double(0));
        }

        monthlyResponse.setPartnerNumber(postRepository.getPartnerByMonth(month));
        monthlyResponse.setTransNumber(postRepository.getTransByMonth(month));
        if(monthlyResponse.getPartnerNumber().intValue() > 0){
            double transRatio = (double) monthlyResponse.getTransNumber()/monthlyResponse.getPartnerNumber();
            double transRatioRound = (double) Math.round(transRatio * 100) / 100;
            monthlyResponse.setTransRatio(transRatioRound);
        }else{
            monthlyResponse.setTransRatio(new Double(0));
        }
        return monthlyResponse;
    }

    @Override
    public ShtKpiStorage getKpiResponseByMonth(String month) throws ParseException {
        ShtKpiStorage monthlyResponse = new ShtKpiStorage();
        monthlyResponse.setQueryTime(month);
        monthlyResponse.setKpiType(ShtKpiStorage.KpiTypeEmum.MONTHLY);
        List<ShtKpiStorage> kpiFromDB = kpiStorageRepository.findByQueryTime(month);
        if(StringUtils.isNotEmpty(month) && month.length() == "yyyy/MM".length()){
            DateFormat formater = new SimpleDateFormat("yyyy/MM");
            String currentDate = formater.format(new Date());
            if(currentDate.equals(month)){
                // collect new data
                monthlyResponse = queryKpiDailyByMonth(month);
                if(CollectionUtils.isNotEmpty(kpiFromDB)){
                    for (ShtKpiStorage kpi: kpiFromDB) {
                        kpi.setQueryTime(month);
                        kpi.setKpiType(ShtKpiStorage.KpiTypeEmum.MONTHLY);
                        kpi.setRegNumber(monthlyResponse.getRegNumber());
                        kpi.setDlNumber(monthlyResponse.getDlNumber());
                        kpi.setRegRatio(monthlyResponse.getRegRatio());
                        kpi.setOwnerNumber(monthlyResponse.getOwnerNumber());
                        kpi.setPostRatio(monthlyResponse.getPostRatio());
                        kpi.setPostNumber(monthlyResponse.getPostNumber());
                        kpi.setPartnerNumber(monthlyResponse.getPartnerNumber());
                        kpi.setTransNumber(monthlyResponse.getTransNumber());
                        kpi.setRegRatio(monthlyResponse.getTransRatio());
                        kpi.setActorNumber(monthlyResponse.getActorNumber());
                        kpiStorageRepository.save(kpi);
                    }
                }else{
                    kpiStorageRepository.save(monthlyResponse);
                }
            }else {
                // get data from kpi storage
                if(CollectionUtils.isNotEmpty(kpiFromDB)){
                    return  kpiFromDB.get(0);
                }else{
                    // if no data selected, insert new data to db
                    monthlyResponse = queryKpiDailyByMonth(month);
                    kpiStorageRepository.save(monthlyResponse);
                }
            }
        }
        return monthlyResponse;
    }

    @Override
    @Async
    public void syncKpiData() throws ParseException {
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();

        // get start date
        String fromDate = userRepository.getMinRegDate();
        // get end date
        DateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = formater.format(new Date());
        try {
            beginCalendar.setTime(formater.parse(fromDate));
            finishCalendar.setTime(formater.parse(currentDate));
            finishCalendar.add(Calendar.DATE, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // sync data kpi daily

        while (beginCalendar.before(finishCalendar)) {
            // add one day to date per loop
            String day = formater.format(beginCalendar.getTime()).toUpperCase();
            getKpiByDay(day);
            beginCalendar.add(Calendar.DATE, 1);
        }

        DateFormat formaterYYMM = new SimpleDateFormat("yyyy/MM");
        try {
            beginCalendar.setTime(formaterYYMM.parse(fromDate.substring(0,7)));
            finishCalendar.setTime(formaterYYMM.parse(currentDate.substring(0,7)));
            finishCalendar.add(Calendar.MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // sync data kpi monthly
        while (beginCalendar.before(finishCalendar)) {
            // add one month to date per loop
            String month = formaterYYMM.format(beginCalendar.getTime()).toUpperCase();
            getKpiResponseByMonth(month);
            beginCalendar.add(Calendar.MONTH, 1);
        }
    }
}
