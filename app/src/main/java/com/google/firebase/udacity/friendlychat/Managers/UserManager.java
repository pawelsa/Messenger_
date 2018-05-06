package com.google.firebase.udacity.friendlychat.Managers;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.udacity.friendlychat.Managers.ListOfConversationsManager.CHAT_ROOM;


public class UserManager {

    private static final String USERS = "users";
    private static final String TIMESTAMP = "timestamp";
    private static final String IS_ONLINE = "isOnline";
    private static final String AVATAR_URI = "avatarUri";

    public static User currentUser;
    private static String currentUserID;

    private OnUserDownloadListener mOnUserDownloadListener;

    public UserManager() {
    }

    public UserManager(OnUserDownloadListener onUserDownloadListener) {

        setOnUserDownloadListener(onUserDownloadListener);

        if (currentUser != null) {
            Log.i("setupUserManager", "constructior");
            currentUserID = currentUser.User_ID;
            mOnUserDownloadListener.userDownloaded();
            return;
        } else {
            currentUserID = getUserIDFromFirebaseAuth();
        }
        getCurrentUserFromServer();
    }

    private static String getUserIDFromFirebaseAuth() {

        return getUserFromFireBaseAuth().getUid();
    }

    private static FirebaseUser getUserFromFireBaseAuth() {

        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getCurrentUserID() {

        return currentUserID;
    }

    private static void createNewUserAndPush() {

        if (UserOnlineStatus.firebaseAuth.getCurrentUser() != null) {
            String userID = getUserIDFromFirebaseAuth();
            String displayName = getUserFromFireBaseAuth().getDisplayName();

            currentUser = new User(userID, displayName, true);

            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(userID);
            userReference.setValue(currentUser);
        }
    }

    static void onSignOut() {

        changeUserOnlineStatus(false);
        currentUser = null;
        currentUserID = null;
    }

    static void changeUserOnlineStatus(boolean isOnline) {

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

    public static void setCurrentUserAvatarUri(Uri photoUri) {
        Map<String, Object> photoUpdate = new HashMap<>();
        currentUser.avatarUri = photoUri.toString();
        photoUpdate.put(AVATAR_URI, photoUri.toString());
        DatabaseReference referenceToUserAvatarUri = FirebaseDatabase.getInstance().getReference().child(USERS).child(currentUserID);
        referenceToUserAvatarUri.updateChildren(photoUpdate);
    }

    public static void updateUserPseudonym(final String newPseudonym, final String conversationID) {

        if (!newPseudonym.equals("")) {
            DatabaseReference pseudonymReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID).child("conversationalistID");
            ValueEventListener check = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String ID = (String) dataSnapshot.getValue();

                    DatabaseReference pseudonymReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID);
                    Map<String, Object> pseudonym = new HashMap<>();
                    if (ID.equals(/*users.get(position).User_ID)*/ currentUserID)) {
                        pseudonym.put("myPseudonym", newPseudonym);
                    } else {
                        pseudonym.put("conversationalistPseudonym", newPseudonym);
                    }
                    pseudonymReference.updateChildren(pseudonym);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            pseudonymReference.addListenerForSingleValueEvent(check);
        }
    }

    private void getCurrentUserFromServer() {
        if (currentUser != null) {
            mOnUserDownloadListener.userDownloaded();
        } else if (currentUserID != null && mOnUserDownloadListener != null) {
            findUser(currentUserID);
        }
    }

    public void findUser(final String mUserID) {

        ValueEventListener userChangeListener = createUserChangeListener(mUserID);

        DatabaseReference userReference;
        userReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(mUserID);
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

        if (downloadedUser != null) {
            if (mUserID.equals(currentUserID) && currentUser == null) {
                Log.i("UserData", "Exist current");
                currentUser = downloadedUser;
                mOnUserDownloadListener.userDownloaded();
            } else {
                Log.i("UserData", "Exist");
                mOnUserDownloadListener.userDownloaded(downloadedUser);
            }
        }
    }

    void clear() {
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

    private void setOnUserDownloadListener(OnUserDownloadListener OnUserDownloadListener) {
        mOnUserDownloadListener = OnUserDownloadListener;
    }

    public interface OnUserDownloadListener {
        void userDownloaded();

        void userDownloaded(User downloadedUser);
    }
}