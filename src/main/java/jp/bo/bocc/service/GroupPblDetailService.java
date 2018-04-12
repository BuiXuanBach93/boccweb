package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShtGroupPblDetail;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
public interface GroupPblDetailService {
    public List<ShtGroupPblDetail> getGroupDetail(Long groupId);

    public List<ShtGroupPblDetail> getGroupDetailByLegalId(String legalId);

    public ShtGroupPblDetail saveDetail(ShtGroupPblDetail pblDetail);
}
