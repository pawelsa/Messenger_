package com.google.firebase.udacity.friendlychat.Objects;

import android.os.Bundle;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATIONALIST_AVATAR_URL;
import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATIONALIST_DISPLAY_NAME;
import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATIONALIST_ID;

public class User {

    public String User_ID;
    public String User_Name;
    public String avatarUri;
    public boolean isOnline;
    public HashMap<String, Object> timestamp;

    public User() {

    }

    public User(String user_ID, String user_Name, String avatarUri) {
        User_ID = user_ID;
        User_Name = user_Name;
        this.avatarUri = avatarUri;
    }

    public User(String user_ID, String user_Name, boolean isOnline) {
        User_ID = user_ID;
        User_Name = user_Name;
        this.isOnline = isOnline;
        timestamp = new HashMap<>();
        timestamp.put("timestamp", ServerValue.TIMESTAMP);
        this.avatarUri = "null";
    }

    public User(Bundle bundle) {
        User_ID = bundle.getString(CONVERSATIONALIST_ID);
        User_Name = bundle.getString(CONVERSATIONALIST_DISPLAY_NAME);
        this.avatarUri = bundle.getString(CONVERSATIONALIST_AVATAR_URL);
    }

    public Bundle getSettingsBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(CONVERSATIONALIST_ID, User_ID);
        bundle.putString(CONVERSATIONALIST_DISPLAY_NAME, User_Name);
        bundle.putString(CONVERSATIONALIST_AVATAR_URL, avatarUri);
        return bundle;
    }
}
