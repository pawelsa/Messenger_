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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    public static String mUsername = ANONYMOUS;

    private RecyclerView userListRecyclerView;
    UsersAdapter adapter;

    private static FirebaseDatabase mFirebaseDatabase;
    DatabaseReference databaseReferenceToUserList;

    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;

    private ChildEventListener newUserInDatabaseListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gettingDatabaseInstanceAndSettingUpAuthorization();

        userListRecyclerView = findViewById(R.id.userList);
        createContactList();

    }


    private void gettingDatabaseInstanceAndSettingUpAuthorization() {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceToUserList = mFirebaseDatabase.getReference().child("users");

        authorizationSetup();
    }

    private void authorizationSetup() {

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    onSignInInitialize(user.getDisplayName());
                } else {

                    onSignOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignInInitialize(String username) {

        mUsername = username;
    }

    private void onSignOutCleanup() {

        mUsername = ANONYMOUS;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        adapter.clear();
        newUserInDatabaseListener = null;
        changeUserOnlineStatus(false);
    }

    public static void changeUserOnlineStatus(boolean isOnline) {

        if (firebaseAuth.getCurrentUser() != null) {

            String userID = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference updateUserStatus = mFirebaseDatabase.getReference().child("users").child(userID);

            Map<String, Object> timestamp = new HashMap<>();
            timestamp.put("timestamp", ServerValue.TIMESTAMP);

            Map<String, Object> updateStatus = new HashMap<>();
            updateStatus.put("isOnline", isOnline);
            updateStatus.put("timestamp", timestamp);

            updateUserStatus.updateChildren(updateStatus);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseAuth.addAuthStateListener(authStateListener);
        loadContactList();
        changeUserOnlineStatus(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                addOrUpdateUserInDatabaseAfterLogging(true);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Could't login", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void addOrUpdateUserInDatabaseAfterLogging(boolean isOnline) {

        if (firebaseAuth.getCurrentUser() != null) {

            String userID = firebaseAuth.getCurrentUser().getUid();
            String displayName = firebaseAuth.getCurrentUser().getDisplayName();

            Toast.makeText(getApplicationContext(), "Welcome " + displayName, Toast.LENGTH_SHORT).show();

            DatabaseReference addUser = mFirebaseDatabase.getReference().child("users").child(userID);

            User currentUser = new User(userID, displayName, isOnline);

            addUser.setValue(currentUser);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                changeUserOnlineStatus(false);
                AuthUI.getInstance().signOut(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createContactList() {

        adapter = new UsersAdapter(this);
        userListRecyclerView.setAdapter(adapter);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadContactList() {

        addMessageChildListener();
    }

    private void addMessageChildListener() {

        if (newUserInDatabaseListener == null) {

            newUserInDatabaseListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    User receivedUser = dataSnapshot.getValue(User.class);

                    if (!(receivedUser.User_ID.equals(firebaseAuth.getCurrentUser().getUid())))
                        adapter.add(receivedUser.User_Name);
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

            databaseReferenceToUserList.addChildEventListener(newUserInDatabaseListener);
        }
    }
}
