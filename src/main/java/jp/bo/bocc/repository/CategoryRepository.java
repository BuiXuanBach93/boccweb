package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by DonBach on 3/15/2017.
 */
@Repository
public interface CategoryRepository extends JpaRepository<ShmCategory, Long>{
    List<ShmCategory> findByCategoryParentId(long categoryParrentId);

    @Query(" select c from jp.bo.bocc.entity.ShmCategory c where c.categoryParentId is null")
    List<ShmCategory> findByCategoryParentIdIsNull();
}
