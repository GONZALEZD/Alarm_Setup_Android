package com.dgo.alarm;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.io.AlarmSettings;
import com.dgo.alarm.io.DBException;
import com.dgo.alarm.io.DBManager;
import com.dgo.alarm.ui.fragments.AlarmFragment;
import com.dgo.alarm.ui.fragments.OverviewFragment;
import com.dgo.alarm.ui.fragments.SettingsFragment;
import com.dgo.alarm.utilities.AlarmIcons;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity{

    private static final String TAG = ""+AlarmActivity.class.getSimpleName();
    private OverviewFragment m_overview;
	private AlarmFragment m_alarm;
	private SettingsFragment m_settings;

    private Fragment m_currentFragment;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.activity_main_panel);

        AlarmSettings settings = new AlarmSettings(this, this.getPreferences(Activity.MODE_PRIVATE));
        AlarmIcons icons = AlarmIcons.getInstance(this);
        icons.resetColors(settings);

		m_overview = new OverviewFragment();
		m_overview.setAlarmActivity(this);
		m_alarm = new AlarmFragment();
		m_settings = new SettingsFragment();
        m_settings.setAlarmSettings(settings);

		setupMainPanel();
		switchPanel(m_overview);

        ColorDrawable drawable= new ColorDrawable();
        drawable.setColor(getResources().getColor(R.color.colorPrimary));
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO|ActionBar.DISPLAY_SHOW_TITLE);
	}
	
	public boolean switchPanel(Fragment to){
        if(to == m_currentFragment){
            return false;
        }
        m_currentFragment = to;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.abc_slide_in_top, R.anim.abc_popup_exit);
        fragmentTransaction.replace(R.id.panelContent, to);
		fragmentTransaction.commit();
        return true;
	}
	
	private void setupMainPanel(){
		// setup all fragments by adding them all
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.panelContent, m_settings);
		fragmentTransaction.replace(R.id.panelContent, m_alarm);
		fragmentTransaction.replace(R.id.panelContent, m_overview);
		fragmentTransaction.commit();

        m_currentFragment = m_overview;
	}
	
	public void switchNewAlarm(Calendar newAlarmDate, boolean oneShot){
        String title;
        if(oneShot){
            title = android.text.format.DateFormat.format("dd MMMM yyyy", newAlarmDate).toString().toUpperCase();
        }
        else{
            title = getString(R.string.menu_add_alarm);
        }
        getSupportActionBar().setTitle(title);
        m_alarm.setDay(newAlarmDate);
        m_alarm.setupOneShot(oneShot);
        m_alarm.setEditionMode(false, null);
        switchPanel(m_alarm);
	}

    public void switchModifyAlarm(Alarm alarm){
        getSupportActionBar().setTitle(alarm.getName());
        m_alarm.setEditionMode(true, alarm);
        switchPanel(m_alarm);
    }

    public void disableAlarm(Alarm alarm, boolean disable){
        DBManager db = DBManager.getInstance(this);
        alarm.setUserDisabled(disable);
        db.deleteTimingExceptions(alarm);
        db.updateAlarm(alarm);
        db.refresh(alarm);
    }

    public void deleteAlarm(Alarm alarm){
        DBManager db = DBManager.getInstance(this);
        try {
            db.deleteAlarm(alarm);
        } catch (DBException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.MSG_unable_delete_alarm,Toast.LENGTH_LONG).show();
        }
        // update overview UI
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(m_overview).attach(m_overview);
        fragmentTransaction.commit();

    }

	@Override
	public void onBackPressed() {
		if(m_currentFragment == m_overview && m_overview.onBackPressed() == false){
			super.onBackPressed();
		} else{
            switchPanel(m_overview);
        }
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment to;
        switch(item.getItemId()){
            case R.id.menu_add_alarm:
                to = m_alarm;
                break;
            case R.id.menu_settings:
                to = m_settings;
                break;
            default:
                to = m_overview;
                break;
        }
        getSupportActionBar().setTitle(item.getTitle());
        return switchPanel(to);
    }
}
