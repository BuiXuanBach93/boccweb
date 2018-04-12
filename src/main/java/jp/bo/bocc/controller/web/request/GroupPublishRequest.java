package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by buixu on 11/17/2017.
 */
public class GroupPublishRequest {
    @Getter @Setter
    private String groupName;

    @Getter @Setter
    List<String> groupDetails;

    @Getter @Setter
    private Long groupId;
}
