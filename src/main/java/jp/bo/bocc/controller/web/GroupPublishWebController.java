package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.AdminNgRequest;
import jp.bo.bocc.controller.web.request.GroupPublishRequest;
import jp.bo.bocc.controller.web.validator.AdminNgValidator;
import jp.bo.bocc.controller.web.validator.GroupPublishValidator;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import jp.bo.bocc.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by buixu on 11/14/2017.
 */
@Controller
public class GroupPublishWebController extends BoccBaseWebController{

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Autowired
    AdminNgService adminNgService;

    @Autowired
    GroupPublishService groupPublishService;

    @Autowired
    GroupPblDetailService groupPblDetailService;

    @Autowired
    PostService postService;

    @Autowired
    AdminNgValidator adminNgValidator;

    @Autowired
    AdminService adminService;

    public static int PAGE_SIZE = 20;

    @RequestMapping(value = "list-group-publish", method = RequestMethod.GET)
    public String listGroupPublish(@RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "groupName", required = false) String groupName,  Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtGroupPublish groupPublish = new ShtGroupPublish();
        groupPublish.setGroupName(groupName);
        if (pageNumber == null)
            pageNumber = 0;
        final Pageable page100Item = new PageRequest(pageNumber, PAGE_SIZE);
        Page<ShtGroupPublish> page = groupPublishService.getListGroupPublish(groupPublish, page100Item);
        List<ShtGroupPublish> groupPublishes = page.getContent();

        if(groupPublishes.size()==0) {
            String nullResult = "データはありません !";
            model.addAttribute("nullResult", nullResult);
        }

        int sizePage = page.getTotalPages();

        model.addAttribute("sizeResult", groupPublishes.size());
        model.addAttribute("listGroupPublish", groupPublishes);
        model.addAttribute("groupPublish", groupPublish);
        int current = page.getNumber();
        model.addAttribute("deploymentLog", page);
        model.addAttribute("currentIndex", current);

        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("curPage", current);
        model.addAttribute("startElement", ((pageNumber) * PAGE_SIZE) + 1);
        model.addAttribute("curElements", ((pageNumber) * PAGE_SIZE) + groupPublishes.size());

        return "list-group-publish";
    }

    @RequestMapping(value = "/create-group-publish")
    public String createGroupPublish(Model model) {
        adminService.getAdminForSuperAdminAndAdmin(getEmail());

        model.addAttribute("groupPublish", new ShtGroupPublish());
        return "create-group-publish";
    }

    @RequestMapping(value = "/validate-group-publish", method = RequestMethod.POST)
    public @ResponseBody GroupPublishValidator validateGroupPublish(Model model, @RequestBody GroupPublishRequest request) {
        return validateGroupPublish(request);
    }


    @RequestMapping(value = "/add-group-publish", method = RequestMethod.POST)
    public @ResponseBody GroupPublishValidator addGroupPublish(Model model, @RequestBody GroupPublishRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        GroupPublishValidator validator = new GroupPublishValidator();

        if(StringUtils.isEmpty(request.getGroupName()) || StringUtils.isEmpty(request.getGroupName().trim())){
            validator.setError(1);
            validator.setErrorMsg("法人グループ名を入力してください");
            return validator;
        }

        List<ShtGroupPublish> checkDuplicateGroupNames = groupPublishService.getGroupByGroupName(request.getGroupName());
        if(checkDuplicateGroupNames.size() > 0){
            validator.setError(1);
            validator.setErrorMsg("入力したグループ名は既に存在しています。変更をお願いします。");
            return validator;
        }

        if(CollectionUtils.isEmpty(request.getGroupDetails()) || request.getGroupDetails().size() < 2){
            validator.setError(1);
            validator.setErrorMsg("２つ以上の法人を指定してください。");
            return validator;
        }

        String legalStr = "";
        for (String legalId: request.getGroupDetails()) {
            if(legalStr.contains(legalId)){
                validator.setError(1);
                validator.setErrorMsg("法人ID："+legalId+" が重複しています。修正をお願いします。");
                return validator;
            }
            legalStr += "-" + legalId;
        }

        for (String legalId : request.getGroupDetails()) {
            if(StringUtils.isEmpty(legalId) || legalId.length() != 6){
                validator.setError(1);
                validator.setErrorMsg("7桁以上は入力不可");
                return validator;
            }

            String pattern = "([０-９]+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(legalId);
            if(m.find()){
                validator.setError(1);
                validator.setErrorMsg("半角の数字を入力してください。");
                return validator;
            }

            List<ShtGroupPblDetail> groupPblDetails = groupPblDetailService.getGroupDetailByLegalId(legalId);
            if(groupPblDetails.size() > 0){
                validator.setError(1);
                validator.setErrorMsg("法人ID：" + legalId + "は法人グループ名："+groupPblDetails.get(0).getGroupPublish().getGroupName()+"に所属しているため、登録できません。");
                return validator;
            }
        }

        ShtGroupPublish groupPublish = new ShtGroupPublish();
        groupPublish.setShmAdmin(shmAdmin);
        groupPublish.setGroupName(request.getGroupName());
        groupPublish.setGroupNo("0");
        groupPublish.setLegalNumber(new Long(request.getGroupDetails().size()));
        ShtGroupPublish groupSaved = groupPublishService.saveGroupPublish(groupPublish);

        for (String legalId: request.getGroupDetails()) {
            ShtGroupPblDetail shtGroupPblDetail = new ShtGroupPblDetail();
            shtGroupPblDetail.setGroupPublish(groupSaved);
            shtGroupPblDetail.setLegalId(legalId);
            groupPblDetailService.saveDetail(shtGroupPblDetail);
        }
        return validator;
    }

    private GroupPublishValidator validateGroupPublish(GroupPublishRequest request){
        GroupPublishValidator validator = new GroupPublishValidator();

        if(StringUtils.isEmpty(request.getGroupName()) || StringUtils.isEmpty(request.getGroupName().trim())){
            validator.setError(1);
            validator.setErrorMsg("法人グループ名を入力してください");
            return validator;
        }

        List<ShtGroupPublish> checkDuplicateGroupNames = groupPublishService.getGroupByGroupName(request.getGroupName());
        if(checkDuplicateGroupNames.size() > 0 && request.getGroupId() == null){
            validator.setError(1);
            validator.setErrorMsg("入力したグループ名は既に存在しています。変更をお願いします。");
            return validator;
        }else if(checkDuplicateGroupNames.size() > 0 && request.getGroupId() != null){
            for (ShtGroupPublish groupNameCheck: checkDuplicateGroupNames) {
                if(groupNameCheck.getGroupId().intValue() != request.getGroupId().intValue()){
                    validator.setError(1);
                    validator.setErrorMsg("入力したグループ名は既に存在しています。変更をお願いします。");
                    return validator;
                }
            }
        }

        if(CollectionUtils.isEmpty(request.getGroupDetails()) || request.getGroupDetails().size() < 2){
            validator.setError(1);
            validator.setErrorMsg("２つ以上の法人を指定してください。");
            return validator;
        }

        String legalStr = "";
        for (String legalId: request.getGroupDetails()) {
            if(legalStr.contains(legalId)){
                validator.setError(1);
                validator.setErrorMsg("法人ID："+legalId+" が重複しています。修正をお願いします。");
                return validator;
            }
            legalStr += "-" + legalId;
        }

        for (String legalId : request.getGroupDetails()) {
            if(StringUtils.isEmpty(legalId) || legalId.length() != 6 || !StringUtils.isNumeric(legalId)){
                validator.setError(1);
                validator.setErrorMsg("6桁の数字を入力してください");
                return validator;
            }
            String pattern = "([０-９]+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(legalId);
            if(m.find()){
                validator.setError(1);
                validator.setErrorMsg("半角の数字を入力してください。");
                return validator;
            }
            List<ShtGroupPblDetail> groupPblDetails = groupPblDetailService.getGroupDetailByLegalId(legalId);
            if(groupPblDetails.size() > 0){
                if(request.getGroupId() == null){
                    validator.setError(1);
                    validator.setErrorMsg("法人ID：" + legalId + "は法人グループ名："+ groupPblDetails.get(0).getGroupPublish().getGroupName() +" に所属しているため、登録できません。");
                    return validator;
                }else{
                    for (ShtGroupPblDetail detail: groupPblDetails) {
                        if(detail.getGroupPublish().getGroupId().intValue() != request.getGroupId().intValue()){
                            validator.setError(1);
                            validator.setErrorMsg("法人ID：" + legalId + "は法人グループ名："+detail.getGroupPublish().getGroupName()+" に所属しているため、登録できません。");
                            return validator;
                        }
                    }
                }
            }
        }
        return validator;
    }

    @RequestMapping(value = "/edit-group-publish")
    public String editGroupPublish(Model model, @RequestParam Long groupId) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtGroupPublish groupPublish = groupPublishService.getGroupPublishById(groupId);
        if(groupPublish == null){
            return "list-group-publish";
        }
        List<ShtGroupPblDetail> groupPblDetails = groupPblDetailService.getGroupDetail(groupId);
        groupPublish.setGroupPblDetails(groupPblDetails);
        model.addAttribute("groupPublish", groupPublish);
        model.addAttribute("groupPblDetails", groupPblDetails);
        return "edit-group-publish";
    }

    @RequestMapping(value = "/update-group-publish", method = RequestMethod.POST)
    public @ResponseBody GroupPublishValidator updateGroupPublish(Model model, @RequestBody GroupPublishRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        GroupPublishValidator validator = new GroupPublishValidator();

        if(StringUtils.isEmpty(request.getGroupName()) || StringUtils.isEmpty(request.getGroupName().trim())){
            validator.setError(1);
            validator.setErrorMsg("法人グループ名を入力してください");
            return validator;
        }

        List<ShtGroupPublish> checkDuplicateGroupNames = groupPublishService.getGroupByGroupName(request.getGroupName());
        if(checkDuplicateGroupNames.size() > 0 && request.getGroupId() == null){
            validator.setError(1);
            validator.setErrorMsg("入力したグループ名は既に存在しています。変更をお願いします。");
            return validator;
        }else if(checkDuplicateGroupNames.size() > 0 && request.getGroupId() != null){
            for (ShtGroupPublish groupNameCheck: checkDuplicateGroupNames) {
                if(groupNameCheck.getGroupId().intValue() != request.getGroupId().intValue()){
                    validator.setError(1);
                    validator.setErrorMsg("入力したグループ名は既に存在しています。変更をお願いします。");
                    return validator;
                }
            }
        }

        if(CollectionUtils.isEmpty(request.getGroupDetails()) || request.getGroupDetails().size() < 2){
            validator.setError(1);
            validator.setErrorMsg("２つ以上の法人を指定してください。");
            return validator;
        }

        String legalStr = "";
        for (String legalId: request.getGroupDetails()) {
            if(legalStr.contains(legalId)){
                validator.setError(1);
                validator.setErrorMsg("法人ID："+legalId+" が重複しています。修正をお願いします。");
                return validator;
            }
            legalStr += "-" + legalId;
        }

        for (String legalId : request.getGroupDetails()) {
            if(StringUtils.isEmpty(legalId) || legalId.length() != 6){
                validator.setError(1);
                validator.setErrorMsg("7桁以上は入力不可");
                return validator;
            }

            String pattern = "([０-９]+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(legalId);
            if(m.find()){
                validator.setError(1);
                validator.setErrorMsg("半角の数字を入力してください。");
                return validator;
            }

            List<ShtGroupPblDetail> groupPblDetails = groupPblDetailService.getGroupDetailByLegalId(legalId);
            if(groupPblDetails.size() > 0){
                if(request.getGroupId() == null){
                    validator.setError(1);
                    validator.setErrorMsg("法人ID：" + legalId + "は法人グループ名："+groupPblDetails.get(0).getGroupPublish().getGroupName()+"に所属しているため、登録できません。");
                    return validator;
                }else{
                    for (ShtGroupPblDetail detail: groupPblDetails) {
                        if(detail.getGroupPublish().getGroupId().intValue() != request.getGroupId().intValue()){
                            validator.setError(1);
                            validator.setErrorMsg("法人ID：" + legalId + "は法人グループ名："+detail.getGroupPublish().getGroupName()+"に所属しているため、登録できません。");
                            return validator;
                        }
                    }
                }
            }
        }

        ShtGroupPublish groupPublish = groupPublishService.getGroupPublishById(request.getGroupId());
        if(groupPublish == null){
            validator.setError(1);
            validator.setErrorMsg("Invalid group");
            return validator;
        }
        groupPublish.setShmAdmin(shmAdmin);
        groupPublish.setGroupName(request.getGroupName());
        groupPublish.setGroupNo("0");
        groupPublish.setLegalNumber(new Long(request.getGroupDetails().size()));
        groupPublish.setUpdatedAt(LocalDateTime.now());
        ShtGroupPublish groupSaved = groupPublishService.saveGroupPublish(groupPublish);


        List<ShtGroupPblDetail> currentGroupDetails = groupPblDetailService.getGroupDetail(request.getGroupId());
        for (ShtGroupPblDetail detail: currentGroupDetails) {
            boolean checkRemove = true;
            for (String legal: request.getGroupDetails()) {
                if(legal.equals(detail.getLegalId())){
                    checkRemove = false;
                }
            }
            if(checkRemove){
                detail.setDeleteFlag(true);
                groupPblDetailService.saveDetail(detail);
            }
        }

        // create new legal ids
        for (String legalId: request.getGroupDetails()) {
            boolean checkAddNew = true;
            for (ShtGroupPblDetail detail: currentGroupDetails) {
                if(detail.getLegalId().equals(legalId)){
                    checkAddNew = false;
                }
            }
            if(checkAddNew){
                ShtGroupPblDetail shtGroupPblDetail = new ShtGroupPblDetail();
                shtGroupPblDetail.setGroupPublish(groupSaved);
                shtGroupPblDetail.setLegalId(legalId);
                groupPblDetailService.saveDetail(shtGroupPblDetail);
            }
        }
        return validator;
    }

    @RequestMapping(value = "/remove-group-publish", method = RequestMethod.PUT)
    @ResponseBody
    public String deleteGroupPublish(Model model, @RequestBody GroupPublishRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());
        if(request.getGroupId() != null){
            List<ShtGroupPblDetail> currentGroupDetails = groupPblDetailService.getGroupDetail(request.getGroupId());
            for (ShtGroupPblDetail detail: currentGroupDetails) {
                detail.setDeleteFlag(true);
                groupPblDetailService.saveDetail(detail);
            }

            List<ShmPost> posts = postService.getPostByGroupId(request.getGroupId());
            for (ShmPost post: posts) {
                post.setGroupId(null);
                post.setDestinationPublishType(ShmPost.DestinationPublishType.PRIVATE);
                postService.savePost(post);
            }

            ShtGroupPublish groupPublish = groupPublishService.getGroupPublishById(request.getGroupId());
            if(groupPublish != null){
                groupPublish.setDeleteFlag(true);
                groupPublishService.saveGroupPublish(groupPublish);
            }
        }
        return "redirect:list-group-publish?pageNumber=0";
    }
}
