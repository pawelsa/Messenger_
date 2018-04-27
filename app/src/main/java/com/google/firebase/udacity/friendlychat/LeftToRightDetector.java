package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Pawel on 25.04.2018.
 */

public class LeftToRightDetector {
    private static final LeftToRightDetector ourInstance = new LeftToRightDetector();

    private LeftToRightDetector() {
    }

    public static GestureDetector getInstance(Activity activity) {
        return detectLeftToRight(activity);
    }

    private static GestureDetector detectLeftToRight(final Activity activity) {
        return new GestureDetector(activity,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                        int width = getOneThirdOfScreenWidth(activity);

                        final int SWIPE_THRESHOLD_VELOCITY = 200;

                        if (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && e2.getX() <= width) {
                            goBack(activity);
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });
    }

    private static int getOneThirdOfScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x / 3;
    }

    public static void goBack(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        Log.i("Fragment quantity", Integer.toString(fm.getBackStackEntryCount()));
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
    }
}
