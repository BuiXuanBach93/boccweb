package jp.bo.bocc.controller.web.validator;

import jp.bo.bocc.controller.web.request.UserSearchRequest;
import jp.bo.bocc.helper.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author NguyenThuong on 4/18/2017.
 */
@Component
public class UserSearchValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return UserSearchRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserSearchRequest userSearchRequest = (UserSearchRequest) o;

        if (!org.apache.commons.lang.StringUtils.isEmpty(userSearchRequest.getFirstName()) && !StringUtils.isOnlyContainLetterAndJapanAndCommaAndDigit(userSearchRequest.getFirstName()))
            errors.rejectValue("firstName", "SH_E100113");

        if (!org.apache.commons.lang.StringUtils.isEmpty(userSearchRequest.getLastName()) && !StringUtils.isOnlyContainLetterAndJapanAndCommaAndDigit(userSearchRequest.getLastName()))
            errors.rejectValue("lastName", "SH_E100113");

        if (!org.apache.commons.lang.StringUtils.isEmpty(userSearchRequest.getBsid()) && !StringUtils.isEmailValid(userSearchRequest.getBsid())
                && (!StringUtils.isOnlyDigits(userSearchRequest.getBsid()) || userSearchRequest.getBsid().length() != 15))
            errors.rejectValue("bsid", "SH_E100052");

        if(!org.apache.commons.lang.StringUtils.isEmpty(userSearchRequest.getEmail()) && !StringUtils.isEmailValid(userSearchRequest.getEmail()))
            errors.rejectValue("email", "SH_E100003");
    }
}