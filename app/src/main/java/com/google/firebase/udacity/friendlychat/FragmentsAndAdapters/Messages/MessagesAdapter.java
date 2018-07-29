package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.Messages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.udacity.friendlychat.Managers.App.ColorManager;
import com.google.firebase.udacity.friendlychat.Managers.Database.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.Message;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessagesAdapter extends RecyclerView.Adapter {

	private static final int VIEW_TYPE_MESSAGE_SENT = 1;
	private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

	private List<Message> messageList;
	private ChatRoom chatRoom;
	private Context context;


	MessagesAdapter(Context context) {
		this.context = context;
		setHasStableIds(true);
		messageList = new ArrayList<>();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;

		viewType = getViewType(viewType);

		if (viewType == VIEW_TYPE_MESSAGE_SENT) {
			view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.my_message, parent, false);
			return new MyMessageHolder(view);
		} else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
			view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.conversationalist_message, parent, false);
			return new ReceivedMessageHolder(view);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	private int getViewType(int position) {
		Message message = messageList.get(position);

		if (message.getUserID().equals(UserManager.getCurrentUserID())) {
			return VIEW_TYPE_MESSAGE_SENT;
		} else {
			return VIEW_TYPE_MESSAGE_RECEIVED;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		Message message = messageList.get(position);

		int viewType = getViewType(position);

		switch (viewType) {
			case VIEW_TYPE_MESSAGE_SENT:
				((MyMessageHolder) holder).bind(message);
				break;
			case VIEW_TYPE_MESSAGE_RECEIVED:
				((ReceivedMessageHolder) holder).bind(message);
		}
	}

	@Override
	public int getItemCount() {
		return messageList.size();
	}

	public void addMessage(Message message) {
		messageList.add(message);
		notifyItemInserted(messageList.size() - 1);
	}

	public void updateChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
		notifyDataSetChanged();
	}


	private class MyMessageHolder extends RecyclerView.ViewHolder {
		TextView messageText, timeText;
		ImageView messageImage;

		MyMessageHolder(View itemView) {
			super(itemView);

			messageText = itemView.findViewById(R.id.my_message_text);
			timeText = itemView.findViewById(R.id.my_message_time);
			messageImage = itemView.findViewById(R.id.my_message_image);
		}

		void bind(Message message) {

			if (message.getText() != null) {
				messageText.setText(message.getText());
				Drawable background = context.getResources().getDrawable(R.drawable.message_rectangle).getConstantState().newDrawable();

				if (background instanceof GradientDrawable) {
					GradientDrawable gradientDrawable = (GradientDrawable) background;
					gradientDrawable.setColor(Color.parseColor(ColorManager.getHexColor(chatRoom.chatRoomObject.chatColor)));
					messageText.setBackground(gradientDrawable);
				}
				//messageText.setBackground(drawable);
			} else if (message.getPhotoUrl() != null) {
				messageText.setVisibility(View.GONE);
				messageImage.setVisibility(View.VISIBLE);
				Glide.with(context).load(message.getPhotoUrl()).apply(new RequestOptions().transforms(new CenterInside(), new RoundedCorners(40))).into(messageImage);
			}

			// Format the stored timestamp into a readable String using method.
			//timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
		}
	}

	private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
		TextView messageText, timeText, userName;
		ImageView profileImage, messageImage;

		ReceivedMessageHolder(View itemView) {
			super(itemView);

			messageText = itemView.findViewById(R.id.text_message_body);
			timeText = itemView.findViewById(R.id.text_message_time);
			userName = itemView.findViewById(R.id.text_message_name);
			profileImage = itemView.findViewById(R.id.image_message_profile);
			messageImage = itemView.findViewById(R.id.image_message_body);
		}

		void bind(Message message) {

			if (message.getText() != null) {
				messageText.setText(message.getText());

				Drawable background = context.getResources().getDrawable(R.drawable.message_rectangle).getConstantState().newDrawable();

				if (background instanceof GradientDrawable) {
					GradientDrawable gradientDrawable = (GradientDrawable) background;
					gradientDrawable.setColor(Color.parseColor("#375D81"));
					messageText.setBackground(gradientDrawable);
				}
			} else if (message.getPhotoUrl() != null) {
				messageText.setVisibility(View.GONE);
				messageImage.setVisibility(View.VISIBLE);
				Glide.with(context).load(message.getPhotoUrl()).apply(new RequestOptions().transforms(new CenterInside(), new RoundedCorners(40))).into(messageImage);
				/*messageImage.setOnClickListener(view -> {
					Log.i("MESSAGE_RECEIVED_P", message.getText() != null ? message.getText() : message.getPhotoUrl());
					Log.i("Info", "ID: " + Integer.toString(id) + " " + messageList.get(id).getText() != null ? messageList.get(id).getText() : messageList.get(id).getPhotoUrl());
				});*/
			}

			// Format the stored timestamp into a readable String using method.
			//timeText.setText(Utils.formatDateTime(message.getCreatedAt()));

			Map<String, String> userData = (Map<String, String>) chatRoom.chatRoomObject.participants.get(message.getUserID());
			String userN = userData.get("Name");
			userName.setText(userN);

			User user = new User();

			for (User mUser : chatRoom.conversationalist) {
				if (mUser.User_ID.equals(message.getUserID())) {
					user = mUser;
					break;
				}
			}

			if (user.avatarUri != null && !user.avatarUri.equals("null"))
				Glide.with(context).load(user.avatarUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(profileImage);
		}
	}
}
