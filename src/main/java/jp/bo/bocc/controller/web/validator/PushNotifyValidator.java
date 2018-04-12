package jp.bo.bocc.controller.web.validator;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by buixu on 11/28/2017.
 */
public class PushNotifyValidator {
    @Getter
    @Setter
    private int error;

    @Getter @Setter
    private String errorMsg;
}
