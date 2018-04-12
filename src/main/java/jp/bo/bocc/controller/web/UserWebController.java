package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.UserMemoRequest;
import jp.bo.bocc.controller.web.request.UserSearchRequest;
import jp.bo.bocc.controller.web.validator.UserSearchValidator;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.PatrolHistoryDTO;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author NguyenThuong on 3/14/2017.
 */
@Controller
public class UserWebController extends BoccBaseWebController {

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    MemoUserService memoUserService;

    @Autowired
    AdminService adminService;

    @Autowired
    AddressService addressService;

    @Autowired
    UserSearchValidator userSearchValidator;

    @Autowired
    ExportCsvService exportCsvService;

    @Autowired
    AdminCsvHstService adminCsvHstService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsers(Model model, @ModelAttribute("userSearch") UserSearchRequest user, BindingResult bindingResult) {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        userSearchValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors())
            return "user-search";

        if (validate(user))
            user.setActiveStt((short)ShmUser.Status.ACTIVATED.ordinal());

        PageRequest pageRequest = new PageRequest(user.getPage(), maxRecordsInPage, Sort.Direction.DESC, "id","createdAt");
        Page<ShmUser> users = userService.searchUserByConditions(user, pageRequest);

        model.addAttribute("users", users.getContent());
        model.addAttribute("totalPage", users.getTotalPages() - 1);
        model.addAttribute("totalElements", users.getTotalElements());
        model.addAttribute("curPage", user.getPage());
        model.addAttribute("startElement", (user.getPage() * maxRecordsInPage) + 1);
        model.addAttribute("curElements", (user.getPage() * maxRecordsInPage) + users.getContent().size());

        return "user-search";
    }

    @RequestMapping(value = "/user-detail", method = RequestMethod.GET)
    public String getUserInfo(Model model, @RequestParam("userId") Long userId, @RequestParam(value = "errorMsg", required = false) String errorMsg) {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());

        ShmUser user = userService.getUserById(userId);
        if (StringUtils.isNotEmpty(errorMsg)) {
            model.addAttribute("errorMsg", getMessage("SH_E100132"));
        }

        // get full address
        if(user != null && user.getAddress() != null){
            ShmAddr shmAddr = user.getAddress();
            user.getAddress().setFullAreaName(addressService.getFullAddress(shmAddr));
        }

        long sellTotal = postService.countPostByPostIdAndPostType(userId, ShmPost.PostType.BUY.ordinal());
        long buyTotal = postService.countPostByPostIdAndPostType(userId, ShmPost.PostType.SELL.ordinal());
        long reviewedTotal = userService.getReviewForUserByUserId(userId);
        model.addAttribute("userAvatarPath", userService.buildOriginalAvatarPathForUser(user.getAvatar()));
        model.addAttribute("user", user);
        model.addAttribute("sellTotal", sellTotal);
        model.addAttribute("reviewedTotal", reviewedTotal);
        model.addAttribute("buyTotal", buyTotal);

        return "user-detail";
    }

    @RequestMapping(value = "/users/csv", method = RequestMethod.GET)
    public void exportCSV(Model model, @ModelAttribute("userSearch") UserSearchRequest request, HttpServletResponse response) throws IOException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdmin(getEmail());
        PageRequest pageRequest = new PageRequest(request.getPage(), maxRecordsInPage, Sort.Direction.DESC, "id","createdAt");
        userService.exportUserCSV(request, pageRequest, response);
        adminCsvHstService.save(admin, ShtAdminCsvHst.CSV_TYPE.USER);
    }

    @RequestMapping(value = "/activation", method = RequestMethod.GET)
    public String redirectToActiveAccount(@RequestParam("token") String token) {

        return "user-activation-email";
    }

    @RequestMapping(value = "/web/memo-user", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PatrolHistoryDTO>> getCategories(@RequestParam(value = "userId") Long userId) {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        List<PatrolHistoryDTO> memoUserList = memoUserService.getAllMemoUserByUserId(userId);
        return new ResponseEntity<>(memoUserList, HttpStatus.OK);
    }

    @RequestMapping(value = "/web/save-user-memo", method = RequestMethod.POST)
    @ResponseBody
    public PatrolHistoryDTO saveUserMemo(@RequestBody UserMemoRequest userMemoRequest) {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");
        return memoUserService.saveMemoUser(userMemoRequest.getUserId(), admin, userMemoRequest.getContent());
    }

    private boolean checkElemNull(Object object) {
        return object == null;
    }

    public boolean validate(UserSearchRequest request) {
        return (checkElemNull(request.getBirthDay())  && checkElemNull(request.getBsid()) && checkElemNull(request.getDistrictId()) && checkElemNull(request.getEmail())
                && checkElemNull(request.getFirstName()) && checkElemNull(request.getFromDate()) && checkElemNull(request.getFemale()) && checkElemNull(request.getMale()) && checkElemNull(request.getLegalId())
                && checkElemNull(request.getLastName()) && checkElemNull(request.getPhoneNumber()) && checkElemNull(request.getProvinceId())
                && checkElemNull(request.getActiveStt()) && checkElemNull(request.getToDate()) && checkElemNull(request.getUserType()));
    }
}
