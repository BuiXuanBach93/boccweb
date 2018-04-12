package jp.bo.bocc.enums;

/**
 * Created by DonBach on 12/19/2017.
 */
public enum PushNotifyActionEnum {
    JUST_PUSH("JUST_PUSH"), JUST_MESSAGE("JUST_MESSAGE"), PUSH_AND_MESSAGE("PUSH_AND_MESSAGE");
    public String value;
    PushNotifyActionEnum(String value) {
        this.value = value;
    }
}
