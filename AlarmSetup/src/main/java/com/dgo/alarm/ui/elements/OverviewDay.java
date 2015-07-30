package com.dgo.alarm.ui.elements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.util.SortedList;
import android.text.format.DateFormat;

import com.dgo.alarm.R;
import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.io.DBManager;
import com.dgo.alarm.utilities.AlarmIcons;
import com.dgo.alarm.utilities.AlarmIcons.TYPE;
import com.dgo.alarm.utilities.AlarmUtility;

import java.util.Calendar;
import java.util.List;

public class OverviewDay{

	private Drawable m_background;
	
	private SortedList<Alarm> m_alarms;
	
	private Context m_context;
	
	private Calendar m_currentDay;

    private class SortCallback extends SortedList.Callback<Alarm>{

        @Override
        public int compare(Alarm alarm, Alarm alarm2) {
            if(alarm != null){
                return alarm.compareTo(alarm2);
            }
            return 0;
        }

        @Override
        public void onInserted(int i, int i1) {

        }

        @Override
        public void onRemoved(int i, int i1) {

        }

        @Override
        public void onMoved(int i, int i1) {

        }

        @Override
        public void onChanged(int i, int i1) {

        }

        @Override
        public boolean areContentsTheSame(Alarm alarm, Alarm alarm2) {
            if( alarm != null && alarm2 != null){
                return alarm.compareTo(alarm2) == 0;
            }
            return false;
        }

        @Override
        public boolean areItemsTheSame(Alarm alarm, Alarm alarm2) {
            if( alarm != null && alarm2 != null){
                return alarm.compareTo(alarm2) == 0;
            }
            return false;
        }
    }
	
	public OverviewDay(Context context){
		m_alarms = new SortedList<Alarm>(Alarm.class, new SortCallback());
		m_background = null;
		m_context = context;
		m_currentDay = null;
	}
	
	public void addAlarm(Alarm a){
		m_alarms.add(a);
	}
	
	public void setAlarms(List<Alarm> alarms){
		m_alarms = new SortedList<Alarm>(Alarm.class, new SortCallback());
		m_alarms.addAll(alarms);
	}
	
	public SortedList<Alarm> getAlarms(){
		return m_alarms;
	}
	
	public TYPE getAlarmsIconType(boolean rounded){
		int nbOneShot = 0;
		int nbRecurrent = 0;
		for(int i=0; i< m_alarms.size(); i++){
            Alarm a = m_alarms.get(i);
			if(AlarmUtility.isDisabled(a, m_currentDay) == false) {
				switch (a.getType()) {
					case ONE_SHOT:
						nbOneShot++;
						break;
					case RECURRENT:
						nbRecurrent++;
						break;
					default:
						break;
				}
			}
		}
		boolean both = nbOneShot>0 && nbRecurrent>0;
		boolean none = nbOneShot<=0 && nbRecurrent<=0;
		if(rounded){
			if(both){
				return TYPE.ROUND_BOTH;
			}
			else if(none){
				return TYPE.ROUND_NONE;
			}
			else if(nbOneShot > 0){
                return TYPE.ROUND_ONE_SHOT;
            }
            else {
                return TYPE.ROUND_RECURRENT;
            }
		}
		else{
			if(both){
				return TYPE.BOTH;
			}
			else if(none){
				return TYPE.NONE;
			}
            else if(nbOneShot > 0){
                return TYPE.ONE_SHOT;
            }
            else {
                return TYPE.RECURRENT;
            }
		}
	}
	
	public void setDay(Calendar day){
		m_currentDay = day;
	}
	
	public void setBackground(Drawable a){
		m_background = a;
	}
	
	public Calendar getCurrentDay(){
		return m_currentDay;
	}
	
	public Drawable getBackground(){
		return m_background;
	}
	
	public Alarm getSoonerAlarm(){
		Alarm rez = null;
		if(m_alarms.size()>0){
            for(int i=0; i< m_alarms.size(); i++){
                Alarm a = m_alarms.get(i);
				if(a.compareTo(rez) < 0 && AlarmUtility.isDisabled(a, m_currentDay) == false){
					rez = a;
				}
			}
		}
		return rez;
	}
	
	public boolean hasAlarm(){
		return m_alarms.size() > 0;
	}
	
	public String getDayNumberString(){
		if(m_currentDay == null){
			return "";
		}
		int dayNum = m_currentDay.get(Calendar.DAY_OF_MONTH);
		return dayNum<10?"0"+dayNum: ""+dayNum;
	}
	
	public String getTimeFirstAlarmString(){
		if(hasAlarm()){
            Alarm sooner = getSoonerAlarm();
            if(sooner != null){
                Calendar firstC = sooner.getFirstTime();
                return DateFormat.format("HH:mm", firstC).toString();
            }
			return m_context.getString(R.string.no_active_alarm);
		}
		// default display
		return m_context.getString(R.string.no_first_alarm);
	}
	
	public String getDateString(){
		int year = m_currentDay.get(Calendar.YEAR);
		int month = m_currentDay.get(Calendar.MONTH);
		String monthStr = m_context.getResources().getStringArray(R.array.month_names)[month];
		String dayStr = getDayNumberString();
		return dayStr + " " + monthStr + " " +year;
	}

	public static OverviewDay getDefaultOverview(Context c){
		OverviewDay day = new OverviewDay(c);
		day.setBackground(AlarmIcons.getInstance(c).get(TYPE.ROUND_NONE));
		day.setDay(null);
		return day;
	}

    public void refreshAlarms(){
        if(m_alarms == null){
            return;
        }
        DBManager db = DBManager.getInstance(m_context);
        for(int i=0; i<m_alarms.size(); i++){
            Alarm a = m_alarms.get(i);
            if(a != null){
                db.refresh(a);
            }
        }
    }
}
