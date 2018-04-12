package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author manhnt
 */
@Controller
public class HelloController extends BoccBaseWebController {

    @RequestMapping(value = {"/","/backend"})
    public String sample(Model model) {
        return "dashboard";
    }

    @RequestMapping("/error")
    public String error(Model model) {
        return "error";
    }
}
