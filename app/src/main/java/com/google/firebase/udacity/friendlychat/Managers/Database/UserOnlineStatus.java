package com.google.firebase.udacity.friendlychat.Managers.Database;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.udacity.friendlychat.Managers.App.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.firebase.udacity.friendlychat.Managers.Database.UserManager.USERS;


public class UserOnlineStatus {

	private static final UserOnlineStatus ourInstance = new UserOnlineStatus();

	public static UserOnlineStatus getInstance() {
		return ourInstance;
	}


	//TODO: Delete userSettingsFragment, when user logs out
	private static final String IS_ONLINE = "isOnline";
	private static final String TIMESTAMP = "timestamp";
	private static final int RC_SIGN_IN = 1;


	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener authStateListener;
	private UserOnlineStatusListener userOnlineStatusListener;

	private Activity mainActivity;

	private User currentUser;
	private String currentUserID;

	private Disposable downloadCurrentUser;


	private UserOnlineStatus() {
	}

	public void setupUserOnlineStatus(Activity activity, UserOnlineStatusListener userOnlineStatusListener) {
		mainActivity = activity;
		this.userOnlineStatusListener = userOnlineStatusListener;
		//authorizationSetup();
	}

	public void authorizationSetup() {
		if (firebaseAuth == null && authStateListener == null) {
			firebaseAuth = FirebaseAuth.getInstance();
			authStateListener = newAuthStateListener();
			firebaseAuth.addAuthStateListener(authStateListener);
		}
	}

	private FirebaseAuth.AuthStateListener newAuthStateListener() {
		return firebaseAuth -> {

			FirebaseUser user = firebaseAuth.getCurrentUser();

			if (user == null) {
				signOut();
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

		currentUserID = getUserIDFromFirebaseAuth();
		downloadCurrentUser = getCurrentUserFromServer();
	}

	private Disposable getCurrentUserFromServer() {

		return SearchForUser.searchUserByID(currentUserID)
				.subscribe(user -> {
							currentUser = user;
							UserManager.setCurrentUser(user);
							userOnlineStatusListener.userLoggedIn();
							Log.i("Current user", "Downloaded");
						},
						Throwable::printStackTrace,
						() -> {
							if (currentUser == null) {
								Log.i("Current user", "Not downloaded");
								createNewUserAndPush();
							} else {
								Log.i("Current user", "Downloaded");
							}
						});
	}

	private void createNewUserAndPush() {

		String userID = getUserIDFromFirebaseAuth();
		String displayName = firebaseAuth.getCurrentUser().getDisplayName();

		currentUser = new User(userID, displayName, true);

		DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(userID);
		userReference.setValue(currentUser);
	}

	private String getUserIDFromFirebaseAuth() {
		return firebaseAuth.getCurrentUser().getUid();
	}

	public void signOut() {

		changeUserOnlineStatus(false);
		currentUser = null;
		currentUserID = null;
		firebaseAuth.signOut();
		FragmentsManager.destroy((AppCompatActivity) mainActivity);
	}

	public void onPause() {
		changeUserOnlineStatus(false);

		if (authStateListener != null)
			firebaseAuth.removeAuthStateListener(authStateListener);
	}

	public void onResume() {
		changeUserOnlineStatus(true);
		//firebaseAuth.addAuthStateListener(authStateListener);
		if (currentUser == null && downloadCurrentUser != null && downloadCurrentUser.isDisposed()) {
			downloadCurrentUser = getCurrentUserFromServer();
		}
	}

	private void changeUserOnlineStatus(boolean isOnline) {

		if (currentUser != null) {
			Map<String, Object> timestamp = new HashMap<>();
			timestamp.put(TIMESTAMP, ServerValue.TIMESTAMP);
			Map<String, Object> updateStatus = new HashMap<>();
			updateStatus.put(IS_ONLINE, isOnline);
			updateStatus.put(TIMESTAMP, timestamp);

			DatabaseReference referenceToUpdateUserStatus = FirebaseDatabase.getInstance().getReference().child(USERS).child(currentUserID);
			referenceToUpdateUserStatus.updateChildren(updateStatus);
		}
	}

	public void onDestroy() {

		onPause();
		authStateListener = null;
		firebaseAuth = null;

		if (downloadCurrentUser != null && !downloadCurrentUser.isDisposed()) {
			downloadCurrentUser.dispose();
		}
	}

	public interface UserOnlineStatusListener {
		void userLoggedIn();
	}
}
