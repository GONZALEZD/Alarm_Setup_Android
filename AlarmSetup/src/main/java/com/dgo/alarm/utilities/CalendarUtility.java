package com.dgo.alarm.utilities;

import java.util.Calendar;

/**
 * Created by david.gonzalez on 24/07/2015.
 */
public class CalendarUtility {
    public static boolean areSameDay(Calendar c1, Calendar c2){
        if(c1 == null || c2 == null){
            return false;
        }
        int y1, y2, d1, d2;
        y1 = c1.get(Calendar.YEAR);
        y2 = c2.get(Calendar.YEAR);
        d1 = c1.get(Calendar.DAY_OF_YEAR);
        d2 = c2.get(Calendar.DAY_OF_YEAR);
        if(y1 == y2 && d1 == d2){
            return true;
        }
        return false;
    }

    public static boolean areSameHoursAndMinuts(Calendar c1, Calendar c2){
        if(c1 == null || c2 == null){
            return false;
        }
        int t1, t2;
        t1 = c1.get(Calendar.HOUR_OF_DAY);
        t2 = c2.get(Calendar.HOUR_OF_DAY);
        t1 = t1 * c1.get(Calendar.MINUTE);
        t2 = t2 * c2.get(Calendar.MINUTE);

        if(t1 == t2){
            return true;
        }
        return false;
    }

}
