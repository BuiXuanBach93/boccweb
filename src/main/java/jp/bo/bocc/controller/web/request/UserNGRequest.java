package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by NguyenThuong on 4/28/2017.
 */
public class UserNGRequest extends BaseNGRequest {

    @Getter @Setter
    @NotNull
    private Long userId;

    @Getter @Setter
    private String ctrlStatus;
}
