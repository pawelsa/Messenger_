package com.google.firebase.udacity.friendlychat.TestObjects;

import com.google.firebase.udacity.friendlychat.Objects.ChatRoomUserObject;

import java.util.HashMap;
import java.util.Map;


public class ChatRoomObject {
	
	public String conversationID;
	public Map<String, Object> participants;
	public int chatColor;
	public String lastMessage;
	public HashMap<String, Object> lastMessageSendTime;
	
	ChatRoomObject() {
	}
	
	public ChatRoomObject(String conversationID, String myID, String conversationalistID) {
		participants = new HashMap<>();
		
		participants.put("member1", ChatRoomUserObject.createUser(myID));
		participants.put("member2", ChatRoomUserObject.createUser(conversationalistID));
		this.conversationID = conversationID;
		String hex = "42E1F4";
		chatColor = Integer.parseInt(hex, 16);
	}
}
