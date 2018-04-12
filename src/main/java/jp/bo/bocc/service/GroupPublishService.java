package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShtGroupPublish;
import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
public interface GroupPublishService {
    public Page<ShtGroupPublish> getListGroupPublish(ShtGroupPublish groupPublish, Pageable pageNumber);

    public  ShtGroupPublish saveGroupPublish(ShtGroupPublish groupPublish);

    public ShtGroupPublish getGroupPublishById(Long groupId);

    public List<ShtGroupPublish> getGroupByGroupName(String groupName);
}
