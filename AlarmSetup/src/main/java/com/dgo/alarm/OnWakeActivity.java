package com.dgo.alarm;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.dgo.alarm.animation.PulseAnimation;
import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.io.DBManager;
import com.dgo.alarm.utilities.ActivityUtilities;
import com.dgo.alarm.utilities.Prng;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OnWakeActivity extends AppCompatActivity implements OnClickListener{

	private DBManager m_alarms;
	private Alarm m_current;
	private MediaPlayer m_audio;

    private Lock lock = new ReentrantLock();

    private int count=1;
    private Prng randomNbr, randomNbr2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Displayed over the default lock screen
		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		this.setContentView(R.layout.on_wake_activity);

		ColorDrawable drawable= new ColorDrawable();
		drawable.setColor(getResources().getColor(R.color.colorPrimary));
		getSupportActionBar().setBackgroundDrawable(drawable);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);
		
		m_alarms = DBManager.getInstance(this);
		m_current = null;
		m_audio = new MediaPlayer();

		setupFromIntent(getIntent());
		setupUI();
	}
	
	private void setupFromIntent(Intent i){
		if(i.hasExtra(ActivityUtilities.INTENT_EXTRA_NAME_ALARM_ID)){
			long id = i.getLongExtra(ActivityUtilities.INTENT_EXTRA_NAME_ALARM_ID, -1);
			if(id != -1){
				m_current = m_alarms.getAlarm(id);
			}
		}
		if(m_current != null && m_current.isUserDisabled() == false){
			// write alarm name into the menu bar
			String txt = m_current.getName();
			if(txt == null || txt.equals("")){
				txt = getString(R.string.app_name);
			}
			txt += " - "+DateFormat.format("dd MMMM yyyy hh:mm", GregorianCalendar.getInstance());
			getSupportActionBar().setTitle(txt);
			AssetFileDescriptor afd = null;
			try {
				afd = getAssets().openFd(m_current.getSong().getPath());
				m_audio.setLooping(true);
				m_audio.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				m_audio.prepare();
				m_audio.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				if(afd != null){
					try {
						afd.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void setupUI(){
        ImageButton disable = (ImageButton) findViewById(R.id.wakeup_button);
        disable.startAnimation(createAnimation());
		disable.setOnClickListener(this);
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            createAndStartAsyncTask();
        }
    }

    private AsyncTask<Void,Void,Void> createAndStartAsyncTask(){
        RelativeLayout r = (RelativeLayout) findViewById(R.id.layoutPanel);
        int width = r.getWidth();
        int height = r.getHeight();
        randomNbr = new Prng((long)(Math.random()*width)%width, width);
        randomNbr2 = new Prng((long)(Math.random()*height)%height, height);

        AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
            int delay=500, decreaseDelayStep=5, delayStopCondition=0;
            @Override
            protected Void doInBackground(Void... voids) {
                while (delay > delayStopCondition){
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress();
                    delay -= decreaseDelayStep;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                int x = (int) randomNbr.rand();
                int y = (int) randomNbr2.rand();
                addButton(x,y);
            }
        };
        t.execute();
        return t;
    }

    private void addButton(int x, int y){
        RelativeLayout parent = (RelativeLayout) findViewById(R.id.layoutPanel);
        ImageButton ib2 = (ImageButton) getLayoutInflater().inflate(R.layout.wakeup_button, parent, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ib2.getLayoutParams());
//        int[] rules = params.getRules();
//        for(int i=0; i< rules.length; i++){
//            params.removeRule(rules[i]);
//        }
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.setMargins(x, y, 0, 0);
        params.alignWithParent = true;
        ib2.startAnimation(createAnimation());
        ib2.setOnClickListener(this);
        parent.addView(ib2, params);
        synchronized (lock){
            count++;
        }
    }

	@Override
	public void onBackPressed() {
		// disable back button
	}

    public Animation createAnimation(){
        return new PulseAnimation(true, 0.8f, 1.5f, 2.0f, 400);
    }

	@Override
    public void onAttachedToWindow()
    {  
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);     
        super.onAttachedToWindow();  
    }
	
	private boolean insterceptHomeAndBack(int keyCode){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return true;
		}
		if(keyCode==KeyEvent.KEYCODE_HOME){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(insterceptHomeAndBack(keyCode)){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if(insterceptHomeAndBack(keyCode)){
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		if(insterceptHomeAndBack(keyCode)){
			return true;
		}
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(insterceptHomeAndBack(keyCode)){
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


    @Override
    public void onClick(final View view) {
        ScaleAnimation a = new ScaleAnimation(1.f, 0.f, 1.f, 0.F,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        a.setDuration(500);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout parent = (RelativeLayout) findViewById(R.id.layoutPanel);
                parent.removeView(view);
                synchronized (lock) {
                    count--;
                    if (count <= 0) {
                        if (m_audio.isPlaying()) {
                            m_audio.stop();
                        }
                        finish();
                    }
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setClickable(false);
        view.startAnimation(a);
    }
}
