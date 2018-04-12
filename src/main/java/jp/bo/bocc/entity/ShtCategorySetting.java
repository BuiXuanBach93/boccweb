package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;


/**
 * Created by buixu on 11/14/2017.
 */
@Entity
@Table(name = "SHT_CATEGORY_SETTING")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_CATEGORY_SETTING\" SET CMN_DELETE_FLAG = 1 WHERE CATEGORY_SETTING_ID = ?")
public class ShtCategorySetting extends BaseEntity {
    @Id
    @Column(name = "CATEGORY_SETTING_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_CATEGORY_SETTING_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long categorySettingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin shmAdmin;

    @Column(name = "CATEGORY_NO")
    @Getter @Setter
    private String categoryNo;

    @Column(name = "CATEGORY_NAME")
    @Getter @Setter
    private String categoryName;

    @Getter @Setter
    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;

    @Column(name = "POST_IDS")
    @Getter @Setter
    private String postIds;

    @Column(name = "KEYWORDS")
    @Getter @Setter
    private String keywords;

    @Getter @Setter
    @Column(name = "FILTER_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private CategoryFilterTypeEnum categoryFilterType;

    @Getter @Setter
    @Column(name = "CATEGORY_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private CategoryStatusEnum categoryStatus;

    @Getter @Setter
    @Column(name = "PUBLISH_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private CategoryPublishTypeEnum categoryPublishType;

    @Transient
    @Getter @Setter
    private List<String> listKeyWords;

    @Transient
    @Getter @Setter
    private List<Long> listPostIds;

    public enum CategoryFilterTypeEnum {
        PRIVATE, KEYWORD, POST_ID
    }

    public enum CategoryStatusEnum {
        SUSPENDED, ACTIVE
    }

    public enum CategoryPublishTypeEnum {
        SELL, BUY, ALL
    }
}
