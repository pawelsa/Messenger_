package com.google.firebase.udacity.friendlychat.FirebaseWrapper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Single;

public class RxSingleEventListener {

	public static Single<DataSnapshot> observeSingleValueEvent(final Query query) {
		return Single.create(emitter -> {

			ValueEventListener eventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					emitter.onSuccess(dataSnapshot);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
				}
			};

			emitter.setCancellable(() -> query.removeEventListener(eventListener));

			query.addListenerForSingleValueEvent(eventListener);
		});
	}

}
