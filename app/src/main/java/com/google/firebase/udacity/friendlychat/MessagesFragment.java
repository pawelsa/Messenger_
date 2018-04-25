package com.google.firebase.udacity.friendlychat;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.google.firebase.udacity.friendlychat.MainActivity.mUsername;

/**
 * Created by Paweł on 17.04.2018.
 */

public class MessagesFragment extends Fragment implements ChatRoomListener.OnConversationListener, com.google.firebase.udacity.friendlychat.Managers.UserManager.OnUserDownloadListener {

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;
    UserManager userManager;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private ImageView mSendButton;
    private ActionBar actionBar;
    private ChatRoomListener chatRoomListener;
    private ChatRoom chatRoom;

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
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
            String conversationID = bundle.getString("conversationID");
            String userName = bundle.getString("displayName");

            setupActionBar(userName);

            chatRoomListener = new ChatRoomListener(conversationID, this);
        }
    }

    private void setupActionBar(String userName) {

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
        //mMessageEditText.setMovementMethod(new ScrollingMovementMethod());

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

                FriendlyMessage newMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null);

                if (newMessage.getText().trim().length() > 0) {

                    // mDatabaseReference.push().setValue(newMessage);
                    // Clear input box
                    mMessageEditText.setText("");
                }
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

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
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

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right).replace(R.id.messageFragment, conversationInfoFragment, "infoFragment").addToBackStack(null).commit();
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
    }

    @Override
    public void userDownloaded() {

    }

    @Override
    public void userDownloaded(User downloadedUser) {
        chatRoom.conversationalist = downloadedUser;

        if (actionBar != null)
            setUserOnlineStatusInActionBar(downloadedUser);
    }

    void setUserOnlineStatusInActionBar(User user) {

        String onlineStatusMessage = "Now online";

        if (!user.isOnline) {
            onlineStatusMessage = getLastSeenOnlineStatusMessage(getLastOnlineTimestamp(user));
        }
        actionBar.setSubtitle(onlineStatusMessage);
    }

    long getLastOnlineTimestamp(User user) {

        return (long) user.timestamp.get("timestamp");
    }

    String getLastSeenOnlineStatusMessage(long lastSeen) {

        long diff = returnTimeSinceLastOnlineTime(lastSeen);

        String onlineStatusMessage = "Last seen ";

        long lastTimeOnline = TimeUnit.MILLISECONDS.toDays(diff);
        if (lastTimeOnline <= 0) {
            lastTimeOnline = TimeUnit.MILLISECONDS.toHours(diff);
            if (lastTimeOnline <= 0) {
                lastTimeOnline = TimeUnit.MILLISECONDS.toMinutes(diff);
                if (lastTimeOnline <= 0) {
                    onlineStatusMessage = "A moment ago";
                } else
                    onlineStatusMessage += lastTimeOnline + " minute";
            } else
                onlineStatusMessage += lastTimeOnline + " hour";
        } else
            onlineStatusMessage += lastTimeOnline + " day";

        onlineStatusMessage += (lastTimeOnline == 1 ? "" : "s") + " ago";

        return onlineStatusMessage;
    }

    long returnTimeSinceLastOnlineTime(long time) {
        Date lastOnline = new Date(time);
        Date actualTime = new Date();
        return actualTime.getTime() - lastOnline.getTime();
    }
}
