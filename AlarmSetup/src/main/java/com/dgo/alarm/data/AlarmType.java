package com.dgo.alarm.data;

public enum AlarmType {
	ONE_SHOT,
	RECURRENT;
	
	public static AlarmType fromInt(int alarmType){
		switch(alarmType){
		case 0:
			return ONE_SHOT;
		case 1:
			return RECURRENT;
		}
		return null;
	}
	
	public int toInt(){
		switch(this){
		case ONE_SHOT:
			return 0;
		case RECURRENT:
			return 1;
		}
		return -1;
	}
}
