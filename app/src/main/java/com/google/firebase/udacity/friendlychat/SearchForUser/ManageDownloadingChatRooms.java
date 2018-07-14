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
import com.google.firebase.udacity.friendlychat.UsersAdapter;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.google.firebase.udacity.friendlychat.Managers.OpenChatRoomWith.USER_CONVERSATIONS;

public class ManageDownloadingChatRooms {

	private UsersAdapter usersAdapter;
	private Disposable disposable;

	public ManageDownloadingChatRooms(UsersAdapter usersAdapter) {
		this.usersAdapter = usersAdapter;
	}

	@SuppressLint("CheckResult")
	public void downloadChatRoomsFromDB() {

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

		disposable = Observable.zip(chatRoomObjectFlowable, getListsOfUsers, (chatRoom, userList) -> new ChatRoom(userList, chatRoom))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(roomObject -> usersAdapter.addConversationToAdapter(roomObject));

	}

	private Observable<List<User>> getChatRoom(ChatRoomObject chatRoomObject) {

		return Observable.fromIterable(chatRoomObject.participants.keySet())
				.filter(userID -> !userID.equals(UserManager.getCurrentUserID()))
				.flatMap(userID -> SearchForUser.searchUserByID(userID).toObservable())
				.toList()
				.toObservable();
	}


	public void dispose() {
		if (disposable != null && !disposable.isDisposed())
			disposable.dispose();
	}
}
