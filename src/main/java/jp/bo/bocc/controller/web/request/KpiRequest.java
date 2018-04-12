package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by buixu on 12/4/2017.
 */
public class KpiRequest {

    @Getter
    @Setter
    private String fromDate;

    @Getter @Setter
    private String toDate;

    @Getter @Setter
    private String fromMonth;

    @Getter @Setter
    private String toMonth;

    @Getter @Setter
    private int page;
}
