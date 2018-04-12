package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtGroupPublish;
import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import jp.bo.bocc.repository.GroupPublishRepository;
import jp.bo.bocc.service.GroupPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by buixu on 11/17/2017.
 */
@Service
public class GroupPublishServiceImpl implements GroupPublishService {

    @Autowired
    GroupPublishRepository groupPublishRepository;

    @Override
    public Page<ShtGroupPublish> getListGroupPublish(ShtGroupPublish groupPublish, Pageable pageNumber) {
        Page<ShtGroupPublish> result = groupPublishRepository.getListGroupPubish(groupPublish, pageNumber);
        return result;
    }

    @Override
    public ShtGroupPublish saveGroupPublish(ShtGroupPublish groupPublish) {
        return groupPublishRepository.save(groupPublish);
    }

    @Override
    public ShtGroupPublish getGroupPublishById(Long groupId) {
        return groupPublishRepository.getOne(groupId);
    }

    @Override
    public List<ShtGroupPublish> getGroupByGroupName(String groupName) {
        return groupPublishRepository.findByGroupName(groupName);
    }
}
