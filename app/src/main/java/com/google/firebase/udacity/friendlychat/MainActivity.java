/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.udacity.friendlychat;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.NetworkCheck.ObserveInternet;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.changeUserOnlineStatus;
import static com.google.firebase.udacity.friendlychat.Managers.UserManager.currentUser;

public class MainActivity extends AppCompatActivity implements UserManager.OnUserDownloadListener, Observer {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    public static String mUsername = ANONYMOUS;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
	
	private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

        ObserveInternet.getInstance().addObserver(this);
	}


    @Override
    protected void onPause() {
        super.onPause();

        changeUserOnlineStatus(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (authStateListener != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
        if (userManager != null) {
            userManager.clear();
            userManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isNetworkAvailable()) {
            Log.i("Internet", "internet");
            RelativeLayout internetStatus = findViewById(R.id.internetStatus);
            internetStatus.setVisibility(View.VISIBLE);
        }

        if (userManager == null || authStateListener == null) {
            authorizationSetup();
        } else {
            changeUserOnlineStatus(true);
        }

        openLastFragment();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void openLastFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        fragmentManager.popBackStack(count, 0);
    }

    private void authorizationSetup() {

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    UserManager.onSignOut();
                    startActivityForResult(createSignUpOrLoginScreenIntent(), RC_SIGN_IN);
                } else {
                    setupUserManager();
                }
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void setupUserManager() {
        if (userLoggedInButNotDownloaded() || userManager == null) {
			changeUserOnlineStatus(true);
	
			userManager = new UserManager(this);
		}
	}

    private Intent createSignUpOrLoginScreenIntent() {

        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                setupUserManager();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Could't login", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private boolean userLoggedInButNotDownloaded() {

        return firebaseAuth.getCurrentUser() != null && currentUser == null;
    }

    @Override
    public void userDownloaded() {
		AllConversationsFragment conversationsFragment = new AllConversationsFragment();
	
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.messageFragment, conversationsFragment, "main_fragment").commit();
	}

    @Override
    public void userDownloaded(User downloadedUser) {
    }

    @Override
    public void update(Observable observable, Object o) {
        //TODO: show no internet connection layout
    }
}
