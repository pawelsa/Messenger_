package com.google.firebase.udacity.friendlychat.TestObjects;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paweł on 20.05.2018.
 */

public class TestObject {
	
	public void start() {
		
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("test").child("-LD1s_vlFrZaIgHunAPd").child("participants").child("-LD2IO0l3kGjE96ut4WR");
		Map<String, Object> secondParticipant = new HashMap<>();
		//secondParticipant.put("ID", "nextID");
		secondParticipant.put("Name", "Next name");
		reference.updateChildren(secondParticipant);
	}
	
	private void addNewMember() {
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("test").child("-LD1s_vlFrZaIgHunAPd").child("participants");
		Map<String, Object> addMember = new HashMap<>();
		Map<String, Object> secondParticipant = new HashMap<>();
		secondParticipant.put("ID", "nextID");
		//secondParticipant.put("Name", "Next name");
		String newAddress = reference.push().getKey();
		addMember.put(newAddress, secondParticipant);
		reference.updateChildren(addMember);
	}
	
	private void obtainObject() {
		
		ValueEventListener valueEventListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				
				ChatRoomObject chatRoomObject = dataSnapshot.getValue(ChatRoomObject.class);
				if (chatRoomObject != null) {
					for (String mapKey : chatRoomObject.participants.keySet()) {
						Map<String, Object> user = (Map<String, Object>) chatRoomObject.participants.get(mapKey);
						Log.i("User " + mapKey, (String) user.get("Name"));
					}
				}
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError) {
			
			}
		};
		
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("test").child("-LD1s_vlFrZaIgHunAPd");
		reference.addValueEventListener(valueEventListener);
	}
	
	private void sendChatRoomObject() {
		
		ChatRoomObject chatRoomObject = new ChatRoomObject("conversationID", "myID", "conversationalistID");
		
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("test");
		reference.push().setValue(chatRoomObject);
	}
	
	private void readValue() {
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("test");
		
		ValueEventListener valueEventListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
				if (map != null) {
					String stringMap = map.toString();
					Log.i("Map", stringMap);
				}
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError) {
			
			}
		};
		
		reference.addValueEventListener(valueEventListener);
	}
	
	private void setValue() {
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("test");
		
		HashMap<String, Object> test = new HashMap<>();
		
		test.put("1", "1");
		test.put("2", "1");
		
		String ID = reference.push().getKey();
		
		reference.child(ID).setValue(test);
		
		test = new HashMap<>();
		
		test.put("3", "1");
		test.put("4", "1");
		
		reference.child(ID).updateChildren(test);
	}
}
