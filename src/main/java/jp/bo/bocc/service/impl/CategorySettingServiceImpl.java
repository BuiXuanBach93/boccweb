package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.repository.CategorySettingRepository;
import jp.bo.bocc.service.CategorySettingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by buixu on 11/17/2017.
 */
@Service
public class CategorySettingServiceImpl implements CategorySettingService {

    private final static Logger LOGGER = Logger.getLogger(CategorySettingServiceImpl.class.getName());

    @Autowired
    CategorySettingRepository repository;

    @Override
    public Page<ShtCategorySetting> getCategorySetting(ShtCategorySetting categorySetting, Pageable pageNumber) {
        return repository.getListCategorySetting(categorySetting, pageNumber);
    }

    @Override
    public ShtCategorySetting saveCategorySetting(ShtCategorySetting categorySetting) {
        return repository.save(categorySetting);
    }

    @Override
    public ShtCategorySetting getCategorySettingById(Long categorySettingId) {
        ShtCategorySetting cate = null;
        try{
            cate = repository.findOne(categorySettingId);
            if(cate != null){
                return  cate;
            }
        }catch (Exception ex){
            LOGGER.error("ERROR: " + ex.getMessage());
            throw ex;
        }
        return null;
    }

    @Override
    public void resetDefaultCategorySetting() {
        repository.resetDefaultCategorySetting();
    }

    @Override
    public Long countCategorySettingActive() {
        return repository.countCategorySettingActive();
    }

    @Override
    public List<ShtCategorySetting> getCategorySettingByName(String categoryName) {
        return repository.findByCategoryName(categoryName);
    }

    @Override
    public List<ShtCategorySetting> getActiveCategorySetting() {
        return repository.getActiveCategorySetting();
    }

    @Override
    public List<ShtCategorySetting> getNoLoginActiveCategorySetting() {
        return repository.getActiveCategorySettingNoLogin();
    }
}
