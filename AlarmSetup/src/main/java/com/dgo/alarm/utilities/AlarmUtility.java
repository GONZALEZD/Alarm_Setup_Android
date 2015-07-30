package com.dgo.alarm.utilities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.data.AlarmType;
import com.dgo.alarm.data.TimingException;
import com.dgo.alarm.io.DBException;
import com.dgo.alarm.io.DBManager;

import java.util.Calendar;

/**
 * Created by david.gonzalez on 27/07/2015.
 */
public class AlarmUtility {

    private static final String TAG = ""+AlarmUtility.class.getSimpleName();

    public static boolean isDisabled(Alarm a, Calendar date){
        if(a == null){
            return true;
        }
        // No given date or One shot or Recurrent without timing exception cases
        if(date == null || a.getType() == AlarmType.ONE_SHOT || a.hasTimingException() == false){
            return a.isUserDisabled();
        }
        // recurrent alarm case containing timing exceptions
        for(TimingException t : a.getTimingExceptions()){
            if (t != null && CalendarUtility.areSameDay(date, t.getNewTime())){
                return true;
            }
        }
        return a.isUserDisabled();

    }

    public static void setDisabled(@NonNull DBManager database,@NonNull Alarm a, Calendar currentDate, boolean isDisabled){
        if(a == null || database == null){
            return;
        }
        if(a.getType() == AlarmType.ONE_SHOT){
            a.setUserDisabled(isDisabled);
            // update alarm
            database.updateAlarm(a);
        }
        // recurrent case
        else if(currentDate == null){
            // general case : disable/enable all the alarm
            // (will remove all timing exceptions)
            a.setUserDisabled(isDisabled);
            // update alarm
            database.updateAlarm(a);
            //delete timing exceptions
            if(a.hasTimingException()) {
                database.deleteTimingExceptions(a);
            }
        }
        else{
            // check which timing exception to disable/enable
            if(a.hasTimingException()){
                TimingException found = null;
                for(TimingException t : a.getTimingExceptions()){
                    if(t != null &&
                            CalendarUtility.areSameDay(currentDate, t.getNewTime())
                            ){
                        found = t;
                        break;
                    }
                }
                if(found != null && isDisabled == false){
                    // remove timing
                    try {
                        database.deleteTimingException(a, found);
                    } catch (DBException e) {
                        e.printStackTrace();
                        Log.e(TAG,
                                "Unable to delete a timing exception for alarm " + a.toString() + " ("+found.toString()+") :" + e.toString());
                    }
                }
                else{
                    // no timing exception found
                    disableRecurrentAlarmWithoutTimingException(database, a, currentDate, isDisabled);
                }
            }
            else{
                // no timing exception
                disableRecurrentAlarmWithoutTimingException(database, a, currentDate, isDisabled);
            }
        }
    }

    private static void disableRecurrentAlarmWithoutTimingException(DBManager database, Alarm a, Calendar currentDate, boolean isDisabled){
        if(isDisabled){
            // add a timing exception
            try {
                database.addTimingException(a, currentDate);
            } catch (DBException e) {
                e.printStackTrace();
                Log.e(TAG,
                        "Unable to add a timing exception for alarm " + a.toString() + " :"+e.toString());
            }
        }
        else{
            // disable all the alarm
            a.setUserDisabled(isDisabled);
            // update alarm
            database.updateAlarm(a);
        }
    }
}
