package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmAdminNg;
import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import jp.bo.bocc.enums.AdminActionEnum;
import jp.bo.bocc.repository.AdminNgRepository;
import jp.bo.bocc.repository.criteria.BaseSpecification;
import jp.bo.bocc.repository.criteria.SearchCriteria;
import jp.bo.bocc.service.AdminNgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
@Service
public class AdminNgServiceImpl implements AdminNgService{

    @Autowired
    AdminNgRepository adminNgRepository;

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Override
    @Transactional(readOnly = true)
    public Page<ShmAdminNgDTO> getListAdminNg(ShmAdminNgDTO adminNgDTO, Pageable pageNumber) {
        Page<ShmAdminNgDTO> result = adminNgRepository.getShmAdminNgWithAdminInfo(adminNgDTO,pageNumber);
        return result;
    }

    @Override
    public void updateAdminNg(ShmAdminNg adminNg, long id, ShmAdmin shmAdmin) {

        ShmAdminNg shmAdminNg = this.showAdminNgById(id);
        shmAdminNg.setAdminNgContent(adminNg.getAdminNgContent());
        shmAdminNg.setShmAdminAction(AdminActionEnum.UPDATED);
        shmAdminNg.setShmAdmin(shmAdmin);
        adminNgRepository.save(shmAdminNg);
    }

    @Override
    public void deleteAdminNg(long id, ShmAdmin shmAdmin) {

        ShmAdminNg shmAdminNg = this.showAdminNgById(id);
        shmAdminNg.setDeleteFlag(true);
        shmAdminNg.setShmAdminAction(AdminActionEnum.DELETED);
        shmAdminNg.setShmAdmin(shmAdmin);
        adminNgRepository.save(shmAdminNg);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean findAdminNgContent(String adminNgContent) {
        boolean result = false;

        List<ShmAdminNg> getListAdminNgContent;

        BaseSpecification adminNgSpec1 = new BaseSpecification
                (new SearchCriteria("adminNgContent".trim(), "==", adminNgContent.trim()));
        BaseSpecification adminNgSpec2 = new BaseSpecification
                (new SearchCriteria("deleteFlag", "!=", true));

        getListAdminNgContent = adminNgRepository.findAll(Specifications.where(adminNgSpec1).and(adminNgSpec2));
        if(getListAdminNgContent.size() > 0 ){
            result = true;
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> checkNGContent(String content) {
        List<String> listNg = new ArrayList<>();
        if (content == null || content.trim().isEmpty()) {
            return listNg;
        }
        List<ShmAdminNg> adminNgs = adminNgRepository.findAll();
        if (adminNgs != null && !adminNgs.isEmpty()) {
            for (ShmAdminNg adminNg : adminNgs) {
                if (!StringUtils.isEmpty(adminNg.getAdminNgContent()) && adminNg.getDeleteFlag() == false
                        && content.trim().toUpperCase().contains(adminNg.getAdminNgContent().trim().toUpperCase())) {
                    if(!listNg.contains(adminNg.getAdminNgContent())){
                        listNg.add(adminNg.getAdminNgContent());
                    }
                }
            }
        }
        return  listNg;
    }

    @Override
    public ShmAdminNg showAdminNgById(long adminNgId) {
        ShmAdminNg searchAdminNg;
        searchAdminNg = adminNgRepository.findOne(adminNgId);
        return searchAdminNg;
    }

    @Override
    public void createAdminNg(ShmAdminNg shmAdminNg, ShmAdmin shmAdmin) {
        shmAdminNg.setShmAdmin(shmAdmin);
        adminNgRepository.save(shmAdminNg);
    }


}
