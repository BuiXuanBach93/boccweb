package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShtKpiStorage;

import java.text.ParseException;
import java.util.List;

/**
 * Created by buixu on 12/19/2017.
 */
public interface KpiStorageService {

    ShtKpiStorage saveKpi(ShtKpiStorage kpiStorage);

    ShtKpiStorage getKpiStorageById(Long kpiId);

    List<ShtKpiStorage> getByQueryTime(String queryTime);

    ShtKpiStorage getKpiByDay(String day);

    ShtKpiStorage getKpiResponseByMonth(String month) throws ParseException;

    void syncKpiData() throws ParseException;
}
