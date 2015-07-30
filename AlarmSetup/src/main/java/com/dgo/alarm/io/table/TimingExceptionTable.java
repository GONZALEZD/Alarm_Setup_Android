package com.dgo.alarm.io.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dgo.alarm.data.TimingException;
import com.dgo.alarm.io.DBException;
import com.dgo.alarm.io.IDBElement;
import com.dgo.alarm.io.IDBTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimingExceptionTable implements IDBTable {

	protected static final String TABLE_NAME = "TIMING_EXCEPTION";
	protected static final String ID = "TIMING_EXCEPTION_ID";
	protected static final String NAME = "NAME";
	protected static final String ALARM_ID = "ALARM_ID";
	protected static final String SCHEDULED_TIME = "SCHEDULED_TIME";
	
	
	@Override
	public boolean createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
				ID + " INTEGER PRIMARY KEY, "+
				NAME + " VARCHAR(30) NOT NULL, "+
				ALARM_ID+" INTEGER, "+
				SCHEDULED_TIME + " BIGINT NOT NULL) ");
		return true;
	}

	@Override
	public String getName() {
		return TABLE_NAME;
	}

	@Override
	public long insert(SQLiteDatabase db,  IDBElement obj) {
		if(obj instanceof TimingException){
			insert(db, (TimingException) obj);
		}
		return INVALID_ID;
	}

	public long insert(SQLiteDatabase db, TimingException newTime, long alarmId){
		if(alarmId == INVALID_ID){
			return INVALID_ID;
		}
		ContentValues cv = new ContentValues();
		cv.put(NAME, ""+newTime.getName());
		cv.put(ALARM_ID, alarmId);
		cv.put(SCHEDULED_TIME, newTime.getNewTime().getTimeInMillis());
		long id = db.insert(TABLE_NAME, null, cv);
		newTime.setID(id);
		return id;
	}

	public List<TimingException> retrieveTimingExceptions(SQLiteDatabase db, long alarmID){

		Cursor c = db.rawQuery("SELECT " + ID + "," + NAME + "," + SCHEDULED_TIME  +
                " FROM " + TABLE_NAME +
                " WHERE " + ALARM_ID + " == " + alarmID, null);
		return retrieveFrom(c);
	}

	private List<TimingException> retrieveFrom(Cursor c){
		List<TimingException> rez = new ArrayList<TimingException>();
		if(c.getCount() > 0){
			c.moveToFirst();
			int idId = c.getColumnIndex(ID);
			int idName = c.getColumnIndex(NAME);
			int idTime = c.getColumnIndex(SCHEDULED_TIME);
			String name;
			long id;
			Calendar scheduledTime;
			TimingException t;
			do{
				id = c.getLong(idId);
				name = c.getString(idName);
				scheduledTime = Calendar.getInstance();
				scheduledTime.setTimeInMillis(c.getLong(idTime));
				t = new TimingException(scheduledTime);
				t.setID(id);
				t.setName(name);
				rez.add(t);
				c.moveToNext();
			}
			while(c.isAfterLast() == false);
		}
		c.close();
		return rez;
	}

	public TimingException getTimingException(SQLiteDatabase db, long timingExceptionId){
		Cursor c = db.rawQuery("SELECT " + ID + "," + NAME + "," + SCHEDULED_TIME +
				" FROM " + TABLE_NAME +
				" WHERE " + ID + " == " + timingExceptionId, null);
		List<TimingException> list = retrieveFrom(c);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}


	public void deleteExceptionsFromAlarm(SQLiteDatabase db, long alarmId) {
		db.delete(TABLE_NAME, ALARM_ID + " = " + alarmId, null);
	}

    public void delete(SQLiteDatabase db, TimingException timing) throws DBException{
        if(timing != null && timing.hasBeenStored()) {
            db.delete(TABLE_NAME, ID + " = " + timing.getID(), null);
        }
        else{
            throw new DBException("Unable to delete alarm");
        }
    }

}
