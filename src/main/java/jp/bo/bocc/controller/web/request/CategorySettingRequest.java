package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by buixu on 11/28/2017.
 */
public class CategorySettingRequest {

    @Getter @Setter
    private Long categorySettingId;

    @Getter @Setter
    private String categoryName;

    @Getter @Setter
    private Boolean isDefault;

    @Getter @Setter
    private Boolean filterTypePrivate;

    @Getter @Setter
    private Boolean filterTypeKeyword;

    @Getter @Setter
    private Boolean filterTypePostId;

    @Getter @Setter
    private String filterText;

    @Getter @Setter
    private Boolean publishTypeAll;

    @Getter @Setter
    private Boolean publishTypeBuy;

    @Getter @Setter
    private Boolean publishTypeSell;
}
