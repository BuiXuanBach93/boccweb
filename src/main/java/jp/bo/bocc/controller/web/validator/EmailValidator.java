package jp.bo.bocc.controller.web.validator;

import jp.bo.bocc.helper.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HaiTH on 3/27/2017.
 */
@Component
public class EmailValidator {

    private Pattern pattern;
    private Matcher matcher;

    public EmailValidator() {
        pattern = Pattern.compile(StringUtils.EMAIL_REGEX);
    }

    public boolean valid(final String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }
}
