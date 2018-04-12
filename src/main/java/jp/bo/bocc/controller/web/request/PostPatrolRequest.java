package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author NguyenThuong on 3/31/2017.
 */
public class PostPatrolRequest {

    @Getter @Setter
    private Long postStatus;

    @Getter @Setter
    private Short pendingStt;

    @Getter @Setter
    private Short ngStt;

    @Getter @Setter
    private Short repairedStt;

    @Getter @Setter
    private Short reportStt;

    @Setter @Getter
    private List<Short> ctrlStt;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date fromUpdateAt;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date toUpdateAt;

    @Getter @Setter
    private String operatorNames;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date fromCompleteAt;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date toCompleteAt;

    @Getter @Setter
    private int page;

    @Getter @Setter
    private int size;

    @Getter @Setter
    private int startPage;

    @Getter @Setter
    private int endPage;
}
