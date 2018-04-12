package jp.bo.bocc.controller.api.request;

import jp.bo.bocc.entity.ShtUserRev;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by DonBach on 4/4/2017.
 */
public class UserRevBodyRequest {

    @Getter @Setter
    private Long talkPurcId;

    @Getter @Setter
    private ShtUserRev.UserReviewRate userRevRate;

    @Getter @Setter
    private String userRevCont;
}
