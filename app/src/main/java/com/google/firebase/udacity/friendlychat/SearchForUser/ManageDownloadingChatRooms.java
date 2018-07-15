package com.google.firebase.udacity.friendlychat.SearchForUser;

import android.annotation.SuppressLint;
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

import static com.google.firebase.udacity.friendlychat.TestObjects.ConversationRequest.USER_CONVERSATIONS;

public class ManageDownloadingChatRooms {

	@SuppressLint("CheckResult")
	public static Observable<ChatRoom> downloadChatRoomsFromDB() {

		Observable<ChatRoomObject> chatRoomObjectFlowable = RxChildEventListener.observeChildEvent(FirebaseDatabase.getInstance().getReference().child(USER_CONVERSATIONS + "/" + UserManager.getCurrentUserID()), BackpressureStrategy.BUFFER)
				.filter(DataSnapshot::exists)
				.subscribeOn(Schedulers.io())
				.map(dataSnapshot -> dataSnapshot.getValue(String.class))
				.flatMap(conversationID -> RxValueEventListener.observeValueEvent(FirebaseDatabase.getInstance().getReference().child("chat_room").child(conversationID).orderByChild("lastMessageSendTime"), BackpressureStrategy.BUFFER))
				.filter(DataSnapshot::exists)
				.map(dataSnapshot -> dataSnapshot.getValue(ChatRoomObject.class))
				.doOnNext(chatRoomObject -> Log.i("New chatRoom", chatRoomObject.conversationID))
				.toObservable();

		Observable<List<User>> getListsOfUsers = chatRoomObjectFlowable
				.concatMap(chatRoomObject -> getChatRoom(chatRoomObject));

		return Observable.zip(chatRoomObjectFlowable, getListsOfUsers, (chatRoom, userList) -> new ChatRoom(userList, chatRoom))
				.observeOn(AndroidSchedulers.mainThread());
	}

	private static Observable<List<User>> getChatRoom(ChatRoomObject chatRoomObject) {

		return Observable.fromIterable(chatRoomObject.participants.keySet())
				.filter(userID -> !userID.equals(UserManager.getCurrentUserID()))
				.flatMap(userID -> SearchForUser.searchUserByID(userID).toObservable())
				.toList()
				.toObservable();
	}
}
