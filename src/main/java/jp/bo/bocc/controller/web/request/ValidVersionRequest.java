package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by buixu on 9/4/2017.
 */
public class ValidVersionRequest {
    @Getter @Setter
    private String sysConfigValues;

    @Getter @Setter
    private String sysConfigMsg;
}
