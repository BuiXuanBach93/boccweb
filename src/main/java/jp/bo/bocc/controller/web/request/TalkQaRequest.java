package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author NguyenThuong on 5/5/2017.
 */
public class TalkQaRequest {

    @Setter @Getter
    private Long qaId;

    @Setter @Getter
    private String qaContent;
}
