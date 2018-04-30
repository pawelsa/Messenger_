package com.google.firebase.udacity.friendlychat;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.udacity.friendlychat.Fragments.AllConversationsFragment;
import com.google.firebase.udacity.friendlychat.Managers.UserOnlineStatus;
import com.google.firebase.udacity.friendlychat.NetworkCheck.NetworkCheckReceiver;
import com.google.firebase.udacity.friendlychat.NetworkCheck.ObserveInternet;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, UserOnlineStatus.UserOnlineStatusListener {

    private static final String BASE_FRAGMENT = "main_fragment";

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
        userOnlineStatus.onDestroy();
        Log.i("State", "stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("State", "destroy");
        unregisterReceiver(networkCheckReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("State", "onResume");
        registerReceiver(networkCheckReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //userOnlineStatus.setupUserOnlineStatus(this, this);
        userOnlineStatus.onActivityResult(requestCode, resultCode);
    }

    @Override
    public void userLoggedIn() {

        Log.i("State", "userLoggedIn");
        startBaseFragment();
    }

    private void startBaseFragment() {
        AllConversationsFragment conversationsFragment = new AllConversationsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.messageFragment, conversationsFragment, BASE_FRAGMENT).commit();
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
