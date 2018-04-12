package jp.bo.bocc.enums;

/**
 * Created by Namlong on 5/29/2017.
 */
public enum JobEnum {
    JOB_SYNC_LEFT_GROUP("JOB_SYNC_LEFT_GROUP"),
    JOB_SYNC_LEFT_TRIGGER("JOB_SYNC_LEFT_TRIGGER"),
    JOB_SYNC_LEFT_ID("JOB_SYNC_LEFT_ID"),
    JOB_SYNC_TEND_TO_LEAVE_GROUP("JOB_SYNC_TEND_TO_LEAVE_GROUP"),
    JOB_SYNC_TEND_TO_LEAVE_TRIGGER("JOB_SYNC_TEND_TO_LEAVE_TRIGGER"),
    JOB_SYNC_TEND_TO_LEAVE_ID("JOB_SYNC_TEND_TO_LEAVE_ID"),
    JOB_BANNER_ACTIVE_GROUP("JOB_BANNER_ACTIVE_GROUP"),
    JOB_BANNER_ACTIVE_TRIGGER("JOB_BANNER_ACTIVE_TRIGGER"),
    JOB_BANNER_ACTIVE_ID("JOB_BANNER_ACTIVE_ID"),
    JOB_BANNER_EXPIRED_GROUP("JOB_BANNER_EXPIRED_GROUP"),
    JOB_BANNER_EXPIRED_TRIGGER("JOB_BANNER_EXPIRED_TRIGGER"),
    JOB_BANNER_EXPIRED_ID("JOB_BANNER_EXPIRED_ID"),
    JOB_PUSH_NOTIFY_GROUP("JOB_PUSH_NOTIFY_GROUP"),
    JOB_PUSH_NOTIFY_TRIGGER("JOB_PUSH_NOTIFY_TRIGGER"),
    JOB_PUSH_NOTIFY_ID("JOB_PUSH_NOTIFY_ID_"),
    JOB_REVIEW_FOR_PARTNER("JOB_REVIEW_FOR_PARTNER_"),
    JOB_REVIEW_FOR_OWNER_POST("JOB_REVIEW_FOR_OWNER_POST_"),
    JOB_REVIEW_GROUP("JOB_REVIEW_GROUP"),
    JOB_TALK_PURC_GROUP("JOB_TALK_PURC_GROUP"),
    JOB_TALK_PURC_TRIGGER("JOB_TALK_PURC_TRIGGER_"),
    JOB_TALK_PURC_ID("JOB_TALK_PURC_ID_"),
    JOB_USER_PATROL_GROUP("JOB_USER_PATROL_GROUP"),
    JOB_USER_PATROL_TRIGGER("JOB_USER_PATROL_TRIGGER_"),
    JOB_USER_PATROL_ID("JOB_USER_PATROL_ID_"),
    JOB_POST_PATROL_GROUP("JOB_POST_PATROL_GROUP"),
    JOB_POST_PATROL_TRIGGER("JOB_POST_PATROL_TRIGGER_"),
    JOB_POST_PATROL_ID("JOB_POST_PATROL_ID_"),
    JOB_USER_LEFT("JOB_USER_LEFT_"),
    JOB_USER_LEFT_GROUP("JOB_USER_LEFT_GROUP"),
    JOB_USER_LEFT_TRIGGER("JOB_USER_LEFT_TRIGGER_");;
    private String value;

    JobEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}