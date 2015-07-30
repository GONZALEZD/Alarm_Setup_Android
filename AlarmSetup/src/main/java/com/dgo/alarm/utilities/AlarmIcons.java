package com.dgo.alarm.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dgo.alarm.R;
import com.dgo.alarm.data.AlarmType;
import com.dgo.alarm.io.AlarmSettings;

import java.util.HashMap;

public class AlarmIcons {
	public enum TYPE{
		ONE_SHOT,
		RECURRENT,
		BOTH,
		NONE,
		ROUND_NONE,
		ROUND_ONE_SHOT,
		ROUND_RECURRENT,
		ROUND_BOTH;
		
		public static TYPE convertType(AlarmType from, boolean round){
			if(round){
				switch (from) {
				case ONE_SHOT:
					return TYPE.ROUND_ONE_SHOT;
				case RECURRENT:
					return TYPE.ROUND_RECURRENT;
				default:
					return TYPE.ROUND_NONE;
				}
			}
			else{
				switch (from) {
				case ONE_SHOT:
					return TYPE.ONE_SHOT;
				case RECURRENT:
					return TYPE.RECURRENT;
				default:
					return TYPE.NONE;
				}
			}
		}
	}
	
	private static final float ICON_CORNER_RADIUS = 20.0f; 
	
	private static AlarmIcons m_instance;
	
	private HashMap<TYPE, Drawable> m_icons;
	private Context m_context;
	private int m_oneShotColor;
	private int m_recurrentColor;
	
	private AlarmIcons(Context context, int oneShot, int recurrent){
		m_icons = new HashMap<AlarmIcons.TYPE, Drawable>();
		m_context = context;
		m_oneShotColor = oneShot;
		m_recurrentColor = recurrent;
	}
	
	public synchronized static AlarmIcons getInstance(Context context){
		if(m_instance == null){
			int oneShot = context.getResources().getColor(R.color.default_one_shot);
			int recurrent = context.getResources().getColor(R.color.default_recurrent);
			m_instance = new AlarmIcons(context, oneShot, recurrent);
		}
		return m_instance;
	}
	
	public Drawable get(AlarmType type){
		return get(TYPE.convertType(type, true));
	}
	
	public Drawable get(TYPE type){
		if(m_icons.containsKey(type)){
			return m_icons.get(type);
		}
		// lazy drawable creation
		Drawable base = null;
		boolean roundCorner = false;
		switch(type){
		case ROUND_ONE_SHOT:
		case ROUND_RECURRENT:
		case ROUND_NONE:
			roundCorner = true;
		case ONE_SHOT:
		case RECURRENT:
		case NONE:
			base = m_context.getResources().getDrawable(R.drawable.single_alarm_icon);
			break;
		case ROUND_BOTH:
			roundCorner = true;
		case BOTH:
			base = m_context.getResources().getDrawable(R.drawable.both_alarms_icon);
			break;
		default:
			return null;
		}
		// copy the base
		Drawable rez = base.getConstantState().newDrawable();
		Bitmap ori = ((BitmapDrawable) rez).getBitmap();
		// set the bitmap copy mutable (for changing pixels)
		Bitmap bitmap = ori.copy(Bitmap.Config.ARGB_8888, true);
		// Add corners to the created bitmap
		if(roundCorner){
			bitmap = setRoundedCorner(bitmap);
		}
		
		rez = new BitmapDrawable(bitmap);
		// change color
		switch (type) {
		case ROUND_ONE_SHOT:
		case ONE_SHOT:
			changeColor(bitmap, Color.WHITE, m_oneShotColor);
			break;
		case ROUND_RECURRENT:
		case RECURRENT:
			changeColor(bitmap, Color.WHITE, m_recurrentColor);
			break;
		case ROUND_BOTH:
		case BOTH:
			changeColor(bitmap, Color.WHITE, m_oneShotColor);
			changeColor(bitmap, Color.BLACK, m_recurrentColor);
			break;
		default:
			changeColor(bitmap, Color.WHITE, m_context.getResources().getColor(
					R.color.default_alarm_icon));
			break;
		}
		
		// store new colored drawable
		m_icons.put(type, rez);
		return rez;
	}
	
	private void changeColor(Bitmap bitmap, int colorFrom, int colorTo)
	{
	    for(int x = 0;x < bitmap.getWidth();x++)
	        for(int y = 0;y < bitmap.getHeight();y++)
	            if(bitmap.getPixel(x, y) == colorFrom) 
	                bitmap.setPixel(x, y, colorTo);
	}
	
	private Bitmap setRoundedCorner(Bitmap bitmap){
		Bitmap bmp;
		 
	    bmp = Bitmap.createBitmap(bitmap.getWidth(), 
	        bitmap.getHeight(), Bitmap.Config.ARGB_8888);
	    BitmapShader shader = new BitmapShader(bitmap, 
	        BitmapShader.TileMode.CLAMP, 
	        BitmapShader.TileMode.CLAMP);
	 
	    Canvas canvas = new Canvas(bmp);
	    Paint paint = new Paint();
	    paint.setAntiAlias(true);
	    paint.setShader(shader);
	 
	    RectF rect = new RectF(0, 0, 
	        bitmap.getWidth(), bitmap.getHeight());
	    canvas.drawRoundRect(rect, ICON_CORNER_RADIUS, ICON_CORNER_RADIUS, paint);
	 
	    return bmp;
	}
	
	public void setOneShotColor(int newColor){
		if(newColor == m_oneShotColor){
			return;
		}
		m_oneShotColor = newColor;

		//re-generate icons
		clearIcons();
	}

	/*
	 * Should be used when wanting the system to regenerate all icons
	  * (after having a color changed for example)
	 */
	private void clearIcons(){
		m_icons.clear();
	}

	public void resetColors(AlarmSettings from){
		if(from != null){
			m_oneShotColor = from.getOneShotColor();
			m_recurrentColor = from.getRecurrentColor();
			clearIcons();
		}
	}

	public int getOneShotColor() {
		return m_oneShotColor;
	}


	public int getRecurrentColor() {
		return m_recurrentColor;
	}

	public void setRecurrentColor(int newColor) {
		if(newColor == m_recurrentColor){
			return;
		}
		m_recurrentColor = newColor;

		//re-generate icons
		clearIcons();
	}
}
