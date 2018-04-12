package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmCategory;
import jp.bo.bocc.repository.CategoryRepository;
import jp.bo.bocc.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by DonBach on 3/15/2017.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository repo;

    @Transactional(readOnly = true)
    @Override
    public List<ShmCategory> findByCategoryParentId(long categoryParrentId) {
       return repo.findByCategoryParentId(categoryParrentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShmCategory> getCategories(Boolean firstLevelOnly) {
        if(firstLevelOnly){
            return repo.findByCategoryParentIdIsNull();
        }else{
            return repo.findAll();
        }
    }

    @Override
    public ShmCategory save(ShmCategory category) {
        return repo.save(category);
    }

    @Transactional(readOnly = true)
    @Override
    public ShmCategory get(Long cagetoryId) {
        return repo.findOne(cagetoryId);
    }

    @Override
    public void delete(Long id) {
        repo.delete(id);
    }
}
