package jp.bo.bocc.controller.web.request;

import jp.bo.bocc.entity.ShtPushNotify;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by buixu on 9/15/2017.
 */
public class PushRequest {
    @Getter @Setter
    private Long pushId;

    @Getter @Setter
    private ShtPushNotify.PushStatus pushStatus;

    @Getter @Setter
    @DateTimeFormat(pattern = "YYYY/MM/DD H:m")
    private Date timerDate;

    @Getter @Setter
    private String timerDateStr;

    @Getter @Setter
    private String pushTitle;

    @Getter @Setter
    private String pushContent;

    @Getter @Setter
    private Boolean pushAndroid;

    @Getter @Setter
    private Boolean pushIos;

    @Getter @Setter
    private String pushImmediate;

    @Getter @Setter
    private String pushActionType;

    @Getter @Setter
    private Long iosNumber;

    @Getter @Setter
    private Long androidNumber;

    @Getter @Setter
    private Long currentAndroidNumber;

    @Getter @Setter
    private Long currentIosNumber;
}
