package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.AdminNgRequest;
import jp.bo.bocc.controller.web.validator.AdminNgValidator;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmAdminNg;
import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import jp.bo.bocc.service.AdminNgService;
import jp.bo.bocc.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
@Controller
public class AdminNgWebController extends BoccBaseWebController {

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Autowired
    AdminNgService adminNgService;

    @Autowired
    AdminNgValidator adminNgValidator;

    @Autowired
    AdminService adminService;

    @RequestMapping(value = "list-admin-ng", method = RequestMethod.GET)
    public String listAdminNg(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @RequestParam(value = "adminNgContent", required = false) String adminNgContent,  Model model) {
        adminService.getAdminForSuperAdminAndAdmin(getEmail());
        ShmAdminNgDTO adminNgDTO = new ShmAdminNgDTO();
        adminNgDTO.setAdminNgContent(adminNgContent);
        if (pageNumber == null)
            pageNumber = 0;
        final Pageable page100Item = createPage100Item(pageNumber);
        Page<ShmAdminNgDTO> page = adminNgService.getListAdminNg(adminNgDTO, page100Item);
        List<ShmAdminNgDTO> adminNgs = page.getContent();

       if(adminNgs.size()==0) {
           String nullResult = "データはありません !";
           model.addAttribute("nullResult", nullResult);
       }

        int sizePage = page.getTotalPages();

        model.addAttribute("sizeResult", adminNgs.size());
        model.addAttribute("listAdminNg", adminNgs);
        model.addAttribute("adminNg", adminNgDTO);
        int current = page.getNumber();
        model.addAttribute("deploymentLog", page);
        model.addAttribute("currentIndex", current);

        model.addAttribute("adminNg", new ShmAdminNg());
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("curPage", current);
        model.addAttribute("startElement", ((pageNumber) * maxRecordsInPage) + 1);
        model.addAttribute("curElements", ((pageNumber) * maxRecordsInPage) + adminNgs.size());

        return "list-admin-ng";
    }

    @RequestMapping(value = "/create-admin-ng")
    public String createAdminNg(Model model) {
        adminService.getAdminForSuperAdminAndAdmin(getEmail());

        model.addAttribute("adminNg", new ShmAdminNg());
        return "create-admin-ng";
    }

    @RequestMapping(value = "/add-admin-ng", method = RequestMethod.POST)
    public String addAdminNg(@ModelAttribute(value = "adminNg") ShmAdminNg adminNg, Model model, BindingResult bindingResult) {
        final ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdmin(getEmail());

        adminNgValidator.validate(adminNg, bindingResult);
        if (bindingResult.hasErrors()){
            return "create-admin-ng";
        }

        boolean tmp = adminNgService.findAdminNgContent(adminNg.getAdminNgContent());
        if (tmp) {
            String errorAdminNgContent = "この言葉が既に登録されています。";
            model.addAttribute("adminNg",adminNg);
            model.addAttribute("errorAdminNgContent",errorAdminNgContent);
            return "create-admin-ng";
        }

        adminNgService.createAdminNg(adminNg, shmAdmin);
        return "redirect:list-admin-ng";
    }

    @RequestMapping("/edit-admin-ng")
    public String editAdminNg(@RequestParam long id, Model model, HttpServletRequest request) {
        final ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdmin(getEmail());

        HttpSession session =request.getSession();
        ShmAdminNg shmAdminNg = adminNgService.showAdminNgById(id);
        model.addAttribute("adminNg",shmAdminNg);
        session.setAttribute("ssAdminNgId",shmAdminNg.getAdminNgId());
        session.setAttribute("ssAdminNgContent",shmAdminNg.getAdminNgContent());
        return "edit-admin-ng";
    }

    @RequestMapping(value = "/update-admin-ng", method = RequestMethod.POST)
    public String updateAdminNg(@ModelAttribute(value="adminNg") ShmAdminNg adminNg, Model model,
                                HttpServletRequest request, BindingResult bindingResult)  {
        final ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdmin(getEmail());

        HttpSession session =request.getSession();
        long id =(long)session.getAttribute("ssAdminNgId");
        String adminNgContent = (String)session.getAttribute("ssAdminNgContent");
        adminNgValidator.validate(adminNg, bindingResult);
        if (bindingResult.hasErrors()) {
            return "edit-admin-ng";
        }

        if(! adminNg.getAdminNgContent().equals(adminNgContent)){
            boolean tmp =adminNgService.findAdminNgContent(adminNg.getAdminNgContent());
            if (tmp) {
                String errorAdminNgContent = "この言葉は存在しています。";
                model.addAttribute("adminNg",adminNg);
                model.addAttribute("errorAdminNgContent",errorAdminNgContent);
                return "edit-admin-ng";
            }
        }

        adminNgService.updateAdminNg(adminNg,id, shmAdmin);
        return "redirect:list-admin-ng";
    }

    @RequestMapping(value = "/remove-admin-ng", method = RequestMethod.PUT)
    @ResponseBody
    public String deleteAdminNg(Model model, @RequestBody AdminNgRequest adminNgRequest) {
        final ShmAdmin shmAdmin = adminService.getAdminForSuperAdminAndAdmin(getEmail());
        if(adminNgRequest.getAdminNgId() != null){
            adminNgService.deleteAdminNg(adminNgRequest.getAdminNgId(),shmAdmin);
        }
        return "redirect:list-admin-ng?pageNumber=0";
    }

}
