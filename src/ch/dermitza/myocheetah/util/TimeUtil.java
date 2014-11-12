package ch.dermitza.myocheetah.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author K. Dermitzakis
 * @email dermitza <AT> ifi.uzh.ch
 * @filename TimeUtil.java
 * @version 0.1
 *
 * Created: 16/12/2008 rev 0.1 dermitza
 * Edited :
 *
 *
 */
public class TimeUtil {
    
    private static Calendar rightNow;
    private static SimpleDateFormat format;
    
    private TimeUtil(){}
    
    public static String getTimeStamp(String dateFormat){
        rightNow = Calendar.getInstance();
        format = new SimpleDateFormat(dateFormat);
        return format.format(rightNow.getTime());
    }
    
    public static String getExtendedTimeStamp(){
        rightNow = Calendar.getInstance();
        String timestamp = "["+
                rightNow.get(Calendar.DAY_OF_MONTH) +"/"+
                rightNow.get(Calendar.MONTH)        +"/"+
                rightNow.get(Calendar.YEAR)         +"]";
        
        return timestamp+getTimeStamp();
    }

    public static String getTimeStamp(){
        rightNow = Calendar.getInstance();
        String timestamp = "[" + 
                rightNow.get(Calendar.HOUR_OF_DAY) +":"+ 
                rightNow.get(Calendar.MINUTE)      +":"+
                rightNow.get(Calendar.SECOND)      +"]";
        return timestamp;
    }
}
