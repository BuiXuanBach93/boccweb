package jp.bo.bocc.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHM_CATEGORY")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShmCategory extends BaseEntity{

    @Id
    @Column(name = "CATEGORY_ID")
    @Getter @Setter
    private Long categoryId;

    @Getter @Setter
    @Column(name = "CATEGORY_NAME")
    private String categoryName;

//    @Getter @Setter
//    @Column(name = "CATEGORY_ICON")
//    private Long categoryIcon;

    @Getter @Setter
    @Column(name = "CATEGORY_PARENT_ID")
    private Long categoryParentId;

    @Getter @Setter
    @Column(name = "IMAGE_REQUIRED")
    private Boolean imageRequired;
}
