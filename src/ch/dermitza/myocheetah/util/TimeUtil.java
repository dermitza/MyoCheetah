/**
 * This file is part of MyoCheetah. Copyright (C) 2014 K. Dermitzakis
 * <dermitza@gmail.com>
 *
 * MyoCheetah is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * MyoCheetah is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with MyoCheetah. If not, see <http://www.gnu.org/licenses/>.
 */
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
