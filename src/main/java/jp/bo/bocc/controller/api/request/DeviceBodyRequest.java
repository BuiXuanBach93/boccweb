package jp.bo.bocc.controller.api.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Namlong on 5/29/2017.
 */
public class DeviceBodyRequest {
    @Getter @Setter
    String deviceToken;

    @Getter @Setter
    String osType;

}
