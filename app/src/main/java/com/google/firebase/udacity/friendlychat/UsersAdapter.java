package com.google.firebase.udacity.friendlychat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomObject;
import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATION_ID;
import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.DISPLAY_NAME;
import static com.google.firebase.udacity.friendlychat.Managers.UserManager.getCurrentUserID;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements UserManager.OnUserDownloadListener, ChatRoomListener.OnConversationListener {
	
	private UserManager userManager;
	private Context context;
	private List<ChatRoom> chatRoomList;
	private List<ChatRoom> finalListChatRoom;
	private ConversationListener conversationListener;
	
	public UsersAdapter(Context context) {
		this.context = context;
		this.chatRoomList = new ArrayList<>();
		this.finalListChatRoom = new ArrayList<>();
		this.userManager = new UserManager(this);
		this.conversationListener = new ConversationListener(getCurrentUserID(), this);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
		
		return new ViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		
		setTexts(holder, position);
		
		String contactAvatar = "http://digitalspyuk.cdnds.net/17/25/980x490/landscape-1498216547-avatar-neytiri.jpg";
		String checkAvatar = "null";
		if (finalListChatRoom.get(position).chatRoomObject.participants.size() < 3) {
			checkAvatar = finalListChatRoom.get(position).conversationalist.get(0).avatarUri;
		}
		if (checkAvatar != null && !checkAvatar.equals("null")) contactAvatar = checkAvatar;
		
		Glide.with(context).load(contactAvatar).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.userAvatarImageView);
		
		onLayoutClick(holder, position);
	}
	
	private void setTexts(final ViewHolder holder, final int position) {
		
		finalListChatRoom.get(position).chatRoomObject.conversationName = getConversationName(position);
		holder.userNameTextView.setText(finalListChatRoom.get(position).chatRoomObject.conversationName);
		
		String time = null;
		
		if (finalListChatRoom.get(position).chatRoomObject.lastMessageSendTime != null) {
			time = (String) finalListChatRoom.get(position).chatRoomObject.lastMessageSendTime.get("timestamp");
		}
		holder.lastMessageSendTime.setText(time);
	}
	
	private String getConversationName(int position) {
		
		String name = "";
		if (finalListChatRoom.get(position).chatRoomObject.conversationName == null) {
			/*if (finalListChatRoom.get(position).chatRoomObject.participants.size() < 3) {
				name = finalListChatRoom.get(position).conversationalist.get(0).User_Name;
			}
			else {*/
			for (String mapKey : finalListChatRoom.get(position).chatRoomObject.participants.keySet()) {
				Map<String, Object> user = (Map<String, Object>) finalListChatRoom.get(position).chatRoomObject.participants.get(mapKey);
				
				if (!user.get("ID").equals(getCurrentUserID()) && name.length() < 30 && user.get("Name") != null) {
					name += user.get("Name").toString() + ", ";
				}
			}
			name = name.substring(0, name.length() - 2);
			//}
		}
		else {
			name = finalListChatRoom.get(position).chatRoomObject.conversationName;
		}
		return name;
	}
	
	private void onLayoutClick(final ViewHolder holder, final int position) {
		
		holder.userItemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

                /*if (finalListChatRoom.get(position).isEmpty()) {
					OpenChatRoomWith.openChatRoomWith(finalListChatRoom.get(position).conversationalist.User_ID);
                }*/
				if (finalListChatRoom.get(position).chatRoomObject.conversationName != null) {
					MessagesFragment messagesFragment = MessagesFragment.getInstance();
					Bundle bundle = new Bundle();
					bundle.putString(CONVERSATION_ID, finalListChatRoom.get(position).chatRoomObject.conversationID);
					bundle.putString(DISPLAY_NAME, finalListChatRoom.get(position).chatRoomObject.conversationName);
					messagesFragment.setArguments(bundle);
					
					FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
					fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right).replace(R.id.messageFragment, messagesFragment, "messageFragment").addToBackStack("main_fragment_replace").commit();
				}
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return finalListChatRoom.size();
	}
	
	private void add(ChatRoom chatRoom) {
		
		if (chatRoom.conversationalist.isEmpty() && chatRoom.chatRoomObject.participants != null && chatRoom.chatRoomObject.participants.size() < 3) {
			
			chatRoomList.add(chatRoom);
			startLookingForConversationalist(getConversationalistID(chatRoom));
		}
		else {
			finalListChatRoom.add(chatRoom);
			notifyDataSetChanged();
		}
	}
	
	private String getConversationalistID(ChatRoom chatRoom) {
		
		if (chatRoom != null) {
			String IDToReturn;
			
			for (String key : chatRoom.chatRoomObject.participants.keySet()) {
				Map<String, Object> user = (Map<String, Object>) chatRoom.chatRoomObject.participants.get(key);
				if (!user.get("ID").equals(getCurrentUserID())) {
					IDToReturn = user.get("ID").toString();
					return IDToReturn;
				}
			}
		}
		return null;
	}
	
	private void startLookingForConversationalist(String conversationalistID) {
		if (conversationalistID != null) userManager.findUser(conversationalistID);
	}
	
	public void updateList(ChatRoom chatRoom) {
		
		for (int i = 0; i < chatRoomList.size(); i++) {
			if (chatRoomEquals(i, chatRoom)) {
				chatRoomList.set(i, chatRoom);
				notifyDataSetChanged();
				return;
			}
		}
		add(chatRoom);
	}
	
	private boolean chatRoomEquals(int i, ChatRoom chatRoom) {
		return chatRoomList.get(i).chatRoomObject != null && chatRoomList.get(i).chatRoomObject.conversationID != null && chatRoomList.get(i).chatRoomObject.conversationID.equals(chatRoom.chatRoomObject.conversationID);
	}
	
	//TODO: Maybe there is better way to check it
	public void pushUser(User pushedUser) {
		
		for (ChatRoom chatRoom : chatRoomList) {
			for (String key : chatRoom.chatRoomObject.participants.keySet()) {
				Map<String, Object> user = (Map<String, Object>) chatRoom.chatRoomObject.participants.get(key);
				if (user.get("ID").equals(pushedUser.User_ID)) {
					chatRoom.conversationalist.add(pushedUser);
					finalListChatRoom.add(chatRoom);
					chatRoomList.remove(chatRoom);
					notifyDataSetChanged();
					return;
				}
			}
		}
		for (ChatRoom chatRoom : finalListChatRoom) {
			if (!chatRoom.conversationalist.isEmpty()) {
				for (User user : chatRoom.conversationalist)
					if (user.User_ID.equals(pushedUser.User_ID)) {
						chatRoom.conversationalist.add(pushedUser);
						notifyDataSetChanged();
						return;
					}
			}
		}
	}
	
	public void clear() {
		if (conversationListener != null) {
			conversationListener.destroy();
			conversationListener = null;
		}
		finalListChatRoom.clear();
		chatRoomList.clear();
		notifyDataSetChanged();
	}
	
	@Override
	public void addConversationToAdapter(ChatRoomObject conversation) {
		if (conversation != null) updateList(new ChatRoom(conversation));
	}
	
	@Override
	public void userDownloaded() {
	}
	
	@Override
	public void userDownloaded(User downloadedUser) {
		if (downloadedUser != null) pushUser(downloadedUser);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		
		TextView userNameTextView;
		ImageView userAvatarImageView;
		TextView lastMessage;
		TextView lastMessageSendTime;
		RelativeLayout userItemLayout;
		
		ViewHolder(View view) {
			super(view);
			userNameTextView = view.findViewById(R.id.username);
			userAvatarImageView = view.findViewById(R.id.avatar);
			lastMessage = view.findViewById(R.id.lastMessage);
			lastMessageSendTime = view.findViewById(R.id.time);
			userItemLayout = view.findViewById(R.id.user_item_layout);
		}
	}
}
