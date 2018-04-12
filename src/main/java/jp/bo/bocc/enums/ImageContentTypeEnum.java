package jp.bo.bocc.enums;

/**
 * Created by DonBach on 12/14/2017.
 */
public enum ImageContentTypeEnum {
    PNG("image/png"), JPG("image/jpg"), GIF("image/gif");
    public String value;
    ImageContentTypeEnum(String value) {
        this.value = value;
    }
}
