package jp.bo.bocc.service.impl;

import jp.bo.bocc.service.ExportCsvService;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author NguyenThuong on 6/5/2017.
 */
@Service
public class ExportCsvServiceImpl implements ExportCsvService {

    /**
     * export CSV file
     */
    @Override
    public void exportCSV(HttpServletResponse response, String fileName, String[] header, String[] properties, List values) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv");
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.setHeader("Content-Disposition", headerValue);

        Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
        writer.write('\uFEFF');
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        csvBeanWriter.writeHeader(header);

        for (Object ob : values) {
            csvBeanWriter.write(ob, properties);
        }
        csvBeanWriter.close();
    }
}
