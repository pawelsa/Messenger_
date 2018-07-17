package com.google.firebase.udacity.friendlychat.Fragments;


import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.Managers.ActionBarManager;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.SearchForUser.ManageDownloadingChatRooms;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATION_ID;


public class ConversationInfoFragment extends Fragment {

	private LinearLayout colorSettings;
	private LinearLayout pseudonymSettings;
	private LinearLayout nameSettings;
	private ActionBar actionBar;

	private Bundle bundle;
	private String conversationID;
	private ChatRoom chatRoom;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.conversation_info, container, false);

		final GestureDetector gesture = LeftToRightDetector.getInstance(getActivity());

		view.setOnTouchListener((v, event) -> gesture.onTouchEvent(event));

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		colorSettings = view.findViewById(R.id.settings_color);
		pseudonymSettings = view.findViewById(R.id.settings_pseudonym);
		nameSettings = view.findViewById(R.id.settings_name);

		Toolbar actionBar = view.findViewById(R.id.conversation_info_toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(actionBar);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		setupActionBar();

		bundle = getArguments();
		if (bundle != null) {
			conversationID = bundle.getString(CONVERSATION_ID);


			ManageDownloadingChatRooms.downloadChatRoom(conversationID)
					.subscribe(chatRoom1 -> {
						chatRoom = chatRoom1;
						changeActionBarAndStatusBarColor(chatRoom.chatRoomObject.chatColor);
						Log.i("Size", Integer.toString(chatRoom.conversationalist.size()));
						if (chatRoom.conversationalist.size() >= 2) {
							nameSettings.setVisibility(View.VISIBLE);
						}
					}, Throwable::printStackTrace, () -> Log.i("ConversationInfoFragmen", "chatRoom downloaded"));

		} else {
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.popBackStack();
		}

		colorSettings.setOnClickListener(v -> {

			ColorBottomSheet colorBottomSheet = new ColorBottomSheet();
			colorBottomSheet.setArguments(bundle);
			colorBottomSheet.show(getActivity().getSupportFragmentManager(), colorBottomSheet.getTag());
		});

		pseudonymSettings.setOnClickListener(v -> {
			PseudonymBottomSheet pseudonymBottomSheet = new PseudonymBottomSheet();
			pseudonymBottomSheet.setArguments(bundle);
			pseudonymBottomSheet.show(getActivity().getSupportFragmentManager(), pseudonymBottomSheet.getTag());
		});

		nameSettings.setOnClickListener(v -> {
			buildConversationNameChangeAlertDialog();
		});
	}

	private void setupActionBar() {

		actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle(getResources().getString(R.string.settings_details));
			actionBar.setSubtitle(null);
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setDisplayUseLogoEnabled(true);
		}
	}

	private void changeActionBarAndStatusBarColor(int color) {

		ColorDrawable actionBarColor = ActionBarManager.getActionBarColor(color);
		actionBar.setBackgroundDrawable(actionBarColor);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int statusBarColor = ActionBarManager.getStatusBarColor(color);
			if (statusBarColor != -1)
				getActivity().getWindow().setStatusBarColor(statusBarColor);
		}
	}

	private void buildConversationNameChangeAlertDialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
		alertDialogBuilder.setTitle(getContext().getResources().getString(R.string.settings_name));

		final EditText input = buildEditText();
		input.setText(chatRoom.chatRoomObject.conversationName);

		alertDialogBuilder
				.setView(input)
				.setPositiveButton(getContext().getResources().getString(R.string.settings_confirm), (dialog, which) -> {
					final String newName = input.getText().toString();
					UserManager.removeConversationName(newName, conversationID);
				})
				.setNeutralButton(getContext().getResources().getString(R.string.settings_original), ((dialog, which) ->
						UserManager.removeConversationName(conversationID)))
				.setNegativeButton(getContext().getResources().getString(R.string.settings_cancel), (dialog, which) ->
						dialog.cancel());
		alertDialogBuilder.show();
	}

	private EditText buildEditText() {
		EditText input = new EditText(getContext());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setFocusable(true);
		input.setSelection(input.getText().length());
		return input;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				FragmentsManager.goBack(getActivity());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
