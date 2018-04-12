package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by buixu on 12/29/2017.
 */
@Entity
@Table(name = "SHT_BANNER_PAGE")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_BANNER_PAGE\" SET CMN_DELETE_FLAG = 1 WHERE PAGE_ID = ?")
public class ShtBannerPage extends BaseEntity {
    @Id
    @Column(name = "PAGE_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_BANNER_PAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    private Long pageId;

    @Column(name = "BANNER_ID")
    @Getter @Setter
    private Long bannerId;

    @Column(name = "PAGE_HEADER")
    @Getter @Setter
    private String pageHeader;

    @Column(name = "PAGE_TITLE")
    @Getter @Setter
    private String pageTitle;

    @Column(name = "PAGE_CONTENT")
    @Getter @Setter
    private String pageContent;

    @Column(name = "PAGE_FOOTER")
    @Getter @Setter
    private String pageFooter;

    @Column(name = "TITLE_COLOR")
    @Getter @Setter
    private String titleColor;

    @Column(name = "BACKGROUND_COLOR")
    @Getter @Setter
    private String backgroundColor;


    @Column(name = "PAGE_HEADER_PRV")
    @Getter @Setter
    private String pageHeaderPrv;

    @Column(name = "PAGE_TITLE_PRV")
    @Getter @Setter
    private String pageTitlePrv;

    @Column(name = "PAGE_CONTENT_PRV")
    @Getter @Setter
    private String pageContentPrv;

    @Column(name = "PAGE_FOOTER_PRV")
    @Getter @Setter
    private String pageFooterPrv;

    @Column(name = "TITLE_COLOR_PRV")
    @Getter @Setter
    private String titleColorPrv;

    @Column(name = "BACKGROUND_COLOR_PRV")
    @Getter @Setter
    private String backgroundColorPrv;

    @Column(name = "IS_ACTIVE")
    @Getter @Setter
    private Boolean isActive;

    @JoinColumn(name = "IMAGE_ID")
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter
    @Setter
    private ShrFile image;

    @JoinColumn(name = "IMAGE_PRV_ID")
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter
    @Setter
    private ShrFile imagePrv;

    @Transient
    @Getter @Setter
    private String imageUrl;

    @Transient
    @Getter @Setter
    private String imageUrlPrv;

}
