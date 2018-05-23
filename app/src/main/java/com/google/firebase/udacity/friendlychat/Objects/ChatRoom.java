package com.google.firebase.udacity.friendlychat.Objects;


import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
	
	public List<User> conversationalist;
	public ChatRoomObject chatRoomObject;

    public ChatRoom() {
		this.conversationalist = new ArrayList<>();
	}

    public ChatRoom(User conversationalist) {

        chatRoomObject = null;
		this.conversationalist = new ArrayList<>();
		this.conversationalist.add(conversationalist);
	}

    public ChatRoom(String conversationID, String myID, String conversationalistID) {
	
		this.conversationalist = new ArrayList<>();
		//chatRoomObject = new ChatRoomObject(conversationID, myID, conversationalistID);
	}

    public ChatRoom(ChatRoomObject chatRoomObject) {
	
		this.conversationalist = new ArrayList<>();
		this.chatRoomObject = chatRoomObject;
    }


    public boolean isEmpty() {

        return chatRoomObject == null;
    }
}
