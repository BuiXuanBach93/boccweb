package jp.bo.bocc.service;

import jp.bo.bocc.controller.web.request.MaintainRequest;
import jp.bo.bocc.controller.web.request.TopicRequest;
import jp.bo.bocc.controller.web.request.ValidVersionRequest;
import jp.bo.bocc.controller.web.request.SyncDateConfigureRequest;
import jp.bo.bocc.entity.ShrSysConfig;
import jp.bo.bocc.enums.SysConfigEnum;
import jp.bo.bocc.system.exception.ServiceException;

/**
 * Created by Namlong on 6/30/2017.
 */
public interface SysConfigService {
    ShrSysConfig getSysConfig(SysConfigEnum sysConfigEnum) throws ServiceException;

    boolean save(SysConfigEnum sysConfigEnum, boolean status, String sysConfigMsg);

    boolean saveSysConfig(MaintainRequest maintainForm);

    boolean saveSysConfig(ValidVersionRequest validVersionRequest);

    boolean saveSysConfig(SyncDateConfigureRequest syncDateConfigureRequest);

    /**
     * Update migration for talk purchase message.
     */
    void updateTalkPurchaseConfig(boolean migrated);

    /**
     * Update migration for talk QA.
     * @param migrated
     */
    void updateQaConfig(boolean migrated);

    boolean saveSysConfig(TopicRequest topicForm);
}
