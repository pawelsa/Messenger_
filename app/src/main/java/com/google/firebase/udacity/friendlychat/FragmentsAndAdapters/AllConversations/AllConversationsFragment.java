package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.AllConversations;


import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.AddUser.AddUserSheet;
import com.google.firebase.udacity.friendlychat.Managers.App.ColorManager;
import com.google.firebase.udacity.friendlychat.Managers.App.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.Database.ConversationRequest;
import com.google.firebase.udacity.friendlychat.Managers.Database.ManageDownloadingChatRooms;
import com.google.firebase.udacity.friendlychat.Managers.Database.UserManager;
import com.google.firebase.udacity.friendlychat.R;

import io.reactivex.disposables.Disposable;


public class AllConversationsFragment extends Fragment {

	public static final String SETTINGS_FRAGMENT = "settings_fragment";

	private UsersAdapter adapter;
	private FrameLayout newUserInfo;
	private RecyclerView allUsersRecyclerView;
	private FloatingActionButton searchButton;

	private Disposable downloadChatRooms;
	private Disposable getNumberOfInvites;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.conversations_fragment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		allUsersRecyclerView = view.findViewById(R.id.allUsersList);
		searchButton = view.findViewById(R.id.floatingActionButton);
		newUserInfo = view.findViewById(R.id.new_user_requests);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);

		createAdapterAndSetupRecyclerView();
		if (UserManager.getCurrentUser() != null)
			setChatRoomDownloader();

		if (UserManager.getCurrentUser() != null)
			getNumberOfInvites = ConversationRequest.getNumberOfRequests()
					.filter(count -> count > 0)
					.subscribe(count -> {
								Log.i("Count invites", Long.toString(count));
								newUserInfo.setVisibility(View.VISIBLE);

								newUserInfo.setOnClickListener(v -> {
									AddUserSheet addUserSheet = new AddUserSheet();
									addUserSheet.show(getActivity().getSupportFragmentManager(), addUserSheet.getTag());
								});
								getNumberOfInvites.dispose();
							},
							Throwable::printStackTrace,
							() -> Log.i("Count invites", "Completed"))
					;


		manageFloatingActionBar();
	}

	private void createAdapterAndSetupRecyclerView() {

		adapter = new UsersAdapter(this.getContext());
		allUsersRecyclerView.setAdapter(adapter);
		allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
	}

	private void setChatRoomDownloader() {

		if (downloadChatRooms == null || downloadChatRooms.isDisposed()) {
			Log.i("Starting", "downloadChatRoom");
			downloadChatRooms = ManageDownloadingChatRooms.downloadChatRoomsFromDB()
					.subscribe(roomObject -> {
								adapter.addConversationToAdapter(roomObject);
								Log.i("New ChatRoom", "added to adapter");
							},
							Throwable::printStackTrace,
							() -> Log.i("downloadChatRoom", "Finish"));
		}
	}

	private void manageFloatingActionBar() {
		if (searchButton != null) {
			searchButton.setOnClickListener(v -> {
				FragmentsManager.startSearchUserFragment((AppCompatActivity) getActivity());
				Log.i("FAB", "clicked");
			});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		manageActionBar();

		Log.i("AllConversationsFragmen", "onResume");
		if (UserManager.getCurrentUser() != null)
			setChatRoomDownloader();
	}

	private void manageActionBar() {

		Toolbar actionBar = getActivity().findViewById(R.id.conversations_toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(actionBar);

		if (actionBar != null) {
			Log.i("AllConversationsFragmen", "manageActionBar");
			actionBar.setTitle(R.string.app_name);
			actionBar.setSubtitle("");
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
			int statusBarColor = ColorManager.getStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
			if (statusBarColor != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				getActivity().getWindow().setStatusBarColor(statusBarColor);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.i("AllConversationsFragmen", "onPause");
		if (downloadChatRooms != null && !downloadChatRooms.isDisposed()) {
			Log.i("Disposing", "downloadChatRoom");
			downloadChatRooms.dispose();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (adapter != null) {
			adapter.destroy();
			adapter = null;
		}
		if (downloadChatRooms != null && !downloadChatRooms.isDisposed()) {
			downloadChatRooms.dispose();
		}
		Log.i("AllConversationsFragmen", "OnDestroy");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		menu.clear();
		inflater.inflate(R.menu.main_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.settings_menu:

				FragmentsManager.startSettingsFragment((AppCompatActivity) getActivity());
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
