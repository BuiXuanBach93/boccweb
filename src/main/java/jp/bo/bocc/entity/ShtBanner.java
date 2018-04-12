package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by buixu on 11/14/2017.
 */
@Entity
@Table(name = "SHT_BANNER")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_BANNER\" SET CMN_DELETE_FLAG = 1 WHERE BANNER_ID = ?")
public class ShtBanner extends BaseEntity {
    @Id
    @Column(name = "BANNER_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_BANNER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long bannerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin shmAdmin;

    @Column(name = "BANNER_NO")
    @Getter @Setter
    private String bannerNo;

    @Column(name = "BANNER_NAME")
    @Getter @Setter
    private String bannerName;


    @Column(name = "POST_IDS")
    @Getter @Setter
    private String postIds;

    @Column(name = "KEYWORDS")
    @Getter @Setter
    private String keywords;

    @Column(name = "WEB_URL")
    @Getter @Setter
    private String webUrl;

    @Getter @Setter
    @Column(name = "DESTINATION_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DestinationTypeEnum destinationType;

    @Getter @Setter
    @Column(name = "BANNER_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private BannerStatusEnum bannerStatus;

    @Getter @Setter
    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @JoinColumn(name = "IMAGE_ID")
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter
    @Setter
    private ShrFile image;

    @Column(name = "FROM_DATE")
    @Getter @Setter
    private LocalDateTime fromDate;

    @Column(name = "TO_DATE")
    @Getter @Setter
    private LocalDateTime toDate;

    @Column(name = "BANNER_PAGE_ID")
    @Getter @Setter
    private Long bannerPageId;

    @Column(name = "IS_BUILD_PAGE")
    @Getter @Setter
    private Boolean isBuildPage;

    @Transient
    @Getter @Setter
    private List<String> listKeyWords;

    @Transient
    @Getter @Setter
    private List<Long> listPostIds;

    @Transient
    @Getter @Setter
    private String bannerDestination;

    @Transient
    @Getter @Setter
    private String imageUrl;

    public enum BannerStatusEnum {
        SUSPENDED, ACTIVE, EXPIRED
    }

    public enum DestinationTypeEnum {
        WEB, CATEGORY, POST_ID, KEYWORD,
    }
}
