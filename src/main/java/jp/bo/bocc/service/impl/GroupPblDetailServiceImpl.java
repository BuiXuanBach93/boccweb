package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtGroupPblDetail;
import jp.bo.bocc.repository.GroupPblDetailRepository;
import jp.bo.bocc.service.GroupPblDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by buixu on 11/17/2017.
 */
@Service
public class GroupPblDetailServiceImpl implements GroupPblDetailService{

    @Autowired
    GroupPblDetailRepository repository;

    @Override
    public List<ShtGroupPblDetail> getGroupDetail(Long groupId) {
        return repository.getGroupDetail(groupId);
    }

    @Override
    public List<ShtGroupPblDetail> getGroupDetailByLegalId(String legalId) {
        return repository.getGroupDetailByLegalId(legalId);
    }

    @Override
    public ShtGroupPblDetail saveDetail(ShtGroupPblDetail pblDetail) {
        return repository.save(pblDetail);
    }
}
