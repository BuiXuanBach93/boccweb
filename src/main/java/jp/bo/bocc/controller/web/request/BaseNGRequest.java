package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author NguyenThuong on 4/28/2017.
 */
public class BaseNGRequest {

    @Getter @Setter
    private String content;

    @Getter @Setter
    private Short notSuitable;

    @Getter @Setter
    private Short sensitiveInf;

    @Getter @Setter
    private Short slander;

    @Getter @Setter
    private Short cheat;

    @Getter @Setter
    private Short other;

    @Getter @Setter
    private String difReason;

    @Getter @Setter
    private Short patrolType;

    @Getter @Setter
    private int sequentNumber;
}
