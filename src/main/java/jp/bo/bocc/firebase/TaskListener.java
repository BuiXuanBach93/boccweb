package jp.bo.bocc.firebase;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Namlong on 8/3/2017.
 */
public class TaskListener {

    @Getter @Setter
    private String customToken;

    public TaskListener() {
    }

    public TaskListener(String customToken) {
        this.customToken = customToken;
    }

}
