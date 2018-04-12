package jp.bo.bocc.controller.api.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Namlong on 4/24/2017.
 */
public class CounterMsgResponse {
    @Getter @Setter
    int toTalNewMsgForOwnerPost;
    @Getter @Setter
    int toTalNewMsgForOthersPost;
    @Getter @Setter
    int toTalNewMsgFormAdmin;

    @Getter @Setter
    boolean newMsgFlagInOwnerPost;

    @Getter @Setter
    boolean newMsgFlagInFromOthersPost;

    @Getter @Setter
    boolean newMsgFlagInFromAdmin;

}
