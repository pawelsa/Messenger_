package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.AddUser;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.udacity.friendlychat.Managers.Database.ConversationRequest;
import com.google.firebase.udacity.friendlychat.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class AddUserSheet extends BottomSheetDialogFragment {

	private RecyclerView addUserRecyclerView;
	private Disposable downloadRequests;
	private AddUserRecyclerViewAdapter adapter;

	public AddUserSheet() {

	}

	@Override
	public void setupDialog(Dialog dialog, int style) {
		super.setupDialog(dialog, style);

		View contentView = View.inflate(getContext(), R.layout.only_recycler_view, null);
		addUserRecyclerView = contentView.findViewById(R.id.common_recyclerView);
		dialog.setContentView(contentView);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


		adapter = new AddUserRecyclerViewAdapter(getContext());

		addUserRecyclerView.setAdapter(adapter);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		linearLayoutManager.setStackFromEnd(true);
		addUserRecyclerView.setLayoutManager(linearLayoutManager);

		startDownloadingUsers();

	}

	private void startDownloadingUsers() {

		downloadRequests = ConversationRequest.getUserRequests()
				.doOnNext(user -> Log.i("AddUserSheet", "doOnNext"))
				//.take(5, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(user -> adapter.add(user),
						Throwable::printStackTrace,
						() -> Log.i("AddUserSheet", "Completed downloading requests")
				);
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.i("AddUserSheet", "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();

		Log.i("AddUserSheet", "onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i("AddUserSheet", "onDestroy");
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);

		Log.i("AddUserSheet", "onCancel");
	}
}
