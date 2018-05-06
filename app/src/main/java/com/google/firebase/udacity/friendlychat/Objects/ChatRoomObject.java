package com.google.firebase.udacity.friendlychat.Objects;

import java.util.HashMap;


public class ChatRoomObject {

    public String conversationID;
    public String myID;
    public String myPseudonym;
    public String conversationalistID;
    public String conversationalistPseudonym;
    public int chatColor;
    public String lastMessage;
	public HashMap<String, Object> lastMessageSendTime;

    ChatRoomObject() {
    }

    public ChatRoomObject(String conversationID, String myID, String conversationalistID) {
        this.myID = myID;
        this.conversationalistID = conversationalistID;
        this.conversationID = conversationID;
        String hex = "42E1F4";
        chatColor = Integer.parseInt(hex, 16);
    }
}
