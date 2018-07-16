package com.google.firebase.udacity.friendlychat.SearchForUser;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxChildEventListener;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxValueEventListener;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject;
import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.google.firebase.udacity.friendlychat.SearchForUser.ConversationRequest.CHAT_ROOM;
import static com.google.firebase.udacity.friendlychat.SearchForUser.ConversationRequest.USER_CONVERSATIONS;

public class ManageDownloadingChatRooms {


	public static Observable<ChatRoom> downloadChatRoomsFromDB() {

		return Observable.zip(getChatRoomObjectFlowable(), getListsOfUsersForEachChatRoom(), (chatRoom, userList) -> new ChatRoom(userList, chatRoom))
				.observeOn(AndroidSchedulers.mainThread());
	}

	private static Observable<ChatRoomObject> getChatRoomObjectFlowable() {
		return RxChildEventListener.observeChildEvent(FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS).child(UserManager.getCurrentUserID()), BackpressureStrategy.BUFFER)
				.filter(DataSnapshot::exists)
				.subscribeOn(Schedulers.io())
				.map(dataSnapshot -> dataSnapshot.getValue(String.class))
				.flatMap(conversationID -> RxValueEventListener.observeValueEvent(FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID).orderByChild("lastMessageSendTime"), BackpressureStrategy.BUFFER))
				.filter(DataSnapshot::exists)
				.map(dataSnapshot -> dataSnapshot.getValue(ChatRoomObject.class))
				.doOnNext(chatRoomObject -> Log.i("New chatRoom", chatRoomObject.conversationID))
				.toObservable();
	}

	private static Observable<List<User>> getListsOfUsersForEachChatRoom() {
		return getChatRoomObjectFlowable()
				.concatMap(chatRoomObject -> getListOfUsersFor(chatRoomObject));
	}

	private static Observable<List<User>> getListOfUsersFor(ChatRoomObject chatRoomObject) {

		return Observable.fromIterable(chatRoomObject.participants.keySet())
				.filter(userID -> !userID.equals(UserManager.getCurrentUserID()))
				.flatMap(userID -> SearchForUser.searchUserByID(userID).toObservable())
				.toList()
				.toObservable();
	}

	public static Observable<ChatRoom> downloadChatRoom(String conversationID) {

		Observable<ChatRoomObject> chatRoomObjectObservable = RxValueEventListener.observeValueEvent(FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID), BackpressureStrategy.BUFFER)
				.filter(DataSnapshot::exists)
				.subscribeOn(Schedulers.io())
				.map(dataSnapshot -> dataSnapshot.getValue(ChatRoomObject.class))
				.toObservable();

		Observable<List<User>> chatRoomUsersList = chatRoomObjectObservable
				.flatMap(chatRoomObject -> getListOfUsersFor(chatRoomObject));

		return Observable.zip(chatRoomObjectObservable, chatRoomUsersList, (t1, t2) -> new ChatRoom(t2, t1))
				.observeOn(AndroidSchedulers.mainThread());
	}
}
