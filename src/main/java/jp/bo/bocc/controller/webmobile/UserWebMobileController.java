package jp.bo.bocc.controller.webmobile;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.entity.ShtUserRev;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.service.UserRevService;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Namlong on 8/1/2017.
 */
@RestController
public class UserWebMobileController extends BoccBaseController {

    @Autowired
    UserRevService userRevService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/webmobile/user/list-reviews", method = RequestMethod.GET)
    @NoAuthentication
    public Page<ShtUserRev> getReviewByUserId(@RequestParam(value ="userId", required = true) Long userId,
                                              @RequestParam(value ="userReviewRate", required = false) ShtUserRev.UserReviewRate userReviewRate,
                                              @RequestParam(value ="page", required = true) int page,
                                              @RequestParam(value ="size", required = true) int size){
        Pageable pageable = createPage(page, size);
        Page<ShtUserRev> userRevs = userRevService.getReviewToUserId(userId,pageable,userReviewRate);
        return userRevs;
    }

    @GetMapping(value = "/webmobile/users/userpage/{userId}")
    @NoAuthentication
    public ResponseEntity<ShmUserDTO> getUser(@PathVariable("userId") Long userId) throws Exception {
        ShmUserDTO userDTO = userService.getUserProfile(userId);
        if (userDTO == null) {
            return new ResponseEntity<ShmUserDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ShmUserDTO>(userDTO, HttpStatus.OK);
    }
}
