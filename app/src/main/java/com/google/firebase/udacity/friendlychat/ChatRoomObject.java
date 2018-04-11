package com.google.firebase.udacity.friendlychat;

import android.graphics.Color;

import java.util.HashMap;

/**
 * Created by Paweł on 31.03.2018.
 */

public class ChatRoomObject {

    public String conversationID;
    public String myID;
    public String conversationalistID;
    public int chatColor;
    public String lastMessage;
	public HashMap<String, Object> lastMessageSendTime;

    ChatRoomObject() {
    }

    public ChatRoomObject(String conversationID, String myID, String conversationalistID) {
        this.myID = myID;
        this.conversationalistID = conversationalistID;
        this.conversationID = conversationID;
        chatColor = Color.BLUE;
    }
}
