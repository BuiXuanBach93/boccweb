package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmCategory;

import java.util.List;

/**
 * Created by DonBach on 3/15/2017.
 */
public interface CategoryService {
    List<ShmCategory> findByCategoryParentId(long categoryParentId);
    List<ShmCategory> getCategories(Boolean firstLevelOnly);
    ShmCategory save(ShmCategory category);
    ShmCategory get(Long cagetoryId);
    void delete(Long categoryId);
}
