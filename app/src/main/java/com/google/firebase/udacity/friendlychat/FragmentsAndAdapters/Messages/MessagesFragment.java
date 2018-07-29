package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.Messages;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Scroller;

import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.Managers.App.ColorManager;
import com.google.firebase.udacity.friendlychat.Managers.App.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.App.LastSeenTime;
import com.google.firebase.udacity.friendlychat.Managers.Database.ManageDownloadingChatRooms;
import com.google.firebase.udacity.friendlychat.Managers.Database.MessageReceiver;
import com.google.firebase.udacity.friendlychat.Managers.Database.MessageSender;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.udacity.friendlychat.Managers.Database.UserManager.getCurrentUserID;


public class MessagesFragment extends Fragment {

	private static final int RC_PHOTO_PICKER = 2;
	public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
	public static final String CONVERSATION_ID = "conversationID";
	public static final String DISPLAY_NAME = "displayName";
	public static final String CONVERSATIONALIST_PSEUDONYM = "conversationalist_pseudonym";

	private ImageButton photoPickerButton;
	private EditText messageEditText;
	private ImageView sendButton;
	private Toolbar toolbar;
	private ActionBar actionBar;
	private RecyclerView recyclerView;

	private ChatRoom chatRoom;

	private MessagesAdapter adapter;

	private Disposable messageReceiver;
	private Disposable downloadingChatRooms;


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		View item = getActivity().findViewById(R.id.allInfo);
		if (item != null)
			item.setVisibility(View.INVISIBLE);

		adapter = new MessagesAdapter(getContext());

		Bundle bundle = getArguments();
		if (bundle != null) {
			String conversationID = bundle.getString(CONVERSATION_ID);
			startWatchingChatRoom(conversationID);

			String conversationName = bundle.getString(DISPLAY_NAME);
			setupActionBar(conversationName);
		}

		initializeReferencesToViews();
		settingUpUIFunctionality();
	}

	private void startWatchingChatRoom(String conversationID) {

		downloadingChatRooms = ManageDownloadingChatRooms.downloadChatRoom(conversationID)
				.subscribe(downloadedChatRoom -> {
					chatRoom = downloadedChatRoom;
					adapter.updateChatRoom(chatRoom);
					startWatchingMessages(chatRoom.getConversationID());
					setupToolbar();
				});
	}

	private void startWatchingMessages(String conversationID) {

		if ((messageReceiver != null && messageReceiver.isDisposed()) || messageReceiver == null)

			messageReceiver = MessageReceiver.getMessage(conversationID)
					.subscribe(message -> {
								adapter.addMessage(message);
								recyclerView.scrollToPosition(adapter.getItemCount() - 1);
							},
							Throwable::printStackTrace,
							() -> Log.i("Watching Messages", "Completed"));

	}

	private void setupToolbar() {

		if (toolbar != null) {
			changeBarColors();
			setTitle();
			setUserAvatarInActionBar();
			setUserOnlineStatusInActionBar();
		}
	}

	private void changeBarColors() {
		int color = chatRoom.chatRoomObject.chatColor;
		if (actionBar != null) {

			ColorDrawable actionBarColor = ColorManager.getActionBarColor(color);
			actionBar.setBackgroundDrawable(actionBarColor);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				int statusBarColor = ColorManager.getStatusBarColor(color);
				if (statusBarColor != -1)
					getActivity().getWindow().setStatusBarColor(statusBarColor);
			}
		}
	}

	private void setTitle() {
		Single.create((SingleOnSubscribe<String>) emitter -> {

			if (chatRoom.chatRoomObject.conversationName == null) {
				Observable.fromIterable(chatRoom.chatRoomObject.participants.keySet())
						.map(key -> (Map<String, Object>) chatRoom.chatRoomObject.participants.get(key))
						.filter(user -> !user.get("ID").equals(getCurrentUserID()) && user.get("Name") != null)
						.map(user -> user.get("Name").toString() + ", ")
						.reduce((total, next) -> total + next)
						.map(totalName -> totalName.length() < 50 ? totalName.substring(0, totalName.length() - 2) : totalName.substring(0, 50))
						.subscribe(emitter::onSuccess);
			} else {
				emitter.onSuccess(chatRoom.chatRoomObject.conversationName);
			}
		})
				.subscribe(convName -> {
					chatRoom.chatRoomObject.conversationName = convName;
					actionBar.setTitle(convName);
				});
	}

	private void setupActionBar(String userName) {

		if (toolbar != null) {
			actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setDisplayUseLogoEnabled(true);
			actionBar.setTitle(userName);
		}
	}

	private void initializeReferencesToViews() {

		photoPickerButton = getActivity().findViewById(R.id.photo_picker_button);
		messageEditText = getActivity().findViewById(R.id.message_edit_text);
		sendButton = getActivity().findViewById(R.id.send_button);
		recyclerView = getActivity().findViewById(R.id.messages_recycler_view);
	}

	private void settingUpUIFunctionality() {
		photoPickerButtonFunctionality();
		messageEditTextFunctionality();
		sendButtonFunctionality();
		recyclerViewFunctionality();
	}


	private void photoPickerButtonFunctionality() {

		photoPickerButton.setOnClickListener(view -> {

			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/jpeg");
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
		});
	}

	private void messageEditTextFunctionality() {

		messageEditText.setScroller(new Scroller(getContext()));
		messageEditText.setMaxLines(2);
		messageEditText.setVerticalScrollBarEnabled(true);

		messageEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.toString().trim().length() > 0) {
					sendButton.setEnabled(true);
				} else {
					sendButton.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
	}

	private void sendButtonFunctionality() {

		sendButton.setOnClickListener(view -> {
			String messageText = messageEditText.getText().toString().trim();

			if (messageText.length() > 0 && messageText.length() < 1000) {
				MessageSender.sendMessage(messageText, chatRoom.getConversationID());
				messageEditText.setText("");
			}

		});
	}

	private void recyclerViewFunctionality() {
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(llm);
		recyclerView.setAdapter(adapter);
		recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
			int y = oldBottom - bottom;
			if (Math.abs(y) > 0) {
				recyclerView.post(() ->
						recyclerView.scrollToPosition(adapter.getItemCount() - 1)
				);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.message_fragment, container, false);

		final GestureDetector gesture = LeftToRightDetector.getInstance(getActivity());

		v.setOnTouchListener((v1, event) -> gesture.onTouchEvent(event));

		toolbar = v.findViewById(R.id.message_toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (chatRoom != null && chatRoom.conversationalist != null) {
			setUserOnlineStatusInActionBar();
		}
		String conversationID = chatRoom == null ? getArguments().getString(CONVERSATION_ID) : chatRoom.getConversationID();
		startWatchingChatRoom(conversationID);

		Log.i("State", "OnResume " + Integer.toString(adapter.getItemCount()));
	}

	@Override
	public void onPause() {
		super.onPause();

		if (downloadingChatRooms != null && !downloadingChatRooms.isDisposed())
			downloadingChatRooms.dispose();

		if (messageReceiver != null && !messageReceiver.isDisposed())
			messageReceiver.dispose();
		Log.i("State", "OnPause " + Integer.toString(adapter.getItemCount()));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (chatRoom != null) {
			chatRoom.chatRoomObject = null;
			chatRoom = null;
		}
		if (downloadingChatRooms != null && !downloadingChatRooms.isDisposed())
			downloadingChatRooms.dispose();

		if (messageReceiver != null && !messageReceiver.isDisposed())
			messageReceiver.dispose();
		Log.i("State", "OnDestroy");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
			Uri photoUri = data.getData();
			MessageSender.sendPhoto(photoUri, chatRoom.getConversationID());
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		menu.clear();
		inflater.inflate(R.menu.conversation_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				FragmentsManager.goBack(getActivity());
				return true;
			case R.id.allInfo: {
				if (doesChatRoomExists()) {
					Bundle bundle = chatRoom.conversationalist.get(0).getSettingsBundle();
					bundle.putString(CONVERSATION_ID, chatRoom.getConversationID());
					bundle.putString(CONVERSATIONALIST_PSEUDONYM, chatRoom.chatRoomObject.conversationName);

					FragmentsManager fragmentManager = FragmentsManager.getInstance();
					fragmentManager.startConversationInfoFragment((AppCompatActivity) getActivity(), bundle);
				}
			}
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private boolean doesChatRoomExists() {
		return chatRoom != null && chatRoom.getConversationID() != null && chatRoom.chatRoomObject.conversationName != null;
	}


	private void setUserAvatarInActionBar() {

	}

	void setUserOnlineStatusInActionBar() {
		String onlineStatusMessage = "";

		if (chatRoom.chatRoomObject.participants.size() < 3 && !chatRoom.conversationalist.isEmpty() && !chatRoom.conversationalist.get(0).isOnline) {
			onlineStatusMessage = LastSeenTime.getLastSeenOnlineStatusMessage(getLastOnlineTimestamp(chatRoom.conversationalist.get(0)), getResources());
		} else if (chatRoom.chatRoomObject.participants.size() < 3 && !chatRoom.conversationalist.isEmpty()) {
			onlineStatusMessage = getResources().getString(R.string.now_online);
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(onlineStatusMessage);
	}

	long getLastOnlineTimestamp(User user) {
		return (long) user.timestamp.get("timestamp");
	}

}
