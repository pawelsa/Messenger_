package com.google.firebase.udacity.friendlychat.Objects;



public class ChatRoom {

    public User conversationalist;
    public ChatRoomObject chatRoomObject;

    public ChatRoom() {
    }

    public ChatRoom(User conversationalist) {

        chatRoomObject = null;
        this.conversationalist = conversationalist;
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
