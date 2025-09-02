package com.enliple.keyboard.activity;

import com.enliple.keyboard.common.KeyboardLogPrint;

import java.util.ArrayList;

public class CharTables {
    // 일반 분해
    private final static char[] KO_INIT_S =
            {
                    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ',
                    'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
            }; // 19

    private final static char[] KO_INIT_M =
            {
                    'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ',
                    'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
            }; // 21
    private final static char[] KO_INIT_E =
            {
                    0, 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ',
                    'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
            }; // 28


    public static final class FIRST_ARRAY {
        public final static char[][] KO_ATOM_S = { { 'ㄱ' }, { 'ㄱ', 'ㄱ' }, { 'ㄴ' }, { 'ㄷ' }, { 'ㄷ', 'ㄷ' }, { 'ㄹ' }, { 'ㅁ' }, { 'ㅂ' }, { 'ㅂ', 'ㅂ' }, { 'ㅅ' }, { 'ㅅ', 'ㅅ' }, { 'ㅇ' }, { 'ㅈ' }, { 'ㅈ', 'ㅈ' }, { 'ㅊ' }, { 'ㅋ' }, { 'ㅌ' }, { 'ㅍ' }, { 'ㅎ' } };
        public final static int CODE_FIRST[] =   {   114,        114,          115,     101,         101,         115,      100,      113,         113,         116,        116,         100,      119,          119,         119,      114,     101,      113,      116   };
        public final static int CODE_SECOND[] =  {   0,          114,          0,       0,           101,         115,      100,      0,           113,         0,          116,         0,       0,            119,         119,      114,     101,      113,      116   };
        public final static int CODE_THIRD[] =   {   0,          114,          0,       0,           101,         0,        0,        0,           113,          0,          116,         0,       0,            119,         0,        0,       0,        0,        0     };
    }

    public static final class FIRST {
        public final static char[][] KO_ATOM_S = { { 'ㄱ' }, { 'ㄱ', 'ㄱ' }, { 'ㄴ' }, { 'ㄷ' }, { 'ㄷ', 'ㄷ' }, { 'ㄹ' }, { 'ㅁ' }, { 'ㅂ' }, { 'ㅂ', 'ㅂ' }, { 'ㅅ' }, { 'ㅅ', 'ㅅ' }, { 'ㅇ' }, { 'ㅈ' }, { 'ㅈ', 'ㅈ' }, { 'ㅊ' }, { 'ㅋ' }, { 'ㅌ' }, { 'ㅍ' }, { 'ㅎ' } };
        // CODE 값이 -일 경우 SHIFT 값으로 간주한다. 114 -> ㄱ , -114 -> ㄲ
        public final static int CODE[] =         {    114,       -114,         115,      101,        -101,        102,      97,       113,       -113,         116,       -116,         100,      119,       -119,         99,       122,      120,     118,      103   };
//        private final static int SHIFT[] =         {   0,          -1,           0,       0,           -1,         0,        0,        0,          -1,          0,          -1,          0,        0,          -1,          0,        0,        0,       0,        0   };
    }

    public static final class MIDDLE {
        public final static char[][] KO_ATOM_M = { { 'ㅏ' }, { 'ㅐ' }, { 'ㅑ' }, { 'ㅒ' }, { 'ㅓ' }, { 'ㅔ' }, { 'ㅕ' }, { 'ㅖ' }, { 'ㅗ' }, { 'ㅗ', 'ㅏ' }, { 'ㅗ', 'ㅐ' }, { 'ㅗ', 'ㅣ' }, { 'ㅛ' }, { 'ㅜ' }, { 'ㅜ', 'ㅓ' }, { 'ㅜ', 'ㅔ' }, { 'ㅜ', 'ㅣ' }, { 'ㅠ' }, { 'ㅡ' }, { 'ㅡ', 'ㅣ' }, { 'ㅣ' } };
        // CODE 값이 -일 경우 SHIFT 값으로 간주한다. 111 -> ㅐ, -111 -> ㅒ
        public final static int[] FIRST_MIDDLE = {   107,      111,      105,     -111,      106,     112,      117,     -112,     104,         104,           104,             104,        121,     110,        110,            110,            110,         98,       109,        109,         108   };
        public final static int[] SECOND_MIDDLE = {   0,        0,        0,       0,         0,       0,        0,       0,        0,           107,           111,             108,        0,       0,          106,            112,            108,         0,        0,          108,         0   };

    }
    public static final class MIDDLE_ARRAY {
        public final static char[][] KO_ATOM_M = { { 'ㅏ' }, { 'ㅐ' }, { 'ㅑ' }, { 'ㅒ' }, { 'ㅓ' }, { 'ㅔ' }, { 'ㅕ' }, { 'ㅖ' }, { 'ㅗ' }, { 'ㅗ', 'ㅏ' }, { 'ㅗ', 'ㅐ' }, { 'ㅗ', 'ㅣ' }, { 'ㅛ' }, { 'ㅜ' }, { 'ㅜ', 'ㅓ' }, { 'ㅜ', 'ㅔ' }, { 'ㅜ', 'ㅣ' }, { 'ㅠ' }, { 'ㅡ' }, { 'ㅡ', 'ㅣ' }, { 'ㅣ' } };
        public final static int CODE_FIRST[] =   {    108,     108,      108,     108,      122,      122,      122,      122,      122,         122,          122,            122,         122,     109,         109,           109,            109,         109,      109,        109,         108   };
        public final static int CODE_SECOND[] =   {   122,     122,      122,     122,      108,      108,      122,      122,      109,         109,          109,            109,         122,     122,         122,           122,            122,         122,      0,          108,         0     };
        public final static int CODE_THIRD[] =   {    0,       108,      122,     122,      0,        108,      108,      108,      0,           108,          108,            108,         109,     0,           122,           122,            108,         122,      0,          0,           0     };
        public final static int CODE_FORTH[] =   {    0,       0,        0,       108,      0,        0,        0,        108,      0,           122,          122,            0,           0,       0,           108,           108,            0,           0,        0,          0,           0     };
        public final static int CODE_FIFTH[] =   {    0,       0,        0,       0,        0,        0,        0,        0,        0,           0,             108,           0,           0,       0,           0,             108,            0,           0,        0,          0,           0     };
    }
    public static final class LAST {
        public final static char[][] KO_ATOM_E = { {}, { 'ㄱ' }, { 'ㄱ', 'ㄱ' }, { 'ㄱ', 'ㅅ' }, { 'ㄴ' }, { 'ㄴ', 'ㅈ' }, { 'ㄴ', 'ㅎ' }, { 'ㄷ' }, { 'ㄹ' }, { 'ㄹ', 'ㄱ' }, { 'ㄹ', 'ㅁ' }, { 'ㄹ', 'ㅂ' }, { 'ㄹ', 'ㅅ' }, { 'ㄹ', 'ㅌ' }, { 'ㄹ', 'ㅍ' }, { 'ㄹ', 'ㅎ' }, { 'ㅁ' }, { 'ㅂ' }, { 'ㅂ', 'ㅅ' }, { 'ㅅ' }, { 'ㅅ', 'ㅅ' }, { 'ㅇ' }, { 'ㅈ' }, { 'ㅊ' }, { 'ㅋ' }, { 'ㅌ' }, { 'ㅍ' }, { 'ㅎ' } };
        public final static int FIRST_LAST[]   = { -1,    114,       -114,            114,         115,         115,           115,         101,      102,        102,           102,            102,            102,           102,            102,           102,         97,       113,        113,         116,        -116,         100,      119,      99,       122,     120,      118,      103   };
        public final static int LAST_LAST[]    = { -1,    0,         0,               116,         0,           119,           103,         0,        0,          114,           97,             113,            116,           120,            118,           103,         0,        0,          116,         0,          0,            0,        0,        0,        0,       0,        0,        0   };
        public final static int LAST_R_CODE[]  = { -1,    114,       114,             116,         115,         119,           116,         101,      115,        114,           100,            113,            116,           101,            113,           116,         100,      113,        116,         116,        116,          100,      119,      119,      114,     101,      113,      116   };
    }

    public static final class LAST_ARRAY {
        public final static char[][] KO_ATOM_E = { {}, { 'ㄱ' }, { 'ㄱ', 'ㄱ' }, { 'ㄱ', 'ㅅ' }, { 'ㄴ' }, { 'ㄴ', 'ㅈ' }, { 'ㄴ', 'ㅎ' }, { 'ㄷ' }, { 'ㄹ' }, { 'ㄹ', 'ㄱ' }, { 'ㄹ', 'ㅁ' }, { 'ㄹ', 'ㅂ' }, { 'ㄹ', 'ㅅ' }, { 'ㄹ', 'ㅌ' }, { 'ㄹ', 'ㅍ' }, { 'ㄹ', 'ㅎ' }, { 'ㅁ' }, { 'ㅂ' }, { 'ㅂ', 'ㅅ' }, { 'ㅅ' }, { 'ㅅ', 'ㅅ' }, { 'ㅇ' }, { 'ㅈ' }, { 'ㅊ' }, { 'ㅋ' }, { 'ㅌ' }, { 'ㅍ' }, { 'ㅎ' } };
        public final static int CODE_FIRST[]   = { -1,    114,       114,            114,         115,         115,           115,         101,      115,        115,           115,            115,            115,           115,            115,           115,         100,      113,        113,         116,        116,         100,      119,      119,     114,      101,      113,      116   };
        public final static int CODE_SECOND[]  = { -1,    0,         114,            116,         0,           119,           116,         0,        115,        115,           115,            115,            115,           115,            115,           115,         100,      0,          116,         0,          116,         0,        0,        119,     114,      101,      113,      116   };
        public final static int CODE_THIRD[]   = { -1,    0,         114,            0,           0,           0,             116,         0,        0,          114,           100,            113,            116,           101,            113,           116,         0,        0,          0,           0,          116,         0,        0,        0,       0,        0,        0,        0     };
        public final static int CODE_FORTH[]   = { -1,    0,         0,              0,           0,           0,             0,           0,        0,          0,             100,            0,              0,             0,              0,             116,         0,        0,          0,           0,          0,           0,        0,        0,       0,        0,        0,        0     };
    }
    // 쌍자음이나 이중모음을 분해
    public static final class COMBINED {
    //                                                   0              1               2          3          4                5          6         7          8          9                10             11             12              13            14              15            16       17          18             19            20          21           22        23          24           25        26       27         28       29       30         31       32        33       35        36        37       38         39         40              41             42             43       44       45               46             47             48       49          50            51
        public final static char[][] KO_ATOM_P   =  { { 'ㄱ' }, { 'ㄱ', 'ㄱ' }, { 'ㄱ', 'ㅅ' }, { 'ㄴ' }, { 'ㄴ', 'ㅈ' }, { 'ㄴ', 'ㅎ' }, { 'ㄷ' }, { 'ㄸ' }, { 'ㄹ' }, { 'ㄹ', 'ㄱ' }, { 'ㄹ', 'ㅁ' }, { 'ㄹ', 'ㅂ' }, { 'ㄹ', 'ㅅ' }, { 'ㄹ', 'ㄷ' }, { 'ㄹ', 'ㅍ' }, { 'ㄹ', 'ㅎ' }, { 'ㅁ' }, { 'ㅂ' }, { 'ㅂ', 'ㅂ' }, { 'ㅂ', 'ㅅ' }, { 'ㅅ' }, { 'ㅅ', 'ㅅ' }, { 'ㅇ' }, { 'ㅈ' }, { 'ㅈ', 'ㅈ' }, { 'ㅊ' }, { 'ㅋ' }, { 'ㅌ' }, { 'ㅍ' }, { 'ㅎ' }, { 'ㅏ' }, { 'ㅐ' }, { 'ㅑ' }, { 'ㅒ' }, { 'ㅓ' }, { 'ㅔ' }, { 'ㅕ' }, { 'ㅖ' }, { 'ㅗ' }, { 'ㅗ', 'ㅏ' }, { 'ㅗ', 'ㅐ' }, { 'ㅗ', 'ㅣ' }, { 'ㅛ' }, { 'ㅜ' }, { 'ㅜ', 'ㅓ' }, { 'ㅜ', 'ㅔ' }, { 'ㅜ', 'ㅣ' }, { 'ㅠ' }, { 'ㅡ' }, { 'ㅡ', 'ㅣ' }, { 'ㅣ' } };
        public final static int COMBINED_FIRST[] =  {   114,       -114,            114,         115,        115,            115,         101,     -101,     102,        102,            102,            102,           102,            102,           102,            102,         97,       113,       -113,           113,         116,       -116,         100,      119,       -119,         99,      122,      120,      118,      103,     107,      111,      105,     -111,      106,      112,      117,     -112,     104,        104,            104,           104,          121,     110,         110,           110,           110,          98,      109,         109,         108   };
        public final static int COMBINED_SECOND[] = {   0,         0,               116,         0,          119,            103,         0,       0,        0,          114,            97,             113,           116,            101,           118,            103,         0,        0,         0,              116,         0,         0,            0,        0,         0,            0,       0,        0,        0,        0,       0,        0,        0,       0,         0,        0,        0,       0,        0,          107,            111,           108,          0,       110,         106,           112,           108,          0,       0,           108,         0     };
    }

    public static final class COMBINED_ARRAY {
        public final static char[][] KO_ATOM_P   =  { { 'ㄱ' }, { 'ㄱ', 'ㄱ' }, { 'ㄱ', 'ㅅ' }, { 'ㄴ' }, { 'ㄴ', 'ㅈ' }, { 'ㄴ', 'ㅎ' }, { 'ㄷ' }, { 'ㄸ' }, { 'ㄹ' }, { 'ㄹ', 'ㄱ' }, { 'ㄹ', 'ㅁ' }, { 'ㄹ', 'ㅂ' }, { 'ㄹ', 'ㅅ' }, { 'ㄹ', 'ㄷ' }, { 'ㄹ', 'ㅍ' }, { 'ㄹ', 'ㅎ' }, { 'ㅁ' }, { 'ㅂ' }, { 'ㅂ', 'ㅂ' }, { 'ㅂ', 'ㅅ' }, { 'ㅅ' }, { 'ㅅ', 'ㅅ' }, { 'ㅇ' }, { 'ㅈ' }, { 'ㅈ', 'ㅈ' }, { 'ㅊ' }, { 'ㅋ' }, { 'ㅌ' }, { 'ㅍ' }, { 'ㅎ' }, { 'ㅏ' }, { 'ㅐ' }, { 'ㅑ' }, { 'ㅒ' }, { 'ㅓ' }, { 'ㅔ' }, { 'ㅕ' }, { 'ㅖ' }, { 'ㅗ' }, { 'ㅗ', 'ㅏ' }, { 'ㅗ', 'ㅐ' }, { 'ㅗ', 'ㅣ' }, { 'ㅛ' }, { 'ㅜ' }, { 'ㅜ', 'ㅓ' }, { 'ㅜ', 'ㅔ' }, { 'ㅜ', 'ㅣ' }, { 'ㅠ' }, { 'ㅡ' }, { 'ㅡ', 'ㅣ' }, { 'ㅣ' } };
        public final static int CODE_FIRST[] =  {   114,       114,            114,         115,        115,            115,         101,       101,     115,        115,            115,            115,           115,            115,           115,            115,         100,       113,       113,           113,         116,       116,          100,      119,       119,           119,     114,      101,      113,      116,     108,      108,      108,     108,      122,      122,      122,      122,     122,        122,            122,           122,          122,     109,         109,           109,           109,          109,      109,         109,         108   };
        public final static int CODE_SECOND[] = {   0,         114,            116,         0,          119,            116,         0,         101,     115,        115,            115,            115,           115,            115,           115,            115,         100,       0,         113,           116,         0,         116,          0,        0,         119,           119,     114,      101,      113,      116,     122,      122,      122,     122,      108,      108,      122,      122,     109,        109,            109,           109,          122,     122,         122,           122,           122,          122,      0,           108,         0     };
        public final static int CODE_THIRD[] =  {   0,         114,            0,           0,          0,              116,         0,         101,     0,          114,            100,            113,           116,            101,           113,            116,         0,         0,         113,           0,           0,         116,          0,        0,         119,           0,       0,        0,        0,        0,       0,        108,      122,     122,      0,        108,      108,      108,     0,          108,            108,           108,          109,     0,           122,           122,           108,          122,      0,           0,           0     };
        public final static int CODE_FORTH[] =  {   0,         0,              0,           0,          0,              0,           0,         0,       0,          0,              100,            0,             0,              0,             113,            116,         0,         0,         0,             0,           0,         0,            0,        0,         0,             0,       0,        0,        0,        0,       0,        0,        0,       108,      0,        0,        0,        108,     0,          122,            122,           0,            0,       0,           108,           108,           0,            0,        0,           0,           0     };
        public final static int CODE_FIFTH[] =  {   0,         0,              0,           0,          0,              0,           0,         0,       0,          0,              0,              0,             0,              0,             0,              0,           0,         0,         0,             0,           0,         0,            0,        0,         0,             0,       0,        0,        0,        0,       0,        0,        0,       0,        0,        0,        0,        0,       0,          0,              108,           0,            0,       0,           0,             108,           0,            0,        0,           0,           0     };
    }

    public static final class STROKE_COUNT {
        //                                    ㅛ    ㅕ    ㅑ  ㅐ    ㅔ   ㅗ    ㅓ   ㅏ   ㅣ  ㅠ   ㅜ    ㅡ   ㅒ     ㅖ
        public final static int CODE[]  =  { 121, 117, 105, 111, 112, 104, 106, 107, 108, 98, 110, 109, -111, -112 };
        public final static int STROKE[] = { 3,    3,   3,   3,   3,   2,   2,   2,   1,   3,  2,   1,   4,     4};
    }

    /** 한글부분을 자소로 완전 분리합니다. <br>많다 = [ㅁㅏㄴㅎㄷㅏ]*/
    public static String toKoJasoAtom(String text)
    {
        if (text == null) { return null; }
        // StringBuilder의 capacity가 0으로 등록되는 것 방지.
        if (text.length() == 0) { return ""; }

        // 한글자당 최대 6글자가 될 수 있다.
        // 추가 할당 없이 사용하기위해 capacity 를 최대 글자 수 만큼 지정하였다.
        StringBuilder rv = new StringBuilder(text.length() * 6);

        for (char ch : text.toCharArray())
        {
            if (ch >= '가' && ch <= '힣')
            {
                // 한글의 시작부분을 구함
                int ce = ch - '가';
                // 초성을 구함
                rv.append(FIRST.KO_ATOM_S[ce / (588)]); // 21 * 28
                // 중성을 구함
                rv.append(MIDDLE.KO_ATOM_M[(ce = ce % (588)) / 28]); // 21 * 28
                // 종성을 구함
                if ((ce = ce % 28) != 0)
                    rv.append(LAST.KO_ATOM_E[ce]);
            }
            // 쌍자음과 이중모음 분리
            else if (ch >= 'ㄱ' && ch <= 'ㅣ')
                rv.append(COMBINED.KO_ATOM_P[ch - 'ㄱ']);
            else
                rv.append(ch);
        }
        return rv.toString();
    }

    public static ArrayList<Integer> getBackCodeArray(String text)
    {
        if (text == null) { return null; }
        // StringBuilder의 capacity가 0으로 등록되는 것 방지.
        if (text.length() == 0) { return null; }

        ArrayList<Integer> codeArray = new ArrayList<Integer>();

        for (char ch : text.toCharArray())
        {
            if (ch >= '가' && ch <= '힣')
            {
                // 한글의 시작부분을 구함
                int ce = ch - '가';
                // 초성을 구함
                int cFirst = FIRST_ARRAY.CODE_FIRST[ce / (588)];
                int cSecond = FIRST_ARRAY.CODE_SECOND[ce / (588)];
                int cThird = FIRST_ARRAY.CODE_THIRD[ce / (588)];
                if (cFirst != 0 )
                    codeArray.add(cFirst);
                if ( cSecond != 0 )
                    codeArray.add(cSecond);
                if ( cThird != 0 )
                    codeArray.add(cThird);

                // 중성을 구함

                int mIndex = (ce = ce % (588)) / 28;
                int vFirst = MIDDLE_ARRAY.CODE_FIRST[mIndex];
                int vSecond = MIDDLE_ARRAY.CODE_SECOND[mIndex];
                int vThird = MIDDLE_ARRAY.CODE_THIRD[mIndex];
                int vForth = MIDDLE_ARRAY.CODE_FORTH[mIndex];
                int fFifth = MIDDLE_ARRAY.CODE_FIFTH[mIndex];

                if ( vFirst != 0 )
                    codeArray.add(vFirst);
                if ( vSecond != 0 )
                    codeArray.add(vSecond);
                if ( vThird != 0 )
                    codeArray.add(vThird);
                if ( vForth != 0 )
                    codeArray.add(vForth);
                if ( fFifth != 0 )
                    codeArray.add(fFifth);

                // 종성을 구함
                if ((ce = ce % 28) != 0)
                {
                    int lIndex = ce;
                    int lFirst = LAST_ARRAY.CODE_FIRST[lIndex];
                    int lSecond = LAST_ARRAY.CODE_SECOND[lIndex];
                    int lThird = LAST_ARRAY.CODE_THIRD[lIndex];
                    int lForth = LAST_ARRAY.CODE_FORTH[lIndex];

                    if ( lFirst != 0 )
                        codeArray.add(lFirst);
                    if ( lSecond != 0 )
                        codeArray.add(lSecond);
                    if ( lThird != 0 )
                        codeArray.add(lThird);
                    if ( lForth != 0 )
                        codeArray.add(lForth);
                }
            }
            // 쌍자음과 이중모음 분리
            else if (ch >= 'ㄱ' && ch <= 'ㅣ')
            {
                KeyboardLogPrint.w("getBackCodeArray 2");
                int cIndex = (ch - 'ㄱ');

                int cFirst = COMBINED_ARRAY.CODE_FIRST[cIndex];
                int cSecond = COMBINED_ARRAY.CODE_SECOND[cIndex];
                int cThird = COMBINED_ARRAY.CODE_THIRD[cIndex];
                int cForth = COMBINED_ARRAY.CODE_FORTH[cIndex];
                int cFifth = COMBINED_ARRAY.CODE_FIFTH[cIndex];

                if ( cFirst != 0 )
                    codeArray.add(cFirst);
                if ( cSecond != 0 )
                    codeArray.add(cSecond);
                if ( cThird != 0 )
                    codeArray.add(cThird);
                if ( cForth != 0 )
                    codeArray.add(cForth);
                if ( cFifth != 0 )
                    codeArray.add(cFifth);

            }
            else if (ch == InputTables.DotCode )
                codeArray.add(122);
            else if ( ch == InputTables.DoubleDotCode )
            {
                codeArray.add(122);
                codeArray.add(122);
            }
        }
        return codeArray;
    }

    public static boolean isCompletedChar(String text)
    {
        if (text == null) { return false; }
        // StringBuilder의 capacity가 0으로 등록되는 것 방지.
        if (text.length() == 0) { return false; }
        boolean isCompletedChar = false;
        for (char ch : text.toCharArray())
        {
            KeyboardLogPrint.w("getBackCodeArray ch val :: " + Character.toString(ch));
            if (ch >= '가' && ch <= '힣')
            {
                isCompletedChar = true;
            }
            else
            {
                isCompletedChar = false;
            }
        }

        return isCompletedChar;
    }
}
