package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.*;
import jp.bo.bocc.controller.web.response.DailyKpiExcelView;
import jp.bo.bocc.controller.web.response.MonthlyKpiListExcelView;
import jp.bo.bocc.controller.web.validator.*;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShrSysConfig;
import jp.bo.bocc.entity.ShtAdminCsvHst;
import jp.bo.bocc.entity.ShtKpiStorage;
import jp.bo.bocc.enums.AdminRoleEnum;
import jp.bo.bocc.enums.SysConfigEnum;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by HaiTH on 3/15/2017.
 */
@Controller
public class AdminUserWebController extends BoccBaseWebController{

    private final static Logger LOGGER = Logger.getLogger(AdminUserWebController.class.getName());

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Autowired
    AdminCsvHstService adminCsvHstService;

    @Autowired
    AdminService adminService;

    @Autowired
    AdminFormValidator adminFormValidator;

    @Autowired
    StorageService storageService;

    @Autowired
    KpiStorageService kpiStorageService;

    @Autowired
    @Qualifier("emailValidator")
    EmailValidator emailValidator;

    @Autowired
    EditAdminFormValidator editAdminFormValidator;

    @Autowired
    ShmAdminValidator shmAdminValidator;

    @Autowired
    ChangeAdminPasswordFormValidator changeAdminPasswordFormValidator;

    @Autowired
    private FileService fileService;

    @Autowired
    SysConfigService configService;

    @Autowired
    SNSMobilePushService snsMobilePushService;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    SyncExcService syncExcService;

    @Autowired
    private Environment env;

//    @Autowired
//    FireBaseService fireBaseService;

    @Autowired
    SysConfigService sysConfigService;

    @RequestMapping("/list-admin-user")
    public String listAdminUser(@RequestParam(defaultValue="1") Integer pageNumber, Model model) throws UnsupportedEncodingException {
        adminService.getAdminForOnlySuperAdmin(getEmail());

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentAdmin", userName);

        String baseUrlPagination = "/backend/list-admin-user";
        Page<ShmAdmin> page= adminService.getListAdminUser(pageNumber);
        List<ShmAdmin> admins = page.getContent();
        List<ShmAdmin> result = setAdminRoleAndFormatDate(admins);

        int sizePage = page.getTotalPages();
        model.addAttribute("sizePage", sizePage);
        model.addAttribute("listAdmin", result);
        model.addAttribute("ad", new ShmAdmin());

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, page.getTotalPages());
        model.addAttribute("deploymentLog", page);
        model.addAttribute("beginIndex", begin );
        model.addAttribute("endIndex", end );
        model.addAttribute("currentIndex", current);

        model.addAttribute("baseUrlPagination", baseUrlPagination);
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("curPage", pageNumber);
        model.addAttribute("startElement", ((pageNumber - 1) * maxRecordsInPage) + 1);
        model.addAttribute("curElements", ((pageNumber - 1) * maxRecordsInPage) + result.size());
        return "list-admin-user";
    }

    @ModelAttribute("roleList")
    public Map<Integer, String> getRoleList()
    {
        Map<Integer, String> roleList = new HashMap<Integer, String>();
        roleList.put(0, AdminRoleEnum.SUPPER_ADMIN.value());
        roleList.put(1, AdminRoleEnum.ADMIN.value());
        roleList.put(2, AdminRoleEnum.SITE_PATROL.value());
        roleList.put(3, AdminRoleEnum.CUSTOMER_SUPPORT.value());
        return roleList;
    }

    @RequestMapping(value="/search-admin-user", method = RequestMethod.GET)
    public String searchAdminUser(@ModelAttribute(value="ad") ShmAdmin ad,
                                  @RequestParam(defaultValue="1") Integer pageNumber, Model model,
                                  HttpServletRequest request) throws UnsupportedEncodingException {
        adminService.getAdminForOnlySuperAdmin(getEmail());

        String baseUrlPagination;
        String requestQueryString = request.getQueryString();
        if (requestQueryString == null) {
            baseUrlPagination = "/search-admin-user?";
        } else {
            baseUrlPagination = "/search-admin-user?"+ requestQueryString;

            baseUrlPagination = baseUrlPagination.replaceAll("&pageNumber=[0-9]*", "");
        }

        Page<ShmAdmin> page= adminService.searchAdminUser(ad, pageNumber);
        List<ShmAdmin> admins = page.getContent();
        List<ShmAdmin> result = setAdminRoleAndFormatDate(admins);

        model.addAttribute("listAdmin", result);

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, page.getTotalPages());

        model.addAttribute("deploymentLog", page);
        model.addAttribute("beginIndex", begin );
        model.addAttribute("endIndex", end );
        model.addAttribute("currentIndex", current);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentAdmin", userName);

        model.addAttribute("baseUrlPagination", baseUrlPagination);
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("curPage", pageNumber);
        model.addAttribute("startElement", ((pageNumber - 1) * maxRecordsInPage) + 1);
        model.addAttribute("curElements", ((pageNumber - 1) * maxRecordsInPage) + result.size());
        return "list-admin-user";
    }

    private List<ShmAdmin> setAdminRoleAndFormatDate(List<ShmAdmin> admins) throws UnsupportedEncodingException {
        List<ShmAdmin> result = new ArrayList<>();
        for (ShmAdmin shmAdmin : admins ) {
            shmAdmin.setAdminRoleTxt(AdminRoleEnum.getRoleAdmin(shmAdmin.getAdminRole()));
            result.add(shmAdmin);
        }
        return result;
    }

    @RequestMapping("/edit-admin-user")
    public String editAdminUser(@RequestParam long id, Model model, HttpServletRequest request) {
        adminService.getAdminForOnlySuperAdmin(getEmail());

        HttpSession session = request.getSession();
        ShmAdmin shmAdmin = adminService.showAdminById(id);
        model.addAttribute("ad",shmAdmin);
        session.setAttribute("ssAdminId",shmAdmin.getAdminId());
        session.setAttribute("ssAdminName",shmAdmin.getAdminName());
        session.setAttribute("ssAdminEmail",shmAdmin.getAdminEmail());
        model.addAttribute("oldPassword", shmAdmin.getAdminPwd());
        return "edit-admin-user";
    }

    @RequestMapping("/change-admin-password")
    public String changeAdminPassword(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        ShmAdmin shmAdmin = adminService.getAdminByEmail(getEmail());
        model.addAttribute("ad",shmAdmin);
        session.setAttribute("ssAdminId",shmAdmin.getAdminId());
        session.setAttribute("ssAdminName",shmAdmin.getAdminName());
        session.setAttribute("ssAdminEmail",shmAdmin.getAdminEmail());
        model.addAttribute("oldPassword", shmAdmin.getAdminPwd());
        return "change-admin-password";
    }

    @RequestMapping(value = "/update-admin-password", method = RequestMethod.POST)
    public String updateAdminPassword(@ModelAttribute(value="ad") ShmAdmin ad, Model model,
                                  HttpServletRequest request, BindingResult bindingResult) throws Exception {
        try {
            HttpSession session = request.getSession();
            long id = (long)session.getAttribute("ssAdminId");
            final ShmAdmin adminById = adminService.getAdminById(id);
            ad.setAdminPwd(adminById.getAdminPwd());

            if (StringUtils.isNotEmpty(ad.getPwdFresh())) {
                if (ad.getPwdFresh().length() < 8 || ad.getPwdFresh().length() > 20) {
                    model.addAttribute("ad", ad);
                    model.addAttribute("errorFreshPwd", getMessage("SH_E100078"));
                    return "change-admin-password";
                }

                if(!ad.getPwdFresh().equals(ad.getConfirmAdminPwd())){
                    final String error = getMessage("SH_E100050");
                    model.addAttribute("ad", ad);
                    model.addAttribute("errorConfirmPwd",error);
                    return "change-admin-password";
                }
            }else {
                model.addAttribute("ad", ad);
                model.addAttribute("errorFreshPwd", getMessage("SH_E100154"));
                return "change-admin-password";
            }

            if(StringUtils.isNotEmpty(ad.getConfirmAdminPwd()) && StringUtils.isEmpty(ad.getPwdFresh())){
                model.addAttribute("ad", ad);
                model.addAttribute("errorFreshPwd", getMessage("SH_E100154"));
                return "change-admin-password";
            }

            changeAdminPasswordFormValidator.validate(ad, bindingResult);
            if (bindingResult.hasErrors()) {
                return "change-admin-password";
            }
            adminService.updateAdminPassword(ad,id);

            session.removeAttribute("ssAdmin");
            session.removeAttribute("ssAdminName");
            session.removeAttribute("ssAdminEmail");
            session.invalidate();
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "change-admin-password";
        }
        return "redirect:/backend/login?changepwd";
    }

    @RequestMapping(value = "/update-admin", method = RequestMethod.POST)
    public String updateAdminUser(@ModelAttribute(value="ad") ShmAdmin ad, Model model,
                                  HttpServletRequest request, BindingResult bindingResult) throws Exception {
        try {
            adminService.getAdminForOnlySuperAdmin(getEmail());

            HttpSession session = request.getSession();
            long id = (long)session.getAttribute("ssAdminId");
            final ShmAdmin adminById = adminService.getAdminById(id);
            ad.setAdminPwd(adminById.getAdminPwd());
            if (bindingResult.hasErrors()) {
                return "edit-admin-user";
            }

            if(StringUtils.isNotEmpty(ad.getPwdFresh())){
                if (ad.getPwdFresh().length() < 8 || ad.getPwdFresh().length() > 20) {
                    model.addAttribute("ad", ad);
                    model.addAttribute("errorFreshPwd", getMessage("SH_E100078"));
                    return "edit-admin-user";
                }
                if(!ad.getPwdFresh().equals(ad.getConfirmAdminPwd())){
                    final String error = getMessage("SH_E100050");
                    model.addAttribute("ad", ad);
                    model.addAttribute("errorConfirmPwd",error);
                    return "edit-admin-user";
                }
            }else{
                if(StringUtils.isNotEmpty(ad.getConfirmAdminPwd())){
                    model.addAttribute("ad", ad);
                    model.addAttribute("errorFreshPwd", getMessage("SH_E100154"));
                    return "edit-admin-user";
                }
            }

            if(ad.getAdminRole() == null || ad.getAdminRole() < 0){
                model.addAttribute("ad", ad);
                model.addAttribute("errorAdminRole", getMessage("SH_E100040"));
                return "edit-admin-user";
            }

            shmAdminValidator.validate(ad, bindingResult);
            if (bindingResult.hasErrors()) {
                return "edit-admin-user";
            }

            if(StringUtils.isNotEmpty(ad.getPwdFresh())){
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                ad.setPwdFresh(encoder.encode(ad.getPwdFresh()));
            }else{
                ad.setPwdFresh(adminById.getAdminPwd());
                ad.setConfirmAdminPwd(adminById.getAdminPwd());
            }

            adminService.updateAdminUser(ad,id);

            session.removeAttribute("ssAdmin");
            session.removeAttribute("ssAdminName");
            session.removeAttribute("ssAdminEmail");

            List<ShmAdmin> result = adminService.getListAdminUser(1).getContent();
            model.addAttribute("listAdmin", result);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "edit-admin-user";
        }
        return "redirect:list-admin-user";
    }

    @RequestMapping(value = "register-admin")
    public String registerAdminUser(Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        model.addAttribute("ad", new ShmAdmin());
        return "register-admin";
    }

    @RequestMapping(value = "/create-admin", method = RequestMethod.POST)
    public String createAdminUser(@ModelAttribute(value="ad") ShmAdmin ad, Model model, BindingResult bindingResult) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        adminFormValidator.validate(ad,bindingResult);

        if (bindingResult.hasErrors()) {
            return "register-admin";
        }

        if (adminService.findAdminEmail(ad.getAdminEmail())) {
            model.addAttribute("adminEmail", getMessage("SH_E100037"));
            return "register-admin";
        }

        if(! ad.getAdminPwd().equals(ad.getConfirmAdminPwd())){
            model.addAttribute("ad", ad);
            model.addAttribute("errorConfirmPwd", getMessage("SH_E100035"));
            return "register-admin";
        }

        if (ad.getAdminPwd().length() < 8 || ad.getAdminPwd().length() > 20) {
            model.addAttribute("ad", ad);
            model.addAttribute("errorConfirmPwd", getMessage("SH_E100078"));
            return "register-admin";
        }

        adminService.createAdminUser(ad);
        List<ShmAdmin> result = adminService.getListAdminUser(1).getContent();
        model.addAttribute("listAdmin", result);
        return "redirect:list-admin-user";
    }

    @RequestMapping("/delete-admin-user")
    @ResponseBody
    public String deleteAdminUser(@RequestBody AdminRequest adminRequest, Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        if(adminRequest.getAdminId() != null){
            adminService.deleteAdminUser(adminRequest.getAdminId());
        }

        List<ShmAdmin> result = adminService.getListAdminUser(1).getContent();
        model.addAttribute("listAdmin", result);
        return "redirect:list-admin-user";
    }

    @RequestMapping("/detail-admin-user")
    public String detailAdminUser(@RequestParam long id, Model model) throws UnsupportedEncodingException {
        adminService.getAdminForOnlySuperAdmin(getEmail());

        ShmAdmin ad = adminService.showAdminById(id);
        List<ShmAdmin> admins = new ArrayList<ShmAdmin>();
        admins.add(ad);
        List<ShmAdmin> result = setAdminRoleAndFormatDate(admins);

        model.addAttribute("listAdmin", result);
        return "detail-admin-user";
    }

    @RequestMapping("/log-export-csv")
    public String logExportCsv(Model model, @RequestParam(defaultValue = "0") Integer page) throws ParseException, SchedulerException {
        adminService.getAdminForSuperAdminAndAdmin(getEmail());
        Pageable pageable = new PageRequest(page , maxRecordsInPage, Sort.Direction.DESC, "createdAt");
        Page<ShtAdminCsvHst> adminCsvHsts = adminCsvHstService.getAdminCsvHst(pageable);
        if (adminCsvHsts.getSize() > 0) {
            model.addAttribute("adminCsvHsts", adminCsvHsts.getContent());
            model.addAttribute("totalPage", adminCsvHsts.getTotalPages() - 1);
            model.addAttribute("totalElements", adminCsvHsts.getTotalElements());
            model.addAttribute("curPage", page);
            model.addAttribute("startElement", (page * maxRecordsInPage) + 1);
            model.addAttribute("curElements", (page * maxRecordsInPage) + adminCsvHsts.getContent().size());
        }
        return "log-export-csv";
    }

    @RequestMapping(value = "/torino-csv", method = RequestMethod.GET)
    public String exportToCsv(Model model, @ModelAttribute("torinoFormSearch") TorinoFormRequest formRequest) {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdmin(getEmail());
        return "torino-csv";
    }

    @RequestMapping(value = "/export/torino-csv", method = RequestMethod.GET)
    @ResponseBody
    public void exportTorinoCsv(Model model, @ModelAttribute("torinoFormSearch") TorinoFormRequest formRequest, HttpServletResponse response) throws IOException, ParseException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdmin(getEmail());
        if (formRequest.getPeriodDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            adminService.exportCSVForTorinoCC(admin, formatter.parse(formRequest.getPeriodDate() + "/01"), response);
            adminCsvHstService.save(admin, ShtAdminCsvHst.CSV_TYPE.TORINO_CSV);
        }
    }


    @RequestMapping(value = "/export/kpi-daily", method = RequestMethod.GET)
    public ModelAndView exportKpiDaily(Model model, @ModelAttribute("kpiRequest") KpiRequest kpiRequest, HttpServletResponse response) throws IOException, ParseException {
        ShmAdmin admin = adminService.getAdminForOnlySuperAdmin(getEmail());
        adminService.getAdminForSuperAdminAndAdmin(getEmail());
        DateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();
        List<ShtKpiStorage> kpiDailys = new ArrayList<>();
        List<ShtKpiStorage> kpiDailysTmp = new ArrayList<>();
        if(StringUtils.isNotEmpty(kpiRequest.getFromDate()) && StringUtils.isNotEmpty(kpiRequest.getToDate())){
            try {
                beginCalendar.setTime(formater.parse(kpiRequest.getFromDate()));
                finishCalendar.setTime(formater.parse(kpiRequest.getToDate()));
                finishCalendar.add(Calendar.DATE, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            while (beginCalendar.before(finishCalendar)) {
                // add one day to date per loop
                String day = formater.format(beginCalendar.getTime()).toUpperCase();
                ShtKpiStorage dailyResponse = kpiStorageService.getKpiByDay(day);
                kpiDailys.add(dailyResponse);
                beginCalendar.add(Calendar.DATE, 1);
            }

            if(kpiDailys.size() > 0){
                for(int i = kpiDailys.size() - 1; i >= 0; i --){
                    kpiDailysTmp.add(kpiDailys.get(i));
                }
            }
        }
        return new ModelAndView (new DailyKpiExcelView(),"kpiDailys", kpiDailysTmp);
    }

    @RequestMapping("/kpi-daily")
    public String kpiDaily(Model model, @ModelAttribute("kpiRequest") KpiRequest kpiRequest) throws ParseException, SchedulerException {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        DateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        Calendar tempCalendar = Calendar.getInstance();
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();
        if(StringUtils.isEmpty(kpiRequest.getFromDate()) && StringUtils.isEmpty(kpiRequest.getToDate())){
            Date currentDate = new Date();
            String currentDateStr = formater.format(currentDate);
            kpiRequest.setToDate(currentDateStr);

            tempCalendar.setTime(formater.parse(currentDateStr));
            tempCalendar.add(Calendar.DATE, -45);
            String fromDate = formater.format(tempCalendar.getTime()).toUpperCase();
            kpiRequest.setFromDate(fromDate);
        }
        if(StringUtils.isNotEmpty(kpiRequest.getFromDate()) && StringUtils.isNotEmpty(kpiRequest.getToDate())){
            try {
                beginCalendar.setTime(formater.parse(kpiRequest.getFromDate()));
                finishCalendar.setTime(formater.parse(kpiRequest.getToDate()));
                finishCalendar.add(Calendar.DATE, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<ShtKpiStorage> kpiDailys = new ArrayList<>();
            while (beginCalendar.before(finishCalendar)) {
                // add one day to date per loop
                String day = formater.format(beginCalendar.getTime()).toUpperCase();
                ShtKpiStorage response = kpiStorageService.getKpiByDay(day);
                kpiDailys.add(response);
                beginCalendar.add(Calendar.DATE, 1);
            }
            List<ShtKpiStorage> kpiDailysTmp = new ArrayList<>();
            if(kpiDailys.size() > 0){
                for(int i = kpiDailys.size() - 1; i >= 0; i --){
                    kpiDailysTmp.add(kpiDailys.get(i));
                }
            }
            model.addAttribute("kpiRequest", kpiRequest);
            if (kpiDailys.size() > 0) {
                model.addAttribute("kpiDailys", kpiDailysTmp);
                int totalPage = kpiDailys.size() / maxRecordsInPage;
                model.addAttribute("totalPage", totalPage);
                model.addAttribute("totalElements", kpiDailys.size());
                model.addAttribute("curPage", kpiRequest.getPage());
                model.addAttribute("startElement", (kpiRequest.getPage() * maxRecordsInPage) + 1);
                model.addAttribute("curElements", (kpiRequest.getPage() * maxRecordsInPage) + kpiDailys.size());
            }
        }

        return "kpi-daily";
    }

    @RequestMapping(value = "/export/kpi-monthly", method = RequestMethod.GET)
    public ModelAndView exportKpiMonthly(Model model, @ModelAttribute("kpiRequest") KpiRequest kpiRequest, HttpServletResponse response) throws IOException, ParseException {
        ShmAdmin admin = adminService.getAdminForOnlySuperAdmin(getEmail());
        DateFormat formater = new SimpleDateFormat("yyyy/MM");
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();
        List<ShtKpiStorage> kpiMonthlys = new ArrayList<>();
        List<ShtKpiStorage> kpiMonthlysTmp = new ArrayList<>();
        if(StringUtils.isNotEmpty(kpiRequest.getFromMonth()) && StringUtils.isNotEmpty(kpiRequest.getToMonth())){
            try {
                beginCalendar.setTime(formater.parse(kpiRequest.getFromMonth()));
                finishCalendar.setTime(formater.parse(kpiRequest.getToMonth()));
                finishCalendar.add(Calendar.MONTH, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String month = formater.format(beginCalendar.getTime()).toUpperCase();
                ShtKpiStorage responseByMonth = kpiStorageService.getKpiResponseByMonth(month);
                responseByMonth.setQueryTime(month);
                kpiMonthlys.add(responseByMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }
            if(kpiMonthlys.size() > 0){
                for(int i = kpiMonthlys.size() - 1; i >= 0; i --){
                    kpiMonthlysTmp.add(kpiMonthlys.get(i));
                }
            }
        }
        return new ModelAndView (new MonthlyKpiListExcelView(),"kpiMonthlys", kpiMonthlysTmp);
    }

    @RequestMapping("/kpi-monthly")
    public String kpiMonthly(Model model, @ModelAttribute("kpiRequest") KpiRequest kpiRequest) throws ParseException, SchedulerException {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        DateFormat formater = new SimpleDateFormat("yyyy/MM");
        Calendar tempCalendar = Calendar.getInstance();
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();

        if(StringUtils.isEmpty(kpiRequest.getFromMonth()) && StringUtils.isEmpty(kpiRequest.getToMonth())){
            Date currentDate = new Date();
            DateFormat formaterYYMM = new SimpleDateFormat("yyyy/MM");
            String currentMonth = formaterYYMM.format(currentDate);
            tempCalendar.setTime(formater.parse(currentMonth));
            tempCalendar.add(Calendar.MONTH, -13);
            String fromMonth = formater.format(tempCalendar.getTime()).toUpperCase();

            kpiRequest.setFromMonth(fromMonth);
            kpiRequest.setToMonth(currentMonth);
        }

        if(StringUtils.isNotEmpty(kpiRequest.getFromMonth()) && StringUtils.isNotEmpty(kpiRequest.getToMonth())){
            try {
                beginCalendar.setTime(formater.parse(kpiRequest.getFromMonth()));
                finishCalendar.setTime(formater.parse(kpiRequest.getToMonth()));
                finishCalendar.add(Calendar.MONTH, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<ShtKpiStorage> kpiMonthlys = new ArrayList<>();
            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String month = formater.format(beginCalendar.getTime()).toUpperCase();
                ShtKpiStorage response = kpiStorageService.getKpiResponseByMonth(month);
                response.setQueryTime(month);
                kpiMonthlys.add(response);
                beginCalendar.add(Calendar.MONTH, 1);
            }

            List<ShtKpiStorage> kpiMonthlysTmp = new ArrayList<>();
            if(kpiMonthlys.size() > 0){
                for(int i = kpiMonthlys.size() - 1; i >= 0; i --){
                    kpiMonthlysTmp.add(kpiMonthlys.get(i));
                }
            }

            model.addAttribute("kpiRequest", kpiRequest);
            if (kpiMonthlys.size() > 0) {
                model.addAttribute("kpiMonthlys", kpiMonthlysTmp);
                int totalPage = kpiMonthlys.size() / maxRecordsInPage;
                model.addAttribute("totalPage", totalPage);
                model.addAttribute("totalElements", kpiMonthlys.size());
                model.addAttribute("curPage", kpiRequest.getPage());
                model.addAttribute("startElement", (kpiRequest.getPage() * maxRecordsInPage) + 1);
                model.addAttribute("curElements", (kpiRequest.getPage() * maxRecordsInPage) + kpiMonthlys.size());
            }
        }

        return "kpi-monthly";
    }

    @RequestMapping(value = "/post-sample-upload", method = RequestMethod.GET)
    public String uploadPostSample(Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        return "post-sample-upload";
    }

    @RequestMapping(value = "/post-sample-upload", method = RequestMethod.POST)
    public String doUploadPostSample(Model model, @RequestParam("excelFile") MultipartFile excelFile, @RequestParam("imgZipFile") MultipartFile imgZipFile) throws IOException, InvalidFormatException {
        try {
        // the first unzip file
        String unzipDirName = fileService.processUnzipImportPostImage(imgZipFile);
        // then read and import post
        fileService.processImportSamplePost(excelFile, unzipDirName);
        // finally delete folder temp
        storageService.deleteImgTempDirectory();

        model.addAttribute("message", "Import sample posts successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "post-sample-upload";
        }
        return "post-sample-upload";
    }

    @RequestMapping(value = "/maintain-system", method = RequestMethod.GET)
    public String maintainSystem(Model model, @ModelAttribute("maintainForm") MaintainRequest maintainForm) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShrSysConfig sysConfig = configService.getSysConfig(SysConfigEnum.MAINTAIN_MODE);
        if (sysConfig != null && sysConfig.getSysConfigValue() == 1)
            maintainForm.setMaintain(true);
        maintainForm = new MaintainRequest();
        maintainForm.setSysConfigMsg(sysConfig.getSysConfigMsg());
        maintainForm.setMaintain(sysConfig.getSysConfigValue() == 1 ? true : false);

        String restrictIps = env.getProperty("ip.hblab1") + ", " + env.getProperty("ip.hblab2") + ", " + env.getProperty("ip.bo") + ", " + env.getProperty("ip.ib");

        model.addAttribute("ipRestrict", restrictIps);
        model.addAttribute("maintainForm", maintainForm);
        return "maintain";
    }

    @RequestMapping(value = "process/maintain-system", method = RequestMethod.POST)
    public String processMaintainMode(Model model, @ModelAttribute("maintainForm") MaintainRequest maintainForm) {
        boolean status = configService.saveSysConfig(maintainForm);
        if (StringUtils.isEmpty(maintainForm.getSysConfigMsg())) {
            model.addAttribute("sysConfigMsgError", "メッセージ内容を入力してください。");
            return "maintain";
        }

        String restrictIps = env.getProperty("ip.hblab1") + ", " + env.getProperty("ip.hblab2") + ", " + env.getProperty("ip.bo") + ", " + env.getProperty("ip.ib");
        model.addAttribute("ipRestrict", restrictIps);
        model.addAttribute("maintainForm", maintainForm);
        model.addAttribute("successMsg", "成功");
        return "maintain";
    }

    @RequestMapping(value = "/kpi-sync", method = RequestMethod.GET)
    public String kpiSyncData(Model model, @RequestParam(value = "successMsg", required = false) String successMsg) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        model.addAttribute("successMsg", successMsg);
        return "kpi-sync";
    }

    @RequestMapping(value = "/kpi-sync-process", method = RequestMethod.POST)
    public String processSyncDataKpi(Model model) throws ParseException {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        // process sync data in another thread
        kpiStorageService.syncKpiData();
        //sync completely
        model.addAttribute("successMsg", "KPIの過去データを同期するのは実行中です。最初の同期には非常に時間がかかりますので、ご了承ください。!");
        return "redirect:/backend/kpi-sync";
    }

    @RequestMapping(value = "/topic-migration", method = RequestMethod.GET)
    public String doSubscribeExistinguserIntoTopic(Model model, @ModelAttribute("topicForm") TopicRequest topicForm) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShrSysConfig sysConfig = configService.getSysConfig(SysConfigEnum.PUSH_SUBSCRIBE_INTO_TOPIC_ALL);
        if (sysConfig != null && sysConfig.getSysConfigValue() == 1)
            topicForm.setSubscribeTopicAll(true);
        topicForm = new TopicRequest();
        topicForm.setSubscribeTopicAll(sysConfig.getSysConfigValue() == 1 ? true : false);
        model.addAttribute("topicForm", topicForm);
        return "topic-migration";
    }

    @RequestMapping(value = "process/topic-migration", method = RequestMethod.POST)
    public String processSubscribeExistingUserIntoTopic(@RequestBody TopicRequest topicForm, Model model) {

        //subscribe into TOPIC all
        snsMobilePushService.subscribeTopicForExistingUser();

        //migrating completely
        topicForm.setSubscribeTopicAll(true);
        configService.saveSysConfig(topicForm);

        model.addAttribute("topicForm", topicForm);
        model.addAttribute("successMsg", "成功");
        return "redirect:/backend/topic-migration";
    }

    @RequestMapping(value = "/valid-version", method = RequestMethod.GET)
    public String validVersion(Model model, @ModelAttribute("validVersionForm") ValidVersionRequest validVersionRequest) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShrSysConfig sysConfig = configService.getSysConfig(SysConfigEnum.VALID_VERSION);

        validVersionRequest = new ValidVersionRequest();
        validVersionRequest.setSysConfigMsg(sysConfig.getSysConfigMsg());
        validVersionRequest.setSysConfigValues(sysConfig.getSysConfigValues());
        model.addAttribute("validVersionForm", validVersionRequest);
        return "valid-version";
    }

    @RequestMapping(value = "update-valid-version", method = RequestMethod.POST)
    public String processValidVersion(Model model, @ModelAttribute("validVersionForm") ValidVersionRequest validVersionRequest) {

        boolean status = configService.saveSysConfig(validVersionRequest);
        if (StringUtils.isEmpty(validVersionRequest.getSysConfigMsg())) {
            model.addAttribute("sysConfigMsgError", "メッセージ内容を入力してください。");
            return "valid-version";
        }
        model.addAttribute("validVersionForm", validVersionRequest);
        model.addAttribute("successMsg", "成功");
        return "redirect:/backend/valid-version";
    }

    @RequestMapping(value = "/sync-date-config", method = RequestMethod.GET)
    public String syncDateConfig(Model model, @ModelAttribute("syncDateConfigForm") SyncDateConfigureRequest syncDateConfigureRequest) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShrSysConfig sysConfig = configService.getSysConfig(SysConfigEnum.SYNC_EXC_DATE_MONTHLY);

        syncDateConfigureRequest = new SyncDateConfigureRequest();
        syncDateConfigureRequest.setDayOfMonth(new Long(sysConfig.getSysConfigValue()));
        model.addAttribute("syncDateConfigForm", syncDateConfigureRequest);
        return "sync-date-config";
    }

    @RequestMapping(value = "update-sync-date", method = RequestMethod.POST)
    public String updateSyncDateConfig(Model model, @ModelAttribute("syncDateConfigForm") SyncDateConfigureRequest syncDateConfigureRequest) {
        syncExcService.updateConfigureDate(syncDateConfigureRequest.getDayOfMonth().intValue());
        configService.saveSysConfig(syncDateConfigureRequest);
        model.addAttribute("syncDateConfigForm", syncDateConfigureRequest);
        model.addAttribute("successMsg", "成功");
        return "redirect:/backend/sync-date-config";
    }

    @RequestMapping(value = "/user-left-sync", method = RequestMethod.GET)
    public String usersLeftSync(Model model) throws ParseException {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        return "user-left-sync";
    }

    @RequestMapping(value = "/sync-user-process", method = RequestMethod.POST)
    public String processUsersLeftSync(Model model) throws ParseException {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        syncExcService.syncUsersManually();
        //sync completely
        model.addAttribute("successMsg", "Sync done!");
        return "redirect:/backend/user-left-sync";
    }

    @RequestMapping(value = "/migration-msg", method = RequestMethod.GET)
    public String redirectMigrationTalkPurchase(Model model) {
        ShrSysConfig sysConfig = configService.getSysConfig(SysConfigEnum.MIGRATION_MSG_TASLK_PURCHASE);
        if (sysConfig != null && sysConfig.getSysConfigValue() == 1)
            model.addAttribute("migrationTalkpurchaseFlag", true);

        sysConfig = configService.getSysConfig(SysConfigEnum.MIGRATION_MSG_QA);
        if (sysConfig != null && sysConfig.getSysConfigValue() == 1)
            model.addAttribute("migrationQaFlag", true);

        return "migration-msg";
    }

    @RequestMapping(value = "process/migration-msg-tarkpurchase", method = RequestMethod.POST)
    public String processMigrationTalkPurchase(Model model) throws Exception {
        try {

            ShmAdmin admin = adminService.getAdminForSuperAdminAndAdmin(getEmail());
          //  fireBaseService.migrationTalkPurchaseMsg();

            // migrated done.
            sysConfigService.updateTalkPurchaseConfig(true);
        } catch (Exception e) {
            throw e;
        }
        return "redirect:/backend/migration-msg";
    }

    @RequestMapping(value = "process/migration-msg-qa", method = RequestMethod.POST)
    public String processQa(Model model) {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdmin(getEmail());
    //    fireBaseService.migrationTalkQAMsg();
        // migrated done.
        sysConfigService.updateQaConfig(true);
        return "redirect:/backend/migration-msg";
    }
}
