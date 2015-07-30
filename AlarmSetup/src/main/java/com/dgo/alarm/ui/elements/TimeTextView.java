package com.dgo.alarm.ui.elements;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeTextView extends TextView {
	private int mHours;
	private int mMinuts;
	private Calendar mDate;
	public TimeTextView(Context c, AttributeSet attrs){
		super(c, attrs);
		mHours = 0;
		mMinuts = 0;
		mDate = GregorianCalendar.getInstance();
		updateText();
		
	}
	
	public void setDate(Calendar date){
		if(date == null){
			mDate = GregorianCalendar.getInstance();
		}
		else{
			mDate = date;
		}
	}
	
	public void setHour(int hour){
		mHours = hour;
		updateText();
	}
	
	public void setMinut(int minut){
		mMinuts = minut;
		updateText();
	}
	
	private void updateText(){
		String str = "";
		str += mHours<10?"0"+mHours:mHours;
		str += ":";
		str += mMinuts<10?"0"+mMinuts:mMinuts;
		setText(str);
	}
	
	public int getHours(){
		return mHours;
	}
	public int getMinuts(){
		return mMinuts;
	}
	
	public Calendar computeTime(Calendar date){
		if(mDate == null){
			mDate = GregorianCalendar.getInstance();
		}
		else {
			mDate = date;
		}
		Calendar copy = (Calendar)mDate.clone();
		copy.set(Calendar.HOUR_OF_DAY, mHours);
		copy.set(Calendar.MINUTE, mMinuts);
		copy.set(Calendar.SECOND, 0);
		copy.set(Calendar.MILLISECOND, 0);
		if(copy.compareTo(mDate) <= 0 && mDate == null){
			//Today Set time passed, count to tomorrow
			copy.add(Calendar.DATE, 1);
		}
		return copy;
	}

}
