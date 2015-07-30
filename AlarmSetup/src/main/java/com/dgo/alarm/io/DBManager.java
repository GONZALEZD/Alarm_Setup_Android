package com.dgo.alarm.io;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.data.AlarmType;
import com.dgo.alarm.data.TimingException;
import com.dgo.alarm.io.table.AlarmTable;
import com.dgo.alarm.io.table.TimingExceptionTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {
	private static final String TAG = "Database_Manager" ;
	
	private static final int VERSION = 1;
	private static final String DB_NAME = "alarms.db";
	private AlarmTable m_alarmTable;
	private TimingExceptionTable m_exceptionsTable;
	
	private static DBManager INSTANCE = null;
	
	public synchronized static DBManager getInstance(Context context){
		if(INSTANCE == null){
			INSTANCE = new DBManager(context);
		}
		return INSTANCE;
	}
	
	private DBManager(Context context) {
		super(context, DB_NAME, null, VERSION);
		m_alarmTable = new AlarmTable();
		m_exceptionsTable = new TimingExceptionTable();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		m_alarmTable.createTable(db);
		m_exceptionsTable.createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public long addAlarm(Alarm alarm){
		Log.e(TAG, "Insert alarm " + alarm);
		SQLiteDatabase db = getWritableDatabase();
		return m_alarmTable.insert(db, alarm);
	}
	
	public Alarm getAlarm(long alarmId){
		SQLiteDatabase db = getReadableDatabase();
		Alarm a = m_alarmTable.getAlarm(db, alarmId);
		if(a != null) {
			a.setTimingExceptions(
                    m_exceptionsTable.retrieveTimingExceptions(db, alarmId));
		}
		return a;
	}
	
	public List<Alarm> getAlarms(Calendar from, Calendar to){
		ArrayList<Alarm> result = new ArrayList<Alarm>();
		Alarm tmpAlarm = null;
		SQLiteDatabase db = getReadableDatabase();
		List<Long> ids = m_alarmTable.getAlarmIds(db, from.getTimeInMillis(), to.getTimeInMillis());
		for(Long id : ids){
			tmpAlarm = getAlarm(id);
			if(tmpAlarm != null){
				result.add(tmpAlarm);
			}
		}
		return result;
	}
	
	public void updateAlarm(Alarm a){
		SQLiteDatabase db = getWritableDatabase();
		m_alarmTable.update(db, a);
	}

	public void deleteAlarm(Alarm a) throws DBException{
		SQLiteDatabase db = getWritableDatabase();
		long alarmId = a.getID();
		m_alarmTable.delete(db, a);
		m_exceptionsTable.deleteExceptionsFromAlarm(db, alarmId);
	}

	public void refresh(Alarm alarm) {
        if(alarm != null) {
            Alarm lastAlarm = getAlarm(alarm.getID());
            alarm.updateParameters(lastAlarm);
        }
	}

	/**
	 * Only available for recurrent alarms.
	 * Add a timing exception if needed (not already there)
	 * @param to
	 * @param time
	 * @return
	 */
	public TimingException addTimingException(Alarm to, Calendar time) throws DBException{
		if(to == null || time == null
				|| to.getType() == AlarmType.ONE_SHOT
				|| to.getID() == IDBTable.INVALID_ID){
            // For oneshot alarm, consider modifying it instead of adding a timing exception
			throw new DBException("You cannot add a timing exception. " +
                    "Feature available only for stored recurrent alarm.");
		}
		SQLiteDatabase db = getWritableDatabase();
		TimingException te = new TimingException(time);
		m_exceptionsTable.insert(db, te, to.getID());
        Log.e(TAG, "Inserted timing exception " + te);
        to.addTimingException(te);
		return te;
	}

    public void deleteTimingExceptions(Alarm from){
        if(from == null || from.hasBeenStored() == false){
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        m_exceptionsTable.deleteExceptionsFromAlarm(db, from.getID());
        from.setTimingExceptions(new ArrayList<TimingException>());
    }

    public void deleteTimingException(Alarm from, TimingException toDelete) throws DBException{
        if(toDelete == null || toDelete.hasBeenStored() == false){
            return;
        }
        Log.e(TAG, "Deleting timing exception " + toDelete.toString());
        SQLiteDatabase db = getWritableDatabase();
        m_exceptionsTable.delete(db, toDelete);
        // refresh alarm
        if(from != null && from.hasTimingException()) {
            from.getTimingExceptions().remove(toDelete);
        }
    }
}
