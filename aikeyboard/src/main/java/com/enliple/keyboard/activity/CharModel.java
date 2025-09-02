package com.enliple.keyboard.activity;

public class CharModel
{
    private char mChar = (char) 0; // 조합자음
    private char mRotatedChar = (char) 0; // 순환자음 ex: ㄱ -> ㅋ -> ㄲ -> ㄱ
    private int mMode = -1;

    public void setChar(char c)
    {
        mChar = c;
    }

    public char getChar()
    {
        return mChar;
    }

    public void setRotatedChar(char c)
    {
        mRotatedChar = c;
    }

    public char getRotatedChar()
    {
        return mRotatedChar;
    }

    public void setMode(int mode)
    {
        mMode = mode;
    }

    public int getMode()
    {
        return mMode;
    }
}
