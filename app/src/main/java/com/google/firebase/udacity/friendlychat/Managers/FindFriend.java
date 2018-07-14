package com.google.firebase.udacity.friendlychat.Managers;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.HaveToBeRemoved.ChatRoomListener;
import com.google.firebase.udacity.friendlychat.Objects.User;

import static com.google.firebase.udacity.friendlychat.Managers.OpenChatRoomWith.FRIEND;
import static com.google.firebase.udacity.friendlychat.Managers.OpenChatRoomWith.USER_CONVERSATIONS;

public class FindFriend {

	private final DatabaseReference baseReference = FirebaseDatabase.getInstance().getReference();
	private User friend;
	private FriendListener listener;
	private String key;

	public FindFriend(User friend, FriendListener listener) {
		this.friend = friend;
		this.listener = listener;
	}

	public void checkIfHasFriend() {

		String friendID = friend.User_ID;

		ValueEventListener hasFriend = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					obtainChatRoomKey();
				} else {
					OpenChatRoomWith openChatRoomWith = new OpenChatRoomWith(listener);
					openChatRoomWith.openChatRoomWith(friendID, friend.User_Name);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		};
		DatabaseReference findFriendReference = baseReference.child(FRIEND).child(UserManager.getCurrentUserID()).child(friendID);
		findFriendReference.addListenerForSingleValueEvent(hasFriend);
	}

	private void obtainChatRoomKey() {

		DatabaseReference findCommonChatRoom = baseReference.child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID());
		ChildEventListener commonChatRoomListener = newCommonChatRoomListener();

		findCommonChatRoom.addChildEventListener(commonChatRoomListener);
	}

	private ChildEventListener newCommonChatRoomListener() {
		return new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				if (dataSnapshot.exists()) {
					key = dataSnapshot.getKey();

					DatabaseReference friendChatRoom = baseReference.child(USER_CONVERSATIONS).child(friend.User_ID).child(key);
					ValueEventListener hasChatRoom = newDoesFriendHasChatRoomListener();
					friendChatRoom.addListenerForSingleValueEvent(hasChatRoom);
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

	private ValueEventListener newDoesFriendHasChatRoomListener() {
		return new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					ChatRoomListener chatRoomListener = new ChatRoomListener(key, conversation -> {
						if (conversation.participants.size() <= 2) {
							listener.foundFriend(conversation.conversationID);
						}
					});
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		};
	}

	public interface FriendListener {
		void foundFriend(String conversationID);
	}
}
