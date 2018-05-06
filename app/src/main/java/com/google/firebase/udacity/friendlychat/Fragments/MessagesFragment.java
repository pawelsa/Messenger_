package com.google.firebase.udacity.friendlychat.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Scroller;

import com.google.firebase.udacity.friendlychat.ChatRoomListener;
import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MessagesFragment extends Fragment implements ChatRoomListener.OnConversationListener, com.google.firebase.udacity.friendlychat.Managers.UserManager.OnUserDownloadListener {

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;
    public static final String CONVERSATION_ID = "conversationID";
    public static final String DISPLAY_NAME = "displayName";
    public static final String CONVERSATIONALIST_ID = "conversationalist_id";
    public static final String CONVERSATIONALIST_DISPLAY_NAME = "conversationalist_display_name";
    public static final String CONVERSATIONALIST_AVATAR_URL = "conversationalist_avatar_url";
    public static final String MY_PSEUDONYM = "my_pseudonym";
    public static final String CONVERSATIONALIST_PSEUDONYM = "conversationalist_pseudonym";
    public static final MessagesFragment ourInstance = new MessagesFragment();
    UserManager userManager;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private ImageView mSendButton;
    private ActionBar actionBar;
    private ChatRoomListener chatRoomListener;
    private ChatRoom chatRoom;
    String conversationID;
    String myPseudonym;
    String conversationalistPseudonym;
    private boolean onPause = false;

    public static MessagesFragment getInstance() {
        return ourInstance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userManager = new UserManager(this);
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

    private void getConversationIdAndSetupActionBar() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            conversationID = bundle.getString(CONVERSATION_ID);
            String userName = bundle.getString(DISPLAY_NAME);

            setupActionBar(userName);

            chatRoomListener = new ChatRoomListener(conversationID, this);
        }
    }

    private void setupActionBar(String userName) {

        if (actionBar != null && !onPause) {
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

        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
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

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                FriendlyMessage newMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null);

                if (newMessage.getText().trim().length() > 0) {

                    // mDatabaseReference.push().setValue(newMessage);
                    // Clear input box
                    mMessageEditText.setText("");
                }*/

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.message_fragment, container, false);

        final GestureDetector gesture = LeftToRightDetector.getInstance(getActivity());

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        onPause = false;
        if (conversationalistPseudonym != null)
            setupActionBar(conversationalistPseudonym);
        if (chatRoom != null && chatRoom.conversationalist != null)
            setUserOnlineStatusInActionBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        onPause = true;
        Log.i("State", "OnPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatRoomListener != null) {
            chatRoomListener.destroy();
            chatRoomListener = null;
        }
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
                LeftToRightDetector.goBack(getActivity());
                return true;
            case R.id.allInfo: {
                ConversationInfoFragment conversationInfoFragment = new ConversationInfoFragment();

                Bundle bundle = chatRoom.conversationalist.getSettingsBundle();
                bundle.putString(CONVERSATION_ID, conversationID);
                bundle.putString(MY_PSEUDONYM, myPseudonym);
                bundle.putString(CONVERSATIONALIST_PSEUDONYM, conversationalistPseudonym);
                conversationInfoFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction
                        .setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
                        .replace(R.id.messageFragment, conversationInfoFragment, "infoFragment")
                        .addToBackStack(null).commit();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void addConversationToAdapter(ChatRoomObject conversation) {

        chatRoom = new ChatRoom(conversation);
        String conversationalistID = (conversation.conversationalistID.equals(UserManager.getCurrentUserID()) ? conversation.myID : conversation.conversationalistID);
        userManager.findUser(conversationalistID);

        changeBarColors(conversation.chatColor);
    }

    private void changeBarColors(int color) {
        if (actionBar != null) {
            changeActionBarColor(color);
            changeStatusBarColor(color);
        }
    }

    private void changeActionBarColor(int color) {
        String hex = Integer.toHexString(color);
        while (hex.length() < 6) {
            hex = "0" + hex;
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + hex)));
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
    public void userDownloaded() {

    }

    @Override
    public void userDownloaded(User downloadedUser) {
        chatRoom.conversationalist = downloadedUser;

        if (downloadedUser.User_ID.equals(chatRoom.chatRoomObject.conversationalistID)) {
            Log.i("Tukej", "Ja");
            if (chatRoom.chatRoomObject.myPseudonym != null)
                myPseudonym = chatRoom.chatRoomObject.myPseudonym;
            else
                myPseudonym = UserManager.currentUser.User_Name;
            if (chatRoom.chatRoomObject.conversationalistPseudonym != null)
                conversationalistPseudonym = chatRoom.chatRoomObject.conversationalistPseudonym;
            else
                conversationalistPseudonym = chatRoom.conversationalist.User_Name;
        } else {
            if (chatRoom.chatRoomObject.conversationalistPseudonym != null)
                myPseudonym = chatRoom.chatRoomObject.conversationalistPseudonym;
            else
                myPseudonym = chatRoom.conversationalist.User_Name;
            if (chatRoom.chatRoomObject.myPseudonym != null)
                conversationalistPseudonym = chatRoom.chatRoomObject.myPseudonym;
            else
                conversationalistPseudonym = UserManager.currentUser.User_Name;
        }
        if (conversationalistPseudonym != null)
            setupActionBar(conversationalistPseudonym);
        if (actionBar != null) {
            setUserAvatarInActionBar();
            setUserOnlineStatusInActionBar();
        }
    }

    void setUserOnlineStatusInActionBar() {
        if (!onPause) {
            String onlineStatusMessage = getResources().getString(R.string.now_online);

            if (!chatRoom.conversationalist.isOnline) {
                onlineStatusMessage = getLastSeenOnlineStatusMessage(getLastOnlineTimestamp(chatRoom.conversationalist));
            }
            actionBar.setSubtitle(onlineStatusMessage);
        }
    }

    private void setUserAvatarInActionBar() {

/*        Glide.with(getContext()).load(chatRoom.conversationalist.avatarUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                Bitmap bitmap1 = ((BitmapDrawable) resource).getBitmap();

                Drawable drawable = new BitmapDrawable(getResources(), bitmap1);
                actionBar.setIcon(drawable);
            }
        });*/
//actionBar.setIcon(R.drawable.avatar);
    }

    long getLastOnlineTimestamp(User user) {

        return (long) user.timestamp.get("timestamp");
    }

    String getLastSeenOnlineStatusMessage(long lastSeen) {

        long diff = returnTimeSinceLastOnlineTime(lastSeen);

        String onlineStatusMessage = getResources().getString(R.string.last_seen);

        long lastTimeOnline = TimeUnit.MILLISECONDS.toDays(diff);
        if (lastTimeOnline <= 0) {
            lastTimeOnline = TimeUnit.MILLISECONDS.toHours(diff);
            if (lastTimeOnline <= 0) {
                lastTimeOnline = TimeUnit.MILLISECONDS.toMinutes(diff);
                if (lastTimeOnline <= 0) {
                    onlineStatusMessage = getResources().getString(R.string.moment_ago);
                    return onlineStatusMessage;
                } else
                    onlineStatusMessage += lastTimeOnline + getResources().getString(R.string.minute);
            } else
                onlineStatusMessage += lastTimeOnline + getResources().getString(R.string.hour);
        } else
            onlineStatusMessage += lastTimeOnline + getResources().getString(R.string.day);

        onlineStatusMessage += (lastTimeOnline == 1 ? "" : getResources().getString(R.string.plural)) + getResources().getString(R.string.ago);

        return onlineStatusMessage;
    }

    long returnTimeSinceLastOnlineTime(long time) {
        Date lastOnline = new Date(time);
        Date actualTime = new Date();
        return actualTime.getTime() - lastOnline.getTime();
    }
}
