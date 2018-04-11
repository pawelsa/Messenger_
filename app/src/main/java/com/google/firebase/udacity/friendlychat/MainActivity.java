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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.udacity.friendlychat.Managers.ListOfConversationsManager;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.Arrays;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.changeUserOnlineStatus;
import static com.google.firebase.udacity.friendlychat.Managers.UserManager.currentUser;

public class MainActivity extends AppCompatActivity implements UserManager.OnUserDownloadListener, ChatRoomListener.OnConversationListener {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    public static String mUsername = ANONYMOUS;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    
    private ListOfConversationsManager conversationsManager;
    private UserManager userManager;
    private RecyclerView allUsersRecyclerView;
    private UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authorizationSetup();

        allUsersRecyclerView = findViewById(R.id.allUsersList);
        createAdapterAndSetupRecyclerView();
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
                else {
                    setupUserManager();
                }
            }
        };
    }
    
    private void setupUserManager() {
        if (userManager == null) userManager = new UserManager(this);
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
    
    private void createAdapterAndSetupRecyclerView() {
        
        adapter = new UsersAdapter(this, this);
        allUsersRecyclerView.setAdapter(adapter);
        allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
    
        changeUserOnlineStatus(false);
        
        if (authStateListener != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
        if (adapter != null)
            adapter.clear();
        if (userManager != null) {
            userManager.clear();
            userManager = null;
        }
        if (conversationsManager != null) {
            conversationsManager.clear();
            conversationsManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    
        if (authStateListener == null) {
            authorizationSetup();
        }
        else {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
        
        changeUserOnlineStatus(true);
    
        if (conversationsManager == null) {
            conversationsManager = new ListOfConversationsManager();
        }
        if (userManager == null) {
            userManager = new UserManager(this);
        }
        if (adapter == null) {
            adapter = new UsersAdapter(this, this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
    
                if (userLoggedInButNotDownloaded() && userManager == null) {
                    userManager = new UserManager(this);
                }

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
    
    @Override
    public void addConversationToAdapter(ChatRoomObject conversation) {
        if (adapter != null) adapter.updateList(new ChatRoom(conversation));
    }
    
    @Override
    public void userDownloaded() {
        Log.i("Start", "userDownloaded");
        setupConversationListener();
    }
    
    private void setupConversationListener() {
        if (conversationsManager != null) {
            Log.i("Build", "createOnUser...");
            conversationsManager.loadConversations(this);
        }
    }
    
    @Override
    public void userDownloaded(User downloadedUser) {
        if (adapter != null) adapter.pushUser(downloadedUser);
    }
}
