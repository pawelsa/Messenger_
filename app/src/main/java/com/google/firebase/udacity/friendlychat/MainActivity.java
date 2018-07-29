package com.google.firebase.udacity.friendlychat;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.udacity.friendlychat.Managers.App.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.Database.UserOnlineStatus;
import com.google.firebase.udacity.friendlychat.Managers.NetworkCheck.NetworkCheckReceiver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements UserOnlineStatus.UserOnlineStatusListener {

	IntentFilter intentFilter;
	UserOnlineStatus userOnlineStatus;

	private Disposable networkCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Log.i("State", "onCreate");
		intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		userOnlineStatus = UserOnlineStatus.getInstance();
		userOnlineStatus.setupUserOnlineStatus(this, this);
		startNetworkCheck();
	}

	private void startNetworkCheck() {

		if (networkCheck == null || networkCheck.isDisposed())
			networkCheck = NetworkCheckReceiver.connectivityChanges(this, (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(connection -> {
						Log.i("Network connection New", connection.toString());
						RelativeLayout internetStatus = findViewById(R.id.internetStatus);
						internetStatus.setVisibility(!connection ? View.VISIBLE : View.GONE);
						if (connection) {
							userOnlineStatus.authorizationSetup();
							userOnlineStatus.onResume();
						} else
							userOnlineStatus.onPause();
					});
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("State", "pause");
		userOnlineStatus.onPause();

		if (networkCheck != null && !networkCheck.isDisposed()) {
			Log.i("Network connection New", "disposed");
			networkCheck.dispose();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (networkCheck != null && !networkCheck.isDisposed()) {
			Log.i("Network connection New", "disposed");
			networkCheck.dispose();
		}
		Log.i("State", "stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i("State", "destroy");
		userOnlineStatus.onDestroy();

		if (networkCheck != null && !networkCheck.isDisposed()) {
			Log.i("Network connection New", "disposed");
			networkCheck.dispose();
		}

		//FragmentsManager.destroy(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i("State", "onResume");
		userOnlineStatus.onResume();
		startNetworkCheck();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		userOnlineStatus.onActivityResult(requestCode, resultCode);
	}

	@Override
	public void userLoggedIn() {

		Log.i("State", "userLoggedIn");
		FragmentsManager fragmentManager = FragmentsManager.getInstance();
		fragmentManager.startBaseFragment(this);
	}
}
