package com.dgo.alarm.ui.elements;

import android.util.SparseArray;

import java.util.Calendar;


public class DayViewList{

	private SparseArray<OverviewDay> m_list;
	
	public DayViewList(){
		m_list = new SparseArray<OverviewDay>();
	}
	
	public void add(OverviewDay day){
		m_list.put(day.getCurrentDay().get(Calendar.DAY_OF_MONTH), day);
	}
	
	public OverviewDay get(int dayOfTheMonth){
		return m_list.get(dayOfTheMonth);
	}
	
	public int size(){
		return m_list.size();
	}
}
