package com.dgo.alarm.ui.elements;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.dgo.alarm.R;

public class MonthGridAnimHandler {
	
	private static final int ANIM_DURATION = 200;

	private AnimatorSet currentAnimator;
	private Activity activity;
	
	private CancelAnimation onCancel;
	
	public MonthGridAnimHandler(Activity a){
		activity = a;
		onCancel = null;
	
	}
	
	public void cancelAnimation(){
		if(onCancel != null){
			onCancel.process();
		}
	}
	
	public void playZoom(View from, Drawable popupBackground, AnimatorListener listener){
		zoomImageFromDayThumb(from, popupBackground, listener);
	}
	
	private void zoomImageFromDayThumb(final View dayView, Drawable extended, AnimatorListener listener) {
	    // If there's an animation in progress, cancel it
	    // immediately and proceed with this one.
	    if (onCancel != null) {
	    	if(currentAnimator != null){
	    		currentAnimator.cancel();
	    	}
	    	onCancel.process();
	    }
	    onCancel = null;

	    // Load the high-resolution "zoomed-in" image.
	    final LinearLayout popup = (LinearLayout) activity.findViewById(R.id.pop_up);

		Drawable d = extended.getConstantState().newDrawable();
		d.setTint(activity.getResources().getColor(R.color.popup_tint));
		d.setTintMode(PorterDuff.Mode.SCREEN);

	    popup.setBackground(d);

//	    popup.buildDrawingCache(true);
	    popup.refreshDrawableState();
	    popup.invalidate();
	    popup.destroyDrawingCache();
	    

	    // Calculate the starting and ending bounds for the zoomed-in image.
	    // This step involves lots of math. Yay, math.
	    final Rect startBounds = new Rect();
	    final Rect finalBounds = new Rect();
	    final Point globalOffset = new Point();

	    // The start bounds are the global visible rectangle of the thumbnail,
	    // and the final bounds are the global visible rectangle of the container
	    // view. Also set the container view's offset as the origin for the
	    // bounds, since that's the origin for the positioning animation
	    // properties (X, Y).
	    dayView.getGlobalVisibleRect(startBounds);
	    popup.getGlobalVisibleRect(finalBounds, globalOffset);
	    startBounds.offset(-globalOffset.x, -globalOffset.y);
	    finalBounds.offset(-globalOffset.x, -globalOffset.y);

	    // Adjust the start bounds to be the same aspect ratio as the final
	    // bounds using the "center crop" technique. This prevents undesirable
	    // stretching during the animation. Also calculate the start scaling
	    // factor (the end scaling factor is always 1.0).
	    final float startScale;
	    if ((float) finalBounds.width() / finalBounds.height()
	            > (float) startBounds.width() / startBounds.height()) {
	        // Extend start bounds horizontally
	        startScale = (float) startBounds.height() / finalBounds.height();
	        float startWidth = startScale * finalBounds.width();
	        float deltaWidth = (startWidth - startBounds.width()) / 2;
	        startBounds.left -= deltaWidth;
	        startBounds.right += deltaWidth;
	    } else {
	        // Extend start bounds vertically
	        startScale = (float) startBounds.width() / finalBounds.width();
	        float startHeight = startScale * finalBounds.height();
	        float deltaHeight = (startHeight - startBounds.height()) / 2;
	        startBounds.top -= deltaHeight;
	        startBounds.bottom += deltaHeight;
	    }

	    // Hide the thumbnail and show the zoomed-in view. When the animation
	    // begins, it will position the zoomed-in view in the place of the
	    // thumbnail.
	    //dayView.setAlpha(0f);
	    popup.setVisibility(View.VISIBLE);
	    popup.bringToFront();

	    // Set the pivot point for SCALE_X and SCALE_Y transformations
	    // to the top-left corner of the zoomed-in view (the default
	    // is the center of the view).
	    popup.setPivotX(0f);
	    popup.setPivotY(0f);

	    // Construct and run the parallel animation of the four translation and
	    // scale properties (X, Y, SCALE_X, and SCALE_Y).
	    AnimatorSet set = new AnimatorSet();
	    set.play(ObjectAnimator.ofFloat(popup, View.X, startBounds.left, finalBounds.left))
	            .with(ObjectAnimator.ofFloat(popup, View.Y, startBounds.top, finalBounds.top))
	            .with(ObjectAnimator.ofFloat(popup, View.SCALE_X, startScale, 1f))
	            .with(ObjectAnimator.ofFloat(popup, View.SCALE_Y, startScale, 1f))
	            ;
	    set.setDuration(ANIM_DURATION);
	    set.setInterpolator(new DecelerateInterpolator());
	    set.addListener(listener);
	    onCancel = new CancelAnimation();
	    set.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {}
			
			@Override
			public void onAnimationRepeat(Animator animation) {}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				currentAnimator = null;
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {}
		});
	    
	    set.start();
	    currentAnimator = set;
	}
	
	private class CancelAnimation{
		
		public void process(){
			LinearLayout popup = (LinearLayout) activity.findViewById(R.id.pop_up);
			
			if(currentAnimator != null){
				currentAnimator.resume();
			}
			else{
				popup.setBackground(null);
				popup.setVisibility(View.GONE);
			}
		}
	}
	
}
