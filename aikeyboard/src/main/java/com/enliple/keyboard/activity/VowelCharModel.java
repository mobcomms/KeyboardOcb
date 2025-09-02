package com.enliple.keyboard.activity;

/**
 * Created by Administrator on 2017-02-06.
 */

public class VowelCharModel
{
    private char mVowelChar = (char)0;
    private boolean mIsCombinedVowel = false;

    public void setVowelChar(char vowelChar)
    {
        mVowelChar = vowelChar;
    }

    public void setCombinedState(boolean combinedState)
    {
        mIsCombinedVowel = combinedState;
    }

    public char getVowelChar()
    {
        return mVowelChar;
    }

    public boolean getCombinedState()
    {
        return mIsCombinedVowel;
    }
}
