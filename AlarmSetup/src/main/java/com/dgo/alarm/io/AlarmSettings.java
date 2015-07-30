package com.dgo.alarm.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dgo.alarm.R;

public class AlarmSettings {
	private SharedPreferences mPreferences;
	private final String KEY_TICK_COUNT = "tick_count";
	private final String KEY_ONE_SHOT_COLOR = "one_shot_color";
	private final String KEY_RECURRENT_COLOR = "recurrent_color";

	private Context mContext;
	
	public AlarmSettings(Context c, SharedPreferences pref){
		mPreferences = pref;
		mContext = c;
	}
	
	public void setTickSetting(boolean is5Min){
		Editor e = mPreferences.edit();
		e.putBoolean(KEY_TICK_COUNT, is5Min);
		e.commit();
	}
	
	public boolean is5minSelected(){
		return mPreferences.getBoolean(KEY_TICK_COUNT, false);
	}

	public void setOneShotColor(int color){
		Editor e = mPreferences.edit();
		e.putInt(KEY_ONE_SHOT_COLOR, color);
		e.commit();
	}

	public int getOneShotColor(){
		return mPreferences.getInt(KEY_ONE_SHOT_COLOR,
				mContext.getResources().getColor(R.color.default_one_shot));
	}

	public int getRecurrentColor(){
		return mPreferences.getInt(KEY_RECURRENT_COLOR,
				mContext.getResources().getColor(R.color.default_recurrent));
	}

	public void setRecurrentColor(int color){
		Editor e = mPreferences.edit();
		e.putInt(KEY_RECURRENT_COLOR, color);
		e.commit();
	}
}
