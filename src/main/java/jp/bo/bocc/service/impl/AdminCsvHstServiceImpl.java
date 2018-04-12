package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtAdminCsvHst;
import jp.bo.bocc.repository.AdminCsvHstRepository;
import jp.bo.bocc.service.AdminCsvHstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author NguyenThuong on 6/5/2017.
 */
@Service
public class AdminCsvHstServiceImpl implements AdminCsvHstService{

    @Autowired
    AdminCsvHstRepository adminCsvHstRepository;

    @Override
    @Transactional
    public ShtAdminCsvHst save(ShmAdmin admin, ShtAdminCsvHst.CSV_TYPE csvType) {
        ShtAdminCsvHst adminCsvHst = new ShtAdminCsvHst();
        adminCsvHst.setAdmin(admin);
        adminCsvHst.setAdminCsvHstType(csvType);
        return adminCsvHstRepository.save(adminCsvHst);
    }

    @Override
    public ShtAdminCsvHst save(ShtAdminCsvHst adminCsvHst) {
        return adminCsvHstRepository.save(adminCsvHst);
    }

    public AdminCsvHstServiceImpl(AdminCsvHstRepository adminCsvHstRepository) {
        this.adminCsvHstRepository = adminCsvHstRepository;
    }

    @Override
    public Page<ShtAdminCsvHst> getAdminCsvHst(Pageable pageable) {
        return adminCsvHstRepository.getAdminCsvHst(pageable);
    }
}
