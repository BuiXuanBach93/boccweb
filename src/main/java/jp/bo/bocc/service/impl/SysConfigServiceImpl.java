package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.MaintainRequest;
import jp.bo.bocc.controller.web.request.SyncDateConfigureRequest;
import jp.bo.bocc.controller.web.request.TopicRequest;
import jp.bo.bocc.controller.web.request.ValidVersionRequest;
import jp.bo.bocc.entity.ShrSysConfig;
import jp.bo.bocc.enums.SysConfigEnum;
import jp.bo.bocc.repository.SysConfigRepository;
import jp.bo.bocc.service.SysConfigService;
import jp.bo.bocc.system.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Namlong on 6/30/2017.
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Autowired
    SysConfigRepository sysConfigRepository;

    @Override
    public boolean save(SysConfigEnum sysConfigEnum, boolean status, String sysConfigMsg) {
        ShrSysConfig sysConfig = sysConfigRepository.findBySysConfigCode(SysConfigEnum.MAINTAIN_MODE.toString());
        if (sysConfig != null)
            return true;
        sysConfig.setSysConfigValue(status == true ? 1 : 0);
         sysConfigRepository.save(sysConfig);
        return status;
    }

    @Override
    public ShrSysConfig getSysConfig(SysConfigEnum sysConfigEnum) throws ServiceException {
        ShrSysConfig sysConfig = sysConfigRepository.findBySysConfigCode(sysConfigEnum.toString());
        return sysConfig;
    }

    @Override
    public boolean saveSysConfig(MaintainRequest maintainForm) {
        ShrSysConfig sysConfig = sysConfigRepository.findBySysConfigCode(SysConfigEnum.MAINTAIN_MODE.toString());
        sysConfig.setSysConfigValue(maintainForm.isMaintain() == true ? 1 : 0);
        sysConfig.setSysConfigMsg(maintainForm.getSysConfigMsg());
        sysConfigRepository.save(sysConfig);
        return true;
    }

    @Override
    public boolean saveSysConfig(ValidVersionRequest validVersionRequest) {
        ShrSysConfig sysConfig = sysConfigRepository.findBySysConfigCode(SysConfigEnum.VALID_VERSION.toString());
        sysConfig.setSysConfigValues(validVersionRequest.getSysConfigValues());
        sysConfig.setSysConfigMsg(validVersionRequest.getSysConfigMsg());
        sysConfigRepository.save(sysConfig);
        return true;
    }

    @Override
    public boolean saveSysConfig(SyncDateConfigureRequest syncDateConfigureRequest) {
        ShrSysConfig sysConfig = sysConfigRepository.findBySysConfigCode(SysConfigEnum.SYNC_EXC_DATE_MONTHLY.toString());
        sysConfig.setSysConfigValue(syncDateConfigureRequest.getDayOfMonth().intValue());
        sysConfigRepository.save(sysConfig);
        return true;
    }

    @Override
    public void updateTalkPurchaseConfig(boolean migrated) {
        ShrSysConfig sysConfig = getSysConfig(SysConfigEnum.MAINTAIN_MODE.MIGRATION_MSG_TASLK_PURCHASE);
        sysConfig.setSysConfigValue(1);
        sysConfigRepository.save(sysConfig);
    }

    @Override
    public void updateQaConfig(boolean migrated) {
        ShrSysConfig sysConfig = getSysConfig(SysConfigEnum.MAINTAIN_MODE.MIGRATION_MSG_QA);
        sysConfig.setSysConfigValue(1);
        sysConfigRepository.save(sysConfig);
    }

    @Override
    public boolean saveSysConfig(TopicRequest topicForm) {
        ShrSysConfig sysConfig = sysConfigRepository.findBySysConfigCode(SysConfigEnum.PUSH_SUBSCRIBE_INTO_TOPIC_ALL.toString());
        sysConfig.setSysConfigValue(topicForm.isSubscribeTopicAll() == true ? 1 : 0);
        sysConfigRepository.save(sysConfig);
        return true;
    }
}
