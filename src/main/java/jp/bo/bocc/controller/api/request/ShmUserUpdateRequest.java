package jp.bo.bocc.controller.api.request;

import jp.bo.bocc.entity.ShmUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by Namlong on 4/22/2017.
 */
public class ShmUserUpdateRequest {

    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private String nickName;
    @Getter
    @Setter
    private ShmUser.Gender gender;
    @Getter
    @Setter
    private LocalDate dateOfBirth;
    @Getter
    @Setter
    private Long addressId;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String career;
    @Getter
    @Setter
    private String job;
    @Getter
    @Setter
    private String avatarRaw;
    @Getter
    @Setter
    private Boolean isUpdateAvatar;
}

