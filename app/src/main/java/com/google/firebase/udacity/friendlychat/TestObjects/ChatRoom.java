package com.google.firebase.udacity.friendlychat.TestObjects;

import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
	
	public List<User> conversationalist;
	public ChatRoomObject chatRoomObject;
	
	public ChatRoom() {
	}
	
	public ChatRoom(User conversationalist) {
		
		chatRoomObject = null;
		this.conversationalist = new ArrayList<>();
		this.conversationalist.add(conversationalist);
	}
	
	public ChatRoom(String conversationID, String myID, String conversationalistID) {
		
		chatRoomObject = new ChatRoomObject(conversationID, myID, conversationalistID);
	}
	
	public ChatRoom(ChatRoomObject chatRoomObject) {
		
		this.chatRoomObject = chatRoomObject;
	}
	
	
	public boolean isEmpty() {
		
		return chatRoomObject == null;
	}
}
