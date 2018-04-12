package jp.bo.bocc.enums;

/**
 * Created by Namlong on 7/11/2017.
 */
public enum ImageEnum {
    PNG("png"), JPEG("jpeg"), JPG("jpg"), GIF("gif");
    public String value;
    ImageEnum(String value) {
        this.value = value;
    }
}
