package com.google.firebase.udacity.friendlychat;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.udacity.friendlychat.Managers.ListOfConversationsManager;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;

/**
 * Created by Paweł on 20.04.2018.
 */

public class AllConversationsFragment extends Fragment implements UserManager.OnUserDownloadListener, ChatRoomListener.OnConversationListener {
	
	private ListOfConversationsManager conversationsManager;
	private UsersAdapter adapter;
	private RecyclerView allUsersRecyclerView;
	
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.conversations_fragment, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		allUsersRecyclerView = view.findViewById(R.id.allUsersList);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		createAdapterAndSetupRecyclerView();
		conversationsManager = new ListOfConversationsManager();
		
		setupConversationListener();
	}
	
	private void createAdapterAndSetupRecyclerView() {
		
		adapter = new UsersAdapter(this.getContext(), this);
		allUsersRecyclerView.setAdapter(adapter);
		allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
	}
	
	private void setupConversationListener() {
		if (conversationsManager != null) {
			conversationsManager.loadConversations(this);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		manageActionBar();
	}
	
	private void manageActionBar() {
		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setTitle(R.string.app_name);
			actionBar.setSubtitle("");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (adapter != null) {
			adapter.clear();
			adapter = null;
		}
		if (conversationsManager != null) {
			conversationsManager.clear();
			conversationsManager = null;
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
			case R.id.sign_out_menu:
				UserManager.onSignOut();
				AuthUI.getInstance().signOut(this.getContext());
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void addConversationToAdapter(ChatRoomObject conversation) {
		if (adapter != null) adapter.updateList(new ChatRoom(conversation));
	}
	
	@Override
	public void userDownloaded() {
	}
	
	@Override
	public void userDownloaded(User downloadedUser) {
		if (adapter != null) adapter.pushUser(downloadedUser);
	}
}
