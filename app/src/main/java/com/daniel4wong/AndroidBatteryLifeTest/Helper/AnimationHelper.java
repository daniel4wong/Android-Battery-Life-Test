package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class AnimationHelper {

    public static Animation getFlashAnimation() {
        Animation animation = new AlphaAnimation(1f, 0.5f);
        animation.setDuration(200);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(5);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
