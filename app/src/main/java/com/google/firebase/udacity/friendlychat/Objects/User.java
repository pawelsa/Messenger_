package com.google.firebase.udacity.friendlychat.Objects;

import android.os.Bundle;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class User {

	private static final String CONVERSATIONALIST_ID = "conversationalist_id";
	private static final String CONVERSATIONALIST_DISPLAY_NAME = "conversationalist_display_name";
	private static final String CONVERSATIONALIST_AVATAR_URL = "conversationalist_avatar_url";


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

	@Override
	public boolean equals(Object obj) {
		super.equals(obj);

		boolean result = false;

		if (obj instanceof User) {
			User other = (User) obj;
			result = this.User_ID.equals(other.User_ID);
		}
		return result;
	}

	@Override
	public int hashCode() {

		return User_ID.hashCode();
	}
}
