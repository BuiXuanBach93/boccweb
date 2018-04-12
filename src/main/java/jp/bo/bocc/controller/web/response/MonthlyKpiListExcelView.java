package jp.bo.bocc.controller.web.response;

import jp.bo.bocc.entity.ShtKpiStorage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by buixu on 12/5/2017.
 */
public class MonthlyKpiListExcelView extends AbstractXlsxView {

    public void setExcelHeader(Sheet excelSheet) {
        Row excelHeader = excelSheet.createRow(0);
        excelHeader.createCell(0).setCellValue("月付");
        excelHeader.createCell(1).setCellValue("DL数");
        excelHeader.createCell(2).setCellValue("会員数");
        excelHeader.createCell(3).setCellValue("会員登録率");
        excelHeader.createCell(4).setCellValue("利用者数");
        excelHeader.createCell(5).setCellValue("投稿者数");
        excelHeader.createCell(6).setCellValue("投稿数");
        excelHeader.createCell(7).setCellValue("平均投稿回数");
        excelHeader.createCell(8).setCellValue("購入者数");
        excelHeader.createCell(9).setCellValue("購入回数");
        excelHeader.createCell(10).setCellValue("平均購入回数");
    }

    public void setExcelRows(Sheet excelSheet, List<ShtKpiStorage> kpiMonthlys){
        int record = 1;
        for (ShtKpiStorage kpiMonthlyResponse : kpiMonthlys) {
            Row excelRow = excelSheet.createRow(record++);
            excelRow.createCell(0).setCellValue(kpiMonthlyResponse.getQueryTime());
            excelRow.createCell(1).setCellValue(kpiMonthlyResponse.getDlNumber());
            excelRow.createCell(2).setCellValue(kpiMonthlyResponse.getRegNumber());
            excelRow.createCell(3).setCellValue(kpiMonthlyResponse.getRegRatio());
            excelRow.createCell(4).setCellValue(kpiMonthlyResponse.getActorNumber());
            excelRow.createCell(5).setCellValue(kpiMonthlyResponse.getOwnerNumber());
            excelRow.createCell(6).setCellValue(kpiMonthlyResponse.getPostNumber());
            excelRow.createCell(7).setCellValue(kpiMonthlyResponse.getPostRatio());
            excelRow.createCell(8).setCellValue(kpiMonthlyResponse.getPartnerNumber());
            excelRow.createCell(9).setCellValue(kpiMonthlyResponse.getTransNumber());
            excelRow.createCell(10).setCellValue(kpiMonthlyResponse.getTransRatio());
        }
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> map, Workbook workbook, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Sheet excelSheet = workbook.createSheet();
        setExcelHeader(excelSheet);

        List kpiMonthlys = (List) map.get("kpiMonthlys");
        setExcelRows(excelSheet,kpiMonthlys);
    }
}
