package com.google.firebase.udacity.friendlychat.Managers.NetworkCheck;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cantrowitz.rxbroadcast.RxBroadcast;

import io.reactivex.Observable;

public class NetworkCheck {

	public static Observable<Boolean> connectivityChanges(final Context context, final ConnectivityManager connectivityManager) {
		return RxBroadcast.fromBroadcast(context, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
				.map(intent -> {
					NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
					return networkInfo != null && networkInfo.isConnected();
				})
				.distinctUntilChanged();
    }
}
