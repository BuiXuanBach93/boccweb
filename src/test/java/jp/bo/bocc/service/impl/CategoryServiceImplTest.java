package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmCategory;
import jp.bo.bocc.service.CategoryService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

/**
 * Created by DonBach on 3/17/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class CategoryServiceImplTest {
    @Autowired
    CategoryService service;

    @Test
    public void getCategories() throws Exception {
        CategoryServiceImplTest.CategorySet categorySet = new CategoryServiceImplTest.CategorySet();
        assertEquals(categorySet.car, service.get(categorySet.car.getCategoryId()));
    }

    private class CategorySet {
        ShmCategory car;
        ShmCategory toyota;
        ShmCategory bmw;

        public CategorySet() {
            ShmCategory creatingCategory = new ShmCategory();
            creatingCategory.setCategoryId(new Long(1));
//            creatingCategory.setCategoryIcon(new Long(123));
            creatingCategory.setCategoryName("car");


            toyota = new ShmCategory();
            toyota.setCategoryId(new Long(2));
//            toyota.setCategoryIcon(new Long(234));
            toyota.setCategoryName("toyota");
            toyota.setCategoryParentId(new Long(1));
            service.save(toyota);

            bmw = new ShmCategory();
            bmw.setCategoryId(new Long(1));
//            bmw.setCategoryIcon(new Long(345));
            bmw.setCategoryParentId(new Long(1));
            bmw.setCategoryName("bmw");
            service.save(bmw);

            ShmCategory saveCategory = service.save(creatingCategory);
            car = service.get(saveCategory.getCategoryId());
        }
    }
}
