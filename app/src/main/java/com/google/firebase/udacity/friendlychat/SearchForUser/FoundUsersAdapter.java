package com.google.firebase.udacity.friendlychat.SearchForUser;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.udacity.friendlychat.Managers.FindFriend;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.ArrayList;
import java.util.List;

public class FoundUsersAdapter extends RecyclerView.Adapter<FoundUsersAdapter.FoundUserViewHolder> {

	public static final int OPEN_CONVERSATION = 4;
	List<User> users;
	Context context;

	public FoundUsersAdapter(Context context) {
		users = new ArrayList<>();
		this.context = context;
	}

	@Override
	public FoundUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

		return new FoundUserViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final FoundUserViewHolder holder, final int position) {

		setText(holder, position);

		String contactAvatar = "http://digitalspyuk.cdnds.net/17/25/980x490/landscape-1498216547-avatar-neytiri.jpg";
		String checkAvatar = "null";

		if (checkAvatar != null && !checkAvatar.equals("null")) contactAvatar = checkAvatar;

		Glide.with(context).load(contactAvatar).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.userAvatarImageView);

		onLayoutClick(holder, position);
	}

	private void setText(final FoundUserViewHolder holder, final int position) {

		holder.userNameTextView.setText(users.get(position).User_Name);
	}

	private void onLayoutClick(final FoundUserViewHolder holder, final int position) {

		holder.userItemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//TODO: Ma sprawdzić czy konwersacja istnieje, jak nie to stworzyć, a potem ją otworzyć

				FindFriend findFriend = new FindFriend(users.get(position), conversationID -> FragmentsManager.startMessageFragment((AppCompatActivity) context, conversationID));

				findFriend.checkIfHasFriend();
			}
		});
	}

	@Override
	public int getItemCount() {
		return users.size();
	}

	public void pushUser(User user) {
		users.add(user);
		notifyDataSetChanged();
	}

	public void clear() {
		users.clear();
		notifyDataSetChanged();
	}

	class FoundUserViewHolder extends RecyclerView.ViewHolder {

		TextView userNameTextView;
		ImageView userAvatarImageView;
		ConstraintLayout userItemLayout;

		FoundUserViewHolder(View view) {
			super(view);
			userNameTextView = view.findViewById(R.id.username);
			userAvatarImageView = view.findViewById(R.id.avatar);
			userItemLayout = view.findViewById(R.id.user_item_layout);
		}
	}
}
