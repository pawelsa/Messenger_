package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
			if (downloadedUser.isOnline) {
				mActionBar.setSubtitle("Now online");
			}
			else {
				HashMap<String, Object> time = downloadedUser.timestamp;
				
				SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				//sfd.format(new Date((long)time.get("timestamp")))
				
				Date lastOnline = new Date((long) time.get("timestamp"));
				Date now = new Date();
				//Log.i("Timestamp", );
			}
			mActionBar.setSubtitle(Boolean.toString(downloadedUser.isOnline));
		}
	}
}
