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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.udacity.friendlychat.Managers.ListOfConversationsManager;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.Arrays;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.changeUserOnlineStatus;
import static com.google.firebase.udacity.friendlychat.Managers.UserManager.currentUser;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    public static String mUsername = ANONYMOUS;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    UsersAdapter adapter;
    UserManager.OnCurrentUserDownloadListener onCurrentUserDownloadListener;
    private RecyclerView userListRecyclerView;
    private RecyclerView allUsersRecyclerView;
    private UsersAdapter allUsersAdapter;
    private Button showAllUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authorizationSetup();
        setOnUserDownloadListener();

        userListRecyclerView = findViewById(R.id.userList);
        allUsersRecyclerView = findViewById(R.id.allUsersList);
        showAllUsers = findViewById(R.id.show_all_users);
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
                }
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

    private void setOnUserDownloadListener() {

        onCurrentUserDownloadListener = createOnUserDownloadListener();
        UserManager.setOnCurrentUserDownloadListener(onCurrentUserDownloadListener);
    }

    private UserManager.OnCurrentUserDownloadListener createOnUserDownloadListener() {

        return new UserManager.OnCurrentUserDownloadListener() {
            @Override
            public void userDownloaded() {

                changeUserOnlineStatus(true);
                createContactList();
                ListOfConversationsManager.setOnConversationListener(createConversationListener());
                ListOfConversationsManager.setLoadConversationIDsListener();

                showAllUsers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userListRecyclerView.setVisibility(View.GONE);
                        allUsersRecyclerView.setVisibility(View.VISIBLE);
                        showAllUsers.setVisibility(View.GONE);

                        allUsersAdapter = new UsersAdapter(getApplicationContext());
                        allUsersRecyclerView.setAdapter(allUsersAdapter);
                        allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        UserManager.downloadAllUsers();
                    }
                });

                UserManager.setOnUserDownloadListener(new UserManager.OnUserDownloadListener() {
                    @Override
                    public void userDownloaded(User downloadedUser) {

                        if (allUsersAdapter != null)
                            allUsersAdapter.add(new ChatRoom(downloadedUser));
                        else
                            adapter.pushUser(downloadedUser);
                    }
                });
            }
        };
    }

    private ListOfConversationsManager.OnConversationListener createConversationListener() {

        return new ListOfConversationsManager.OnConversationListener() {
            @Override
            public void addConversationToAdapter(ChatRoomObject conversation) {

                adapter.updateList(new ChatRoom(conversation));
            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (authStateListener != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
        /*if (adapter != null)
            adapter.clear();
        if (allUsersAdapter != null)
            allUsersAdapter.clear();*/
        if (onCurrentUserDownloadListener != null) {
            onCurrentUserDownloadListener = null;
        }
        changeUserOnlineStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        changeUserOnlineStatus(true);
        firebaseAuth.addAuthStateListener(authStateListener);
        if (userLoggedInButNotDownloaded()) {
            UserManager.getCurrentUserFromServer();
        }
        setOnUserDownloadListener();
    }

    private void createContactList() {

        adapter = new UsersAdapter(this);
        userListRecyclerView.setAdapter(adapter);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                if (userLoggedInButNotDownloaded())
                    UserManager.getCurrentUserFromServer();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                UserManager.onSignOut();
                AuthUI.getInstance().signOut(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
