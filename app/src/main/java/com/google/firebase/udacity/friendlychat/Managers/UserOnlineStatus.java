package com.google.firebase.udacity.friendlychat.Managers;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.Arrays;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.firebase.udacity.friendlychat.Managers.UserManager.changeUserOnlineStatus;


public class UserOnlineStatus implements UserManager.OnUserDownloadListener {

	//TODO: Delete userSettingsFragment, when user logs out

	public static final int RC_SIGN_IN = 1;
	private static final UserOnlineStatus ourInstance = new UserOnlineStatus();
	static FirebaseAuth firebaseAuth;
	private static FirebaseAuth.AuthStateListener authStateListener;
	private static UserOnlineStatusListener userOnlineStatusListener;
	private static UserManager userManager;
	private Activity mainActivity;

	private UserOnlineStatus() {
	}

	public static UserOnlineStatus getInstance() {
		return ourInstance;
	}

	public void setupUserOnlineStatus(Activity activity, UserOnlineStatusListener userOnlineStatusListener) {
		mainActivity = activity;
		UserOnlineStatus.userOnlineStatusListener = userOnlineStatusListener;
		authorizationSetup();
	}

	private void authorizationSetup() {

		firebaseAuth = FirebaseAuth.getInstance();

		authStateListener = newAuthStateListener();

		firebaseAuth.addAuthStateListener(authStateListener);
	}

	private FirebaseAuth.AuthStateListener newAuthStateListener() {
		return firebaseAuth -> {

			FirebaseUser user = firebaseAuth.getCurrentUser();

			if (user == null) {
				UserManager.onSignOut();
				mainActivity.startActivityForResult(createSignUpOrLoginScreenIntent(), RC_SIGN_IN);
			} else {
				setupUserManager();
			}
		};
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

	public void onActivityResult(int requestCode, int resultCode) {

		if (requestCode == RC_SIGN_IN) {
			if (resultCode == RESULT_OK && userOnlineStatusListener != null) {
				authorizationSetup();

			} else if (resultCode == RESULT_CANCELED) {
				if (UserManager.currentUser != null && userOnlineStatusListener != null) {
					authorizationSetup();
				}
				Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getResources().getString(R.string.couldnt_login), Toast.LENGTH_SHORT).show();
				mainActivity.finish();
			}
		}

	}

	private void setupUserManager() {
		userManager = new UserManager(this);
	}

	public void logOut() {

		userManager.clear();
		UserManager.onSignOut();
		FirebaseAuth.getInstance().signOut();
	}

	public void onPause() {
		changeUserOnlineStatus(false);

		if (authStateListener != null)
			firebaseAuth.removeAuthStateListener(authStateListener);
	}

	public void onResume() {
		changeUserOnlineStatus(true);
		//firebaseAuth.addAuthStateListener(authStateListener);
	}

	public void onDestroy() {
		onPause();
		authStateListener = null;
		firebaseAuth = null;
		if (userManager != null) {
			userManager.clear();
		}
	}

	@Override
	public void userDownloaded() {
		changeUserOnlineStatus(true);
		if (userOnlineStatusListener != null)
			userOnlineStatusListener.userLoggedIn();
	}

	@Override
	public void userDownloaded(User downloadedUser) {
	}

	public interface UserOnlineStatusListener {
		void userLoggedIn();
	}
}
