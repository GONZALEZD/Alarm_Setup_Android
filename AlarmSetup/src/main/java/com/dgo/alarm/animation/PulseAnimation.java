package com.dgo.alarm.animation;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by david.gonzalez on 28/07/2015.
 */
public class PulseAnimation extends AnimationSet{

    public PulseAnimation(boolean infiniteLoop, float minAlpha, float minScale, float maxScale, int duration){
        super(true);
        setup(infiniteLoop, minAlpha, minScale, maxScale, duration);
    }

    private void setup(boolean infiniteLoop, float minAlpha, float minScale, float maxScale, int duration){
        Interpolator interpolator = new OvershootInterpolator(5.f);

        Animation a = new AlphaAnimation(minAlpha, 1.0f);


        Animation a2 = new ScaleAnimation(maxScale, minScale, maxScale, minScale,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);


        this.setDuration(duration);
        this.setInterpolator(interpolator);
        this.addAnimation(a);
        this.addAnimation(a2);

        if(infiniteLoop){
            a.setRepeatMode(Animation.REVERSE);
            a.setRepeatCount(Animation.INFINITE);
            a2.setRepeatMode(Animation.REVERSE);
            a2.setRepeatCount(Animation.INFINITE);
        }
    }
}
