package com.google.firebase.udacity.friendlychat.Managers;


import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.ChatRoom;
import com.google.firebase.udacity.friendlychat.ChatRoomObject;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.currentUser;

/**
 * Created by Paweł on 30.03.2018.
 */

public class ListOfConversationsManager {

    private static OnConversationListener mOnConversationListener;

    private static String conversationalist;

    ListOfConversationsManager() {
    }

    public static void setLoadConversationIDsListener() {

        ChildEventListener listener = createConversationListener();

        if (conversationalist != null) {

            createNewUserConversation();
        }

        DatabaseReference userConversations = FirebaseDatabase.getInstance().getReference().child("user_conversations/" + currentUser.User_ID);
        userConversations.addChildEventListener(listener);
    }

    private static void createNewUserConversation() {

        DatabaseReference userConversations = FirebaseDatabase.getInstance().getReference().child("user_conversations/" + currentUser.User_ID);

        String key = userConversations.push().getKey();
        userConversations.child(key).setValue(key);

        ChatRoomObject chatRoomObject = createChatRoomAt(key).chatRoomObject;
        DatabaseReference userChatRoom = FirebaseDatabase.getInstance().getReference().child("chat_room");
        userChatRoom.child(key).setValue(chatRoomObject);
    }

    private static ChatRoom createChatRoomAt(String key) {

        return new ChatRoom(key, UserManager.currentUser.User_ID, conversationalist);
    }

    private static ChildEventListener createConversationListener() {

        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String conversationKey = dataSnapshot.getValue(String.class);

                if (conversationKey != null) {
                    addChatRoomListener(conversationKey);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    private static void addChatRoomListener(String conversationID) {

        ValueEventListener newConversationListener = createChatRoomListener();

        DatabaseReference referenceToChatRooms = FirebaseDatabase.getInstance().getReference().child("chat_room").child(conversationID);
        referenceToChatRooms.addValueEventListener(newConversationListener);
    }

    private static ValueEventListener createChatRoomListener() {

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("Conversation Key", dataSnapshot.getKey());

                ChatRoomObject addedChatRoom = dataSnapshot.getValue(ChatRoomObject.class);
                if (conversationalist == null && addedChatRoom != null) {
                    mOnConversationListener.addConversationToAdapter(addedChatRoom);
                } else {

                    String conversationKey = dataSnapshot.getKey();

                    if (downloadedUserEqualsConversationalist(addedChatRoom, conversationKey)) {

                        ChatRoom chatRoom = createChatRoomAt(conversationKey);
                        conversationalist = null;
                        DatabaseReference chatRoomReference = FirebaseDatabase.getInstance().getReference().child("chat_room").child(conversationKey);
                        chatRoomReference.setValue(chatRoom.chatRoomObject);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    private static boolean downloadedUserEqualsConversationalist(ChatRoomObject addedChatRoom, String conversationKey) {

        return (addedChatRoom != null && addedChatRoom.conversationalistID.equals(conversationalist)) || (conversationalist.equals(conversationKey));
    }

    public static void openChatRoomWith(String conversationalistID) {

        conversationalist = conversationalistID;
        setLoadConversationIDsListener();
    }


    public static void setOnConversationListener(OnConversationListener mOnConversationListener) {
        ListOfConversationsManager.mOnConversationListener = mOnConversationListener;
    }

    public interface OnConversationListener {

        void addConversationToAdapter(ChatRoomObject conversation);
    }

}