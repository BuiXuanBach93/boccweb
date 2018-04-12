package jp.bo.bocc.helper;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Namlong on 3/31/2017.
 */
public class MessageUtils {
    @Getter
    @Setter
    private String from;
    @Getter @Setter
    private String to;
    @Getter @Setter
    private List<String> tos;
    @Getter @Setter
    private String subject;
    @Getter @Setter
    private String content;

    public MessageUtils() {
    }

    public MessageUtils(String from, String to, String subject, String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    public MessageUtils(String from, List<String> tos, String subject, String content) {
        this.from = from;
        this.tos = tos;
        this.subject = subject;
        this.content = content;
    }

}
