package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Namlong on 6/23/2017.
 */
public class PostUploadRequest {

    @Getter @Setter
    MultipartFile excelFile;

    @Getter @Setter
    MultipartFile imgZipFile;
}
