package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by buixu on 11/28/2017.
 */
public class BannerRequest {

    @Getter @Setter
    private Long bannerId;

    @Getter @Setter
    private String bannerName;

    @Getter @Setter
    private Boolean desTypeWebUrl;

    @Getter @Setter
    private Boolean desTypeCategoryId;

    @Getter @Setter
    private Boolean desTypePostId;

    @Getter @Setter
    private Boolean desTypeKeyword;

    @Getter @Setter
    private String destinationText;

    @Getter @Setter
    private String fromDate;

    @Getter @Setter
    private String toDate;

    @Getter @Setter
    private MultipartFile imageFile;

    @Getter @Setter
    private Long desType;

    @Getter @Setter
    private Long urlType;

    @Getter @Setter
    private String imageUrl;

    @Getter @Setter
    private String imagePageUrl;

    @Getter @Setter
    private Long isChangeImage;

    @Getter @Setter
    private Long isChangeImagePage;

    @Getter @Setter
    private String pageTitle;

    @Getter @Setter
    private String pageContent;

    @Getter @Setter
    private Long pageId;

    @Getter @Setter
    private Boolean urlFillType;

    @Getter @Setter
    private Boolean urlBuildType;

    @Getter @Setter
    private Boolean isBuildPage;

    @Getter @Setter
    private MultipartFile bannerImage;

    @Getter @Setter
    private MultipartFile pageImage;

    @Getter @Setter
    private String titleColor;

    @Getter @Setter
    private String backgroundColor;

}
