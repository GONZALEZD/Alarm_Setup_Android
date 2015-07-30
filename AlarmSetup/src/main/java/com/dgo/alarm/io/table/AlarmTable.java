package com.dgo.alarm.io.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.data.AlarmType;
import com.dgo.alarm.data.DaysOfTheWeek;
import com.dgo.alarm.io.DBException;
import com.dgo.alarm.io.IDBElement;
import com.dgo.alarm.io.IDBTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmTable implements IDBTable {

	protected static final String TABLE_NAME = "ALARM";
	protected static final String ID = "ALARM_ID";
	protected static final String NAME = "NAME";
	protected static final String ALARM_TYPE = "ALARM_TYPE";
	protected static final String DAYS_OF_THE_WEEK = "DAYS_OF_THE_WEEK";
	protected static final String SCHEDULED_TIME = "SCHEDULED_TIME";
	protected static final String UI_WAKEUP_MODE = "UI_WAKEUP_MODE";
	protected static final String SONG_URI = "SONG_URI";
	protected static final String IS_RANDOM_SONG = "IS_RANDOM_SONG";
	protected static final String IS_CRESCENDO_SONG = "IS_CRESCENDO_SONG";
	protected static final String IS_DISABLED_BY_USER = "IS_DISABLED_BY_USER";
	protected static final String IS_WAVE_LUMINOSITY = "IS_WAVE_LUMINOSITY";
	
	@Override
	public boolean createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
				ID + " INTEGER PRIMARY KEY, "+
				NAME + " VARCHAR(30) NOT NULL, "+
				ALARM_TYPE + " TINYINT NOT NULL, "+
				DAYS_OF_THE_WEEK + " VARCHAR(30) NOT NULL, "+
				SCHEDULED_TIME + " BIGINT NOT NULL, "+
				UI_WAKEUP_MODE + " TINYINT DEFAULT 0,"+
				SONG_URI + " VARCHAR(30) NOT NULL, "+
				IS_RANDOM_SONG + " BOOLEAN DEFAULT FALSE, "+
				IS_CRESCENDO_SONG + " BOOLEAN DEFAULT FALSE, "+
				IS_DISABLED_BY_USER + " BOOLEAN DEFAULT FALSE, "+
				IS_WAVE_LUMINOSITY + " BOOLEAN DEFAULT FALSE)");
		return true;
	}

	@Override
	public String getName() {
		return TABLE_NAME;
	}

	@Override
	public long insert(SQLiteDatabase db, IDBElement obj) {
		if(obj instanceof Alarm){
			return insert(db, (Alarm) obj);
		}
		return INVALID_ID;
	}
	
	public long insert(SQLiteDatabase db, Alarm alarm){
		String daysOfTheWeek = "";
		for(DaysOfTheWeek day : alarm.getDays()){
			daysOfTheWeek += day.toInt();
		}
		
		ContentValues cv = new ContentValues();
		cv.put(NAME, alarm.getName());
		cv.put(ALARM_TYPE, alarm.getType().toInt());
		cv.put(DAYS_OF_THE_WEEK, daysOfTheWeek);
		cv.put(SCHEDULED_TIME, alarm.getFirstTime().getTimeInMillis());
		cv.put(SONG_URI, alarm.getSong().getPath());
		cv.put(IS_CRESCENDO_SONG, alarm.isCrescendoSong());
		cv.put(IS_DISABLED_BY_USER, alarm.isUserDisabled());
		cv.put(IS_RANDOM_SONG, alarm.isRandomSong());
		cv.put(IS_WAVE_LUMINOSITY, alarm.isWaveLuminosity());
		long id = db.insert(TABLE_NAME, null, cv);
		alarm.setID(id);
		return id;
	}
	
	
	public Alarm getAlarm(SQLiteDatabase db, long id){
		Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " == " + id,
                null);
		if(c.getCount() > 0){
			// we should have only 1 item inside the cursor
			c.moveToFirst();
			int nameId = c.getColumnIndex(NAME);
			int idId = c.getColumnIndex(ID);
			int alarmTypeId = c.getColumnIndex(ALARM_TYPE);
			int daysListId = c.getColumnIndex(DAYS_OF_THE_WEEK);
			int crescendoId = c.getColumnIndex(IS_CRESCENDO_SONG);
			int randomId = c.getColumnIndex(IS_RANDOM_SONG);
			int disabledId = c.getColumnIndex(IS_DISABLED_BY_USER);
			int waveId = c.getColumnIndex(IS_WAVE_LUMINOSITY);
			int scheduleId = c.getColumnIndex(SCHEDULED_TIME);
			int uriId = c.getColumnIndex(SONG_URI);
			
			String name = c.getString(nameId);
			long idAlarm = c.getLong(idId);
			AlarmType alarmType = AlarmType.fromInt(c.getInt(alarmTypeId));
			String strDays = c.getString(daysListId);
			DaysOfTheWeek[] days = null;
			if(strDays != null){
				days = new DaysOfTheWeek[strDays.length()];
				for(int i=0; i<strDays.length(); i++){
					char s = strDays.charAt(i);
					if(s >= '0' && s <= '9'){
						days[i] = DaysOfTheWeek.fromInt(Integer.parseInt(""+s));
					}
				}
			}
			boolean isCrescendo = c.getInt(crescendoId) == 1;
			boolean isRandom = c.getInt(randomId) == 1;
			boolean isDisabled = c.getInt(disabledId) == 1;
			boolean isWave = c.getInt(waveId) == 1;
			long timeMilli = c.getLong(scheduleId);
			Uri uri = Uri.parse(c.getString(uriId));
			Calendar firstTime = Calendar.getInstance();
			firstTime.setTimeInMillis(timeMilli);
			Alarm a = new Alarm(name, alarmType, days, firstTime, uri);
			a.setCrescendoSong(isCrescendo);
			a.setRandomSong(isRandom);
			a.setWaveLuminosity(isWave);
			a.setUserDisabled(isDisabled);
			a.setID(idAlarm);
			c.close();
			return a;
		}
		return null;
	}
	
	public List<Long> getAlarmIds(SQLiteDatabase db, long from, long to) {
		List<Long> oneShotAlarmIds = getOneShotAlarmIds(db, from, to);
		List<Long> recurrentAlarmIds = getRecurrentAlarmIds(db);
		oneShotAlarmIds.addAll(recurrentAlarmIds);
		
		return oneShotAlarmIds;
	}
	
	private List<Long> getOneShotAlarmIds(SQLiteDatabase db, long from, long to){
		List<Long> result = new ArrayList<Long>();
		Cursor c = db.rawQuery(
				"SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + SCHEDULED_TIME + " >= " + from +
						" AND " + SCHEDULED_TIME + " <= " + to + " AND " + ALARM_TYPE + " = " + AlarmType.ONE_SHOT.toInt(),
				null);
		if(c.getCount() > 0){
			c.moveToFirst();
			int idId = c.getColumnIndex(ID);
			do{
				result.add(c.getLong(idId));
				c.moveToNext();
			}
			while(c.isAfterLast() == false);
		}
		c.close();
		return result;
	}
	
	private List<Long> getRecurrentAlarmIds(SQLiteDatabase db){
		List<Long> result = new ArrayList<Long>();
		Cursor c = db.rawQuery(
				"SELECT " + ID + " FROM "+ TABLE_NAME +" WHERE " +
						ALARM_TYPE + " == "+ AlarmType.RECURRENT.toInt(),
				null);
		if(c.getCount() > 0){
			c.moveToFirst();
			int idId = c.getColumnIndex(ID);
			do{
				result.add(c.getLong(idId));
				c.moveToNext();
			}
			while(c.isAfterLast() == false);
		}
		c.close();
		return result;
	}

	public void update(SQLiteDatabase db, Alarm a) {
		String daysOfTheWeek = "";
		for(DaysOfTheWeek day : a.getDays()){
			daysOfTheWeek += day.toInt();
		}

		ContentValues cv = new ContentValues();
		cv.put(NAME, a.getName());
		cv.put(ALARM_TYPE, a.getType().toInt());
		cv.put(DAYS_OF_THE_WEEK, daysOfTheWeek);
		cv.put(SCHEDULED_TIME, a.getFirstTime().getTimeInMillis());
		cv.put(SONG_URI, a.getSong().getPath());
		cv.put(IS_CRESCENDO_SONG, a.isCrescendoSong());
		cv.put(IS_DISABLED_BY_USER, a.isUserDisabled());
		cv.put(IS_RANDOM_SONG, a.isRandomSong());
		cv.put(IS_WAVE_LUMINOSITY, a.isWaveLuminosity());
		db.update(TABLE_NAME, cv, ID + " = " + a.getID(), null);
	}

	public void delete(SQLiteDatabase db, Alarm a) throws DBException{
		if(a != null && a.hasBeenStored()) {
			db.delete(TABLE_NAME, ID + " = " + a.getID(), null);
		}
		else{
			throw new DBException("Unable to delete alarm");
		}
	}
}
