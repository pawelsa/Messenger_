package com.google.firebase.udacity.friendlychat;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 10.04.2018.
 */

public class ConversationListener {
	
	ChatRoomListener.OnConversationListener onConversationListener;
	private ChildEventListener listener;
	private DatabaseReference userConversations;
	private List<ChatRoomListener> chatRoomListenerList;
	
	public ConversationListener(String currentUserID, ChatRoomListener.OnConversationListener mOnConversationListener) {
		chatRoomListenerList = new ArrayList<>();
		
		onConversationListener = mOnConversationListener;
		listener = createConversationListener();
		userConversations = FirebaseDatabase.getInstance().getReference().child("user_conversations/" + currentUserID);
		userConversations.addChildEventListener(listener);
		
		Log.i("Build", "ConversationListener");
	}
	
	private ChildEventListener createConversationListener() {
		
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
	
	private void addChatRoomListener(String conversationID) {
		
		chatRoomListenerList.add(new ChatRoomListener(conversationID, onConversationListener));
	}
	
	public void destroy() {
		
		for (ChatRoomListener listener : chatRoomListenerList) {
			listener.destroy();
			Log.i("Destroy", "ChatRoomListener - for loop");
		}
		
		userConversations.removeEventListener(listener);
		userConversations = null;
		listener = null;
		
		Log.i("Destroy", "ConversationListener");
	}
}
