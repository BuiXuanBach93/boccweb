package jp.bo.bocc.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Namlong on 7/27/2017.
 */
public enum AdminActionEnum {
    CREATED(0), UPDATED(1), DELETED(2);

    @Getter @Setter
    private int value;

    AdminActionEnum(int value) {
        this.value = value;
    }

}
