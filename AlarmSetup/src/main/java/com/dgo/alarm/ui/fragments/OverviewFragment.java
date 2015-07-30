package com.dgo.alarm.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.dgo.alarm.AlarmActivity;
import com.dgo.alarm.R;
import com.dgo.alarm.ui.elements.MonthGridAdapter;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class OverviewFragment extends Fragment {
	
	private View m_overview;
//	private MonthGridAdapter m_gridAdapter;
    private AlarmActivity activity;
	private CalendarFragment currentMonth;
    private CalendarPagerAdapter calendarPager;
    private ViewPager pager;

    private static final String SAVE_CURRENT_PAGE = "currentPos";



    public void setAlarmActivity(AlarmActivity activity){
        this.activity = activity;
    }

	private void setupPager(){

        calendarPager = new CalendarPagerAdapter(getChildFragmentManager());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Calendar c = calendarPager.getCalendarFromPosition(position);
                String title = DateFormat.format("MMMM yyyy", c).toString().toUpperCase();
                activity.getSupportActionBar().setTitle(title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setAdapter(calendarPager);
        pager.setCurrentItem(calendarPager.getCount() - 1);
        pager.setCurrentItem(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_CURRENT_PAGE, pager.getCurrentItem());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            setupPager();
            pager.setCurrentItem(savedInstanceState.getInt(SAVE_CURRENT_PAGE));
        }
        super.onViewStateRestored(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		m_overview = inflater.inflate(R.layout.overview_pager, null);

        View addButton = m_overview.findViewById(R.id.buttonAddAlarmFloating);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OverviewFragment.this.activity != null && currentMonth != null) {
                    boolean oneShot = true;
                    Calendar day = currentMonth.getCurrentDay();
                    if (day == null) {
                        day = Calendar.getInstance();
                        oneShot = false;
                    }
                    OverviewFragment.this.activity.switchNewAlarm(day, oneShot);
                }
            }
        });

        pager = (ViewPager) m_overview.findViewById(R.id.pager);
        setupPager();

        return m_overview;
	}
	
	public boolean onBackPressed() {
		LinearLayout l = (LinearLayout)m_overview.findViewById(R.id.pop_up);
		if(l != null && l.getVisibility() == View.VISIBLE && currentMonth != null){
            currentMonth.getGridAdapter().updateContent();
			currentMonth.onBackPressed();
            String title = DateFormat.format("MMMM yyyy",
                    currentMonth.m_gridAdapter.getCalendarCurrentMonth()).toString().toUpperCase();
            activity.getSupportActionBar().setTitle(title);
			return true;
		}
		return false;
	}

	private class CalendarPagerAdapter extends FragmentStatePagerAdapter{

        private FragmentManager fragmentManager;

		public CalendarPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
		}

		@Override
		public Fragment getItem(int position) {
			CalendarFragment c = new CalendarFragment();
			c.setCurrentMonth(getCalendarFromPosition(position));
			return c;
		}

        @Override
		public int getCount() {
			return 12;
		}

        public Calendar getCalendarFromPosition(int position){
            Calendar c = GregorianCalendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.MONTH, position);
            return c;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            currentMonth = (CalendarFragment) object;
            if(currentMonth.getGridAdapter() != null) {
                currentMonth.getGridAdapter().updateContent();
            }
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

	@SuppressLint("ValidFragment")
    public class CalendarFragment extends Fragment{

		public static final String ARG_NAME_MONTH = "month";
        private final String TAG = ""+CalendarFragment.class.getSimpleName();
		private MonthGridAdapter m_gridAdapter;
        private View rootView;
        private Calendar currentMonth;

        public void setCurrentMonth(Calendar currentMonth){
            this.currentMonth = currentMonth;
        }

        @Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.overview_layout, container, false);

            m_gridAdapter = new MonthGridAdapter(activity, this, 6*7);
            m_gridAdapter.setup(currentMonth);
            GridView g = (GridView)rootView.findViewById(R.id.gridOverview);
            g.setAdapter(m_gridAdapter);
            return rootView;
		}

        public void onBackPressed(){
			m_gridAdapter.onBackPressed();
		}

		public MonthGridAdapter getGridAdapter(){
			return m_gridAdapter;
		}

		public Calendar getCurrentDay(){
			if(m_gridAdapter != null){
				return m_gridAdapter.getCurrentDay();
			}
			return null;
		}

        public void setOverviewGridEltsVisibility(int visibility){
            if(rootView != null) {
                GridView v = (GridView) rootView.findViewById(R.id.gridOverview);
                for (int i = 0; i < v.getChildCount(); i++) {
                    v.getChildAt(i).setVisibility(visibility);
                }
            }
        }
	}
}
