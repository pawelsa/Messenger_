package com.google.firebase.udacity.friendlychat.Objects;

import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.HashMap;
import java.util.Map;


public class ChatRoomObject {
	
	public String conversationID;
	public String conversationName;
	public String conversationPictureUrl;
	public Map<String, Object> participants;
	public int chatColor;
    public String lastMessage;
	public HashMap<String, Object> lastMessageSendTime;
	
	ChatRoomObject() {
    }
	
	public ChatRoomObject(String conversationID, String myID, String conversationalistID, String conversationalistName) {
		participants = new HashMap<>();

		participants.put(myID, ChatRoomUserObject.createUser(myID, UserManager.getCurrentUserName()));
		participants.put(conversationalistID, ChatRoomUserObject.createUser(conversationalistID, conversationalistName));
		this.conversationID = conversationID;
        String hex = "42E1F4";
        chatColor = Integer.parseInt(hex, 16);
    }
}
