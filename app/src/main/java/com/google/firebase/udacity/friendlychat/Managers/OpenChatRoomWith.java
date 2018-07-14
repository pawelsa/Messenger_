package com.google.firebase.udacity.friendlychat.Managers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.currentUser;


public class OpenChatRoomWith {

	public static final String CHAT_ROOM = "chat_room";
	public static final String USER_CONVERSATIONS = "user_conversations";
	public static final String PARTICIPANTS = "participants";
	public static final String FRIEND = "friend";
	private static final DatabaseReference baseReference = FirebaseDatabase.getInstance().getReference();
	private FindFriend.FriendListener friendListener;

	public OpenChatRoomWith(FindFriend.FriendListener friendListener) {
		this.friendListener = friendListener;
	}

	public void openChatRoomWith(String conversationalistID, String conversationalistName) {

		ValueEventListener findFriend = friendListener(conversationalistID, conversationalistName);

		DatabaseReference friendReference = baseReference.child(FRIEND).child(getCurrentUserID()).child(conversationalistID);
		friendReference.addListenerForSingleValueEvent(findFriend);
	}

	private ValueEventListener friendListener(final String findFriend, final String conversationalistName) {

		return new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				String friend = dataSnapshot.getValue(String.class);

				if (friend == null) createNewUserConversation(findFriend, conversationalistName);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		};
	}

	private void createNewUserConversation(String friendID, String conversationalistName) {

		String key = addConversationToDatabaseWith(friendID);

		addChatRoomToDatabaseAt(key, friendID, conversationalistName);

		addFriendToDatabase(friendID);
	}

	private String addConversationToDatabaseWith(String friendID) {

		DatabaseReference currentUserConversations = baseReference.child(USER_CONVERSATIONS + "/" + getCurrentUserID());
		DatabaseReference userConversations = baseReference.child(USER_CONVERSATIONS + "/" + friendID);

		String key = userConversations.push().getKey();
		currentUserConversations.child(key).setValue(key);
		userConversations.child(key).setValue(key);

		return key;
	}

	private void addChatRoomToDatabaseAt(String key, String friendID, String conversationalistName) {

		ChatRoomObject chatRoomObject = createChatRoomAtWith(key, friendID, conversationalistName);
		friendListener.foundFriend(chatRoomObject.conversationID);

		DatabaseReference userChatRoom = baseReference.child(CHAT_ROOM);
		userChatRoom.child(key).setValue(chatRoomObject);
	}

	private ChatRoomObject createChatRoomAtWith(String key, String conversationalistID, String conversationalistName) {

		return new ChatRoomObject(key, getCurrentUserID(), conversationalistID, conversationalistName);
	}

	private void addFriendToDatabase(String friendID) {

		DatabaseReference friend = baseReference.child(FRIEND).child(friendID);
		DatabaseReference me = baseReference.child(FRIEND).child(getCurrentUserID());

		friend.child(getCurrentUserID()).setValue(getCurrentUserID());
		me.child(friendID).setValue(friendID);
	}

	private String getCurrentUserID() {

		return currentUser.User_ID;
	}
}