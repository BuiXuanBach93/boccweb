package jp.bo.bocc.controller.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Namlong on 4/5/2017.
 */
@NoArgsConstructor @AllArgsConstructor
public class ShtUserRprtBodyRequest {
    @Getter @Setter
    private Long postId;
    @Getter @Setter
    private String rprtType;
    @Getter @Setter
    private String rprtCont;
}
