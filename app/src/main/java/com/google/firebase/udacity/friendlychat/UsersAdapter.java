package com.google.firebase.udacity.friendlychat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

import static com.google.firebase.udacity.friendlychat.Managers.UserManager.getCurrentUserID;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

	private Context context;
	private List<ChatRoom> chatRoomList;


	public UsersAdapter(Context context) {
		this.context = context;
		this.chatRoomList = new ArrayList<>();
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
		if (chatRoomList.get(position).chatRoomObject.participants.size() < 3) {
			checkAvatar = chatRoomList.get(position).conversationalist.get(0).avatarUri;
		}
		if (checkAvatar != null && !checkAvatar.equals("null")) contactAvatar = checkAvatar;

		Glide.with(context).load(contactAvatar).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.userAvatarImageView);

		onLayoutClick(holder, position);
	}

	private void setTexts(final ViewHolder holder, final int position) {

		setConversationName(position);
		holder.userNameTextView.setText(chatRoomList.get(position).chatRoomObject.conversationName);

		String time = null;

		if (chatRoomList.get(position).chatRoomObject.lastMessageSendTime != null) {
			time = (String) chatRoomList.get(position).chatRoomObject.lastMessageSendTime.get("timestamp");
		}
		holder.lastMessageSendTime.setVisibility(View.VISIBLE);
		holder.lastMessageSendTime.setText(time);
	}

	@SuppressLint("CheckResult")
	private void setConversationName(int position) {

		Single.create((SingleOnSubscribe<String>) emitter -> {

			if (chatRoomList.get(position).chatRoomObject.conversationName == null) {
				Observable.fromIterable(chatRoomList.get(position).chatRoomObject.participants.keySet())
						.map(key -> (Map<String, Object>) chatRoomList.get(position).chatRoomObject.participants.get(key))
						.take(4)
						.filter(user -> !user.get("ID").equals(getCurrentUserID()) && user.get("Name") != null)
						.map(user -> user.get("Name").toString() + ", ")
						.reduce((total, next) -> total + next)
						.map(totalName -> totalName.length() < 50 ? totalName.substring(0, totalName.length() - 2) : totalName.substring(0, 50))
						.subscribe(emitter::onSuccess);
			} else {
				emitter.onSuccess(chatRoomList.get(position).chatRoomObject.conversationName);
			}
		})
				.subscribe(userName -> chatRoomList.get(position).chatRoomObject.conversationName = userName);
	}

	private void onLayoutClick(final ViewHolder holder, final int position) {

		holder.userItemLayout.setOnClickListener(v -> {

			if (chatRoomList.get(position).chatRoomObject.conversationName != null) {
				FragmentsManager.startMessageFragment((AppCompatActivity) context, chatRoomList.get(position).chatRoomObject.conversationID);
			}
		});
	}

	@Override
	public int getItemCount() {
		return chatRoomList.size();
	}

	public void addConversationToAdapter(ChatRoom conversation) {

		int index = chatRoomList.indexOf(conversation);
		if (index != -1) {
			Log.i("updatwConvInAdapter", conversation.chatRoomObject.conversationID);
			chatRoomList.set(index, conversation);
		} else {
			Log.i("addConvToAdapter", conversation.chatRoomObject.conversationID);
			chatRoomList.add(conversation);
		}
		notifyDataSetChanged();
	}

	public void destroy() {
		chatRoomList.clear();
		notifyDataSetChanged();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		TextView userNameTextView;
		ImageView userAvatarImageView;
		TextView lastMessage;
		TextView lastMessageSendTime;
		ConstraintLayout userItemLayout;

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
