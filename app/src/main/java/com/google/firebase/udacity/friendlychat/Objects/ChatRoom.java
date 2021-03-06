package com.google.firebase.udacity.friendlychat.Objects;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom {


	public List<User> conversationalist;
	public ChatRoomObject chatRoomObject;


    public ChatRoom(ChatRoomObject chatRoomObject) {
	
		this.conversationalist = new ArrayList<>();
		this.chatRoomObject = chatRoomObject;
    }

	public ChatRoom(List<User> conversationalist, ChatRoomObject chatRoomObject) {
		this.conversationalist = conversationalist;
		this.chatRoomObject = chatRoomObject;
	}

	public String getConversationID() {
		return chatRoomObject.conversationID;
	}

    public boolean isEmpty() {

        return chatRoomObject == null;
    }

	@Override
	public int hashCode() {
		return chatRoomObject.conversationID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		super.equals(obj);

		boolean result = false;

		if (obj instanceof ChatRoom) {
			ChatRoom other = (ChatRoom) obj;
			result = this.chatRoomObject.conversationID.equals(other.chatRoomObject.conversationID);
		}
		return result;
	}

	public static Map<String, Object> toMap(ChatRoomObject chatRoomObject) {
		Map<String, Object> chatRoomMap = new HashMap<>();
		chatRoomMap.put("chatColor", chatRoomObject.chatColor);
		chatRoomMap.put("conversationID", chatRoomObject.conversationID);
		chatRoomMap.put("participants", chatRoomObject.participants);

		return chatRoomMap;
	}
}
