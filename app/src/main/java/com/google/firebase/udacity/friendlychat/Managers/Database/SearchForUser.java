package com.google.firebase.udacity.friendlychat.Managers.Database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxChildEventListener;
import com.google.firebase.udacity.friendlychat.FirebaseWrapper.RxSingleEventListener;
import com.google.firebase.udacity.friendlychat.Objects.User;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public class SearchForUser {


	public static Maybe<User> searchUserByID(String userID) {

		return RxSingleEventListener.observeSingleValueEvent(getUserQueryByID(userID))
				.filter(DataSnapshot::exists)
				.map(dataSnapshot -> dataSnapshot.getValue(User.class));
	}

	private static Query getUserQueryByID(String userID) {
		return FirebaseDatabase.getInstance().getReference().child(UserManager.USERS).child(userID);
	}

	public static Flowable<User> searchUserByName(String userSearch) {

		return firstLettersToUpperCase(userSearch)
					.flatMap(query -> RxChildEventListener.observeChildEvent(getUsersQueryByName(query), BackpressureStrategy.BUFFER))
					.filter(DataSnapshot::exists)
					.map(dataSnapshot -> dataSnapshot.getValue(User.class))
				.filter(user -> !user.User_ID.equals(UserManager.getCurrentUserID()));
	}

	private static Flowable<String> firstLettersToUpperCase(String toModify) {

		return Observable.just(toModify)
				.flatMap(s -> Observable.fromArray(s.split(" ")))
				.map(s -> {
					StringBuilder edit = new StringBuilder(s);
					edit.setCharAt(0, Character.toUpperCase(s.charAt(0)));
					return " " + String.valueOf(edit);
				})
				.reduce((finalString, nextString) -> finalString + nextString)
				.map(s -> s.substring(1))
				.toFlowable();
	}

	private static Query getUsersQueryByName(String userSearch) {

		return FirebaseDatabase.getInstance().getReference().child("users").orderByChild("User_Name").startAt(userSearch).endAt(userSearch + "\uf8ff").limitToFirst(10);
	}
}
