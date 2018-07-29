package com.google.firebase.udacity.friendlychat.Managers.Database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxChildEventListener;
import com.google.firebase.udacity.friendlychat.Objects.Message;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class MessageReceiver {

	public static Flowable<Message> getMessage(String conversationID) {
		final DatabaseReference messageReference = FirebaseDatabase.getInstance().getReference().child("messages").child(conversationID)/*.orderByChild("timestamp")*/;

		return RxChildEventListener.observeChildEvent(messageReference, BackpressureStrategy.BUFFER)
				.subscribeOn(Schedulers.io())
				.filter(DataSnapshot::exists)
				.map(dataSnapshot -> dataSnapshot.getValue(Message.class))
				/*.doOnNext(message -> Log.i("Message Receiver", message.getUserID()))*/;
	}

}
