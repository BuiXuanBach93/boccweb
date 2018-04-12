package jp.bo.bocc.repository.criteria;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author NguyenThuong on 3/16/2017.
 */
public class SearchCriteria {

    @Getter @Setter
    private String key;

    @Getter @Setter
    private String operation;

    @Getter @Setter
    private Object value;

    @Getter @Setter
    private LocalDateTime startTime;

    @Getter @Setter
    private LocalDateTime endTime;

    @Getter @Setter
    private List<?> objectList;

    public SearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SearchCriteria(String key, String operation, LocalDateTime startTime, LocalDateTime endTime) {
        this.key = key;
        this.operation = operation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public SearchCriteria(String key, String operation, List<?> objectList) {
        this.key = key;
        this.operation = operation;
        this.objectList = objectList;
    }
}
