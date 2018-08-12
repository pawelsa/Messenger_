package com.google.firebase.udacity.friendlychat.Objects;

import com.google.firebase.udacity.friendlychat.Managers.Database.UserManager;

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

	public ChatRoomObject(String conversationID, String conversationalistID, String conversationalistName) {
		participants = new HashMap<>();

		String myID = UserManager.getCurrentUserID();
		participants.put(myID, ChatRoomUserObject.createUser(myID, UserManager.getCurrentUserName()));
		participants.put(conversationalistID, ChatRoomUserObject.createUser(conversationalistID, conversationalistName));
		this.conversationID = conversationID;
        String hex = "42E1F4";
        chatColor = Integer.parseInt(hex, 16);
    }
}
