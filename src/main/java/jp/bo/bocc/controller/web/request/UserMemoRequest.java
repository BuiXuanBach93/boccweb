package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author NguyenThuong on 4/6/2017.
 */
public class UserMemoRequest {

    @Getter @Setter
    private Long userId;

    @Getter @Setter
    private String content;
}
