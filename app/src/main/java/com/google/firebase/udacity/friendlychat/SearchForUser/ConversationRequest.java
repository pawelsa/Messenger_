package com.google.firebase.udacity.friendlychat.SearchForUser;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxChildEventListener;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxSingleEventListener;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public class ConversationRequest {

	public static final String CHAT_ROOM = "chat_room";
	public static final String USER_CONVERSATIONS = "user_conversations";
	public static final String PARTICIPANTS = "participants";
	public static final String FRIEND_REQUEST = "friendRequest";

	private static void makeRequest(String otherID) {

		Map<String, Object> sendRequest = new HashMap<>();
		sendRequest.put(UserManager.getCurrentUserID(), otherID);

		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FRIEND_REQUEST);
		reference.child(otherID).updateChildren(sendRequest);
	}

	private static Maybe<String> checkIfRequestExists(String otherID) {

		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FRIEND_REQUEST).child(otherID).child(UserManager.getCurrentUserID());

		return RxSingleEventListener.observeSingleValueEvent(reference)
				.flatMap(dataSnapshot -> doesRequestNotExists(dataSnapshot, otherID)
						? Maybe.just("-1") : Maybe.just("1"))
				.filter(request -> request.equals("-1"));

	}

	private static boolean doesRequestNotExists(DataSnapshot dataSnapshot, String otherID) {
		return !dataSnapshot.exists() || (dataSnapshot.exists() && !dataSnapshot.getValue(String.class).equals(otherID));
	}

	public static Flowable<String> checkIfConversationExists(String otherID, Context context) {

		final DatabaseReference otherUserConversationReference = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(otherID);
		final DatabaseReference participantsReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM);

		return Flowable.zip(getCount(), getConversationKeys(), (t1, t2) -> t2)
				.flatMap(key -> RxSingleEventListener.observeSingleValueEvent(otherUserConversationReference.child(key)).toFlowable())
				.filter(DataSnapshot::exists)
				.map(DataSnapshot::getKey)
				.flatMap(conversationKey ->
						RxSingleEventListener.observeSingleValueEvent(participantsReference.child(conversationKey).child(PARTICIPANTS))
								.filter(DataSnapshot::exists)
								.flatMap(dataSnapshot -> getExistingKey(dataSnapshot, otherID, conversationKey, context)).toFlowable())
				.filter(key -> !key.equals("-1"));
	}

	private static Flowable<String> getConversationKeys() {

		DatabaseReference myConversationReference = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID());
		return RxChildEventListener.observeChildEvent(myConversationReference, BackpressureStrategy.BUFFER)
				.filter(DataSnapshot::exists)
				.map(dataSnapshot -> dataSnapshot.getKey());
	}

	private static Flowable<Integer> getCount() {

		DatabaseReference myConversationReference = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID());
		return RxSingleEventListener.observeSingleValueEvent(myConversationReference)
				.filter(DataSnapshot::exists)
				.map(dataSnapshot -> dataSnapshot.getChildrenCount())
				.flatMapPublisher(count -> Observable.range(0, safeLongToInt(count)).toFlowable(BackpressureStrategy.BUFFER));
	}

	private static Maybe<String> getExistingKey(DataSnapshot dataSnapshot, String otherID, String conversationKey, Context context) {
		return Maybe.just(dataSnapshot)
				.map(snapshot -> (Map) snapshot.getValue())
				.map(map -> safeLongToInt(map.keySet().size()))
				.flatMap(count -> {
					if (count <= 2) {
						return Maybe.just(conversationKey);
					} else
						checkIfRequestExists(otherID)
								.subscribe(map -> makeRequest(otherID), Throwable::printStackTrace, () -> Toast.makeText(context, "Request send", Toast.LENGTH_SHORT).show());
					return Maybe.just("-1");
				});
	}

	private static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException
					(l + " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

}