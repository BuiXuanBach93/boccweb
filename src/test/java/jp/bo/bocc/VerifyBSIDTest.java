package jp.bo.bocc;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author manhnt
 */
public class VerifyBSIDTest {
	private static final String MINFO_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
			"<MINFO>\n" +
			" <UID>%1$s</UID>\n" +
			" <PWD>%2$s</PWD>\n" +
			" <MNO></MNO>\n" +
			" <SCD></SCD>\n" +
			" <SHA></SHA>\n" +
			"</MINFO>";
	private static String bsidOrEmail = "y.nemoto@benefit-one.co.jp";
	private static String bsPassword = "wk123456";
	private static String chkNumber = "88111920";

	//  Real API
	//  bs.verify_url=https://bs.benefit-one.co.jp/bs_api/pages/auth/memberAuth.faces
	//  Test API
	//	bs.verification.url=https://test.bene-st.jp/bs_api/pages/auth/memberAuth.faces
	//	bs.verification.chk=88111920
	public static void main(String[] args) throws UnirestException {
		HttpResponse<String> response = Unirest.get("https://test.bene-st.jp/bs_api/pages/auth/memberAuth.faces")
				.queryString("chk", chkNumber)
				.queryString("minfo", String.format(MINFO_TEMPLATE, bsidOrEmail, bsPassword))
				.asString();

		System.out.println(response.getBody());
	}
}
