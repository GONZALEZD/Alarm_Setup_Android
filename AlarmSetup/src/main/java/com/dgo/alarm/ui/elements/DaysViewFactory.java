package com.dgo.alarm.ui.elements;

import android.content.Context;
import android.util.Pair;

import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.data.DaysOfTheWeek;
import com.dgo.alarm.io.DBManager;
import com.dgo.alarm.utilities.AlarmIcons;
import com.dgo.alarm.utilities.AlarmIcons.TYPE;
import com.dgo.alarm.utilities.AlarmUtility;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DaysViewFactory {

	public static DayViewList getDayViewList(Context c, Calendar month){
		final Calendar now = GregorianCalendar.getInstance();
		DBManager db = DBManager.getInstance(c);
		AlarmIcons icons = AlarmIcons.getInstance(c);
		Pair<Calendar, Calendar> interval = getInterval(month);
		Calendar from = interval.first;
		Calendar to = interval.second;
		List<Alarm> alarms = db.getAlarms(from, to);
		DayViewList list = new DayViewList();
		OverviewDay day = null;
		for(int i = from.get(Calendar.DAY_OF_MONTH); i<= to.get(Calendar.DAY_OF_MONTH); i++){
			int nbActiveOneShot = 0;
			int nbActiveRecurrent = 0;
			day = new OverviewDay(c);
			Calendar dataTime = (Calendar)from.clone();
			dataTime.set(Calendar.DAY_OF_MONTH, i);
			day.setDay(dataTime);
			for(Alarm a : alarms){
				switch (a.getType()) {
				case ONE_SHOT:
					if(a.getFirstTime().get(Calendar.DAY_OF_MONTH) == dataTime.get(Calendar.DAY_OF_MONTH)){
						day.addAlarm(a);
                        if(AlarmUtility.isDisabled(a, dataTime) == false) {
                            nbActiveOneShot += 1;
                        }
					}
					break;

				case RECURRENT:
					DaysOfTheWeek e = DaysOfTheWeek.fromCalendarDay(dataTime.get(Calendar.DAY_OF_WEEK));
					for(int i2=0; i2<a.getDays().length; i2++){
						if(e.equals(a.getDays()[i2]) && day.getCurrentDay().before(now) == false){
							day.addAlarm(a);
                            if(AlarmUtility.isDisabled(a, dataTime) == false) {
                                nbActiveRecurrent += 1;
                            }
							break;
						}
					}
					break;

				default:
					break;
				}
			}
			// set day background
			if(nbActiveOneShot!= 0 && nbActiveRecurrent != 0){
				day.setBackground(icons.get(TYPE.ROUND_BOTH));
			}
			else if(nbActiveOneShot != 0){
				day.setBackground(icons.get(TYPE.ROUND_ONE_SHOT));
			}
			else if(nbActiveRecurrent != 0){
				day.setBackground(icons.get(TYPE.ROUND_RECURRENT));
			}
			else{
				day.setBackground(icons.get(TYPE.ROUND_NONE));
			}
			// add day to the list
			list.add(day);
		}
		return list;
	}
	
	private static Pair<Calendar, Calendar> getInterval(Calendar month){
		Calendar from = (Calendar) month.clone();
//		boolean isValidMonth = (month >= Calendar.JANUARY && month <= Calendar.DECEMBER);
//		if(isValidMonth){
//			from.set(Calendar.MONTH, month);
//		}
		from.set(Calendar.DAY_OF_MONTH, 1);
//		from.set(GregorianCalendar.HOUR_OF_DAY, 0);
//		from.set(GregorianCalendar.MINUTE, 0);
//		from.set(GregorianCalendar.SECOND, 0);
//		from.set(GregorianCalendar.MILLISECOND, 0);
		
		Calendar to = (Calendar) from.clone();
//		if(isValidMonth){
//			to.set(Calendar.MONTH, month);
//		}
		to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH));
		to.set(Calendar.HOUR_OF_DAY, to.getActualMaximum(Calendar.HOUR_OF_DAY));
		to.set(Calendar.MINUTE, to.getActualMaximum(Calendar.MINUTE));
		to.set(Calendar.SECOND, to.getActualMaximum(Calendar.SECOND));
		to.set(Calendar.MILLISECOND, to.getActualMaximum(Calendar.MILLISECOND));
		
		return new Pair<Calendar, Calendar>(from, to);
	}
}
