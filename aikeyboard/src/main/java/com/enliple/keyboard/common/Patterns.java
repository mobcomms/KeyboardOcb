package com.enliple.keyboard.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.tribe7.common.net.InternetDomainName;

public class Patterns
{

	/* Added by 김현준 (2015-03-02) : 한글과 영문, 숫자 유니코드값 추가 (Reference: http://www.ssec.wisc.edu/~tomw/java/unicode.html) */
	private static final char	INITIAL_SOUND_BEGIN_UNICODE	= 12593;						// 초성 유니코드 시작 값
	private static final char	INITIAL_SOUND_LAST_UNICODE	= 12622;						// 초성 유니코드 마지막 값
	private static final char	HANGUL_BEGIN_UNICODE		= 44032;						// 한글 유니코드 시작 값
	private static final char	HANGUL_LAST_UNICODE			= 55203;						// 한글 유니코드 마지막 값
	private static final char	NUMBER_BEGIN_UNICODE		= 48;							// 숫자 유니코드 시작 값
	private static final char	NUMBER_LAST_UNICODE			= 57;							// 숫자 유니코드 마지막 값
	private static final char	ENGLISH_ROWER_BEGIN_UNICODE	= 65;							// 영문(소문자) 유니코드 시작 값
	private static final char	ENGLISH_ROWER_LAST_UNICODE	= 90;							// 영문(소문자) 유니코드 마지막 값
	private static final char	ENGLISH_UPPER_BEGIN_UNICODE	= 97;							// 영문(대문자) 유니코드 시작 값
	private static final char	ENGLISH_UPPER_LAST_UNICODE	= 122;							// 영문(대문자) 유니코드 마지막 값
	private static final char	HANGUL_BASE_UNIT			= 588;							// 자음 마다 가지는 글자수
	
	public static final String KEYWORD_MATCHES	= "^[0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]*$";

	public static boolean findHTMLTag(String string)
	{
		Pattern tag = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		Matcher mat = tag.matcher(string);
		return mat.find();
	}

	public static String removeAllHTMLTag(String string)
	{
		return string.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
	
	/**
	 * Added by 김현준 (2015-03-02)
	 * 인코딩 깨져서 정확한 문자가 리턴되지 못하는 경우를 체크!
	 * 숫자, 한글, 한글 초성, 영문(대소문자)를 제외한 다른 값은 false 리턴!
	 * 한글의 경우 모음만 존재할 경우 이 체크를 통과하지 못한다.
	 * @param pStr
	 * @return
	 */
	public static boolean checkNumEngHangulUnicode(String pStr)
	{
		if (pStr == null || pStr.length() <= 0 || pStr.trim().length() <= 0) {
			return false;
		}
		
		boolean isCheckedComplete = false;
		
		char c;
		int size = pStr.length();
		for (int i=0; i<size; i++)
		{
			c = pStr.charAt(i);
			if (((c >= NUMBER_BEGIN_UNICODE && c <= NUMBER_LAST_UNICODE) 
				|| (c >= ENGLISH_UPPER_BEGIN_UNICODE && c <= ENGLISH_UPPER_LAST_UNICODE) 
				|| (c >= ENGLISH_ROWER_BEGIN_UNICODE && c <= ENGLISH_ROWER_LAST_UNICODE) 
				|| (c >= HANGUL_BEGIN_UNICODE && c <= HANGUL_LAST_UNICODE) 
				|| (c >= INITIAL_SOUND_BEGIN_UNICODE && c <= INITIAL_SOUND_LAST_UNICODE)))
			{
				isCheckedComplete = true;
			}
			else
			{
				isCheckedComplete = false;
				break;
			}
		}
		
		return isCheckedComplete;
	}
	
	/**
	 * Added by 김현준 (2015-03-02)
	 * 숫자, 영문(대소문자), 한글, 초성 등의 유니코드를 체크
	 * 숫자, 한글, 한글 초성, 영문(대소문자)를 제외한 다른 값은 false 리턴!
	 * 한글의 경우 모음만 존재할 경우 이 체크를 통과하지 못한다.
	 * @param c
	 * @return
	 */
	public static boolean checkNumEngHangulUnicode(char c)
	{
		if (((c >= NUMBER_BEGIN_UNICODE && c <= NUMBER_LAST_UNICODE) 
			|| (c >= ENGLISH_UPPER_BEGIN_UNICODE && c <= ENGLISH_UPPER_LAST_UNICODE) 
			|| (c >= ENGLISH_ROWER_BEGIN_UNICODE && c <= ENGLISH_ROWER_LAST_UNICODE) 
			|| (c >= HANGUL_BEGIN_UNICODE && c <= HANGUL_LAST_UNICODE) 
			|| (c >= INITIAL_SOUND_BEGIN_UNICODE && c <= INITIAL_SOUND_LAST_UNICODE)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
