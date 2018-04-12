package jp.bo.bocc.helper;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import org.apache.commons.lang3.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by buixu on 12/5/2017.
 */
public class GGAUtils {
    private static final String VIEW_ID = "151976792";
    private static final String APPLICATION_NAME = "Hello Analytics Reporting";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String CLIENT_SECRET_JSON_RESOURCE = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"bo-worker-167710\",\n" +
            "  \"private_key_id\": \"612b1f01efd2376421fcd897c79c5195bc2fbdaf\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDE1RiaXoHMnnXA\\nOans1UOSRgGF9z1zyzwgg+Et5vx5AkIw8Acg5KQDrzq52hGNZYg3cKFFDnaePCqO\\nVCzsGCS59V7PFSjc6MJM0oVKofmwF2WMaBOot+0HILriqcelKutD4Zg2gxp0PIQu\\nIkGnIaUtTfewtUDp07MqeLDmXohJEmqwABI65M+e7bCp95i5DJIyBIHJfKftC2Ye\\nPaOsUICvtyHXZjgLe5+OlJPOX2cArBMSJQM93Vum/MmMgvGRkWM/UBd5aodtvn2Q\\nrvdM5bYLLhnJBxsg5j65k3oCDIQHLuZHk6KwunyubU/7GEj2b2QEJhKBj178yMx9\\nDz67cgPHAgMBAAECggEAUJVpuDF+9JXfYW56OiLpwiX1E7KwLwsCt9EmTJOnw6cA\\nEGcElBvnZjbEVTiJPRlXVlM/QzVzrL63MYQWzCXqS1PAmAlyrFaCzc7WkUKVJdm+\\nUmh9u/JpEST+PjQCL4XNwgHHPnIYT4RDHVwbAuDMbirggDEEvXZVgMreqcIEC93M\\nkMENVvz+VSCIgmHuV0noSWP8hQzMS8nBXsObL5D3Su114nKp9c9iPEQZAlYsJc6p\\nidnqbEHdFunDR1qLTK+UIagDIXWMflt9rLEDqAoZni9HJ5MtcCwo5gv167U6TFJ+\\nyMBJxu927r/PJAcb984P+uVFb70qVVh9/UW9jNXWsQKBgQD5HI7h7IrPX/UyNhVY\\nil6dFdH7EaHABcWp9LiOmk6TA0Ko5SNela85Tf5pDwi86qFbwqaEcIKzeoSH3arb\\nCcGtmu6NdopgTy1F8NV4iV2aaYNSisurMs5CfdQ0M7Xcvvo+5EC4szQ2T5+SlGWA\\nIH1r7iBB2BnCZ42vpogSyjk8WQKBgQDKRnU0Y6nWs9K0aLKCWTY6nW4rTbc6XzlS\\nBVlw+qYzXRWFK2N8a1tpfXdAOHPsMoFM5IEY8rvcAO9FjjvkbW5cxeq+Xm0uiKfx\\nsfinRnVX+hYfhQs3QvKLgIpi19Iu7wHHdpFbLOxDFguASCKxfT2HJa/dYVnO0nB7\\nodDBTea9HwKBgBmMDTcsK/wiaiHxG/VNk3BmqXcnEsB/lwrb9wVg28gF4JMzlJIB\\npcuprPqXOR0ursebSbpxU2YWnXrVh80Yzg0Aw5AsZPqFhC4VCb9nzvqj8XzIkSSI\\nfFCjWNzVpMPzrfQ/Mkf+0M71EXdV0qb2LgagkBxQjiu6tPQbjv7mUEJZAoGAMJe8\\nkFlg87M/axe7ypSj8nDfhoek/ODjZFDkq7+LLcuu388Ml1czHqSFgWgOcS+w4EgE\\nu3mUp8WRtaP1v2qKmL7zJciJciRiNC4NDiJfDkOGDQxpJV1v5xN6K0StuZixVLmn\\nMcAZAgTJvoeVzg3IOi9TkFtSt9zWKN02yTlttQECgYEA3kdfz8sTf31pw0jpXAFP\\nt8GQ13Ujc4FdLiPvwFsCKBnHRXFL6vaay3BM4OBOS1ZdgD9m3npWSM42Jwycxm2L\\nt4GHntMr1xlu77fbNKYBTYgStrz6JQv98fzxnjiKZMAI1hh3fPI6wUNV5u5YSqVV\\njLT0Joo6pM2WdELN0MUyC7g=\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"backendaccount@bo-worker-167710.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"108869039461947123147\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/backendaccount%40bo-worker-167710.iam.gserviceaccount.com\"\n" +
            "}";

    public static int countNewUser(String fromDate, String toDate){
        AnalyticsReporting service = null;
        try {
            service = initializeAnalyticsReporting();
            GetReportsResponse response = getReport(service, fromDate, toDate);
            String value = getValueResponse(response);
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(value)){
                return Integer.parseInt(value);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException, IOException {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential
                .fromStream(new ByteArrayInputStream(CLIENT_SECRET_JSON_RESOURCE.getBytes(StandardCharsets.UTF_8.name())))
                .createScoped(AnalyticsReportingScopes.all());

        // Construct the Analytics Reporting service object.
        return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    private static GetReportsResponse getReport(AnalyticsReporting service, String fromDate, String toDate) throws IOException {
        // Create the DateRange object.
        DateRange dateRange = new DateRange();
        dateRange.setStartDate(fromDate);
        dateRange.setEndDate(toDate);

        // Create the Metrics object.
        Metric sessions = new Metric()
                .setExpression("ga:newusers")
                .setAlias("ga users");

        //Create the Dimensions object.
        Dimension browser = new Dimension()
                .setName("ga:browser");

        // Create the ReportRequest object.
        ReportRequest request = new ReportRequest()
                .setViewId(VIEW_ID)
                .setDateRanges(Arrays.asList(dateRange))
                .setDimensions(Arrays.asList(browser))
                .setMetrics(Arrays.asList(sessions));

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        // Create the GetReportsRequest object.
        GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        GetReportsResponse response = service.reports().batchGet(getReport).execute();

        // Return the response.
        return response;
    }

    private static String getValueResponse(GetReportsResponse response) {

        String value = "";
        for (Report report: response.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<String> dimensionHeaders = header.getDimensions();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();

            if (rows == null) {
                System.out.println("No data found for " + VIEW_ID);
                return "0";
            }

            for (ReportRow row: rows) {
                List<String> dimensions = row.getDimensions();
                List<DateRangeValues> metrics = row.getMetrics();
                for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
                    System.out.println(dimensionHeaders.get(i) + ": " + dimensions.get(i));
                }
                for (int j = 0; j < metrics.size(); j++) {
                    System.out.print("Date Range (" + j + "): ");
                    DateRangeValues values = metrics.get(j);
                    for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
                         value = values.getValues().get(k);
                         break; // get first value
                    }
                }
                break; // get first row to value
            }
        }
        return  value;
    }
}
