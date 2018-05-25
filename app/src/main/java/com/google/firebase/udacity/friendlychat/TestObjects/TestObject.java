package com.google.firebase.udacity.friendlychat.TestObjects;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject;
import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.flowable.FlowableJust;
import io.reactivex.schedulers.Schedulers;

import static com.google.firebase.udacity.friendlychat.Managers.OpenChatRoomWith.CHAT_ROOM;

/**
 * Created by Paweł on 20.05.2018.
 */

public class TestObject {

	private Disposable disposable;

	public void start() {

		observeUsersInConversation();
	}

	@SuppressLint("CheckResult")
	private void observeUsersInConversation() {

		Log.i("Disposable", "Create");
		disposable = observeSingleValue(FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child("-L9XDb089Z0einwGoNv7"))
				/*.filter(new Predicate<DataSnapshot>() {
					@Override
					public boolean test(DataSnapshot dataSnapshot) throws Exception {
						com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject chatRoomObject = dataSnapshot.getValue(com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject.class);
						if (chatRoomObject.conversationName!=null && chatRoomObject.conversationName.length()>0){
							return true;
						}
						return false;
					}
				})*/
				.map(this::getParticipantsIDs)
				.flatMap(Flowable::fromIterable)
				.flatMap(userID -> observeValue(FirebaseDatabase.getInstance().getReference().child("users").child(userID)))
				.flatMap(userInfo -> Flowable.fromCallable(new FlowableJust<>(getStringOfUser(userInfo))))
				.observeOn(Schedulers.io())
				.subscribeOn(AndroidSchedulers.mainThread())
				.subscribe(info -> Log.i("value", info), error -> Log.i("Error", error.getMessage()));
	}

	public void onPause() {
		if (disposable != null && !disposable.isDisposed()) {
			Log.i("Disposable", "Pause");
			disposable.dispose();
		}
	}

	private Flowable<DataSnapshot> observeSingleValue(Query query) {
		return Flowable.create(emitter -> {
			ValueEventListener valueEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					emitter.onNext(dataSnapshot);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					emitter.onError(databaseError.toException());
				}
			};
			emitter.setCancellable(() -> query.removeEventListener(valueEventListener));
			query.addListenerForSingleValueEvent(valueEventListener);

		}, BackpressureStrategy.BUFFER);
	}

	private Flowable<DataSnapshot> observeValue(Query query) {
		return Flowable.create(emitter -> {
			ValueEventListener valueEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					emitter.onNext(dataSnapshot);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					emitter.onError(databaseError.toException());
				}
			};
			emitter.setCancellable(() -> query.removeEventListener(valueEventListener));
			query.addValueEventListener(valueEventListener);

		}, BackpressureStrategy.BUFFER);
	}

	private List<String> getParticipantsIDs(DataSnapshot chatRoom) {

		ChatRoomObject chatRoomObject = chatRoom.getValue(ChatRoomObject.class);
		List<String> participantsIDs = new ArrayList<>();

		for (String key : chatRoomObject.participants.keySet()) {
			HashMap<String, Object> user = (HashMap<String, Object>) chatRoomObject.participants.get(key);

			if (/*!user.get("ID").equals(getCurrentUserID()) && */user.get("ID") != null) {
				participantsIDs.add((String) user.get("ID"));
			}
		}
		return participantsIDs;
	}

	private String getStringOfUser(DataSnapshot userData) {
		User user = userData.getValue(User.class);

		return user.User_Name + "  " + user.User_ID + " " + user.isOnline;
	}

	private void addNameToMember() {
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

		ChatRoomObject chatRoomObject = new ChatRoomObject("conversationID", "myID", "conversationalistID", "its name");

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
