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

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

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
	private UserOnlineStatusListener userOnlineStatusListener;

	private Activity mainActivity;

	private User currentUser;
	private String currentUserID;

	private Disposable authenticationListener;
	private Disposable downloadCurrentUser;


	private UserOnlineStatus() {
	}

	public void setupUserOnlineStatus(Activity activity, UserOnlineStatusListener userOnlineStatusListener) {
		mainActivity = activity;
		this.userOnlineStatusListener = userOnlineStatusListener;
	}

	public void startAuthentication() {


		if (authenticationListener == null || authenticationListener.isDisposed())
			authenticationListener = Observable.create(emitter -> {

				firebaseAuth = FirebaseAuth.getInstance();

				final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {

					FirebaseUser user = firebaseAuth.getCurrentUser();

					emitter.onNext(user != null);
				};

				emitter.setCancellable(() -> firebaseAuth.removeAuthStateListener(authStateListener));

				firebaseAuth.addAuthStateListener(authStateListener);
			})
					.map(result -> (boolean) result)
					.distinctUntilChanged()
					.doOnNext(loggedIn -> {
						if (loggedIn)
							Log.i("UserOnlineStatus", "User logged");
						else
							Log.i("UserOnlineStatus", "User not logged");
					})
					.subscribe(
							loggedIn -> {
								if (loggedIn)
									startApplication();
								else
									launchLoginScreen();
							},
							Throwable::printStackTrace,
							() -> Log.i("AuthStateListener", "Completed"));

	}


	private void startApplication() {

		currentUserID = firebaseAuth.getCurrentUser().getUid();
		downloadCurrentUser = getCurrentUserFromServer();
	}

	private void launchLoginScreen() {
		//signOut();
		mainActivity.startActivityForResult(createSignUpOrLoginScreenIntent(), RC_SIGN_IN);
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
				startAuthentication();

			} else if (resultCode == RESULT_CANCELED) {
				if (UserManager.currentUser != null && userOnlineStatusListener != null) {
					startAuthentication();
				}
				Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getResources().getString(R.string.couldnt_login), Toast.LENGTH_SHORT).show();
				mainActivity.finish();
			}
		}

	}

	private Disposable getCurrentUserFromServer() {

		return SearchForUser.searchUserByID(currentUserID)
				.subscribeOn(Schedulers.io())
				.doOnSuccess(sth -> Log.i("UserOnlineStatus", "doOnSuccess getUserFromServer"))
				.doOnComplete(() -> Log.i("UserOnlineStatus", "doOnComplete getUserFromServer"))
				.doOnDispose(() -> Log.i("UserOnlineStatus", "Disposing getUserFromServer"))
				.subscribeWith(new DisposableMaybeObserver<User>() {
					@Override
					public void onSuccess(User user) {
						currentUser = user;
						UserManager.setCurrentUser(user);
						userOnlineStatusListener.userLoggedIn();
						Log.i("Current user", "Downloaded");
						if (!UserManager.currentUser.isOnline)
							changeUserOnlineStatus(true);
						dispose();
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onComplete() {
						if (currentUser == null)
							createNewUserAndPush();
						Log.i("UserOnlineStatus", "OnComplete");
					}
				})
				/*.subscribe(user -> {
							currentUser = user;
							UserManager.setCurrentUser(user);
							userOnlineStatusListener.userLoggedIn();
							Log.i("Current user", "Downloaded");
						},
						Throwable::printStackTrace,
						() -> {
							if (currentUser == null)
								createNewUserAndPush();
							Log.i("UserOnlineStatus", "OnComplete");
						})*/
				;
	}

	private void createNewUserAndPush() {

		Log.i("Current user", "Creating New User");
		currentUser = getNewCurrentUser();
		UserManager.setCurrentUser(currentUser);

		DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(currentUserID);
		userReference.setValue(currentUser);
	}

	private User getNewCurrentUser() {

		FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

		String userID = firebaseUser.getUid();
		String displayName = firebaseUser.getDisplayName();
		String photoUri = firebaseUser.getPhotoUrl().toString();

		return new User(userID, displayName, true, photoUri);
	}

	public void signOut() {
		changeUserOnlineStatus(false);

		if (downloadCurrentUser != null && !downloadCurrentUser.isDisposed()) {
			downloadCurrentUser.dispose();
		}

		currentUser = null;
		currentUserID = null;
		UserManager.setCurrentUser(null);

		Log.i("UserOnlineStatus", "signOut " + downloadCurrentUser.isDisposed());

		firebaseAuth.signOut();
		FragmentsManager.destroy((AppCompatActivity) mainActivity);
	}

	public void onPause() {
		changeUserOnlineStatus(false);

/*		if (authStateListener != null)
			firebaseAuth.removeAuthStateListener(authStateListener);*/

		if (authenticationListener != null && !authenticationListener.isDisposed()) {
			authenticationListener.dispose();
			authenticationListener = null;
		}
	}

	public void onResume() {
		if (currentUser != null && currentUserID != null)
			changeUserOnlineStatus(true);
		//firebaseAuth.addAuthStateListener(authStateListener);
		startAuthentication();

/*		if (currentUser == null && downloadCurrentUser != null && downloadCurrentUser.isDisposed()) {
			downloadCurrentUser = getCurrentUserFromServer();
		}*/
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
/*		authStateListener = null;
		firebaseAuth = null;*/

		if (downloadCurrentUser != null && !downloadCurrentUser.isDisposed()) {
			downloadCurrentUser.dispose();
			downloadCurrentUser = null;
		}
	}

	public interface UserOnlineStatusListener {
		void userLoggedIn();
	}
}
