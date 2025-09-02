/*
 * halbae87: this project is created from Soft Keyboard Sample source
 *  but this part is my original source
 */

package com.enliple.keyboard.activity;

public final class InputTables {
	/** 한글 초성 갯수 */
	public static final int NUM_OF_FIRST = 19;
	/** 한글 중성 갯수 */
	public static final int NUM_OF_MIDDLE = 21;
	/** 한글 종성 갯수 */
	public static final int NUM_OF_LAST = 27;
	public static final int NUM_OF_LAST_INDEX = NUM_OF_LAST + 1; // add 1 for non-last consonant added characters
	
	public static final int KEYSTATE_NONE = 0;
	
	public static final int KEYSTATE_SHIFT = 1;
	public static final int KEYSTATE_SHIFT_LEFT = 1;
	public static final int KEYSTATE_SHIFT_RIGHT = 2;
	public static final int KEYSTATE_SHIFT_MASK = 3;
	
	public static final int KEYSTATE_ALT = 4;
	public static final int KEYSTATE_ALT_LEFT = 4;
	public static final int KEYSTATE_ALT_RIGHT = 8;
	public static final int KEYSTATE_ALT_MASK = 12;
	
	public static final int KEYSTATE_CTRL = 16;
	public static final int KEYSTATE_CTRL_LEFT = 16;
	public static final int KEYSTATE_CTRL_RIGHT = 32;
	public static final int KEYSTATE_CTRL_MASK = 48;
	
	public static final int KEYSTATE_FN = 64;	// just for future usage...
	
	// jkchoi
	public static final int KEYSTATE_CAPS_LOCK = 70;
	
	public static final char BACK_SPACE = 0x8;

	// formula to get HANGUL_CODE by composing consonants and vowel indexes
	// HANGUL_CODE = HANGUL_START + iFirst*NUM_OF_MIDDLE*NUM_OF_LAST_INDEX + iMiddle*NUM_OF_LAST_INDEX + iLast
	
	// getting the first consonant index from code
	// iFirst = (vCode - HANGUL_START) / (NUM_OF_MIDDLE * NUM_OF_LAST_INDEX)

	// getting the vowel index from code
	// iMiddle = ((vCode - HANGUL_START) % (NUM_OF_MIDDLE * NUM_OF_LAST_INDEX)) / NUM_OF_LAST_INDEX

	// getting the last consonant index from code
	// iLast = (vCode - HANGUL_START) % NUM_OF_LAST_INDEX

	// 한글쿼티 자판 AlpabetIndex
	// ㅂ	ㅈ	ㄷ	ㄱ	ㅅ	ㅛ	ㅕ	ㅑ	ㅐ	ㅔ
	// 16	22	 4	17	19	24  20   8  14  15

	// ㅁ	ㄴ	ㅇ	ㄹ	ㅎ	ㅗ	ㅓ	ㅏ	ㅣ
	// 0    18  3   5   6   7   9   10  11

	// ㅋ	ㅌ	ㅊ	ㅍ	ㅠ	ㅜ	ㅡ
	// 25   23  2   21  1   13  12
													// a	   b       c      d       e        f      g       h       i       j       k       l       m       n       o       p       q       r       s       t       u       v       w       x       y       z
													// ㅁ      ㅠ      ㅊ     ㅇ      ㄷ       ㄹ      ㅎ
	public static final class NormalKeyMap {
		public static final char Code[] 		= {0x3141,	0x3160,	0x314A,	0x3147,	0x3137,	0x3139,	0x314E,	0x3157,	0x3151,	0x3153,	0x314F,	0x3163,	0x3161,	0x315C,	0x3150,	0x3154,	0x3142,	0x3131,	0x3134,	0x3145,	0x3155,	0x314D,	0x3148,	0x314C,	0x315B,	0x314B, 0x314B, 0x3132, 0x3138, 0x3143, 0x3146, 0x3149};
		public static final int FirstIndex[] 	= {6,		-1,		14,		11,		3,		5,		18,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		7,		0,		2,		9,		-1,		17,		12,		16,		-1,		15};
		public static final int MiddleIndex[] 	= {-1,		17,		-1,		-1,		-1,		-1,		-1,		8,		2,		4,		0,		20,		18,		13,		1,		5,		-1,		-1,		-1,		-1,		6,		-1,		-1,		-1,		12,		-1};
		public static final int LastIndex[]		= {16,		-1,		23,		21,		7,		8,		27,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		17,		1,		4,		19,		-1,		26,		22,		25,		-1,		24};
	}

	public static final class ShiftedKeyMap {
		public static final char Code[] 		= {0x3141,	0x3160,	0x314A,	0x3147,	0x3138,	0x3139,	0x314E,	0x3157,	0x3151,	0x3153,	0x314F,	0x3163,	0x3161,	0x315C,	0x3152,	0x3156,	0x3143,	0x3132,	0x3134,	0x3146,	0x3155,	0x314D,	0x3149,	0x314C,	0x315B,	0x314B};
		public static final int FirstIndex[] 	= {6,		-1,		14,		11,		4,		5,		18,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		8,		1,		2,		10,		-1,		17,		13,		16,		-1,		15};
		public static final int MiddleIndex[] 	= {-1,		17,		-1,		-1,		-1,		-1,		-1,		8,		2,		4,		0,		20,		18,		13,		3,		7,		-1,		-1,		-1,		-1,		6,		-1,		-1,		-1,		12,		-1};
		public static final int LastIndex[]		= {16,		-1,		23,		21,		-1,		8,		27,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		2,		4,		20,		-1,		26,		-1,		25,		-1,		24};
	}

	public static final char MiddleDot = 0x00B7; // ·

	public static final char DotCode = 0x318D; // ·
//	public static final char DoubleDotCode = 0x00A8; //‥
//	public static final char DotCode = 0x119E; // ·
	public static final char DoubleDotCode = 0x11A2; //‥
	public static final char CODE_ADD = 0x119E;
	public static final char CODE_DOUBLE = 0x11A2;
//											ㅣ		·		ㅡ
	public static final char CVowel[] = {0x3163, 0x318D, 0x3161};

	// 한글 초성
	/*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
	//													0		  1		   2       3       4      5       6        7      8       9       10      11      12      13      14     15       16      17     18
	//                                                  ㄱ        ㄲ       ㄴ      ㄷ      ㄸ      ㄹ      ㅁ       ㅂ      ㅃ      ㅅ       ㅆ      ㅇ      ㅈ      ㅉ      ㅊ      ㅋ       ㅌ      ㅍ      ㅎ
	public static final char FirstConsonantCodes[] = {0x3131,	0x3132,	0x3134,	0x3137,	0x3138,	0x3139,	0x3141,	0x3142,	0x3143,	0x3145,	0x3146,	0x3147,	0x3148,	0x3149,	0x314A,	0x314B,	0x314C,	0x314D,	0x314E };

	/**
	 * iLast 값
	 * ㄱ, ㄲ 등의 iLast 값 : ㄱ의 이전 상태 종성은 아무것도 없음(ex 각 ->back key -> 가, '가'의 종성은 아무값이 없음). 따라서 position 은 -1
	 * ㄳ의 iLast 값 : ㄳ의 이전 상태 종성은 ㄱ(ex 넋 -> back key -> 넉, '넉'의 종성은 ㄱ). 이전상태 종성 'ㄱ'의 index(position)값은 1
	 * ㅀ의 iLast 값 : ㅀ의 이전 상태 종성은 ㄹ(ex 랋 -> back key -> 랄, '랄'의 종성은 ㄹ). 이전상태 종성 'ㄹ'의 index(position)값은 8
	 */

	/**
	 * iFirst 값
	 * ㄱ, ㄲ 등의 iFirst 값 : 조합자음의 두번째 자음의 position 값은 없음. ㄱ, ㄲ 등은 조합자음이 아니므로 -1
	 * ㄳ의 iFirst 값 : 조합자음 ㄳ의 두번째 자음은 ㅅ. ㅅ의 초성의 position 값은 9
	 * ㄾ의 iFirst 값 : 조합자음 ㄾ의 두번째 자음은 ㅌ. ㅌ의 초성의 position 값은 16
	 */
	// 한글 종성
	/*X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
	public static final class LastConsonants {
		//                                   0        1       2        3      4       5       6       7       8       9       10      11      12      13      14       15     16      17      18      19      20      21      22      23      24      25      26     27
		//                                   E        ㄱ      ㄲ       ㄳ      ㄴ      ㄵ      ㄶ       ㄷ      ㄹ      ㄺ       ㄻ       ㄼ      ㄽ      ㄾ       ㄿ       ㅀ      ㅁ      ㅂ      ㅄ       ㅅ      ㅆ      ㅇ      ㅈ      ㅊ       ㅋ       ㅌ      ㅍ      ㅎ
		public static final char Code[]  = {0x0,	0x3131,	0x3132,	0x3133,	0x3134,	0x3135,	0x3136,	0x3137,	0x3139,	0x313A,	0x313B,	0x313C,	0x313D,	0x313E,	0x313F,	0x3140,	0x3141,	0x3142,	0x3144,	0x3145,	0x3146,	0x3147,	0x3148,	0x314A,	0x314B,	0x314C,	0x314D,	0x314E};
		public static final int iLast[]	 = {-1,		-1,		-1,		1,		-1,		4,		4,		-1,		-1,		8,		8,		8,		8,		8,		8,		8,		-1,		-1,		17,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1};
		public static final int iFirst[] = {-1,		-1,		-1,		9,		-1,		12,		18,		-1,		-1,		0,		6,		7,		9,		16,		17,		18,		-1,		-1,		9,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1};
	}

	/**
	 * iMiddle 값
	 * ㅏ, ㅗ, ㅑ, ㅐ 등 중성이 조합이 아닐 경우 -1
	 * ㅘ, ㅙ, ㅚ : 조합중성의 첫번째 값 ㅗ의 index 값 : 8
	 * ㅟ, ㅞ, ㅟ : 조합중성의 첫번째 값 ㅜ의 index 값 : 13
	 */
	// 한글 중성
	/*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
	public static final class Vowels {
		//									  0       1        2      3        4      5       6      7        8        9      10      11      12      13     14       15       16      17     18      19      20
		//                                    ㅏ      ㅐ       ㅑ      ㅒ       ㅓ     ㅔ      ㅕ      ㅖ       ㅗ       ㅘ      ㅙ      ㅚ      ㅛ       ㅜ     ㅝ       ㅞ       ㅟ      ㅠ      ㅡ      ㅢ       ㅣ
		public static final char Code[]  = {0x314F,	0x3150,	0x3151,	0x3152,	0x3153,	0x3154,	0x3155,	0x3156,	0x3157,	0x3158,	0x3159,	0x315A,	0x315B,	0x315C,	0x315D,	0x315E,	0x315F,	0x3160,	0x3161,	0x3162,	0x3163};
		public static final int iMiddle[] = {-1,	-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		8,		8,		8,		-1,		-1,		13,		13,		13,		-1,		-1,		18,		-1};
		public static final int iLMiddle[]= {-1,	-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		0,		1,		20,		-1,		-1,		4,		5,		20,		-1,		-1,		20,		-1};
	}
}

