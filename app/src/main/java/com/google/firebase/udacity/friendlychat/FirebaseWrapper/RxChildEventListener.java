package com.google.firebase.udacity.friendlychat.FirebaseWrapper;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class RxChildEventListener {

	static AtomicInteger count = new AtomicInteger(0);

	public static Flowable<DataSnapshot> observeChildEvent(final Query query, BackpressureStrategy backpressureStrategy) {
		return Flowable.create(emitter -> {

			ChildEventListener childEventListener = new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String s) {
					emitter.onNext(dataSnapshot);
				}

				@Override
				public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				}

				@Override
				public void onChildRemoved(DataSnapshot dataSnapshot) {
				}

				@Override
				public void onChildMoved(DataSnapshot dataSnapshot, String s) {
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
				}
			};

			emitter.setCancellable(() -> {
				query.removeEventListener(childEventListener);
				Log.i("ChildEventListener", count.decrementAndGet() + " Cancelling");
			});

			query.addChildEventListener(childEventListener);
			count.incrementAndGet();

		}, backpressureStrategy);
	}
}
