package com.dgo.alarm.data;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.dgo.alarm.io.IDBElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@SuppressLint("DefaultLocale")
public class Alarm extends IDBElement{
	private String name;
	private AlarmType type;
	private Uri song;
	private DaysOfTheWeek[] days;
	private Calendar firstTime;
	private boolean isRandomSong;
	private boolean isCrescendoSong;
	private boolean isUserDisabled;
	private boolean isWaveLuminosity;
	
	private List<TimingException> timingExceptions;
	
	public Alarm(String name, AlarmType type, DaysOfTheWeek[] days, Calendar firstTime, Uri song){
		super();
		this.name = name;
		this.type = type;
		this.days = days;
		this.firstTime= firstTime;
		this.song = song;
		this.isRandomSong = false;
		this.isCrescendoSong = false;
		this.isUserDisabled = false;
		this.isWaveLuminosity = false;
		this.timingExceptions = null;
	}
	
	public boolean hasTimingException(){
		return timingExceptions != null && timingExceptions.size()>0;
	}
	
	public List<TimingException> getTimingExceptions(){
		return timingExceptions;
	}
	
	public void setTimingExceptions(List<TimingException> exceptions){
		timingExceptions = exceptions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AlarmType getType() {
		return type;
	}

	public void setType(AlarmType type) {
		this.type = type;
	}

	public Uri getSong() {
		return song;
	}

	public void setSong(Uri song) {
		this.song = song;
	}

	public DaysOfTheWeek[] getDays() {
		return days;
	}

	public void setDays(DaysOfTheWeek[] days) {
		this.days = days;
	}

	public Calendar getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Calendar firstTime) {
		this.firstTime = firstTime;
	}

	public boolean isRandomSong() {
		return isRandomSong;
	}

	public void setRandomSong(boolean isRandomSong) {
		this.isRandomSong = isRandomSong;
	}

	public boolean isCrescendoSong() {
		return isCrescendoSong;
	}

	public void setCrescendoSong(boolean isCrescendoSong) {
		this.isCrescendoSong = isCrescendoSong;
	}

	public boolean isUserDisabled() {
		return isUserDisabled;
	}

	public void setUserDisabled(boolean isUserDisabled) {
		this.isUserDisabled = isUserDisabled;
	}

	public boolean isWaveLuminosity() {
		return isWaveLuminosity;
	}

	public void setWaveLuminosity(boolean isWaveLuminosity) {
		this.isWaveLuminosity = isWaveLuminosity;
	}

	@Override
	public String toString() {
		String buffer = String.format(
				"%s [%d] : {type=%s, song=%s, days=%s, timing=%d, "+
				"crescendo=%b, disabled=%b, waveLuminosity=%b, random=%b}", 
				name, getID(), type.name(), song.getPath(), Arrays.toString(days), firstTime.getTimeInMillis(),
				isCrescendoSong, isUserDisabled, isWaveLuminosity, isRandomSong);
		return buffer;
	}

	public int compareTo(Alarm to){
		if(to == null || this.getFirstTime() == null || to.getFirstTime()== null){
			return -1;
		}
		int d1 = this.getFirstTime().get(Calendar.HOUR_OF_DAY)*60+ this.getFirstTime().get(Calendar.MINUTE);
		int d2 = to.getFirstTime().get(Calendar.HOUR_OF_DAY)*60+ to.getFirstTime().get(Calendar.MINUTE);
		if(d1 - d2 != 0){
			return d1 - d2;
		}
		int nameCmp = this.getName().compareTo(to.getName());
		if(nameCmp != 0){
			return nameCmp;
		}
		int typeCmp = getType().compareTo(to.getType());
		if(typeCmp != 0){
			return typeCmp;
		}
		int diffID = (int) (getID() - to.getID());
		return diffID;
	}

	public void updateParameters(Alarm from){
		if(from == null){
			return;
		}
		name = from.name;
		type = from.type;
		song = from.song;
		days = from.days;
		firstTime = from.firstTime;
		isRandomSong = from.isRandomSong;
		isCrescendoSong = from.isCrescendoSong;
		isUserDisabled = from.isUserDisabled;
		isWaveLuminosity = from.isWaveLuminosity;
        timingExceptions = from.getTimingExceptions();
	}

    public void addTimingException(TimingException t){
        if(timingExceptions == null){
            timingExceptions = new ArrayList<TimingException>();
        }
        timingExceptions.add(t);
    }
}
