package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author NguyenThuong on 3/24/2017.
 */
public class PostSearchRequest {

    @Getter @Setter
    private String postId;

    @Getter @Setter
    private String userName;

    @Getter @Setter
    private Integer sellType;

    @Getter @Setter
    private Integer buyType;

    @Getter @Setter
    private Long parentCat;

    @Getter @Setter
    private Long childCat;

    @Getter @Setter
    private List<Short> postSellStt;

    @Getter @Setter
    private Short pubSellStt;

    @Getter @Setter
    private Short inCnvStt;

    @Getter @Setter
    private Short tendSellStt;

    @Getter @Setter
    private Short soldStt;

    @Getter @Setter
    private Short delStt;

    @Getter @Setter
    private Short reservedStt;

    @Getter @Setter
    private Short suspendedStt;

    @Getter @Setter
    private Integer dateType;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date fromDate;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date toDate;

    @Getter @Setter
    private int page;

    @Getter @Setter
    private int size;
}
