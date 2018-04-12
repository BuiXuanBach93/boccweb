package jp.bo.bocc.controller.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by DonBach on 4/25/2017.
 */
@AllArgsConstructor
@NoArgsConstructor
public class TalkQaRequest {
    @Getter @Setter
    private Long qaId;

    @Getter @Setter
    private String msg;
}
