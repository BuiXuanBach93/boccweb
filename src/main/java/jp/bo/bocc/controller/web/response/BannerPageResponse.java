package jp.bo.bocc.controller.web.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by buixu on 1/3/2018.
 */
public class BannerPageResponse {
    @Getter
    @Setter
    private int error;

    @Getter @Setter
    private String errorMsg;

    @Getter @Setter
    private Long pageId;

    @Getter @Setter
    private String pageUrl;
}
