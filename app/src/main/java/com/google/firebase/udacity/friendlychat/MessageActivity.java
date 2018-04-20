package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.google.firebase.udacity.friendlychat.MainActivity.authStateListener;
import static com.google.firebase.udacity.friendlychat.MainActivity.firebaseAuth;
import static com.google.firebase.udacity.friendlychat.Managers.UserManager.changeUserOnlineStatus;

public class MessageActivity extends AppCompatActivity implements ChatRoomListener.OnConversationListener, UserManager.OnUserDownloadListener {


    public ChatRoom conversation;
    private ChatRoomListener listener;

    // private MessageAdapter mMessageAdapter;


    public MessageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Get the default actionbar instance
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setSubtitle("OnlineStatus");
        }

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
    public void addConversationToAdapter(ChatRoomObject conversation) {
        if (conversation != null) {
            this.conversation = new ChatRoom(conversation);
            UserManager userManager = new UserManager(this);
            userManager.findUser(conversation.conversationalistID);
        }
    }

    @Override
    public void userDownloaded() {

    }

    @Override
    public void userDownloaded(User downloadedUser) {
        this.conversation.conversationalist = downloadedUser;
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            setUserOnlineStatusInActionBar(mActionBar, downloadedUser);
        }
    }

    void setUserOnlineStatusInActionBar(android.support.v7.app.ActionBar mActionBar, User user) {

        String onlineStatusMessage = "Now online";

        if (!user.isOnline) {
            onlineStatusMessage = getLastSeenOnlineStatusMessage(getLastOnlineTimestamp(user));
        }
        mActionBar.setSubtitle(onlineStatusMessage);
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
