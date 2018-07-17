package com.google.firebase.udacity.friendlychat.Fragments;


import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.google.firebase.udacity.friendlychat.Managers.ActionBarManager;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.SearchForUser.ManageDownloadingChatRooms;
import com.google.firebase.udacity.friendlychat.UsersAdapter;

import io.reactivex.disposables.Disposable;


public class AllConversationsFragment extends Fragment {

	public static final String SETTINGS_FRAGMENT = "settings_fragment";

	private UsersAdapter adapter;
	private RecyclerView allUsersRecyclerView;
	private FloatingActionButton searchButton;
	private Disposable downloadChatRooms;


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
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);

		createAdapterAndSetupRecyclerView();
		setChatRoomDownloader();
		searchButton = getActivity().findViewById(R.id.floatingActionButton);
		manageFloatingActionBar();
	}

	private void createAdapterAndSetupRecyclerView() {

		adapter = new UsersAdapter(this.getContext());
		allUsersRecyclerView.setAdapter(adapter);
		allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
	}

	private void setChatRoomDownloader() {

		downloadChatRooms = ManageDownloadingChatRooms.downloadChatRoomsFromDB()
				.subscribe(roomObject -> adapter.addConversationToAdapter(roomObject));
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
	}

	private void manageActionBar() {
		Toolbar actionBar = getActivity().findViewById(R.id.conversations_toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(actionBar);

		if (actionBar != null) {
			actionBar.setTitle(R.string.app_name);
			actionBar.setSubtitle("");
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
			int statusBarColor = ActionBarManager.getStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
			if (statusBarColor != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				getActivity().getWindow().setStatusBarColor(statusBarColor);
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
		Log.i("State", "OnDestroy");
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

				UserSettingsFragment conversationsFragment = UserSettingsFragment.getInstance();

				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right).replace(R.id.messageFragment, conversationsFragment, SETTINGS_FRAGMENT).addToBackStack(null).commit();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
