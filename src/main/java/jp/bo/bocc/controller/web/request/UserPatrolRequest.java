package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author haipv on 3/31/2017.
 */
public class UserPatrolRequest {

    @Getter @Setter
    private Long userId;

    @Getter @Setter
    private String processStatus;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date updatedAtFrom;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date updatedAtTo;

    @Getter @Setter
    private String patrolAdminNames;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date censoredFrom;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date censoredTo;

    @Getter @Setter
    private String pending;

    @Getter @Setter
    private String ng;

    @Getter @Setter
    private String userUpdatedAt;

    @Getter @Setter
    private String ctrlStatus;

    @Getter @Setter
    private int pageIndex;

    @Getter @Setter
    private String reportByOthers;
}
