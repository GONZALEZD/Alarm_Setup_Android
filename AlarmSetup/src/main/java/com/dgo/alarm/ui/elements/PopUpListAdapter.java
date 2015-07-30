package com.dgo.alarm.ui.elements;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dgo.alarm.AlarmActivity;
import com.dgo.alarm.R;
import com.dgo.alarm.data.Alarm;
import com.dgo.alarm.data.AlarmType;
import com.dgo.alarm.data.DaysOfTheWeek;
import com.dgo.alarm.io.DBManager;
import com.dgo.alarm.utilities.AlarmIcons;
import com.dgo.alarm.utilities.AlarmIcons.TYPE;
import com.dgo.alarm.utilities.AlarmUtility;

import java.util.Calendar;

public class PopUpListAdapter extends BaseAdapter {

	private static final float TRANSPARENCY_ALARM_TYPE = 1.0f;
	
	private OverviewDay mDay;
	private AlarmActivity mActivity;
	
	public PopUpListAdapter(OverviewDay day, AlarmActivity c) {
		mDay = day;
		mActivity = c;
	}

	@Override
	public int getCount() {
		int count = 0;
		if(mDay != null && mDay.getAlarms() != null){
			count = mDay.getAlarms().size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		if(mDay != null && mDay.getAlarms() != null && mDay.getAlarms().size() > position){
			return mDay.getAlarms().get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout l = null;
		if (convertView != null && (convertView instanceof LinearLayout)) {
			l = (LinearLayout) convertView.findViewById(R.id.popUpItemListLayout);
		}
		else{
			View x = View.inflate(parent.getContext(), R.layout.pop_up_list_item, null);
			l = (LinearLayout) x.findViewById(R.id.popUpItemListLayout);
		}
		
		final Alarm a = mDay.getAlarms().get(position);
		Calendar firstTime = a.getFirstTime();
		String time = DateFormat.format("HH:mm", firstTime).toString();
		String name = a.getName();
		
		TextView timeView = (TextView) l.findViewById(R.id.alarmTime);
		timeView.setText(time+" "+name);
		
		TextView daysView = (TextView) l.findViewById(R.id.alarmDays);
		if(a.getType() == AlarmType.ONE_SHOT){
			daysView.setText(R.string.msg_one_shot_alarm);
		}
		else{
			daysView.setText(DaysOfTheWeek.toString(a.getDays()));
		}
		
		AlarmIcons icons = AlarmIcons.getInstance(parent.getContext());
		ImageView img = (ImageView) l.findViewById(R.id.imageAlarmType);
		img.setImageDrawable(icons.get(TYPE.convertType(a.getType(), false)));
		img.setAlpha((int) (TRANSPARENCY_ALARM_TYPE * 255));
		
		final ToggleButton onOff = (ToggleButton)l.findViewById(R.id.toggleAlarm);
		onOff.setChecked(AlarmUtility.isDisabled(a, mDay.getCurrentDay()) == false);
		
		onOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DBManager db = DBManager.getInstance(mActivity);
				AlarmUtility.setDisabled(db, a, mDay.getCurrentDay(), isChecked == false);
			}
		});

		ImageButton imageButton = (ImageButton) l.findViewById(R.id.itemEdit);

		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                String[] items = mActivity.getResources().getStringArray(R.array.edit_alarm_options);

				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle(mActivity.getString(R.string.alarm_options_title));
                DialogListAdapter adapter = new DialogListAdapter(mActivity);
                adapter.add(R.drawable.ic_create_white_48dp, items[0]);
                if(a.isUserDisabled()){
                    // modify 'disable/enable' option
                    adapter.add(
                            R.drawable.ic_alarm_on_white_48dp,
                            mActivity.getString(R.string.edit_alarm_enable));
                }
                else{
                    adapter.add(R.drawable.ic_alarm_off_white_48dp, items[1]);
                }
                adapter.add(R.drawable.ic_delete_white_48dp, items[2]);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
								mActivity.switchModifyAlarm(a);
                                break;
                            case 1:
								mActivity.disableAlarm(a, a.isUserDisabled() == false);
                                onOff.setChecked(AlarmUtility.isDisabled(a, mDay.getCurrentDay()) == false);
                                break;
                            case 2:
								mActivity.deleteAlarm(a);
                                break;
                            default:
                                break;
                        }
                    }
                });
				builder.show();
			}
		});
		return l;
	}

}
