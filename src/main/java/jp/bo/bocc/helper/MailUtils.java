package jp.bo.bocc.helper;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Namlong on 5/23/2017.
 */
public class MailUtils {
    private static final Pattern TITLE_REGEX = Pattern.compile("<title>(.*)</title>");
    private static final Pattern BODY_REGEX = Pattern.compile("<body>(.*)</body>", Pattern.DOTALL);

    /**
     * get email title.
     *
     * @param template
     * @return
     */
    public static String getEmailTitle(String template) {
        Matcher titleMatcher = TITLE_REGEX.matcher(template);
        String result = null;
        if (titleMatcher.find())
         result = titleMatcher.group(1);
        return result;
    }

    /**
     * get email content.
     *
     * @param template
     * @return
     */
    public static String getEmailContent(String template) {
        Matcher bodyMatcher = BODY_REGEX.matcher(template);
        String result = null;
        if (bodyMatcher.find())
         result = bodyMatcher.group(1);
        return result;
    }

    /**
     * get email template for first contact.
     *
     * @return
     * @throws IOException
     */
    public static String getTemplateMailFirstContact(String property) throws IOException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream(property);
            String template = IOUtils.toString(input, "UTF8");
            return template;
        } catch (IOException e) {
            throw e;
        }

    }

}
