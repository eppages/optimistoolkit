/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Date_and_Time {

	public static String getDate_Time()
	{
		Calendar cal = Calendar.getInstance();
		
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		
		return year+
				"\\"+changeIn2Digits(month)+
				"\\"+changeIn2Digits(day)+
				" "+getCurrentTime().replace(".", ":");
		
	}//getDate_Time()

	public static String changeIn2Digits(int value)
	{
		if (value<10) return "0"+Integer.toString(value);
		
		return Integer.toString(value);
		
	}//changeIn2Digits(int value)
	
	public static String getDate()
	{
		Date dateNow = new Date ();
		 
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        //SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("MMddyyyy");
 
        StringBuilder nowYYYYMMDD = new StringBuilder( dateformatYYYYMMDD.format( dateNow ) );
        //StringBuilder nowMMDDYYYY = new StringBuilder( dateformatMMDDYYYY.format( dateNow ) );
 
    
        return nowYYYYMMDD.toString();
        
	}//getDate()
	
	
	public static String getCurrentTime()
	{  
		  Calendar calendar = new GregorianCalendar();
		  String am_pm;
		  
		  if(calendar.get(Calendar.AM_PM) == 0)
			  am_pm = "AM";
		  else
			  am_pm = "PM";
			  
		  int hour = calendar.get(Calendar.HOUR);
		 
		  if(am_pm.contains("PM"))
			  hour+=12;
		    
		  int minute = calendar.get(Calendar.MINUTE);
		  int second = calendar.get(Calendar.SECOND);
		  
		  String Hours=Integer.toString(hour);
		  String Minute=Integer.toString(minute);
		  String Second=Integer.toString(second);
		  
		  if(hour<10)Hours="0"+Hours;
		  if(minute<10)Minute="0"+Minute;
		  if(second<10)Second="0"+Second;
		  
		  String currentTime=Hours+"."+Minute+"."+Second;
		  
		  return currentTime;
		  
	}//getCurrentTime()
	
}//class
