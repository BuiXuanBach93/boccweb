package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author NguyenThuong on 5/28/2017.
 */
public class QaMemoRequest {

    @Getter @Setter
    private Long qaId;

    @Getter @Setter
    private String content;
}
