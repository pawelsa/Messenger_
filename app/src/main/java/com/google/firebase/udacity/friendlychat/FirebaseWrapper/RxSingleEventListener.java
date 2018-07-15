package com.google.firebase.udacity.friendlychat.FirebaseWrapper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Maybe;

public class RxSingleEventListener {

	public static Maybe<DataSnapshot> observeSingleValueEvent(final Query query) {
		return Maybe.create(emitter -> {

			ValueEventListener eventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					emitter.onSuccess(dataSnapshot);
					emitter.onComplete();
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					emitter.onError(databaseError.toException());
				}
			};

			emitter.setCancellable(() -> query.removeEventListener(eventListener));

			query.addListenerForSingleValueEvent(eventListener);
		});
	}

}
