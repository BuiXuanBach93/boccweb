package jp.bo.bocc.firebase;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

/**
 * Created by Namlong on 8/2/2017.
 */
public class DatabaseRealtime {

    private final static Logger LOGGER = Logger.getLogger(DatabaseRealtime.class.getName());

    @Getter @Setter
    private String accessToken;

    public DatabaseRealtime(String accessToken) {
        this.accessToken = accessToken;
    }
}
