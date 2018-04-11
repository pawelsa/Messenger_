package com.google.firebase.udacity.friendlychat;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Paweł on 11.04.2018.
 */

public class ChatRoomListener {
	
	private static OnConversationListener mOnConversationListener;
	private ValueEventListener chatRoomListener;
	private DatabaseReference referenceToChatRooms;
	
	ChatRoomListener(String conversationID, OnConversationListener onConversationListener) {
		
		mOnConversationListener = onConversationListener;
		chatRoomListener = createChatRoomListener();
		referenceToChatRooms = FirebaseDatabase.getInstance().getReference().child("chat_room").child(conversationID);
		referenceToChatRooms.orderByChild("lastMessageSendTime").addValueEventListener(chatRoomListener);
		Log.i("Build", "ChatRoomListener");
	}
	
	private ValueEventListener createChatRoomListener() {
		
		return new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				
				Log.i("Conversation Key", dataSnapshot.getKey());
				
				ChatRoomObject addedChatRoom = dataSnapshot.getValue(ChatRoomObject.class);
				if (addedChatRoom != null) {
					mOnConversationListener.addConversationToAdapter(addedChatRoom);
				}
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		};
	}
	
	void destroy() {
		mOnConversationListener = null;
		referenceToChatRooms.removeEventListener(chatRoomListener);
		chatRoomListener = null;
		referenceToChatRooms = null;
		Log.i("Destroy", "ChatRoomListener");
	}
	
	public interface OnConversationListener {
		
		void addConversationToAdapter(ChatRoomObject conversation);
	}
}
