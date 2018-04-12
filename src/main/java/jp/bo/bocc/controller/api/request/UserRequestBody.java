package jp.bo.bocc.controller.api.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by DonBach on 5/22/2017.
 */
public class UserRequestBody {
    @Getter @Setter
    private String email;

    @Getter @Setter
    private String password;

    @Getter @Setter
    private String newPassword;

    @Getter @Setter
    private String retypePassword;

    @Getter @Setter
    private String deviceToken;
}
