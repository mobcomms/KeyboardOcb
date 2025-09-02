package com.enliple.keyboard.automata;

import com.enliple.keyboard.common.KeyboardLogPrint;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-02-16.
 */

public class Automata {
    public Automata()
    {
    }

    public int FinishAutomataWithoutInput()
    {
        int ret = 0;
        return ret;
    }

    public boolean IsKoreanMode()
    {
        return true;
    }

    public String GetCompositionString()
    {
        return "";
    }

    public int DoAutomata(char code, int KeyState)
    {
        return 0;
    }

    public int DoBackSpace()
    {
        return 0;
    }

    public void ToggleMode()
    {

    }

    public int GetState()
    {
        return 0;
    }

    public String GetCompleteString()
    {
        return "";
    }

    public int getStrLength()
    {
        return 0;
    }

    public void setInitState()
    {
        KeyboardLogPrint.d("setInitState automata");
    }

    public String DecomposeConsonant(ArrayList<Integer> keys){return "";}

    public ArrayList<Integer> getState(String str)
    {
        return null;
    }

    public void setAutomata(String complete, String composition, int state)
    {

    }

    public ArrayList<String> getAutomataValue()
    {
        return null;
    }

    public void setAutomataValue (ArrayList<String> val )
    {}
}
