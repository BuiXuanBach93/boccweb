package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShtSyncExc;
import org.quartz.SchedulerException;

import java.text.ParseException;

/**
 * @author donbach
 */
public interface SyncExcService {

	ShtSyncExc saveSyncExc(ShtSyncExc syncExc);

	ShtSyncExc getSyncExcById(Long syncExcId);

	void handleSyncUserTendToLeave() throws SchedulerException, ParseException;

	void handleSyncUserLeft(Long syncExcId);

	void updateConfigureDate(int newConfigDate);

	void syncUsersManually();
}
