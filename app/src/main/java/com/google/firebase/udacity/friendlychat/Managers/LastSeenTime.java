package com.google.firebase.udacity.friendlychat.Managers;

import android.content.res.Resources;

import com.google.firebase.udacity.friendlychat.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LastSeenTime {

	public static String getLastSeenOnlineStatusMessage(long lastSeen, Resources resources) {

		long diff = returnTimeSinceLastOnlineTime(lastSeen);

		String onlineStatusMessage = resources.getString(R.string.last_seen);

		long lastTimeOnline = TimeUnit.MILLISECONDS.toDays(diff);
		if (lastTimeOnline <= 0) {
			lastTimeOnline = TimeUnit.MILLISECONDS.toHours(diff);
			if (lastTimeOnline <= 0) {
				lastTimeOnline = TimeUnit.MILLISECONDS.toMinutes(diff);
				if (lastTimeOnline <= 0) {
					onlineStatusMessage = resources.getString(R.string.moment_ago);
					return onlineStatusMessage;
				} else {
					onlineStatusMessage += lastTimeOnline + resources.getString(R.string.minute);
				}
			} else {
				onlineStatusMessage += lastTimeOnline + resources.getString(R.string.hour);
			}
		} else {
			onlineStatusMessage += lastTimeOnline + resources.getString(R.string.day);
		}

		onlineStatusMessage += (lastTimeOnline == 1 ? "" : resources.getString(R.string.plural)) + resources.getString(R.string.ago);

		return onlineStatusMessage;
	}

	private static long returnTimeSinceLastOnlineTime(long time) {
		Date lastOnline = new Date(time);
		Date actualTime = new Date();
		return actualTime.getTime() - lastOnline.getTime();
	}
}
