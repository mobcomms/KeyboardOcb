package com.enliple.keyboard.mobonAD;

import com.enliple.keyboard.ui.common.LogPrint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateManagers
{


	public static String getDate()
	{
		String date = "";
		try
		{
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
			date = formater.format(new Date());
		}
		catch (Exception e)
		{
			LogPrint.e("getDate() Exception! : " + e.getLocalizedMessage());
			// e.toString();
		}

		return date;
	}

}
