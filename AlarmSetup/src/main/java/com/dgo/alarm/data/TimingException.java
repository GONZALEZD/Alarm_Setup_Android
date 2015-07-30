package com.dgo.alarm.data;

import com.dgo.alarm.io.IDBElement;

import java.util.Calendar;

public class TimingException extends IDBElement{
	
	private Calendar newTime;

	private String name;

	public TimingException(Calendar newTime){
		this.newTime = newTime;
	}
	
	public Calendar getNewTime() {
		return newTime;
	}
	public void setNewTime(Calendar newTime) {
		this.newTime = newTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void updateParameters(TimingException lastVersion) {
		if(lastVersion != null){
			setName(lastVersion.getName());
			setNewTime(lastVersion.getNewTime());
		}
	}

    @Override
    public String toString() {
        String buffer = String.format(
                "%s [%d] : {name=%s, newTime=%s}",
                name, getID(), name, newTime.getTimeInMillis());
        return buffer;
    }
}
