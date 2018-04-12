package jp.bo.bocc.controller.api.request;

import jp.bo.bocc.entity.ShtPushNotify;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created by buixu on 9/14/2017.
 */
public class PushBodyRequest {
    @Getter
    @Setter
    private Long adminId;

    @Getter @Setter
    private Long userId;

    @Getter @Setter
    private Boolean pushAndroid;

    @Getter @Setter
    private Boolean pushIOs;

    @Getter @Setter
    private Boolean pushImmediate;

    @Getter @Setter
    private String pushTitle;

    @Getter @Setter
    private String pushContent;

    @Getter @Setter
    private ShtPushNotify.PushStatus pushStatus;

    @Getter @Setter
    private String deviceToken;

    @Getter @Setter
    private String password;
}
