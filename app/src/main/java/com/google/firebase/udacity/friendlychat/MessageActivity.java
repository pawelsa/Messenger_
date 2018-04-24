package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import static com.google.firebase.udacity.friendlychat.MainActivity.authStateListener;
import static com.google.firebase.udacity.friendlychat.MainActivity.firebaseAuth;
import static com.google.firebase.udacity.friendlychat.MainActivity.mUsername;
import static com.google.firebase.udacity.friendlychat.Managers.UserManager.changeUserOnlineStatus;

public class MessageActivity extends AppCompatActivity implements ChatRoomListener.OnConversationListener, UserManager.OnUserDownloadListener {

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;

    private ChatRoom conversation;
    private ChatRoomListener listener;
    private UserManager userManager;

    // private MessageAdapter mMessageAdapter;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private ImageView mSendButton;

    public MessageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Get the default actionbar instance
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();

        mActionBar.setSubtitle("OnlineStatus");

        Intent intent = getIntent();

        if (intent != null) {
            String conversationID = intent.getStringExtra("conversationID");
            String conversationalistName = intent.getStringExtra("displayName");
            if (conversationID != null) {
                listener = new ChatRoomListener(conversationID, this);
                Toast.makeText(this, conversationID, Toast.LENGTH_LONG).show();
            }
            if (conversationalistName != null) {
                setTitle(conversationalistName);
            }
        }



/*        initializeReferencesToViews();

        addMessageChildListener();

        listViewAndAdapterSetup();
*/
        //settingUpUIFunctionality();
    }


    /*
        private void initializeReferencesToViews() {

            mMessageListView = findViewById(R.id.messageListView);
            mPhotoPickerButton = findViewById(R.id.photoPickerButton);
            mMessageEditText = findViewById(R.id.messageEditText);
            mSendButton = findViewById(R.id.sendButton);
        }

        private void listViewAndAdapterSetup() {


        }

    */
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
    protected void onPause() {
        super.onPause();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        //removeMessageChildListener();
        //mMessageAdapter.clear();
        changeUserOnlineStatus(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void addConversationToAdapter(ChatRoomObject conversation) {
        if (conversation != null) {
            this.conversation = new ChatRoom(conversation);
            userManager = new UserManager(this);
            userManager.findUser(conversation.conversationalistID);
        }
    }

    @Override
    public void userDownloaded() {

    }

    @Override
    public void userDownloaded(User downloadedUser) {
        this.conversation.conversationalist = downloadedUser;
    }
}
