package com.dgo.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.io.DBManager;
import com.dgo.alarm.utilities.ActivityUtilities;

public class OnWakeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if(launchAlarm(arg0, arg1)){
			Intent i = new Intent(arg0, OnWakeActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			i.putExtras(arg1.getExtras());
			arg0.startActivity(i);			
		}
	}
	
	private boolean launchAlarm(Context c, Intent i){
		DBManager alarms = DBManager.getInstance(c);
		if(i.hasExtra(ActivityUtilities.INTENT_EXTRA_NAME_ALARM_ID)){
			long id = i.getLongExtra(ActivityUtilities.INTENT_EXTRA_NAME_ALARM_ID, -1);
			if(id != -1){
				Alarm a = alarms.getAlarm(id);
				if(a != null && a.isUserDisabled() == false){
					return true;
				}
			}
		}
		return false;
	}

}
