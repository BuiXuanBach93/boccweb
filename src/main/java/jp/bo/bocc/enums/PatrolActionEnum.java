package jp.bo.bocc.enums;

/**
 * Created by Namlong on 6/15/2017.
 */
public enum PatrolActionEnum{
    PATROL_POST_OK(0),PATROL_POST_RESERVED(1), PATROL_POST_SUSPENDED(2),
    PATROL_USER_OK(3),PATROL_USER_RESERVED(4), PATROL_USER_SUSPENDED(5);
    private int value;

    PatrolActionEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
