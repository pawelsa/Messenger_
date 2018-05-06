package com.google.firebase.udacity.friendlychat.Managers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.ChatRoomListener;
import com.google.firebase.udacity.friendlychat.ConversationListener;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.currentUser;


public class ListOfConversationsManager {

    public static final String CHAT_ROOM = "chat_room";
    private static final String FRIEND = "friend";
    private static final String USER_CONVERSATIONS = "user_conversations/";
    private ConversationListener conversationListener;

    public ListOfConversationsManager() {
    }

    public static void openChatRoomWith(String conversationalistID) {

        ValueEventListener findFriend = friendListener(conversationalistID);

        DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference().child(FRIEND).child(getCurrentUserID()).child(conversationalistID);
        friendReference.addListenerForSingleValueEvent(findFriend);
    }

    private static ValueEventListener friendListener(final String findFriend) {

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String friend = dataSnapshot.getValue(String.class);

                if (friend == null) createNewUserConversation(findFriend);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    private static void createNewUserConversation(String friendID) {

        String key = addConversationToDatabaseWith(friendID);

        addChatRoomToDatabaseAt(key, friendID);

        addFriendToDatabase(friendID);
    }

    private static String addConversationToDatabaseWith(String friendID) {

        DatabaseReference currentUserConversations = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS + getCurrentUserID());
        DatabaseReference userConversations = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS + friendID);

        String key = userConversations.push().getKey();
        currentUserConversations.child(key).setValue(key);
        userConversations.child(key).setValue(key);

        return key;
    }

    private static void addChatRoomToDatabaseAt(String key, String friendID) {

        ChatRoomObject chatRoomObject = createChatRoomAtWith(key, friendID);

        DatabaseReference userChatRoom = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM);
        userChatRoom.child(key).setValue(chatRoomObject);
    }

    private static ChatRoomObject createChatRoomAtWith(String key, String conversationalistID) {

        return new ChatRoomObject(key, getCurrentUserID(), conversationalistID);
    }

    private static void addFriendToDatabase(String friendID) {

        DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child(FRIEND).child(friendID);
        DatabaseReference me = FirebaseDatabase.getInstance().getReference().child(FRIEND).child(getCurrentUserID());

        friend.child(getCurrentUserID()).setValue(getCurrentUserID());
        me.child(friendID).setValue(friendID);
    }

    private static String getCurrentUserID() {

        return currentUser.User_ID;
    }

    public void loadConversations(ChatRoomListener.OnConversationListener conversationListenerInterface) {

        conversationListener = new ConversationListener(getCurrentUserID(), conversationListenerInterface);
        Log.i("Build", "setLoad...");
    }

    public void clear() {
        conversationListener.destroy();
        conversationListener = null;
    }
}