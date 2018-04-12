package jp.bo.bocc.enums;

/**
 * Created by Namlong on 3/30/2017.
 */
public enum ImageSavedEnum {
    ORIGINAL("org"), THUMBNAIL("thb");
    private String key;

    ImageSavedEnum(String key) {
        this.key = key;
    }

    public String key(){
        return key;
    }
}
