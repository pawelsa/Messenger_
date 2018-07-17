package com.google.firebase.udacity.friendlychat.Managers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

public class ActionBarManager {

	public static ColorDrawable getActionBarColor(int color) {
		String hex = Integer.toHexString(color);
		while (hex.length() < 6) {
			hex = "0" + hex;
		}
		return new ColorDrawable(Color.parseColor("#" + hex));
	}

	public static int getStatusBarColor(int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			float[] hsv = new float[3];
			Color.colorToHSV(color, hsv);
			hsv[2] *= 0.8f; // value component
			color = Color.HSVToColor(hsv);
			return color;
		}
		return -1;
	}

}
