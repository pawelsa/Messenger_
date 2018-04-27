package com.google.firebase.udacity.friendlychat;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Paweł on 25.03.2018.
 */

public class User {

    public String User_ID;
    public String User_Name;
    public String avatarUri;
    public boolean isOnline;
    public HashMap<String, Object> timestamp;

    public User() {

    }

    public User(String user_ID, String user_Name, boolean isOnline) {
        User_ID = user_ID;
        User_Name = user_Name;
        this.isOnline = isOnline;
        timestamp = new HashMap<>();
        timestamp.put("timestamp", ServerValue.TIMESTAMP);
        this.avatarUri = "null";
    }
}
