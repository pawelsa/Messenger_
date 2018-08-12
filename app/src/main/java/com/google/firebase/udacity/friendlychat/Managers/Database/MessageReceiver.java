package com.google.firebase.udacity.friendlychat.Managers.Database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxChildEventListener;
import com.google.firebase.udacity.friendlychat.Objects.Message;

import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class MessageReceiver {

	public static Flowable<Message> getMessage(String conversationID) {
		final DatabaseReference messageReference = FirebaseDatabase.getInstance().getReference().child("messages").child(conversationID)/*.orderByChild("timestamp")*/;

		return RxChildEventListener.observeChildEvent(messageReference, BackpressureStrategy.BUFFER)
				.subscribeOn(Schedulers.io())
				.filter(DataSnapshot::exists)
				//.map(dataSnapshot -> dataSnapshot.getValue(Message.class))
				.map(dataSnapshot -> {
					GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
					};
					Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

					Message message = new Message();
					message.timestamp = (Map<String, Object>) map.get("timestamp");
					if (map.containsKey("photoUrl"))
						message.photoUrl = (String) map.get("photoUrl");
					if (map.containsKey("text"))
						message.text = (String) map.get("text");
					message.userID = (String) map.get("userID");
					return message;
				})
				/*.doOnNext(message -> Log.i("Message Receiver", message.getUserID()))*/;
	}

}
