package com.pay.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class getTime {
	private int weeks = 0;
	  
	  public static Date weekEndTime() throws Exception{
		  Calendar nowTime = Calendar.getInstance();
		  nowTime.setTime(weekStateTime());
		  nowTime.add(Calendar.DATE,-1);//前天
			Calendar cal = Calendar.getInstance();
			cal.setTime(nowTime.getTime());
			int d = 0;
			if(cal.get(Calendar.DAY_OF_WEEK)==1){
				d = -6;
			}else{
				d = 2-cal.get(Calendar.DAY_OF_WEEK);
			}
			cal.add(Calendar.DAY_OF_WEEK, d);
			cal.add(Calendar.DAY_OF_WEEK, 6);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf2.parse(sdf.format(cal.getTime())+" 23:59:59");
		}
	  
	  public static Date weekStateTime2() throws ParseException{
		  
		  Calendar nowTime = Calendar.getInstance();
		  nowTime.setTime(weekStateTime());
		  nowTime.add(Calendar.DATE,-1);//前天
		  
			Calendar cal = Calendar.getInstance();
			cal.setTime(nowTime.getTime());
			int d = 0;
			if(cal.get(Calendar.DAY_OF_WEEK)==1){
				d = -6;
			}else{
				d = 2-cal.get(Calendar.DAY_OF_WEEK);
			}
			cal.add(Calendar.DAY_OF_WEEK, d);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf2.parse(sdf.format(cal.getTime())+" 00:00:00");
		}
	  
	  private static Date weekStateTime() throws ParseException{
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int d = 0;
			if(cal.get(Calendar.DAY_OF_WEEK)==1){
				d = -6;
			}else{
				d = 2-cal.get(Calendar.DAY_OF_WEEK);
			}
			cal.add(Calendar.DAY_OF_WEEK, d);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf2.parse(sdf.format(cal.getTime())+" 00:00:00");
		}


	  //根据时间得到周几
	  public static String getWeek(String sdate)
	  {
	    Date date = strToDate(sdate);


	    Calendar c = Calendar.getInstance();


	    c.setTime(date);


	    return new SimpleDateFormat("EEEE").format(c.getTime());
	  }

	  //将yyyy-MM-dd格式字符串转成Date
	  public static Date strToDate(String strDate)
	  {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


	    ParsePosition pos = new ParsePosition(0);


	    Date strtodate = formatter.parse(strDate, pos);


	    return strtodate;
	  }

      //获取两个时间间隔的天数
	  public static long getDays(String date1, String date2)
	  {
	    if ((date1 == null) || (date1.equals("")))
	    {
	      return 0L;
	    }
	    if ((date2 == null) || (date2.equals("")))
	    {
	      return 0L;
	    }


	    SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");


	    Date date = null;


	    Date mydate = null;
	    try
	    {
	      date = myFormatter.parse(date1);


	      mydate = myFormatter.parse(date2);
	    }
	    catch (Exception localException)
	    {
	    }


	    long day = (date.getTime() - mydate.getTime()) / 86400000L;


	    return day;
	  }


	  public String getyd()
	  {
	    Calendar cal = Calendar.getInstance();
	    cal.add(5, -1);
	    String yesterday = new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
	    return yesterday;
	  }


	  public static Date getDefaultDay() throws Exception
	  {
	    String str = "";


	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	    Calendar lastDate = Calendar.getInstance();


	    lastDate.set(5, 1);


	    lastDate.add(2, 1);


	    lastDate.add(5, -1);


	    str = sdf.format(lastDate.getTime());


	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return sdf2.parse(str+" 23:59:59") ;
	  }


	  public static Date getPreviousMonthFirst() throws Exception
	  {
	    String str = "";


	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	    Calendar lastDate = Calendar.getInstance();


	    lastDate.set(5, 1);


	    lastDate.add(2, -1);


	    str = sdf.format(lastDate.getTime());

	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return sdf2.parse(str+" 00:00:00") ;
	  }


	  public static Date getFirstDayOfMonth() throws Exception
	  {
	    String str = "";


	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	    Calendar lastDate = Calendar.getInstance();


	    lastDate.set(5, 1);


	    str = sdf.format(lastDate.getTime());


	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return sdf2.parse(str+" 00:00:00") ;
	  }


	  public String getCurrentWeekday()
	  {
	    this.weeks = 0;


	    int mondayPlus = getMondayPlus();


	    GregorianCalendar currentDate = new GregorianCalendar();


	    currentDate.add(5, mondayPlus + 6);


	    Date monday = currentDate.getTime();


	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	    String preMonday = sdf.format(monday);


	    return preMonday;
	  }


	  public String getNowTime(String dateformat)
	  {
	    Date now = new Date();


	    SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);


	    String hehe = dateFormat.format(now);


	    return hehe;
	  }


	  private int getMondayPlus()
	  {
	    Calendar cd = Calendar.getInstance();


	    int dayOfWeek = cd.get(7) - 1;


	    if (dayOfWeek == 1)
	    {
	      return 0;
	    }


	    return (1 - dayOfWeek);
	  }


	  public String getMondayOFWeek()
	  {
	    this.weeks = 0;


	    int mondayPlus = getMondayPlus();


	    GregorianCalendar currentDate = new GregorianCalendar();


	    currentDate.add(5, mondayPlus);


	    Date monday = currentDate.getTime();


	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	    String preMonday = sdf.format(monday);


	    return preMonday;
	  }


	  public String getPreviousWeekSunday()
	  {
	    this.weeks = 0;


	    this.weeks -= 1;


	    int mondayPlus = getMondayPlus();


	    GregorianCalendar currentDate = new GregorianCalendar();


	    currentDate.add(5, mondayPlus + this.weeks);


	    Date monday = currentDate.getTime();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String preMonday = sdf.format(monday);


	    return preMonday;
	  }


	  public String getPreviousWeekday()
	  {
	    this.weeks -= 1;


	    int mondayPlus = getMondayPlus();


	    GregorianCalendar currentDate = new GregorianCalendar();


	    currentDate.add(5, mondayPlus + 7 * this.weeks);


	    Date monday = currentDate.getTime();


	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	    String preMonday = sdf.format(monday);


	    return preMonday;
	  }


	  public static Date getPreviousMonthEnd() throws Exception
	  {
	    String str = "";
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar lastDate = Calendar.getInstance();
	    lastDate.add(2, -1);
	    lastDate.set(5, 1);
	    lastDate.roll(5, -1);
	    str = sdf.format(lastDate.getTime());
	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return sdf2.parse(str+" 23:59:59") ;
	  }
}
