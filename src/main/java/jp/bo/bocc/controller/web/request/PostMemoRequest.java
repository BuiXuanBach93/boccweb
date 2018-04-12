package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author NguyenThuong on 4/20/2017.
 */
public class PostMemoRequest extends BaseNGRequest {

    @Getter @Setter
    @NotNull
    private Long postId;
}
