package com.google.firebase.udacity.friendlychat.FirebaseWrapper;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Maybe;

public class RxSingleEventListener {

	static AtomicInteger count = new AtomicInteger(0);

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

			emitter.setCancellable(() -> {
				query.removeEventListener(eventListener);
				emitter.onComplete();

				Log.i("SingleEventListener", count.decrementAndGet() + " Cancelling "/* + query.getRef().toString()*/);
			});

			query.addListenerForSingleValueEvent(eventListener);
			count.incrementAndGet();

		});
	}

}
