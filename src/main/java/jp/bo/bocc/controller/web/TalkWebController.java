package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by HaiTH on 3/29/2017.
 */

@Controller
public class TalkWebController extends BoccBaseWebController{

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    TalkPurcService talkPurcService;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Autowired
    AddressService addressService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AdminService adminService;

    @RequestMapping("/list-talk")
    public String postDetail(@RequestParam long id, Model model) {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        ShmPost shmPost = postService.getPost(id);
        if (shmPost != null ) {
            ShmPost result = postService.buildListImagePathsForPost(shmPost);
            // get full address, parent category
            if(result != null){
                final ShmCategory postCategory = result.getPostCategory();
                if (postCategory != null) {
                    final ShmCategory shmCategory = categoryService.get(postCategory.getCategoryParentId());
                    result.setPostCategoryParent(shmCategory);
                }
                if (result.getShmUser() != null && result.getShmUser().getAddress() != null) {
                    ShmAddr shmAddr = result.getShmUser().getAddress();
                    if(result.getShmUser() != null && shmAddr != null){
                        result.getShmUser().getAddress().setFullAreaName(addressService.getFullAddress(shmAddr));
                    }
                }
            }
            List<ShtTalkPurc> talkList = talkPurcService.findTalksByPostId(shmPost);
            model.addAttribute("talks", talkList);
            model.addAttribute("post", result);
            final ShmUser shmUser = shmPost.getShmUser();
            model.addAttribute("user", shmUser);
            if (shmUser != null)
                model.addAttribute("userAvatar", userService.buildOriginalAvatarPathForUser(shmUser.getAvatar()));
            result.setPostReportTimes(postService.getReportTimeForPost(shmPost.getPostId()));
        }else {
            model.addAttribute("errorMsg", getMessage("SH_E100082"));
        }
        return "list-talk";
    }

    @RequestMapping("/talk-detail")
    public String talkDetail(@RequestParam long talkId, Model model) {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        List<ShtTalkPurcMsg> messageList = talkPurcMsgService.getTalkMsgList(talkId);

        model.addAttribute("msgList", messageList);
        if (CollectionUtils.isNotEmpty(messageList)) {
            ShmPost post = messageList.get(0).getShtTalkPurc().getShmPost();
            ShmUser user = messageList.get(0).getShtTalkPurc().getShmUser();
            ShmPost result = postService.buildListImagePathsForPost(post);

            // get full address, parent category
            if(result != null){
                final ShmCategory postCategory = result.getPostCategory();
                if(postCategory != null){
                    final ShmCategory shmCategory = categoryService.get(postCategory.getCategoryParentId());
                    post.setPostCategoryParent(shmCategory);
                }
                if (result.getShmUser() != null && result.getShmUser().getAddress() != null) {
                    ShmAddr shmAddr = result.getShmUser().getAddress();
                    if(post.getShmUser() != null){
                        post.getShmUser().getAddress().setFullAreaName(addressService.getFullAddress(shmAddr));
                    }
                }
            result.setPostReportTimes(postService.getReportTimeForPost(result.getPostId()));
            }

            model.addAttribute("post", result);
            model.addAttribute("partner", user);
            if (result.getShmUser() != null)
                model.addAttribute("userAvatar", userService.buildOriginalAvatarPathForUser(result.getShmUser().getAvatar()));
        }
        return "talk-detail";
    }
}
