package com.google.firebase.udacity.friendlychat.Managers.Database;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxChildEventListener;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxSingleEventListener;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxValueEventListener;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject;
import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public class ConversationRequest {

	public static final String CHAT_ROOM = "chat_room";
	public static final String USER_CONVERSATIONS = "user_conversations";
	public static final String PARTICIPANTS = "participants";
	public static final String FRIEND_REQUEST = "friendRequest";

	public static Flowable<String> checkIfConversationExists(String otherID, Context context) {

		final DatabaseReference otherUserConversationReference = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(otherID);
		final DatabaseReference participantsReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM);
		final AtomicInteger countCommonRooms = new AtomicInteger(0);
		/*return checkIfRoomsExists()
				.toFlowable()
				.flatMap(result -> {
							if (result) {
								return getConversationKeys()
										.flatMap(key -> RxSingleEventListener.observeSingleValueEvent(otherUserConversationReference.child(key)).toFlowable())
										.filter(DataSnapshot::exists)
										.map(DataSnapshot::getKey)
										.flatMap(conversationKey ->
												RxSingleEventListener.observeSingleValueEvent(participantsReference.child(conversationKey).child(PARTICIPANTS))
														.doOnSuccess(key -> Log.i("Key", key.getKey()))
														.filter(DataSnapshot::exists)
														.flatMap(dataSnapshot -> getExistingKey(dataSnapshot, otherID, conversationKey, context)).toFlowable())
										.filter(key -> !key.equals("-1"));
							} else {
								makeRequest(otherID);
								Toast.makeText(context, "Request send", Toast.LENGTH_SHORT).show();
								return Flowable.empty();
							}
						}
				);*/

		return checkIfRoomsExists()
				.toFlowable()
				.flatMap(result -> {
							if (result) {
								return Flowable.zip(getCount(), getConversationKeys(), (t1, t2) -> t2)
										.flatMap(key -> RxSingleEventListener.observeSingleValueEvent(otherUserConversationReference.child(key)).toFlowable())
										.filter(DataSnapshot::exists)
										.doOnNext(key -> countCommonRooms.incrementAndGet())
										.map(DataSnapshot::getKey)
										.flatMap(conversationKey ->
												RxSingleEventListener.observeSingleValueEvent(participantsReference.child(conversationKey).child(PARTICIPANTS))
														.filter(DataSnapshot::exists)
														.flatMap(dataSnapshot -> getExistingKey(dataSnapshot, otherID, conversationKey, context)).toFlowable())
										.filter(key -> !key.equals("-1"))
										.doOnComplete(() -> {
											if (countCommonRooms.get() < 1)
												makeRequest(otherID, context);
										});
							} else {
								makeRequest(otherID, context);
								return Flowable.empty();
							}
						}
				);
	}

	private static Flowable<Integer> getCount() {

		DatabaseReference myConversationReference = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID());
		return RxSingleEventListener.observeSingleValueEvent(myConversationReference)
				.filter(DataSnapshot::exists)
				.map(DataSnapshot::getChildrenCount)
				.flatMapPublisher(count -> Observable.range(0, safeLongToInt(count)).toFlowable(BackpressureStrategy.BUFFER));
	}

	private static Maybe<Boolean> checkIfRoomsExists() {

		final DatabaseReference userConversationReference = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID());
		return RxSingleEventListener.observeSingleValueEvent(userConversationReference)
				.map(DataSnapshot::hasChildren);
	}

	private static Flowable<String> getConversationKeys() {

		DatabaseReference myConversationReference = FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID());
		return RxChildEventListener.observeChildEvent(myConversationReference, BackpressureStrategy.BUFFER)
				.filter(DataSnapshot::exists)
				.map(DataSnapshot::getKey);
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
								.subscribe(map -> makeRequest(otherID, context), Throwable::printStackTrace);
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

	private static void makeRequest(String otherID, Context context) {

		Map<String, Object> sendRequest = new HashMap<>();
		sendRequest.put(UserManager.getCurrentUserID(), otherID);

		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FRIEND_REQUEST);
		reference.child(otherID).updateChildren(sendRequest).addOnSuccessListener(
				aVoid -> Toast.makeText(context, "Request send", Toast.LENGTH_SHORT).show())
				.addOnFailureListener(
						aVoid -> Toast.makeText(context, "Request cannot be send", Toast.LENGTH_SHORT).show());
	}

	public static Flowable<Long> getNumberOfRequests() {

		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FRIEND_REQUEST).child(UserManager.getCurrentUserID());

		return RxValueEventListener.observeValueEvent(reference, BackpressureStrategy.BUFFER)
				.filter(DataSnapshot::exists)
				.map(DataSnapshot::getChildrenCount)
				.take(1);
	}


	public static Flowable<User> getUserRequests() {

		DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FRIEND_REQUEST).child(UserManager.getCurrentUserID());

		return RxChildEventListener.observeChildEvent(reference, BackpressureStrategy.BUFFER)
				.filter(DataSnapshot::exists)
				.map(dataSnapshot -> dataSnapshot.getKey())
				.flatMap(userID -> SearchForUser.searchUserByID(userID).toFlowable());
	}

	public static Observable<Boolean> requestAccepted(User otherUser) {

		//TODO: remove from User_Request, add conversationKey to both users, create chat room

		return Observable.create(emitter -> {
			DatabaseReference toDelete = FirebaseDatabase.getInstance().getReference().child(FRIEND_REQUEST).child(UserManager.getCurrentUserID()).child(otherUser.User_ID);

			toDelete.removeValue((databaseError, databaseReference) -> {
				if (databaseError != null)
					emitter.onError(databaseError.toException());
				else
					emitter.onNext(true);
			});
		})
				.flatMap(confirmation -> Observable.create(emitter -> {
					try {
						DatabaseReference baseReference = FirebaseDatabase.getInstance().getReference();
						DatabaseReference reference = baseReference.child(CHAT_ROOM);
						String conversationKey = reference.push().getKey();

						ChatRoomObject chatRoomObject = new ChatRoomObject(conversationKey, otherUser.User_ID, otherUser.User_Name);
						reference.child(conversationKey).setValue(chatRoomObject);

						DatabaseReference myConversationReference = baseReference.child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID()).child(conversationKey);
						myConversationReference.setValue(conversationKey);
						DatabaseReference otherUserConversationReference = baseReference.child(USER_CONVERSATIONS).child(otherUser.User_ID).child(conversationKey);
						otherUserConversationReference.setValue(conversationKey);

						emitter.onNext(true);
					} catch (Exception e) {
						emitter.onError(e);
					}
				}));

	}

}