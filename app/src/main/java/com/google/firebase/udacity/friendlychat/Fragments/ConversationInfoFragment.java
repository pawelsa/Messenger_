package com.google.firebase.udacity.friendlychat.Fragments;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.LinearLayout;

import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.HaveToBeRemoved.ChatRoomListener;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.R;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATION_ID;


public class ConversationInfoFragment extends Fragment {

	Bundle bundle;
	private LinearLayout colorSettings;
	private LinearLayout pseudonymSettings;
	private String conversationID;
	private ChatRoomListener chatRoomListener;
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

			chatRoomListener = new ChatRoomListener(conversationID, conversation -> {
				chatRoom = new ChatRoom(conversation);
				changeActionBarAndStatusBarColor(conversation.chatColor);
			});
		} else {
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.popBackStack();
		}

		colorSettings.setOnClickListener(v -> {

			ColorBottomSheet colorBottomSheet = new ColorBottomSheet();
			Bundle args = new Bundle();
			args.putString(CONVERSATION_ID, conversationID);
			colorBottomSheet.setArguments(args);
			colorBottomSheet.show(getActivity().getSupportFragmentManager(), colorBottomSheet.getTag());
		});

		pseudonymSettings.setOnClickListener(v -> {
			PseudonymBottomSheet pseudonymBottomSheet = new PseudonymBottomSheet();
			pseudonymBottomSheet.setArguments(bundle);
			pseudonymBottomSheet.show(getActivity().getSupportFragmentManager(), pseudonymBottomSheet.getTag());
		});
	}

	private void setupActionBar() {
		if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.settings_details));
			((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
			((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
		}
	}

	private void changeActionBarAndStatusBarColor(int color) {
		changeActionBarColor(color);
		changeStatusBarColor(color);
	}

	private void changeActionBarColor(int color) {
		String hex = Integer.toHexString(color);
		while (hex.length() < 6) {
			hex = "0" + hex;
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + hex)));
	}

	private void changeStatusBarColor(int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			float[] hsv = new float[3];
			Color.colorToHSV(color, hsv);
			hsv[2] *= 0.8f; // value component
			color = Color.HSVToColor(hsv);

			getActivity().getWindow().setStatusBarColor(color);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		chatRoomListener.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		chatRoomListener.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		chatRoomListener.destroy();
		Log.i("State", "OnDestroy");
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
