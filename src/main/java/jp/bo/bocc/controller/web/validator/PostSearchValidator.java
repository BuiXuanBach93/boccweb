package jp.bo.bocc.controller.web.validator;

import jp.bo.bocc.controller.web.request.PostSearchRequest;
import jp.bo.bocc.helper.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author NguyenThuong on 4/17/2017.
 */

@Component
public class PostSearchValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return PostSearchRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PostSearchRequest postSearchRequest = (PostSearchRequest) o;

        final String postId = postSearchRequest.getPostId();
        if (!org.apache.commons.lang.StringUtils.isEmpty(postId) && !StringUtils.isOnlyContainDigitsAndComma(postId.trim()))
            errors.rejectValue("postId", "SH_E100046");
        //remove msg follow bug bocc-1217
//        if (!org.apache.commons.lang.StringUtils.isEmpty(postSearchRequest.getUserName()) && !StringUtils.isOnlyContainLetterAndJapanAndCommaAndDigit(postSearchRequest.getUserName()))
//            errors.rejectValue("userName", "SH_E100113");
    }
}
