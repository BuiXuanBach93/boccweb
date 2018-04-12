package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.dto.TorinoCsvDTO;
import jp.bo.bocc.repository.AdminRepository;
import jp.bo.bocc.repository.criteria.BaseSpecification;
import jp.bo.bocc.repository.criteria.SearchCriteria;
import jp.bo.bocc.service.AdminService;
import jp.bo.bocc.service.ExportCsvService;
import jp.bo.bocc.service.PostService;
import jp.bo.bocc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by HaiTH on 3/16/2017.
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Value("${torino.menu.number}")
    private int torinoMenuNumber;

    @Value("${torino.plan.id}")
    private String torinoPlanId;

    @Autowired
    MessageSource messageSource;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ExportCsvService exportCsvService;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Override
    public Page<ShmAdmin> getListAdminUser(Integer pageNumber) {

        Pageable pageRequest =
                new PageRequest(pageNumber - 1, maxRecordsInPage);

        return adminRepository.findAllOrderByCreatedAt(new Boolean(true), pageRequest);
    }

    @Override
    public Page<ShmAdmin> searchAdminUser(ShmAdmin shmAdmin, Integer pageNumber) {
        Page<ShmAdmin> searchAdmin;

        PageRequest pageRequest =
                new PageRequest(pageNumber - 1, maxRecordsInPage, Sort.Direction.DESC, "updatedAt");
        BaseSpecification adminSpec1 = new BaseSpecification
                (new SearchCriteria("adminRole", ":", shmAdmin.getAdminRole()));
        BaseSpecification adminSpec2 = new BaseSpecification
                (new SearchCriteria("adminName", ":", shmAdmin.getAdminName()));
        BaseSpecification adminNgSpec3 = new BaseSpecification
                (new SearchCriteria("deleteFlag", "!=", true));

        if (shmAdmin.getAdminRole() != null && shmAdmin.getAdminName() != null) {
            searchAdmin = adminRepository.findAll(Specifications.where(adminSpec1).and(adminSpec2).and(adminNgSpec3), pageRequest);

        } else {
            searchAdmin = adminRepository.findAll((Specifications.where(adminSpec1).or(adminSpec2)).and(adminNgSpec3), pageRequest);
        }
        return searchAdmin;
    }

    @Override
    public ShmAdmin getShmAdminByAdminNameAndDeleteFlag(String adminName) {
        return adminRepository.findByAdminNameAndDeleteFlag(adminName, false);
    }

    @Override
    public ShmAdmin getAdminForOnlySuperAdmin(String email) {
        return adminRepository.findByAdminEmailAndDeleteFlag(email, false);
    }

    @Override
    public ShmAdmin getAdminForSuperAdminAndAdmin(String email) {
        return adminRepository.findByAdminEmailAndDeleteFlag(email, false);
    }

    @Override
    public ShmAdmin getAdminForSuperAdminAndAdminAndCusSupport(String email) {
        return adminRepository.findByAdminEmailAndDeleteFlag(email, false);
    }

    @Override
    @Transactional(readOnly = true)
    public ShmAdmin getAdminForSuperAdminAndAdminAndSitePatrol(String email) {
        return adminRepository.findByAdminEmailAndDeleteFlag(email, false);
    }

    @Override
    public ShmAdmin getAdminByEmail(String email) {
        return adminRepository.findByAdminEmailAndDeleteFlag(email, false);
    }

    @Override
    public boolean findAdminEmail(String email) {

        boolean result = false;

        List<ShmAdmin> getListAdminEmail;

        BaseSpecification adminNgSpec1 = new BaseSpecification
                (new SearchCriteria("adminEmail", ":", email));

        BaseSpecification adminNgSpec2 = new BaseSpecification
                (new SearchCriteria("deleteFlag", "!=", true));

        getListAdminEmail = adminRepository.findAll(Specifications.where(adminNgSpec1).and(adminNgSpec2));

        if (getListAdminEmail.size() > 0) {
            result = true;
        }
        return result;

    }

    @Override
    public ShmAdmin showAdminById(long adminId) {
        ShmAdmin searchAdmin;
        searchAdmin = adminRepository.findOne(adminId);
        return searchAdmin;
    }

    @Override
    public void updateAdminUser(ShmAdmin ad, long id) throws Exception {

        final String adminEmail = ad.getAdminEmail();
        final ShmAdmin shmAdminFindByEmail = adminRepository.findByAdminEmailIgnoreCase(adminEmail);
        ShmAdmin shmAdmin = this.showAdminById(id);
        if (shmAdminFindByEmail != null && !shmAdminFindByEmail.getAdminEmail().equalsIgnoreCase(shmAdmin.getAdminEmail())) {
            throw new Exception(messageSource.getMessage("SH_E100037", null, null));
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        shmAdmin.setAdminName(ad.getAdminName());
        shmAdmin.setAdminEmail(adminEmail);
        final String adminPwd = ad.getPwdFresh();
        shmAdmin.setAdminPwd(adminPwd);
        shmAdmin.setAdminRole(ad.getAdminRole());

        adminRepository.save(shmAdmin);
    }

    @Override
    public void updateAdminPassword(ShmAdmin ad, long id) throws Exception {
        ShmAdmin shmAdmin = this.showAdminById(id);
        final String adminPwd = ad.getPwdFresh();
        shmAdmin.setAdminPwd(adminPwd);
        adminRepository.save(shmAdmin);
    }

    @Override
    public void deleteAdminUser(long id) {

        ShmAdmin shmAdmin = this.showAdminById(id);
        shmAdmin.setDeleteFlag(true);
        adminRepository.save(shmAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public void exportCSVForTorinoCC(ShmAdmin admin, Date timeRequest, HttpServletResponse response) throws IOException {
        String[] header = {"メニュー名", "年月日", "連番", "会員ID", "会員氏名", "メニュー番号", "プランID", "明細ST", "手配結果区分", "利用日", "利用人数",
                "利用数", "見出", "数量", "提携料金", "会員料金/割引率", "出発日", "総人数",
                "会員（男）", "会員（女）", "会員（A）", "会員（B）", "会員（C）", "会員（D）", "ビジター（男）", "ビジター（女）", "ビジター（A）", "ビジター（B）", "ビジター（C）",
                "ビジター（D）", "料金内容", "基本料金", "割引", "割引金額", "人数", "処理ST", "ご質問事項1", "ご質問事項2",
                "ご質問事項3", "ご質問事項4", "ご質問事項5", "メモ入力", "住所区分", "郵便番号", "都道府県", "市区町村番地", "建物名・号室", "宛名", "会社名", "部署名",
                "電話自宅フラグ1", "TELNo", "FAX自宅フラグ1", "FAXNo", "会員向け", "パートナー向け", "特記事項"};
        String[] properties = {
                "companyName"
                , "inputDate"
                , "index"
                , "bsid"
                , "memberName"
                , "menuNumber"
                , "planId"
                , "st"
                , "sortResult"
                , "startDate"
                , "totalPerson"
                , "totalInUse"
                , "title"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "stProcess"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"
                , "blankValues"};
        LocalDate startDate = timeRequest.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = startDate.plusMonths(1);

        Map<String, List<Long>> groupBsid = userService.getUserListRegistInMonth(startDate, endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<TorinoCsvDTO> data = new ArrayList<>();
        TorinoCsvDTO torinoCsvDTO;
        int count = 1;

        for (Map.Entry<String, List<Long>> entry : groupBsid.entrySet()) {
            torinoCsvDTO = new TorinoCsvDTO();
            torinoCsvDTO.setBsid(entry.getKey());
            torinoCsvDTO.setInputDate(startDate.format(formatter));
            torinoCsvDTO.setStartDate(startDate.format(formatter));
            torinoCsvDTO.setIndex(count);
            torinoCsvDTO.setCompanyName("ワーカーズマーケット");
            torinoCsvDTO.setMemberName("");
            torinoCsvDTO.setMenuNumber(torinoMenuNumber);
            torinoCsvDTO.setPlanId(torinoPlanId);
            torinoCsvDTO.setSt(10);
            torinoCsvDTO.setStProcess(120);
            torinoCsvDTO.setSortResult(10);
            torinoCsvDTO.setTotalPerson(entry.getValue().size() + postService.countTotalPostMatchingByUserIdList(entry.getValue()) + postService.countTotalPostByUserIdList(entry.getValue()));
            torinoCsvDTO.setTotalInUse(torinoCsvDTO.getTotalPerson());
            count++;

            data.add(torinoCsvDTO);
        }
        exportCsvService.exportCSV(response, "torino.csv", header, properties, data);
    }

    @Override
    public void createAdminUser(ShmAdmin shmAdmin) {

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        ShmAdmin shmAdmin1 = new ShmAdmin();
        shmAdmin1.setAdminName(shmAdmin.getAdminName());
        shmAdmin1.setAdminEmail(shmAdmin.getAdminEmail());
        shmAdmin1.setAdminPwd(encoder.encode(shmAdmin.getAdminPwd()));
        shmAdmin1.setAdminRole(shmAdmin.getAdminRole());

        adminRepository.save(shmAdmin1);
    }

    @Override
    public ShmAdmin getAdminById(long id) {
        return adminRepository.getOne(id);
    }
}
