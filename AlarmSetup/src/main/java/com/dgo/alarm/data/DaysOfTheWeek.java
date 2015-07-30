package com.dgo.alarm.data;

import com.dgo.alarm.R;

import java.util.Calendar;

public enum DaysOfTheWeek {

	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY,
	SUNDAY;

	public String shortName(){
		return name().substring(0, 3);
	}

	public int toInt(){
		switch(this){
		case MONDAY:
			return 0;
		case TUESDAY:
			return 1;
		case WEDNESDAY:
			return 2;
		case THURSDAY:
			return 3;
		case FRIDAY:
			return 4;
		case SATURDAY:
			return 5;
		case SUNDAY:
			return 6;
		}
		return -1;
	}

	public static DaysOfTheWeek fromInt(int day){
		switch(day){
		case 0:
			return MONDAY;
		case 1:
			return TUESDAY;
		case 2:
			return WEDNESDAY;
		case 3:
			return THURSDAY;
		case 4:
			return FRIDAY;
		case 5:
			return SATURDAY;
		case 6:
			return SUNDAY;
		}
		return null;
	}

	public static DaysOfTheWeek convert(int resourceID){
		switch(resourceID){
		case R.string.monday_name:
			return MONDAY;
		case R.string.tuesday_name:
			return TUESDAY;
		case R.string.wednesday_name:
			return WEDNESDAY;
		case R.string.thursday_name:
			return THURSDAY;
		case R.string.friday_name:
			return FRIDAY;
		case R.string.saturday_name:
			return SATURDAY;
		case R.string.sunday_name:
			return SUNDAY;
		}

		return null;
	}
	
	public static DaysOfTheWeek fromCalendarDay(int calendarDay){
		switch(calendarDay){
		case Calendar.SUNDAY:
			return DaysOfTheWeek.SUNDAY;
		case Calendar.MONDAY:
			return DaysOfTheWeek.MONDAY;
		case Calendar.TUESDAY:
			return DaysOfTheWeek.TUESDAY;
		case Calendar.WEDNESDAY:
			return DaysOfTheWeek.WEDNESDAY;
		case Calendar.THURSDAY:
			return DaysOfTheWeek.THURSDAY;
		case Calendar.FRIDAY:
			return DaysOfTheWeek.FRIDAY;
		case Calendar.SATURDAY:
			return DaysOfTheWeek.SATURDAY;
		default:
			break;
		}
		return null;
	}

	public int toCalendarInt() {
		switch (this) {
		case SUNDAY:
			return Calendar.SUNDAY;
		case MONDAY:
			return Calendar.MONDAY;
		case TUESDAY:
			return Calendar.TUESDAY;
		case WEDNESDAY:
			return Calendar.WEDNESDAY;
		case THURSDAY:
			return Calendar.THURSDAY;
		case FRIDAY:
			return Calendar.FRIDAY;
		case SATURDAY:
			return Calendar.SATURDAY;

		default:
			break;
		}
		return -1;
	}
	
	public static String toString(DaysOfTheWeek[] days){
		String rez = "";
		if(days == null){
			return "";
		}
		for(int i=0; i<days.length; i++){
			rez += days[i].shortName();
			if(i != (days.length -1)){
				rez +=", ";
			}
		}
		return rez;
	}
}
