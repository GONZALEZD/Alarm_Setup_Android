package com.dgo.alarm.ui.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.dgo.alarm.OnWakeBroadcastReceiver;
import com.dgo.alarm.R;
import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.data.AlarmType;
import com.dgo.alarm.data.DaysOfTheWeek;
import com.dgo.alarm.io.AlarmSettings;
import com.dgo.alarm.io.DBManager;
import com.dgo.alarm.ui.elements.TimeTextView;
import com.dgo.alarm.utilities.ActivityUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AlarmFragment extends Fragment {
	
	private static final String INAPP_ALARM_DIRECTORY = "alarm_songs";
	private static final int INTENT_CODE_SELECT_ALARM_SONG = 11;
	private static final String TAG = ""+AlarmFragment.class.getSimpleName();

	private View m_newAlarm;
	private AlarmSettings m_alarmSettings;
	private DBManager m_alarms;

	private Alarm m_alarm;
    private Calendar m_day;
	private ArrayAdapter<String> m_musics;

	private boolean editionMode = false;
	private boolean m_oneShot = true;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_alarms = DBManager.getInstance(getActivity());
		m_alarmSettings = new AlarmSettings(getActivity(), getActivity().getPreferences(Activity.MODE_PRIVATE));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		m_newAlarm = inflater.inflate(R.layout.new_alarm_layout, null);

		return m_newAlarm;
	}
	
	private void setupSingleShotAlarmTab(){
		//initialize hour and minuts
		updateDateTime();

		SeekBar sbHour = (SeekBar) m_newAlarm.findViewById(R.id.seekBarChooseHour_single);
		sbHour.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				TimeTextView ttv = (TimeTextView) m_newAlarm.findViewById(R.id.textTime_single);
				ttv.setHour(progress);
			}
		});

		SeekBar sbMinuts = (SeekBar) m_newAlarm.findViewById(R.id.seekBarChooseMinuts_single);
		boolean is5min = m_alarmSettings.is5minSelected();
		sbMinuts.incrementProgressBy(is5min?5:1);
		sbMinuts.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				boolean is5min = m_alarmSettings.is5minSelected();
				int div = is5min?5:1;
				int value = (progress/div)*div;
				TimeTextView ttv = (TimeTextView) m_newAlarm.findViewById(R.id.textTime_single);
				ttv.setMinut(value);
			}
		});

		Button bValidate = (Button) m_newAlarm.findViewById(R.id.buttonValidateAlarm_single);
		if(editionMode){
			bValidate.setText(R.string.button_modify_alarm);
			bValidate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
                    modifyAlarm();
				}
			});
		}
		else{
			bValidate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					createNewAlarm();
				}
			});
		}
		
		Spinner musicChooser = (Spinner) m_newAlarm.findViewById(R.id.spinnerMusicChooser);
		try {
			String[] inAppAlarms = getActivity().getAssets().list(INAPP_ALARM_DIRECTORY);
			ArrayList<String> items = new ArrayList<String>();
			items.addAll(Arrays.asList(inAppAlarms));
			items.add(0, this.getString(R.string.msg_choose_music_item));
			m_musics = new ArrayAdapter<String>(
					getActivity(), R.layout.spinner_dropdown_item, R.id.item_text, items);
			musicChooser.setAdapter(m_musics);
		} catch (IOException e) {
			e.printStackTrace();
        }
        musicChooser.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
									   int position, long arg3) {
				if (position == 0) {
					requestMusicChooser();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		int id = musicChooser.getAdapter().getCount() > 1?1:0;
		musicChooser.setSelection(id);


		RadioButton radioBtn;
		if(m_oneShot){
			radioBtn = (RadioButton) getActivity().findViewById(R.id.oneShotRadioBtn);
		}
		else{
			radioBtn = (RadioButton) getActivity().findViewById(R.id.weeklyRadioBtn);
		}
		radioBtn.setChecked(true);
	}

	private void updateDateTime(){
		TimeTextView ttv = null;
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minuts = now.get(Calendar.MINUTE);
		ttv = (TimeTextView) m_newAlarm.findViewById(R.id.textTime_single);
		ttv.setHour(hour);
		ttv.setMinut(minuts);
		SeekBar seekHours3 = (SeekBar) m_newAlarm.findViewById(R.id.seekBarChooseHour_single);
		seekHours3.setProgress(hour);
		SeekBar seekMinuts3 = (SeekBar) m_newAlarm.findViewById(R.id.seekBarChooseMinuts_single);
		seekMinuts3.setProgress(minuts);
	}
	
	private void requestMusicChooser(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Intent intent = new Intent(path);
        intent.setType("audio/mp3");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent starter = Intent.createChooser(intent, getString(R.string.msg_choose_music));
        startActivityForResult(starter, INTENT_CODE_SELECT_ALARM_SONG);
	}
	
	private void createNewAlarm(){
		TimeTextView ttv = (TimeTextView) m_newAlarm.findViewById(R.id.textTime_single);
        Calendar calSet = ttv.computeTime(m_day);

		Spinner spinner = (Spinner) m_newAlarm.findViewById(R.id.spinnerMusicChooser);
		String item = (String)spinner.getSelectedItem();
		Uri uri = Uri.parse(item);
		
		DaysOfTheWeek[] days = getDaysOfTheWeekFromUI();
		
		boolean isOneShot = ((RadioButton) m_newAlarm.findViewById(R.id.oneShotRadioBtn)).isChecked();
		if(isOneShot){
			createOneShotAlarms(calSet, days, uri);
		}
		else{
			createRecurrentAlarm(calSet, days, uri);
		}
		
	}
	
	private void createRecurrentAlarm(Calendar firstTime, DaysOfTheWeek[] days, Uri song){
		// Store in database
		long id = m_alarms.addAlarm(new Alarm(
				"new ALARM", AlarmType.RECURRENT, days, firstTime, song));
        setWakeUpToAlarmManager(firstTime, id);
	}
	
	private void createOneShotAlarms(Calendar firstTime, DaysOfTheWeek[] days, Uri song){
		Calendar modif = (Calendar) firstTime.clone();
		for(DaysOfTheWeek d : days){
			modif.set(Calendar.DAY_OF_WEEK, d.toCalendarInt());
			if(modif.compareTo(firstTime) < 0){
				modif.add(Calendar.DAY_OF_MONTH, 7);
			}
			// Store in database
			long id = m_alarms.addAlarm(new Alarm(
                    "new ALARM", AlarmType.ONE_SHOT, days, modif, song));
            setWakeUpToAlarmManager(firstTime, id);
		}
	}

    private void setWakeUpToAlarmManager(Calendar time, long alarmId){
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getActivity().getBaseContext(), OnWakeBroadcastReceiver.class);
        i.putExtra(ActivityUtilities.INTENT_EXTRA_NAME_ALARM_ID, alarmId);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && data != null){
			switch(requestCode){
			case INTENT_CODE_SELECT_ALARM_SONG:
				Uri uriSound=data.getData();

				Spinner spinner = (Spinner) m_newAlarm.findViewById(R.id.spinnerMusicChooser);
				m_musics.add(uriSound.getPath());
				m_musics.notifyDataSetChanged();
				spinner.setSelection(m_musics.getCount() - 1);
	    		break;
	    	default: 
	    		break;
			}
		}
		else{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private void updateNewAlarm(Calendar dateTime, DaysOfTheWeek[] days, int hours, int minuts, Uri song){
		// configure new tab
		Calendar now = dateTime;
		int finalHour = hours;
		if(finalHour < 0 || finalHour > 23){
			finalHour = now.get(Calendar.HOUR_OF_DAY);
		}
		int finalMinuts = minuts;
		if(finalMinuts < 0 || finalMinuts > 59){
			finalMinuts = now.get(Calendar.MINUTE);
		}
		DaysOfTheWeek[] finalDays = days;
		if(finalDays == null || finalDays.length == 0){
			finalDays = new DaysOfTheWeek[]{
			DaysOfTheWeek.fromCalendarDay(now.get(Calendar.DAY_OF_WEEK))};
		}
		
		SeekBar hoursSeekBar = (SeekBar) m_newAlarm.findViewById(R.id.seekBarChooseHour_single);
		hoursSeekBar.setProgress(finalHour);
		
		SeekBar minutsSeekBar = (SeekBar) m_newAlarm.findViewById(R.id.seekBarChooseMinuts_single);
		minutsSeekBar.setProgress(finalMinuts);
		boolean is5min = m_alarmSettings.is5minSelected();
		minutsSeekBar.incrementProgressBy(is5min?5:1);
		minutsSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				boolean is5min = m_alarmSettings.is5minSelected();
				int div = is5min?5:1;
				int value = (progress/div)*div;
				TimeTextView ttv = (TimeTextView) m_newAlarm.findViewById(R.id.textTime_single);
				ttv.setMinut(value);
			}
		});
		
		TimeTextView ttv = (TimeTextView) m_newAlarm.findViewById(R.id.textTime_single);
		ttv.setDate(now);
		ttv.setHour(finalHour);
		ttv.setMinut(finalMinuts);
		
		int[] idToggles = new int[]{
				R.id.toggleSunday, R.id.toggleMonday, R.id.toggleTuesday, R.id.toggleWednesday,
				R.id.toggleThursday, R.id.toggleFriday, R.id.toggleSaturday
		};
		for(int idT : idToggles){
            ToggleButton toggleBtn = (ToggleButton) m_newAlarm.findViewById(idT);
            toggleBtn.setSelected(false);
            toggleBtn.setChecked(false);
            toggleBtn.setPressed(false);
		}

		for(DaysOfTheWeek mDay : finalDays){
            int dayId = -1;
			switch (mDay) {
			case SUNDAY:
				dayId = R.id.toggleSunday;
				break;
			case MONDAY:
				dayId = R.id.toggleMonday;
				break;
			case TUESDAY:
                dayId = R.id.toggleTuesday;
				break;
			case WEDNESDAY:
                dayId = R.id.toggleWednesday;
				break;
			case THURSDAY:
                dayId = R.id.toggleThursday;
				break;
			case FRIDAY:
                dayId = R.id.toggleFriday;
				break;
			case SATURDAY:
                dayId = R.id.toggleSaturday;
				break;
			default:
                dayId = -1;
                break;
			}
            if(dayId != -1){
                ToggleButton tb = ((ToggleButton) m_newAlarm.findViewById(dayId));
                tb.setChecked(true);
                tb.setPressed(true);
            }
		}
        m_newAlarm.invalidate();
	}

    @Override
    public void onResume() {
        super.onResume();
        setupSingleShotAlarmTab();
        if(m_day != null){
            updateNewAlarm(m_day, null, -1, -1, null);
        }
		if(m_alarm != null){
			int hours = m_alarm.getFirstTime().get(Calendar.HOUR_OF_DAY);
			int minuts = m_alarm.getFirstTime().get(Calendar.MINUTE);
			updateNewAlarm(m_alarm.getFirstTime(),m_alarm.getDays(),hours, minuts,m_alarm.getSong());
			// we have to keep the alarm instance in order to apply modifications on it !
		}
    }

	@Override
	public void onPause() {
		super.onPause();
		m_alarm = null;
	}

	public void setDay(Calendar day){
        m_day = day;
    }

	public void setupOneShot(boolean oneShot){
		m_oneShot = oneShot;
	}

	public void setEditionMode(boolean enable, Alarm which){
		editionMode = enable;
		m_alarm = which;
	}

    private void modifyAlarm(){
        TimeTextView ttv = (TimeTextView) m_newAlarm.findViewById(R.id.textTime_single);
        Calendar calSet = ttv.computeTime(m_alarm.getFirstTime());

		Spinner spinner = (Spinner) m_newAlarm.findViewById(R.id.spinnerMusicChooser);
		String item = (String)spinner.getSelectedItem();
		Uri uri = Uri.parse(item);

        m_alarm.setFirstTime(calSet);
        m_alarm.setSong(uri);
        m_alarm.setDays(getDaysOfTheWeekFromUI());
        m_alarm.setUserDisabled(false);

        m_alarms.updateAlarm(m_alarm);
        setWakeUpToAlarmManager(calSet, m_alarm.getID());
    }

    private DaysOfTheWeek[] getDaysOfTheWeekFromUI(){
        List<DaysOfTheWeek> daysList = new ArrayList<DaysOfTheWeek>();
        List<Pair<Integer,DaysOfTheWeek>> idToggleBtn = new ArrayList<Pair<Integer,DaysOfTheWeek>>();
        idToggleBtn.add(new Pair<Integer,DaysOfTheWeek>(R.id.toggleSunday, DaysOfTheWeek.SUNDAY));
        idToggleBtn.add(new Pair<Integer,DaysOfTheWeek>(R.id.toggleMonday, DaysOfTheWeek.MONDAY));
        idToggleBtn.add(new Pair<Integer,DaysOfTheWeek>(R.id.toggleTuesday, DaysOfTheWeek.TUESDAY));
        idToggleBtn.add(new Pair<Integer,DaysOfTheWeek>(R.id.toggleWednesday, DaysOfTheWeek.WEDNESDAY));
        idToggleBtn.add(new Pair<Integer,DaysOfTheWeek>(R.id.toggleThursday, DaysOfTheWeek.THURSDAY));
        idToggleBtn.add(new Pair<Integer,DaysOfTheWeek>(R.id.toggleFriday, DaysOfTheWeek.FRIDAY));
        idToggleBtn.add(new Pair<Integer,DaysOfTheWeek>(R.id.toggleSaturday, DaysOfTheWeek.SATURDAY));
        for(Pair<Integer, DaysOfTheWeek> i : idToggleBtn){
            ToggleButton tb = (ToggleButton) m_newAlarm.findViewById(i.first);
            if(tb.isChecked() || tb.isPressed()){
                daysList.add(i.second);
            }
        }
        DaysOfTheWeek[] days = new DaysOfTheWeek[daysList.size()];
        for(int i=0; i<daysList.size(); i++){
            days[i] = daysList.get(i);
        }
        return days;
    }
}
