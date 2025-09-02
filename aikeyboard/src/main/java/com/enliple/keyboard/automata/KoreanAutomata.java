/*
 * halbae87: this project is created from Soft Keyboard Sample source
 * 	but this part is my original source.
 */

package com.enliple.keyboard.automata;

import com.enliple.keyboard.activity.InputTables;
import com.enliple.keyboard.common.KeyboardLogPrint;

public class KoreanAutomata extends Automata {

	/**
	 유니코드값에서 초,중,종 값을 구하는 방법:
     21 : 중성 갯수
	 28 : 종성갯수(27) + 1

	 원리:
	 유니코드 = 0xAC00 + x*21*28 + y*28  + z
	 = 0xAC00 + (x*21 + y) * 28 + z
	 (x는 초성)
	 (y는 중성, 0 <= y <= 20)
	 (z는 종성, 0 <= z <= 27)
	 계산방법:
	 u = 유니코드 - 0xAC00
	 z = u % 28
	 y = (u / 28) % 21
	 x = u / 28 / 21


	 예) 럼 (완성형 코드 0xB7FC)의 초/중/종성을 구하라.

	 u = 유니코드 - 0xAC00   = 3068
	 z = u % 28              = 16 ... ㅁ
	 y = (u / 28) % 21       = 4 ... ㅓ
	 x = u / 28 / 21         = 5 ... ㄹ
	 */


	/** 한글 유니코드 영역 : 0xAC00 <= 한글유니코드 <= 0xD7A3 */
	/** 한글 유니코드 시작. '가' : 0xAC00*/
	public static int HANGUL_START = 0xAC00;
	/** 한글 유니코드 끝. '핳' : 0xD7A3*/
	public static int HANGUL_END = 0xD7A3;
	/** 한글 자모음 시작*/
	public static int HANGUL_JAMO_START = 0x3131;
	/** 한글 중성 모음 시작*/
	public static int HANGUL_MO_START = 0x314F;
	/** 한글 중성 모음 끝 */
	public static int HANGUL_JAMO_END = 0x3163;
	private static final String TAG = "KoreanAutomata";

	// Action Codes
	public static final int ACTION_NONE = 0;
	public static final int ACTION_UPDATE_COMPOSITIONSTR = 1; /** 문자열 update */
	public static final int ACTION_UPDATE_COMPLETESTR = 2;
	public static final int ACTION_USE_INPUT_AS_RESULT = 4; /** 입력값이 영문이거나 한글 키패드인 상태에서  값이 하나(ㄱ , ㅏ, 빈값)만 입력된 상태에서 back key 눌렀을 경우 setting 됨 */
	public static final int ACTION_APPEND = 8;
	// public static final int ACTION_BACKSPACE = 8; not used.
	public static final int ACTION_ERROR = -1; /** 에러 발생 시 setting 됨 */

	/**
	 * mState 정의
	 * 0 : 현재 구성된 문자열 null 일 경우
	 * 1 : 현재 구성된 문자열이 단자음 일 경우 ( ex : ㅂ )
	 * 2 : 현재 구성된 문자열이 단자음 + 단모음 일 경우 ( ex : 바 )
	 * 3 : 현재 구성된 문자열이 단자음 + 단모음 + 단자음 일 경우 ( ex : 밥 )
	 * 4 : 현재 구성된 문자열이 단모음 일 경우 ( ex : ㅏ )
	 * 5 : 현재 구성된 문자열이 조합모음(조합모음 : ㅟ ㅢ etc.. ㅔ ㅐ등 조합하지 않고 쓰는 경우는 단모음으로 본다) 일 경우  ( ex : ㅟ )
	 * 10 : 현재 구성된 문자열이 조합자음(조합자음 : ㅄ ㄳ etc.. ㅆ ㅃ등 조합하지 않고 쓰는 경우는 단자음으로 본다) 일 경우  ( ex : ㅄ )
	 * 11 : 현재 구성된 문자열이 단자음 + 단모음 + 조합자음 일 경우 ( ex : 값 )
	 * 20 : 현재 구성된 문자열이 단자음 + 조합모음 일 경우 ( ex : 뷔 )
	 * 21 : 현재 구성된 문자열이 단자음 + 조합모음 + 단자음 일 경우 ( ex : 뷥, 봤 )
	 * 22 : 현재 구성된 문자열이 단자음 + 조합모음 + 조합자음 일 경우 ( ex : 쇣 )
	 */
	private int mState = 0;

	/** edit 영역의 text 값 */
	private String mCompositionString = "";
	private String mCompleteString = "";
	private boolean mKoreanMode = false;

	public KoreanAutomata()
	{
		mState = 0;
		mCompositionString = "";
		mCompleteString = "";
		mKoreanMode = true; // 2017.02.27 기본 키보드 한글 변경
//		mKoreanMode = false;
	}
	@Override
	public int GetState()
	{
		return mState;
	};
	@Override
	public String GetCompositionString()
	{
		return mCompositionString;
	};
	@Override
	public String GetCompleteString()
	{
		return mCompleteString;
	};

	/** 한글모드를 현재의 반대모드로 변경한다 */
	@Override
	public void ToggleMode()
	{
		mKoreanMode = !mKoreanMode;
	}
	/** 한글모드인지 여부를 check */
	@Override
	public boolean IsKoreanMode()
	{
		return mKoreanMode;
	}

	/**
	 * 입력된 code 값이 한글인지 판단
	 * @param code 입력된 code 값
	 * @return 입력된 code 값이 한글이면 true 아니면 false
     */
	public boolean IsHangul(char code)
	{
		if ((code >= HANGUL_START) && (code <= HANGUL_END))
			return true;
		if ((code >= HANGUL_JAMO_START) && (code <= HANGUL_JAMO_END))
			return true;
		return false;
	}

	public boolean IsJAMO(char code)
	{
		if ((code >= HANGUL_JAMO_START) && (code <= HANGUL_JAMO_END))
			return true;
		return false;
	};

	/**
	 * 입력된 code 값이 초성 자음인지 check
	 * @param code 입력된 code 값
	 * @return 자음이 맞을 경우 true, 자음이 아닐경우 false
     */
	public boolean IsConsonant(char code)
	{
		if ((code >= HANGUL_JAMO_START) && (code < HANGUL_MO_START))
			return true;
		return false;
	};

	/**
	 *
	 * @param code 입력된 code 값
	 * @return 모음이 맞을 경우 true, 모음이 아닐 경우 false
     */
	public boolean IsVowel(char code)
	{
		if ((code >= HANGUL_MO_START) && (code <= HANGUL_JAMO_END))
			return true;
		return false;
	};

	/** not used.
	public boolean IsLastConsonanted(char code)
	{
		if (IsHangul(code))
		{
			if (IsJAMO(code)) // <- need to fix, if this routine is to be used...
				return true;
			int offset = code - HANGUL_START;
			if (offset % InputTables.NUM_OF_LAST_INDEX == 0)
				return false;
			else
				return true;
		}
		else
		{
			// wrong input
			return false;
		}
	}
	**/

	/**
	 * 입력문자의 종성자음 값을 가져옴
	 * @param code 입력 문자
	 * @return 압력문자의 종성자음 ex) code : '갑'이면 return 값은 ㅂ
     */
	public int GetLastConsonantIndex(char code)
	{
		KeyboardLogPrint.e("GetLastConsonantIndex");
		int lcIndex= -1;
		if (IsHangul(code))
		{
			if (IsJAMO(code))
			{
				if (IsConsonant(code))
				{
					for (lcIndex = 0; lcIndex < InputTables.NUM_OF_LAST_INDEX; lcIndex++)
					{
						if (code == InputTables.LastConsonants.Code[lcIndex])
						{
							KeyboardLogPrint.e("GetLastConsonantIndex consonant :: " + lcIndex);
							break;
						}
						else
						{
							KeyboardLogPrint.e("GetLastConsonantIndex consonant else :: " + lcIndex);
						}
					}
					if (lcIndex >= InputTables.NUM_OF_LAST_INDEX)
						lcIndex = -1;
				}
				else
					lcIndex = -1;
			}
			else
			{
				/**
				 입력된 CODE가 '넋' 일 경우 넋의 종성자음 'ㄳ'의 INDEX값 (3)을 가져오는 수식
				 초성 = (((글자 - 0xAC00) - (글자 - 0xAC00) % 28 ) ) / 28 ) / 21
				 중성 = (((글자 - 0xAC00) - (글자 - 0xAC00) % 28 ) ) / 28 ) % 21
				 종성 =  (글자 - 0xAC00) % 28
				 */
				int offset = code - HANGUL_START;
				KeyboardLogPrint.e("offset :: " + offset);
				lcIndex = (offset % InputTables.NUM_OF_LAST_INDEX);
				KeyboardLogPrint.e("GetLastConsonantIndex not jamo :: " + lcIndex);
			}
		}
		return lcIndex;
	}

//	public char GetLastConsonant(char code)
//	{
//		char lcCode;
//		int lcIndex = GetLastConsonantIndex(code);
//		if (lcIndex < 0)
//			lcCode = (char) 0;
//		else
//			lcCode = InputTables.LastConsonants.Code[lcIndex];
//		return lcCode;
//	}

	public int GetFirstConsonantIndex(char code)
	{
		int fcIndex = -1;
		if (IsHangul(code))
		{
			if (IsConsonant(code))
			{
				for (fcIndex = 0; fcIndex < InputTables.NUM_OF_FIRST; fcIndex++)
					if (code == InputTables.FirstConsonantCodes[fcIndex])
						break;
				if (fcIndex >= InputTables.NUM_OF_FIRST)
					fcIndex = -1;
			}
			else if (IsVowel(code))
			{
				fcIndex = -1;
			}
			else
			{
				/**
				 입력된 code의 초성자음의 index 가져오는 수식 ( ex : 입력된 code 값이 '위' 이면 '위'의 초성자음인 'ㅇ'의 index 값인 11
				 */
				int offset = code - HANGUL_START;
				fcIndex = (offset / (InputTables.NUM_OF_MIDDLE * InputTables.NUM_OF_LAST_INDEX));
			}
		}

		return fcIndex;
	}

	public char GetFirstConsonant(char code)
	{
		char fcCode;
		int fcIndex = GetFirstConsonantIndex(code);
		if (fcIndex < 0)
			fcCode = (char) 0;
		else
			fcCode = InputTables.FirstConsonantCodes[fcIndex];
		return fcCode;
	}

	/**
	 * 해당 code 값이 vowel array의 몇번째 index 값인지 알아내는 함수
	 * @param code : 입력된 vowel code
	 * @return 입력된 code값에 해당하는 voewl array의 index값
     */
	public int GetVowelIndex(char code)
	{
		int vIndex = -1;
		if (IsHangul(code))
		{
			if (IsVowel(code)) // vowel only character..
			{
				KeyboardLogPrint.e("GetVowelIndex vowel");
				vIndex = ConvertVowelCodeToIndex(code);
			}
			else
			{
				KeyboardLogPrint.e("GetVowelIndex not vowel");
				int offset = code - HANGUL_START;
				vIndex = (offset % (InputTables.NUM_OF_MIDDLE * InputTables.NUM_OF_LAST_INDEX))/ InputTables.NUM_OF_LAST_INDEX;
			}
		}
		return vIndex;
	}

	/**
	 * 입력된 code값에 대한 모음을 가져옴
	 * @param code 입력된 code 값
	 * @return 입력된 code 값에 해당하는 모음(char)
     */
	public char GetVowel(char code)
	{
		char vCode;
		int vIndex = GetVowelIndex(code);
		if (vIndex < 0)
			vCode = (char) 0;
		else
			vCode = InputTables.Vowels.Code[vIndex];
		return vCode;
	}

	/**
	 * 입력된 code 값이 초성 배열의 몇번재 값인지를 얻음
	 * @param fcCode 초성 자음 code
	 * @return 입력된 초성자음의 index 값
	 */
	public int ConvertFirstConsonantCodeToIndex(char fcCode) // fcCode should be one of "First Consonants" otherwise return -1
	{
		int fcIndex = 0;
		while  (fcIndex < InputTables.NUM_OF_FIRST)
		{
			if (fcCode == InputTables.FirstConsonantCodes[fcIndex])
				break;
			fcIndex++;
		}
		if (fcIndex ==  InputTables.NUM_OF_FIRST)
			fcIndex = -1;
		return fcIndex;
	}

	/**
	 * 입력된 code 값이 종성 배열의 몇번재 값인지를 얻음
	 * @param lcCode 종성 자음 code
	 * @return 입력된 종성자음의 index 값
     */
	public int ConvertLastConsonantCodeToIndex(char lcCode) // fcCode should be one of "Last Consonants", otherwise return -1
	{
		int lcIndex = 0;
		while  (lcIndex < InputTables.NUM_OF_LAST_INDEX)
		{
			if (lcCode == InputTables.LastConsonants.Code[lcIndex])
				break;
			lcIndex++;
		}
		if (lcIndex == InputTables.NUM_OF_LAST_INDEX)
			lcIndex = -1;
		return lcIndex;
	}

	/**
	 * vowel code값을 index로 변환
	 * @param vCode 입력된 voewl code
	 * @return 입력된 vowel code 값에 해당하는 index
     */
	public int ConvertVowelCodeToIndex(char vCode)
	{
		if (vCode < InputTables.Vowels.Code[0])
			return -1;
		int vIndex = vCode - InputTables.Vowels.Code[0];
		if (vIndex >= InputTables.NUM_OF_MIDDLE)
			return -1;
		return vIndex;
	}

	/**
	 * 입력된 두 자음을 가지고 조합된 unicode 값을 가져옴
	 * @param cIndex1 입력된 자음 index
	 * @param cIndex2 입력된 자음 index
     * @return 입력된 두 자음으로 조합된 조합자음의 unicode 값을 return
     */
	public int CombineLastConsonantWithIndex(int cIndex1, int cIndex2)
	{
		int newIndex = 0;
		char newCode = (char) 0;

		if (InputTables.LastConsonants.Code[cIndex1] == 0x3131 && InputTables.LastConsonants.Code[cIndex2] == 0x3145) // 두 입력값이 ㄱ, ㅅ
			newCode = 0x3133; // ㄳ

		if (InputTables.LastConsonants.Code[cIndex1] == 0x3142 && InputTables.LastConsonants.Code[cIndex2] == 0x3145) // 두 입력값이 ㅂ, ㅅ
			newCode = 0x3144; // ㅄ

		if (InputTables.LastConsonants.Code[cIndex1] == 0x3134) // ㄴ
		{
			if (InputTables.LastConsonants.Code[cIndex2] == 0x3148) // ㅈ
				newCode = 0x3135; // ㄵ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314E) // ㅎ
				newCode = 0x3136; // ㄶ
		}

		if (InputTables.LastConsonants.Code[cIndex1] == 0x3139) // ㄹ
		{
			if (InputTables.LastConsonants.Code[cIndex2] == 0x3131) // ㄱ
				newCode = 0x313A; // ㄺ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x3141) // ㅁ
				newCode = 0x313B; // ㄻ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x3142) // ㅂ
				newCode = 0x313C; // ㄼ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x3145) // ㅅ
				newCode = 0x313D; // ㄽ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314C) // ㅌ
				newCode = 0x313E; // ㄾ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314D) // ㅍ
				newCode = 0x313F; // ㄿ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314E) // ㅎ
				newCode = 0x3140; // ㅀ
		}

		if (newCode == (char) 0)
			newIndex = -1;
		else
			newIndex = ConvertLastConsonantCodeToIndex(newCode);

		return newIndex;
	}

	/**
	 *  입력된 두 자음을 조합한 조합자음값을 출력
	 * @param lcCode1 입력된 자음 문자
	 * @param lcCode2 입력된 자음 문자
     * @return 두 입력 자음이 조합된 문자
     */
	public char CombineLastConsonantWithCode(char lcCode1, char lcCode2)
	{
		char newCode = (char) 0;

		if (lcCode1 == 0x3131 && lcCode2 == 0x3145) // 두 입력값이 ㄱ, ㅅ
			newCode = 0x3133; // ㄳ

		else if (lcCode1 == 0x3142 && lcCode2 == 0x3145) // 두 입력값이 ㅂ, ㅅ
			newCode = 0x3144; // ㅄ

		else if (lcCode1 == 0x3134) // ㄴ
		{
			if (lcCode2 == 0x3148) // ㅈ
				newCode = 0x3135; // ㄵ
			else if (lcCode2 == 0x314E) // ㅎ
				newCode = 0x3136; // ㄶ
		}

		else if (lcCode1 == 0x3139) // ㄹ
		{
			if (lcCode2 == 0x3131) //ㄱ
				newCode = 0x313A; // ㄺ
			else if (lcCode2 == 0x3141) //ㅁ
				newCode = 0x313B; // ㄻ
			else if (lcCode2 == 0x3142) // ㅂ
				newCode = 0x313C; // ㄼ
			else if (lcCode2 == 0x3145) //ㅅ
				newCode = 0x313D; // ㄽ
			else if (lcCode2 == 0x314C) // ㅌ
				newCode = 0x313E; // ㄾ
			else if (lcCode2 == 0x314D) // ㅍ
				newCode = 0x313F; // ㄿ
			else if (lcCode2 == 0x314E) // ㅎ
				newCode = 0x3140; // ㅀ
		}

		return newCode;
	}

	/**
	 * 입력된  두 문자를 조합하여 조합모음을 만듬
	 * @param vCode1 입력된 첫번째 문자
	 * @param vCode2  입력된 두번째 문자
     * @return 입력된 두 문자가 조합된 조합모음
     */
	public char CombineVowelWithCode(char vCode1, char vCode2)
	{
		char newCode = (char) 0;
		if (vCode1 == 0x3157) // ㅗ
		{
			if (vCode2 == 0x314F) // ㅏ
				newCode = 0x3158; // ㅘ
			else if (vCode2 == 0x3150) // ㅐ
				newCode = 0x3159; // ㅙ
			else if (vCode2 == 0x3163) // ㅣ
				newCode = 0x315A; // ㅚ
		}
		else if (vCode1 == 0x315C) // ㅜ
		{
			if (vCode2 == 0x3153) // ㅓ
				newCode = 0x315D; // ㅝ
			else if (vCode2 == 0x3154) // ㅔ
				newCode = 0x315E;  // ㅞ
			else if (vCode2 == 0x3163) // ㅣ
				newCode = 0x315F; // ㅟ
		}
		else if (vCode1 == 0x3161) // ㅡ
		{
			if (vCode2 == 0x3163) // ㅣ
				newCode = 0x3162; // ㅢ
		}
		return newCode;
	}

	public int CombineVowelWithIndex(int vIndex1, int vIndex2)
	{
		int newIndex = -1;
		char vCode1 = InputTables.Vowels.Code[vIndex1];
		char vCode2 = InputTables.Vowels.Code[vIndex2];

		char newCode = CombineVowelWithCode(vCode1, vCode2);
		if (newCode != (char) 0)
		{
			newIndex = ConvertVowelCodeToIndex(newCode);
		}
		return newIndex;
	}

	/**\
	 * 입력된 초성, 중성, 종성의 index값을 가지고 한글 문자 조합
	 * @param fcIndex 초성 index
	 * @param vIndex 중성 index
	 * @param lcIndex 종성 index
     * @return
     */
	public char ComposeCharWithIndexs(int fcIndex, int vIndex, int lcIndex)
	{
		char Code = (char) 0;
		if ((fcIndex >= 0) && (fcIndex < InputTables.NUM_OF_FIRST)) // 입력된 fcIndex 값이 초성 범위 내에 있을 경우
		{
			if ((vIndex >= 0) && (vIndex < InputTables.NUM_OF_MIDDLE)) // 입력된 vIndex가 중성 범위내에 있을 경우
			{
				if ((lcIndex >= 0) && (lcIndex < InputTables.NUM_OF_LAST)) // 입력된 lcIndex가 종성 범위내에 있을 경우
				{
					// 입력된 초성 인덱스(fcIndex), 중성 인덱스(vIndex), 종성 인덱스 (lcIndex)를 조합하여 code값 만듬
					int offset = fcIndex* InputTables.NUM_OF_MIDDLE* InputTables.NUM_OF_LAST_INDEX + vIndex* InputTables.NUM_OF_LAST_INDEX + lcIndex;
					Code = (char) (offset + HANGUL_START);
				}
			}
		}
		return Code;
	}

	/**
	 * 입력된 코드의 alphabet index
	 * @param code 입력코드
	 * @return 입력 코드에 해당하는 알파벳 인덱스
     */
	public int GetAlphabetIndex(char code)
	{
		if (code >= 'a' && code <= 'z')
			return (int) (code - 'a');
		if (code >= 'A' && code <= 'Z')
			return (int) (code - 'A');
		return -1;
	}

	/* not used...
	public boolean IsToggleKey(char code, int KeyState)
	{
		boolean bRet = false;
		if ((code == ' ') && ((KeyState & InputTables.KEYSTATE_SHIFT_MASK) != 0)) // SHIFT-SPACE
			bRet = true;
		return bRet;
	}
	*/

	// korean mode 에서 back key 눌렀을 경우
	@Override
	public int DoBackSpace()
	{
		int ret = ACTION_NONE;
		char code;
		// 1. 현재 문자열의 값을 code에 넣음
		if (mCompositionString != "")
			code =  mCompositionString.charAt(0);
		else
			code = (char) 0; // 문자열이 빈값일 경우 이 값을 set
		KeyboardLogPrint.e("code :: " + code);
		if (mState != 0 && code == (char) 0) // mState != 0, 즉 문자열 null인데 빈값일 경우의 code 값이 넘어올 경우 error
		{
			return ACTION_ERROR;
		}
		KeyboardLogPrint.e("mState :: " + mState);
		switch (mState)
		{
			case  0: // 현재 구성된 문자열 null 일 경우
				ret = ACTION_USE_INPUT_AS_RESULT; // 한글 키패드에서 값이 없을 경우 setting
				break;
			case  1:  // 현재 구성된 문자열이 단자음 일 경우 ( ex : ㅂ )
			case  4:  // 현재 구성된 문자열이 단모음 일 경우 ( ex : ㅏ )
				mCompositionString = ""; //  문자열 초기화 한 후
				KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
				mState = 0; //0, 1, 4 상태에서 back key를 누르면 아무 값도 없는 상태가 되므로 0상태로 바꿔 줌. ㅂ -> back key  ->  빈값 .
				ret = ACTION_USE_INPUT_AS_RESULT; // 한글 키패드에서 값이 없을 경우 setting
				break;

			case  2: // 현재 구성된 문자열이 단자음 + 단모음 일 경우 ( ex : 바 )
				{
					int fcIndex = GetFirstConsonantIndex(code); // '바'의 초성 자음 ㅂ의 index를 가져와서
					code = InputTables.FirstConsonantCodes[fcIndex]; // 'ㅂ'의 code를 가져와
					mCompositionString = ""; // 문자열을 초기화 한 후
					mCompositionString += code; // 'ㅂ'의 code를 문자열에 add
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 1; // 2 상태에서 1 상태로 바꿔줌. 바 -> back key -> ㅂ ('ㅂ' 의 상태값은 1) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			case  3: // 현재 구성된 문자열이 단자음 + 단모음 + 단자음 일 경우 ( ex : 밥 )
				// iLast = (vCode - HANGUL_START) % NUM_OF_LAST_INDEX
				{
					/**
					 * 한글 UNICODE 값 정리 :: http://sexy.pe.kr/tc/113 참조
					 * '간'의 unicode : OxAC04
					 * '간'의 종성 자음 'ㄴ'의 index : 4
					 * (int) OxAC11 - 4 = OxAC00. OxAC00의 CODE 값 : '가'
					 */
					int lcIndex = GetLastConsonantIndex(code); // 현재 code(갑)의 종성자음 'ㅂ'의 index 값을 가져와서
					code = (char)((int)code - lcIndex); // 현재 code(갑)에서 종성자음 'ㅂ'을 빼서 '가'의 code값을 만들어
					mCompositionString = ""; // 문자열을 초기화 한 후
					mCompositionString += code; // '가'의 code 값을 문자열에 add
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 2; // 3 상태에서 2 상태로 바꿔줌. 밥 -> back key -> 바 ('바' 의 상태값은 2) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			case  5: // 현재 구성된 문자열이 조합모음(조합모음 : ㅟ ㅢ etc.. ㅔ ㅐ등 조합하지 않고 쓰는 경우는 단모음으로 본다) 일 경우  ( ex : ㅟ )
				{
					int vIndex = GetVowelIndex(code); // 현재 code의 모음값의 index ('ㅟ'의 경우 16)
					if (vIndex < 0) // vIndex < 0 인경우는 모음 아님
					{
						ret = ACTION_ERROR;
						break;
					}
					int newIndex = InputTables.Vowels.iMiddle[vIndex]; // 'ㅟ' 조합중성의 첫값인 ㅜ의 index 값 13
					if (newIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
					code = InputTables.Vowels.Code[newIndex]; // 중성배열의 13번째 값 'ㅜ'의 code 값
					mCompositionString = ""; // 문자열을 초기화 한 후
					mCompositionString += code; // 'ㅜ'의 code 값을 문자열에 add
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 4; // 5 상태에서 4 상태로 바꿔줌. ㅟ -> back key -> ㅜ ('ㅜ' 의 상태값은 4) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			case 10: // 현재 구성된 문자열이 조합자음(조합자음 : ㅄ ㄳ etc.. ㅆ ㅃ등 조합하지 않고 쓰는 경우는 단자음으로 본다) 일 경우  ( ex : ㅄ )
				{
					KeyboardLogPrint.e("first code :: " + code);
					int lcIndex = GetLastConsonantIndex(code); // 조합종성 'ㅄ' code의 index 값 : 18
					if (lcIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
					int newIndex = InputTables.LastConsonants.iLast[lcIndex];  // 조합자음 'ㅄ'의 첫번째 자음 'ㅂ'의 index 값 : 17

					if (newIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
					code = InputTables.LastConsonants.Code[newIndex]; // 조합자음의 17번째 값 'ㅂ'의 code값
					KeyboardLogPrint.e("second code :: " + code);
					mCompositionString = ""; // 문자열을 초기화 한 후
					mCompositionString += code; // 'ㅂ'의 code 값을 문자열에 add
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 1; // 10 상태에서 1 상태로 바꿔줌. ㅄ -> back key -> ㅂ ('ㅂ' 의 상태값은 1) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			case 11: // 현재 구성된 문자열이 단자음 + 단모음 + 조합자음 일 경우 ( ex : 값 )
				{

					KeyboardLogPrint.e("11 val :: " + code);
					int lcIndex = GetLastConsonantIndex(code); // '값' code의 종성자음의 'ㅄ'의 종성자음의 index를 가져옴 : 18
					if (lcIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
					int newIndex = InputTables.LastConsonants.iLast[lcIndex]; // '값'의 종성자음 'ㅄ'의 첫번째 자음 'ㅂ'의 index 값 : 17
					if (newIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
					KeyboardLogPrint.e("mState :: " + mState);
					KeyboardLogPrint.e("code :: " + code); // 값 : 0xAC12
					KeyboardLogPrint.e("lcIndex :: " + lcIndex); // 18 // ㅄ의 index 값
					KeyboardLogPrint.e("newIndex :: " + newIndex); // 17 // ㅂ의 index 값
					/** 값 - ㅄ = 가
					    가 + ㅂ = 갑 */
					code = (char)((int) code - lcIndex + newIndex);
					KeyboardLogPrint.e("result code :: " + code); // 갑 : 0xAC11
					mCompositionString = "";
					mCompositionString += code;
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 3; // 11 상태에서 3 상태로 바꿔줌. 값 -> back key -> 갑 ('갑' 의 상태값은 3) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			case 20: // 현재 구성된 문자열이 단자음 + 조합모음 일 경우 ( ex : 뷔 )
				{
					int fcIndex = GetFirstConsonantIndex(code); // 현재 code값의 초성 자음의 index를 가져옴. ex : '뷔'의 초성 자음 'ㅂ'의 index를 가져옴 7
					int vIndex = GetVowelIndex(code); // 현재 code값의 모음의 index를 가져옴 : ex : '뷔'의 모음 'ㅟ'의 index 16
					int newIndex = InputTables.Vowels.iMiddle[vIndex]; // code 값의 모음 'ㅟ'의 첫번째 모음 'ㅜ'의 index 13
					KeyboardLogPrint.e("fcIndex :: " + fcIndex);
					KeyboardLogPrint.e("vIndex :: " + vIndex);
					KeyboardLogPrint.e("newIndex :: " + newIndex);
					if (newIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
					code = ComposeCharWithIndexs(fcIndex, newIndex, 0); // 'ㅂ'의 index 값과 'ㅜ'의 index 값을 넣어 '부' code 값을 만듬
					mCompositionString = "";
					mCompositionString += code;
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 2; // 20 상태에서 2 상태로 바꿔줌. 뷔 -> back key -> 부 ('부' 의 상태값은 2) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			case 21: // 현재 구성된 문자열이 단자음 + 조합모음 + 단자음 일 경우 ( ex : 뷥, 봤 )
				{
					int lcIndex =  GetLastConsonantIndex(code); // 현재 code의 종성자음의 index를 가져옴 ex : '봤'의 종성자음 'ㅆ'의 index 20
					KeyboardLogPrint.e("lcIndex ::: " + lcIndex);
					code =  (char) ((int) code - lcIndex); // '봤' - 'ㅆ' = '봐'
					mCompositionString = "";
					mCompositionString += code;
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 20; // 21 상태에서 20 상태로 바꿔줌. 뷥 -> back key -> 뷔 ('뷔' 의 상태값은 20) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			case 22: // 현재 구성된 문자열이 단자음 + 조합모음 + 조합자음 일 경우 ( ex : 쇣 )
				{
					int lcIndex = GetLastConsonantIndex(code); // 현재 code의 종성자음의 index를 가져옴 ex : '쇣'의 종성자음 'ㄳ'의 index 3
					if (lcIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
					int newIndex = InputTables.LastConsonants.iLast[lcIndex]; // 현재 code의 종성자음 'ㄳ'의 첫 자음 'ㄱ'의 index를 가져옴 1
					if (newIndex < 0)
					{
						ret = ACTION_ERROR;
						break;
					}
//					code = (char)((int) code - lcIndex + newIndex);
					KeyboardLogPrint.e("mState :: " + mState);
					KeyboardLogPrint.e("code :: " + code); // 쇣 : 0xC1E3
					KeyboardLogPrint.e("lcIndex :: " + lcIndex); // 3 // ㅄ의 index 값
					KeyboardLogPrint.e("newIndex :: " + newIndex); // 1 // ㅂ의 index 값
					/** 쇣 - ㄳ + ㄱ = 쇡 */
					code = (char)((int) code - lcIndex + newIndex);
					KeyboardLogPrint.e("result code :: " + code); // 쇡 : 0xC1E1
					mCompositionString = "";
					mCompositionString += code;
					KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
					mState = 21; // 22 상태에서 21 상태로 바꿔줌. 삯 -> back key -> 삭 ('삭' 의 상태값은 21) 이므로
				}
				ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
				break;
			default: ret = ACTION_ERROR; // error. should not be here in any circumstance.
		}
		return ret;
	}

	/** 문자열 및 상태 초기화 */
	@Override
	public int FinishAutomataWithoutInput() // Input is ended by external causes
	{
		int ret = ACTION_NONE;
		if (mKoreanMode) //  && mState > 0)
		{
			mCompleteString = "";
			mCompositionString = "";
			mState = 0;
			//ret |= ACTION_UPDATE_COMPOSITIONSTR;
			//ret |= ACTION_UPDATE_COMPLETESTR;
		}
		return ret;
	}

	/**
	 * 한글 키보드의 키가 눌렸을 경우 호출 됨
	 * @param code 입력된 한글의 code
	 * @param KeyState
     * @return
     */
	@Override
	public int DoAutomata(char code, int KeyState) // , String CurrentCompositionString)
	{
		KeyboardLogPrint.e("DoAutomata code :: " + code + ", KeyState :: " + KeyState);
		int result = ACTION_NONE;
		int AlphaIndex = GetAlphabetIndex(code); // alphabet인지 여부
		char hcode;  // 해당 key의 utf-8(hex) 값
		KeyboardLogPrint.e("AlphaIndex :: " + AlphaIndex);
		/* remove toggle key check and backspace check.
		// check toggle key first
		if (IsToggleKey(code, KeyState)) // SHIFT-SPACE
		{
			// toggle Korean/English
			if (mState != 0) // flushing..
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mState = 0;
				result = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			mKoreanMode = !mKoreanMode; // input mode toggle
		}
		else if (code == InputTables.BACK_SPACE)
		{
			// do back space
		}
		else */
		if (AlphaIndex < 0) // white spaces...
		{
			KeyboardLogPrint.e("mKoreanMode :: " + mKoreanMode);
			if (mKoreanMode)
			{
				// flush Korean characters first.
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mState = 0;
				result = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}

			// process the code as English
			if ((KeyState & (InputTables.KEYSTATE_ALT_MASK | InputTables.KEYSTATE_CTRL_MASK | InputTables.KEYSTATE_FN)) == 0)
			{
				// result |= expression  => result = result | expression (비트 논리합 할당 연산자)
				// 두 식의 특정 자릿수 중 하나가 1이면 해당 자릿수의 결과 값은 1이 되고, 그렇지 않으면 해당 자릿수의 결과 값은 0이 됩니다.
				result |= ACTION_USE_INPUT_AS_RESULT;
			}
		}
		else if (!mKoreanMode){
			KeyboardLogPrint.e("no korean mode");
			// process the code as English
			result = ACTION_USE_INPUT_AS_RESULT;
		}
		else {
			KeyboardLogPrint.e("else");
			if ((KeyState & InputTables.KEYSTATE_SHIFT_MASK) == 0)
			{
				KeyboardLogPrint.e("not shift");
				// 쿼티 한글의 shift 눌렀을 경우 해당 키 값에 해당하는 utf-8(hex) 값을 return 한다.
				hcode = InputTables.NormalKeyMap.Code[AlphaIndex];
			}
			else
			{
				KeyboardLogPrint.e("shift");
				// 쿼티 한글일 경우 해당 키 값에 해당하는 utf-8(hex) 값을 return 한다.
				hcode = InputTables.ShiftedKeyMap.Code[AlphaIndex];
			}
			KeyboardLogPrint.e("DoAutomata mState :: " + mState);
			switch (mState)
			{
				case 0: result = DoState00(hcode); break; // current composition string: NULL
				case 1: result = DoState01(hcode); break; // current composition string: single consonant only
				case 2: result = DoState02(hcode); break; // current composition string: single consonant + single vowel
				case 3: result = DoState03(hcode); break; // current composition string: single consonant + single vowel + single consonant
				case 4: result = DoState04(hcode); break; // current composition string: single vowel
				case 5: result = DoState05(hcode); break; // current composition string: a combined vowel
				case 10: result = DoState10(hcode); break; // current composition string: a combined consonant
				case 11: result = DoState11(hcode); break; // current composition string: single consonant + single vowel + a combined consonant
				case 20: result = DoState20(hcode); break; // current composition string: single consonant + a combined vowel
				case 21: result = DoState21(hcode); break; // current composition string: single consonant + a combined vowel + single consonant
				case 22: result = DoState22(hcode); break; // current composition string: single consonant + a combined vowel + a combined consonant
				default: result = ACTION_ERROR; // error. should not be here in any circumstance.
			}
		}
		return result;
	};

	/**
	 * 현재 구성된 문자열 null 일 경우 호출 됨
	 * @param code
	 * @return
     */
	private int DoState00(char code) // current composition string: NULL
	{
		KeyboardLogPrint.e("DoState00 :: " + code);
		KeyboardLogPrint.e("DoStateOO CODE :: " + code);
		if (IsConsonant(code)) // 입력된 코드값이 자음이면
		{
			mState = 1; // '현재 구성된 문자열이 단자음' 상태로 바꿈
		}
		else
		{
			mState = 4; // '현재 구성된 문자열이 단모음' 상태로 바꿈
		}
		mCompleteString = "";
		mCompositionString = "";
		mCompositionString += code; // 입력창의 문자열을 입력된 코드로 바꿈
		return ACTION_UPDATE_COMPOSITIONSTR | ACTION_APPEND;
	};

	/**
	 * 현재 구성된 문자열이 단자음일 경우 호출 됨
	 * @param code
	 * @return
	 */
	private int DoState01(char code)
	{
		KeyboardLogPrint.e("DoState01 :: " + code);
		KeyboardLogPrint.e("mCompositionString :: " + mCompositionString);
		if (mCompositionString == "") // DoState01은 현재 상태가 1 일 경우 즉 입력창에 단자음이 존재할 경우에만 호출되어야 하는데 mCompositionString이 빈값이면 에러임
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code)) // 입력 코드가 자음일 경우
		{
			// 2022.03.28 초성 자음 입력 시 조합자음 만들어지지 않도록 수정
			mCompleteString = mCompositionString; // 현재 입력창의 값을 우선 mCompleteString에 담아놓음
			mCompositionString = "";
			mCompositionString += code;
			// 상태 1로 바꾸는 이유는 조합자음이 만들어지지 않았기 때문에 이후 들어오는 값들은 마지막에 들어온 ㅈ 하나만 있는 상태로 바꾸어야 함
			// ex. ㅂㅈ 이후 'ㅏ'가 들어올 경우 상태가 단자음만 있을 경우여야 ㅂ자 와 같이 만들어지기 때문
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			/*
			char newCode = CombineLastConsonantWithCode(mCompositionString.charAt(0), code); // 현재 입력창에 있는 자음과 넘어온 code(자음)의 조합자음

			if (newCode == (char)0) // 조합자음 값이 없다면 (ex : 기존의 값이 ㅂ 이고 새로 들어온 값이 ㅈ 이라면 이 두 값으로 조합자음이 만들어질 수 없는 경우
			{
				mCompleteString = mCompositionString; // 현재 입력창의 값을 우선 mCompleteString에 담아놓음
				mCompositionString = "";
				mCompositionString += code;
				// 상태 1로 바꾸는 이유는 조합자음이 만들어지지 않았기 때문에 이후 들어오는 값들은 마지막에 들어온 ㅈ 하나만 있는 상태로 바꾸어야 함
				// ex. ㅂㅈ 이후 'ㅏ'가 들어올 경우 상태가 단자음만 있을 경우여야 ㅂ자 와 같이 만들어지기 때문
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			else // 조합자음이 되었다면
			{
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newCode; // 입력창의 값을 조합자음으로 바꿈
				mState = 10; // '현재 구성된 문자열이 조합자음' 상태로 바꿈
			ret = ACTION_UPDATE_COMPOSITIONSTR;
			}

			 */
		}
		else // 입력된 값이 자음이 아닐 경우
		{
			KeyboardLogPrint.e("입력된 값이 자음이 아닐 경우");
			int fcIndex = ConvertFirstConsonantCodeToIndex(mCompositionString.charAt(0));
			int vIndex = ConvertVowelCodeToIndex(code);
			char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			KeyboardLogPrint.e("fcIndex :: " + fcIndex);
			KeyboardLogPrint.e("vIndex :: " + vIndex);
			KeyboardLogPrint.e("newCode :: " + newCode);
			mCompleteString = "";
			mCompositionString = "";
			mCompositionString += newCode;
			mState = 2;
			ret = ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	/**
	 * 현재 구성된 문자열이 단자음 + 단모음 일 경우 호출됨
	 * @param code
	 * @return
     */
	private int DoState02(char code)
	{
		KeyboardLogPrint.e("DoState02 :: " + code);
		KeyboardLogPrint.e("mCompositionString :: " + mCompositionString);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code)) // 입력된 코드값이 자음일 경우
		{
			int lcIndex = GetLastConsonantIndex(code); // 입력된 코드값의 종성자음 index를 가져옴
			if (lcIndex != -1) // 입력된 코드값이 종성자음일 경우
			{
				// 기존 입력창에 '가' 가 입력되어 있고 새로 들어온 코드 값이 'ㄱ' 일 경우
				KeyboardLogPrint.e("mCompositionString.charAt(0) :: " + mCompositionString.charAt(0)); // '가'
				char newCode = (char)((int) mCompositionString.charAt(0) + lcIndex);
				KeyboardLogPrint.e("newCode :: " + newCode); // 각
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newCode; // 입력창의 값을 '각'으로 바꿔줌
				mState = 3; // '현재 구성된 문자열이 단자음 + 단모음 + 단자음' 상태로 바꿈
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else // 입력된 코드값이 종성자음이 아닐 경우 ex. 기존 입력창의 값이 '가' 이고 새로 들어온 코드 값이 'ㅃ'(종성자음에 없는 자음) 일 경우
			{
				KeyboardLogPrint.e("DoState02 신규값이 종성자음이 아님");
				mCompleteString = mCompositionString; // 기존 입력창의 값 '가'를 mCompleteString에 담아놓음
				mCompositionString = "";
				mCompositionString += code;
				mState = 1; // '현재 구성된 문자열이 단자음 일 경우' 상태로 변경
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		else // 입력된 코드값이 모음일 경우
		{
			char vCode = GetVowel(mCompositionString.charAt(0)); // 현재 입력창의 중성모음을 가져옴 ex. 현재 입력창의 값이 '고' 일 경우 'ㅗ'
			char newCode = CombineVowelWithCode(vCode, code); // 현재 입력창의 중성모음과 새로 들어온 모음의 조합모음 ex. 현재 입력창의 값 '고'의 중성모음 'ㅗ'와 새로 들어온 모음 'ㅏ'의 조합모음 'ㅘ'
			KeyboardLogPrint.e("vCode :: " + vCode);
			KeyboardLogPrint.e("newCode :: " + newCode);

			if (newCode != (char) 0) // 입력창 값의 모음과 새로 들어온 모음이 조합모음인 경우 ex. 입력창의 값 '고'의 모음 'ㅗ'와 새로 들어온 모음 'ㅏ'일 경우 조합모음 ㅘ가 된다
			{
				int fcIndex = GetFirstConsonantIndex(mCompositionString.charAt(0));
				int vIndex = ConvertVowelCodeToIndex(newCode);
				char newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
				KeyboardLogPrint.e("fcIndex" + fcIndex);
				KeyboardLogPrint.e("vIndex" + vIndex);
				KeyboardLogPrint.e("newChar" + newChar);
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newChar; // 입력창의 값을 '과'로 바꿔줌
				mState = 20;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else // 입력창 값의 모음과 새로 들어온 모음이 조합모음이 아닌경우 ex. 입력창의 값 '고'의 모음 'ㅗ'와 새로 들어온 모음 'ㅔ'일 경우 조합모음이 안된다.
			{
				mCompleteString = mCompositionString; // 기존 입력창의 값 '고'를 mCompleteString에 담아놓음
				mCompositionString = "";
				mCompositionString += code;
				mState = 4; // '현재 구성된 문자열이 단모음' 상태값으로 변경
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		return ret;
	};

	/**
	 * 현재 구성된 문자열이 단자음 + 단모음 + 단자음 일 경우 호출됨
	 * @param code
	 * @return
	 */
	private int DoState03(char code)
	{
		KeyboardLogPrint.e("DoState03 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
			KeyboardLogPrint.e("mCompositionString.char :: " + mCompositionString.charAt(0));
			KeyboardLogPrint.e("lcIndex :: " + lcIndex);
			if (lcIndex < 0)
			{
				return ACTION_ERROR;
			}
			char newCode = CombineLastConsonantWithCode(InputTables.LastConsonants.Code[lcIndex], code);
			if (newCode != (char)0) // 새로 입력된 자음과 기존 문자열의 종성자음이 조합자음이 된다면
			{
				// 기존 문자열에서 종성자음을 제거하고 새로 조합된 조합자음을 붙인다. ex: 기존 문자 ( 갑 ), 새로 입력된 자음 ( ㅅ )이면 '갑'에서 'ㅂ'제거 해서
				// '가'를 만들고 여기에 종성조합자음(ㅄ)을 붙여 '값'을 만든다.
				char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex + GetLastConsonantIndex(newCode));
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newChar;
				mState = 11;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else // 새로 입력된 자음과 기존 문자열의 종성자음이 조합자음이 아니라면
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		else // vowel
		{
			int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
			if (lcIndex < 0)
			{
				return ACTION_ERROR;
			}
			char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex); // remove last consonant and flush it.
			mCompleteString = "";
			mCompleteString += newChar;
			int fcIndex = GetFirstConsonantIndex(InputTables.LastConsonants.Code[lcIndex]);
			if (fcIndex < 0)
			{
				return ACTION_ERROR;
			}
			int vIndex = GetVowelIndex(code);
			char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0); // compose new composition string
			mCompositionString = "";
			mCompositionString += newCode;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState04(char code) // current composition string: single vowel
	{
		KeyboardLogPrint.e("DoState04 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			char newCode = CombineVowelWithCode(mCompositionString.charAt(0), code);
			if (newCode != (char) 0)
			{
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newCode;
				mState = 5;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 4;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		return ret;
	};

	private int DoState05(char code) // current composition string: a combined vowel
	{
		KeyboardLogPrint.e("DoState05 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 4;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState10(char code) // current composition string: a combined consonant
	{
		KeyboardLogPrint.e("DoState10 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			int lcIndex0 = GetLastConsonantIndex(mCompositionString.charAt(0));
			int lcIndex1 = InputTables.LastConsonants.iLast[lcIndex0];
			int fcIndex = InputTables.LastConsonants.iFirst[lcIndex0];
			mCompleteString = "";
			mCompleteString += InputTables.LastConsonants.Code[lcIndex1];
			int vIndex = GetVowelIndex(code);
			char newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState11(char code) // current composition string: single consonant + single vowel + a combined consonant
	{
		KeyboardLogPrint.e("DoState11 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			int lcIndexOrg = GetLastConsonantIndex(mCompositionString.charAt(0));
			int fcIndexOrg = GetFirstConsonantIndex(mCompositionString.charAt(0));
			int vIndexOrg = GetVowelIndex(mCompositionString.charAt(0));
			int lcIndexNew = InputTables.LastConsonants.iLast[lcIndexOrg];
			char newChar = ComposeCharWithIndexs(fcIndexOrg, vIndexOrg, lcIndexNew);
			int fcIndexNew = InputTables.LastConsonants.iFirst[lcIndexOrg];
			int vIndexNew = ConvertVowelCodeToIndex(code);
			mCompleteString = "";
			mCompleteString += newChar;
			newChar = ComposeCharWithIndexs(fcIndexNew, vIndexNew, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState20(char code) // current composition string: single consonant + a combined vowel
	{
		KeyboardLogPrint.e("DoState20 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			int lcIndex = ConvertLastConsonantCodeToIndex(code);
			if (lcIndex < 0) // cannot compose the code with composition string. flush it.
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			else // compose..
			{
				char newChar = mCompositionString.charAt(0);
				newChar = (char)((int)newChar + lcIndex);
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newChar;
				mState = 21;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		else
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 4;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState21(char code) // current composition string: single consonant + a combined vowel + single consonant
	{
		KeyboardLogPrint.e("DoState21 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
			int lcIndexTemp = ConvertLastConsonantCodeToIndex(code);
			if (lcIndexTemp < 0)
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			else {
				int lcIndexNew = CombineLastConsonantWithIndex(lcIndex, lcIndexTemp);
				if (lcIndexNew < 0)
				{
					mCompleteString = mCompositionString;
					mCompositionString = "";
					mCompositionString += code;
					mState = 1;
					ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
				}
				else
				{
					char newChar = mCompositionString.charAt(0);
					newChar = (char)((int)newChar - lcIndex + lcIndexNew);
					mCompleteString = "";
					mCompositionString = "";
					mCompositionString += newChar;
					mState = 22;
					ret = ACTION_UPDATE_COMPOSITIONSTR;
				}
			}

		}
		else
		{
			char newChar = mCompositionString.charAt(0);
			int lcIndex = GetLastConsonantIndex(newChar);
			newChar = (char)((int)newChar - lcIndex);
			mCompleteString = "";
			mCompleteString += newChar;
			int fcIndex = ConvertFirstConsonantCodeToIndex(InputTables.LastConsonants.Code[lcIndex]);
			int vIndex = ConvertVowelCodeToIndex(code);
			newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState22(char code) // current composition string: single consonant + a combined vowel + a combined consonant
	{
		KeyboardLogPrint.e("DoState22 :: " + code);
		if (mCompositionString == "")
		{
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			char tempChar = mCompositionString.charAt(0);
			int lcIndex0 = GetLastConsonantIndex(tempChar);
			int lcIndex1 = InputTables.LastConsonants.iLast[lcIndex0];
			int fcIndex = InputTables.LastConsonants.iFirst[lcIndex0];

			tempChar = (char) ((int) tempChar - lcIndex0 + lcIndex1);
			mCompleteString = "";
			mCompleteString += tempChar;

			int vIndex = GetVowelIndex(code);
			char newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	@Override
	public void setInitState()
	{
		KeyboardLogPrint.d("setInitState korean");
	}
}
