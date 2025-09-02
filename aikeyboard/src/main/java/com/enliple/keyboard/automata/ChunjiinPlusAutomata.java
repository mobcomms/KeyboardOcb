/*
 * halbae87: this project is created from Soft Keyboard Sample source
 * 	but this part is my original source.
 */

package com.enliple.keyboard.automata;

import android.text.TextUtils;

import com.enliple.keyboard.activity.CharModel;
import com.enliple.keyboard.activity.CharTables;
import com.enliple.keyboard.activity.InputTables;
import com.enliple.keyboard.activity.VowelCharModel;
import com.enliple.keyboard.common.KeyboardLogPrint;

import java.util.ArrayList;


public class ChunjiinPlusAutomata extends Automata {

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
    /**
     * 한글 유니코드 시작. '가' : 0xAC00
     */
    public static int HANGUL_START = 0xAC00;
    /**
     * 한글 유니코드 끝. '핳' : 0xD7A3
     */
    public static int HANGUL_END = 0xD7A3;
    /**
     * 한글 자모음 시작
     */
    public static int HANGUL_JAMO_START = 0x3131;
    /**
     * 한글 중성 모음 시작
     */
    public static int HANGUL_MO_START = 0x314F;
    /**
     * 한글 중성 모음 끝
     */
    public static int HANGUL_JAMO_END = 0x3163;
    private static final String TAG = "KoreanAutomata";

    // Action Codes
    public static final int ACTION_NONE = 0;
    public static final int ACTION_UPDATE_COMPOSITIONSTR = 1;
    /**
     * 문자열 update
     */
    public static final int ACTION_UPDATE_COMPLETESTR = 2;
    public static final int ACTION_USE_INPUT_AS_RESULT = 4;
    /**
     * 입력값이 영문이거나 한글 키패드인 상태에서  값이 하나(ㄱ , ㅏ, 빈값)만 입력된 상태에서 back key 눌렀을 경우 setting 됨
     */
    public static final int ACTION_APPEND = 8;
    public static final int ACTION_MAKE_VOWEL = 16;
    public static final int ACTION_REMAKECHAR = 32;
    public static final int ACTION_REMOVE_PREV_CHAR = 64;
    public static final int ACTION_MAKE_DOUBLE_DOT = 17;
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

    /**
     * edit 영역의 text 값
     */
    private String mCompositionString = "";
    private String mCompleteString = "";
    private String mConsonantBeforeDoubleDot = "";

    //    '박' 에서 . 이 입력될 경우 '박'이라는 값을 임시 값(mPrefCharbeforeDot)으로 저장하고 상태를 4(단모음)으로 넘김
//    4 상태에서는 새로 들어온 입력값이 l 혹은 ㅡ 일 경우 그 앞의 숫자가 .인지 판단하고 , 이라면 ㅓ 혹은 ㅗ로 단모음 값을 변경
//    mPrefCharbeforeDot 값이 있을 경우 이 값에서 종성자음 ㄱ을 뺀 '바'라는 글자와 종성자음 ㄱ과 새로 만들어진 모음인 ㅓ 혹은 ㅗ로 새로운 문자
//    거 혹은 고를 붙여 갱신하고 mPrefCharbeforeDot을 초기화
    private String mPrefCharbeforeDot = "";

    private String mPrefComplete = ""; // 갍 -> 갈ㄷ으로 갈때 '갈'을 임시로 저장하는 변수.
    private boolean mKoreanMode = false;

    //    private long mStateChangeTime1 = 0L; // mState 가 2에서 3으로 변경되는 시점의 시간 ( 단자음 + 단모음 -> 단자음 + 단모음 + 단자음 으로 변하는 시점의 시간 )
    private long mStateChangeTime2 = 0L; // mState 가 20에서 21으로 변경되는 시점의 시간 ( 단자음 + 조합모음 -> 단자음 + 조합모음 + 단자음 으로 변하는 시점의 시간 )
    private char mLastConsonantChar1 = (char) 0; // mState가 2에서 3으로 변경되는 시점의 종성 단모음 값
    private char mLastConsonantChar2 = (char) 0; // mState가 20에서 21로 변경되는 시점의 종성 단모음 값

    private int mCharLength = 0;

    public ChunjiinPlusAutomata() {
        mState = 0;
        mCompositionString = "";
        mCompleteString = "";
        mKoreanMode = true; // 2017.02.27 기본 키보드 한글 변경
//        mKoreanMode = false;
    }

    @Override
    public int GetState() {
        return mState;
    }

    ;

    @Override
    public String GetCompositionString() {
        return mCompositionString;
    }

    ;

    @Override
    public String GetCompleteString() {
        return mCompleteString;
    }

    ;

    /**
     * 한글모드를 현재의 반대모드로 변경한다
     */
    @Override
    public void ToggleMode() {
        mKoreanMode = !mKoreanMode;
    }

    /**
     * 한글모드인지 여부를 check
     */
    @Override
    public boolean IsKoreanMode() {
        return mKoreanMode;
    }

    /**
     * 입력된 code 값이 한글인지 판단
     *
     * @param code 입력된 code 값
     * @return 입력된 code 값이 한글이면 true 아니면 false
     */
    public boolean IsHangul(char code) {
        if ((code >= HANGUL_START) && (code <= HANGUL_END))
            return true;
        if ((code >= HANGUL_JAMO_START) && (code <= HANGUL_JAMO_END))
            return true;
        return false;
    }

    public boolean IsJAMO(char code) {
        if ((code >= HANGUL_JAMO_START) && (code <= HANGUL_JAMO_END))
            return true;
        return false;
    }

    ;

    /**
     * 입력된 code 값이 초성 자음인지 check
     *
     * @param code 입력된 code 값
     * @return 자음이 맞을 경우 true, 자음이 아닐경우 false
     */
    public boolean IsConsonant(char code) {
        if ((code >= HANGUL_JAMO_START) && (code < HANGUL_MO_START))
            return true;
        return false;
    }

    ;

    /**
     * @param code 입력된 code 값
     * @return 모음이 맞을 경우 true, 모음이 아닐 경우 false
     */
    public boolean IsVowel(char code) {
        if ((code >= HANGUL_MO_START) && (code <= HANGUL_JAMO_END))
            return true;
        return false;
    }

    ;

    public boolean IsCVowel(char code) {
        if (code == 0x3163 || code == 0x318D || code == 0x3161)
            return true;
        else
            return false;
    }

    /**
     * not used.
     * public boolean IsLastConsonanted(char code)
     * {
     * if (IsHangul(code))
     * {
     * if (IsJAMO(code)) // <- need to fix, if this routine is to be used...
     * return true;
     * int offset = code - HANGUL_START;
     * if (offset % InputTables.NUM_OF_LAST_INDEX == 0)
     * return false;
     * else
     * return true;
     * }
     * else
     * {
     * // wrong input
     * return false;
     * }
     * }
     **/

    public char GetLastConsonant(int index) {
        KeyboardLogPrint.e("GetLastConsonant");
        char val = (char) 0;
        for (int i = 0; i < InputTables.NUM_OF_LAST_INDEX; i++) {
            if (i == index) {
                val = InputTables.LastConsonants.Code[i];
            }
        }

        return val;
    }

    public int GetLastConsonantIndex(char code) {
        KeyboardLogPrint.e("GetLastConsonantIndex");
        int lcIndex = -1;
        if (IsHangul(code)) {
            if (IsJAMO(code)) {
                if (IsConsonant(code)) {
                    for (lcIndex = 0; lcIndex < InputTables.NUM_OF_LAST_INDEX; lcIndex++) {
                        if (code == InputTables.LastConsonants.Code[lcIndex]) {
                            break;
                        }
                    }
                    if (lcIndex >= InputTables.NUM_OF_LAST_INDEX)
                        lcIndex = -1;
                } else
                    lcIndex = -1;
            } else {
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

    public int GetFirstConsonantIndex(char code) {
        int fcIndex = -1;
        if (IsHangul(code)) {
            if (IsConsonant(code)) {
                for (fcIndex = 0; fcIndex < InputTables.NUM_OF_FIRST; fcIndex++)
                    if (code == InputTables.FirstConsonantCodes[fcIndex])
                        break;
                if (fcIndex >= InputTables.NUM_OF_FIRST)
                    fcIndex = -1;
            } else if (IsVowel(code)) {
                fcIndex = -1;
            } else {
                /**
                 입력된 code의 초성자음의 index 가져오는 수식 ( ex : 입력된 code 값이 '위' 이면 '위'의 초성자음인 'ㅇ'의 index 값인 11
                 */
                int offset = code - HANGUL_START;
                fcIndex = (offset / (InputTables.NUM_OF_MIDDLE * InputTables.NUM_OF_LAST_INDEX));
            }
        }
        KeyboardLogPrint.w("GetFirstConsonantIndex fcIndex :: " + fcIndex);
        return fcIndex;
    }

    public char GetFirstConsonant(char code) {
        char fcCode;
        int fcIndex = GetFirstConsonantIndex(code);
        KeyboardLogPrint.i("GetFirstConsonant fcIndex :: " + fcIndex);
        if (fcIndex < 0)
            fcCode = (char) 0;
        else
            fcCode = InputTables.FirstConsonantCodes[fcIndex];
        KeyboardLogPrint.i("GetFirstConsonant fcCode :: " + fcCode);
        return fcCode;
    }

    /**
     * 해당 code 값이 vowel array의 몇번째 index 값인지 알아내는 함수
     *
     * @param code : 입력된 vowel code
     * @return 입력된 code값에 해당하는 voewl array의 index값
     */
    public int GetVowelIndex(char code) {
        int vIndex = -1;
        if (IsHangul(code)) {
            if (IsVowel(code)) // vowel only character..
            {
                KeyboardLogPrint.e("GetVowelIndex vowel");
                vIndex = ConvertVowelCodeToIndex(code);
            } else {
                KeyboardLogPrint.e("GetVowelIndex not vowel");
                int offset = code - HANGUL_START;
                vIndex = (offset % (InputTables.NUM_OF_MIDDLE * InputTables.NUM_OF_LAST_INDEX)) / InputTables.NUM_OF_LAST_INDEX;
            }
        }
        return vIndex;
    }

    /**
     * 입력된 code값에 대한 모음을 가져옴
     *
     * @param code 입력된 code 값
     * @return 입력된 code 값에 해당하는 모음(char)
     */
    public char GetVowel(char code) {
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
     *
     * @param fcCode 초성 자음 code
     * @return 입력된 초성자음의 index 값
     */
    public int ConvertFirstConsonantCodeToIndex(char fcCode) // fcCode should be one of "First Consonants" otherwise return -1
    {
        int fcIndex = 0;
        while (fcIndex < InputTables.NUM_OF_FIRST) {
            if (fcCode == InputTables.FirstConsonantCodes[fcIndex])
                break;
            fcIndex++;
        }
        if (fcIndex == InputTables.NUM_OF_FIRST)
            fcIndex = -1;
        return fcIndex;
    }

    /**
     * 입력된 code 값이 종성 배열의 몇번재 값인지를 얻음
     *
     * @param lcCode 종성 자음 code
     * @return 입력된 종성자음의 index 값
     */
    public int ConvertLastConsonantCodeToIndex(char lcCode) // fcCode should be one of "Last Consonants", otherwise return -1
    {
        int lcIndex = 0;
        while (lcIndex < InputTables.NUM_OF_LAST_INDEX) {
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
     *
     * @param vCode 입력된 voewl code
     * @return 입력된 vowel code 값에 해당하는 index
     */
    public int ConvertVowelCodeToIndex(char vCode) {
        if (vCode < InputTables.Vowels.Code[0])
            return -1;
        int vIndex = vCode - InputTables.Vowels.Code[0];
        if (vIndex >= InputTables.NUM_OF_MIDDLE)
            return -1;
        return vIndex;
    }

    /**
     * 입력된 두 자음을 가지고 조합된 unicode 값을 가져옴
     *
     * @param cIndex1 입력된 자음 index
     * @param cIndex2 입력된 자음 index
     * @return 입력된 두 자음으로 조합된 조합자음의 unicode 값을 return
     */
    public int CombineLastConsonantWithIndex(int cIndex1, int cIndex2) {
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
     * 입력된 두 자음을 조합한 조합자음값을 출력 (천지인+)
     *
     * @param lcCode1 기존 자음 문자
     * @param lcCode2 입력된 자음 문자
     * @return 두 입력 자음이 조합된 문자
     */
    public CharModel ChunjiinCombineLastConsonantWithCode(char lcCode1, char lcCode2) {
        KeyboardLogPrint.e("ChunjiinCombineLastConsonantWithCode first : " + lcCode1);
        KeyboardLogPrint.e("ChunjiinCombineLastConsonantWithCode second : " + lcCode2);
        char newCode = (char) 0;
        char rotatedCode = (char) 0;
        int mode = 0;
        if (lcCode1 == 0x314B) // ㅋ
        {
            if (lcCode2 == 0x314B) // ㅋ
            {
                rotatedCode = 0x3132; // ㄲ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x3132) // ㄲ
        {
            if (lcCode2 == 0x314B) // ㅋ
            {
                rotatedCode = 0x314B; // ㅋ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x314C) // ㅌ
        {
            if (lcCode2 == 0x314C) // ㅌ
            {
                rotatedCode = 0x3138; // ㄸ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x3138) //ㄸ
        {
            if (lcCode2 == 0x314C) // ㅌ
            {
                rotatedCode = 0x314C; // ㅌ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x314D) // ㅍ
        {
            if (lcCode2 == 0x314D) // ㅍ
            {
                rotatedCode = 0x3143; // ㅃ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x3143) //ㅃ
        {
            if (lcCode2 == 0x314D) // ㅍ
            {
                rotatedCode = 0x314D; // ㅍ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x314E) // ㅎ
        {
            if (lcCode2 == 0x314E) // ㅎ
            {
                rotatedCode = 0x3146; // ㅆ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x3146) //ㅆ
        {
            if (lcCode2 == 0x314E) // ㅎ
            {
                rotatedCode = 0x314E; // ㅎ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x314A) // ㅊ
        {
            if (lcCode2 == 0x314A) // ㅊ
            {
                rotatedCode = 0x3149; // ㅉ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x3149) //ㅉ
        {
            if (lcCode2 == 0x314A) // ㅊ
            {
                rotatedCode = 0x314A; // ㅊ
                mode = -1;
                newCode = (char) 0;
            }
        } else if (lcCode1 == 0x3131) // ㄱ
        {
            if (lcCode2 == 0x3145) // ㅅ
            {
                newCode = 0x3133; // ㄳ
            }
        } else if (lcCode1 == 0x3134) // ㄴ
        {
            if (lcCode2 == 0x3148) // ㅈ
                newCode = 0x3135; // ㄵ
            else if (lcCode2 == 0x314E) // ㅎ
                newCode = 0x3136; // ㄶ
        }  else if (lcCode1 == 0x3142) // ㅂ
        {
            if (lcCode2 == 0x3145) {
                newCode = 0x3144; // ㅄ
            }
        } else if (lcCode1 == 0x3139) // ㄹ
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
            /**
             else if (lcCode2 == 0x3134) // ㄴ
             {
             rotatedCode = 0x3134; // ㄴ
             mode = -1;
             newCode = (char) 0;
             }**/
        }

        CharModel model = new CharModel();
        model.setChar(newCode);
        model.setMode(mode);
        model.setRotatedChar(rotatedCode);

        return model;
    }

    /**
     * 입력된  두 문자를 조합하여 조합모음을 만듬
     *
     * @param vCode1 입력된 첫번째 문자
     * @param vCode2 입력된 두번째 문자
     * @return 입력된 두 문자가 조합된 조합모음
     */
    public VowelCharModel ChunjiinCombineVowelWithCode(char vCode1, char vCode2) {
        VowelCharModel model = new VowelCharModel();

        char newCode = (char) 0;
        boolean isCombinedVowel = false;
        if (vCode1 == 0x3157) // ㅗ
        {
            if (vCode2 == 0x314F) // ㅏ
                newCode = 0x3158; // ㅘ
            else if (vCode2 == 0x3150) // ㅐ
                newCode = 0x3159; // ㅙ
            else if (vCode2 == 0x3163) // ㅣ
                newCode = 0x315A; // ㅚ

            isCombinedVowel = true;
        } else if (vCode1 == 0x315C) // ㅜ
        {
            if (vCode2 == InputTables.DotCode) {
                newCode = 0x3160; // ㅠ
            } else {
                if (vCode2 == 0x3153) // ㅓ
                    newCode = 0x315D; // ㅝ
                else if (vCode2 == 0x3154) // ㅔ
                    newCode = 0x315E;  // ㅞ
                else if (vCode2 == 0x3163) // ㅣ
                    newCode = 0x315F; // ㅟ

                isCombinedVowel = true;
            }
        } else if (vCode1 == 0x3161) // ㅡ
        {
            if (vCode2 == 0x3163) // ㅣ
            {
                newCode = 0x3162; // ㅢ
                isCombinedVowel = true;
            } else if (vCode2 == InputTables.DotCode) {
                newCode = 0x315C;
            }
        } else if (vCode1 == 0x3163) // ㅣ
        {
            if (vCode2 == InputTables.DotCode)
                newCode = 0x314F;
        } else if (vCode1 == 0x314F) //ㅏ
        {
            if (vCode2 == InputTables.DotCode)
                newCode = 0x3151;
            else if (vCode2 == 0x3163) // ㅣ
                newCode = 0x3150; //ㅐ
        } else if (vCode1 == 0x3151) //ㅑ
        {
            if (vCode2 == 0x3163) //ㅣ
                newCode = 0x3152;
            else if ( vCode2 == InputTables.DotCode )
                newCode = 0x314F;
        } else if (vCode1 == 0x3153) // ㅓ
        {
            if (vCode2 == 0x3163) // ㅣ
            {
                newCode = 0x3154; // ㅔ
            }
        } else if (vCode1 == 0x3155) //ㅕ
        {
            if (vCode2 == 0x3163) //ㅣ
                newCode = 0x3156;
        } else if (vCode1 == 0x3160) // ㅠ
        {
            if (vCode2 == 0x3163) //ㅣ
            {
                newCode = 0x315D; // ㅝ
                isCombinedVowel = true;
            }
            else if (vCode2 == InputTables.DotCode)
                newCode = 0x315C;
        }

        model.setVowelChar(newCode);
        model.setCombinedState(isCombinedVowel);
        return model;
    }

    /**
     * \
     * 입력된 초성, 중성, 종성의 index값을 가지고 한글 문자 조합
     *
     * @param fcIndex 초성 index
     * @param vIndex  중성 index
     * @param lcIndex 종성 index
     * @return
     */
    public char ComposeCharWithIndexs(int fcIndex, int vIndex, int lcIndex) {
        KeyboardLogPrint.i("ComposeCharWithIndexs fcIndex :: " + fcIndex);
        char Code = (char) 0;
        if ((fcIndex >= 0) && (fcIndex < InputTables.NUM_OF_FIRST)) // 입력된 fcIndex 값이 초성 범위 내에 있을 경우
        {
            if ((vIndex >= 0) && (vIndex < InputTables.NUM_OF_MIDDLE)) // 입력된 vIndex가 중성 범위내에 있을 경우
            {
                if ((lcIndex >= 0) && (lcIndex < InputTables.NUM_OF_LAST)) // 입력된 lcIndex가 종성 범위내에 있을 경우
                {
                    // 입력된 초성 인덱스(fcIndex), 중성 인덱스(vIndex), 종성 인덱스 (lcIndex)를 조합하여 code값 만듬
                    int offset = fcIndex * InputTables.NUM_OF_MIDDLE * InputTables.NUM_OF_LAST_INDEX + vIndex * InputTables.NUM_OF_LAST_INDEX + lcIndex;
                    Code = (char) (offset + HANGUL_START);
                }
            }
        }
        return Code;
    }

    /**
     * 입력된 코드의 alphabet index
     *
     * @param code 입력코드
     * @return 입력 코드에 해당하는 알파벳 인덱스
     */
    public int GetAlphabetIndex(char code) {
        if (code >= 'a' && code <= 'z')
            return (int) (code - 'a');
        if (code >= 'A' && code <= 'Z')
            return (int) (code - 'A');
        if (code == 'ㄲ')
            return 27;
        if (code == 'ㄸ')
            return 28;
        if (code == 'ㅃ')
            return 29;
        if (code == 'ㅆ')
            return 30;
        if (code == 'ㅉ')
            return 31;
        if (code == 'ㅋ')
            return (int) (code - 'ㄱ');

        return -1;
    }

    // korean mode 에서 back key 눌렀을 경우
    @Override
    public int DoBackSpace() {
        int ret = ACTION_NONE;
        char code;
        // 1. 현재 문자열의 값을 code에 넣음
        KeyboardLogPrint.w("DoBackSpace mCompositionString :: " + mCompositionString);
        KeyboardLogPrint.w("DoBackSpace mCompleteString :: " + mCompleteString);
        if (mCompositionString != "")
            code = mCompositionString.charAt(0);
        else
            code = (char) 0; // 문자열이 빈값일 경우 이 값을 set
        KeyboardLogPrint.e("code :: " + code);
        if (mState != 0 && code == (char) 0) // mState != 0, 즉 문자열 null인데 빈값일 경우의 code 값이 넘어올 경우 error
        {
            return ACTION_ERROR;
        }
        KeyboardLogPrint.e("mState :: " + mState);
        switch (mState) {
            case 0: // 현재 구성된 문자열 null 일 경우
                ret = ACTION_USE_INPUT_AS_RESULT; // 한글 키패드에서 값이 없을 경우 setting
                break;
            case 1:  // 현재 구성된 문자열이 단자음 일 경우 ( ex : ㅂ )
            case 4:  // 현재 구성된 문자열이 단모음 일 경우 ( ex : ㅏ )
                mCompositionString = ""; //  문자열 초기화 한 후
                KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
                mState = 0; //0, 1, 4 상태에서 back key를 누르면 아무 값도 없는 상태가 되므로 0상태로 바꿔 줌. ㅂ -> back key  ->  빈값 .
                ret = ACTION_USE_INPUT_AS_RESULT; // 한글 키패드에서 값이 없을 경우 setting
                break;

            case 2: // 현재 구성된 문자열이 단자음 + 단모음 일 경우 ( ex : 바 )
            {
                int fcIndex = GetFirstConsonantIndex(code); // '바'의 초성 자음 ㅂ의 index를 가져와서
                if (fcIndex == -1) {
                    ret = ACTION_ERROR;
                    break;
                }
                code = InputTables.FirstConsonantCodes[fcIndex]; // 'ㅂ'의 code를 가져와
                mCompositionString = ""; // 문자열을 초기화 한 후
                mCompositionString += code; // 'ㅂ'의 code를 문자열에 add
                KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
                mState = 1; // 2 상태에서 1 상태로 바꿔줌. 바 -> back key -> ㅂ ('ㅂ' 의 상태값은 1) 이므로
            }
            ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
            break;
            case 3: // 현재 구성된 문자열이 단자음 + 단모음 + 단자음 일 경우 ( ex : 밥 )
                // iLast = (vCode - HANGUL_START) % NUM_OF_LAST_INDEX
            {
                /**
                 * 한글 UNICODE 값 정리 :: http://sexy.pe.kr/tc/113 참조
                 * '간'의 unicode : OxAC04
                 * '간'의 종성 자음 'ㄴ'의 index : 4
                 * (int) OxAC11 - 4 = OxAC00. OxAC00의 CODE 값 : '가'
                 */
                int lcIndex = GetLastConsonantIndex(code); // 현재 code(갑)의 종성자음 'ㅂ'의 index 값을 가져와서
                code = (char) ((int) code - lcIndex); // 현재 code(갑)에서 종성자음 'ㅂ'을 빼서 '가'의 code값을 만들어
                mCompositionString = ""; // 문자열을 초기화 한 후
                mCompositionString += code; // '가'의 code 값을 문자열에 add
                KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
                mState = 2; // 3 상태에서 2 상태로 바꿔줌. 밥 -> back key -> 바 ('바' 의 상태값은 2) 이므로
            }
            ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
            break;
            case 5: // 현재 구성된 문자열이 조합모음(조합모음 : ㅟ ㅢ etc.. ㅔ ㅐ등 조합하지 않고 쓰는 경우는 단모음으로 본다) 일 경우  ( ex : ㅟ )
            {
                int vIndex = GetVowelIndex(code); // 현재 code의 모음값의 index ('ㅟ'의 경우 16)
                if (vIndex < 0) // vIndex < 0 인경우는 모음 아님
                {
                    ret = ACTION_ERROR;
                    break;
                }
                int newIndex = InputTables.Vowels.iMiddle[vIndex]; // 'ㅟ' 조합중성의 첫값인 ㅜ의 index 값 13
                if (newIndex < 0) {
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
                if (lcIndex < 0) {
                    ret = ACTION_ERROR;
                    break;
                }
                int newIndex = InputTables.LastConsonants.iLast[lcIndex];  // 조합자음 'ㅄ'의 첫번째 자음 'ㅂ'의 index 값 : 17

                if (newIndex < 0) {
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
                if (lcIndex < 0) {
                    ret = ACTION_ERROR;
                    break;
                }
                int newIndex = InputTables.LastConsonants.iLast[lcIndex]; // '값'의 종성자음 'ㅄ'의 첫번째 자음 'ㅂ'의 index 값 : 17
                if (newIndex < 0) {
                    ret = ACTION_ERROR;
                    break;
                }
                KeyboardLogPrint.e("mState :: " + mState);
                KeyboardLogPrint.e("code :: " + code); // 값 : 0xAC12
                KeyboardLogPrint.e("lcIndex :: " + lcIndex); // 18 // ㅄ의 index 값
                KeyboardLogPrint.e("newIndex :: " + newIndex); // 17 // ㅂ의 index 값
                /** 값 - ㅄ = 가
                 가 + ㅂ = 갑 */
                code = (char) ((int) code - lcIndex + newIndex);
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
                if (newIndex < 0) {
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
                int lcIndex = GetLastConsonantIndex(code); // 현재 code의 종성자음의 index를 가져옴 ex : '봤'의 종성자음 'ㅆ'의 index 20
                KeyboardLogPrint.e("lcIndex ::: " + lcIndex);
                code = (char) ((int) code - lcIndex); // '봤' - 'ㅆ' = '봐'
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
                if (lcIndex < 0) {
                    ret = ACTION_ERROR;
                    break;
                }
                int newIndex = InputTables.LastConsonants.iLast[lcIndex]; // 현재 code의 종성자음 'ㄳ'의 첫 자음 'ㄱ'의 index를 가져옴 1
                if (newIndex < 0) {
                    ret = ACTION_ERROR;
                    break;
                }
                KeyboardLogPrint.e("mState :: " + mState);
                KeyboardLogPrint.e("code :: " + code); // 쇣 : 0xC1E3
                KeyboardLogPrint.e("lcIndex :: " + lcIndex); // 3 // ㅄ의 index 값
                KeyboardLogPrint.e("newIndex :: " + newIndex); // 1 // ㅂ의 index 값
                /** 쇣 - ㄳ + ㄱ = 쇡 */
                code = (char) ((int) code - lcIndex + newIndex);
                KeyboardLogPrint.e("result code :: " + code); // 쇡 : 0xC1E1
                mCompositionString = "";
                mCompositionString += code;
                KeyboardLogPrint.e("mState :: " + mState + " , mCompositionString :: " + mCompositionString);
                mState = 21; // 22 상태에서 21 상태로 바꿔줌. 삯 -> back key -> 삭 ('삭' 의 상태값은 21) 이므로
            }
            ret = ACTION_UPDATE_COMPOSITIONSTR; // 문자열 update
            break;
            default:
                ret = ACTION_ERROR; // error. should not be here in any circumstance.
        }
        return ret;
    }

    /**
     * 문자열 및 상태 초기화
     */
    @Override
    public int FinishAutomataWithoutInput() // Input is ended by external causes
    {
        int ret = ACTION_NONE;
        if (mKoreanMode) //  && mState > 0)
        {
            mCompleteString = "";
            mCompositionString = "";
            mState = 0;
        }
        return ret;
    }

    /**
     * 한글 키보드의 키가 눌렸을 경우 호출 됨
     *
     * @param code     입력된 한글의 code
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
        if (AlphaIndex < 0) // white spaces...
        {
            KeyboardLogPrint.e("mKoreanMode :: " + mKoreanMode);
            if (mKoreanMode) {
                // flush Korean characters first.
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mState = 0;
                result = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }

            // process the code as English
            if ((KeyState & (InputTables.KEYSTATE_ALT_MASK | InputTables.KEYSTATE_CTRL_MASK | InputTables.KEYSTATE_FN)) == 0) {
                // result |= expression  => result = result | expression (비트 논리합 할당 연산자)
                // 두 식의 특정 자릿수 중 하나가 1이면 해당 자릿수의 결과 값은 1이 되고, 그렇지 않으면 해당 자릿수의 결과 값은 0이 됩니다.
                result |= ACTION_USE_INPUT_AS_RESULT;
            }
        } else if (!mKoreanMode) {
            KeyboardLogPrint.e("no korean mode");
            // process the code as English
            result = ACTION_USE_INPUT_AS_RESULT;
        } else {
            KeyboardLogPrint.e("else");
            if ((KeyState & InputTables.KEYSTATE_SHIFT_MASK) == 0) {
                KeyboardLogPrint.e("not shift");
                // 쿼티 한글의 shift 눌렀을 경우 해당 키 값에 해당하는 utf-8(hex) 값을 return 한다.
                hcode = InputTables.NormalKeyMap.Code[AlphaIndex];
            } else {
                KeyboardLogPrint.e("shift");
                // 쿼티 한글일 경우 해당 키 값에 해당하는 utf-8(hex) 값을 return 한다.
                hcode = InputTables.ShiftedKeyMap.Code[AlphaIndex];
            }

            if (AlphaIndex == 25)   // . 일경우 해당 키 값으로 치환
            {
                hcode = InputTables.DotCode;
            }
            KeyboardLogPrint.e("DoAutomata mState :: " + mState + " / hcode :: "+hcode);
            switch (mState) {
                case 0:
                    result = DoState00(hcode);
                    break; // current composition string: NULL
                case 1:
                    result = DoState01(hcode);
                    break; // current composition string: single consonant only
                case 2:
                    result = DoState02(hcode);
                    break; // current composition string: single consonant + single vowel
                case 3:
                    result = DoState03(hcode);
                    break; // current composition string: single consonant + single vowel + single consonant
                case 4:
                    result = DoState04(hcode);
                    break; // current composition string: single vowel
                case 5:
                    result = DoState05(hcode);
                    break; // current composition string: a combined vowel
                case 10:
                    result = DoState10(hcode);
                    break; // current composition string: a combined consonant
                case 11:
                    result = DoState11(hcode);
                    break; // current composition string: single consonant + single vowel + a combined consonant
                case 20:
                    result = DoState20(hcode);
                    break; // current composition string: single consonant + a combined vowel
                case 21:
                    result = DoState21(hcode);
                    break; // current composition string: single consonant + a combined vowel + single consonant
                case 22:
                    result = DoState22(hcode);
                    break; // current composition string: single consonant + a combined vowel + a combined consonant
                default:
                    result = ACTION_ERROR; // error. should not be here in any circumstance.
            }
        }

        return result;
    }

    /**
     * 현재 구성된 문자열 null 일 경우 호출 됨
     *
     * @param code
     * @return
     */
    private int DoState00(char code) // current composition string: NULL
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*******************00******************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState00 :: " + code);
        KeyboardLogPrint.e("DoStateOO CODE :: " + code);
        if (IsConsonant(code)) // 입력된 코드값이 자음이면
        {
            mState = 1; // '현재 구성된 문자열이 단자음' 상태로 바꿈
        } else {
            mState = 4; // '현재 구성된 문자열이 단모음' 상태로 바꿈
        }
        KeyboardLogPrint.e("DoState00 mState :: " + mState);
        mCompleteString = "";
        mCompositionString = "";
        mCompositionString += code; // 입력창의 문자열을 입력된 코드로 바꿈
        return ACTION_UPDATE_COMPOSITIONSTR | ACTION_APPEND;
    };

    /**
     * 현재 구성된 문자열이 단자음일 경우 호출 됨
     *
     * @param code
     * @return
     */
    private int DoState01(char code) {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*******************01******************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.i("DoState01 :: " + code);
        KeyboardLogPrint.i("mCompleteString :: " + mCompleteString);
        KeyboardLogPrint.i("mCompositionString :: " + mCompositionString);

        if (mCompositionString == "") // DoState01은 현재 상태가 1 일 경우 즉 입력창에 단자음이 존재할 경우에만 호출되어야 하는데 mCompositionString이 빈값이면 에러임
        {
            return ACTION_ERROR;
        }

        int ret = ACTION_NONE;
        if (IsConsonant(code)) // 입력 코드가 자음일 경우
        {
            CharModel model = ChunjiinCombineLastConsonantWithCode(mCompositionString.charAt(0), code); // 현재 입력창에 있는 자음과 넘어온 code(자음)의 조합자음, 갈ㄷ + ㄷ 일 경우 rotateCode ㅌ를 가져온다.
            char newCode = model.getChar(); // 0
            char rotatedCode = model.getRotatedChar(); // ㄷ
            KeyboardLogPrint.i("chunjiin newCode :: " + newCode);
            KeyboardLogPrint.i("chunjiin rotatedCode :: " + rotatedCode);
            if (newCode == (char) 0) // 조합자음 값이 없다면 (ex : 기존의 값이 ㅂ 이고 새로 들어온 값이 ㅈ 이라면 이 두 값으로 조합자음이 만들어질 수 없는 경우
            {
                if (rotatedCode == (char) 0) // 순환값이 아닐 경우
                {
                    KeyboardLogPrint.i("rotatedCode == 0 ");
                    KeyboardLogPrint.w("DoState01 :: 1");
                    mCompleteString = mCompositionString; // 현재 입력창의 값을 우선 mCompleteString에 담아놓음
                    mCompositionString = "";
                    mCompositionString += code;
                    // 상태 1로 바꾸는 이유는 조합자음이 만들어지지 않았기 때문에 이후 들어오는 값들은 마지막에 들어온 ㅈ 하나만 있는 상태로 바꾸어야 함
                    // ex. ㅂㅈ 이후 'ㅏ'가 들어올 경우 상태가 단자음만 있을 경우여야 ㅂ자 와 같이 만들어지기 때문
                    KeyboardLogPrint.i("DoState01 mCompositionString :: " + mCompositionString);
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState01 1 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
                else // 순환값일 경우
                {
                    KeyboardLogPrint.i("rotatedCode != 0 ");

                    if ("".equals(mCompleteString)) {
                        if (!"".equals(mPrefComplete)) {
                            int completeLastConsonant = GetLastConsonantIndex(mPrefComplete.charAt(0));
                            char lastConsonant = GetLastConsonant(completeLastConsonant);
                            CharModel combinedLastConsonanat = ChunjiinCombineLastConsonantWithCode(lastConsonant, rotatedCode);
                            char cConsonant = combinedLastConsonanat.getChar();
                            KeyboardLogPrint.i("cConsonant : " + cConsonant);
                            if (cConsonant != (char) 0) {
                                KeyboardLogPrint.w("DoState01 :: 2");
                                int lastConsonantIndex = GetLastConsonantIndex(cConsonant);
                                char newChar = (char) ((int) mPrefComplete.charAt(0) - completeLastConsonant + lastConsonantIndex);

                                KeyboardLogPrint.i("newChar1 : " + newChar);
                                mPrefComplete = "";
                                mCompleteString = "";
                                mCompositionString = "";
                                mCompositionString += newChar;
                                mState = 11;
                                KeyboardLogPrint.w("kims DoState01 2 mCompleteString :: " + mCompleteString);
                                ret = ACTION_REMAKECHAR;
                            } else {
                                KeyboardLogPrint.w("DoState01 :: 7");
                                mPrefComplete = "";
                                mCompleteString = "";
                                mCompositionString = "";
                                mCompositionString += rotatedCode;
                                mState = 1;
                                KeyboardLogPrint.w("kims DoState01 3 mCompleteString :: " + mCompleteString);
                                ret = ACTION_UPDATE_COMPOSITIONSTR;
                            }
                        } else {
                            KeyboardLogPrint.w("DoState01 :: 5");
                            mCompleteString = "";
                            mCompositionString = "";
                            mCompositionString += rotatedCode;
                            mState = 1;
                            KeyboardLogPrint.w("kims DoState01 4 mCompleteString :: " + mCompleteString);
                            ret = ACTION_UPDATE_COMPOSITIONSTR;
                        }
                    } else {
                        int completeLastConsonant = GetLastConsonantIndex(mCompleteString.charAt(0));
                        char lastConsonant = GetLastConsonant(completeLastConsonant);
                        KeyboardLogPrint.i("completeLastConsonant : " + completeLastConsonant);
                        CharModel combinedLastConsonanat = ChunjiinCombineLastConsonantWithCode(lastConsonant, rotatedCode);
                        char cConsonant = combinedLastConsonanat.getChar();
                        KeyboardLogPrint.i("cConsonant : " + cConsonant);
                        if (cConsonant != (char) 0) {
                            KeyboardLogPrint.w("DoState01 :: 6");
                            int lastConsonantIndex = GetLastConsonantIndex(cConsonant);
                            char newChar = (char) ((int) mCompleteString.charAt(0) - completeLastConsonant + lastConsonantIndex);

                            KeyboardLogPrint.i("newChar1 : " + newChar);
                            mCompleteString = "";
                            mCompositionString = "";
                            mCompositionString += newChar;
                            mState = 11;
                            KeyboardLogPrint.w("kims DoState01 5 mCompleteString :: " + mCompleteString);
                            ret = ACTION_REMAKECHAR;
                        } else {
                            if (canUseLastConsonant(mCompositionString.charAt(0))) // ㄸ, ㅉ, ㅃ 등이 compositionString 값으로 들어왔고 입력값과 이 값들이 순환값일 경우 즉 ㄸ이 compositionString이고 code가 ㄷ일 경우
                            {
                                KeyboardLogPrint.w("DoState01 :: 3");
                                mPrefComplete = mCompleteString;
                                mCompleteString = "";
                                mCompositionString = "";
                                mCompositionString += rotatedCode;
                                mState = 1;
                                KeyboardLogPrint.w("kims DoState01 6 mCompleteString :: " + mCompleteString);
                                ret = ACTION_UPDATE_COMPOSITIONSTR;
                            } else {
                                int rotatedIndex = GetLastConsonantIndex(rotatedCode);
                                char ncode = (char) ((int) mCompleteString.charAt(0) + rotatedIndex);

                                char compChar = mCompleteString.charAt(0);
                                int lastCChar = GetLastConsonantIndex(compChar);
                                if (lastCChar > 0) {
                                    int combinedIndex = CombineLastConsonantWithIndex(lastCChar, rotatedIndex);

                                    if (combinedIndex != -1) // 조합자음이 있을 경우
                                    {
                                        KeyboardLogPrint.w("DoState01 :: 4");
                                        char newChar = (char) ((int) mCompleteString.charAt(0) - lastCChar + combinedIndex);
                                        mPrefComplete = "";
                                        mCompleteString = "";
                                        mCompositionString = "";
                                        mCompositionString += newChar;
                                        mState = 11;
                                        KeyboardLogPrint.w("kims DoState01 7 mCompleteString :: " + mCompleteString);
                                        ret = ACTION_REMAKECHAR;
                                    } else {
                                        KeyboardLogPrint.e("DoState01 :: 15");
                                        mPrefComplete = mCompleteString;
                                        mCompleteString = "";
                                        mCompositionString = "";
                                        mCompositionString += rotatedCode;
                                        mState = 1;
                                        KeyboardLogPrint.w("kims DoState01 8 mCompleteString :: " + mCompleteString);
                                        ret = ACTION_UPDATE_COMPOSITIONSTR;
                                    }
                                } else {
                                    KeyboardLogPrint.w("DoState01 :: 16");
                                    mCompleteString = "";
                                    mCompositionString = "";
                                    mCompositionString += ncode;
                                    mState = 3; // '현재 구성된 문자열이 단자음 + 단모음 + 단자음' 상태로 바꿈
                                    KeyboardLogPrint.w("kims DoState01 9 mCompleteString :: " + mCompleteString);
                                    ret = ACTION_REMAKECHAR;
                                }
                            }
                        }
                    }
                }
            } else // 조합자음이 되었다면
            {
                KeyboardLogPrint.w("DoState01 :: 8");
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mCompositionString += code;
                mState = 1;
                KeyboardLogPrint.w("kims DoState01 10 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }
        } else // 입력된 값이 자음이 아닐 경우
        {
            KeyboardLogPrint.i("입력된 값이 자음이 아닐 경우");
            int fcIndex = ConvertFirstConsonantCodeToIndex(mCompositionString.charAt(0));

            if (code == InputTables.DotCode) // 입력코드가 dot 일 경우
            {
                KeyboardLogPrint.i("mCompleteString :: " + mCompleteString);
                KeyboardLogPrint.i("mCompositionString :: " + mCompositionString);
                KeyboardLogPrint.i("mCompositionString.length() : " + mCompositionString.length());
                if (mCompositionString.charAt(mCompositionString.length() - 1) == InputTables.DotCode) // 문자열 마지막이 dot 일 경우
                {
                    KeyboardLogPrint.w("DoState01 :: 9");
                    KeyboardLogPrint.i("마지막 문자열이 dot 일경우");
                    mConsonantBeforeDoubleDot = mCompleteString;
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    code = InputTables.DoubleDotCode;
                    mCompositionString += code;
                    KeyboardLogPrint.i("mCompositionString :: " + mCompositionString);
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState01 11 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                }
                else if (mCompositionString.charAt(mCompositionString.length() - 1) == InputTables.DoubleDotCode) // 문자열 마지막이 double dot 일 경우
                {
                    KeyboardLogPrint.w("DoState01 :: 10");
                    mCompleteString = mConsonantBeforeDoubleDot;
                    mConsonantBeforeDoubleDot = "";
                    mCompositionString = "";
                    code = InputTables.DotCode;
                    mCompositionString += code;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState01 12 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else {
                    KeyboardLogPrint.w("DoState01 :: 11");
                    KeyboardLogPrint.i("마지막 문자열이 dot 이 아닐경우");
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState01 13 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            } else {
                KeyboardLogPrint.i("mCompleteString : " + mCompleteString.toString());
                int length = mCompleteString.length();
                if (mCompleteString.length() == 0 && mCompositionString.charAt(0) != InputTables.DoubleDotCode) {
                    KeyboardLogPrint.w("DoState01 :: 12");
                    int vIndex = ConvertVowelCodeToIndex(code);
                    char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                    KeyboardLogPrint.i("fcIndex :: " + fcIndex);
                    KeyboardLogPrint.i("vIndex :: " + vIndex);
                    KeyboardLogPrint.i("newCode :: " + newCode);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += newCode;
                    mState = 2;
                    KeyboardLogPrint.w("kims DoState01 14 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else {
                    char lastChar = mCompositionString.charAt(length - 1);
                    KeyboardLogPrint.i("length :: " + length);
                    KeyboardLogPrint.i("lastChar : " + lastChar);

                    if (lastChar == InputTables.DotCode) {
                        if (hasCombinedLastConsonant(mCompleteString.charAt(0))) // mCompleteString의 종성자음이 조합자음일 경우 '갃'
                        {
                            KeyboardLogPrint.w("DoState01 :: 13");
                            KeyboardLogPrint.e("mCompleteString 의 종성자음이 조합자음");
                            setStrLength((length + 1));
                            KeyboardLogPrint.i("lastChar dot");
                            if (code == 0x3163) // ㅣ
                            {
                                code = 0x3153;
                            } else if (code == 0x3161)  // ㅡ
                            {
                                code = 0x3157;
                            }

                            int completeLastConsonant = GetLastConsonantIndex(mCompleteString.charAt(0)); // 갃
                            int iFirst = InputTables.LastConsonants.iLast[completeLastConsonant]; // '갃'의 종성모음의 첫글자 ㄱ 의 인덱스
                            int iLast = InputTables.LastConsonants.iFirst[completeLastConsonant]; // '갃'의 종성모음의 마지막글자 ㅅ 의 인덱스
                            KeyboardLogPrint.i("iFirst :: " + iFirst);
                            KeyboardLogPrint.i("iLast :: " + iLast);
                            int vIndex = ConvertVowelCodeToIndex(code);

                            char newChar = (char) ((int) mCompleteString.charAt(0) - completeLastConsonant + iFirst); // 갃 에서 ㄳ을 뺀 가 에서 ㄱ을 더해 각
                            char newCode = ComposeCharWithIndexs(iLast, vIndex, 0);
                            KeyboardLogPrint.i("newChar :: " + newChar);
                            KeyboardLogPrint.i("newCode :: " + newCode);
                            mCompleteString = "";
                            mCompositionString = "";
                            mCompositionString += newChar;
                            mCompositionString += newCode;
                            mState = 2;
                            KeyboardLogPrint.w("kims DoState01 15 mCompleteString :: " + mCompleteString);
                            KeyboardLogPrint.e("DoState01에서 ACTION_MAKE_VOWEL return 1");
                            ret = ACTION_MAKE_VOWEL;
                        }
                        else // mCompleteString 값이 ㄱ 과 같이 단자음 형태일 경우
                        {
                            KeyboardLogPrint.w("DoState01 :: 14");
                            KeyboardLogPrint.e("mCompleteString 의 종성자음이 조합자음이 아님");
                            setStrLength((length + 1));
                            KeyboardLogPrint.i("lastChar dot");
                            if (code == 0x3163) // ㅣ
                            {
                                code = 0x3153;
                            } else if (code == 0x3161)  // ㅡ
                            {
                                code = 0x3157;
                            }
                            fcIndex = ConvertFirstConsonantCodeToIndex(mCompleteString.charAt(0));
                            int vIndex = ConvertVowelCodeToIndex(code);
                            char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                            KeyboardLogPrint.i("fcIndex :: " + fcIndex);
                            KeyboardLogPrint.i("vIndex :: " + vIndex);
                            KeyboardLogPrint.i("newCode :: " + newCode);
                            mCompleteString = "";
                            mCompositionString = "";
                            mCompositionString += newCode;
                            mState = 2;
                            KeyboardLogPrint.e("DoState01에서 ACTION_MAKE_VOWEL return");
                            KeyboardLogPrint.w("kims DoState01 16 mCompleteString :: " + mCompleteString);
                            ret = ACTION_MAKE_VOWEL;
                        }
                    } else if (lastChar == InputTables.DoubleDotCode) {
                        /**
                         * 2017.12.19 기존 로직(아래 주석)은 ㅂ.. + ㅣ 일 경우 '벼'가 만들어지며 정상적으로 동작하지만
                         * 홟.. + ㅣ일 경우 활벼 가 아닌 글자가 전부 삭제되는 형태로 나타나 이를 수정함
                         */
                        if ( mConsonantBeforeDoubleDot != null && !TextUtils.isEmpty(mConsonantBeforeDoubleDot) && hasCombinedLastConsonant(mConsonantBeforeDoubleDot.charAt(0))) {
                            KeyboardLogPrint.e("DoState01 15 new");
                            KeyboardLogPrint.w("DoState01 :: 15");
                            setStrLength((length + 1));
                            KeyboardLogPrint.i("lastChar double dot");
                            if (code == 0x3163) // ㅣ
                            {
                                code = 0x3155; // ㅕ
                            } else if (code == 0x3161)  // ㅡ
                            {
                                code = 0x315B; // ㅛ
                            }
                            int completeLastConsonant = GetLastConsonantIndex(mConsonantBeforeDoubleDot.charAt(0)); // 홟
                            int iFirst = InputTables.LastConsonants.iLast[completeLastConsonant]; // '홟'의 종성모음의 첫글자 ㄹ 의 인덱스
                            int iLast = InputTables.LastConsonants.iFirst[completeLastConsonant]; // '홟'의 종성모음의 마지막글자 ㅂ 의 인덱스
                            int vIndex = ConvertVowelCodeToIndex(code);

                            char newChar = (char) ((int) mConsonantBeforeDoubleDot.charAt(0) - completeLastConsonant + iFirst); // 홟 에서 ㄼ을 뺀 화 에서 ㄹ을 더해 활
                            char newCode = ComposeCharWithIndexs(iLast, vIndex, 0);
                            KeyboardLogPrint.i("newChar :: " + newChar);
                            KeyboardLogPrint.i("newCode :: " + newCode);
                            mCompleteString = "";
                            mCompositionString = "";
                            mCompositionString += newChar;
                            mCompositionString += newCode;
                            mState = 2;
                            ret = ACTION_MAKE_VOWEL;
                        } else {
                            // ㅂ.. + ㅣ가 들어와 벼가 될 경우
                            KeyboardLogPrint.w("DoState01 :: 15");
                            setStrLength((length + 1));
                            KeyboardLogPrint.i("lastChar double dot");
                            if (code == 0x3163) // ㅣ
                            {
                                code = 0x3155; // ㅕ
                            } else if (code == 0x3161)  // ㅡ
                            {
                                code = 0x315B; // ㅛ
                            }
                            KeyboardLogPrint.i("mConsonantBeforeDoubleDot :: " + mConsonantBeforeDoubleDot);


                            fcIndex = ConvertFirstConsonantCodeToIndex(mConsonantBeforeDoubleDot.charAt(0));
                            int vIndex = ConvertVowelCodeToIndex(code);
                            char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                            KeyboardLogPrint.i("fcIndex :: " + fcIndex);
                            KeyboardLogPrint.i("vIndex :: " + vIndex);
                            KeyboardLogPrint.i("newCode :: " + newCode);
                            mCompleteString = "";
                            mCompositionString = "";
                            mCompositionString += newCode;
                            mState = 2;
                            KeyboardLogPrint.i("DoState01에서 ACTION_MAKE_VOWEL return1");
                            KeyboardLogPrint.w("kims DoState01 17 mCompleteString :: " + mCompleteString);
                            KeyboardLogPrint.w("kims DoState01 17 mCompositionString :: " + mCompositionString);
                            ret = ACTION_MAKE_VOWEL;
                        }

//                        KeyboardLogPrint.w("DoState01 :: 15");
//                        setStrLength((length + 1));
//                        KeyboardLogPrint.i("lastChar double dot");
//                        if (code == 0x3163) // ㅣ
//                        {
//                            code = 0x3155; // ㅕ
//                        } else if (code == 0x3161)  // ㅡ
//                        {
//                            code = 0x315B; // ㅛ
//                        }
//                        KeyboardLogPrint.i("mConsonantBeforeDoubleDot :: " + mConsonantBeforeDoubleDot); //홟
//
//
//                        fcIndex = ConvertFirstConsonantCodeToIndex(mConsonantBeforeDoubleDot.charAt(0)); // 홟
//                        int vIndex = ConvertVowelCodeToIndex(code);
//                        char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
//                        KeyboardLogPrint.i("fcIndex :: " + fcIndex);
//                        KeyboardLogPrint.i("vIndex :: " + vIndex);
//                        KeyboardLogPrint.i("newCode :: " + newCode);
//                        mCompleteString = "";
//                        mCompositionString = "";
//                        mCompositionString += newCode;
//                        mState = 2;
//                        KeyboardLogPrint.i("DoState01에서 ACTION_MAKE_VOWEL return1");
//                        KeyboardLogPrint.w("kims DoState01 17 mCompleteString :: " + mCompleteString);
//                        KeyboardLogPrint.w("kims DoState01 17 mCompositionString :: " + mCompositionString);
//                        ret = ACTION_MAKE_VOWEL;
                    } else {
                        KeyboardLogPrint.w("DoState01 :: 16");
                        KeyboardLogPrint.i("lastChar not dot");
                        int vIndex = ConvertVowelCodeToIndex(code);
                        char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                        KeyboardLogPrint.i("fcIndex :: " + fcIndex);
                        KeyboardLogPrint.i("vIndex :: " + vIndex);
                        KeyboardLogPrint.i("newCode :: " + newCode);
                        mCompleteString = "";
                        mCompositionString = "";
                        mCompositionString += newCode;
                        mState = 2;
                        KeyboardLogPrint.w("kims DoState01 18 mCompleteString :: " + mCompleteString);
                        ret = ACTION_UPDATE_COMPOSITIONSTR;
                    }
                }
            }
        }
        return ret;
    }

    ;

    /**
     * 현재 구성된 문자열이 단자음 + 단모음 일 경우 호출됨
     *
     * @param code
     * @return
     */
    private int DoState02(char code) {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*******************02******************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState02 :: " + code);
        KeyboardLogPrint.w("mCompleteString :: " + mCompleteString);
        KeyboardLogPrint.w("mCompositionString :: " + mCompositionString);
        if (mCompositionString == "") {
            return ACTION_ERROR;
        }

        int ret = ACTION_NONE;

        if (mCompositionString.length() == 2)
            mCompositionString = mCompositionString.substring(1, 2); // 갃 . ㅣ -> 각서 가 되어 mCompositionString이 각서 가 담겨 있을 경우 끝 글자인 '서' 만 취급함

        if (IsConsonant(code)) // 입력된 코드값이 자음일 경우
        {
            int lcIndex = GetLastConsonantIndex(code); // 입력된 코드값의 종성자음 index를 가져옴
            if (lcIndex != -1) // 입력된 코드값이 종성자음일 경우
            {
                // 기존 입력창에 '가' 가 입력되어 있고 새로 들어온 코드 값이 'ㄱ' 일 경우
                KeyboardLogPrint.e("mCompositionString.charAt(0) :: " + mCompositionString.charAt(0)); // '가'
                char newCode = (char) ((int) mCompositionString.charAt(0) + lcIndex);
                KeyboardLogPrint.e("newCode :: " + newCode); // 각
                mCompleteString = "";
                mCompositionString = "";
                mCompositionString += newCode; // 입력창의 값을 '각'으로 바꿔줌
                mState = 3; // '현재 구성된 문자열이 단자음 + 단모음 + 단자음' 상태로 바꿈
//                mStateChangeTime1 = System.currentTimeMillis();
                mLastConsonantChar1 = code;
                KeyboardLogPrint.w("kims DoState02 1 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPOSITIONSTR;
            } else // 입력된 코드값이 종성자음이 아닐 경우 ex. 기존 입력창의 값이 '가' 이고 새로 들어온 코드 값이 'ㅃ'(종성자음에 없는 자음) 일 경우
            {
                KeyboardLogPrint.e("DoState02 신규값이 종성자음이 아님");
                mCompleteString = mCompositionString; // 기존 입력창의 값 '가'를 mCompleteString에 담아놓음
                mCompositionString = "";
                mCompositionString += code;
                mState = 1; // '현재 구성된 문자열이 단자음 일 경우' 상태로 변경
                KeyboardLogPrint.w("kims DoState02 2 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }
        } else // 입력된 코드값이 모음일 경우
        {
            KeyboardLogPrint.e("DoState02 입력코드값이 자음 아님");
            char vCode = GetVowel(mCompositionString.charAt(0)); // 현재 입력창의 중성모음을 가져옴 ex. 현재 입력창의 값이 '고' 일 경우 'ㅗ'
            VowelCharModel model = ChunjiinCombineVowelWithCode(vCode, code); // 현재 입력창의 중성모음과 새로 들어온 모음의 조합모음 ex. 현재 입력창의 값 '고'의 중성모음 'ㅗ'와 새로 들어온 모음 'ㅏ'의 조합모음 'ㅘ'
            char newCode = model.getVowelChar();
            boolean isCombinedVowel = model.getCombinedState();
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
                if (code == InputTables.DotCode || !isCombinedVowel) // 기존 '그'에서 새로 입력값이 . 이 들어오면 '구' 로 바꿔주고 상태도 단자음 + 단모음 상태로 바꿔준다.
                    mState = 2;
                else
                    mState = 20;
                KeyboardLogPrint.w("kims DoState02 3 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPOSITIONSTR;
            }
            else // 입력창 값의 모음과 새로 들어온 모음이 조합모음이 아닌경우 ex. 입력창의 값 '고'의 모음 'ㅗ'와 새로 들어온 모음 'ㅔ'일 경우 조합모음이 안된다.
            {
                mCompleteString = mCompositionString; // 기존 입력창의 값 '고'를 mCompleteString에 담아놓음
                mCompositionString = "";
                mCompositionString += code;
                mState = 4; // '현재 구성된 문자열이 단모음' 상태값으로 변경
                KeyboardLogPrint.w("kims DoState02 4 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }
        }
        return ret;
    }

    ;

    /**
     * 현재 구성된 문자열이 단자음 + 단모음 + 단자음 일 경우 호출됨
     *
     * @param code
     * @return
     */
    private int DoState03(char code) {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*******************03******************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.i("DoState03 :: " + code);

        if (mCompositionString == "") {
            return ACTION_ERROR;
        }
        KeyboardLogPrint.w("mCompositionString : " + mCompositionString);
        KeyboardLogPrint.w("mCompleteString : " + mCompleteString);
        int ret = ACTION_NONE;
        if (IsConsonant(code)) {
            KeyboardLogPrint.i("DoState03 mCompositionString.charAt(0) : " + mCompositionString.charAt(0));

            int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
            KeyboardLogPrint.i("DoState03 lcIndex : " + lcIndex);
            if (lcIndex < 0) {
                return ACTION_ERROR;
            }

//            long curTime = System.currentTimeMillis();
//            char newCode = CombineLastConsonantWithCode(InputTables.LastConsonants.Code[lcIndex], code);
            CharModel model = ChunjiinCombineLastConsonantWithCode(InputTables.LastConsonants.Code[lcIndex], code);

            char newCode = model.getChar();
            char rotatedChar = model.getRotatedChar();
            int mode = model.getMode();
            KeyboardLogPrint.i("DoState03 newCode : " + String.valueOf(newCode));
            KeyboardLogPrint.i("DoState03 rotatedChar : " + String.valueOf(rotatedChar));
            KeyboardLogPrint.i("DoState03 mode : " + mode);
            if (newCode != (char) 0) // Last Consonants can be combined
            {
                KeyboardLogPrint.i("DoState03 1");
                char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex + GetLastConsonantIndex(newCode));
                mCompleteString = "";
                mCompositionString = "";
                mCompositionString += newChar;
                mState = 11;
                KeyboardLogPrint.w("kims DoState03 1 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPOSITIONSTR;
            } else {
                if (rotatedChar != (char) 0) // 순환 값일 경우 종성자음을 순환된 값으로 변경
                {
                    KeyboardLogPrint.e("DoState03 2");

                    if (canUseLastConsonant(rotatedChar)) {
                        char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex + GetLastConsonantIndex(rotatedChar));
                        mCompleteString = "";
                        mCompositionString = "";
                        mCompositionString += newChar;
                        mState = 3;
                        KeyboardLogPrint.w("kims DoState03 2 mCompleteString :: " + mCompleteString);
                        ret = ACTION_UPDATE_COMPOSITIONSTR;

//                        mStateChangeTime1 = curTime;
                    } else // '같' 다음 ㄷ이 올 경우 ㄸ은 종성자음으로 쓸 수 없음
                    {
                        int lastConsonant = GetLastConsonantIndex(mCompositionString.charAt(0));
                        char newChar = (char) ((int) mCompositionString.charAt(0) - lastConsonant);
                        mCompleteString = "";
                        mCompositionString = "";
                        mCompleteString += newChar;
                        mCompositionString += rotatedChar;
                        mState = 1;
                        KeyboardLogPrint.w("kims DoState03 3 mCompleteString :: " + mCompleteString);
                        ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                    }
//                    if ( (curTime - mStateChangeTime1) > 500 )
//                    {
//                        KeyboardLogPrint.i("DoState03 4");
//                        if ( GetLastConsonantIndex(rotatedChar) == -1 )
//                        {
//                            char compchar = (char) ((int) mCompositionString.charAt(0) - lcIndex);
//                            mCompleteString += compchar;
//                            mCompositionString = "";
//                            mCompositionString += rotatedChar;
//                            mState = 1;
//                            ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
//
//                            mStateChangeTime1 = 0;
//                        }
//                        else
//                        {
//                            char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex + GetLastConsonantIndex(rotatedChar));
//                            mCompleteString = "";
//                            mCompositionString = "";
//                            mCompositionString += newChar;
//                            mState = 3;
//                            ret = ACTION_UPDATE_COMPOSITIONSTR;
//
//                            mStateChangeTime1 = 0;
//                        }
//                    }
//                    else
//                    {
//                        KeyboardLogPrint.e("DoState03 2");
//
//                        if ( canUseLastConsonant(rotatedChar) )
//                        {
//                            char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex + GetLastConsonantIndex(rotatedChar));
//                            mCompleteString = "";
//                            mCompositionString = "";
//                            mCompositionString += newChar;
//                            mState = 3;
//                            ret = ACTION_UPDATE_COMPOSITIONSTR;
//
//                            mStateChangeTime1 = curTime;
//                        }
//                        else // '같' 다음 ㄷ이 올 경우 ㄸ은 종성자음으로 쓸 수 없음
//                        {
//                            int lastConsonant = GetLastConsonantIndex(mCompositionString.charAt(0));
//                            char newChar = (char) ((int) mCompositionString.charAt(0) - lastConsonant);
//                            mCompleteString = "";
//                            mCompositionString = "";
//                            mCompleteString += newChar;
//                            mCompositionString += rotatedChar;
//                            mState = 1;
//                            ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
//                        }
//                    }
                } else //순환값이 아닐 경우 기존 automata와 동일
                {
                    KeyboardLogPrint.i("DoState03 3");
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState03 4 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            }
        } else // vowel
        {
            if (code == InputTables.DotCode) {
                KeyboardLogPrint.i(" DoState03 mCompositionString :: " + mCompositionString);
                mPrefCharbeforeDot = mCompositionString;
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mCompositionString += code;
                KeyboardLogPrint.e(" DoState03 mCompositionString :: " + mCompositionString);
                mState = 4;
                KeyboardLogPrint.w("kims DoState03 5 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            } else {
                int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
                if (lcIndex < 0) {
                    return ACTION_ERROR;
                }
                KeyboardLogPrint.i("lcIndex : " + lcIndex);

                char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex); // remove last consonant and flush it.
                KeyboardLogPrint.i("newChar : " + newChar);
                mCompleteString = "";
                mCompleteString += newChar;
                int fcIndex = GetFirstConsonantIndex(InputTables.LastConsonants.Code[lcIndex]);
                if (fcIndex < 0) {
                    return ACTION_ERROR;
                }
                int vIndex = GetVowelIndex(code);
                char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0); // compose new composition string
                KeyboardLogPrint.i("newCode : " + newCode);
                mCompositionString = "";
                mCompositionString += newCode;
                KeyboardLogPrint.i("mCompositionString : " + mCompositionString);
                mState = 2;
                KeyboardLogPrint.w("kims DoState03 6 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }

        }
        return ret;
    }

    ;

    private int DoState04(char code) // current composition string: single vowel
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("********************04*****************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState04 :: " + code);

        if (mCompositionString == "") {
            return ACTION_ERROR;
        }
        KeyboardLogPrint.w("DoState04 mCompositionString : " + mCompositionString);
        KeyboardLogPrint.w("DoState04 mCompleteString : " + mCompleteString);
        int ret = ACTION_NONE;
        if (IsConsonant(code)) {
            mCompleteString = mCompositionString;
            mCompositionString = "";
            mCompositionString += code;
            mState = 1;
            KeyboardLogPrint.w("kims DoState04 1 mCompleteString :: " + mCompleteString);
            ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
        } else {
            KeyboardLogPrint.e("DoState04 mCompositionString : " + mCompositionString);
            KeyboardLogPrint.e("DoState04 mCompositionString.charAt(0) : " + mCompositionString.charAt(0));
            KeyboardLogPrint.e("DoState04 code : " + code);
            VowelCharModel model = ChunjiinCombineVowelWithCode(mCompositionString.charAt(0), code);
            char newCode = model.getVowelChar();
            boolean combinedVowelState = model.getCombinedState();
            KeyboardLogPrint.e("DoState04 newCode : " + newCode);

            if (mCompositionString.charAt(0) == InputTables.DotCode) {
                if (code == InputTables.DotCode) {
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += InputTables.DoubleDotCode;
                    mState = 4;
                    KeyboardLogPrint.w("kims DoState04 2 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else {
                    if (code == 0x3163) // ㅣ
                    {
                        newCode = 0x3153; // ㅓ
                    } else if (code == 0x3161)  // ㅡ
                    {
                        newCode = 0x3157; // ㅗ
                    }

                    /**
                     * . 다음 ㅣ 또는 ㅡ 가 올 경우 StringIndexOutOfBoundsException 발생
                     */

                    if ("".equals(mCompleteString)) {
                        KeyboardLogPrint.e("mCompleteString is empty :: " + newCode);
                        mCompleteString = "";
                        mCompositionString = "";
                        mCompositionString += newCode;
                        mState = 4;
                        KeyboardLogPrint.w("kims DoState04 3 mCompleteString :: " + mCompleteString);
                        ret = ACTION_UPDATE_COMPOSITIONSTR;
                    } else {
                        char compchar = mCompleteString.charAt(0); // null 인 경우
                        boolean hasCLastConsonant = hasCombinedLastConsonant(compchar);
                        if ("".equals(mPrefCharbeforeDot)) {
                            mCompleteString = "";
                            mCompositionString = "";
                            mCompositionString += newCode;
                            mState = 4;
                            KeyboardLogPrint.w("kims DoState04 4 mCompleteString :: " + mCompleteString);
                            ret = ACTION_UPDATE_COMPOSITIONSTR;
                        } else {
                            if (hasCLastConsonant) {
                                char prefVal = mPrefCharbeforeDot.charAt(0); //갍
                                int lcIndex = GetLastConsonantIndex(prefVal);
                                int iFirst = InputTables.LastConsonants.iFirst[lcIndex]; // '갍'의 종성모음의 마지막글자 ㅌ 의 초성 인덱스
                                int iLast = InputTables.LastConsonants.iLast[lcIndex];

                                char compChar = (char) ((int) prefVal - lcIndex + iLast); // 갍 - ㄾ + ㄹ -> 갈
                                int vIndex = GetVowelIndex(newCode);
                                char compoChar = ComposeCharWithIndexs(iFirst, vIndex, 0);
                                KeyboardLogPrint.e("prefVal :: " + prefVal);
                                KeyboardLogPrint.e("lcIndex :: " + lcIndex);
                                KeyboardLogPrint.e("iLast :: " + iLast);
                                KeyboardLogPrint.e("vIndex :: " + vIndex);
                                KeyboardLogPrint.e("compChar :: " + compChar);
                                KeyboardLogPrint.e("compoChar :: " + compoChar);

                                mCompleteString = "";
                                mCompositionString = "";
                                mCompleteString += compChar;
                                mCompositionString += compoChar;
                                mPrefCharbeforeDot = "";
                                KeyboardLogPrint.w("kims DoState04 5 mCompleteString :: " + mCompleteString);
                                mState = 2;
                                ret = ACTION_REMAKECHAR;
                            } else {
                                char prefVal = mPrefCharbeforeDot.charAt(0);
                                int lcIndex = GetLastConsonantIndex(prefVal);
                                char lcChar = GetLastConsonant(lcIndex);
                                int vIndex = GetVowelIndex(newCode);
                                int fcIndex = GetFirstConsonantIndex(lcChar);
                                char newChar = (char) ((int) prefVal - lcIndex);
                                char newChar2 = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                                KeyboardLogPrint.e("prefVal :: " + prefVal);
                                KeyboardLogPrint.e("lcIndex :: " + lcIndex);
                                KeyboardLogPrint.e("vIndex :: " + vIndex);
                                KeyboardLogPrint.e("newChar :: " + newChar);
                                KeyboardLogPrint.e("newChar2 :: " + newChar2);

                                mCompleteString = "";
                                mCompositionString = "";
                                mCompleteString += newChar;
                                mCompositionString += newChar2;
                                mPrefCharbeforeDot = "";
                                KeyboardLogPrint.w("kims DoState04 6 mCompleteString :: " + mCompleteString);
                                mState = 2;
                                ret = ACTION_REMAKECHAR;
                            }
                        }
                    }
                }
            } else if (mCompositionString.charAt(0) == InputTables.DoubleDotCode) {
                if (code == InputTables.DotCode) {
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 4;
                    KeyboardLogPrint.w("kims DoState04 7 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else {
                    if (code == 0x3163) // ㅣ
                    {
                        newCode = 0x3155; // ㅕ
                    } else if (code == 0x3161)  // ㅡ
                    {
                        newCode = 0x315B; // ㅛ
                    }
                    KeyboardLogPrint.e("DoState04 mPrefCharbeforeDot :: " + mPrefCharbeforeDot);
                    if ("".equals(mPrefCharbeforeDot)) {
                        mCompleteString = "";
                        mCompositionString = "";
                        mCompositionString += newCode;
                        mState = 4;
                        KeyboardLogPrint.w("kims DoState04 8 mCompleteString :: " + mCompleteString);
                        ret = ACTION_UPDATE_COMPOSITIONSTR;
                    } else {
                        boolean hasCLastConsonant = hasCombinedLastConsonant(mPrefCharbeforeDot.charAt(0));

                        if (hasCLastConsonant) {
                            char prefVal = mPrefCharbeforeDot.charAt(0); //갍
                            int lcIndex = GetLastConsonantIndex(prefVal);
                            int iFirst = InputTables.LastConsonants.iFirst[lcIndex]; // '갍'의 종성모음의 마지막글자 ㅌ 의 초성 인덱스
                            int iLast = InputTables.LastConsonants.iLast[lcIndex]; // '갍'의 종성모음의 첫글자 ㄹ 의 종성 인덱스

                            char compChar = (char) ((int) prefVal - lcIndex + iLast); // 갍 - ㄾ + ㄹ -> 갈
                            int vIndex = GetVowelIndex(newCode);
                            char compoChar = ComposeCharWithIndexs(iFirst, vIndex, 0); // ㅌ +  ㅕ 또는 ㅛ -> 텨 또는 툐
                            KeyboardLogPrint.e("prefVal :: " + prefVal);
                            KeyboardLogPrint.e("lcIndex :: " + lcIndex);
                            KeyboardLogPrint.e("iLast :: " + iLast);
                            KeyboardLogPrint.e("vIndex :: " + vIndex);
                            KeyboardLogPrint.e("compChar :: " + compChar);
                            KeyboardLogPrint.e("compoChar :: " + compoChar);

                            mCompleteString = "";
                            mCompositionString = "";
                            mCompleteString += compChar;
                            mCompositionString += compoChar;
                            mPrefCharbeforeDot = "";
                            KeyboardLogPrint.w("kims DoState04 9 mCompleteString :: " + mCompleteString);
                            mState = 2;
                            ret = ACTION_REMOVE_PREV_CHAR | ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                        } else {
                            int lastConsonantIndex = GetLastConsonantIndex(mPrefCharbeforeDot.charAt(0));
                            char cLastConsonant = GetLastConsonant(lastConsonantIndex);
                            KeyboardLogPrint.e("DoState04 lastConsonantIndex :: " + lastConsonantIndex);
                            int firstConsonantIndex = GetFirstConsonantIndex(cLastConsonant);
                            int vowelIndex = GetVowelIndex(newCode);
                            if (lastConsonantIndex > 0) {
                                char compChar = (char) ((int) mPrefCharbeforeDot.charAt(0) - lastConsonantIndex);
                                char compoChar = ComposeCharWithIndexs(firstConsonantIndex, vowelIndex, 0);
                                mPrefCharbeforeDot = "";
                                mCompleteString = "";
                                mCompleteString += compChar;
                                mCompositionString = "";
                                mCompositionString += compoChar;
                                mState = 2;
                                KeyboardLogPrint.w("kims DoState04 10 mCompleteString :: " + mCompleteString);
                                ret = ACTION_REMOVE_PREV_CHAR | ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                            } else {
                                mCompleteString = "";
                                mCompositionString = "";
                                mCompositionString += newCode;
                                mState = 4;
                                KeyboardLogPrint.w("kims DoState04 11 mCompleteString :: " + mCompleteString);
                                ret = ACTION_UPDATE_COMPOSITIONSTR;
                            }
                        }
                    }
                }
            } else {
                if (newCode != (char) 0) {
                    if (combinedVowelState) {
                        KeyboardLogPrint.e("newCode :: not 0");
                        mCompleteString = "";
                        mCompositionString = "";
                        mCompositionString += newCode;
                        mState = 5;
                        KeyboardLogPrint.w("kims DoState04 12 mCompleteString :: " + mCompleteString);
                        ret = ACTION_UPDATE_COMPOSITIONSTR;
                    } else {
                        KeyboardLogPrint.e("newCode :: 0");
                        mCompleteString = "";
                        mCompositionString = "";
                        mCompositionString += newCode;
                        mState = 4;
                        KeyboardLogPrint.w("kims DoState04 13 mCompleteString :: " + mCompleteString);
                        ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                    }
                } else {
                    KeyboardLogPrint.w("*******************************");
                    KeyboardLogPrint.w("**********  ERROR  ************");
                    KeyboardLogPrint.w("*******************************");

                    KeyboardLogPrint.e("newCode :: 0");
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 4;
                    KeyboardLogPrint.w("kims DoState04 14 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }

            }
        }
        return ret;
    }

    ;

    private int DoState05(char code) // current composition string: a combined vowel
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*********************05****************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState05 :: " + code);
        KeyboardLogPrint.w("mCompleteString :: " + mCompleteString);
        KeyboardLogPrint.w("mCompositionString :: " + mCompositionString);
        if (mCompositionString == "") {
            return ACTION_ERROR;
        }

        int ret = ACTION_NONE;
        if (IsConsonant(code)) {
            mCompleteString = mCompositionString;
            mCompositionString = "";
            mCompositionString += code;
            mState = 1;
            KeyboardLogPrint.w("kims DoState05 1 mCompleteString :: " + mCompleteString);
            ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
        } else {
            if (code == InputTables.DotCode) {
                char compositionChar = mCompositionString.charAt(0);
                KeyboardLogPrint.e("compositionChar : " + compositionChar);
                char vowel = GetVowel(compositionChar);
                KeyboardLogPrint.e("vowel : " + vowel);
                if (vowel == 0x315A) { // ㅚ
                    vowel = 0x3158; // ㅘ
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += vowel;
                    mState = 5;
                    KeyboardLogPrint.w("kims DoState5 3 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                }
                else
                {
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 4;
                    KeyboardLogPrint.w("kims DoState05 2 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            }
            else if ( code == 0x3163 )
            {
                char compositionChar = mCompositionString.charAt(0);
                KeyboardLogPrint.e("compositionChar : " + compositionChar);
                char vowel = GetVowel(compositionChar);
                if ( vowel == 0x3158 ) // ㅘ
                {
                    vowel = 0x3159; // ㅙ
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += vowel;
                    mState = 5;
                    KeyboardLogPrint.w("kims DoState5 4 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                }
                else if ( vowel == 0x315D ) // ㅝ
                {
                    vowel = 0x315E; // ㅞ
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += vowel;
                    mState = 5;
                    KeyboardLogPrint.w("kims DoState5 5 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                }
                else
                {
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 4;
                    KeyboardLogPrint.w("kims DoState05 6 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            }
            else
            {
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mCompositionString += code;
                mState = 4;
                KeyboardLogPrint.w("kims DoState05 6 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }




//            mCompleteString = mCompositionString;
//            mCompositionString = "";
//            mCompositionString += code;
//            mState = 4;
//            KeyboardLogPrint.w("kims DoState05 2 mCompleteString :: " + mCompleteString);
//            ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
        }
        return ret;
    }

    ;

    private int DoState10(char code) // current composition string: a combined consonant
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("********************10*****************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState10 :: " + code);
        KeyboardLogPrint.w("mCompleteString :: " + mCompleteString);
        KeyboardLogPrint.w("mCompositionString :: " + mCompositionString);
        if (mCompositionString == "") {
            return ACTION_ERROR;
        }

        int ret = ACTION_NONE;
        if (IsConsonant(code)) {
            mCompleteString = mCompositionString;
            mCompositionString = "";
            mCompositionString += code;
            mState = 1;
            KeyboardLogPrint.w("kims DoState10 1 mCompleteString :: " + mCompleteString);
            ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
        } else {
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
            KeyboardLogPrint.w("kims DoState10 2 mCompleteString :: " + mCompleteString);
            ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
        }
        return ret;
    }

    ;

    private int DoState11(char code) // current composition string: single consonant + single vowel + a combined consonant
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*********************11****************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState11 :: " + code);
        KeyboardLogPrint.i("mCompleteString : " + mCompleteString);
        KeyboardLogPrint.i("mCompositionString : " + mCompositionString);

        if (mCompositionString == "") {
            return ACTION_ERROR;
        }

        int ret = ACTION_NONE;
        if (IsConsonant(code)) {
            int completeLastConsonant = GetLastConsonantIndex(mCompositionString.charAt(0)); // 갍
            int iFirst = InputTables.LastConsonants.iLast[completeLastConsonant]; // '갍'의 종성모음의 첫글자 ㄹ 의 인덱스
            int iLast = InputTables.LastConsonants.iFirst[completeLastConsonant]; // '갍'의 종성모음의 마지막글자 ㅌ 의 인덱스
            KeyboardLogPrint.i("iFirst :: " + iFirst);
            KeyboardLogPrint.i("iLast :: " + iLast);
            char cFirst = GetLastConsonant(iFirst); // '갍'의 ㄹ
            char cLast = InputTables.FirstConsonantCodes[iLast]; // '갍' 의 ㅌ
            KeyboardLogPrint.i("cFirst :: " + cFirst);
            KeyboardLogPrint.i("cLast :: " + cLast);
            CharModel model = ChunjiinCombineLastConsonantWithCode(cLast, code); // ㅌ 과 새로 입력된 값의 합성모음
            char rotateChar = model.getRotatedChar();
            KeyboardLogPrint.i("rotateChar :: " + rotateChar);
            if (rotateChar != (char) 0) {
                model = ChunjiinCombineLastConsonantWithCode(cFirst, rotateChar);
                char cConsonant = model.getChar();
                KeyboardLogPrint.i("cConsonant :: " + cConsonant);
                if (cConsonant != (char) 0) // 합성자음이 있다면
                {
                    int cConsonanaIndex = GetLastConsonantIndex(cConsonant);
                    char newChar = (char) ((int) mCompositionString.charAt(0) - completeLastConsonant + cConsonanaIndex);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += newChar;
                    mState = 11;
                    KeyboardLogPrint.w("kims DoState11 1 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else // 합성자음이 없다면
                {
                    KeyboardLogPrint.i("DoState11 :: 1");
                    int cNIndex = GetLastConsonantIndex(cFirst);
                    char newChar = (char) ((int) mCompositionString.charAt(0) - completeLastConsonant + cNIndex);
                    mCompositionString = "";
                    mCompleteString += newChar;
                    mCompositionString += rotateChar;
                    KeyboardLogPrint.d("DoState11 in mCompleteString :: " + mCompleteString);
                    KeyboardLogPrint.d("DoState11 in mCompositionString :: " + mCompositionString);
                    mPrefComplete = mCompleteString;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState11 2 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            } else {
                KeyboardLogPrint.i("DoState11 :: 2");
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mCompositionString += code;
                mState = 1;
                KeyboardLogPrint.w("kims DoState11 3 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }
        } else {
            if (InputTables.DotCode == code) {
                KeyboardLogPrint.i("DoState11 inin mCompleteString :: " + mCompleteString);
                KeyboardLogPrint.i("DoState11 inin mCompositionString :: " + mCompositionString);
                KeyboardLogPrint.i("DoState11 inin mCompositionString.length() : " + mCompositionString.length());

                mPrefCharbeforeDot = mCompositionString;
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mCompositionString += code;
                KeyboardLogPrint.e(" DoState03 mCompositionString :: " + mCompositionString);
                mState = 4;
                KeyboardLogPrint.w("kims DoState11 4 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            } else {
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
                KeyboardLogPrint.w("kims DoState11 5 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }

        }
        return ret;
    }

    ;

    private int DoState20(char code) // current composition string: single consonant + a combined vowel
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("********************20*****************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState20 :: " + code);
        KeyboardLogPrint.w("mCompleteString :: " + mCompleteString);
        KeyboardLogPrint.w("mCompositionString :: " + mCompositionString);

        if (mCompositionString == "") {
            return ACTION_ERROR;
        }

        int ret = ACTION_NONE;
        if (IsConsonant(code)) {
            int lcIndex = ConvertLastConsonantCodeToIndex(code);
            if (lcIndex < 0) // cannot compose the code with composition string. flush it.
            {
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mCompositionString += code;
                mState = 1;
                KeyboardLogPrint.w("kims DoState20 1 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            } else // compose..
            {
                char newChar = mCompositionString.charAt(0);
                newChar = (char) ((int) newChar + lcIndex);
                mCompleteString = "";
                mCompositionString = "";
                mCompositionString += newChar;
                mState = 21;
                KeyboardLogPrint.w("kims DoState20 2 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPOSITIONSTR;
            }
        } else {
            if (code == InputTables.DotCode) {
                char compositionChar = mCompositionString.charAt(0);
                KeyboardLogPrint.e("compositionChar : " + compositionChar);
                char vowel = GetVowel(compositionChar);
                char consonent = GetFirstConsonant(compositionChar);

                KeyboardLogPrint.e("vowel : " + vowel);
                KeyboardLogPrint.e("consonent : " + consonent);
                if (vowel == 0x315A) {
                    vowel = 0x3158;
                    int fcIndex = ConvertFirstConsonantCodeToIndex(consonent);
                    int vIndex = ConvertVowelCodeToIndex(vowel);
                    char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                    KeyboardLogPrint.e("fcIndex :: " + fcIndex);
                    KeyboardLogPrint.e("vIndex :: " + vIndex);
                    KeyboardLogPrint.e("newCode :: " + newCode);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += newCode;
                    mState = 20;
                    KeyboardLogPrint.w("kims DoState20 3 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else {
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 4;
                    KeyboardLogPrint.w("kims DoState20 4 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            } else {
                char compositionChar = mCompositionString.charAt(0);
                KeyboardLogPrint.e("compositionChar : " + compositionChar);
                char combinedVowel = GetVowel(compositionChar);
                KeyboardLogPrint.e("combinedVowel : " + combinedVowel);

                if (combinedVowel == 0x3158 && code == 0x3163)   // 기존 모음이 ㅘ 이고 입력값이 ㅣ 일 경우 ㅙ로 만들어야함
                {
                    char consonent = GetFirstConsonant(compositionChar);
                    char vowel = 0x3159; // ㅙ
                    int fcIndex = ConvertFirstConsonantCodeToIndex(consonent);
                    int vIndex = ConvertVowelCodeToIndex(vowel);
                    char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += newCode;
                    mState = 20;
                    KeyboardLogPrint.w("kims DoState20 5 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else if (combinedVowel == 0x315D && code == 0x3163)    // 기존 모음이 ㅝ 이고 입력값이 ㅣ 일 경우 ㅙ로 만들어야함
                {
                    char consonent = GetFirstConsonant(compositionChar);
                    char vowel = 0x315E; // ㅞ
                    int fcIndex = ConvertFirstConsonantCodeToIndex(consonent);
                    int vIndex = ConvertVowelCodeToIndex(vowel);
                    char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += newCode;
                    mState = 20;
                    KeyboardLogPrint.w("kims DoState20 6 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else {
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 4;
                    KeyboardLogPrint.w("kims DoState20 7 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            }

        }
        return ret;
    }

    ;

    private int DoState21(char code) // current composition string: single consonant + a combined vowel + single consonant
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*******************21******************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState21 :: " + code);
        KeyboardLogPrint.w("mCompleteString :: " + mCompleteString);
        KeyboardLogPrint.w("mCompositionString :: " + mCompositionString);

        if (mCompositionString == "") {
            return ACTION_ERROR;
        }

        int ret = ACTION_NONE;
        if (IsConsonant(code)) {
            int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
            int lcIndexTemp = ConvertLastConsonantCodeToIndex(code);
            char lConsonant = GetLastConsonant(lcIndex);
            CharModel model = ChunjiinCombineLastConsonantWithCode(lConsonant, code);
            char mChar = model.getChar();
            char rotatedChar = model.getRotatedChar();
            int rotatedIndex = GetLastConsonantIndex(rotatedChar);
            if (rotatedChar != (char) 0) // 순환자음일 경우 ex: 왓 + ㅅ -> ㅎ
            {
                if (canUseLastConsonant(rotatedChar)) // 종성자음으로 쓸 수 있을 경우, 순환종성자음이 ㄸ, ㅃ, ㅉ 가 아닐경우
                {
                    char compChar = mCompositionString.charAt(0);
                    char lChar = (char) ((int) compChar - lcIndex + rotatedIndex);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += lChar;
                    mState = 21;
                    KeyboardLogPrint.w("kims DoState21 1 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else // 종성자음으로 쓸 수 없을 경우, 순환종성자음이 ㄸ, ㅃ, ㅉ 일 경우
                {
                    char compChar = mCompositionString.charAt(0);
                    char lChar = (char) ((int) compChar - lcIndex);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompleteString += lChar;
                    mCompositionString += rotatedChar;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState21 2 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            } else // 순환 자음이 아닐 경우 왓 + ㄷ
            {
                if (mChar != (char) 0) // 종성자음이 조합자음일 경우
                {
                    int lastConsonantIndex = GetLastConsonantIndex(mChar);
                    char compChar = mCompositionString.charAt(0);
                    char lChar = (char) ((int) compChar - lcIndex + lastConsonantIndex);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += lChar;
                    mState = 22;
                    KeyboardLogPrint.w("kims DoState21 3 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else // 종성자음이 조합자음도 아니고 순환자음도 아닐경우
                {
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState21 4 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            }
        } else {
            if (code == InputTables.DotCode) {
                KeyboardLogPrint.i(" DoState03 mCompositionString :: " + mCompositionString); // 발
                mPrefCharbeforeDot = mCompositionString; // 발 저장
                mCompleteString = mCompositionString; // 발 저장
                mCompositionString = "";
                mCompositionString += code; // .
                KeyboardLogPrint.e(" DoState03 mCompositionString :: " + mCompositionString);
                mState = 4;
                KeyboardLogPrint.w("kims DoState21 5 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            } else {
                char newChar = mCompositionString.charAt(0);
                int lcIndex = GetLastConsonantIndex(newChar);
                newChar = (char) ((int) newChar - lcIndex);
                mCompleteString = "";
                mCompleteString += newChar;
                int fcIndex = ConvertFirstConsonantCodeToIndex(InputTables.LastConsonants.Code[lcIndex]);
                int vIndex = ConvertVowelCodeToIndex(code);
                newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
                mCompositionString = "";
                mCompositionString += newChar;
                mState = 2;
                KeyboardLogPrint.w("kims DoState21 6 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }
        }
        return ret;
    }

    ;

    private int DoState22(char code) // current composition string: single consonant + a combined vowel + a combined consonant
    {
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.d("*********************22****************");
        KeyboardLogPrint.d("*************************************");
        KeyboardLogPrint.e("DoState22 :: " + code);
        KeyboardLogPrint.w("mCompleteString :: " + mCompleteString);
        KeyboardLogPrint.w("mCompositionString :: " + mCompositionString);
        int ret = ACTION_NONE;

        if (mCompositionString == "") {
            return ACTION_ERROR;
        }

        if (IsConsonant(code)) {
            int completeLastConsonant = GetLastConsonantIndex(mCompositionString.charAt(0)); // 봝
            int iFirst = InputTables.LastConsonants.iLast[completeLastConsonant]; // '봝'의 종성모음의 첫글자 ㄹ 의 인덱스
            int iLast = InputTables.LastConsonants.iFirst[completeLastConsonant]; // '봝'의 종성모음의 마지막글자 ㅌ 의 인덱스
            KeyboardLogPrint.i("iFirst :: " + iFirst);
            KeyboardLogPrint.i("iLast :: " + iLast);
            char cFirst = GetLastConsonant(iFirst); // '봝'의 ㄹ
            char cLast = InputTables.FirstConsonantCodes[iLast]; // '봝' 의 ㅌ
            KeyboardLogPrint.i("cFirst :: " + cFirst);
            KeyboardLogPrint.i("cLast :: " + cLast);
            CharModel model = ChunjiinCombineLastConsonantWithCode(cLast, code); // ㅌ 과 새로 입력된 값의 합성모음
            char rotateChar = model.getRotatedChar();
            KeyboardLogPrint.i("rotateChar :: " + rotateChar);
            if (rotateChar != (char) 0) {
                model = ChunjiinCombineLastConsonantWithCode(cFirst, rotateChar);
                char cConsonant = model.getChar();
                KeyboardLogPrint.i("cConsonant :: " + cConsonant);
                if (cConsonant != (char) 0) // 합성자음이 있다면
                {
                    int cConsonanaIndex = GetLastConsonantIndex(cConsonant);
                    char newChar = (char) ((int) mCompositionString.charAt(0) - completeLastConsonant + cConsonanaIndex);
                    mCompleteString = "";
                    mCompositionString = "";
                    mCompositionString += newChar;
                    mState = 22;
                    KeyboardLogPrint.w("kims DoState22 1 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPOSITIONSTR;
                } else // 합성자음이 없다면
                {
                    KeyboardLogPrint.i("DoState22 :: 1");
                    int cNIndex = GetLastConsonantIndex(cFirst);
                    char newChar = (char) ((int) mCompositionString.charAt(0) - completeLastConsonant + cNIndex);
                    mCompositionString = "";
                    mCompleteString += newChar;
                    mCompositionString += rotateChar;
                    KeyboardLogPrint.d("DoState22 in mCompleteString :: " + mCompleteString);
                    KeyboardLogPrint.d("DoState22 in mCompositionString :: " + mCompositionString);
                    mPrefComplete = mCompleteString;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState22 2 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            } else {
                KeyboardLogPrint.i("DoState22 :: 2");
                mCompleteString = mCompositionString;
                mCompositionString = "";
                mCompositionString += code;
                mState = 1;
                KeyboardLogPrint.w("kims DoState22 3 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }
        } else {
            if (InputTables.DotCode == code) {
                KeyboardLogPrint.i("DoState22 inin mCompleteString :: " + mCompleteString);
                KeyboardLogPrint.i("DoState22 inin mCompositionString :: " + mCompositionString);
                KeyboardLogPrint.i("DoState22 inin mCompositionString.length() : " + mCompositionString.length());
                if (mCompositionString.charAt(mCompositionString.length() - 1) == InputTables.DotCode) // 문자열 마지막이 dot 일 경우
                {
                    KeyboardLogPrint.i("kims 마지막 문자열이 dot 일경우");
                } else if (mCompositionString.charAt(mCompositionString.length() - 1) == InputTables.DoubleDotCode) // 문자열 마지막이 double dot 일 경우
                {
                    KeyboardLogPrint.i("kims 마지막 문자열이 double dot 일경우");
                } else {
                    KeyboardLogPrint.i("마지막 문자열이 dot 이 아닐경우");
                    mCompleteString = mCompositionString;
                    mCompositionString = "";
                    mCompositionString += code;
                    mState = 1;
                    KeyboardLogPrint.w("kims DoState22 4 mCompleteString :: " + mCompleteString);
                    ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
                }
            } else {
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
                KeyboardLogPrint.w("kims DoState22 5 mCompleteString :: " + mCompleteString);
                ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
            }

        }
        return ret;
    }

    ;

    public void setStrLength(int length) {
        mCharLength = length;
    }

    @Override
    public int getStrLength() {
        return mCharLength;
    }

    private boolean hasCombinedLastConsonant(char character) {
        KeyboardLogPrint.e("hasCombinedLastConsonant character :: " + character);
        boolean hasCombinedLastConsonant = false;
        int lastConsonantIndex = GetLastConsonantIndex(character);
        KeyboardLogPrint.e("lastConsonantIndex :: " + lastConsonantIndex);
        int[] COMBINED_LAST_CONSONANT = {3, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18}; // 조합자음의 index 모음
        for (int i = 0; i < COMBINED_LAST_CONSONANT.length; i++) {
            if (lastConsonantIndex == COMBINED_LAST_CONSONANT[i]) {
                hasCombinedLastConsonant = true;
            }
        }
        KeyboardLogPrint.e("hasCombinedLastConsonant hasCombinedLastConsonant :: " + hasCombinedLastConsonant);
        return hasCombinedLastConsonant;
    }

    private boolean canUseLastConsonant(char character) {
        boolean canUseLastConsonant = true;
        char[] exceptChar = {0x3138, 0x3143, 0x3149}; // 종성자음으로 사용할 수 없는 것들 ㄸ, ㅃ, ㅉ
        for (int i = 0; i < exceptChar.length; i++) {
            if (character == exceptChar[i]) {
                canUseLastConsonant = false;
            }
        }
        KeyboardLogPrint.e("canUseLastConsonant :: " + canUseLastConsonant);
        return canUseLastConsonant;
    }


    @Override
    public String DecomposeConsonant(ArrayList<Integer> pkeys) {
        mState = 0;
        mCompleteString = "";
        mCompositionString = "";
        mPrefComplete = "";
        StringBuilder mComposing = new StringBuilder();

        for (int i = 0; i < pkeys.size(); i++) {
            int primaryKey = pkeys.get(i);
            int ret = DoAutomata((char) primaryKey, InputTables.KEYSTATE_NONE);

            if ((ret & ChunjiinPlusAutomata.ACTION_REMOVE_PREV_CHAR) != 0) {
                if (mComposing.length() > 0) {
                    mComposing.replace(mComposing.length() - 1, mComposing.length(), "");
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_REMOVE_PREV_CHAR after replace :: " + mComposing.toString());
                }
            }

            if ((ret & KoreanAutomata.ACTION_UPDATE_COMPLETESTR) != 0) {
                // mComposing.setLength(0);

                if (mComposing.length() > 0) {
                    mComposing.replace(mComposing.length() - 1, mComposing.length(), mCompleteString);
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_UPDATE_COMPLETESTR after replace :: " + mComposing.toString());
                } else {
                    mComposing.append(mCompleteString);
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_UPDATE_COMPLETESTR after append :: " + mComposing.toString());
                }
            }

            if ((ret & ChunjiinPlusAutomata.ACTION_REMAKECHAR) != 0) // 천지인에서 각. 다음 ㅣ가 왔을 때 '각.' 을 '가'로 바꿔줘야함
            {
                if (mComposing.length() > 1) {
                    KeyboardLogPrint.w("mComposing.length :: " + mComposing.length());
                    mComposing.replace(mComposing.length() - 2, mComposing.length(), mCompleteString);
                    mComposing.append(mCompositionString);
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_REMAKECHAR after replace and append :: " + mComposing.toString());
                } else {
                    KeyboardLogPrint.w("mComposing.length :: " + mComposing.length());
                    KeyboardLogPrint.w("handleCharacter ACTION_REMAKECHAR kauto.GetCompleteString() :: " + mCompleteString);
                    mComposing.append(mCompleteString);
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_REMAKECHAR after append :: " + mComposing.toString());
                }
            }

            if ((ret & KoreanAutomata.ACTION_UPDATE_COMPOSITIONSTR) != 0) {
                if ((mComposing.length() > 0) && ((ret & KoreanAutomata.ACTION_UPDATE_COMPLETESTR) == 0) && ((ret & KoreanAutomata.ACTION_APPEND) == 0)) {
                    mComposing.replace(mComposing.length() - 1, mComposing.length(), mCompositionString);
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_UPDATE_COMPOSITIONSTR after replace :: " + mComposing.toString());
                } else {
                    mComposing.append(mCompositionString);
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_UPDATE_COMPOSITIONSTR after append :: " + mComposing.toString());
                }
            }


            if ((ret & ChunjiinPlusAutomata.ACTION_MAKE_VOWEL) != 0) // 천지인일 경우 . 단자음 + dot 조합에서 모음 들어왔을 경우
            {
                if ( mComposing.length() > 0  )
                {
                    mComposing.replace(mComposing.length() - getStrLength(), mComposing.length(), "");
                    mComposing.append(mCompositionString);
                    KeyboardLogPrint.w(" *** handleCharacter ACTION_MAKE_VOWEL after replace and append :: " + mComposing.toString());
                }
            }
        }



        return mComposing.toString();
    }

//    @Override
//    public String DecomposeConsonant(ArrayList<Integer> pkeys) {
//
//        mState = 0;
//        mCompleteString = "";
//        mCompositionString = "";
//        mPrefComplete = "";
//        String retString = "";
//        for (int i = 0; i < pkeys.size(); i++) {
//            int primaryKey = pkeys.get(i);
//            DoAutomata((char) primaryKey, InputTables.KEYSTATE_NONE);
//
//            if (!TextUtils.isEmpty(mCompleteString) && retString.lastIndexOf(mCompleteString) < 1)
//            {
//                KeyboardLogPrint.w("DecomposeConsonant mCompleteString :: " + mCompleteString);
//                if ( GetFirstConsonant(mCompleteString.charAt(0)) != (char)0 && GetVowel(mCompleteString.charAt(0)) != (char)0 )
////                if ( GetVowel(mCompleteString.charAt(0)) != (char)0 )
//                    retString += mCompleteString;
//            }
//        }
//        KeyboardLogPrint.w("DecomposeConsonant mCompositionString :: " + mCompositionString);
//        if (!TextUtils.isEmpty(mCompositionString) && mCompositionString.charAt(0) != InputTables.DotCode )
//            retString += mCompositionString;
//        KeyboardLogPrint.w("DecomposeConsonant retString :: " + retString);
//        return retString;
//    }

    @Override
    public void setInitState() {
        KeyboardLogPrint.w("setInitState chunjiinplus : " + mState + " : " + mCompleteString + " : " + mCompositionString + " : " + mPrefComplete);
        mState = 0;
        mPrefComplete = "";
        mCompleteString = "";
        mCompositionString = "";
        mPrefCharbeforeDot = "";
        mConsonantBeforeDoubleDot = "";
    }

    @Override
    public void setAutomataValue(ArrayList<String> val)
    {
        if ( val == null )
        {
            mPrefComplete = "";
            mCompleteString = "";
            mCompositionString = "";
            mPrefCharbeforeDot = "";
            mConsonantBeforeDoubleDot = "";
            mState = 0;
        }
        else
        {
            mPrefComplete = val.get(0);
            mCompleteString = val.get(1);
            mCompositionString = val.get(2);
            mPrefCharbeforeDot = val.get(3);
            mConsonantBeforeDoubleDot = val.get(4);
            try
            {
                mState = Integer.valueOf(val.get(5));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mState = 0;
            }
        }
    }

    @Override
    public ArrayList<String> getAutomataValue()
    {
        ArrayList<String> array = new ArrayList<String>();
        array.add(0, mPrefComplete);
        array.add(1, mCompleteString);
        array.add(2, mCompositionString);
        array.add(3, mPrefCharbeforeDot);
        array.add(4, mConsonantBeforeDoubleDot);
        try
        {
            array.add(5, String.valueOf(mState));
        }
        catch (Exception e)
        {
            array.add(5, String.valueOf(0));
            e.printStackTrace();

        }

        KeyboardLogPrint.w("getAutomataValue 1 :: " + mPrefComplete);
        KeyboardLogPrint.w("getAutomataValue 2 :: " + mCompleteString);
        KeyboardLogPrint.w("getAutomataValue 3 :: " + mCompositionString);
        KeyboardLogPrint.w("getAutomataValue 4 :: " + mPrefCharbeforeDot);
        KeyboardLogPrint.w("getAutomataValue 5 :: " + mConsonantBeforeDoubleDot);
        KeyboardLogPrint.w("getAutomataValue 6 :: " + mState);

        return array;
    }

    @Override
    public void setAutomata(String complete, String composition, int state)
    {
        mCompleteString = complete;
        mCompositionString = composition;
        mState = state;
    }

    @Override
    public ArrayList<Integer> getState(String str)
    {
        ArrayList<Integer> array = new ArrayList<Integer>();
        if ( str.length() <= 0 )
            return null;
        int state = -1;
        int lcCode = -1;
        char ch = str.charAt(0);

        if (ch >= '가' && ch <= '힣')
        {
            int ce = ch - '가';
            int mIndex = (ce = ce % (588)) / 28;
            boolean lExist = ((ce = ce % 28) != 0);
            boolean first = false;
            KeyboardLogPrint.w("ce :: " + ce);
            KeyboardLogPrint.w("mIndex :: " + mIndex);
            KeyboardLogPrint.w("lExist :: " + lExist);

            int mFirstCode = CharTables.MIDDLE.FIRST_MIDDLE[mIndex];
            int mSecondCode = CharTables.MIDDLE.SECOND_MIDDLE[mIndex];
            KeyboardLogPrint.w("mFirstCode : " + mFirstCode);
            KeyboardLogPrint.w("mSecondCode : " + mSecondCode);
            if ( mSecondCode > 0 )
            {
                KeyboardLogPrint.w("조합모음 있음");
                state = 20;
            }
            else
            {
                KeyboardLogPrint.w("조합모음 없음");
                state = 2;
            }

            // 종성을 구함
            if ((ce = ce % 28) != 0)
            {
                int lIndex = ce;
                int lFirstCode = CharTables.LAST.FIRST_LAST[lIndex];
                int lSecondCode = CharTables.LAST.LAST_LAST[lIndex];
                lcCode = CharTables.LAST.LAST_R_CODE[lIndex];
                if ( lFirstCode != -1 && lSecondCode > 0 )
                {
                    if ( state == 20 )
                    {
                        KeyboardLogPrint.w("조합모음 + 종성 조합자음 있음");
                        state = 22;
                    }
                    else
                    {
                        KeyboardLogPrint.w("단모음 + 종성 조합자음 있음");
                        state = 11;
                    }
                }
                else
                {
                    if ( state == 20 )
                    {
                        KeyboardLogPrint.w("조합모음 + 종성 조합자음 없음");
                        state = 21;
                    }
                    else
                    {
                        KeyboardLogPrint.w("종성 조합자음 없음");
                        state = 3;
                    }
                }
            }
        }

        if ( state == -1 )
        {
            char firstConsonant = GetFirstConsonant(str.charAt(0));
            char vowel = GetVowel(str.charAt(0));
            int vIndex = GetVowelIndex(vowel);
            int fcIndex = GetFirstConsonantIndex(firstConsonant);
            KeyboardLogPrint.w("getState firstConsonant :: " + firstConsonant);
            KeyboardLogPrint.w("getState vowel :: " + vowel);
            KeyboardLogPrint.w("getState vIndex :: " + vIndex);
            KeyboardLogPrint.w("getState fcIndex :: " + fcIndex);

            if ( fcIndex >= 0 )
                state = 1;
            else if ( vIndex >= 0 )
            {
                if ( vIndex == 9 || vIndex == 10 || vIndex == 11 || vIndex == 14 || vIndex == 15 || vIndex == 16 || vIndex == 19)
                    state = 5;
                else
                    state = 4;
            }
        }

        KeyboardLogPrint.w("state :: " + state);
        array.add(0, state);
        array.add(1, lcCode);
        return array;
    }

//    public static ArrayList<Integer> getCodeArray(String text)
//    {
//        if (text == null) { return null; }
//        // StringBuilder의 capacity가 0으로 등록되는 것 방지.
//        if (text.length() == 0) { return null; }
//
//        ArrayList<Integer> codeArray = new ArrayList<Integer>();
//
//        for (char ch : text.toCharArray())
//        {
//            if (ch >= '가' && ch <= '힣')
//            {
//                // 한글의 시작부분을 구함
//                int ce = ch - '가';
//                KeyboardLogPrint.w("ce :: " + ce);
//                // 초성을 구함
////                rv.append(FIRST.KO_ATOM_S[ce / (588)]); // 21 * 28
////                KeyboardLogPrint.w("vall1 :: " + FIRST.KO_ATOM_S[ce / (588)]);
//                KeyboardLogPrint.w("vall1 index  :: " + ce / (588));
//                codeArray.add(CharTables.FIRST.CODE[ce / (588)]);
//
//
//                // 중성을 구함
////                rv.append(MIDDLE.KO_ATOM_M[(ce = ce % (588)) / 28]); // 21 * 28
////                KeyboardLogPrint.w("vall2 :: " + MIDDLE.KO_ATOM_M[(ce = ce % (588)) / 28]);
//                KeyboardLogPrint.w("vall2 index  :: " + (ce = ce % (588)) / 28);
//
//                int mIndex = (ce = ce % (588)) / 28;
//                KeyboardLogPrint.w("middle index : " + mIndex);
//                int mFirstCode = CharTables.MIDDLE.FIRST_MIDDLE[mIndex];
//                int mSecondCode = CharTables.MIDDLE.SECOND_MIDDLE[mIndex];
//                KeyboardLogPrint.w("mFirstCode : " + mFirstCode);
//                KeyboardLogPrint.w("mSecondCode : " + mSecondCode);
//                codeArray.add(mFirstCode);
//                if ( mSecondCode > 0 )
//                    codeArray.add(mSecondCode);
//
//                // 종성을 구함
//                if ((ce = ce % 28) != 0)
//                {
////                    rv.append(LAST.KO_ATOM_E[ce]);
////                    KeyboardLogPrint.w("vall3 :: " + LAST.KO_ATOM_E[ce]);
//                    KeyboardLogPrint.w("vall3 index  :: " + ce);
//
//                    int lIndex = ce;
//                    KeyboardLogPrint.w("last index : " + lIndex);
//                    int lFirstCode = CharTables.LAST.FIRST_LAST[lIndex];
//                    int lSecondCode = CharTables.LAST.LAST_LAST[lIndex];
//                    KeyboardLogPrint.w("lFirstCode : " + lFirstCode);
//                    KeyboardLogPrint.w("lSecondCode : " + lSecondCode);
//                    if ( lFirstCode != -1 )
//                        codeArray.add(lFirstCode);
//                    if ( lSecondCode > 0 )
//                        codeArray.add(lSecondCode);
//                }
//            }
//            // 쌍자음과 이중모음 분리
//            else if (ch >= 'ㄱ' && ch <= 'ㅣ')
//            {
////                rv.append(COMBINED.KO_ATOM_P[ch - 'ㄱ']);
////                KeyboardLogPrint.w("vall4 :: " + COMBINED.KO_ATOM_P[ch - 'ㄱ']);
//                KeyboardLogPrint.w("vall4 index  :: " + (ch - 'ㄱ'));
//                int cIndex = (ch - 'ㄱ');
//                KeyboardLogPrint.w("combined index : " + cIndex);
//                int cFirstCode = CharTables.COMBINED.COMBINED_FIRST[cIndex];
//                int cSecondCode = CharTables.COMBINED.COMBINED_SECOND[cIndex];
//                KeyboardLogPrint.w("cFirstCode : " + cFirstCode);
//                KeyboardLogPrint.w("cSecondCode : " + cSecondCode);
//            }
//            else
//            {
////                rv.append(ch);
//                KeyboardLogPrint.w("vall5 :: " + ch);
//            }
//        }
//
//        return codeArray;
//    }
}
