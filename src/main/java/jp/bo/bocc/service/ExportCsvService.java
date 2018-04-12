package jp.bo.bocc.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author NguyenThuong on 6/5/2017.
 */
public interface ExportCsvService<T> {
    void exportCSV(HttpServletResponse response, String fileName, String[] header, String[] properties, List<T> values) throws IOException;
}
