package com.google.firebase.udacity.friendlychat.Managers;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.MainActivity;
import com.google.firebase.udacity.friendlychat.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paweł on 29.03.2018.
 */

public class UserManager {


    public static User currentUser;
    private static String currentUserID;

    private OnUserDownloadListener mOnUserDownloadListener;

    public UserManager() {
    }

    public UserManager(OnUserDownloadListener onUserDownloadListener) {

        setOnUserDownloadListener(onUserDownloadListener);
        currentUserID = getUserIDFromFirebaseAuth();
        getCurrentUserFromServer();
    }

    private static String getUserIDFromFirebaseAuth() {

        return getUserFromFireBaseAuth().getUid();
    }

    private static FirebaseUser getUserFromFireBaseAuth() {

        return MainActivity.firebaseAuth.getCurrentUser();
    }

    public static String getCurrentUserID() {

        return currentUserID;
    }

    private static void createNewUserAndPush() {

        if (MainActivity.firebaseAuth.getCurrentUser() != null) {
            String userID = getUserIDFromFirebaseAuth();
            String displayName = getUserFromFireBaseAuth().getDisplayName();

            currentUser = new User(userID, displayName, true);

            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users/" + userID);
            userReference.setValue(currentUser);
        }
    }

    public static void onSignOut() {

        changeUserOnlineStatus(false);
        currentUser = null;
        currentUserID = null;
    }

    public static void changeUserOnlineStatus(boolean isOnline) {

        if (currentUser != null) {
            Map<String, Object> timestamp = new HashMap<>();
            timestamp.put("timestamp", ServerValue.TIMESTAMP);
            Map<String, Object> updateStatus = new HashMap<>();
            updateStatus.put("isOnline", isOnline);
            updateStatus.put("timestamp", timestamp);

            DatabaseReference referenceToUpdateUserStatus = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
            referenceToUpdateUserStatus.updateChildren(updateStatus);
        }
    }

    public static void setCurrentUserAvatarUri(Uri photoUri) {
        Map<String, Object> photoUpdate = new HashMap<>();
        currentUser.avatarUri = photoUri.toString();
        photoUpdate.put("avatarUri", photoUri.toString());
        DatabaseReference referenceToUserAvatarUri = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        referenceToUserAvatarUri.updateChildren(photoUpdate);
    }

    private void getCurrentUserFromServer() {

        if (currentUser == null) {
            if (currentUserID == null)
                currentUserID = getUserIDFromFirebaseAuth();
            findUser(currentUserID);
        } else if (mOnUserDownloadListener != null) mOnUserDownloadListener.userDownloaded();
    }

    public void findUser(final String mUserID) {

        ValueEventListener userChangeListener = createUserChangeListener(mUserID);

        DatabaseReference userReference;
        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
        if (!mUserID.equals(currentUserID))
            userReference.addValueEventListener(userChangeListener);
        else
            userReference.addListenerForSingleValueEvent(userChangeListener);
    }

    private ValueEventListener createUserChangeListener(final String mUserID) {

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userData) {

                if (userData.exists()) {

                    downloadUserFromDatabase(userData, mUserID);
                } else if (currentUserExistInDatabase(userData, mUserID)) {

                    Log.i("UserData", "Doesn't exist");
                    createNewUserAndPush();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    private boolean currentUserExistInDatabase(DataSnapshot userData, String mUserID) {

        return !userData.exists() && mUserID.equals(currentUserID);
    }

    private void downloadUserFromDatabase(DataSnapshot userData, String mUserID) {

        User downloadedUser = userData.getValue(User.class);

        if (mUserID.equals(currentUserID) && currentUser == null) {
            Log.i("UserData", "Exist current");
            currentUser = downloadedUser;
            mOnUserDownloadListener.userDownloaded();
        } else {
            Log.i("UserData", "Exist");
            mOnUserDownloadListener.userDownloaded(downloadedUser);
        }
    }

    public void clear() {
        mOnUserDownloadListener = null;
    }

    public void downloadAllUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

        ChildEventListener childEventListener = allUsersListener();

        reference.limitToFirst(10).addChildEventListener(childEventListener);
    }


    private ChildEventListener allUsersListener() {

        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && !user.User_ID.equals(currentUserID))
                    mOnUserDownloadListener.userDownloaded(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void setOnUserDownloadListener(OnUserDownloadListener OnUserDownloadListener) {
        mOnUserDownloadListener = OnUserDownloadListener;
    }

    public interface OnUserDownloadListener {
        void userDownloaded();

        void userDownloaded(User downloadedUser);
    }
}