package com.google.firebase.udacity.friendlychat.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.udacity.friendlychat.Managers.ActionBarManager;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.LastSeenTime;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.SearchForUser.ManageDownloadingChatRooms;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.getCurrentUserID;


public class MessagesFragment extends Fragment /*implements ChatRoomListener.OnConversationListener, com.google.firebase.udacity.friendlychat.Managers.UserManager.OnUserDownloadListener*/ {

	private static final int RC_PHOTO_PICKER = 2;
	public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
	public static final String CONVERSATION_ID = "conversationID";
	public static final String DISPLAY_NAME = "displayName";
	public static final String CONVERSATIONALIST_PSEUDONYM = "conversationalist_pseudonym";

	public static final MessagesFragment ourInstance = new MessagesFragment();

	private ImageButton mPhotoPickerButton;
	private EditText mMessageEditText;
	private ImageView mSendButton;
	private Toolbar toolbar;
	private ActionBar actionBar;

	private ChatRoom chatRoom;

	public static MessagesFragment getInstance() {
		return ourInstance;
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		View item = getActivity().findViewById(R.id.allInfo);
		if (item != null) item.setVisibility(View.INVISIBLE);
		getConversationIdAndSetupActionBar();

		initializeReferencesToViews();
		settingUpUIFunctionality();
	}

	@SuppressLint("CheckResult")
	private void getConversationIdAndSetupActionBar() {

		Bundle bundle = getArguments();
		if (bundle != null) {
			String conversationID = bundle.getString(CONVERSATION_ID);
			String conversationName = bundle.getString(DISPLAY_NAME);
			setupActionBar(conversationName);

			ManageDownloadingChatRooms.downloadChatRoom(conversationID)
					.subscribe(downloadedChatRoom -> {
						chatRoom = downloadedChatRoom;
						changeBarColors();
						if (toolbar != null) {
							setTitle();
							setUserAvatarInActionBar();
							setUserOnlineStatusInActionBar();
						}
					});


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

		mPhotoPickerButton = getActivity().findViewById(R.id.photoPickerButton);
		mMessageEditText = getActivity().findViewById(R.id.messageEditText);
		mSendButton = getActivity().findViewById(R.id.sendButton);
	}

	private void settingUpUIFunctionality() {

		photoPickerButtonFunctionality();

		messageEditTextFunctionality();

		sendButtonFunctionality();
	}


	private void photoPickerButtonFunctionality() {

		mPhotoPickerButton.setOnClickListener(view -> {

			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/jpeg");
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
		});
	}

	private void messageEditTextFunctionality() {

		mMessageEditText.setScroller(new Scroller(getContext()));
		mMessageEditText.setMaxLines(2);
		mMessageEditText.setVerticalScrollBarEnabled(true);

		mMessageEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.toString().trim().length() > 0) {
					mSendButton.setEnabled(true);
				} else {
					mSendButton.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
	}

	private void sendButtonFunctionality() {

		mSendButton.setOnClickListener(view -> {
/*
			FriendlyMessage newMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null);

if (newMessage.getText().trim().length() > 0) {

// mDatabaseReference.push().setValue(newMessage);
// Clear input box
mMessageEditText.setText("");
}*/

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
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.i("State", "OnPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (chatRoom != null) {
			chatRoom.chatRoomObject = null;
			chatRoom = null;
		}
		Log.i("State", "OnDestroy");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

/*        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
			Uri photoUri = data.getData();
            final StorageReference photoReference = storagePhotosReference.child(photoUri.getLastPathSegment());

            photoReference.putFile(photoUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FriendlyMessage friendlyMessageWithPhoto = new FriendlyMessage(null, mUsername, taskSnapshot.getDownloadUrl().toString());
                    mDatabaseReference.push().setValue(friendlyMessageWithPhoto);
                }
            });
        }*/
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
					bundle.putString(CONVERSATION_ID, chatRoom.chatRoomObject.conversationID);
					bundle.putString(CONVERSATIONALIST_PSEUDONYM, chatRoom.chatRoomObject.conversationName);

					FragmentsManager.startConversationInfoFragment((AppCompatActivity) getActivity(), bundle);
				}
			}
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private boolean doesChatRoomExists() {
		return chatRoom != null && chatRoom.chatRoomObject.conversationID != null && chatRoom.chatRoomObject.conversationName != null;
	}

	private void changeBarColors() {
		int color = chatRoom.chatRoomObject.chatColor;
		if (toolbar != null && actionBar != null) {

			ColorDrawable actionBarColor = ActionBarManager.getActionBarColor(color);
			actionBar.setBackgroundDrawable(actionBarColor);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				int statusBarColor = ActionBarManager.getStatusBarColor(color);
				if (statusBarColor != -1)
					getActivity().getWindow().setStatusBarColor(statusBarColor);
			}
		}
	}


	private void setUserAvatarInActionBar() {

/*        Glide.with(getContext()).load(chatRoom.conversationalist.avatarUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(new SimpleTarget<Drawable>() {
			@Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                Bitmap bitmap1 = ((BitmapDrawable) resource).getBitmap();

                Drawable drawable = new BitmapDrawable(getResources(), bitmap1);
                toolbar.setIcon(drawable);
            }
        });*/
		//toolbar.setIcon(R.drawable.avatar);
	}

	void setUserOnlineStatusInActionBar() {
		String onlineStatusMessage = "";

		if (chatRoom.chatRoomObject.participants.size() < 3 && !chatRoom.conversationalist.isEmpty() && !chatRoom.conversationalist.get(0).isOnline) {
			onlineStatusMessage = getResources().getString(R.string.now_online);
			onlineStatusMessage = LastSeenTime.getLastSeenOnlineStatusMessage(getLastOnlineTimestamp(chatRoom.conversationalist.get(0)), getResources());
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(onlineStatusMessage);
	}

	long getLastOnlineTimestamp(User user) {
		return (long) user.timestamp.get("timestamp");
	}

}
