/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.trecdb.sp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jsubirat
 */
public class DateFormatter {
    public static Date fromCalendarToDate(Calendar cal) throws ParseException {

        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(cal.get(Calendar.MINUTE));
        String second = Integer.toString(cal.get(Calendar.SECOND));
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dat = (Date) format.parse(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
        
        return dat;
    }
    
    /**
     * Input string format: yyyyMMddhhmmss.
     * @param str
     * @return 
     */
    public static Date getDateFromString(String str) throws ParseException {
        String year = str.substring(0, 4);
        String month = str.substring(4, 6);
        String day = str.substring(6, 8);
        String hour = str.substring(8, 10);
        String minute = str.substring(10, 12);
        String second = str.substring(12, 14);
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dat = (Date) format.parse(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
        
        return dat;
    }
}
