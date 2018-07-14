package com.google.firebase.udacity.friendlychat;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.UserOnlineStatus;
import com.google.firebase.udacity.friendlychat.NetworkCheck.NetworkCheckReceiver;
import com.google.firebase.udacity.friendlychat.NetworkCheck.ObserveInternet;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, UserOnlineStatus.UserOnlineStatusListener {

	IntentFilter intentFilter;
	UserOnlineStatus userOnlineStatus;
	private NetworkCheckReceiver networkCheckReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Log.i("State", "onCreate");
		intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		networkCheckReceiver = new NetworkCheckReceiver();

		userOnlineStatus = UserOnlineStatus.getInstance();
		userOnlineStatus.setupUserOnlineStatus(this, this);

		ObserveInternet.getInstance().addObserver(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("State", "pause");
		userOnlineStatus.onPause();
		unregisterReceiver(networkCheckReceiver);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("State", "stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i("State", "destroy");
		userOnlineStatus.onDestroy();
		unregisterReceiver(networkCheckReceiver);

		FragmentsManager.destroy(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i("State", "onResume");
		userOnlineStatus.onResume();
		registerReceiver(networkCheckReceiver, intentFilter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		userOnlineStatus.onActivityResult(requestCode, resultCode);
	}

	@Override
	public void userLoggedIn() {

		Log.i("State", "userLoggedIn");
		FragmentsManager.startBaseFragment(this);
	}

	@Override
	public void update(Observable observable, Object o) {

		Log.i("onReceive", "internet " + Boolean.toString((boolean) o));
		RelativeLayout internetStatus = findViewById(R.id.internetStatus);
		boolean isNetworkAvailable = (boolean) o;
		if (!isNetworkAvailable) {
			internetStatus.setVisibility(View.VISIBLE);
		} else {
			internetStatus.setVisibility(View.GONE);
		}

	}
}
