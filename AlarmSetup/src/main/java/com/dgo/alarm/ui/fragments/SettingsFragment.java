package com.dgo.alarm.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.dgo.alarm.R;
import com.dgo.alarm.io.AlarmSettings;
import com.dgo.alarm.utilities.AlarmIcons;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;

public class SettingsFragment extends Fragment {

	private static final String TAG = ""+SettingsFragment.class.getSimpleName();
	private View m_settings;
	
	private AlarmSettings m_alarmSettings;

	public void setAlarmSettings(AlarmSettings settings){
		m_alarmSettings = settings;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		m_settings = inflater.inflate(R.layout.settings_layout, null);
		setupSettingsTab();
		return m_settings;
		
	}
	
	private void setupSettingsTab(){
		boolean is5min = m_alarmSettings.is5minSelected();
		
		RadioButton rb1 = (RadioButton)m_settings.findViewById(R.id.tick_1_min);
		rb1.setChecked(is5min == false);
		rb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					m_alarmSettings.setTickSetting(false);
				}
			}
		});
		
		
		RadioButton rb5 = (RadioButton)m_settings.findViewById(R.id.tick_5_min);
		rb5.setChecked(is5min);
		rb5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					m_alarmSettings.setTickSetting(true);
				}
			}
		});

		final AlarmIcons alarmIcons = AlarmIcons.getInstance(getActivity());

		final ImageView colorOneShot = (ImageView) m_settings.findViewById(R.id.image_oneshot_color);
		colorOneShot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final ColorPickerDialog dialog = new ColorPickerDialog(getActivity(), alarmIcons.getOneShotColor(),
						getString(R.string.choose_color_oneshot_title));
				dialog.setAlphaSliderVisible(false);

				dialog.setButton(ColorPickerDialog.BUTTON_POSITIVE, getString(R.string.choose_color_btn), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface d, int which) {
						alarmIcons.setOneShotColor(dialog.getColor());
						m_alarmSettings.setOneShotColor(dialog.getColor());
						colorOneShot.setImageDrawable(alarmIcons.get(AlarmIcons.TYPE.ROUND_ONE_SHOT));
					}
				});
				dialog.setCancelable(true);
				dialog.setButton(ColorPickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface d, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		colorOneShot.setImageDrawable(AlarmIcons.getInstance(getActivity()).get(AlarmIcons.TYPE.ROUND_ONE_SHOT));

		final ImageView colorRecurrent = (ImageView) m_settings.findViewById(R.id.image_recurrent_color);
		colorRecurrent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final ColorPickerDialog dialog = new ColorPickerDialog(getActivity(), alarmIcons.getRecurrentColor(),
						getString(R.string.choose_color_recurrent_title));
				dialog.setAlphaSliderVisible(false);
				dialog.setButton(ColorPickerDialog.BUTTON_POSITIVE, getString(R.string.choose_color_btn), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface d, int which) {
						alarmIcons.setRecurrentColor(dialog.getColor());
						m_alarmSettings.setRecurrentColor(dialog.getColor());
						colorRecurrent.setImageDrawable(alarmIcons.get(AlarmIcons.TYPE.ROUND_RECURRENT));
					}
				});
				dialog.setCancelable(true);
				dialog.setButton(ColorPickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface d, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		colorRecurrent.setImageDrawable(AlarmIcons.getInstance(getActivity()).get(AlarmIcons.TYPE.ROUND_RECURRENT));

		Button resetColor = (Button) m_settings.findViewById(R.id.reset_color_btn);
		resetColor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				int recurrentColor = getResources().getColor(R.color.default_recurrent);
				alarmIcons.setRecurrentColor(recurrentColor);
				m_alarmSettings.setRecurrentColor(recurrentColor);
				colorRecurrent.setImageDrawable(alarmIcons.get(AlarmIcons.TYPE.ROUND_RECURRENT));

				int oneShotColor = getResources().getColor(R.color.default_one_shot);
				alarmIcons.setOneShotColor(oneShotColor);
				m_alarmSettings.setOneShotColor(oneShotColor);
				colorOneShot.setImageDrawable(alarmIcons.get(AlarmIcons.TYPE.ROUND_ONE_SHOT));
			}
		});
	}

}
