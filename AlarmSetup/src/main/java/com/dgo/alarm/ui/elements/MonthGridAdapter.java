package com.dgo.alarm.ui.elements;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dgo.alarm.AlarmActivity;
import com.dgo.alarm.R;
import com.dgo.alarm.animation.PulseAnimation;
import com.dgo.alarm.data.DaysOfTheWeek;
import com.dgo.alarm.ui.fragments.OverviewFragment;
import com.dgo.alarm.utilities.AlarmIcons;
import com.dgo.alarm.utilities.AlarmIcons.TYPE;
import com.dgo.alarm.utilities.CalendarUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthGridAdapter extends BaseAdapter {
	
	private AlarmActivity mActivity;
	private OverviewFragment.CalendarFragment fragment;
	private int size;
	private DayViewList list;
	private String[] dayNames;
	private int firstDayPosition;
	private ArrayList<View> internalElts;
	private Calendar currentMonth;

    private OverviewDay m_currentOverviewDay;

	private MonthGridAnimHandler dayAnim;
	
	private static final float TRANSPARENCY_BCK_NORMAL = 0.9f;
	
	public MonthGridAdapter(AlarmActivity activity, OverviewFragment.CalendarFragment calendarFragment, int nbClones){
		size = nbClones + 7;
		fragment = calendarFragment;
		mActivity = activity;
		list = null;
		firstDayPosition = 7;
        dayNames = new String[]{
                mActivity.getString(R.string.sunday_name),
                mActivity.getString(R.string.monday_name),
                mActivity.getString(R.string.tuesday_name),
                mActivity.getString(R.string.wednesday_name),
                mActivity.getString(R.string.thursday_name),
                mActivity.getString(R.string.friday_name),
                mActivity.getString(R.string.saturday_name),
        };
		internalElts = new ArrayList<View>(size);
		currentMonth = GregorianCalendar.getInstance();
		
		dayAnim = new MonthGridAnimHandler(mActivity);
	}
	
	public void setup(Calendar calendarMonth){
		currentMonth = calendarMonth;
		this.list = DaysViewFactory.getDayViewList(mActivity, currentMonth);
		int dayInt = DaysOfTheWeek.fromCalendarDay(list.get(1).getCurrentDay().get(Calendar.DAY_OF_WEEK)).toInt();

		if(mActivity.getResources().getInteger(R.integer.first_day_of_the_week) == 0){
			String[] days2 = {dayNames[1], dayNames[2],dayNames[3],dayNames[4],dayNames[5],dayNames[6],dayNames[0]};
			dayNames = days2;
			// -1 % 7  is equivalent to -1, so do +6 % 7 instead
			dayInt = (dayInt+6) % 7;
		}
		firstDayPosition = 7 + dayInt +1;
	}

	@Override
	public int getCount() {
		return size;
	}

	@Override
	public Object getItem(int position) {
		if(position >= firstDayPosition && position < (firstDayPosition + list.size())){
    		return list.get(position +1 - firstDayPosition);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private View getDayView(int position, View convertView, ViewGroup parent){
		TextView tv = null;
		if (convertView == null || !(convertView instanceof TextView)) {
			// if it's not recycled, initialize some attributes
			tv = new TextView(mActivity);
		}
		else{
			tv = (TextView) convertView;
		}
		tv.setText(dayNames[position].substring(0, 3));
		tv.setTextAppearance(this.mActivity, R.style.Widget_TextView);
		tv.setGravity(Gravity.CENTER);
		internalElts.add(position, tv);
		return tv;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(internalElts.size() > position && internalElts.get(position) != null && position < firstDayPosition){
			return internalElts.get(position);
		}
		if(position < 7){
			return getDayView(position, convertView, parent);
		}
		final RelativeLayout relativeLayout;
		int size = parent.getWidth()/mActivity.getResources().getInteger(R.integer.grid_columns);
		View v;
        if (convertView == null || (convertView instanceof RelativeLayout)==false) {
        	// if it's not recycled, initialize some attributes
        	relativeLayout = new RelativeLayout(mActivity);
        	relativeLayout.setLayoutParams(new GridView.LayoutParams(size, size));
        	View x = View.inflate(mActivity, R.layout.day_button, relativeLayout);
        	v = x.findViewById(R.id.dayButtonLayout);
        	internalElts.add(position, relativeLayout);
        }
        else{
        	relativeLayout = (RelativeLayout) convertView;
        	v = relativeLayout.findViewById(R.id.dayButtonLayout);
        	relativeLayout.getChildAt(0).getBackground().setAlpha((int)(TRANSPARENCY_BCK_NORMAL*255));
        }

    	final StringBuffer popUpName = new StringBuffer();
    	if(position >= firstDayPosition && position < (firstDayPosition + list.size())){
    		final OverviewDay day = list.get(position +1 - firstDayPosition);
        	if(day != null){
        		v.setBackground(day.getBackground());
        		v.setAlpha(1.f);

        		TextView dayText = (TextView) v.findViewById(R.id.textIconDay);
            	dayText.setText(day.getDayNumberString());

            	TextView firstAlarm = (TextView) v.findViewById(R.id.textFirstAlarm);
            	firstAlarm.setText(day.getTimeFirstAlarmString());

            	popUpName.append(day.getDateString());

                if(CalendarUtility.areSameDay(day.getCurrentDay(), Calendar.getInstance())){
                    // current day
					relativeLayout.startAnimation(new PulseAnimation(true, 0.8f, 0.97f,1.03f,600));
                }
        	}
        	relativeLayout.setOnClickListener(new OnClickListener() {

        		class MyAnimatorListener implements AnimatorListener{
        			RelativeLayout rootView;

        			public MyAnimatorListener(RelativeLayout root){
        				rootView = root;
        			}

					@Override
					public void onAnimationCancel(Animator animation) {
                        m_currentOverviewDay = null;
                    }
					@Override
					public void onAnimationEnd(Animator animation) {}
					@Override
					public void onAnimationRepeat(Animator animation) {}
					@Override
					public void onAnimationStart(Animator animation) {
                        m_currentOverviewDay = day;
						LinearLayout pop_up = (LinearLayout)mActivity.findViewById(R.id.pop_up);
						popupSetup(pop_up, rootView, day, popUpName.toString());
					}
        		}

				@Override
				public void onClick(View v) {
					final RelativeLayout l = (RelativeLayout)v;
					View popup = l.getChildAt(0);
					l.invalidate();
                    Drawable d = AlarmIcons.getInstance(mActivity).get(day.getAlarmsIconType(false));
					dayAnim.playZoom(popup, d, new MyAnimatorListener(l));
				}
			});
    	}
    	else{
    		// for days of the non-current month
    		AlarmIcons icons = AlarmIcons.getInstance(mActivity);

    		v.setBackground(icons.get(TYPE.ROUND_NONE));
            v.setAlpha(0.2f);
    		popUpName.append("");
    	}

        return relativeLayout;

	}

    public void updateContent(){
		OverviewDay none = OverviewDay.getDefaultOverview(mActivity);
		for(int i=0; i<internalElts.size(); i++){
			if(internalElts.get(i) instanceof RelativeLayout){
				RelativeLayout r = (RelativeLayout) internalElts.get(i);
				TextView dayText = (TextView) r.findViewById(R.id.textIconDay);
	        	dayText.setText(none.getDayNumberString());
	        	r.getChildAt(0).setBackground(none.getBackground());
				
				TextView firstAlarm = (TextView) r.findViewById(R.id.textFirstAlarm);
	        	firstAlarm.setText("");				
			}
		}
		this.list = DaysViewFactory.getDayViewList(mActivity, currentMonth);
		Calendar curr = GregorianCalendar.getInstance();
		curr.set(Calendar.MONTH, currentMonth.get(Calendar.MONTH));
		for(int i=1;i<= curr.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
			OverviewDay day = list.get(i);
			int id = i-1 + firstDayPosition;
			if(day != null && internalElts.size() > id){
				RelativeLayout relativeLayout = (RelativeLayout)internalElts.get(id);
				View v = relativeLayout.findViewById(R.id.dayButtonLayout);
				if(v != null){
					TextView dayText = (TextView) v.findViewById(R.id.textIconDay);
		        	dayText.setText(day.getDayNumberString());
					
					
					v.setBackground(day.getBackground());
					
					TextView firstAlarm = (TextView) v.findViewById(R.id.textFirstAlarm);
                	firstAlarm.setText(day.getTimeFirstAlarmString());
				}				
			}
		}
	}

	public Calendar getCalendarCurrentMonth(){
		return currentMonth;
	}
	
	public void onBackPressed(){
		dayAnim.cancelAnimation();
		m_currentOverviewDay = null;
	}

    public Calendar getCurrentDay(){
        if(m_currentOverviewDay != null){
            return m_currentOverviewDay.getCurrentDay();
        }
        return null;
    }

    private void popupSetup(LinearLayout pop_up, View rootView, OverviewDay day, String popUpName){
        pop_up.setTag(rootView);
        pop_up.setVisibility(View.VISIBLE);
        pop_up.bringToFront();
        pop_up.requestLayout();
        // set pop-up title
        mActivity.getSupportActionBar().setTitle(popUpName);

        //set pop-up content
        day.refreshAlarms();
        ListView l = (ListView) pop_up.findViewById(R.id.popUpAlarmList);
        l.setAdapter(new PopUpListAdapter(day, mActivity));
    }
}
