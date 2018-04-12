package jp.bo.bocc.helper;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author manhnt
 */
public class StringUtils {

	public static final String DIGITS_AND_COMMA_REGEX = "^[0-9,\\s+]+$";
	public static final String LETTER_AND_COMMA_REGEX = "[a-zA-Z,\\s+]+";
	public static final String LETTER_AND_JAPAN_AND_COMMA_REGEX = "[a-zA-Z,　、ぁ-ゔゞァ-・ヽヾ゛゜ー一-龯\\s+]+";
    public static final String LETTER_DIGIT_AND_JAPAN_AND_COMMA_REGEX = "[a-z0-9A-Z,　、ぁ-ゔゞァ-・ヽヾ゛゜ー一-龯\\s+]+";
	public static final String DIGITS_REGEX = "^[0-9]+$";
	public static final String EMAIL_REGEX = "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b";

	public static String getMD5(String data) throws NoSuchAlgorithmException {
		return getDigest(data, "MD5");
	}

	public static String getSHA2(String data) throws NoSuchAlgorithmException {
		return getDigest(data, "SHA-256");
	}

	public static boolean isOnlyContainLetterAndJapanAndComma(String input) {
		return input.matches(LETTER_AND_JAPAN_AND_COMMA_REGEX);
	}

	public static boolean isOnlyContainLetterAndComma(String input) {
		return input.matches(LETTER_AND_COMMA_REGEX);
	}

	public static boolean isOnlyDigits(String input) {
		return input.matches(DIGITS_REGEX);
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static List<String> convertStringToList(String input, String regex) {
		String[] array = input.split(regex);
		List<String> list = new ArrayList<>();
		for (String item : array) {
			item = item.trim().replace("　", "");
			if (item.isEmpty())
				continue;
			list.add(item);
		}

		return list;
	}

	public static boolean isOnlyContainDigitsAndComma(String input) {
		return input.matches(DIGITS_AND_COMMA_REGEX);
	}

	private static String getDigest(String data, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

		messageDigest.update(data.getBytes());
		byte[] digest = messageDigest.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(Integer.toHexString(b & 0xff));
		}
		return sb.toString();
	}

	public static String generateUniqueToken() throws NoSuchAlgorithmException {
		SecureRandom rand = new SecureRandom();
		return getSHA2(rand.nextInt() + String.valueOf(System.currentTimeMillis()));
	}

	public static String base64Encode(byte[] input) {
		return Base64.getEncoder().encodeToString(input);
	}

	public static byte[] base64Decode(String base64String) {
		return Base64.getDecoder().decode(base64String);
	}

	public static String randomNumberSeq(int length) {
		return RandomStringUtils.random(length, "0123456789");
	}

	/**
	 * return hash tag list.
	 * @param str
	 * @return
	 */
	public static List<String> getHashTagValue(String str){
		List<String> result =new ArrayList<String>();
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(str)) {
			String pattern = "[#](\\w+\\s*)";
			Pattern p = Pattern.compile(pattern);
			Matcher matcher = p.matcher(str);
			while(matcher.find())
				result.add(matcher.group().trim());
		}
		return result;
	}

	/**
	 * return string list splitted by hash tag.
	 * @param str
	 * @return
	 */
	public static List<String> getNoHashTagValue(String str){
		List<String> result =new ArrayList<String>();
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(str)) {
			String pattern = "[#](\\w+\\s*)";
			Arrays.stream(str.split(pattern)).forEach(e ->
				result.add(e.trim())
			);
		}
		return result;
	}

	public static String randomPassword(int passwordLength) {
		return RandomStringUtils.randomAlphanumeric(passwordLength);
	}

	public static boolean isNumber(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c < '0' || c > '9') return false;
		}
		return true;
	}

    public static boolean isOnlyContainLetterAndJapanAndCommaAndDigit(String input) {
        return input.matches(LETTER_DIGIT_AND_JAPAN_AND_COMMA_REGEX);
    }
}
