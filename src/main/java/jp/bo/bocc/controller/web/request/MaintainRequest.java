package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author NguyenThuong on 6/30/2017.
 */
public class MaintainRequest {

    @Getter @Setter
    private boolean maintain;
    @Getter @Setter
    private String sysConfigMsg;
}
