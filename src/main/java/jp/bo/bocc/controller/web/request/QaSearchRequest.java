package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by DonBach on 4/14/2017.
 */
public class QaSearchRequest {

    @Getter @Setter
    private Boolean isNoResponse = false;

    @Getter @Setter
    private Boolean isInProgress = false;

    @Getter @Setter
    private  Boolean isResolved = false;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date createdAt;

    @Getter @Setter
    private Boolean haveFeedback = false;
}
